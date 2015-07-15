package com.pla.grouplife.policy.presentation.controller;

import com.pla.grouplife.policy.application.service.GLPolicyService;
import com.pla.grouplife.policy.presentation.dto.GLPolicyDetailDto;
import com.pla.grouplife.policy.presentation.dto.SearchGLPolicyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Samir on 7/9/2015.
 */
@Controller
@RequestMapping(value = "/grouplife/policy")
public class GLPolicyController {

    private GLPolicyService glPolicyService;

    @Autowired
    public GLPolicyController(GLPolicyService glPolicyService) {
        this.glPolicyService = glPolicyService;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/searchPolicy");
        modelAndView.addObject("searchResult", glPolicyService.findAllPolicy());
        return modelAndView;
    }


    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/viewPolicy");
        modelAndView.addObject("policyDetail", glPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/getpolicydetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public GLPolicyDetailDto findPolicyDetail(@PathVariable("policyId") String policyId) {
        GLPolicyDetailDto policyDetailDto = glPolicyService.getPolicyDetail(policyId);
        return policyDetailDto;
    }

    @RequestMapping(value = "/search/", method = RequestMethod.POST)
    @ResponseBody
    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<GLPolicyDetailDto> policyDetailDtos = glPolicyService.searchPolicy(searchGLPolicyDto);
        return policyDetailDtos;
    }
}
