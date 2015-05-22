package com.pla.individuallife.presentation.controller;

import com.pla.core.query.MasterFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by pradyumna on 21-05-2015.
 */
@Controller
@RequestMapping(value = "/individualLife/proposal")
public class ProposalController {

    @Autowired
    private MasterFinder masterFinder;

    /**
     * For routing of /core/plan to the index.html page under core/plan.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView proposalListPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/proposal/individualLife/createProposal/index");
        return modelAndView;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/createProposalForm")
    public ModelAndView proposalForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/proposal/individualLife/createProposal/createProposal");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllOccupation")
    public List<Map<String, Object>> getAllOccupationClassification() {
        List<Map<String, Object>> occupationClassList = masterFinder.getAllOccupationClassification();
        return occupationClassList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllIndividualLifePlans")
    public List<Map<String, Object>> getAllIndividualLifePlans() {
        List<Map<String, Object>> planList = masterFinder.getAllPlanForIndividualLife();
        return planList;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllOccupation")
    public List<Map<String, Object>> getOptionalCoverages(@PathVariable("planId") String planId) {
        List<Map<String, Object>> planList = masterFinder.getOptionalCoverages(planId);
        return planList;
    }


}
