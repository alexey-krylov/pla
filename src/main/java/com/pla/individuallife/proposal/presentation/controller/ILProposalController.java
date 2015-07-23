package com.pla.individuallife.proposal.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.core.query.AgentFinder;
import com.pla.core.query.MasterFinder;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.proposal.application.command.*;
import com.pla.individuallife.proposal.domain.model.ILProposalStatus;
import com.pla.individuallife.proposal.presentation.dto.*;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.quotation.application.service.ILQuotationAppService;
import com.pla.individuallife.quotation.presentation.dto.ILSearchQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.wordnik.swagger.annotations.ApiOperation;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.GatewayProxyFactory;
import org.bson.types.ObjectId;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by pradyumna on 21-05-2015.
 */
@Controller
@RequestMapping(value = "/individuallife/proposal")
public class ILProposalController {

    private final ILProposalCommandGateway proposalCommandGateway;
    private final PlanFinder planFinder;
    @Autowired
    private MasterFinder masterFinder;
    @Autowired
    private AgentFinder agentFinder;
    @Autowired
    private ILProposalFinder proposalFinder;
    @Autowired
    private ILQuotationFinder ilQuotationFinder;

    @Autowired
    private ILQuotationAppService ilQuotationService;

    @Autowired
    public ILProposalController(CommandBus commandBus, PlanFinder planFinder) {
        this.planFinder = planFinder;
        GatewayProxyFactory factory = new GatewayProxyFactory(commandBus);
        proposalCommandGateway = factory.createGateway(ILProposalCommandGateway.class);
    }

    @ResponseBody
    @RequestMapping(value = "/create")
    public ResponseEntity<Map> createProposal(@RequestBody ILCreateProposalCommand createProposalCommand, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = null;

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            proposalId = new ObjectId().toString();
            UserDetails userDetails = getLoggedInUserDetail(request);
            createProposalCommand.setUserDetails(userDetails);
            createProposalCommand.setProposalId(proposalId);
            proposalCommandGateway.createProposal(createProposalCommand);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map map = new HashMap<>();
        map.put("message", "Proposal got created successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateproposedassuredandagent")
    public ResponseEntity<Map> updateProposedAssuredAndAgents(@RequestBody ILUpdateProposalWithProposedAssuredCommand cmd, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = cmd.getProposalId();;

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updateProposedAssuredAndAgents(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map map = new HashMap<>();
        map.put("message", "Proposal updated with ProposedAssured and Agent Details successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateproposer")
    public ResponseEntity<Map> updateWithProposerDetails(@RequestBody ILProposalUpdateWithProposerCommand cmd, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = cmd.getProposalId();
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updateWithProposer(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map map = new HashMap<>();
        map.put("message", "Proposal updated with Proposer Details successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateplan")
    public ResponseEntity<Map> updateWithPlanDetails(@RequestBody ILProposalUpdateWithPlanAndBeneficiariesCommand cmd, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = cmd.getProposalId();
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updateWithPlandetail(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map map = new HashMap<>();
        map.put("message", "Proposal updated with Plan and Beneficiary Details successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "/updatecompulsoryhealthstatement", method = RequestMethod.POST)
    public ResponseEntity<Map> updateCompulsoryHealthStatement(
            @RequestBody ILProposalUpdateCompulsoryHealthStatementCommand cmd,
            HttpServletRequest request,
            BindingResult bindingResult) {
        String proposalId = cmd.getProposalId();
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updateCompulsoryHealthStatement(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<QuestionDto> list = cmd.getCompulsoryHealthDetails();
        Map map = new HashMap<>();
        map.put("message", "Proposal updated with Compulsory Health Statement Details successfully");
        map.put("proposalId", proposalId);
        map.put("questions", list);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updategeneraldetails", method = RequestMethod.POST)
    public ResponseEntity<Map> updateGeneralDetails(
            @RequestBody ILProposalUpdateGeneralDetailsCommand cmd,
            HttpServletRequest request,
            BindingResult bindingResult) {
        String proposalId = cmd.getProposalId();
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updateGeneralDetails(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map map = new HashMap<>();
        map.put("message", "Proposal updated with General Details successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateadditionaldetails", method = RequestMethod.POST)
    public ResponseEntity<Map> updateAdditionalDetails(
            @RequestBody ILProposalUpdateAdditionalDetailsCommand cmd,
            HttpServletRequest request,
            BindingResult bindingResult) {
        String proposalId = cmd.getProposalId();
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updateAdditionalDetails(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map map = new HashMap<>();
        map.put("message", "Proposal updated with Additional Details successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updatefamily", method = RequestMethod.POST)
    public ResponseEntity updateFamilyPersonalDetails(@RequestBody ILProposalUpdateFamilyPersonalDetailsCommand cmd, HttpServletRequest request,
                                                      BindingResult bindingResult) {

        String proposalId = cmd.getProposalId();
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }

        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updateFamilyPersonal(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map map = new HashMap<>();
        map.put("message", "Proposal updated with Family and Personal Details successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updatepremiumpaymentdetails", method = RequestMethod.POST)
    public ResponseEntity updatePremiumPaymentDetails(@RequestBody ILProposalUpdatePremiumPaymentDetailsCommand cmd, HttpServletRequest request,
                                                      BindingResult bindingResult) {

        String proposalId = cmd.getProposalId();
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }

        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.updatePremiumPaymentDetails(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map map = new HashMap<>();
        map.put("message", "Proposal updated with Premium Payment Details successfully");
        map.put("proposalId", proposalId);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadMandatoryDocument(ILProposalDocumentCommand cmd, HttpServletRequest request) {
        cmd.setUserDetails(getLoggedInUserDetail(request));
        try {
            String proposalId = cmd.getProposalId();
            proposalCommandGateway.uploadMandatoryDocument(cmd);
            Map map = new HashMap<>();
            map.put("message", "Document uploaded successfully");
            map.put("proposalId", proposalId);
            return new ResponseEntity(map, HttpStatus.OK);
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
            String proposalId = cmd.getProposalId();
            cmd.setUserDetails(getLoggedInUserDetail(request));
            proposalCommandGateway.submitProposal(cmd);
            Map map = new HashMap<>();
            map.put("message", "Proposal submitted successfully");
            map.put("proposalId", proposalId);
            return new ResponseEntity(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To approve proposal")
    public ResponseEntity approveProposal(@RequestBody ILProposalApprovalCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            cmd.setStatus(ILProposalStatus.APPROVED);
            proposalCommandGateway.approveProposal(cmd);
            Map map = new HashMap<>();
            map.put("message", "Proposal approved successfully");
            map.put("proposalId", cmd.getProposalId());
            return new ResponseEntity(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/return", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To return/reject proposal")
    public ResponseEntity returnProposal(@RequestBody ILProposalApprovalCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            cmd.setStatus(ILProposalStatus.RETURNED);
            proposalCommandGateway.returnProposal(cmd);
            Map map = new HashMap<>();
            map.put("message", "Proposal returned successfully");
            map.put("proposalId", cmd.getProposalId());
            return new ResponseEntity(map, HttpStatus.OK);
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
        modelAndView.setViewName("pla/individuallife/proposal/searchquotationforilproposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView searchProposal(ILSearchProposalDto ilSearchProposalDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/proposal/index");
        try {
            modelAndView.addObject("searchResult", proposalFinder.searchProposal(ilSearchProposalDto, new String[]{"DRAFT", "SUBMITTED", "PENDING_ACCEPTANCE"}));
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
        modelAndView.setViewName("pla/individuallife/proposal/index");
        try {
            modelAndView.addObject("searchResult", proposalFinder.searchProposalToApprove(dto, new String[]{"PENDING_ACCEPTANCE"}));
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
        modelAndView.setViewName("pla/individualLife/proposal/index");
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


    @RequestMapping(method = RequestMethod.GET, value = "getproposal/{proposalId}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit proposal screen.")
    @ResponseBody
    public ILProposalDto getProposalById(@PathVariable("proposalId") String proposalId) {
        ILProposalDto dto = proposalFinder.getProposalById(proposalId);
        checkArgument(dto != null, "Proposal not found");
        return dto;
    }

    @RequestMapping(method = RequestMethod.GET, value = "getproposalnumber/{proposalId}")
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
        modelAndView.setViewName("pla/individuallife/proposal/searchquotationforilproposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllOccupation")
    @ResponseBody
    public List<Map<String, Object>> getAllOccupationClassification() {
        List<Map<String, Object>> occupationClassList = masterFinder.getAllOccupationClassification();
        return occupationClassList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllIndividualLifePlans")
    public List<Map<String, Object>> getAllIndividualLifePlans() {
        List<Map<String, Object>> planList = masterFinder.getAllPlanForIndividualLife();
        return planList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankNames")
    @ResponseBody
    public List<Map<String, Object>> getAllBankNames() {
        List<Map<String, Object>> bankList = masterFinder.getAllBank();
        return bankList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankBranchNames/{bankCode}")
    @ResponseBody
    public List<Map<String, Object>> getAllBankBranchNames(@PathVariable("bankCode") String bankCode) {
        List<Map<String, Object>> bankBranchList = masterFinder.getAllBankBranch(bankCode);
        return bankBranchList;
    }


    @RequestMapping(method = RequestMethod.GET, value = "getridersforplan/{planId}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit quotation screen.")
    @ResponseBody
    public List<RiderDetailDto> getRidersForPlan(@PathVariable("planId") String planId) {
        List<Map<String, Object>> optionalCoverages = proposalFinder.findAllOptionalCoverages(planId);
        List<RiderDetailDto> riderDetails = new ArrayList<>();
        for (Map<String, Object> m : optionalCoverages) {
            RiderDetailDto dto = new RiderDetailDto();
            dto.setCoverageName(m.get("coverage_name").toString());
            dto.setCoverageId(m.get("coverage_id").toString());
            riderDetails.add(dto);
        }
        return riderDetails;
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
    public List<Map<String, Object>> searchPlan(@PathVariable("proposalId") String proposalId, @RequestParam("q") String q) {
        List<Map<String, Object>> planList = proposalFinder.getPlans(proposalId);
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

    @RequestMapping(value = "/getmandatorydocuments/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("proposalId") String proposalId) {
        List<ILProposalMandatoryDocumentDto> ilProposalMandatoryDocumentDtos = proposalFinder.findMandatoryDocuments(proposalId);
        return ilProposalMandatoryDocumentDtos;
    }
}
