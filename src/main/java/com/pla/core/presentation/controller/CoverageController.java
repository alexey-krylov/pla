/*
 * Copyright (c) 3/12/15 6:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Created with IntelliJ IDEA.
 * User: Tejeswar
 * Date: 3/9/15
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
package com.pla.core.presentation.controller;


import com.pla.core.application.CreateCoverageCommand;
import com.pla.core.application.InactivateCoverageCommand;
import com.pla.core.application.MarkCoverageAsUsedCommand;
import com.pla.core.application.UpdateCoverageCommand;
import com.pla.core.application.exception.BenefitApplicationException;
import com.pla.core.dto.CoverageDto;
import com.pla.core.query.CoverageFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

@Controller
@RequestMapping(value = "/core")
public class CoverageController {


    private static final Logger LOGGER = LoggerFactory.getLogger(CoverageController.class);

    private CommandGateway commandGateway;

    private CoverageFinder coverageFinder;


    @Autowired
    public CoverageController(CommandGateway commandGateway, CoverageFinder coverageFinder) {
        this.commandGateway = commandGateway;
        this.coverageFinder = coverageFinder;
    }

    @RequestMapping(value = "/coverages/view", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView viewCoverages() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/viewCoverage");
        modelAndView.addObject("listOfCoverage", coverageFinder.getAllCoverage());
        return modelAndView;
    }

    @RequestMapping(value = "/coverages/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createCoverage(@RequestBody CreateCoverageCommand createCoverageCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createCoverageCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createCoverageCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in creating coverage", e);
            return Result.failure("Error in creating coverage");
        }
        return Result.success("Coverage created successfully");
    }

    @RequestMapping(value = "/coverages/update", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateCoverage(@RequestBody UpdateCoverageCommand updateCoverageCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in updating coverage", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            updateCoverageCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateCoverageCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in updating coverage", e);
            return Result.failure("Error in updating coverage");
        }
        return Result.success("coverage updated successfully");
    }

    @RequestMapping(value = "/coverages/inactive", method = RequestMethod.POST)
    public
    @ResponseBody
    Result inactivateCoverage(@RequestBody InactivateCoverageCommand inactivateCoverageCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in inactivating coverage", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            inactivateCoverageCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(inactivateCoverageCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in inactivating coverage", e);
            return Result.failure("Error in inactivating coverage");
        } catch (Exception e) {
            LOGGER.error("Error in inactivating coverage", e);
            return Result.failure("Error in inactivating coverage");
        }
        return Result.success("Coverage inactivated successfully");
    }

    @RequestMapping(value = "/coverages/markasused", method = RequestMethod.POST)
    public
    @ResponseBody
    Result markCoverageAsUsed(@RequestBody MarkCoverageAsUsedCommand inactivateCoverageCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in inactivating coverage", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(inactivateCoverageCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in marking the coverage", e);
            return Result.failure("Error in  marking the coverage");
        } catch (Exception e) {
            LOGGER.error("Error in  marking the coverage", e);
            return Result.failure("Error in  marking the coverage");
        }
        return Result.success("Coverage marked as INUSE successfully");
    }
}
