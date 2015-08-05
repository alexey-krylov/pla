package com.pla.individuallife.policy.presentation.controller;

import com.pla.grouphealth.policy.presentation.dto.SearchGHPolicyDto;
import com.pla.individuallife.policy.service.ILPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Admin on 8/4/2015.
 */
@Controller
@RequestMapping(value = "/individuallife/policy")
public class ILPolicyController {

    private ILPolicyService ilPolicyService;
    @Autowired
    public ILPolicyController(ILPolicyService ilPolicyService) {
        this.ilPolicyService = ilPolicyService;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/searchPolicy");
        modelAndView.addObject("searchResult", ilPolicyService.findAllPolicy());
        modelAndView.addObject("searchCriteria", new SearchGHPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/viewPolicy");
        modelAndView.addObject("policyDetail", ilPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }



}
