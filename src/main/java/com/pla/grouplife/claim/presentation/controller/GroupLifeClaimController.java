package com.pla.grouplife.claim.presentation.controller;

import com.pla.core.query.MasterFinder;
import com.pla.grouplife.claim.application.command.GLClaimIntimationCommand;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.presentation.dto.AssuredSearchDto;
import com.pla.grouplife.claim.presentation.dto.ClaimIntimationDetailDto;
import com.pla.grouplife.claim.presentation.dto.GLInsuredDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.sharedkernel.domain.model.ClaimType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by Mirror on 8/6/2015.
 */

@Controller
@RequestMapping(value = "/grouplife/claim")
public class GroupLifeClaimController {

    @Autowired
    private GLClaimService glClaimService;

    @Autowired
    private MasterFinder masterFinder;

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
    public ModelAndView searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/claim/searchPolicy");
        modelAndView.addObject("searchResult", glClaimService.searchPolicy(searchGLPolicyDto));
        modelAndView.addObject("searchCriteria", searchGLPolicyDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getrelationshipandcategory/{policyId}",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getConfiguredRelationshipAndCategory(@PathVariable("policyId") String policyId){
        return glClaimService.getConfiguredRelationShipAndCategory(policyId);
    }


    @RequestMapping(value = "/assuredsearch",method = RequestMethod.POST)
    @ResponseBody
    public List<GLInsuredDetailDto> assuredSearch(@RequestBody AssuredSearchDto assuredSearchDto){
        return glClaimService.assuredSearch(assuredSearchDto);
    }


    @RequestMapping(value = "/claimintimationdetail/{policyNumber}",method = RequestMethod.GET)
    @ResponseBody
    public List<ClaimIntimationDetailDto> getClaimIntimationDetail(@PathVariable("policyNumber")String policyNumber){
        return glClaimService.getClaimIntimationDetail(policyNumber);
    }


    @RequestMapping(value = "/getclaimtype",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getClaimType(){
        return ClaimType.getAllClaimType();
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


}
