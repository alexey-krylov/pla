package com.pla.core.presentation.controller;

import com.pla.core.application.CreateCommissionCommand;
import com.pla.core.application.UpdateCommissionCommand;
import com.pla.core.presentation.dto.CommissionDto;
import com.pla.core.query.CommissionFinder;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionType;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core/commission")
public class CommissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommissionController.class);

    private CommandGateway commandGateway;

    private CommissionFinder commissionFinder;

    private PlanFinder planFinder;

    private MongoTemplate mongoTemplate;

    @Autowired
    public CommissionController(CommandGateway commandGateway, CommissionFinder commissionFinder, MongoTemplate springMongoTemplate, PlanFinder planFinder) {
        this.commandGateway = commandGateway;
        this.commissionFinder = commissionFinder;
        this.mongoTemplate = springMongoTemplate;
        this.planFinder = planFinder;
    }

    @RequestMapping(value = "/list/{commissionType}", method = RequestMethod.GET)
    public ModelAndView viewCommissions(@PathVariable("commissionType") String commissionType) {
        if (commissionType.equals("Override")) {
            List<Map<String, Object>> commissions = commissionFinder.getAllCommissionByCommissionType(CommissionType.OVERRIDE);
            List<Map<String, Object>> commissionTerms = commissionFinder.getAllCommissionTerm();
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/core/commission/viewCommission");
            modelAndView.addObject("commissionList", CommissionDto.transformToCommissionDto(commissions, commissionTerms, planFinder));
            return modelAndView;
        } else {

            List<Map<String, Object>> commissions = commissionFinder.getAllCommissionByCommissionType(CommissionType.NORMAL);
            List<Map<String, Object>> commissionTerms = commissionFinder.getAllCommissionTerm();
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/core/commission/viewCommission");
            modelAndView.addObject("commissionList", CommissionDto.transformToCommissionDto(commissions, commissionTerms, planFinder));
            return modelAndView;
        }
    }

    @RequestMapping(value = "/opencreatepage/{commissiontype}", method = RequestMethod.GET)
    public ModelAndView openCreatePageCommission(@PathVariable("commissiontype") String commissionType) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/commission/createCommission");
        modelAndView.addObject("plans", planFinder.findAllPlan());
        modelAndView.addObject("designation", CommissionDesignation.values());
        if (commissionType.equals("Override")) {
            modelAndView.addObject("commissiontype", "Override");
        } else {
            modelAndView.addObject("commissiontype", "Normal");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/getcommissiondetail/{commissionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CommissionDto getCommissionDetail(@PathVariable("commissionId") String commissionId) {
        Map<String, Object> commissions = commissionFinder.getCommissionById(commissionId);
        List<Map<String, Object>> commissionTerms = commissionFinder.getCommissionTermByCommissionId(commissionId);
        CommissionDto commissionDto = CommissionDto.transformToCommissionDto(commissions, commissionTerms, planFinder);
        return commissionDto;
    }


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createCommission(@RequestBody @Valid CreateCommissionCommand createCommissionCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating commission", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createCommissionCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createCommissionCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating commission", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Commission created successfully");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateCommission(@RequestBody UpdateCommissionCommand updateCommissionCommand, BindingResult bindingResult, HttpServletRequest request) {
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            updateCommissionCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateCommissionCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating commission", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Commission updated successfully");
    }
}
