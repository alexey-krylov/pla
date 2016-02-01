
package com.pla.individuallife.endorsement.presentation.controller;

import com.google.common.collect.Maps;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.query.MasterFinder;
import com.pla.individuallife.endorsement.application.command.*;
import com.pla.individuallife.endorsement.application.service.ILEndorsementService;
import com.pla.individuallife.endorsement.application.service.IndividualLifeEndorsementChecker;
import com.pla.individuallife.endorsement.domain.service.IndividualLifeEndorsementService;
import com.pla.individuallife.endorsement.presentation.dto.ILEndorsementApproverCommentDto;
import com.pla.individuallife.endorsement.presentation.dto.ILEndorsementDto;
import com.pla.individuallife.endorsement.presentation.dto.SearchILEndorsementDto;
import com.pla.individuallife.endorsement.query.ILEndorsementFinder;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.policy.service.ILPolicyService;
import com.pla.individuallife.proposal.application.command.ILProposalUpdateWithProposerCommand;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.individuallife.sharedresource.dto.SearchILPolicyDto;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.sharedkernel.domain.model.EndorsementNumber;
import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.domain.model.Relationship;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Raghu bandi on 8/4/2015.
 */
@Controller
@RequestMapping(value = "/individuallife/endorsement")
public class ILEndorsementController {

    @Autowired
    private ILEndorsementService ilEndorsementService;

    @Autowired
    private IndividualLifeEndorsementService individualLifeEndorsementService;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private ILEndorsementFinder ilEndorsementFinder;

    @Autowired
    private ILPolicyService ilPolicyService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private IndividualLifeEndorsementChecker individualLifeEndorsementChecker;

    @Autowired
    private MasterFinder masterFinder;

    @RequestMapping(value = "/createendorsement",method = RequestMethod.GET)
    public ModelAndView openViewPageGroup(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createEndorsement");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankNames")
    @ResponseBody
    public List<Map<String, Object>> getAllBankNames() {
        return masterFinder.getAllBank();
    }


    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchILPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchpolicy/{policyNumber}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity searchPolicy(@PathVariable("policyNumber") String policyNumber) {

        SearchILPolicyDto searchILPolicyDto = new SearchILPolicyDto();
        searchILPolicyDto.setPolicyNumber(policyNumber);
        ILPolicyDto ilPolicyDto = ilEndorsementService.searchByPolicyNumber(searchILPolicyDto);
        if (ilPolicyDto != null) {
            return new ResponseEntity(Result.success("Policy details found", ilPolicyDto), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Policy details Not Found for "+policyNumber), HttpStatus.INTERNAL_SERVER_ERROR);
    }

/*    @RequestMapping(value = "/downloadtemplatebyendorsementtype/{endorsementType}/{endorsementId}", method = RequestMethod.GET)
    public void downloadTemplateByEndorsementType(@PathVariable("endorsementType") GLEndorsementType glEndorsementType, @PathVariable("endorsementId") String endorsementId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        String fileName = glEndorsementType.getDescription().replaceAll("[\\s]*", "");
        response.setHeader("content-disposition", "attachment; filename=" + (fileName + "_Template.xls") + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = ilEndorsementService.generateEndorsementExcel(glEndorsementType, new EndorsementId(endorsementId));
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }*/

    @RequestMapping(value = "/downloaderrortemplate/{endorsementId}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("endorsementId") String endorsementId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "errorInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(endorsementId);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }

    @RequestMapping(value = "/opencreateendorsementpage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createEndorsement(@RequestBody ILPolicyDto ilPolicyDto, HttpServletRequest request) {
        //String endorsementRequestNumber = "135667";
        //return new ResponseEntity(Result.success("Endorsement successfully created", endorsementRequestNumber), HttpStatus.OK);
        UserDetails userDetails = getLoggedInUserDetail(request);
        ILCreateEndorsementCommand ilCreateEndorsementCommand = new ILCreateEndorsementCommand(userDetails,ilPolicyDto);
        String endorsementRequestNumber = commandGateway.sendAndWait(ilCreateEndorsementCommand);
        return new ResponseEntity(Result.success("Endorsement successfully created", endorsementRequestNumber), HttpStatus.OK);
    }

    @RequestMapping(value = "/openupdateendorsementpage", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateEndorsement(@RequestBody ILEndorsementDto iLEndorsementDto, HttpServletRequest request) {
        //String endorsementRequestNumber = "135667";
        //return new ResponseEntity(Result.success("Endorsement successfully created", endorsementRequestNumber), HttpStatus.OK);
        UserDetails userDetails = getLoggedInUserDetail(request);
        ILUpdateEndorsementCommand ilUpdateEndorsementCommand = new ILUpdateEndorsementCommand(userDetails,iLEndorsementDto);
        String endorsementRequestNumber = commandGateway.sendAndWait(ilUpdateEndorsementCommand);
        return new ResponseEntity(Result.success("Endorsement successfully updated", endorsementRequestNumber), HttpStatus.OK);
    }


    @RequestMapping(value = "/editEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit proposal page")
    private ModelAndView openCreateEndorosement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/editEndorsementUpload", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit Endorsement upload page")
    private ModelAndView openCreateEndorsementUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createEndorsementUpload");
        return modelAndView;
    }

    @RequestMapping(value = "/editEndorsementReturnStatus", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit endorsement page")
    public ModelAndView gotoCreateEndorsementReturnStatus() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createEndorsementReturnStatus");
        return modelAndView;
    }

    @RequestMapping(value = "/editEndorsementReturnStatusUpload", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit endorsement page")
    public ModelAndView gotoCreateEndorsementReturnStatusUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createEndorsementUploadReturnStatus");
        return modelAndView;
    }

    @RequestMapping(value = "/viewApprovalEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval endorsement page in view Mode")
    public ModelAndView gotoApprovalEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createApprovalEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/viewUploadApprovalEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval endorsement page in view Mode")
    public ModelAndView gotoUploadApprovalEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createUploadApprovalEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/approveEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval Endorsement page")
    private ModelAndView openApprovalEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/createApprovalEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/getendorsementnumber/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getEndorsementNumber(@PathVariable("endorsementId") String endorsementId) {
        Map endorsmentMap = ilEndorsementFinder.findEndorsementById(endorsementId);
        Map<String,Object> endorsement = Maps.newLinkedHashMap();
        endorsement.put("endorsementNumber", endorsmentMap.get("endorsementNumber") != null ? ((EndorsementNumber) endorsmentMap.get("endorsementNumber")).getEndorsementNumber() : "");
        endorsement.put("hasUploaded", endorsmentMap.get("endorsement") != null ? Boolean.TRUE : Boolean.FALSE);
        return new ResponseEntity(endorsement,HttpStatus.OK);
    }

    @RequestMapping(value = "/opensearchendorsement", method = RequestMethod.GET)
    public ModelAndView openSearchEndorsementPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/searchEndorsement");
        SearchILEndorsementDto searchILEndorsementDto = new SearchILEndorsementDto();
        searchILEndorsementDto.setEndorsementTypes(ILEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchCriteria", searchILEndorsementDto);
        return modelAndView;
    }

    @RequestMapping(value = "/openapprovalendorsement", method = RequestMethod.GET)
    public ModelAndView gotoApprovalEndorsementPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/viewApprovalEndorsement");
        SearchILEndorsementDto searchILEndorsementDto = new SearchILEndorsementDto();
        searchILEndorsementDto.setEndorsementTypes(ILEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchCriteria", searchILEndorsementDto);
        return modelAndView;
    }

    @RequestMapping(value = "/searchEndorsementApprovalpolicy", method = RequestMethod.POST)
    public ModelAndView searchEndorsementPolicy(SearchILEndorsementDto searchILEndorsementDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/viewApprovalEndorsement");
        modelAndView.addObject("searchCriteria", searchILEndorsementDto);
        searchILEndorsementDto.setEndorsementTypes(ILEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchResult", ilEndorsementService.searchEndorsement(searchILEndorsementDto, new String[]{"APPROVER_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE"}));
        return modelAndView;
    }

    @RequestMapping(value = "/searchendorsement", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To search endorsement")
    public ModelAndView searchEndorsement(@RequestBody SearchILEndorsementDto searchILEndorsementDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/searchEndorsement");
        modelAndView.addObject("searchCriteria", searchILEndorsementDto);
        searchILEndorsementDto.setEndorsementTypes(ILEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchResult", ilEndorsementService.searchEndorsement(searchILEndorsementDto, new String[]{"DRAFT", "APPROVER_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE","RETURN"}));
        return modelAndView;
    }

    @RequestMapping(value = "/getAgentDetailsByPlanAndAgentId/{planId}/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAgentDetailsByPlanAndAgentId(@PathVariable("planId") String planId,@PathVariable("agentId") String agentId ) {
        Map<String, Object> agent = ilEndorsementService.getAgentDetailsByPlanAndAgentId(planId, agentId);
        checkArgument(agent != null, "Agent not found for the plan");
        return agent;
    }

    @RequestMapping(value = "/searchendorsement/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity searchEndorsement(@PathVariable("endorsementId") String endorsementId) {
/*        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individualLife/endorsement/searchEndorsement");
        modelAndView.addObject("searchCriteria", searchILEndorsementDto);
        searchILEndorsementDto.setEndorsementTypes(ILEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchResult", ilEndorsementService.searchEndorsement(searchILEndorsementDto, new String[]{"DRAFT", "APPROVER_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE","RETURN"}));
        return modelAndView;*/

        ILEndorsementDto iLEndorsementDto = ilEndorsementService.findEndorsementByEndorsementId(endorsementId);

        //Map<String,Object> endorsement = Maps.newLinkedHashMap();
        //endorsement.put("endorsementNumber", endorsmentMap.get("endorsementNumber") != null ? ((EndorsementNumber) endorsmentMap.get("endorsementNumber")).getEndorsementNumber() : "");
        //endorsement.put("hasUploaded", endorsmentMap.get("endorsement") != null ? Boolean.TRUE : Boolean.FALSE);
        //return new ResponseEntity(endorsement,HttpStatus.OK);
        if (iLEndorsementDto != null) {
            return new ResponseEntity(Result.success("Endorsement details found", iLEndorsementDto), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Endorsement details Not Found for "+ endorsementId), HttpStatus.INTERNAL_SERVER_ERROR);
    }


/*    @RequestMapping(value = "/getagentdetailfrompolicy/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "Fetch agent detail for a given Policy ID")
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("endorsementId") String endorsementId) {
        PolicyId policyId = ilEndorsementService.getPolicyIdFromEndorsment(endorsementId);
        return ilPolicyService.getAgentDetail(policyId);
    }

    @RequestMapping(value = "/getproposerdetail/{endorsementId}")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get proposer detail from proposer")
    public ProposerDto getProposerDetail(@PathVariable("endorsementId") String endorsementId) {
        PolicyId policyId = ilEndorsementService.getPolicyIdFromEndorsment(endorsementId);
        return ilPolicyService.getProposerDetail(policyId);
    }*/

    @RequestMapping(value = "/getpolicydetail/{endorsementId}")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get Policy detail from proposer")
    public Map<String, Object> getPolicyDetail(@PathVariable("endorsementId") String endorsementId) throws ParseException {
        return ilEndorsementService.getPolicyDetail(endorsementId);
    }


    @RequestMapping(value = "/getmandatorydocuments/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("endorsementId") String endorsementId) {
        List<ILProposalMandatoryDocumentDto> ilProposalMandatoryDocumentDtos = ilEndorsementService.findMandatoryDocuments(endorsementId);
        return ilProposalMandatoryDocumentDtos;
    }

/*    @RequestMapping(value = "/getpremiumdetail/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get premium detail")
    public PremiumDetailDto getPremiumDetail(@PathVariable("endorsementId") String endorsementId,HttpServletRequest request) throws ParseException {
        UserDetails userDetails  = getLoggedInUserDetail(request);
        return individualLifeEndorsementService.recalculatePremium(endorsementId,userDetails);

    }*/

    @RequestMapping(value = "/downloadmandatorydocument/{gridfsdocid}", method = RequestMethod.GET)
    public void downloadMandatoryDocument(@PathVariable("gridfsdocid") String gridfsDocId, HttpServletResponse response) throws IOException {
        GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(gridfsDocId)));
        response.reset();
        response.setContentType(gridFSDBFile.getContentType());
        response.setHeader("content-disposition", "attachment; filename=" + gridFSDBFile.getFilename() + "");
        OutputStream outputStream = response.getOutputStream();
        org.apache.commons.io.IOUtils.copy(gridFSDBFile.getInputStream(), outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @Synchronized
    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadMandatoryDocument(ILEndorsementDocumentCommand glEndorsementDocumentCommand, HttpServletRequest request) {
        glEndorsementDocumentCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.sendAndWait(glEndorsementDocumentCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Documents uploaded successfully"), HttpStatus.OK);
    }
/*    //TODO implement
    @RequestMapping(value = "/uploadinsureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(UploadInsuredDetailDto uploadInsuredDetailDto, HttpServletRequest request) throws IOException {
        MultipartFile file = uploadInsuredDetailDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType()) || "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidExcel = ilEndorsementService.isValidExcel(uploadInsuredDetailDto.getEndorsementType(), insuredTemplateWorkbook, new EndorsementId(uploadInsuredDetailDto.getEndorsementId()));
            if (!isValidExcel) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getEndorsementId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded template is not valid.Please download to check the errors");
            }
            GLEndorsementInsuredDto glEndorsementInsuredDto = ilEndorsementService.parseExcel(uploadInsuredDetailDto.getEndorsementType(), insuredTemplateWorkbook, new EndorsementId(uploadInsuredDetailDto.getEndorsementId()));
            GLEndorsementCommand glEndorsementCommand = new GLEndorsementCommand();
            glEndorsementCommand.setGlEndorsementType(uploadInsuredDetailDto.getEndorsementType());
            glEndorsementCommand.setEndorsementId(new EndorsementId(uploadInsuredDetailDto.getEndorsementId()));
            glEndorsementCommand.setGlEndorsementInsuredDto(glEndorsementInsuredDto);
            String endorsementId = commandGateway.sendAndWait(glEndorsementCommand);
            return Result.success("Insured detail uploaded successfully", endorsementId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }*/

    //TODO implement
    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProposalWithProposerDetail(@RequestBody ILProposalUpdateWithProposerCommand updateGLProposalWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update proposal proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            return Result.success("Proposer detail updated successfully");
        } catch (Exception e) {
            return Result.failure();
        }
    }

    //TODO implement
    @RequestMapping(value = "/getapprovercomments/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list approval comments")
    public List<ILEndorsementApproverCommentDto> findApproverComments(@PathVariable("endorsementId") String endorsementId) {
        return individualLifeEndorsementService.findApproverComments(endorsementId);
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To submit endorsement for approval")
    public ResponseEntity submitProposal(@RequestBody SubmitILEndorsementCommand submitILEndorsementCommand, HttpServletRequest request) {
        try {
            submitILEndorsementCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(submitILEndorsementCommand);
            return new ResponseEntity(Result.success("Endorsement submitted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To reject endorsement for approval")
    public ResponseEntity rejectEndorsement(@RequestBody ReturnILEndorsementCommand rejectGLEndorsementCommand, HttpServletRequest request) {
        try {
            rejectGLEndorsementCommand.setUserDetails(getLoggedInUserDetail(request));
            rejectGLEndorsementCommand.setStatus(EndorsementStatus.REJECTED);
            commandGateway.sendAndWait(rejectGLEndorsementCommand);
            return new ResponseEntity(Result.success("Endorsement rejected successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/return", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To return endorsement to processor")
    public ResponseEntity returnEndorsement(@RequestBody ReturnILEndorsementCommand returnILEndorsementCommand, HttpServletRequest request) {
        try {
            returnILEndorsementCommand.setUserDetails(getLoggedInUserDetail(request));
            returnILEndorsementCommand.setStatus(EndorsementStatus.RETURN);
            commandGateway.sendAndWait(returnILEndorsementCommand);
            return new ResponseEntity(Result.success("Endorsement returned successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To approve endorsement by approval")
    public ResponseEntity approveEndorsement(@RequestBody ApproveILEndorsementCommand approveILEndorsementCommand, HttpServletRequest request) {
        try {
            approveILEndorsementCommand.setUserDetails(getLoggedInUserDetail(request));
            /*
            * service method which will check for the uploaded documents and waived documents..
            * */
            approveILEndorsementCommand.setStatus(EndorsementStatus.APPROVED);
             commandGateway.sendAndWait(approveILEndorsementCommand);
            return new ResponseEntity(Result.success("Endorsement approved successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
//            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/waivedocument", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To waive mandatory document by Approver")
    public ResponseEntity waiveDocument(@RequestBody ILWaiveMandatoryDocumentCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            String proposalId =  commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success("Mandatory Document waives successfully",proposalId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/getadditionaldocuments/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list additional documents which is being configured in Mandatory Document SetUp")
    public Set<ILProposalMandatoryDocumentDto> findAdditionalDocuments(@PathVariable("endorsementId") String endorsementId) {
        Set<ILProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = ilEndorsementService.findAdditionalDocuments(endorsementId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/getapprovedendorsement/{policyNumber}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list Endorsements which are done for the policy")
    public List<ILEndorsementDto> getApprovedEndorsementByPolicyNumber(@PathVariable("policyNumber") String policyNumber) {
        List<ILEndorsementDto> glEndorsementSchedules = ilEndorsementService.getApprovedEndorsementByPolicyNumber(policyNumber);
        return glEndorsementSchedules;
    }

    @RequestMapping(value = "/getallrelations/{age}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list all the relations")
    public List<Map<String,Object>> getAllRelationShip(@PathVariable("age") Integer age) {
        if (age<=16) {
            return Arrays.asList(Relationship.values()).parallelStream().filter(new Predicate<Relationship>() {
                @Override
                public boolean test(Relationship relationship) {
                    return Arrays.asList(Relationship.DAUGHTER,Relationship.SON,Relationship.STEP_DAUGHTER,Relationship.STEP_SON,Relationship.SISTER,Relationship.BROTHER).contains(relationship);
                }
            }).map(new RelationTransformer()).collect(Collectors.toList());
        }
        return Arrays.asList(Relationship.values()).parallelStream().filter(new Predicate<Relationship>() {
            @Override
            public boolean test(Relationship relationship) {
                return !(Arrays.asList(Relationship.DAUGHTER, Relationship.SON, Relationship.STEP_DAUGHTER, Relationship.STEP_SON).contains(relationship));
            }
        }).map(new RelationTransformer()).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankBranchNames/{bankCode}")
    @ResponseBody
    public List<Map<String, Object>> getAllBankBranchNames(@PathVariable("bankCode") String bankCode) {
        return masterFinder.getAllBankBranch(bankCode);
    }

    private class RelationTransformer implements Function<Relationship, Map<String,Object>> {
        public Map<String, Object> apply(Relationship relationship) {
            Map<String, Object> relationMap = Maps.newLinkedHashMap();
            relationMap.put("relationCode", relationship.name());
            relationMap.put("description", relationship.description);
            return relationMap;
        }
    }

}
