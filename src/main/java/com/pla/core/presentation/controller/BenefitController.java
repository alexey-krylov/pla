package com.pla.core.presentation.controller;

import com.pla.core.application.CreateBenefitCommand;
import com.pla.core.application.InactivateBenefitCommand;
import com.pla.core.application.UpdateBenefitCommand;
import com.pla.core.application.exception.BenefitApplicationException;
import com.pla.core.query.BenefitFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;


/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Controller
@RequestMapping(value = "/core/benefit")
public class BenefitController {


    private static final Logger LOGGER = LoggerFactory.getLogger(BenefitController.class);

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

    @RequestMapping(value = "/activebenefits", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getActiveBenefit() {
        return benefitFinder.getAllActiveBenefit();
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createBenefit(@RequestBody @Valid CreateBenefitCommand createBenefitCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createBenefitCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createBenefitCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in creating benefit", e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error in creating benefit", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Benefit created successfully");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateBenefit(@RequestBody @Valid UpdateBenefitCommand updateBenefitCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in updating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            updateBenefitCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateBenefitCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in updating benefit", e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error in updating benefit", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Benefit updated successfully");
    }

    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    public
    @ResponseBody
    Result inactivateBenefit(@RequestBody @Valid InactivateBenefitCommand inactivateBenefitCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in inactivating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            inactivateBenefitCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(inactivateBenefitCommand);
        } catch (BenefitApplicationException e) {
            LOGGER.error("Error in inactivating benefit", e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error in inactivating benefit", e);
            return Result.failure("Error in inactivating benefit");
        }
        return Result.success("Benefit inactivated successfully");
    }

}
