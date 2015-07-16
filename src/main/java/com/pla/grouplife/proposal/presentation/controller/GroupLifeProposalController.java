package com.pla.grouplife.proposal.presentation.controller;

import com.google.common.collect.Lists;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouphealth.proposal.presentation.dto.SearchGHProposalDto;
import com.pla.grouplife.proposal.application.command.*;
import com.pla.grouplife.proposal.application.service.GLProposalService;
import com.pla.grouplife.proposal.presentation.dto.GLProposalApproverCommentDto;
import com.pla.grouplife.proposal.presentation.dto.GLProposalDto;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.proposal.presentation.dto.SearchGLProposalDto;
import com.pla.grouplife.proposal.query.GLProposalFinder;
import com.pla.grouplife.sharedresource.dto.AgentDetailDto;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.grouplife.sharedresource.model.vo.GLProposalStatus;
import com.pla.publishedlanguage.contract.IClientProvider;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Samir on 6/24/2015.
 */
@Controller
@RequestMapping(value = "/grouplife/proposal", consumes = MediaType.ALL_VALUE)
public class GroupLifeProposalController {

    private CommandGateway commandGateway;

    private GLProposalService glProposalService;

    private IClientProvider clientProvider;

    private GLProposalFinder glProposalFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    public GroupLifeProposalController(CommandGateway commandGateway, GLProposalService glProposalService, IClientProvider clientProvider, GLProposalFinder glProposalFinder) {
        this.commandGateway = commandGateway;
        this.glProposalService = glProposalService;
        this.clientProvider = clientProvider;
        this.glProposalFinder = glProposalFinder;
    }

    @RequestMapping(value = "/opengrouplifeproposal/{quotationId}", method = RequestMethod.GET)
    public ResponseEntity createProposal(@PathVariable("quotationId") String quotationId, HttpServletRequest request) {
        if (glProposalService.hasProposalForQuotation(quotationId)) {
            return new ResponseEntity(Result.failure("Proposal Already Exists..Do you want to override the same?"), HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            UserDetails userDetails = getLoggedInUserDetail(request);
            GLQuotationToProposalCommand glQuotationToProposalCommand = new GLQuotationToProposalCommand(quotationId, userDetails);
            commandGateway.sendAndWait(glQuotationToProposalCommand);
        }
        return new ResponseEntity(Result.success("Proposal successfully created"), HttpStatus.OK);
    }


    @RequestMapping(value = "/searchquotation", method = RequestMethod.GET)
    public ModelAndView searchQuotation(@RequestParam("quotationNumber") String quotationNumber) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/proposal/searchQuotation");
        try {
            modelAndView.addObject("searchResult", glProposalService.searchGeneratedQuotation(quotationNumber));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", quotationNumber);
        return modelAndView;
    }

    @RequestMapping(value = "/editProposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit proposal page")
    public ModelAndView gotoCreateProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/proposal/createProposal");
        return modelAndView;
    }

    @RequestMapping(value = "/forcecreateproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To create proposal forcefully if proposal already exists for the same quotation number")
    public ResponseEntity gotoCreateProposal(@RequestParam("quotationId") String quotationId, HttpServletRequest request) {
        String proposalId = commandGateway.sendAndWait(new GLQuotationToProposalCommand(quotationId, getLoggedInUserDetail(request)));
        return new ResponseEntity(Result.success("", proposalId), HttpStatus.OK);
    }

    @RequestMapping(value = "/opensearchquotation", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open search quotation page")
    public ModelAndView openSearchQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/proposal/searchQuotation");
        return modelAndView;
    }


    @RequestMapping(value = "/getproposerdetailfromclient/{proposerCode}/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public ProposerDto getProposerDetailFromClientRepository(@PathVariable("proposerCode") String proposerCode, @PathVariable("proposalId") String proposalId) {
        ClientDetailDto clientDetailDto = clientProvider.getClientDetail(proposerCode);
        ProposerDto proposerDto = new ProposerDto();
        if (clientDetailDto != null) {
            proposerDto.setProposerName(clientDetailDto.getClientName());
            proposerDto.setAddressLine1(clientDetailDto.getAddress1());
            proposerDto.setAddressLine2(clientDetailDto.getAddress2());
            proposerDto.setPostalCode(clientDetailDto.getPostalCode());
            proposerDto.setContactPersonEmail(clientDetailDto.getEmailAddress());
            proposerDto.setTown(clientDetailDto.getTown());
            proposerDto.setProvince(clientDetailDto.getProvince());
            proposerDto.setProposerCode(proposerCode);
        } else {
            proposerDto = getProposerDetail(proposalId);
            proposerDto.setProposerCode(proposerCode);
        }
        return proposerDto;
    }

    @RequestMapping(value = "/getagentdetailfromproposal/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("proposalId") String proposalId) {
        return glProposalService.getAgentDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/listgrouplifeproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open search proposal page")
    public ModelAndView listProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/proposal/viewProposal");
        modelAndView.addObject("searchCriteria", new SearchGLProposalDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchproposal", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "To search proposal")
    public ModelAndView searchProposal(SearchGLProposalDto searchGLProposalDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewProposal");
        try {
            modelAndView.addObject("searchResult", glProposalService.searchProposal(searchGLProposalDto, new String[]{"DRAFT", "PENDING_ACCEPTANCE", "RETURNED"}));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", searchGLProposalDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getsubmittedproposals", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "To search submitted proposal for approver approval")
    public List<GLProposalDto> findSubmittedProposal(SearchGLProposalDto searchGLProposalDto) {
        List<GLProposalDto> submittedProposals = glProposalService.searchProposal(searchGLProposalDto, new String[]{"PENDING_ACCEPTANCE"});
        return submittedProposals;
    }

    @RequestMapping(value = "/opensearchproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open search proposal page")
    public ModelAndView openSearchProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/proposal/viewProposal");
        modelAndView.addObject("searchCriteria", new SearchGHProposalDto());
        return modelAndView;
    }

    @RequestMapping(value = "/updatewithagentdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithAgentId(@RequestBody UpdateGLProposalWithAgentCommand updateGLProposalWithAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update proposal agent data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLProposalWithAgentCommand.setUserDetails(getLoggedInUserDetail(request));
            String proposalId = commandGateway.sendAndWait(updateGLProposalWithAgentCommand);
            return Result.success("Agent detail updated successfully", proposalId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProposalWithProposerDetail(@RequestBody UpdateGLProposalWithProposerCommand updateGLProposalWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update proposal proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLProposalWithProposerCommand.setUserDetails(getLoggedInUserDetail(request));
            String proposalId = commandGateway.sendAndWait(updateGLProposalWithProposerCommand);
            return Result.success("Proposer detail updated successfully", proposalId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/downloadplandetail/{proposalId}", method = RequestMethod.GET)
    public void downloadPlanDetail(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "planReadyReckoner.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(glProposalService.getPlanReadyReckoner(proposalId));
        outputStream.flush();
        outputStream.close();
    }


    @RequestMapping(value = "/downloadinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "insuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = glProposalService.getInsuredTemplateExcel(proposalId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
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

    @RequestMapping(value = "/downloaderrorinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GLInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(proposalId);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }


    @RequestMapping(value = "/uploadinsureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(@RequestBody UploadInsuredDetailDto uploadInsuredDetailDto, HttpServletRequest request) throws IOException {
        MultipartFile file = uploadInsuredDetailDto.getFile();
        if (!("application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = glProposalService.isValidInsuredTemplate(uploadInsuredDetailDto.getProposalId(), insuredTemplateWorkbook, uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getProposalId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Insured template is not valid.Please download to check the errors");
            }
            List<InsuredDto> insuredDtos = glProposalService.transformToInsuredDto(insuredTemplateWorkbook, uploadInsuredDetailDto.getProposalId(), uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            String quotationId = commandGateway.sendAndWait(new UpdateGLProposalWithInsuredCommand(uploadInsuredDetailDto.getProposalId(), insuredDtos, getLoggedInUserDetail(request)));
            return Result.success("Insured detail uploaded successfully", quotationId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getpremiumdetail/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public PremiumDetailDto getPremiumDetail(@PathVariable("proposalId") String proposalId) {
        return glProposalService.getPremiumDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public Result reCalculatePremium(@RequestBody GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand, HttpServletRequest request) {
        PremiumDetailDto premiumDetailDto = null;
        try {
            glRecalculatedInsuredPremiumCommand.setUserDetails(getLoggedInUserDetail(request));
            premiumDetailDto = glProposalService.recalculatePremium(glRecalculatedInsuredPremiumCommand);
            return Result.success("Premium recalculated successfully", premiumDetailDto);
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/savepremiumdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result savePremiumDetail(@RequestBody UpdateGLProposalWithPremiumDetailCommand updateGLProposalWithPremiumDetailCommand, HttpServletRequest request) {
        try {
            updateGLProposalWithPremiumDetailCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(updateGLProposalWithPremiumDetailCommand);
            return Result.success("Premium detail saved successfully");
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/getproposerdetail/{proposalId}")
    @ResponseBody
    public ProposerDto getProposerDetail(@PathVariable("proposalId") String proposalId) {
        return glProposalService.getProposerDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To submit proposal for approval")
    public ResponseEntity submitProposal(@RequestBody SubmitGLProposalCommand submitGLProposalCommand, HttpServletRequest request) {
        try {
            submitGLProposalCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(submitGLProposalCommand);
            return new ResponseEntity(Result.success("Proposal submitted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To approve proposal")
    public ResponseEntity approveProposal(@RequestBody GLProposalApprovalCommand glProposalApprovalCommand, HttpServletRequest request) {
        try {
            glProposalApprovalCommand.setUserDetails(getLoggedInUserDetail(request));
            glProposalApprovalCommand.setStatus(GLProposalStatus.APPROVED);
            commandGateway.sendAndWait(glProposalApprovalCommand);
            return new ResponseEntity(Result.success("Proposal approved successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/return", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To return/reject proposal")
    public ResponseEntity returnProposal(@RequestBody GLProposalApprovalCommand glProposalApprovalCommand, HttpServletRequest request) {
        try {
            glProposalApprovalCommand.setUserDetails(getLoggedInUserDetail(request));
            glProposalApprovalCommand.setStatus(GLProposalStatus.RETURNED);
            commandGateway.sendAndWait(glProposalApprovalCommand);
            return new ResponseEntity(Result.success("Proposal returned successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/getapprovercomments", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list approval comments")
    public List<GLProposalApproverCommentDto> findApproverComments() {
        return glProposalService.findApproverComments();
    }

    @RequestMapping(value = "/getmandatorydocuments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GLProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("proposalId") String proposalId) {
        List<GLProposalMandatoryDocumentDto> glProposalMandatoryDocumentDtos = glProposalService.findMandatoryDocuments(proposalId);
        return glProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/getproposalnumber/{proposalId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Proposal number for a given proposal ID")
    @ResponseBody
    public Result getProposalNumber(@PathVariable("proposalId") String proposalId) {
        Map proposalMap = glProposalFinder.getProposalById(new ProposalId(proposalId));
        return Result.success("Proposal number ", proposalMap.get("proposalNumber") != null ? ((ProposalNumber) proposalMap.get("proposalNumber")).getProposalNumber() : "");
    }

    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadMandatoryDocument(GLProposalDocumentCommand glProposalDocumentCommand, HttpServletRequest request) {
        glProposalDocumentCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.sendAndWait(glProposalDocumentCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Documents uploaded successfully"), HttpStatus.OK);
    }
}
