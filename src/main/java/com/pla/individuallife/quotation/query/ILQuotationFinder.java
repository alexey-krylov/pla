package com.pla.individuallife.quotation.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.repository.PlanRepository;
import com.pla.individuallife.quotation.domain.model.*;
import com.pla.individuallife.sharedresource.dto.*;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Finder
@Service
public class ILQuotationFinder {

    public static final String FIND_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId";
    public static final String FIND_QUOTATION_BY_ID_FOR_PREMIUM_WITH_RIDER_QUERY =
            " SELECT  r.`coverage_id` AS COVERAGEID, r.`cover_term` AS COVERTERM, r.`sum_assured` AS RIDER_SA, " +
                    " r.`waiver_of_premium` AS RIDER_PREMIUM_WAIVER, c.`coverage_name` AS COVERAGENAME " +
                    " FROM individual_life_quotation_rider r  INNER JOIN coverage c " +
                    " ON r.`coverage_id` = c.`coverage_id` " +
                    " WHERE r.`quotation_id` =:quotationId";

    public static final String FIND_QUOTATION_BY_ID_FOR_PREMIUM_QUERY = "SELECT quotation_id AS QUOTATIONID, plan_id AS PLANID, date_of_birth AS ASSURED_DOB,  " +
            "             gender AS ASSURED_GENDER, policy_term AS POLICYTERM, IFNULL(oc.code,'') AS ASSURED_OCCUPATION,   " +
            "             premium_payment_term AS PREMIUMPAYMENT_TERM, sum_assured AS SUMASSURED, premium_payment_type AS PREMIUMPAYMENT_TYPE, " +
            "             annual_fee annualFee,monthly_fee monthlyFee,quarterly_fee quarterlyFee,semi_annual_fee semiAnnualFee ,il_quotation_status status,version_number versionNumber ,quotation_number quotationNumber " +
            "             FROM individual_life_quotation il LEFT JOIN occupation_class oc ON il.occupation = oc.description " +
            " WHERE quotation_id =:quotationId";

    public static final String findILQuotationByQuotationNumberQuery = "SELECT * FROM individual_life_quotation WHERE quotation_number=:quotationNumber AND quotation_id !=:quotationId AND il_quotation_status=:quotationStatus";
    public static final String  findQuotationByQuotationNumberQuery = " SELECT * FROM individual_life_quotation WHERE parent_quotation_id=:parentQuotationId ";
    private static String findQuotation = "SELECT DISTINCT CONCAT(COALESCE(A.FIRST_NAME,' '), ' ', COALESCE(A.last_name,'')) AS agentName, CONCAT(COALESCE(IL.first_name,' '), ' ', " +
            "   COALESCE(surname,'')) AS proposedName,CONCAT(proposer_first_name,' ',proposer_surname) AS proposerName,  " +
            "   il_quotation_status AS quotationStatus, quotation_id AS quotation_id, quotation_number AS quotationNumber, " +
            "   generated_on AS generatedOn,version_number AS versionNumber,COALESCE(p.plan_name,'') planName FROM individual_life_quotation IL LEFT JOIN agent A   ON IL.agent_id = A.agent_id " +
            "   LEFT JOIN plan_coverage_benefit_assoc p ON p.plan_id = IL.plan_id ";
    public static final String QUOTATION_SEARCH_QUERY = findQuotation +" WHERE LOWER(IL.proposer_first_name) LIKE :proposerName OR IL.quotation_number= :quotationNumber " +
            " OR IL.proposer_nrc_number= :nrcNumber OR il.agent_id = :agentCode OR IL.il_quotation_status=:quotationStatus order by version_number desc";
    public static final String findSharedQuotationByQuotationNumberQuery = findQuotation +" WHERE  IL.quotation_number= :quotationNumber  AND IL.il_quotation_status= '"+ ILQuotationStatus.SHARED.name()+"' ORDER BY version_number desc";

    public static final String findQuotationDetailByQuotationNumberQuery = " SELECT DISTINCT annual_fee annualFee,monthly_fee monthlyFee,quarterly_fee quarterlyFee,semi_annual_fee semiAnnualFee FROM individual_life_quotation WHERE quotation_number= :quotationNumber AND  il_quotation_status IN ('GENERATED','SHARED') LIMIT 1";
    @Autowired
    private MongoTemplate mongoTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private ILQuotationRepository ilQuotationRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public Map getQuotationForPremiumById(String quotationId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_QUOTATION_BY_ID_FOR_PREMIUM_QUERY, new MapSqlParameterSource().addValue("quotationId", quotationId));
    }

    public List<Map<String, Object>> getQuotationForPremiumWithRiderById(String quotationId) {
        return namedParameterJdbcTemplate.queryForList(FIND_QUOTATION_BY_ID_FOR_PREMIUM_WITH_RIDER_QUERY,
                new MapSqlParameterSource().addValue("quotationId", quotationId));
    }

    public ILQuotationDto getQuotationById(String quotationId) {
        ILQuotation quotation = ilQuotationRepository.findOne(new QuotationId(quotationId));
        ILQuotationDto dto = new ILQuotationDto();
        ProposerDto proposerDto = new ProposerDto();
        ProposedAssuredDto proposedAssuredDto = new ProposedAssuredDto();
        PlanDetailDto planDetailDto = new PlanDetailDto();
        dto.setAgentId(quotation.getAgentId().toString());
        dto.setPlanId(quotation.getPlanDetail().getPlanId().toString());
        dto.setParentQuotationId(quotation.getQuotationARId());
        dto.setVersionNumber(quotation.getVersionNumber());
        dto.setQuotationGeneratedOn(quotation.getGeneratedOn());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setQuotationId(quotation.getQuotationId());
        dto.setQuotationStatus(quotation.getIlQuotationStatus().name());
        dto.setAssuredTheProposer(quotation.isAssuredTheProposer());
        dto.setOpportunityId(quotation.getOpportunityId()!=null?quotation.getOpportunityId().getOpportunityId():"");
        //TODO change the type in dto
//        dto.setQuotationStatus(quotation.getIlQuotationStatus());
        try {
            if (quotation.getProposer() != null) {
                BeanUtils.copyProperties(proposerDto, quotation.getProposer());
                Proposer proposer = quotation.getProposer();
                proposerDto.setNrc(proposer.getNrcNumber());
            }
            if (quotation.getProposedAssured() != null) {
                BeanUtils.copyProperties(proposedAssuredDto, quotation.getProposedAssured());
                ProposedAssured proposedAssured = quotation.getProposedAssured();
                proposedAssuredDto.setNrc(proposedAssured.getNrcNumber());
            }
            if (quotation.getPlanDetail() != null)
                BeanUtils.copyProperties(planDetailDto, quotation.getPlanDetail());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> optionalCoverages = findAllOptionalCoverages(planDetailDto.getPlanId());
        Map<String, Map<String, Object>> coverageReferenceMap = new HashMap<>();
        for (Map<String, Object> m : optionalCoverages) {
            coverageReferenceMap.put(m.get("coverage_id").toString(), m);
        }
        Set<RiderDetailDto> riderDetailDtoList = Sets.newLinkedHashSet();
        if (quotation.getRiderDetails() != null && quotation.getRiderDetails().size() > 0) {
            for (RiderDetail rider : quotation.getRiderDetails()) {
                RiderDetailDto riderDetailDto = new RiderDetailDto();
                Map m = coverageReferenceMap.remove(rider.getCoverageId());
                riderDetailDto.setCoverageName(m.get("coverage_name").toString());
                riderDetailDto.setCoverageId(rider.getCoverageId());
                riderDetailDto.setCoverTerm(rider.getCoverTerm());
                riderDetailDto.setSumAssured(rider.getSumAssured());
                riderDetailDto.setWaiverOfPremium(rider.getWaiverOfPremium());
                riderDetailDtoList.add(riderDetailDto);
            }
        }
        planDetailDto.setRiderDetails(riderDetailDtoList);
        dto.setPlanDetailDto(planDetailDto);
        dto.setProposedAssured(proposedAssuredDto);
        dto.setProposer(proposerDto);
        return dto;
    }

    public List<Map<String, Object>> findAllOptionalCoverages(String planId) {
        // Query against the view
        List<Map<String, Object>> resultSet = namedParameterJdbcTemplate.queryForList("select * from plan_coverage where plan_id in (:planId) " +
                "AND (optional = 1)", new MapSqlParameterSource().addValue("planId", planId.toString()));
        return resultSet;
    }

    public List<ILSearchQuotationResultDto> searchQuotation(String quotationNumber, String proposerName, String proposerNrcNumber, String agentCode, String quotationStatus) {
        SqlParameterSource sqlParameterSource  = new MapSqlParameterSource("quotationNumber",quotationNumber).addValue("proposerName",proposerName)
                .addValue("nrcNumber",proposerNrcNumber).addValue("quotationStatus",quotationStatus).addValue("agentCode",agentCode);
        return namedParameterJdbcTemplate.query(QUOTATION_SEARCH_QUERY,sqlParameterSource, new BeanPropertyRowMapper(ILSearchQuotationResultDto.class));
    }

    public List<ILSearchQuotationResultDto> findSharedQuotationByQuotationNumber(String quotationNumber) {
        SqlParameterSource sqlParameterSource  = new MapSqlParameterSource("quotationNumber",quotationNumber);
        return namedParameterJdbcTemplate.query(findSharedQuotationByQuotationNumberQuery,sqlParameterSource, new BeanPropertyRowMapper(ILSearchQuotationResultDto.class));
    }

    public Map<String,Object> findPolicyFeeBy(String quotationNumber) {
        SqlParameterSource sqlParameterSource  = new MapSqlParameterSource("quotationNumber",quotationNumber);
        List<Map<String,Object>> policyFeeMap  = namedParameterJdbcTemplate.query(findQuotationDetailByQuotationNumberQuery,sqlParameterSource, new ColumnMapRowMapper());
        if (isNotEmpty(policyFeeMap)){
            return policyFeeMap.get(0);
        }
        return Collections.EMPTY_MAP;
    }


    public List<ILQuotation> findQuotationByQuotNumberAndStatusByExcludingGivenQuotId(String quotationNumber, QuotationId quotationId, String quotationStatus) {
        SqlParameterSource sqlParameterSource =  new MapSqlParameterSource("quotationNumber",quotationNumber).addValue("quotationId",quotationId.getQuotationId()).addValue("quotationStatus",quotationStatus);
        return namedParameterJdbcTemplate.query(findILQuotationByQuotationNumberQuery,sqlParameterSource,new BeanPropertyRowMapper<>(ILQuotation.class));
    }

    public List<Map<String,Object>> getChildQuotations(String parentQuotationId) {
        SqlParameterSource sqlParameterSource =  new MapSqlParameterSource("parentQuotationId",parentQuotationId);
        return namedParameterJdbcTemplate.query(findQuotationByQuotationNumberQuery, sqlParameterSource, new ColumnMapRowMapper());
    }

    public List<RiderDetailDto> findCoveragesByPlanAndAssuredDOB(String planId, int proposedAssuredDOB) {
        List<String> coverageIds = getCoverageIds(planId, proposedAssuredDOB);
        // Query against the view
        List<Map<String, Object>> resultSet = namedParameterJdbcTemplate.queryForList("select DISTINCT * from plan_coverage where plan_id in (:planId) " +
                "AND (optional = 1)", new MapSqlParameterSource().addValue("planId", planId.toString()));
        return resultSet.parallelStream().filter(new Predicate<Map<String, Object>>() {
            @Override
            public boolean test(Map<String, Object> coverageMap) {
                return coverageIds.contains(coverageMap.get("coverage_id"));
            }
        }).map(new Function<Map<String, Object>, RiderDetailDto>() {
            @Override
            public RiderDetailDto apply(Map<String, Object> coverageMap) {
                RiderDetailDto dto = new RiderDetailDto();
                dto.setCoverageName(coverageMap.get("coverage_name").toString());
                dto.setCoverageId(coverageMap.get("coverage_id").toString());
                return dto;

            }
        }).collect(Collectors.toList());
    }


    private List<String> getCoverageIds(String planId,int age){
        Criteria planCriteria = Criteria.where("_id").is(planId);
        Query query = new Query(planCriteria);
        query.fields().include("coverages");
        Map planMap = mongoTemplate.findOne(query, Map.class, "PLAN");
        List<PlanCoverage> planCoverages = (List<PlanCoverage>) planMap.get("coverages");
        return planCoverages.parallelStream().filter(new Predicate<PlanCoverage>() {
            @Override
            public boolean test(PlanCoverage planCoverage) {
                return (planCoverage.getCoverageType().equals(CoverageType.OPTIONAL) && age >= planCoverage.getMinAge() && age <=planCoverage.getMaxAge());
            }
        }).map(new Function<PlanCoverage, String>() {
            @Override
            public String apply(PlanCoverage planCoverage) {
                return planCoverage.getCoverageId().getCoverageId();
            }
        }).collect(Collectors.toList());
    }

    public Map<String, Set> getPremiumPaymentType(PlanId planId) {
        Plan plan = planRepository.findOne(planId);
        return constructPremiumTypeMapFromPlan(plan);
    }

    private Map<String, Set> constructPremiumTypeMapFromPlan(Plan plan) {
        PremiumTermType premiumTermType = plan.getPremiumTermType();
        Map<String, Set> premiumTypeMap = Maps.newLinkedHashMap();
        Set<String> sheetNames = premiumTermType.getSheetNamesByPremiumTermType();
        sheetNames.forEach(s -> {
            if (s.equalsIgnoreCase("SINGLE")) {
                premiumTypeMap.put(s, Sets.newHashSet("Single"));
            } else if(s.equalsIgnoreCase("SPECIFIED_AGES")) {
                premiumTypeMap.put(s, plan.getPremiumTerm().getMaturityAges());
            } else{
                premiumTypeMap.put(s, plan.getPremiumTerm().getValidTerms());
            }
        });
        return premiumTypeMap;
    }
}
