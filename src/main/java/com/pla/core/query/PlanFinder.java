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
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.dto.CoverageDto;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.domain.model.EndorsementType;
import com.pla.sharedkernel.domain.model.PlanStatus;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.DateTime;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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

    public static final String findActivePlanByPlanCode = "SELECT COUNT(plan_id) FROM plan_coverage_benefit_assoc WHERE plan_code = :planCode AND plan_status = 'LAUNCHED'";

    public static final String findOptionalCoverageByPlanId = "SELECT DISTINCT c.coverage_id coverageId,c.coverage_name coverageName FROM plan_coverage_benefit_assoc p INNER JOIN coverage c ON c.coverage_id = p.coverage_id\n" +
            "WHERE p.plan_id=:planId AND p.optional='1'";

    public static final String findAllPlanByLaunchDate = "SELECT plan_id planId FROM plan_coverage_benefit_assoc WHERE plan_status = 'DRAFT' AND launch_date = :currentDate";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> findAllPlanForThymeleaf() {
        List<Map<String, Object>> planList = namedParameterJdbcTemplate.queryForList("SELECT * FROM plan_coverage_benefit_assoc WHERE withdrawal_date >= NOW() OR withdrawal_date IS NULL GROUP BY plan_code ORDER BY plan_name,launch_date", EmptySqlParameterSource.INSTANCE);
        return planList;
    }


    public List<Map<String, Object>> findAllOptionalCoverage(String planId) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("planId", planId);
        return namedParameterJdbcTemplate.query(findOptionalCoverageByPlanId, sqlParameterSource, new ColumnMapRowMapper());
    }

    public List<Plan> findPlanBy(List<PlanId> planIds) {
        Criteria planCriteria = Criteria.where("planId").in(planIds);
        return mongoTemplate.find(new Query(planCriteria), Plan.class);
    }

    public List<Map> findAllPlan() {
        Criteria planCriteria = Criteria.where("status").ne(PlanStatus.WITHDRAWN);
        Query query = new Query(planCriteria);
        List<Plan> allPlans = mongoTemplate.find(query, Plan.class);
        allPlans.sort(Comparator.comparing(e -> e.getPlanDetail().getPlanName()));
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

    public List<Map<String, Object>> getAllPlans() {
        final String FIND_ALL_PLAN_QUERY = "SELECT DISTINCT planId,planName,planCode FROM plan_coverage_benefit_assoc_view WHERE planStatus !='WITHDRAWN'";
        return namedParameterJdbcTemplate.query(FIND_ALL_PLAN_QUERY, new ColumnMapRowMapper());
    }

    public Set<String> findConfiguredEndorsementType(Set<PlanId> planIds) {
        Criteria planCriteria = Criteria.where("planId").in(planIds);
        Query query = new Query(planCriteria);
        List<Plan> plans = mongoTemplate.find(query, Plan.class);
        Set<String> endorsementTypes = Sets.newLinkedHashSet();
        plans.parallelStream().map(new Function<Plan, String>() {
            @Override
            public String apply(Plan plan) {
                Set<EndorsementType> endorsementType = plan.getPlanDetail().getEndorsementTypes();
                if (isNotEmpty(endorsementType)) {
                    endorsementType.forEach(endorsement -> {
                        endorsementTypes.add(endorsement.getDescription());
                    });
                }
                return "";
            }
        }).collect(Collectors.toSet());
        return endorsementTypes;
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

    public int findActivePlanByPlanCode(String planCode) {
        Number activePlanCount = namedParameterJdbcTemplate.queryForObject(findActivePlanByPlanCode, new MapSqlParameterSource("planCode", planCode), Number.class);
        return activePlanCount.intValue();
    }

    public Set<PlanId> getPlanByLaunchDate(){
        java.sql.Date now = new java.sql.Date(DateTime.now().toDate().getTime());
        List<Map<String,Object>> planByLaunchDate = namedParameterJdbcTemplate.query(findAllPlanByLaunchDate,new MapSqlParameterSource("currentDate",now),new ColumnMapRowMapper());
        if (isNotEmpty(planByLaunchDate)) {
            return planByLaunchDate.parallelStream().map(plan -> plan.get("planId") != null ? new PlanId((String) plan.get("planId")) : null).filter(planIds -> planIds != null).collect(Collectors.toSet());
        }
        return Collections.EMPTY_SET;
    }

    public void markPlanLaunched(){
        java.sql.Date now = new java.sql.Date(DateTime.now().toDate().getTime());
        namedParameterJdbcTemplate.execute("update plan_coverage_benefit_assoc set plan_status='" + PlanStatus.LAUNCHED + "' " +
                        "where launch_date='" + now + "'",
                new EmptySqlParameterSource(), new PreparedStatementCallback<Object>() {
                    @Override
                    public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
                        return preparedStatement.execute();
                    }
                });

    }
}
