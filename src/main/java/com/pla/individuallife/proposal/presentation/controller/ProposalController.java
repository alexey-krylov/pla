package com.pla.individuallife.proposal.presentation.controller;

import com.pla.core.query.AgentFinder;
import com.pla.core.query.MasterFinder;
import com.pla.individuallife.proposal.presentation.dto.ILSearchProposalDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by pradyumna on 21-05-2015.
 */
@Controller
@RequestMapping(value = "/individuallife/proposal")
public class ProposalController {

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

    @RequestMapping(method = RequestMethod.GET, value = "/getRiders")
    public List<Map<String, Object>> getOptionalCoverages(@PathVariable("planId") String planId) {
        List<Map<String, Object>> optionalCoverages = masterFinder.getOptionalCoverages(planId);
        return optionalCoverages;
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
}
