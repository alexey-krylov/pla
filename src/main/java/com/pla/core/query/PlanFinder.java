/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.dto.CoverageDto;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * @author: Nischitha
 * @since 1.0 05/03/2015
 */
@Service
public class PlanFinder {

    private MongoTemplate mongoTemplate;

    private ObjectMapper objectMapper;

    @Autowired
    private CoverageFinder coverageFinder;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public PlanFinder(MongoTemplate mongoTemplate) {
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
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> findAllPlanForThymeleaf() {
        List<Map<String, Object>> planList = namedParameterJdbcTemplate.queryForList("SELECT * FROM plan_coverage_benefits_assoc group by plan_code ORDER BY plan_name,launch_date", EmptySqlParameterSource.INSTANCE);
        return planList;
    }

    public List<Plan> findPlanBy(List<PlanId> planIds) {
        Criteria planCriteria = Criteria.where("planId").in(planIds);
        return mongoTemplate.find(new Query(planCriteria), Plan.class);
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

    public String getPlanName(PlanId planId) {
        Map plan = findPlanByPlanId(planId);
        if (isEmpty(plan)) {
            return "";
        }
        Map planDetail = (Map) plan.get("planDetail");
        String planName = (String) planDetail.get("planName");
        return planName;
    }

    public List<Map> getAllOptionalCoverage(PlanId planId) {
        Map plan = findPlanByPlanId(planId);
        if (isEmpty(plan)) {
            return Lists.newArrayList();
        }
        List<Map> planCoverageList = (List) plan.get("coverages");
        List<CoverageDto> coverageNameList = coverageFinder.getAllCoverage();
        planCoverageList = planCoverageList.stream().filter(new Predicate<Map>() {
            @Override
            public boolean test(Map map) {
                return CoverageType.OPTIONAL.name().equals(map.get("coverageType"));
            }
        }).map(new TransformPlanCoverageWithCoverageName(coverageNameList)).collect(Collectors.toList());
        return planCoverageList;
    }

    public List<CoverageId> getAllCoverageAssociatedWithPlan() {
        Criteria premiumCriteria = Criteria.where("coverages.coverageType").is(CoverageType.OPTIONAL);
        Query query = new Query(premiumCriteria);
        query.fields().include("coverages.coverageId.coverageId");
        List<Plan> coveragesAssociatedWithPlan = mongoTemplate.find(query, Plan.class);
        List<CoverageId> coverageList = Lists.newArrayList();
        for (Plan plan : coveragesAssociatedWithPlan) {
            PlanCoverage planCoverage = plan.getCoverages().iterator().next();
            if (Optional.ofNullable(planCoverage).isPresent())
                coverageList.add(planCoverage.getCoverageId());
        }
        return coverageList;
    }

    public String getCoverageAssociatedWithPremiumPlan(String coverageId) {
        Map<String, Object> coverageDetail = coverageFinder.getCoverageDetail(coverageId);
        return UtilValidator.isNotEmpty(coverageDetail) ? (String) coverageDetail.get("coverageName") : "";
    }

    public List<Map<String, Object>> findAllEndorsements() {
        List<Map<String, Object>> endorsementTypeList = namedParameterJdbcTemplate.queryForList("SELECT description,category FROM ENDORSEMENT_TYPE ORDER BY DESCRIPTION", EmptySqlParameterSource.INSTANCE);
        return endorsementTypeList;
    }

    private class TransformPlanCoverageWithCoverageName implements Function<Map, Map<String, Object>> {

        private List<CoverageDto> allCoverages;

        TransformPlanCoverageWithCoverageName(List<CoverageDto> allCoverages) {
            this.allCoverages = allCoverages;
        }

        @Override
        public Map<String, Object> apply(Map planCoverageMap) {
            CoverageDto coverageMap = allCoverages.stream().filter(new Predicate<CoverageDto>() {
                @Override
                public boolean test(CoverageDto coverageMap) {
                    return planCoverageMap.get("coverageId").equals(coverageMap.getCoverageId());
                }
            }).findAny().get();
            planCoverageMap.put("coverageName", coverageMap.getCoverageName());
            return planCoverageMap;
        }
    }

    public List<Map<String,Object>> getAllPlans(){
        final String FIND_ALL_PLAN_QUERY = "SELECT DISTINCT planId,planName,planCode FROM plan_coverage_benefit_assoc_view";
            return namedParameterJdbcTemplate.query(FIND_ALL_PLAN_QUERY,new ColumnMapRowMapper());
    }
}
