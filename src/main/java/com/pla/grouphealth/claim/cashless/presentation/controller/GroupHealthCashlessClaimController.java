package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.pla.grouphealth.claim.cashless.application.command.claim.*;
import com.pla.grouphealth.claim.cashless.application.service.claim.GHCashlessClaimExcelHeader;
import com.pla.grouphealth.claim.cashless.application.service.claim.GroupHealthCashlessClaimService;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.domain.exception.*;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchReopenedClaimDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GHCashlessClaimMailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.SearchGroupHealthCashlessClaimRecordDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimRelatedFileUploadDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.publishedlanguage.contract.IAuthenticationFacade;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 2/03/2016.
 */
@RestController
@RequestMapping(value = "/grouphealth/claim/cashless/claim")
public class GroupHealthCashlessClaimController {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private PreAuthorizationService preAuthorizationService;
    @Autowired
    private PreAuthorizationRequestService preAuthorizationRequestService;
    @Autowired
    private GroupHealthCashlessClaimService groupHealthCashlessClaimService;
    @Autowired
    @Qualifier("authenticationFacade")
    private IAuthenticationFacade authenticationFacade;
    @Autowired
    private MailService mailService;

    @RequestMapping(value = "/downloadghcashlessclaimtemplate/{hcpCode}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("hcpCode") String ghCashlessClaimCode, HttpServletResponse response) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GroupHealthCashlessClaimTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook hcpRateExcel = preAuthorizationService.getGHCashlessClaimPreAuthtemplate(ghCashlessClaimCode, GHCashlessClaimExcelHeader.class);
        hcpRateExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/getghcashlessclaimuploadview" ,method = RequestMethod.GET)
    public ModelAndView cashlessUpload(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/ghcashlessclaimupload");
        return modelAndView;
    }


    @RequestMapping(value = "/loadpageforghcashlessclaimupdateview" ,method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView loadPageforghCashlessClaimupdateview(@RequestParam String groupHealthCashlessClaimId, @RequestParam String clientId, HttpServletResponse response) throws IOException, GroupHealthCashlessClaimProcessingException {
        ModelAndView modelAndView=new ModelAndView();
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        groupHealthCashlessClaimService.populateGroupHeathCashlessClaimWithProcessorId(groupHealthCashlessClaimId, userName);
        modelAndView.setViewName("pla/grouphealth/claim/ghcashlessclaim");
        return modelAndView;
    }

    @ExceptionHandler(GroupHealthCashlessClaimProcessingException.class)
    public ModelAndView handleException(GroupHealthCashlessClaimProcessingException ex){
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchcashlessclaim");
        List<GroupHealthCashlessClaimDto> searchResult = groupHealthCashlessClaimService.getCashlessClaimByDefaultList(userName);
        modelAndView.addObject("CashlessResult", searchResult);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto().updateWithShowModalWin());
        return modelAndView;
    }

    @RequestMapping(value = "/uploadghcashlessclaimtemplate", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadGroupHealthCashlessClaimTemplate(ClaimRelatedFileUploadDto claimRelatedFileUploadDto, HttpServletRequest request) throws IOException {
        Result result = null;
        MultipartFile file = claimRelatedFileUploadDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType())|| "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel",Boolean.FALSE);
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook ghCashlessCLaimTemplate = new HSSFWorkbook(fs);
        try {
            Map dataMap = Maps.newHashMap();
            dataMap.put("hcpCode", claimRelatedFileUploadDto.getHcpCode());
            boolean isValidInsuredTemplate = groupHealthCashlessClaimService.isValidInsuredTemplate(ghCashlessCLaimTemplate, dataMap);
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(claimRelatedFileUploadDto.getHcpCode());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                ghCashlessCLaimTemplate.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded claim template is not valid.Please download to check the errors", Boolean.TRUE);
            }
            String userName = StringUtils.EMPTY;
            Authentication authentication = authenticationFacade.getAuthentication();
            if(!(authentication instanceof AnonymousAuthenticationToken)){
                userName = authentication.getName();
            }
            Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtoList = preAuthorizationService.transformToPreAuthorizationDetailDto(ghCashlessCLaimTemplate, GHCashlessClaimExcelHeader.class);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new UploadGroupHealthCashlessClaimCommand(claimRelatedFileUploadDto.getHcpCode(), claimUploadedExcelDataDtoList, claimRelatedFileUploadDto.getBatchDate(), userName), callback);
            callback.onSuccess(callback.get());
            return Result.success("Claim Details successfully uploaded.");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage(), Boolean.FALSE);
        }
    }

    @RequestMapping(value = "/downloaderrorpreauthtemplate/{hcpCode}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("hcpCode") String hcpCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GroupHealthCashlessClaimTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(hcpCode);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }

    @RequestMapping(value = "/getgrouphealthcashlessclaimdtobygrouphealthcashlessclaimid", method = RequestMethod.GET)
    @ResponseBody
    public GroupHealthCashlessClaimDto getGroupHealthCashlessClaimDtoBygroupHealthCashlessClaimId(@RequestParam String groupHealthCashlessClaimId, HttpServletResponse response) throws IOException {
        if (isEmpty(groupHealthCashlessClaimId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "preAuthorizationId cannot be empty");
            return null;
        }
        return groupHealthCashlessClaimService.getGroupHealthCashlessClaimDtoBygroupHealthCashlessClaimId(groupHealthCashlessClaimId);
    }

    @RequestMapping(value = "/updategrouphealthcashlessclaim", method = RequestMethod.POST)
    public Result createUpdate(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while updating cashless claim", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            UpdateGroupHealthCashlessClaimCommand updateGroupHealthCashlessClaimCommand = new UpdateGroupHealthCashlessClaimCommand(groupHealthCashlessClaimDto, null, userName);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(updateGroupHealthCashlessClaimCommand, callback);
            callback.onSuccess(callback.get());
            return Result.success("Group Health cashless claim successfully updated");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }


    @RequestMapping(value = "/submitgrouphealthcashlessclaim", method = RequestMethod.POST)
    public Result submitPreAuthorization(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while submitting Group Health cashless claim", bindingResult.getAllErrors());
        }
        try {
            RoutingLevel routingLevel = null;
            if(groupHealthCashlessClaimDto.isSubmitEventFired()) {
                routingLevel = groupHealthCashlessClaimService.getRoutingLevelForPreAuthorization(groupHealthCashlessClaimDto);
            }
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto.updateWithClaimProcessorUserId(userName);
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            UpdateGroupHealthCashlessClaimCommand updateGroupHealthCashlessClaimCommand = new UpdateGroupHealthCashlessClaimCommand(groupHealthCashlessClaimDto, routingLevel, userName);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(updateGroupHealthCashlessClaimCommand, callback);
            callback.onSuccess(callback.get());
            return Result.success(callback.get().toString());
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getmandatorydocuments/{clientId}/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("clientId") String clientId, @PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId, HttpServletResponse response) throws Exception {
        if (isEmpty(clientId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "clientId cannot be empty");
            return Lists.newArrayList();
        }
        List<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = groupHealthCashlessClaimService.findMandatoryDocuments(new FamilyId(clientId), groupHealthCashlessClaimId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/getcashlessclaimfordefaultlist", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getCashlessForDefaultList(HttpServletRequest request) {
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchcashlessclaim");
        List<GroupHealthCashlessClaimDto> searchResult = groupHealthCashlessClaimService.getCashlessClaimByDefaultList(userName);
        modelAndView.addObject("CashlessResult", searchResult);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto());
        return modelAndView;
    }

    @RequestMapping(value = "/getghcashlessclaimbycriteria", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView getCashlessClaimByCriteria(SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto) {
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchcashlessclaim");
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> searchResult = groupHealthCashlessClaimService.getCashlessClaimByCriteria(searchGroupHealthCashlessClaimRecordDto, userName);
        modelAndView.addObject("CashlessResult", searchResult);
        modelAndView.addObject("searchCriteria", searchGroupHealthCashlessClaimRecordDto);
        return modelAndView;
    }


    @Synchronized
    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadMandatoryDocument(GroupHealthCashlessClaimDocumentCommand groupHealthCashlessClaimDocumentCommand, HttpServletRequest request) {
        groupHealthCashlessClaimDocumentCommand.setUserDetails(preAuthorizationRequestService.getUserDetailFromAuthentication());
        try {
            FutureCallback callback = new FutureCallback();
            commandGateway.send(groupHealthCashlessClaimDocumentCommand, callback);
            callback.onSuccess(callback.get());
            return Result.success("Documents uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getadditionaldocuments/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list additional documents which is being configured in Mandatory Document SetUp")
    public Set<GHProposalMandatoryDocumentDto> findAdditionalDocuments(@PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId, HttpServletResponse response) throws IOException {
        if (isEmpty(groupHealthCashlessClaimId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "groupHealthCashlessClaimId cannot be empty");
            return Sets.newHashSet();
        }
        Set<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = groupHealthCashlessClaimService.findAdditionalDocuments(groupHealthCashlessClaimId);
        return ghProposalMandatoryDocumentDtos;
    }

    @Synchronized
    @RequestMapping(value = "/removeadditionalDocument", method = RequestMethod.POST)
    @ResponseBody
    public Result removePreAuthorizationAdditionalDocument(@Valid @RequestBody GroupHealthCashlessClaimRemoveAdditionalDocumentCommand groupHealthCashlessClaimRemoveAdditionalDocumentCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while removing additional documents", bindingResult.getAllErrors());
        }
        try {
            FutureCallback callback = new FutureCallback();
            commandGateway.send(groupHealthCashlessClaimRemoveAdditionalDocumentCommand, callback);
            callback.onSuccess(callback.get());
            return Result.success("Document deleted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/billmismatch/approve", method = RequestMethod.POST)
    public Result billMismatchApproved(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while approving Group Health cashless claim bill mismatch.", bindingResult.getAllErrors());
        }
        try {
            RoutingLevel routingLevel = groupHealthCashlessClaimService.getRoutingLevelForPreAuthorization(groupHealthCashlessClaimDto);
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto.updateWithBillMismatchProcessorId(userName);
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            GroupHealthCashlessClaimBillMismatchedApprovedCommand groupHealthCashlessClaimBillMismatchedApprovedCommand = new GroupHealthCashlessClaimBillMismatchedApprovedCommand(groupHealthCashlessClaimDto, routingLevel, userName);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(groupHealthCashlessClaimBillMismatchedApprovedCommand, callback);
            callback.onSuccess(callback.get());
            return Result.success(callback.get().toString());
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/servicemismatch/approve", method = RequestMethod.POST)
    public Result serviceMismatchApproved(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while approving Group Health cashless claim service mismatch", bindingResult.getAllErrors());
        }
        try {
            RoutingLevel routingLevel = groupHealthCashlessClaimService.getRoutingLevelForPreAuthorization(groupHealthCashlessClaimDto);
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto.updateWithServiceMismatchProcessorId(userName);
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            GroupHealthCashlessClaimServiceMismatchedApprovedCommand groupHealthCashlessClaimServiceMismatchedApprovedCommand = new GroupHealthCashlessClaimServiceMismatchedApprovedCommand(groupHealthCashlessClaimDto, routingLevel, userName);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(groupHealthCashlessClaimServiceMismatchedApprovedCommand, callback);
            callback.onSuccess(callback.get());
            return Result.success(callback.get().toString());
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }



    @RequestMapping(value = "/underwriter/getdefaultlistofunderwriterlevels/{level}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getDefaultListOfUnderwriterLevels(@PathVariable("level") String level, HttpServletResponse response) {
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> groupHealthCashlessClaimDtos = groupHealthCashlessClaimService.getDefaultListByUnderwriterLevel(level, userName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("claimResult", groupHealthCashlessClaimDtos);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto().updateWithUnderwriterLevel(level));
        modelAndView.setViewName("pla/grouphealth/claim/searchghcashlessclaimunderwriter");
        return modelAndView;
    }


    @ExceptionHandler(GroupHealthCashlessClaimUnderWriterProcessingException.class)
    public ModelAndView handleException(GroupHealthCashlessClaimUnderWriterProcessingException ex){
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> groupHealthCashlessClaimDtos = groupHealthCashlessClaimService.getDefaultListByUnderwriterLevel(ex.getUnderwriterLevel(), userName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("claimResult", groupHealthCashlessClaimDtos);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto().updateWithUnderwriterLevel(ex.getUnderwriterLevel()).updateWithShowModalWin());
        modelAndView.setViewName("pla/grouphealth/claim/searchghcashlessclaimunderwriter");
        return modelAndView;
    }

    @RequestMapping (value= "/searchghcashlessclaimunderwriterbycriteria" , method = RequestMethod.POST)
    @ResponseBody
    public  ModelAndView searchCashlessClaimUnderwriterByCriteria(SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchghcashlessclaimunderwriter");
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> ghCashlessClaimMailDtos = groupHealthCashlessClaimService.searchCashlessClaimUnderwriterCriteria(searchGroupHealthCashlessClaimRecordDto, userName);
        modelAndView.addObject("claimResult", ghCashlessClaimMailDtos);
        modelAndView.addObject("searchCriteria", searchGroupHealthCashlessClaimRecordDto);
        return modelAndView;
    }

    @RequestMapping(value = "/loadunderwriterviewforupdate", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView loadUnderwriterViewForView(@RequestParam String groupHealthCashlessClaimId, @RequestParam String underwriterLevel, @RequestParam String clientId, HttpServletResponse response) throws IOException, GroupHealthCashlessClaimUnderWriterProcessingException {
        ModelAndView modelAndView = new ModelAndView();
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        groupHealthCashlessClaimService.populateGroupHeathCashlessClaimWithUnderwriterId(groupHealthCashlessClaimId, userName, underwriterLevel);
        modelAndView.setViewName("pla/grouphealth/claim/ghcashlessclaimunderwriter");
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/update", method = RequestMethod.POST)
    public Result updatedByUnderwriter(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while updating Group Health Cashless Claim", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new UnderwriterGroupHealthCashlessClaimUpdateCommand(groupHealthCashlessClaimDto, userName), callback);
            callback.onSuccess(callback.get());
            return Result.success("Group Health Cashless Claim successfully updated.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/approve", method = RequestMethod.POST)
    public Result approvedByUnderwriter(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while approving Group Health Cashless Claim", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new ApproveGroupHealthCashlessClaimCommand(groupHealthCashlessClaimDto, userName),callback);
            callback.onSuccess(callback.get());
            return Result.success("Group Health Cashless Claim successfully approved.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public Result rejectedByUnderwriter(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while rejecting Group Health Cashless Claim", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto.updateClaimRejectedBy(userName);
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new RejectGroupHealthCashlessClaimCommand(groupHealthCashlessClaimDto, userName), callback);
            callback.onSuccess(callback.get());
            return Result.success("Group Health Cashless Claim successfully rejected.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/return", method = RequestMethod.POST)
    public Result returnByUnderwriter(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while returning Group Health Cashless Claim", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new ReturnGroupHealthCashlessClaimCommand(groupHealthCashlessClaimDto, userName),callback);
            callback.onSuccess(callback.get());
            return Result.success("Group Health Cashless Claim successfully returned.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/routetoseniorunderwriter", method = RequestMethod.POST)
    public Result routeToSeniorUnderwriter(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while routing Group Health Cashless Claim", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new RouteGroupHealthCashlessClaimCommand(groupHealthCashlessClaimDto, userName), callback);
            callback.onSuccess(callback.get());
            return Result.success("Group Health Cashless Claim successfully routed to senior underwriter.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/addrequirement", method = RequestMethod.POST)
    public Result addRequirement(@Valid @RequestBody GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while routing Group Health Cashless Claim", bindingResult.getAllErrors());
        }
        try {
            String userName = preAuthorizationRequestService.getLoggedInUsername();
            groupHealthCashlessClaimDto = groupHealthCashlessClaimService.reConstructProbableClaimAmountForServices(groupHealthCashlessClaimDto);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new AddRequirementGroupHealthCashlessClaimCommand(groupHealthCashlessClaimDto, userName), callback);
            callback.onSuccess(callback.get());
            return Result.success("Group Health Cashless Claim successfully routed to senior underwriter.");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }
    @RequestMapping(value = "/getallrelevantservices/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> getAllRelevantServices(@PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId, HttpServletResponse response) throws IOException {
        if(isEmpty(groupHealthCashlessClaimId)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "groupHealthCashlessClaimId cannot be empty");
            return Collections.EMPTY_SET;
        }
        return groupHealthCashlessClaimService.getAllRelevantServices(groupHealthCashlessClaimId);
    }

    @RequestMapping(value = "/getunderwriterlevelfromclaim/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getUnderwriterLevelForClaim(@PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId, HttpServletResponse response) throws IOException {
        if(isEmpty(groupHealthCashlessClaimId)){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "groupHealthCashlessClaimId cannot be empty");
            return Result.failure();
        }
        try {
            Object level = groupHealthCashlessClaimService.getUnderwriterLevelForPreAuthorization(groupHealthCashlessClaimId);
            return Result.success("", level);
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }


    @RequestMapping(value = "/underwriter/getgrouphealthcashlessclaimrejectionletter/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/emailghcashlessclaimrejectionletter");
        modelAndView.addObject("mailContent", groupHealthCashlessClaimService.getGroupHealthCashlessClaimRejectionLetter(groupHealthCashlessClaimId));
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/getgrouphealthcashlessclaimaddrequirementrequestletter/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    public ModelAndView getAddRequirementRequestLetter(@PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/emailghcashlessclaimrequirementletter");
        modelAndView.addObject("mailContent", groupHealthCashlessClaimService.getAddRequirementRequestLetter(groupHealthCashlessClaimId));
        return modelAndView;
    }

    @RequestMapping(value = "/underwriter/emailgrouphealthcashlessclaimrejectionletter", method = RequestMethod.POST)
    @ResponseBody
    public Result emailPreAuthorizationRejectionLetter(@RequestBody GHCashlessClaimMailDto ghCashlessClaimMailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            mailService.sendMailWithAttachment(ghCashlessClaimMailDto.getSubject(), ghCashlessClaimMailDto.getMailContent(), Lists.newArrayList(), ghCashlessClaimMailDto.getRecipientMailAddress().split(";"));
            groupHealthCashlessClaimService.updateRejectionEmailSentFlag(ghCashlessClaimMailDto.getGroupHealthCashlessClaimId());
            return Result.success("Email sent successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/emailgrouphealthcashlessclaimrequirementletter", method = RequestMethod.POST)
    @ResponseBody
    public Result emailPreAuthorizationRequirementLetter(@RequestBody GHCashlessClaimMailDto ghCashlessClaimMailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            mailService.sendMailWithAttachment(ghCashlessClaimMailDto.getSubject(), ghCashlessClaimMailDto.getMailContent(), Lists.newArrayList(), ghCashlessClaimMailDto.getRecipientMailAddress().split(";"));
            groupHealthCashlessClaimService.updateRequirementEmailSentFlag(ghCashlessClaimMailDto.getGroupHealthCashlessClaimId());
            return Result.success("Email sent successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/checkifgrouphealthcashlessclaimrejectionemailsent/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    @ResponseBody
    public Result checkIfPreAuthorizationRejectionEmailSent(@PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId) {
        if (isEmpty(groupHealthCashlessClaimId)) {
            return Result.failure("PreAuthorizationId cannot be empty.");
        }
        try {
            boolean result = groupHealthCashlessClaimService.checkIfGroupHealthCashlessClaimRejectionEmailSent(groupHealthCashlessClaimId);
            return Result.success("Email sent successfully", result);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/underwriter/checkifgrouphealthcashlessclaimrequirementemailsent/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    @ResponseBody
    public Result checkIfPreAuthorizationRequirementEmailSent(@PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId) {
        if (isEmpty(groupHealthCashlessClaimId)) {
            return Result.failure("PreAuthorizationId cannot be empty.");
        }
        try {
            boolean result = groupHealthCashlessClaimService.checkIfGroupHealthCashlessClaimRequirementEmailSent(groupHealthCashlessClaimId);
            return Result.success("Email sent successfully", result);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getallservicemismatchedgrouphealthcashlessclaims", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getAllServiceMismatchedGroupHealthCashlessClaims() {
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> groupHealthCashlessClaimDtos = groupHealthCashlessClaimService.getAllServiceMismatchedGroupHealthCashlessClaims(userName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("claimResult", groupHealthCashlessClaimDtos);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto());
        modelAndView.setViewName("pla/grouphealth/claim/searchghcashlessclaimservicemismatch");
        return modelAndView;
    }

    @RequestMapping(value = "/getallbillmismatchedgrouphealthcashlessclaims", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getAllBillMismatchedGroupHealthCashlessClaims() {
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> groupHealthCashlessClaimDtos = groupHealthCashlessClaimService.getAllBillMismatchedGroupHealthCashlessClaims(userName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("claimResult", groupHealthCashlessClaimDtos);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto());
        modelAndView.setViewName("pla/grouphealth/claim/searchghcashlessclaimbillmismatch");
        return modelAndView;
    }
    @RequestMapping(value = "/updateviewbillmismatch", method = RequestMethod.GET)
    public ModelAndView getBillMismatch(@RequestParam String groupHealthCashlessClaimId, @RequestParam String clientId,HttpServletResponse httpServletResponse) throws IOException, GroupHealthCashlessClaimBillMismatchProcessingException {
        ModelAndView modelAndView = new ModelAndView();
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        groupHealthCashlessClaimService.populateGroupHeathCashlessClaimWithBillMismatchProcessorId(groupHealthCashlessClaimId, userName);
        modelAndView.setViewName("pla/grouphealth/claim/ghcashlessclaimbillmismatch");
        return modelAndView;
    }

    @ExceptionHandler(GroupHealthCashlessClaimBillMismatchProcessingException.class)
    public ModelAndView handleException(GroupHealthCashlessClaimBillMismatchProcessingException ex){
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> groupHealthCashlessClaimDtos = groupHealthCashlessClaimService.getAllBillMismatchedGroupHealthCashlessClaims(userName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("claimResult", groupHealthCashlessClaimDtos);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto().updateWithShowModalWin());
        modelAndView.setViewName("pla/grouphealth/claim/searchghcashlessclaimbillmismatch");
        return modelAndView;
    }

    @RequestMapping(value = "/updateviewservicemismatch", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getServiceMismatch(@RequestParam String groupHealthCashlessClaimId, @RequestParam String clientId,HttpServletResponse httpServletResponse) throws IOException, GroupHealthCashlessClaimServiceMismatchProcessingException {
        ModelAndView modelAndView = new ModelAndView();
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        groupHealthCashlessClaimService.populateGroupHeathCashlessClaimWithServiceMismatchProcessorId(groupHealthCashlessClaimId, userName);
        modelAndView.setViewName("pla/grouphealth/claim/ghcashlessclaimservicemismatch");
        return modelAndView;
    }

    @ExceptionHandler(GroupHealthCashlessClaimServiceMismatchProcessingException.class)
    public ModelAndView handleException(GroupHealthCashlessClaimServiceMismatchProcessingException ex){
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> groupHealthCashlessClaimDtos = groupHealthCashlessClaimService.getAllServiceMismatchedGroupHealthCashlessClaims(userName);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("claimResult", groupHealthCashlessClaimDtos);
        modelAndView.addObject("searchCriteria", new SearchGroupHealthCashlessClaimRecordDto().updateWithShowModalWin());
        modelAndView.setViewName("pla/grouphealth/claim/searchghcashlessclaimservicemismatch");
        return modelAndView;
    }

    @RequestMapping(value = "/getbillmismatchrecordbysearchcriteria", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView getBillMismatchRecordBySearchCriteria (SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchghcashlessclaimbillmismatch");
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> ghCashlessClaimMailDtos = groupHealthCashlessClaimService.searchCashlessClaimBillMismatchCriteria(searchGroupHealthCashlessClaimRecordDto, userName);
        modelAndView.addObject("claimResult", ghCashlessClaimMailDtos);
        modelAndView.addObject("searchCriteria", searchGroupHealthCashlessClaimRecordDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getservicemismatchrecordbysearchcriteria", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView getServiceMismatchRecordBySearchCriteria (SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchghcashlessclaimservicemismatch");
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        List<GroupHealthCashlessClaimDto> ghCashlessClaimMailDtos = groupHealthCashlessClaimService.searchCashlessClaimServiceMismatchCriteria(searchGroupHealthCashlessClaimRecordDto, userName);
        modelAndView.addObject("claimResult", ghCashlessClaimMailDtos);
        modelAndView.addObject("searchCriteria", searchGroupHealthCashlessClaimRecordDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getallreopenedclaimdefaultlist", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getAllClaimReopenDefaultList (){
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchghcashlessclaimreopen");
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        modelAndView.addObject("claimResult", groupHealthCashlessClaimService.getAllReopenedClaimForDefaultDisplay(userName));
        modelAndView.addObject("searchCriteria", new SearchReopenedClaimDetailDto());
        return modelAndView;
    }

    @RequestMapping (value= "/searchreopenedghcashlessclaimbycriteria" , method = RequestMethod.POST)
    @ResponseBody
    public  ModelAndView searchCashlessClaimUnderwriterByCriteria(SearchReopenedClaimDetailDto searchReopenedClaimDetailDto, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchghcashlessclaimreopen");
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        List<SearchReopenedClaimDetailDto> searchReopenedClaimDetailDtos = groupHealthCashlessClaimService.searchReopenedCashlessClaimByCriteria(searchReopenedClaimDetailDto, userName);
        modelAndView.addObject("claimResult", searchReopenedClaimDetailDtos);
        modelAndView.addObject("searchCriteria", searchReopenedClaimDetailDto);
        return modelAndView;
    }

    @RequestMapping(value = "/updateviewcashlessclaimreopen", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getCashlessClaimReopen(@RequestParam String groupHealthCashlessClaimId, @RequestParam String clientId,HttpServletResponse httpServletResponse) throws IOException, ReopenGroupHealthCashlessClaimProcessingException {
        ModelAndView modelAndView = new ModelAndView();
        String userName = preAuthorizationRequestService.getLoggedInUsername();
        groupHealthCashlessClaimService.populateGroupHeathCashlessClaimWithReopenProcessorUserId(groupHealthCashlessClaimId, userName);
        modelAndView.setViewName("pla/grouphealth/claim/ghcashlessclaimreopen");
        return modelAndView;
    }

    @ExceptionHandler(ReopenGroupHealthCashlessClaimProcessingException.class)
    public ModelAndView handleException(ReopenGroupHealthCashlessClaimProcessingException ex){
        String userName = groupHealthCashlessClaimService.getLoggedInUsername();
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchghcashlessclaimreopen");
        modelAndView.addObject("claimResult", groupHealthCashlessClaimService.getAllReopenedClaimForDefaultDisplay(userName));
        modelAndView.addObject("searchCriteria", new SearchReopenedClaimDetailDto().updateWithShowModalWin());
        return modelAndView;
    }

}


