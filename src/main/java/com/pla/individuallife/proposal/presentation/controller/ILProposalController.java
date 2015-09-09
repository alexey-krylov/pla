package com.pla.individuallife.proposal.presentation.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.query.MasterFinder;
import com.pla.individuallife.proposal.application.command.*;
import com.pla.individuallife.proposal.domain.model.ILProposalStatus;
import com.pla.individuallife.proposal.exception.ILProposalException;
import com.pla.individuallife.proposal.presentation.dto.*;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.proposal.service.ILProposalService;
import com.pla.individuallife.quotation.application.service.ILQuotationAppService;
import com.pla.individuallife.quotation.presentation.dto.ILSearchQuotationDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.bson.types.ObjectId;
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by pradyumna on 21-05-2015.
 */
@Controller
@RequestMapping(value = "/individuallife/proposal")
public class ILProposalController {

    public static ImmutableMap<ILProposalStatus,String> messageMap  = ImmutableMap.of(ILProposalStatus.APPROVED,"Proposal approved successfully",ILProposalStatus.RETURNED,"Proposal returned successfully",ILProposalStatus.DECLINED,"Proposal rejected successfully",ILProposalStatus.PENDING_DECISION,"Proposal held successfully",
            ILProposalStatus.UNDERWRITING_LEVEL_TWO,"Successfully Routed to Senior UnderWriter");

    @Autowired
    private MasterFinder masterFinder;
    @Autowired
    private ILProposalFinder proposalFinder;
    @Autowired
    private ILQuotationAppService ilQuotationService;
    @Autowired
    private ILProposalService ilProposalService;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private CommandGateway commandGateway;



    @ResponseBody
    @RequestMapping(value = "/create" , method = RequestMethod.POST)
    public ResponseEntity<Result> createProposal(@RequestBody ILCreateProposalCommand createProposalCommand, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = null;
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        if (ilProposalService.hasProposalForQuotation(createProposalCommand.getQuotationId())) {
            return new ResponseEntity(Result.failure("Proposal already exists for the selected quotation"), HttpStatus.OK);
        }
        try {
            proposalId = new ObjectId().toString();
            UserDetails userDetails = getLoggedInUserDetail(request);
            createProposalCommand.setUserDetails(userDetails);
            createProposalCommand.setProposalId(proposalId);
            proposalId =  commandGateway.sendAndWait(createProposalCommand);
        }catch (Exception e){
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Proposal got created successfully",proposalId), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateproposedassuredandagent", method = RequestMethod.POST)
    public ResponseEntity<Map> updateProposedAssuredAndAgents(@RequestBody ILUpdateProposalWithProposedAssuredCommand cmd, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = null;

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalId = commandGateway.sendAndWait(cmd);
        }catch (Exception e){
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity(Result.success("Proposal updated with ProposedAssured and Agent Details successfully",proposalId), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateproposer", method = RequestMethod.POST)
    public ResponseEntity<Map> updateWithProposerDetails(@RequestBody ILProposalUpdateWithProposerCommand cmd, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = null;
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalId =  commandGateway.sendAndWait(cmd);
        } catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Proposal updated with Proposer Details successfully",proposalId), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateplan",method = RequestMethod.POST)
    public ResponseEntity<Map> updateWithPlanDetails(@RequestBody ILProposalUpdateWithPlanAndBeneficiariesCommand cmd, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = null;
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalId =  commandGateway.sendAndWait(cmd);
        } catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity(Result.success("Proposal updated with Plan and Beneficiary Details successfully",proposalId), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updatecompulsoryhealthstatement", method = RequestMethod.POST)
    public ResponseEntity<Map> updateCompulsoryHealthStatement( @RequestBody ILProposalUpdateCompulsoryHealthStatementCommand cmd, HttpServletRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            commandGateway.sendAndWait(cmd);
        } catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
        List<QuestionDto> list = cmd.getCompulsoryHealthDetails();
        return new ResponseEntity(Result.success("Proposal updated with Compulsory Health Statement Details successfully",list), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updategeneraldetails", method = RequestMethod.POST)
    public ResponseEntity<Map> updateGeneralDetails( @RequestBody ILProposalUpdateGeneralDetailsCommand cmd,HttpServletRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        String proposalId = cmd.getProposalId();
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalId  = commandGateway.sendAndWait(cmd);
        } catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity(Result.success("Proposal updated with General Details successfully",proposalId), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateadditionaldetails", method = RequestMethod.POST)
    public ResponseEntity<Map> updateAdditionalDetails(@RequestBody ILProposalUpdateAdditionalDetailsCommand cmd,HttpServletRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        String proposalId = null;
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalId = commandGateway.sendAndWait(cmd);
        } catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity(Result.success("Proposal updated with Additional Details successfully",proposalId), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updatefamily", method = RequestMethod.POST)
    public ResponseEntity updateFamilyPersonalDetails(@RequestBody ILProposalUpdateFamilyPersonalDetailsCommand cmd, HttpServletRequest request,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        String proposalId = null;
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalId = commandGateway.sendAndWait(cmd);
        } catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity(Result.success("Proposal updated with Family and Personal Details successfully",proposalId), HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updatepremiumpaymentdetails", method = RequestMethod.POST)
    public ResponseEntity updatePremiumPaymentDetails(@RequestBody ILProposalUpdatePremiumPaymentDetailsCommand cmd, HttpServletRequest request,
                                                      BindingResult bindingResult) {
        String proposalId = null;
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure(bindingResult.getAllErrors().toString()), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalId = commandGateway.sendAndWait(cmd);
        } catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity(Result.success("Proposal updated with Premium Payment Details successfully",proposalId), HttpStatus.OK);
    }

    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadMandatoryDocument(ILProposalDocumentCommand cmd, HttpServletRequest request) {
        cmd.setUserDetails(getLoggedInUserDetail(request));
        try {
            String proposalId = commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success("Document uploaded successfully",proposalId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To submit proposal for approval")
    public ResponseEntity submitProposal(@RequestBody SubmitILProposalCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            String proposalId  = commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success("Proposal submitted successfully",proposalId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To approve proposal")
    public ResponseEntity approveProposal(@RequestBody ILProposalApprovalCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            String proposalId = commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success(messageMap.get(cmd.getStatus()),proposalId), HttpStatus.OK);
        }catch (ILProposalException e){
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/routetonextlevel", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To route to next level of a proposal")
    public ResponseEntity routeToNextLevel(@RequestBody ILProposalUnderwriterNextLevelCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            String proposalId =  commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success("Proposal rejected successfully",proposalId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/waivedocument", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To waive mandatory document by Approver")
    public ResponseEntity waiveDocument(@RequestBody WaiveMandatoryDocumentCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            String proposalId =  commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success("Mandatory Document waives successfully",proposalId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/searchQuotation", method = RequestMethod.POST)
       public ModelAndView searchQuotation(ILSearchQuotationDto searchIlDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchResult", ilQuotationService.searchQuotation(searchIlDto));
        modelAndView.addObject("searchCriteria", searchIlDto);
        modelAndView.setViewName("pla/individuallife/proposal/searchQuotationforIlProposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView searchProposal(ILSearchProposalDto ilSearchProposalDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/index");
        try {
            modelAndView.addObject("searchResult", proposalFinder.searchProposal(ilSearchProposalDto, new String[]{"DRAFT", "RETURNED", "PENDING_ACCEPTANCE", "UNDERWRITING_LEVEL_ONE", "UNDERWRITING_LEVEL_TWO"}));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", ilSearchProposalDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getsubmittedproposalsforapprover", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "To search submitted proposal for approver approval")
    public ModelAndView findSubmittedProposal(ILSearchProposalForApprovalDto dto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/viewApprovalProposal");
        try {
            modelAndView.addObject("searchResult", proposalFinder.searchProposalToApprove(dto, new String[]{"PENDING_ACCEPTANCE","UNDERWRITING_LEVEL_ONE","UNDERWRITING_LEVEL_TWO","PENDING_DECISION"}));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", dto);
        return modelAndView;
    }

    /**
     * For routing of proposal list page to the index.html page under core/plan.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView proposalListPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchCriteria", new ILSearchProposalDto());
        modelAndView.setViewName("pla/individuallife/proposal/index");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/new")
    public ModelAndView newProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/proposal/individuallife/createProposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view")
    public ModelAndView viewProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/proposal/individuallife/createProposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getproposal/{proposalId}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit proposal screen.")
    @ResponseBody
    public  ILProposalDto  getProposalById(@PathVariable("proposalId") String proposalId) {
        ILProposalDto dto = proposalFinder.getProposalById(proposalId);
        checkArgument(dto != null, "Proposal not found");
        return dto;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getproposalnumber/{proposalId}")
    @ApiOperation(httpMethod = "GET", value = "This call to get proposal number")
    @ResponseBody
    public String getProposalNumberById(@PathVariable("proposalId") String proposalId) {
        String proposalNumber = proposalFinder.getProposalNumberById(proposalId);
        checkArgument(proposalNumber != null, "Proposal number not found");
        return proposalNumber;
    }

    @RequestMapping(value = "/getPage/{pageName}", method = RequestMethod.GET)
    public ModelAndView proposal(@PathVariable("pageName") String pageName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/" + pageName);
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/edit")
    public ModelAndView proposalForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/createProposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    public ModelAndView searchQuotationForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/searchQuotationforIlProposal");
        return modelAndView;
    }

    @RequestMapping(value = "/openapprovalproposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval proposal page")
    public ModelAndView gotoApprovalProposalPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/viewApprovalProposal");
        modelAndView.addObject("searchCriteria", new ILSearchProposalDto());
        return modelAndView;
    }

    @RequestMapping(value = "/viewApprovalProposal", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval proposal page in view Mode")
    public ModelAndView gotoApprovalProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/createApprovalProposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllOccupation")
    @ResponseBody
    public List<Map<String, Object>> getAllOccupationClassification() {
        List<Map<String, Object>> occupationClassList = masterFinder.getAllOccupationClassification();
        return occupationClassList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllIndividualLifePlans")
    @ResponseBody
    public List<Map<String, Object>> getAllIndividualLifePlans() {
        List<Map<String, Object>> planList = masterFinder.getAllPlanForIndividualLife();
        return planList;
    }

    @RequestMapping(value = "/getpremiumdetail/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getPremiumDetail(@PathVariable("proposalId") String proposalId) {
        PremiumDetailDto dto = null;
        try {
            dto = proposalFinder.getPremiumDetail(proposalId);
        } catch (IllegalArgumentException iag) {
            return new ResponseEntity(Result.failure(iag.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(dto, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankNames")
    @ResponseBody
    public List<Map<String, Object>> getAllBankNames() {
        return masterFinder.getAllBank();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankBranchNames/{bankCode}")
    @ResponseBody
    public List<Map<String, Object>> getAllBankBranchNames(@PathVariable("bankCode") String bankCode) {
        return masterFinder.getAllBankBranch(bankCode);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getridersforplan/{planId}/{proposedAssuredDOB}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit quotation screen.")
    @ResponseBody
    public List<RiderDetailDto> getRidersForPlan(@PathVariable("planId") String planId,@PathVariable("proposedAssuredDOB") Integer proposedAssuredDOB) {
       return proposalFinder.findAllOptionalCoverages(planId,proposedAssuredDOB);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllEmploymentType")
    @ResponseBody
    public List<Map<String, Object>> getAllEmploymentType() {
        List<Map<String, Object>> allEmploymentTypes = masterFinder.getAllEmploymentTypes();
        return allEmploymentTypes;
    }

    @RequestMapping(value = "/getagentdetail/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getAgentDetail(@PathVariable("agentId") String agentId) {
        Map<String, Object> agentDetail = proposalFinder.getAgentById(agentId);
        checkArgument(agentDetail != null, "Agent not found");
        return agentDetail;
    }

    @RequestMapping(value = "/searchplan/{proposalId}", method = RequestMethod.GET)
     @ResponseBody
     public List<Map<String, Object>> searchPlan(@PathVariable("proposalId") String proposalId) {
        List<Map<String, Object>> planList = proposalFinder.getPlans(proposalId);
        return planList;
    }

    @RequestMapping(value = "/getmandatorydocuments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("proposalId") String proposalId) {
        return ilProposalService.findAllMandatoryDocument(proposalId);
    }

    @RequestMapping(value = "/getadditionaldocuments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list additional documents which is being configured in Mandatory Document SetUp")
    public Set<ILProposalMandatoryDocumentDto> findAdditionalDocuments(@PathVariable("proposalId") String proposalId) {
        return ilProposalService.findAdditionalDocuments(proposalId);
    }

    @RequestMapping(value = "/getapprovercomments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list approval comments")
    public List<ProposalApproverCommentsDto> findApproverComments(@PathVariable("proposalId") String proposalId) {
        return ilProposalService.findApproverComments(proposalId);
    }

    @RequestMapping(value = "/getallrelations", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list all the relations")
    public List<Map<String,Object>> getAllRelationShip() {
        return Arrays.asList(Relationship.values()).parallelStream().map(new Function<Relationship, Map<String,Object>>() {
            @Override
            public  Map<String,Object> apply(Relationship relationship) {
                Map<String,Object> relationMap = Maps.newLinkedHashMap();
                relationMap.put("relationCode",relationship.name());
                relationMap.put("description",relationship.description);
                return relationMap;
            }
        }).collect(Collectors.toList());
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
}
