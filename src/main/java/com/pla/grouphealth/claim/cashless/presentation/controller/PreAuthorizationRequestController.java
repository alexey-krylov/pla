package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouphealth.claim.cashless.application.command.*;
import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.exception.PreAuthorizationInProcessingException;
import com.pla.grouphealth.claim.cashless.domain.model.CommentDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;

import com.pla.grouphealth.claim.cashless.presentation.dto.GHClaimDocumentCommand;
import com.pla.grouphealth.claim.cashless.presentation.dto.GHPreAuthorizationMailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchPreAuthorizationRecordDto;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouplife.policy.presentation.dto.GLPolicyMailDto;
import com.pla.publishedlanguage.contract.IAuthenticationFacade;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import java.lang.*;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.DateTime;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nthdimenzion.presentation.AppUtils.deleteTempFileIfExists;
import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/6/2016.
 */
@RestController
@RequestMapping(value = "/grouphealth/claim/cashless/preauthorizationrequest")
public class PreAuthorizationRequestController {
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private PreAuthorizationRequestService preAuthorizationRequestService;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    @Qualifier("authenticationFacade")
    IAuthenticationFacade authenticationFacade;
    @Autowired
    private MailService mailService;

    @RequestMapping(value = "/getpreauthorizationbypreauthorizationIdandclientId/{preAuthorizationId}/{clientId}", method = RequestMethod.GET)
    public
    @ResponseBody
    PreAuthorizationClaimantDetailCommand getPreAuthorizationByPreAuthorizationIdAndClientId(@PathVariable("preAuthorizationId") String preAuthorizationId, @PathVariable("clientId") String clientId) {
        //return preAuthorizationRequestService.getPreAuthorizationByPreAuthorizationIdAndClientId(new PreAuthorizationId(preAuthorizationId), clientId);
        return null;
    }

    @RequestMapping(value = "/loadpreauthorizationviewforupdate", method = RequestMethod.GET)
    public
    @ResponseBody
    PreAuthorizationClaimantDetailCommand loadpreauthorizationviewforupdate(@RequestParam String preAuthorizationId, HttpServletResponse response) throws IOException {
        if (isEmpty(preAuthorizationId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return null;
        }
        return preAuthorizationRequestService.getPreAuthorizationClaimantDetailCommandFromPreAuthorizationRequestId(new PreAuthorizationRequestId(preAuthorizationId));
    }

    @RequestMapping(value = "/updatepreauthorization", method = RequestMethod.POST)
    public Result createUpdate(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            UpdatePreAuthorizationCommand updatePreAuthorizationCommand = new UpdatePreAuthorizationCommand(preAuthorizationClaimantDetailCommand, null, userName);
            commandGateway.sendAndWait(updatePreAuthorizationCommand);
            return Result.success("PreAuthorization successfully updated");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/submitpreauthorization", method = RequestMethod.POST)
    public Result submitPreAuthorization(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            RoutingLevel routingLevel = null;
            if(preAuthorizationClaimantDetailCommand.isSubmitEventFired()) {
                routingLevel = preAuthorizationRequestService.getRoutingLevelForPreAuthorization(preAuthorizationClaimantDetailCommand);
            }
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand.setPreAuthProcessorUserId(userName);
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            UpdatePreAuthorizationCommand updatePreAuthorizationCommand = new UpdatePreAuthorizationCommand(preAuthorizationClaimantDetailCommand, routingLevel, userName);
            String preAuthorizationRequestId = commandGateway.sendAndWait(updatePreAuthorizationCommand);
            return Result.success("Pre Authorization successfully submitted");
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/loadpreauthorizationrequest", method = RequestMethod.GET)
    public ModelAndView searchPreAuthorizationRecor() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/preAuthorizationRequest");

        return modelAndView;
    }

    @RequestMapping(value = "/getmandatorydocuments/{clientId}/{preAuthorizationId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("clientId") String clientId, @PathVariable("preAuthorizationId") String preAuthorizationId, HttpServletResponse response) throws Exception {
        if (isEmpty(clientId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "clientId cannot be empty");
            return Lists.newArrayList();
        }
        List<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = preAuthorizationRequestService.findMandatoryDocuments(new FamilyId(clientId), preAuthorizationId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/downloadmandatorydocument/{gridfsdocid}", method = RequestMethod.GET)
    public void downloadMandatoryDocument(@PathVariable("gridfsdocid") String gridfsDocId, HttpServletResponse response) throws IOException {
        GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(gridfsDocId)));
        response.reset();
        response.setContentType(gridFSDBFile.getContentType());
        response.setHeader("content-disposition", "attachment; filename=" + gridFSDBFile.getFilename() + "");
        OutputStream outputStream = response.getOutputStream();
        IOUtils.copy(gridFSDBFile.getInputStream(), outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/getpreauthorizationrequestbycriteria", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView getPreAuthorizationRequestByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto) {
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthorizationRequestRecord");
        List<PreAuthorizationClaimantDetailCommand> searchResult = preAuthorizationRequestService.getPreAuthorizationRequestByCriteria(searchPreAuthorizationRecordDto);
        modelAndView.addObject("searchResult", searchResult);
        modelAndView.addObject("searchResult", searchPreAuthorizationRecordDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getpreauthorizationfordefaultlist", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getPreAuthorizationForDefaultList(HttpServletRequest request) {
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthorizationRecord");
        List<PreAuthorizationClaimantDetailCommand> searchResult = preAuthorizationRequestService.getPreAuthorizationForDefaultList(userName);
        modelAndView.addObject("preAuthorizationResult", searchResult);
        modelAndView.addObject("searchCriteria", new SearchPreAuthorizationRecordDto());
        return modelAndView;
    }

    @Synchronized
    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadMandatoryDocument(GHClaimDocumentCommand ghClaimDocumentCommand, HttpServletRequest request) {
        ghClaimDocumentCommand.setUserDetails(preAuthorizationRequestService.getUserDetailFromAuthentication());
        try {
            commandGateway.send(ghClaimDocumentCommand);
            return Result.success("Documents uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @Synchronized
    @RequestMapping(value = "/removeadditionalDocument", method = RequestMethod.POST)
    @ResponseBody
    public Result removePreAuthorizationAdditionalDocument(@Valid @RequestBody PreAuthorizationRemoveAdditionalDocumentCommand preAuthorizationRemoveAdditionalDocumentCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while updating comments", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(preAuthorizationRemoveAdditionalDocumentCommand);
            return Result.success("Document deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getpreauthorizationclaimantdetailcommandfrompreauthorizationrequestid", method = RequestMethod.GET)
    @ResponseBody
    public PreAuthorizationClaimantDetailCommand getPreAuthorizationClaimantDetailCommandFromPreAuthorizationRequestId(@RequestParam String preAuthorizationId, HttpServletResponse response) throws IOException {
        if (isEmpty(preAuthorizationId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return null;
        }
        return preAuthorizationRequestService.getPreAuthorizationClaimantDetailCommandFromPreAuthorizationRequestId(new PreAuthorizationRequestId(preAuthorizationId));
    }

    @RequestMapping(value = "/loadpreauthorizationviewforupdateview", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView loadpreauthorizationviewforupdateview(@RequestParam String preAuthorizationId, @RequestParam String clientId, HttpServletResponse response) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/preAuthorizationRequest");
        return modelAndView;
    }

    @RequestMapping(value = "/getadditionaldocuments/{preAuthorizationId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list additional documents which is being configured in Mandatory Document SetUp")
    public Set<GHProposalMandatoryDocumentDto> findAdditionalDocuments(@PathVariable("preAuthorizationId") String preAuthorizationId, HttpServletResponse response) throws IOException {
        if (isEmpty(preAuthorizationId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return Sets.newHashSet();
        }
        Set<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = preAuthorizationRequestService.findAdditionalDocuments(preAuthorizationId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/underwriter/getlistofpreauthorizationassigned", method = RequestMethod.POST)
    public ModelAndView getDefaultListOfPreAuthorizationAssignedToUnderwriter(HttpServletResponse response, HttpServletRequest request){
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<PreAuthorizationClaimantDetailCommand> preAuthorizationClaimantDetailCommands = preAuthorizationRequestService.getDefaultListOfPreAuthorizationAssignedToUnderwriter(userName);
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthUnderwriter");
        modelAndView.addObject("preAuthorizationResult", preAuthorizationClaimantDetailCommands);
        modelAndView.addObject("searchCriteria", new SearchPreAuthorizationRecordDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchpreauthorizationforunderwriterbycriteria", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPreAuthorizationForUnderWriterByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthorizationRequestRecord");
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        if(isNotEmpty(userName)) {
            List<PreAuthorizationClaimantDetailCommand> searchResult = preAuthorizationRequestService.searchPreAuthorizationForUnderWriterByCriteria(searchPreAuthorizationRecordDto, userName);
            modelAndView.addObject("searchResult", searchResult);
        }
        modelAndView.addObject("searchResult", searchPreAuthorizationRecordDto);
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/update", method = RequestMethod.POST)
    public Result updatedByUnderwriter(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while updating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            String preAuthorizationRequestId = commandGateway.sendAndWait(new UnderwriterPreAuthorizationUpdateCommand(preAuthorizationClaimantDetailCommand, userName));
            return Result.success("Pre Authorization successfully updated.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/approve", method = RequestMethod.POST)
    public Result approvedByUnderwriter(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while approving Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            boolean result = commandGateway.sendAndWait(new ApprovePreAuthorizationCommand(preAuthorizationClaimantDetailCommand, userName));
            return Result.success("Pre Authorization Request successfully approved.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/reject", method = RequestMethod.POST)
    public Result rejectedByUnderwriter(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while rejecting Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            boolean result = commandGateway.sendAndWait(new RejectPreAuthorizationCommand(preAuthorizationClaimantDetailCommand, userName));
            return Result.success("Pre Authorization Request successfully rejected.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/return", method = RequestMethod.POST)
    public Result returnByUnderwriter(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while returning Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            boolean result = commandGateway.sendAndWait(new ReturnPreAuthorizationCommand(preAuthorizationClaimantDetailCommand, userName));
            return Result.success("Pre Authorization Request successfully returned.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/routetoseniorunderwriter", method = RequestMethod.POST)
    public Result routeToSeniorUnderwriter(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while routing Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            boolean result = commandGateway.sendAndWait(new RoutePreAuthorizationCommand(preAuthorizationClaimantDetailCommand, userName));
            return Result.success("Pre Authorization Request successfully routed to senior underwriter.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/addrequirement", method = RequestMethod.POST)
    public Result addRequirement(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while routing Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.reConstructProbableClaimAmountForServices(preAuthorizationClaimantDetailCommand);
            boolean result = commandGateway.sendAndWait(new AddRequirementPreAuthorizationCommand(preAuthorizationClaimantDetailCommand, userName));
            return Result.success("Pre Authorization Request successfully routed to senior underwriter.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @Synchronized
    @RequestMapping(value = "/loadunderwriterviewforupdate", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView loadUnderwriterViewForUpdate(@RequestParam String preAuthorizationId, @RequestParam String clientId, HttpServletResponse response) throws IOException, PreAuthorizationInProcessingException {
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        ModelAndView modelAndView = new ModelAndView();
        preAuthorizationRequestService.populatePreAuthorizationWithPreAuthorizationUnderWriterUserId(preAuthorizationId, userName);
        modelAndView.setViewName("pla/grouphealth/claim/preauthorizationunderwriter");
        return modelAndView;
    }

    @RequestMapping(value = "/loadunderwriterviewforview", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView loadUnderwriterViewForView(@RequestParam String preAuthorizationId, @RequestParam String clientId, HttpServletResponse response) throws IOException, PreAuthorizationInProcessingException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/preauthorizationunderwriter");
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/getdefaultlistofunderwriterlevels/{level}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getDefaultListOfUnderwriterLevels(@PathVariable("level") String level, HttpServletResponse response) {
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<PreAuthorizationClaimantDetailCommand> preAuthorizationClaimantDetailCommands = preAuthorizationRequestService.getDefaultListByUnderwriterLevel(level, userName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("preAuthorizationResult", preAuthorizationClaimantDetailCommands);
        modelAndView.addObject("searchCriteria", new SearchPreAuthorizationRecordDto().updateWithUnderwriterLevel(level));
        modelAndView.setViewName("pla/grouphealth/claim/preAuthUnderwriter");
        return modelAndView;
    }

    @RequestMapping(value = "/getallrelevantservices/{preAuthorizationId}", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> getAllRelevantServices(@PathVariable("preAuthorizationId") String preAuthorizationId, HttpServletResponse response) throws IOException {
        if(isEmpty(preAuthorizationId)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return Collections.EMPTY_SET;
        }
        return preAuthorizationRequestService.getAllRelevantServices(preAuthorizationId);
    }

    @RequestMapping(value = "/getunderwriterlevelforpreauthorization/{preAuthorizationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getUnderwriterLevelForPreAuthorization(@PathVariable("preAuthorizationId") String preAuthorizationId, HttpServletResponse response) throws IOException {
        if(isEmpty(preAuthorizationId)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return Result.failure();
        }
        try {
            Object level = preAuthorizationRequestService.getUnderwriterLevelForPreAuthorization(preAuthorizationId);
            return Result.success("", level);
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/getpreauthorizationrejectionletter/{preAuthorizationId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("preAuthorizationId") String preAuthorizationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/emailpreauthorizationrejectionletter");
        modelAndView.addObject("mailContent", preAuthorizationRequestService.getPreScriptedEmail(preAuthorizationId));
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/getaddrequirementrequestletter/{preAuthorizationId}", method = RequestMethod.GET)
    public ModelAndView getAddRequirementRequestLetter(@PathVariable("preAuthorizationId") String preAuthorizationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/emailpreauthorizationrequirementletter");
        modelAndView.addObject("mailContent", preAuthorizationRequestService.getAddRequirementRequestLetter(preAuthorizationId));
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/emailpreauthorizationrejectionletter", method = RequestMethod.POST)
    @ResponseBody
    public Result emailPreAuthorizationRejectionLetter(@RequestBody GHPreAuthorizationMailDto ghPreAuthorizationMailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            mailService.sendMailWithAttachment(ghPreAuthorizationMailDto.getSubject(), ghPreAuthorizationMailDto.getMailContent(), Lists.newArrayList(), ghPreAuthorizationMailDto.getRecipientMailAddress().split(";"));
            return Result.success("Email sent successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/emailpreauthorizationrequirementletter", method = RequestMethod.POST)
    @ResponseBody
    public Result emailPreAuthorizationRequirementLetter(@RequestBody GHPreAuthorizationMailDto ghPreAuthorizationMailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            mailService.sendMailWithAttachment(ghPreAuthorizationMailDto.getSubject(), ghPreAuthorizationMailDto.getMailContent(), Lists.newArrayList(), ghPreAuthorizationMailDto.getRecipientMailAddress().split(";"));
            return Result.success("Email sent successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/checkifpreauthorizationrejectionemailsent/{preAuthorizationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result checkIfPreAuthorizationRejectionEmailSent(@PathVariable("preAuthorizationId") String preAuthorizationId) {
        if (isEmpty(preAuthorizationId)) {
            return Result.failure("PreAuthorizationId cannot be empty.");
        }
        try {
            boolean result = preAuthorizationRequestService.checkIfPreAuthorizationRejectionEmailSent(preAuthorizationId);
            return Result.success("Email sent successfully", result);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/checkifpreauthorizationrequirementemailsent/{preAuthorizationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result checkIfPreAuthorizationRequirementEmailSent(@PathVariable("preAuthorizationId") String preAuthorizationId) {
        if (isEmpty(preAuthorizationId)) {
            return Result.failure("PreAuthorizationId cannot be empty.");
        }
        try {
            boolean result = preAuthorizationRequestService.checkIfPreAuthorizationRequirementEmailSent(preAuthorizationId);
            return Result.success("Email sent successfully", result);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }
}
