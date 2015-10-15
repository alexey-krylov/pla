package com.pla.grouplife.endorsement.presentation.controller;

import com.google.common.collect.Lists;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouplife.endorsement.application.command.GLCreateEndorsementCommand;
import com.pla.grouplife.endorsement.application.command.GLEndorsementCommand;
import com.pla.grouplife.endorsement.application.command.GLEndorsementDocumentCommand;
import com.pla.grouplife.endorsement.application.command.SubmitGLEndorsementCommand;
import com.pla.grouplife.endorsement.application.service.GLEndorsementService;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.endorsement.presentation.dto.SearchGLEndorsementDto;
import com.pla.grouplife.endorsement.presentation.dto.UploadInsuredDetailDto;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.policy.application.service.GLPolicyService;
import com.pla.grouplife.proposal.application.command.UpdateGLProposalWithProposerCommand;
import com.pla.grouplife.proposal.presentation.dto.GLProposalApproverCommentDto;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.sharedresource.dto.*;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.sharedkernel.domain.model.EndorsementNumber;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Samir on 8/4/2015.
 */
@Controller
@RequestMapping(value = "/grouplife/endorsement")
public class GroupLifeEndorsementController {

    @Autowired
    private GLEndorsementService glEndorsementService;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private GLEndorsementFinder glEndorsementFinder;

    @Autowired
    private GLPolicyService glPolicyService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchpolicy", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        List<GLPolicyDetailDto> policyDetailDtos = glEndorsementService.searchPolicy(searchGLPolicyDto);
        modelAndView.addObject("searchResult", policyDetailDtos);
        return modelAndView;
    }

    @RequestMapping(value = "/downloadtemplatebyendorsementtype/{endorsementType}/{endorsementId}", method = RequestMethod.GET)
    public void downloadTemplateByEndorsementType(@PathVariable("endorsementType") GLEndorsementType glEndorsementType, @PathVariable("endorsementId") String endorsementId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        String fileName = glEndorsementType.getDescription().replaceAll("[\\s]*", "");
        response.setHeader("content-disposition", "attachment; filename=" + (fileName + "_Template.xls") + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = glEndorsementService.generateEndorsementExcel(glEndorsementType, new EndorsementId(endorsementId));
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


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


    @RequestMapping(value = "/opencreateendorsementpage", method = RequestMethod.GET)
    public ResponseEntity createEndorsement(@RequestParam(value = "policyId", required = true) String policyId, @RequestParam(value = "endorsementType", required = true) GLEndorsementType endorsementType, HttpServletRequest request) {
        UserDetails userDetails = getLoggedInUserDetail(request);
        GLCreateEndorsementCommand glCreateEndorsementCommand = new GLCreateEndorsementCommand(userDetails, endorsementType, policyId);
        String endorsementId = commandGateway.sendAndWait(glCreateEndorsementCommand);
        return new ResponseEntity(Result.success("Endorsement successfully created", endorsementId), HttpStatus.OK);
    }


    @RequestMapping(value = "/editEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit proposal page")
    private ModelAndView openCreateEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/createEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/editEndorsementUpload", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit Endorsement upload page")
    private ModelAndView openCreateEndorsementUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/createEndorsementUpload");
        return modelAndView;
    }

    @RequestMapping(value = "/editEndorsementReturnStatus", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit endorsement page")
    public ModelAndView gotoCreateEndorsementReturnStatus() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/createEndorsementReturnStatus");
        return modelAndView;
    }

    @RequestMapping(value = "/editEndorsementReturnStatusUpload", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit endorsement page")
    public ModelAndView gotoCreateEndorsementReturnStatusUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/createEndorsementUploadReturnStatus");
        return modelAndView;
    }

    @RequestMapping(value = "/viewApprovalEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval endorsement page in view Mode")
    public ModelAndView gotoApprovalEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/createApprovalEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/viewUploadApprovalEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval endorsement page in view Mode")
    public ModelAndView gotoUploadApprovalEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/createUploadApprovalEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/approveEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval Endorsement page")
    private ModelAndView openApprovalEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/createApprovalEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/getendorsementnumber/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getEndorsementNumber(@PathVariable("endorsementId") String endorsementId) {
        Map endorsmentMap = glEndorsementFinder.findEndorsementById(endorsementId);
        return Result.success("Endorsement number ", endorsmentMap.get("endorsementNumber") != null ? ((EndorsementNumber) endorsmentMap.get("endorsementNumber")).getEndorsementNumber() : "");
    }

    @RequestMapping(value = "/opensearchendorsement", method = RequestMethod.GET)
    public ModelAndView openSearchEndorsementPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/searchEndorsement");
        SearchGLEndorsementDto searchGLEndorsementDto = new SearchGLEndorsementDto();
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);
        return modelAndView;
    }

    @RequestMapping(value = "/openapprovalendorsement", method = RequestMethod.GET)
    public ModelAndView gotoApprovalEndorsementPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/viewApprovalEndorsement");
        SearchGLEndorsementDto searchGLEndorsementDto = new SearchGLEndorsementDto();
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);
        return modelAndView;
    }

    @RequestMapping(value = "/searchEndorsementApprovalpolicy", method = RequestMethod.POST)
    public ModelAndView searchEndorsementPolicy(SearchGLEndorsementDto searchGLEndorsementDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/viewApprovalEndorsement");
        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchResult", glEndorsementService.searchEndorsement(searchGLEndorsementDto, new String[]{"APPROVER_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE"}));
        return modelAndView;
    }


    @RequestMapping(value = "/searchendorsement", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "To search endorsement")
    public ModelAndView searchEndorsement(SearchGLEndorsementDto searchGLEndorsementDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/endorsement/searchEndorsement");
        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchResult", glEndorsementService.searchEndorsement(searchGLEndorsementDto, new String[]{"DRAFT", "APPROVER_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE"}));
        return modelAndView;
    }


    @RequestMapping(value = "/getagentdetailfrompolicy/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "Fetch agent detail for a given Policy ID")
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("endorsementId") String endorsementId) {
        PolicyId policyId = glEndorsementService.getPolicyIdFromEndorsment(endorsementId);
        return glPolicyService.getAgentDetail(policyId);
    }

    @RequestMapping(value = "/getproposerdetail/{endorsementId}")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get proposer detail from proposer")
    public ProposerDto getProposerDetail(@PathVariable("endorsementId") String endorsementId) {
        PolicyId policyId = glEndorsementService.getPolicyIdFromEndorsment(endorsementId);
        return glPolicyService.getProposerDetail(policyId);
    }

    @RequestMapping(value = "/getpolicydetail/{endorsementId}")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get Policy detail from proposer")
    public Map<String, Object> getPolicyDetail(@PathVariable("endorsementId") String endorsementId) {
        return glEndorsementService.getPolicyDetail(endorsementId);
    }


    @RequestMapping(value = "/getmandatorydocuments/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GLProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("endorsementId") String endorsementId) {
        List<GLProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = glEndorsementService.findMandatoryDocuments(endorsementId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/getpremiumdetail/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get premium detail")
    public PremiumDetailDto getPremiumDetail(@PathVariable("endorsementId") String endorsementId) {
        PolicyId policyId = glEndorsementService.getPolicyIdFromEndorsment(endorsementId);
        return glPolicyService.getPremiumDetail(policyId);
    }


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
    public ResponseEntity uploadMandatoryDocument(GLEndorsementDocumentCommand glEndorsementDocumentCommand, HttpServletRequest request) {
        glEndorsementDocumentCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.sendAndWait(glEndorsementDocumentCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Documents uploaded successfully"), HttpStatus.OK);
    }
    //TODO implement
    @RequestMapping(value = "/uploadinsureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(UploadInsuredDetailDto uploadInsuredDetailDto, HttpServletRequest request) throws IOException {
        MultipartFile file = uploadInsuredDetailDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType()) || "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        boolean isValidExcel = glEndorsementService.isValidExcel(uploadInsuredDetailDto.getEndorsementType(), insuredTemplateWorkbook, new EndorsementId(uploadInsuredDetailDto.getEndorsementId()));
        try {
            if (!isValidExcel) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getEndorsementId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded template is not valid.Please download to check the errors");
            }
            GLEndorsementInsuredDto glEndorsementInsuredDto = glEndorsementService.parseExcel(uploadInsuredDetailDto.getEndorsementType(), insuredTemplateWorkbook, new EndorsementId(uploadInsuredDetailDto.getEndorsementId()));
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
    }

    //TODO implement
    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProposalWithProposerDetail(@RequestBody UpdateGLProposalWithProposerCommand updateGLProposalWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
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
    public List<GLProposalApproverCommentDto> findApproverComments(@PathVariable("endorsementId") String endorsementId) {
        return Lists.newArrayList();
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To submit endorsement for approval")
    public ResponseEntity submitProposal(@RequestBody SubmitGLEndorsementCommand submitGLEndorsementCommand, HttpServletRequest request) {
        try {
            submitGLEndorsementCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(submitGLEndorsementCommand);
            return new ResponseEntity(Result.success("Endorsement submitted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/downloadinsuredtemplate/{endorsementId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To download insured template")
    public void downloadInsuredTemplate(@PathVariable("endorsementId") String endorsementId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "Group_Life_InsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel =/* glEndorsementService.getInsuredTemplateExcel(policyId);*/ null;
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    @RequestMapping(value = "/getadditionaldocuments/{endorsementId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list additional documents which is being configured in Mandatory Document SetUp")
    public Set<GLProposalMandatoryDocumentDto> findAdditionalDocuments(@PathVariable("endorsementId") String endorsementId) {
        Set<GLProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = glEndorsementService.findAdditionalDocuments(endorsementId);
        return ghProposalMandatoryDocumentDtos;
    }

}
