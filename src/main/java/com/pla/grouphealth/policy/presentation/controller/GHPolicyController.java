package com.pla.grouphealth.policy.presentation.controller;

import com.pla.grouphealth.policy.application.service.GHPolicyService;
import com.pla.grouphealth.policy.presentation.dto.PolicyDetailDto;
import com.pla.grouphealth.policy.presentation.dto.SearchGHPolicyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Samir on 7/9/2015.
 */
@Controller
@RequestMapping(value = "/grouphealth/policy")
public class GHPolicyController {

    private GHPolicyService ghPolicyService;

    @Autowired
    public GHPolicyController(GHPolicyService ghPolicyService) {
        this.ghPolicyService = ghPolicyService;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/searchPolicy");
        modelAndView.addObject("searchResult", ghPolicyService.findAllPolicy());
        modelAndView.addObject("searchCriteria", new SearchGHPolicyDto());
        return modelAndView;
    }


    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/viewPolicy");
        modelAndView.addObject("policyDetail", ghPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/getpolicydetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public PolicyDetailDto findPolicyDetail(@PathVariable("policyId") String policyId) {
        PolicyDetailDto policyDetailDto = ghPolicyService.getPolicyDetail(policyId);
        return policyDetailDto;
    }

   /* @RequestMapping(value = "/search/", method = RequestMethod.POST)
    @ResponseBody
    public List<PolicyDetailDto> searchPolicy(SearchGHPolicyDto searchGHPolicyDto) {
        List<PolicyDetailDto> policyDetailDtos = ghPolicyService.searchPolicy(searchGHPolicyDto);
        return policyDetailDtos;
    }*/
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPolicy(SearchGHPolicyDto searchGHPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/searchPolicy");
        List<PolicyDetailDto> policyDetailDtos = ghPolicyService.searchPolicy(searchGHPolicyDto);
        modelAndView.addObject("searchResult",policyDetailDtos);
        modelAndView.addObject("searchCriteria", searchGHPolicyDto);
        return modelAndView;
    }
}
