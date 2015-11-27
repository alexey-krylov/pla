package com.pla.grouphealth.proposal.presentation.controller;

import com.google.common.collect.Lists;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouphealth.proposal.application.command.*;
import com.pla.grouphealth.proposal.application.service.GHProposalService;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalDto;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouphealth.proposal.presentation.dto.ProposalApproverCommentsDto;
import com.pla.grouphealth.proposal.presentation.dto.SearchGHProposalDto;
import com.pla.grouphealth.proposal.query.GHProposalFinder;
import com.pla.grouphealth.sharedresource.dto.*;
import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import com.pla.publishedlanguage.contract.IClientProvider;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Created by Samir on 6/24/2015.
 */
@Controller
@RequestMapping(value = "/grouphealth/proposal")
public class GroupHealthProposalController {

    private CommandGateway commandGateway;

    private GHProposalService ghProposalService;

    private IClientProvider clientProvider;

    private GHProposalFinder ghProposalFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    public GroupHealthProposalController(CommandGateway commandGateway, GHProposalService ghProposalService, IClientProvider clientProvider,
                                         GHProposalFinder ghProposalFinder) {
        this.commandGateway = commandGateway;
        this.ghProposalService = ghProposalService;
        this.clientProvider = clientProvider;
        this.ghProposalFinder = ghProposalFinder;
    }

    @RequestMapping(value = "/opengrouphealthproposal/{quotationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To create proposal from quotation")
    public ResponseEntity createProposal(@PathVariable("quotationId") String quotationId) {
        /*
        * Disable OK BUTTON IN ui WHEN Agent is inactive
        * */
        if (!ghProposalService.isAgentActive(quotationId)) {
            return new ResponseEntity(Result.failure("The Agent is Inactive", Boolean.TRUE), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (ghProposalService.hasProposalForQuotation(quotationId)) {
            return new ResponseEntity(Result.failure("Proposal Already Exists..Do you want to override the same?"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String proposalId = commandGateway.sendAndWait(new GHQuotationToProposalCommand(quotationId));
        return new ResponseEntity(Result.success("", proposalId), HttpStatus.OK);
    }

    @RequestMapping(value = "/searchquotation", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Search GENERATED/SHARED quotations")
    public ModelAndView searchQuotation(@RequestParam("quotationNumber") String quotationNumber) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/searchQuotation");
        try {
            modelAndView.addObject("searchResult", ghProposalService.searchGeneratedQuotation(quotationNumber));
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
        modelAndView.setViewName("pla/grouphealth/proposal/createProposal");
        return modelAndView;
    }


    @RequestMapping(value = "/viewApprovalProposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval proposal page in view Mode")
    public ModelAndView gotoApprovalProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/createApprovalProposal");
        return modelAndView;
    }


    @RequestMapping(value = "/editProposalReturnStatus", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit proposal page")
    public ModelAndView gotoCreateProposalReturnStatus() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/createProposalReturnStatus");
        return modelAndView;
    }

    @RequestMapping(value = "/openapprovalproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval proposal page")
    public ModelAndView gotoApprovalProposalPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewApprovalProposal");
        //List<GHProposalDto> submittedProposals = ghProposalService.searchProposal(new SearchGHProposalDto(), new String[]{"PENDING_ACCEPTANCE"});
        //modelAndView.addObject("searchResult", submittedProposals);
        modelAndView.addObject("searchCriteria", new SearchGHProposalDto());
        return modelAndView;
    }

    @RequestMapping(value = "/getsubmittedproposals", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "To search submitted proposal for approver approval")
    public ModelAndView findSubmittedProposal(SearchGHProposalDto searchGHProposalDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewApprovalProposal");
        List<GHProposalDto> submittedProposals = ghProposalService.searchProposal(searchGHProposalDto, new String[]{"PENDING_ACCEPTANCE"});

        modelAndView.addObject("searchResult", submittedProposals);
        modelAndView.addObject("searchCriteria", searchGHProposalDto);
        return modelAndView;
    }


    @RequestMapping(value = "/forcecreateproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To create proposal forcefully if proposal already exists for the same quotation number")
    public ResponseEntity gotoCreateProposal(@RequestParam("quotationId") String quotationId) {
        String proposalId = commandGateway.sendAndWait(new GHQuotationToProposalCommand(quotationId));
        return new ResponseEntity(Result.success("", proposalId), HttpStatus.OK);
    }

    @RequestMapping(value = "/opensearchquotation", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open search quotation page")
    public ModelAndView openSearchQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/searchQuotation");
        return modelAndView;
    }

    @RequestMapping(value = "/getproposerdetailfromclient/{proposerCode}/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get proposer detail from client DB based on the proposer code")
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
    @ApiOperation(httpMethod = "GET", value = "Fetch agent detail for a given proposal ID")
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("proposalId") String proposalId) {
        return ghProposalService.getAgentDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/listgrouphealthproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open search proposal page")
    public ModelAndView listProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewProposal");
        modelAndView.addObject("searchCriteria", new SearchGHProposalDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchproposal", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "To search proposal")
    public ModelAndView searchProposal(SearchGHProposalDto searchGHProposalDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewProposal");
        try {
            modelAndView.addObject("searchResult", ghProposalService.searchProposal(searchGHProposalDto, new String[]{"DRAFT", "PENDING_ACCEPTANCE", "RETURNED"}));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", searchGHProposalDto);
        return modelAndView;
    }

    @RequestMapping(value = "/opensearchproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open search proposal page")
    public ModelAndView openSearchProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewProposal");
        modelAndView.addObject("searchCriteria", new SearchGHProposalDto());
        return modelAndView;
    }


    @RequestMapping(value = "/updatewithagentdetail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To update agent detail")
    public Result updateProposalWithAgentDetail(@RequestBody UpdateGHProposalWithAgentCommand updateGHProposalWithAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation agent data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGHProposalWithAgentCommand.setUserDetails(getLoggedInUserDetail(request));
            String proposalId = commandGateway.sendAndWait(updateGHProposalWithAgentCommand);
            return Result.success("Agent detail updated successfully", proposalId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To update proposer detail")
    public Result updateProposalWithProposerDetail(@RequestBody UpdateGHProposalWithProposerCommand updateGHProposalWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGHProposalWithProposerCommand.setUserDetails(getLoggedInUserDetail(request));
            String proposalId = commandGateway.sendAndWait(updateGHProposalWithProposerCommand);
            return Result.success("Proposer detail updated successfully", proposalId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/downloadplandetail/{proposalId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To download plan ready reckoner")
    public void downloadPlanDetail(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "planReadyReckoner.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ghProposalService.getPlanReadyReckoner(proposalId));
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloadinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To download insured template")
    public void downloadInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = ghProposalService.getInsuredTemplateExcel(proposalId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloaderrorinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To download error insured template after upload")
    public void downloadErrorInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(proposalId);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
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

    @RequestMapping(value = "/uploadinsureddetail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To upload insured template")
    public Result uploadInsuredDetail(UploadInsuredDetailDto uploadInsuredDetailDto, HttpServletRequest request) throws IOException {
        MultipartFile file = uploadInsuredDetailDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType())|| "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel",Boolean.FALSE);
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = ghProposalService.isValidInsuredTemplate(uploadInsuredDetailDto.getProposalId(), insuredTemplateWorkbook, uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getProposalId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Insured template is not valid.Please download to check the errors", Boolean.TRUE);
            }
            List<GHInsuredDto> insuredDtos = ghProposalService.transformToInsuredDto(insuredTemplateWorkbook, uploadInsuredDetailDto.getProposalId(), uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            String proposalId = commandGateway.sendAndWait(new UpdateGHProposalWithInsuredCommand(uploadInsuredDetailDto.getProposalId(), insuredDtos, getLoggedInUserDetail(request), uploadInsuredDetailDto.isSamePlanForAllRelation(), uploadInsuredDetailDto.isSamePlanForAllRelation()));
            return Result.success("Insured detail uploaded successfully", proposalId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage(),Boolean.FALSE);
        }
    }

    @RequestMapping(value = "/validateIfLessThanMinimumPremiumOrNoOfPersonsForGHProposal/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Boolean> validateIfLessThanMinimumPremiumOrNoOfPersonsForGHProposal(@PathVariable("proposalId") String proposalId) {
        return ghProposalService.validateIfLessThanMinimumPremiumOrNoOfPersonsForGHProposal(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/getpremiumdetail/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get premium detail")
    public GHPremiumDetailDto getPremiumDetail(@PathVariable("proposalId") String proposalId) {
        return ghProposalService.getPremiumDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To recalculate premium")
    public Result reCalculatePremium(@RequestBody GHProposalRecalculatedInsuredPremiumCommand ghProposalRecalculatedInsuredPremiumCommand, HttpServletRequest request) {
        GHPremiumDetailDto premiumDetailDto = null;
        try {
            ghProposalRecalculatedInsuredPremiumCommand.setUserDetails(getLoggedInUserDetail(request));
            premiumDetailDto = ghProposalService.recalculatePremium(ghProposalRecalculatedInsuredPremiumCommand);
            return Result.success("Premium recalculated successfully", premiumDetailDto);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/savepremiumdetail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To save premium detail")
    public Result savePremiumDetail(@RequestBody UpdateGHProposalWithPremiumDetailCommand updateGHProposalWithPremiumDetailCommand, HttpServletRequest request) {
        try {
            updateGHProposalWithPremiumDetailCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(updateGHProposalWithPremiumDetailCommand);
            return Result.success("Premium detail saved successfully");
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To submit proposal for approval")
    public ResponseEntity submitProposal(@RequestBody SubmitGHProposalCommand submitGHProposalCommand, HttpServletRequest request) {
        try {
            submitGHProposalCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(submitGHProposalCommand);
            return new ResponseEntity(Result.success("Proposal submitted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To approve proposal")
    public ResponseEntity approveProposal(@RequestBody GHProposalApprovalCommand ghProposalApprovalCommand, HttpServletRequest request) {
        try {
            ghProposalApprovalCommand.setUserDetails(getLoggedInUserDetail(request));
            ghProposalApprovalCommand.setStatus(ProposalStatus.APPROVED);
            commandGateway.sendAndWait(ghProposalApprovalCommand);
            return new ResponseEntity(Result.success("Proposal approved successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/return", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To return/reject proposal")
    public ResponseEntity returnProposal(@RequestBody GHProposalApprovalCommand ghProposalApprovalCommand, HttpServletRequest request) {
        try {
            ghProposalApprovalCommand.setUserDetails(getLoggedInUserDetail(request));
            ghProposalApprovalCommand.setStatus(ProposalStatus.RETURNED);
            commandGateway.sendAndWait(ghProposalApprovalCommand);
            return new ResponseEntity(Result.success("Proposal returned successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/getapprovercomments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list approval comments")
    public List<ProposalApproverCommentsDto> findApproverComments(@PathVariable("proposalId") String proposalId) {
        return ghProposalService.findApproverComments(proposalId);
    }

    @RequestMapping(value = "/getmandatorydocuments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public Set<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("proposalId") String proposalId) {
        Set<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = ghProposalService.findMandatoryDocuments(proposalId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/getadditionaldocuments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list additional documents which is being configured in Mandatory Document SetUp")
    public Set<GHProposalMandatoryDocumentDto> findAdditionalDocuments(@PathVariable("proposalId") String proposalId) {
        Set<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = ghProposalService.findAdditionalDocuments(proposalId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/getproposerdetail/{proposalId}")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get proposer detail from proposer")
    public ProposerDto getProposerDetail(@PathVariable("proposalId") String proposalId) {
        return ghProposalService.getProposerDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/getproposalnumber/{proposalId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Proposal number for a given proposal ID")
    @ResponseBody
    public Result getProposalNumber(@PathVariable("proposalId") String proposalId) {
        Map proposalMap = ghProposalFinder.findProposalById(proposalId);
        return Result.success("Proposal number ", proposalMap.get("proposalNumber") != null ? ((ProposalNumber) proposalMap.get("proposalNumber")).getProposalNumber() : "");
    }

    @Synchronized
    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadMandatoryDocument(GHProposalDocumentCommand ghProposalDocumentCommand, HttpServletRequest request) {
        ghProposalDocumentCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.send(ghProposalDocumentCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Documents uploaded successfully"), HttpStatus.OK);
    }

    @Synchronized
    @RequestMapping(value = "/removeGHProposalAdditionalDocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity removeGHProposalAdditionalDocument(@RequestParam String proposalId, @RequestParam String gridFsDocId, HttpServletRequest request) {
        if(UtilValidator.isEmpty(gridFsDocId) || UtilValidator.isEmpty(proposalId)){
            return new ResponseEntity(Result.failure("request parameter cannot be empty"), HttpStatus.BAD_REQUEST);
        }
        try {
            if(commandGateway.sendAndWait(new GHProposalDocumentRemoveCommand(proposalId, gridFsDocId)))
                return new ResponseEntity(Result.success("Document deleted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Document cannot be deleted"), HttpStatus.OK);
    }

}
