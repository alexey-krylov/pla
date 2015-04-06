package com.pla.core.presentation.controller;

import com.mongodb.BasicDBObject;
import com.pla.core.presentation.command.CreatePlanCommand;
import com.pla.core.presentation.command.UpdatePlanCommand;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author: pradyumna
 * @since 1.0 15/03/2015
 */
@Controller
@RequestMapping(value = "/core/plan")
public class PlanSetupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenefitController.class);
    private CommandGateway commandGateway;
    private MongoTemplate mongoTemplate;

    @Autowired
    public PlanSetupController(CommandGateway commandGateway,
                               MongoTemplate springMongoTemplate) {
        this.commandGateway = commandGateway;
        this.mongoTemplate = springMongoTemplate;
    }


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView plan() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/plan/index");
        return modelAndView;
    }

    @RequestMapping(value = "/getallplan", method = RequestMethod.GET)
    @ResponseBody
    public List<Map> findAllPlan() {
        return mongoTemplate.findAll(Map.class, "PLAN");
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView gotoPlanList() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/plan/plan_list");
        modelAndView.addObject("planList", mongoTemplate.findAll(Map.class, "PLAN"));
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
        PlanId planId = new PlanId();
        command.setPlanId(planId);
        commandGateway.sendAndWait(command);
        return Result.success("Plan created successfully", planId.toString());
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    Result updatePlan(@RequestBody @Valid UpdatePlanCommand command) {
        commandGateway.sendAndWait(command);
        return Result.success("Plan updated successfully");
    }

    @RequestMapping(value = "/viewplan/{planId}", method = RequestMethod.GET)
    public String viewPlan() {
        return "pla/core/plan/plan_new";
    }

    private void covertSumAssuredToTags(Map sumAssured) {
        List values = new LinkedList();
        for (String val : (List<String>) sumAssured.get("sumAssuredValue")) {
            Map obj = new HashMap();
            obj.put("text", val);
            values.add(obj);
        }
        sumAssured.put("sumAssuredValue", values);
    }

    private void convertTermToTags(Map term) {
        List values = new LinkedList<>();
        for (Integer val : (List<Integer>) term.get("validTerms")) {
            Map obj = new HashMap();
            obj.put("text", val);
            values.add(obj);
        }
        term.put("validTerms", values);
        values = new LinkedList<>();
        for (Integer val : (List<Integer>) term.get("maturityAges")) {
            Map obj = new HashMap();
            obj.put("text", val);
            values.add(obj);
        }
        term.put("maturityAges", values);
    }

    @RequestMapping(value = "/getPlanById/{planId}", method = RequestMethod.GET)
    public
    @ResponseBody
    Map getPlanByPlanId(@PathVariable("planId") PlanId planId) {
        System.out.println("getPlanByPlanId**********");
        BasicDBObject query = new BasicDBObject();
        query.put("planId", planId);
        Map plan = mongoTemplate.findOne(new BasicQuery(query), Map.class, "PLAN");
        covertSumAssuredToTags((Map) plan.get("sumAssured"));

        Map policyTerm = (Map) plan.get("policyTerm");
        convertTermToTags(policyTerm);
        Map premiumTerm = (Map) plan.get("premiumTerm");
        convertTermToTags(premiumTerm);
        List<Map> coverageMaps = (List<Map>) plan.get("coverages");

        for (Map coverageMap : coverageMaps) {
            covertSumAssuredToTags((Map) coverageMap.get("sumAssured"));
            Map coverageTerm = (Map) coverageMap.get("coverageTerm");
            convertTermToTags(coverageTerm);
        }
        return plan;
    }

}
