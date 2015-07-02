package com.pla.individuallife.proposal.presentation.controller;

import com.pla.core.query.AgentFinder;
import com.pla.core.query.MasterFinder;
import com.pla.individuallife.proposal.presentation.dto.ILProposalDto;
import com.pla.individuallife.proposal.presentation.dto.ILSearchProposalDto;
import com.pla.individuallife.proposal.presentation.dto.RiderDetailDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by pradyumna on 21-05-2015.
 */
@Controller
@RequestMapping(value = "/individuallife/proposal")
public class ILProposalController {

    @Autowired
    private MasterFinder masterFinder;
    @Autowired
    private AgentFinder agentFinder;
    @Autowired
    private ILProposalFinder proposalFinder;

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

    @RequestMapping(value = "/searchplan", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> searchPlan(@RequestParam("proposalId") String proposalId) {
        List<Map<String, Object>> planList = proposalFinder.getAgents(proposalId);
        return planList;
    }
}
