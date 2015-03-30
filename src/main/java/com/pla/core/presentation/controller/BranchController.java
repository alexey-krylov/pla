package com.pla.core.presentation.controller;

import com.pla.core.UpdateBranchManagerCommand;
import com.pla.core.application.exception.BranchApplicationException;
import com.pla.core.application.service.BranchService;
import com.pla.core.query.BranchFinder;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core", consumes = MediaType.ALL_VALUE)


public class BranchController {


    private static final Logger LOGGER = LoggerFactory.getLogger(BranchController.class);

    private BranchService branchService;

    private BranchFinder branchFinder;

    @Autowired
    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @RequestMapping(value = "/branch/view", method = RequestMethod.GET)
    public ModelAndView viewBranches() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/branch/viewBranch");
        modelAndView.addObject("branchList", branchFinder.getAllBranch());
        return modelAndView;
    }

    @RequestMapping(value = "/branch/redirectToAssignPage", method = RequestMethod.GET)
    public String redirectToAssignPage(@RequestParam(value = "branchId", required = false) String branchId) {
        return "pla/core/branch/assignBranch";
    }

    @RequestMapping(value = "/branch/openAssignPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> openAssignPage(@RequestParam(value = "branchId", required = false) String branchId) {
        return branchFinder.getBranchById(branchId);
    }

    @RequestMapping(value = "/branch/assign", method = RequestMethod.POST)
    public
    @ResponseBody
    Result assignBranchManager(@RequestBody UpdateBranchManagerCommand updateBranchManagerCommand, BindingResult bindingResult, HttpServletRequest request) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("*****Command Received*****" + updateBranchManagerCommand);
            }
            try {
                branchService.updateBranchManager(updateBranchManagerCommand.getBranchCode(), updateBranchManagerCommand.getBranchManagerEmployeeId(), updateBranchManagerCommand.getBranchManagerFirstName(), updateBranchManagerCommand.getBranchBDELastName(), updateBranchManagerCommand.getBranchManagerFromDate());

            } catch (BranchApplicationException e) {
                LOGGER.error("Error in assigning branch manager", e);
                return Result.failure("Error in assigning branch manager");
            }
            try {
                branchService.updateBranchBDE(updateBranchManagerCommand.getBranchCode(), updateBranchManagerCommand.getBranchBDEEmployeeId(), updateBranchManagerCommand.getBranchBDEFirstName(), updateBranchManagerCommand.getBranchBDELastName(), updateBranchManagerCommand.getBranchBDEFromDate());

            } catch (BranchApplicationException e) {
                LOGGER.error("Error in assigning branch manager", e);
                return Result.failure("Error in assigning branch bde");
            }
        } catch (Exception e) {
            LOGGER.error("Error in assigning branch manager", e);
            return Result.failure("Error in assigning branch manager");
        }
        return Result.success("Region manager updated successfully");
    }


}
