package com.pla.core.presentation.controller;

import com.pla.core.domain.exception.PlanException;
import com.pla.core.presentation.command.CreatePlanCommand;
import com.pla.core.presentation.command.PlanCommandGateway;
import com.pla.core.presentation.command.UpdatePlanCommand;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.GatewayProxyFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
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

    private final PlanCommandGateway planCommandGateway;
    private final PlanFinder planFinder;


    @Autowired
    public PlanSetupController(CommandBus commandBus, PlanFinder planFinder) throws Exception {
        this.planFinder = planFinder;
        GatewayProxyFactory factory = new GatewayProxyFactory(commandBus);
        planCommandGateway = factory.createGateway(PlanCommandGateway.class);
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
     * Get all available endorsements.
     *
     * @return
     */
    @RequestMapping(value = "/getAllEndorsements", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEndorsements() {
        List<Map<String, Object>> endorsementTypeList = planFinder.findAllEndorsements();
        return endorsementTypeList;
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

    @RequestMapping(value = "/getcoveragebyplanid/{planId}")
    @ResponseBody
    public List<Map> getCoverageAssociatedWithPlan(@PathVariable("planId") PlanId planId) {
        return planFinder.getAllOptionalCoverage(planId);
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
        List<Map<String, Object>> planList = planFinder.findAllPlanForThymeleaf();
        modelAndView.addObject("planList", planList);
        return modelAndView;
    }

    @RequestMapping(value = "/newplan", method = RequestMethod.GET)
    public String newPlan() {
        return "pla/core/plan/plan_new";
    }

    @RequestMapping(value = "/viewplan", method = RequestMethod.GET)
    public String viewplan() {
        return "pla/core/plan/planview";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ModelAndView createPlan(@RequestBody @Valid CreatePlanCommand command, HttpServletResponse response) {
        PlanId planId = new PlanId(new ObjectId().toString());
        command.setPlanId(planId);
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        Map<String, String> viewMap = new HashMap<String, String>();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json");
        try {
            planCommandGateway.createPlan(command);
            viewMap.put("message", "Plan created successfully");
            viewMap.put("id", planId.toString());
        } catch (PlanException e) {
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            viewMap.put("message", e.getMessage());
        }
        view.setAttributesMap(viewMap);
        return new ModelAndView(view);
    }

    @RequestMapping(value = "/coverage-form.html", method = RequestMethod.GET)
    public ModelAndView coverageFormPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/plan/coverage-form");
        return modelAndView;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ModelAndView updatePlan(@RequestBody @Valid UpdatePlanCommand command, HttpServletResponse response) {
        PlanId planId = new PlanId(new ObjectId().toString());
        command.setNewPlanId(planId);
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        Map<String, String> viewMap = new HashMap<String, String>();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/json");
        try {
            planCommandGateway.updatePlan(command);
            viewMap.put("message", "Plan updated successfully");
        } catch (PlanException exception) {
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            viewMap.put("message", exception.getMessage());
        }
        view.setAttributesMap(viewMap);
        return new ModelAndView(view);
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
