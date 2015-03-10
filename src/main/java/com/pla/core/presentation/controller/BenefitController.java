package com.pla.core.presentation.controller;

import com.pla.core.application.CreateBenefitCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ASUS on 02-Mar-15.
 */
@Controller
@RequestMapping(value = "/core")
public class BenefitController {

    @Autowired
    private CommandGateway commandGateway;
    
    @RequestMapping(value = "/benefits/view", method = RequestMethod.GET)
    public String viewBenefits(@RequestParam(value = "page", required = false) Integer pageNumber, Model model) {
        return "pla/core/viewBenefit";
    }

    @RequestMapping(value = "/benefits/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String createBenefit(@RequestBody CreateBenefitCommand createBenefitCommand) {
        return "success";
    }

    @RequestMapping(value = "/benefits/update", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateBenefit(@RequestBody CreateBenefitCommand createBenefitCommand) {
        return "success";
    }


}
