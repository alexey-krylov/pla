package com.pla.grouplife.claim.presentation.controller;

import com.pla.grouplife.claim.application.service.ClaimService;
import com.pla.grouplife.claim.presentation.dto.GLAssuredSearchDto;
import com.pla.grouplife.claim.presentation.dto.GLInsuredDetailDto;
import com.pla.grouplife.claim.presentation.dto.SearchPolicyDto;
import com.pla.grouplife.claim.query.GLClaimFinder;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by nthdimensioncompany on 21/10/2015.
 */

@Controller
@RequestMapping(value = "/grouplife/claim")


public class GLClaimController {

    @Autowired
    private ClaimService claimService;
    @Autowired
    private GLClaimFinder glClaimFinder;

    @RequestMapping(value = "/searchpolicy", method = RequestMethod.GET)
    //public ModelAndView searchPolicyGL(@RequestBody SearchPolicyDto searchPolicyDto) {
    public List< GLPolicyDetailDto > searchPolicyGL(@RequestBody SearchPolicyDto searchPolicyDto) {
      // ModelAndView modelAndView = new ModelAndView();
       // modelAndView.setViewName("pla/grouplife/claim/searchPolicy");
       // modelAndView.addObject("searchResult", claimService.searchGLPolicy(searchPolicyDto));
       // modelAndView.addObject("searchCriteria", searchPolicyDto);
        //return modelAndView;
        return claimService.searchGLPolicy(searchPolicyDto);
    }


    @RequestMapping(value = "/claimassuredsearch",method = RequestMethod.POST)
    @ResponseBody
    public List<GLInsuredDetailDto> assuredSearch(@RequestBody GLAssuredSearchDto assuredSearchDto){
        return claimService.assuredSearches(assuredSearchDto);
        //return null;
    }

}
