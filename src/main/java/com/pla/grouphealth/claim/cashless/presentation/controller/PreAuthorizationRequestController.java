package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouphealth.claim.cashless.application.command.*;
import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.model.CommentDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.grouphealth.claim.cashless.presentation.dto.GHClaimDocumentCommand;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchPreAuthorizationRecordDto;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.DateTime;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;
import static org.nthdimenzion.utils.UtilValidator.*;
import static org.springframework.util.Assert.*;

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

    @RequestMapping(value = "/getpreauthorizationbypreauthorizationIdandclientId/{preAuthorizationId}/{clientId}", method = RequestMethod.GET)
    public @ResponseBody
    PreAuthorizationClaimantDetailCommand getPreAuthorizationByPreAuthorizationIdAndClientId(@PathVariable("preAuthorizationId") String preAuthorizationId, @PathVariable("clientId") String clientId){
        //return preAuthorizationRequestService.getPreAuthorizationByPreAuthorizationIdAndClientId(new PreAuthorizationId(preAuthorizationId), clientId);
        return null;
    }

    @RequestMapping(value = "/loadpreauthorizationviewforupdate", method = RequestMethod.GET)
    public @ResponseBody
    PreAuthorizationClaimantDetailCommand loadpreauthorizationviewforupdate(@RequestParam String preAuthorizationId, HttpServletResponse response) throws IOException {
        if(isEmpty(preAuthorizationId)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return null;
        }
        return preAuthorizationRequestService.getPreAuthorizationClaimantDetailCommandFromPreAuthorizationRequestId(new PreAuthorizationRequestId(preAuthorizationId));
    }

    @RequestMapping(value = "/updatepreauthorization", method = RequestMethod.POST)
    public Result createUpdate(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String preAuthorizationRequestId = commandGateway.sendAndWait(preAuthorizationClaimantDetailCommand);
            return Result.success("Pre Authorization Request successfully created with PreAuthorizationRequestId - "+ preAuthorizationRequestId);
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/submitpreauthorization", method = RequestMethod.POST)
    public Result submitPreAuthorization(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            notNull(userDetails, "No user details found please login");
            preAuthorizationClaimantDetailCommand.setPreAuthProcessorUserId(userDetails.getUsername());
            String preAuthorizationRequestId = commandGateway.sendAndWait(preAuthorizationClaimantDetailCommand);
            return Result.success("Pre Authorization Request successfully submitted");
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value="/loadpreauthorizationrequest",method = RequestMethod.GET)
    public ModelAndView searchPreAuthorizationRecor() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/preAuthorizationRequest");

        return modelAndView;
    }

    @RequestMapping(value = "/getmandatorydocuments/{clientId}/{preAuthorizationId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("clientId") String clientId, @PathVariable("preAuthorizationId") String preAuthorizationId, HttpServletResponse response) throws Exception {
        if(isEmpty(clientId)){
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
        UserDetails userDetails = getLoggedInUserDetail(request);
        notNull(userDetails, "No user details found please login");
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthorizationRecord");
        List<PreAuthorizationClaimantDetailCommand> searchResult = preAuthorizationRequestService.getPreAuthorizationForDefaultList(userDetails.getUsername());
        modelAndView.addObject("preAuthorizationResult", searchResult);
        modelAndView.addObject("searchCriteria", new SearchPreAuthorizationRecordDto());
        return modelAndView;
    }

    @Synchronized
    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadMandatoryDocument(GHClaimDocumentCommand ghClaimDocumentCommand, HttpServletRequest request) {
        ghClaimDocumentCommand.setUserDetails(getLoggedInUserDetail(request));
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
    public Result removePreAuthorizationAdditionalDocument(@Valid @RequestBody PreAuthorizationRemoveAdditionalCommand preAuthorizationRemoveAdditionalCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while updating comments", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(preAuthorizationRemoveAdditionalCommand);
            return Result.success("Document deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithcomments", method = RequestMethod.POST)
    public @ResponseBody Result updateWithComment(@Valid @RequestBody UpdateCommentCommand updateCommentCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while updating comments", bindingResult.getAllErrors());
        }
        try{
            updateCommentCommand.setUserDetails(getLoggedInUserDetail(request));
            updateCommentCommand.setCommentDateTime(DateTime.now());
            Set<CommentDetail> comments = commandGateway.sendAndWait(updateCommentCommand);
            return Result.success("Proposal submitted successfully", comments);
        } catch (Exception e){
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getpreauthorizationclaimantdetailcommandfrompreauthorizationrequestid", method = RequestMethod.GET)
    @ResponseBody
    public PreAuthorizationClaimantDetailCommand getPreAuthorizationClaimantDetailCommandFromPreAuthorizationRequestId(@RequestParam String preAuthorizationId, HttpServletResponse response) throws IOException {
        if(isEmpty(preAuthorizationId)){
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
        if(isEmpty(preAuthorizationId)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return Sets.newHashSet();
        }
        Set<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = preAuthorizationRequestService.findAdditionalDocuments(preAuthorizationId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/underwriter/getlistofpreauthorizationassigned", method = RequestMethod.POST)
    public List<PreAuthorizationClaimantDetailCommand> getDefaultListOfPreAuthorizationAssignedToUnderwriter(HttpServletResponse response, HttpServletRequest request){
        UserDetails userDetails = getLoggedInUserDetail(request);
        notNull(userDetails, "No user login details found please login.");
        return preAuthorizationRequestService.getDefaultListOfPreAuthorizationAssignedToUnderwriter(userDetails.getUsername());
    }

    @RequestMapping(value = "/searchpreauthorizationforunderwriterbycriteria", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPreAuthorizationForUnderWriterByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthorizationRequestRecord");
        UserDetails userDetails = getLoggedInUserDetail(request);
        if(isNotEmpty(userDetails)) {
            List<PreAuthorizationClaimantDetailCommand> searchResult = preAuthorizationRequestService.searchPreAuthorizationForUnderWriterByCriteria(searchPreAuthorizationRecordDto, userDetails.getUsername());
            modelAndView.addObject("searchResult", searchResult);
        }
        modelAndView.addObject("searchResult", searchPreAuthorizationRecordDto);
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/approve", method = RequestMethod.POST)
    public Result approvedByUnderwriter(@Valid @RequestBody ApprovePreAuthorizationCommand approvePreAuthorizationCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String preAuthorizationRequestId = commandGateway.sendAndWait(approvePreAuthorizationCommand);
            return Result.success("Pre Authorization Request successfully submitted");
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/reject", method = RequestMethod.POST)
    public Result rejectedByUnderwriter(@Valid @RequestBody RejectPreAuthorizationCommand rejectPreAuthorizationCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String preAuthorizationRequestId = commandGateway.sendAndWait(rejectPreAuthorizationCommand);
            return Result.success("Pre Authorization Request successfully submitted");
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/return", method = RequestMethod.POST)
    public Result returnByUnderwriter(@Valid @RequestBody ReturnPreAuthorizationCommand returnPreAuthorizationCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String preAuthorizationRequestId = commandGateway.sendAndWait(returnPreAuthorizationCommand);
            return Result.success("Pre Authorization Request successfully submitted");
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/routetoseniorunderwriter", method = RequestMethod.POST)
    public Result routeToSeniorUnderwriter(@Valid @RequestBody RoutePreAuthorizationCommand routePreAuthorizationCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            String preAuthorizationRequestId = commandGateway.sendAndWait(routePreAuthorizationCommand);
            return Result.success("Pre Authorization Request successfully submitted");
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

}
