package com.pla.core.presentation.controller;

import com.pla.core.application.UpdateBranchManagerCommand;
import com.pla.core.application.exception.BranchApplicationException;
import com.pla.core.application.service.BranchService;
import com.pla.core.query.BranchFinder;
import com.pla.publishedlanguage.contract.ISMEGateway;
import com.pla.publishedlanguage.domain.model.EmployeeDto;
import org.nthdimenzion.common.AppConstants;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core/branch", consumes = MediaType.ALL_VALUE)


public class BranchController {


    private static final Logger LOGGER = LoggerFactory.getLogger(BranchController.class);

    private BranchService branchService;

    private BranchFinder branchFinder;

    private ISMEGateway smeGateway;

    @Autowired
    public BranchController(BranchService branchService, BranchFinder branchFinder, ISMEGateway smeGateway) {
        this.branchService = branchService;
        this.branchFinder = branchFinder;
        this.smeGateway = smeGateway;
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView viewBranches() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/branch/viewBranchManager");
        modelAndView.addObject("branchList", branchFinder.getAllBranch());
        return modelAndView;
    }

    @RequestMapping(value = "/redirecttoassignPage", method = RequestMethod.GET)
    ModelAndView redirectToAssignPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/branch/assignBranchManager");
        return modelAndView;
    }

    @RequestMapping(value = "/getbranchdetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> openAssignPage(@RequestParam(value = "branchId", required = false) String branchId) {
        return branchFinder.getBranchById(branchId);
    }

    @RequestMapping(value = "/getallbranchmanager", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmployeeDto> getAllBranchManager() {
        List<Map<String, Object>> allBranch = branchFinder.getAllBranch();
        List<EmployeeDto> allBranchManagers = smeGateway.getEmployeeDetailByDesignation(AppConstants.BRANCH_MANAGER_DESIGNATION);
        List<EmployeeDto> branchAllManagersNotAssociatedWithBranch = allBranchManagers.stream().filter(new FilterBranchManagerFromBranchPredicate(allBranch)).collect(Collectors.toList());
        branchAllManagersNotAssociatedWithBranch.add(new EmployeeDto("Unassigned", "", "", "Unassigned", "Unassigned", "Unassigned", "Unassigned", "Unassigned"));
        return branchAllManagersNotAssociatedWithBranch;
    }

    @RequestMapping(value = "/getallbranchbde", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmployeeDto> getAllBranchBDE() {
        List<Map<String, Object>> allBranch = branchFinder.getAllBranch();
        List<EmployeeDto> allBranchManagers = smeGateway.getEmployeeDetailByDesignation(AppConstants.BRANCH_BDE_DESIGNATION);
        List<EmployeeDto> branchAllBDEsNotAssociatedWithBranch = allBranchManagers.stream().filter(new FilterBranchBDEFromBranchPredicate(allBranch)).collect(Collectors.toList());
        branchAllBDEsNotAssociatedWithBranch.add(new EmployeeDto("Unassigned", "", "Unassigned", "Unassigned", "Unassigned", "Unassigned", "Unassigned", "Unassigned"));
        return branchAllBDEsNotAssociatedWithBranch;
    }

    @RequestMapping(value = "/assign", method = RequestMethod.POST)
    public
    @ResponseBody
    Result assignBranchManager(@RequestBody UpdateBranchManagerCommand updateBranchManagerCommand, BindingResult bindingResult, HttpServletRequest request) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("*****Command Received*****" + updateBranchManagerCommand);
            }
            if (updateBranchManagerCommand.isOnlyBranchManager()) {
                branchService.updateBranchManager(updateBranchManagerCommand.getBranchCode(), updateBranchManagerCommand.getBranchManagerEmployeeId(), updateBranchManagerCommand.getBranchManagerFirstName(), updateBranchManagerCommand.getBranchManagerLastName(), updateBranchManagerCommand.getBranchManagerFromDate());
            }
            if (updateBranchManagerCommand.isOnlyBde()) {
                branchService.updateBranchBDE(updateBranchManagerCommand.getBranchCode(), updateBranchManagerCommand.getBranchBDEEmployeeId(), updateBranchManagerCommand.getBranchBDEFirstName(), updateBranchManagerCommand.getBranchBDELastName(), updateBranchManagerCommand.getBranchBDEFromDate());
            }

        } catch (BranchApplicationException e) {
            LOGGER.error("Error in assigning branch manager", e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error in assigning branch manager", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Branch manager updated successfully");
    }

    private class FilterBranchManagerFromBranchPredicate implements Predicate<EmployeeDto> {

        List<Map<String, Object>> allBranchs;

        FilterBranchManagerFromBranchPredicate(List<Map<String, Object>> allBranchs) {
            this.allBranchs = allBranchs;
        }

        @Override
        public boolean test(EmployeeDto employeeDto) {
            Optional<Map<String, Object>> branchOptional = allBranchs.stream().filter(branch -> employeeDto.getEmployeeId().equals((String) branch.get("branchManager"))).findAny();
            return !branchOptional.isPresent();
        }
    }

    private class FilterBranchBDEFromBranchPredicate implements Predicate<EmployeeDto> {

        List<Map<String, Object>> allBranchs;

        FilterBranchBDEFromBranchPredicate(List<Map<String, Object>> allBranchs) {
            this.allBranchs = allBranchs;
        }

        @Override
        public boolean test(EmployeeDto employeeDto) {
            Optional<Map<String, Object>> branchOptional = allBranchs.stream().filter(branch -> employeeDto.getEmployeeId().equals((String) branch.get("branchBDE"))).findAny();
            return !branchOptional.isPresent();
        }
    }
}
