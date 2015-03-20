/*
 * Copyright (c) 3/19/15 9:09 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.pla.core.application.exception.BenefitApplicationException;
import com.pla.core.query.AgentFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * @author: Samir
 * @since 1.0 19/03/2015
 */
@Controller
@RequestMapping(value = "/core/agent")
public class AgentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentController.class);

    private CommandGateway commandGateway;

    private AgentFinder agentFinder;


    @Autowired
    public AgentController(CommandGateway commandGateway, AgentFinder agentFinder) {
        this.commandGateway = commandGateway;
        this.agentFinder = agentFinder;
    }

    @RequestMapping(value = "/listagent", method = RequestMethod.GET)
    public ModelAndView viewBenefits() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/viewagent");
        return modelAndView;
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public
    @ResponseBody
    Result createAgent(@RequestBody @Valid CreateAgentCommand createAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating agent", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createAgentCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createAgentCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in creating benefit", e);
            return Result.failure("Error in creating agent");
        }
        return Result.success("Agent created successfully");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public
    @ResponseBody
    Result updateAgent(@RequestBody @Valid UpdateAgentCommand updateAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in updating agent", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            updateAgentCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateAgentCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in updating agent", e);
            return Result.failure("Error in updating benefit");
        }
        return Result.success("Agent updated successfully");
    }

}
