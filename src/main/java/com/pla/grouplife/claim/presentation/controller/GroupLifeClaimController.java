package com.pla.grouplife.claim.presentation.controller;

import com.pla.grouplife.claim.application.command.GLClaimIntimationCommand;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Mirror on 8/6/2015.
 */

@Controller
@RequestMapping(value = "/grouplife/claim")
public class GroupLifeClaimController {

    @Autowired
    private GLClaimService glClaimService;

    @RequestMapping(value = "/createclaimintimation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createClaimIntimation(@RequestBody GLClaimIntimationCommand glClaimIntimationCommand, BindingResult bindingResult) {
        return null;
    }


    @RequestMapping(value = "/openclaimintimationpage", method = RequestMethod.GET)
    public ModelAndView claimIntimation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/claim/claimIntimation");
        return modelAndView;
    }



    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView searchPolicy() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/claim/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/claim/searchPolicy");
        modelAndView.addObject("searchResult", glClaimService.searchPolicy(searchGLPolicyDto));
        modelAndView.addObject("searchCriteria", searchGLPolicyDto);
        return modelAndView;
    }
/*
    @RequestMapping(value = "/getclaimtype",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getClaimType(){
        return null;
    }

   */
}
