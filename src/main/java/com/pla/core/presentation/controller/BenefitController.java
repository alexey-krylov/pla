package com.pla.core.presentation.controller;

import com.pla.core.application.CreateBenefitCommand;
import com.pla.core.application.InactivateBenefitCommand;
import com.pla.core.application.UpdateBenefitCommand;
import com.pla.core.query.BenefitFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
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


/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Controller
@RequestMapping(value = "/core/benefit")
public class BenefitController {

    private CommandGateway commandGateway;

    private BenefitFinder benefitFinder;

    @Autowired
    public BenefitController(CommandGateway commandGateway, BenefitFinder benefitFinder) {
        this.commandGateway = commandGateway;
        this.benefitFinder = benefitFinder;
    }

    @RequestMapping(value = "/listbenefit", method = RequestMethod.GET)
    public ModelAndView viewBenefits() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/viewBenefit");
        modelAndView.addObject("benefitList", benefitFinder.getAllBenefit());
        return modelAndView;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createBenefit(@RequestBody @Valid CreateBenefitCommand createBenefitCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.Failure("Error in creating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createBenefitCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createBenefitCommand);
        } catch (Exception e) {
            return Result.Failure("Error in creating benefit");
        }
        return Result.Success("Benefit created successfully");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateBenefit(@RequestBody @Valid UpdateBenefitCommand updateBenefitCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.Failure("Error in updating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            updateBenefitCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateBenefitCommand);
        } catch (Exception e) {
            return Result.Failure("Error in updating benefit");
        }
        return Result.Success("Benefit updated successfully");
    }

    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    public
    @ResponseBody
    Result inactivateBenefit(@RequestBody @Valid InactivateBenefitCommand inactivateBenefitCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.Failure("Error in inactivating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            inactivateBenefitCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(inactivateBenefitCommand);
        } catch (Exception e) {
            return Result.Failure("Error in inactivating benefit");
        }
        return Result.Success("Benefit inactivated successfully");
    }

    private UserDetails getLoggedInUSerDetail(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(AppConstants.loggedInUser);
        return userDetails;
    }
}
