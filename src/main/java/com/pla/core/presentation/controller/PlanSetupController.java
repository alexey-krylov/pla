package com.pla.core.presentation.controller;

import com.pla.core.presentation.command.CreatePlanCommand;
import com.pla.core.query.PlanFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collections;

/**
 * @author: pradyumna
 * @since 1.0 15/03/2015
 */
@Controller
@RequestMapping(value = "/core/plan")
public class PlanSetupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenefitController.class);
    private PlanFinder planFinder;
    private CommandGateway commandGateway;

    @Autowired
    public PlanSetupController(CommandGateway commandGateway, PlanFinder planFinder) {
        this.commandGateway = commandGateway;
        this.planFinder = planFinder;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView viewBenefits() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/plan_list");
        modelAndView.addObject("planList", Collections.emptyList());
        return modelAndView;
    }

    @RequestMapping(value = "/newplan", method = RequestMethod.GET)
    public String newPlan() {
        return "pla/core/plan_new";
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void createPlan(@RequestBody @Valid CreatePlanCommand command) {
        commandGateway.sendAndWait(command);
    }

}
