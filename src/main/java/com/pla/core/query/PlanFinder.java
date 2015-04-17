/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.BasicDBObject;
import com.pla.core.domain.model.plan.Plan;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.identifier.PlanId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * @author: Nischitha
 * @since 1.0 05/03/2015
 */
@Service
public class PlanFinder {


    private MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper;
    private MandatoryDocumentFinder mandatoryDocumentFinder;

    @Autowired
    public PlanFinder(MongoTemplate mongoTemplate,MandatoryDocumentFinder mandatoryDocumentFinder) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        this.mongoTemplate = mongoTemplate;
        this.mandatoryDocumentFinder = mandatoryDocumentFinder;
    }

    public List<Plan> findAllPlanForThymeleaf() {
        List<Plan> allPlans = mongoTemplate.findAll(Plan.class, "PLAN");
        return allPlans;
    }


    public List<Map> findAllPlan() {
        List<Plan> allPlans = mongoTemplate.findAll(Plan.class, "PLAN");
        List<Map> planList = new ArrayList<Map>();
        for (Plan p : allPlans) {
            Map plan = objectMapper.convertValue(p, Map.class);
            planList.add(plan);
        }
        return planList;
    }

    public Map findPlanByPlanId(PlanId planId) {
        BasicDBObject query = new BasicDBObject();
        query.put("planId", planId);
        Plan _plan = mongoTemplate.findOne(new BasicQuery(query), Plan.class, "PLAN");
        Map plan = objectMapper.convertValue(_plan, Map.class);
        covertSumAssuredToTags((Map) plan.get("sumAssured"));

        Map policyTerm = (Map) plan.get("policyTerm");
        convertTermToTags(policyTerm);
        Map premiumTerm = (Map) plan.get("premiumTerm");
        convertTermToTags(premiumTerm);
        List<Map> coverageMaps = (List<Map>) plan.get("coverages");

        for (Map coverageMap : coverageMaps) {
            covertSumAssuredToTags((Map) coverageMap.get("coverageSumAssured"));
            Map coverageTerm = (Map) coverageMap.get("coverageTerm");
            convertTermToTags(coverageTerm);
        }
        return plan;
    }


    private void covertSumAssuredToTags(Map sumAssured) {
        List values = new LinkedList();
        for (Double val : (List<Double>) sumAssured.get("sumAssuredValue")) {
            Map obj = new HashMap();
            obj.put("text", val);
            values.add(obj);
        }
        sumAssured.put("sumAssuredValue", values);
    }

    private void convertTermToTags(Map term) {
        if (term == null) {
            return;
        }
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

    public Map<String,String> getPlanNameAndCoverageName(PlanId planId){
        Map<String,String> plans = Maps.newLinkedHashMap();
        Map plan = findPlanByPlanId(planId);
        if (isEmpty(plan)){
            return Maps.newLinkedHashMap();
        }
        Map planDetail =(Map)plan.get("planDetail");
        String planName  = (String) planDetail.get("planName");
        plans.put("planName",planName);
        List<Map> listCoverages  = (List) plan.get("coverages");
        for (Map coverageMap : listCoverages){
            String  coverageId = (String) coverageMap.get("coverageId");
            if (coverageId!=null){
                if (CoverageType.OPTIONAL.name().equals(coverageMap.get("coverageType"))){
                    plans.put("coverageName",mandatoryDocumentFinder.getCoverageNameById(coverageId));
                }
            }
        }
        return plans;
    }
}
