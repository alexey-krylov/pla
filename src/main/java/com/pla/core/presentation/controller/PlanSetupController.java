package com.pla.core.presentation.controller;

import com.pla.core.presentation.command.CreatePlanCommand;
import com.pla.core.presentation.command.UpdatePlanCommand;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.bson.types.ObjectId;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling all the User Interface calls related to
 * creation/edition of Plan.
 * <p/>
 * It also have interface to handled ajax calls to return list of plans.
 *
 * @author: pradyumna
 * @since 1.0 15/03/2015
 */
@Controller
@RequestMapping(value = "/core/plan")
public class PlanSetupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlanSetupController.class);
    private final CommandGateway commandGateway;

    private final PlanFinder planFinder;


    @Autowired
    public PlanSetupController(CommandGateway commandGateway, PlanFinder planFinder) {
        this.commandGateway = commandGateway;
        this.planFinder = planFinder;
    }

    /**
     * For routing of /core/plan to the index.html page under core/plan.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView plan() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/plan/index");
        return modelAndView;
    }

    /**
     * For handling ajax call to get list of all active Plan.
     *
     * @return
     */
    @RequestMapping(value = "/getallplan", method = RequestMethod.GET)
    @ResponseBody
    public List<Map> findAllPlan() {
        return planFinder.findAllPlan();
    }

    /**
     * For routing the /core/plan/list url. The model is populated
     * with All the active plans and rendered via Thyme View Resolver.
     *
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView gotoPlanList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/plan/plan_list");
        modelAndView.addObject("planList", planFinder.findAllPlanForThymeleaf());
        return modelAndView;
    }

    @RequestMapping(value = "/newplan", method = RequestMethod.GET)
    public String newPlan() {
        return "pla/core/plan/plan_new";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    Result createPlan(@RequestBody @Valid CreatePlanCommand command) {
        PlanId planId = new PlanId(new ObjectId().toString());
        command.setPlanId(planId);
        Result operationResult = null;
        commandGateway.send(command, new CommandCallback<Object>() {
            @Override
            public void onSuccess(Object result) {

            }

            @Override
            public void onFailure(Throwable cause) {

            }
        });
        return Result.success("Plan created successfully", planId.toString());
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    Result updatePlan(@RequestBody @Valid UpdatePlanCommand command) {
        command.setNewPlanId(new PlanId(new ObjectId().toString()));
        commandGateway.sendAndWait(command);
        return Result.success("Plan updated successfully");
    }

    @RequestMapping(value = "/viewplan/{planId}", method = RequestMethod.GET)
    public String viewPlan() {
        return "pla/core/plan/plan_new";
    }


    @RequestMapping(value = "/getPlanById/{planId}", method = RequestMethod.GET)
    public
    @ResponseBody
    Map getPlanByPlanId(@PathVariable("planId") PlanId planId) {
        Map plan = planFinder.findPlanByPlanId(planId);
        return plan;
    }

}
