package com.pla.individuallife.policy.presentation.controller;

import com.pla.individuallife.policy.presentation.dto.PolicyDetailDto;
import com.pla.individuallife.policy.presentation.dto.SearchILPolicyDto;
import com.pla.individuallife.policy.service.ILPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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
        modelAndView.addObject("searchCriteria", new SearchILPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
     @ResponseBody
     public ModelAndView searchPolicy(SearchILPolicyDto searchILPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/searchPolicy");
        List<PolicyDetailDto> policyDetailDtos = ilPolicyService.searchPolicy(searchILPolicyDto);
        modelAndView.addObject("searchResult", policyDetailDtos);
        modelAndView.addObject("searchCriteria", searchILPolicyDto);
        return modelAndView;
    }

    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/viewPolicy");
        modelAndView.addObject("policyDetail", ilPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/getPage/{pageName}", method = RequestMethod.GET)
    public ModelAndView proposal(@PathVariable("pageName") String pageName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/" + pageName);
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view")
    public ModelAndView viewProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/createProposal");
        return modelAndView;
    }

    /*@RequestMapping(method = RequestMethod.GET, value = "/getproposal/{proposalId}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit proposal screen.")
    @ResponseBody
    public ILProposalDto getProposalById(@PathVariable("proposalId") String proposalId) {
        ILProposalDto dto = proposalFinder.getProposalById(proposalId);
        checkArgument(dto != null, "Proposal not found");
        return dto;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getproposalnumber/{proposalId}")
    @ApiOperation(httpMethod = "GET", value = "This call to get proposal number")
    @ResponseBody
    public String getProposalNumberById(@PathVariable("proposalId") String proposalId) {
        String proposalNumber = proposalFinder.getProposalNumberById(proposalId);
        checkArgument(proposalNumber != null, "Proposal number not found");
        return proposalNumber;
    }*/


}
