package com.pla.grouplife.claim.presentation.controller;

import com.pla.grouplife.claim.application.command.GLClaimIntimationCommand;
import org.nthdimenzion.presentation.Result;
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

    @RequestMapping(value = "/createclaimintimation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createClaimIntimation(@RequestBody GLClaimIntimationCommand glClaimIntimationCommand, BindingResult bindingResult) {
        return null;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView searchPolicy() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/claim/searchPolicy");
        return modelAndView;
    }


    @RequestMapping(value = "/openclaimintimationpage", method = RequestMethod.GET)
    public ModelAndView claimIntimation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/claim/claimIntimation");
        return modelAndView;
    }


}
