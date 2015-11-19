package com.pla.individuallife.proposal.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.pla.core.domain.model.CoverageName;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.query.PremiumFinder;
import com.pla.individuallife.proposal.domain.model.ILProposalStatus;
import com.pla.individuallife.proposal.presentation.dto.*;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.CoverageType;
import com.pla.sharedkernel.domain.model.PlanStatus;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.nthdimenzion.presentation.AppUtils;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 6/25/2015.
 */
@Service
@Finder
public class ILProposalFinder {

    public static final String FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY = "SELECT * FROM agent WHERE first_name =:firstName";
    public static final String FIND_AGENT_BY_ID_QUERY = "SELECT agent_id as agentId, first_name as firstName, last_name as lastName FROM agent WHERE agent_id=:agentId AND agent_status = 'ACTIVE'";
    public static final String FIND_ACTIVE_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId AND agentStatus='ACTIVE'";
    public static final String FIND_OCCUPATION_CLASS_QUERY = "SELECT code,description FROM occupation_class WHERE description=:occupation";
    /**
     * Find all the Plans by Agent Id and for a line of business.
     */
    private static final String SEARCH_PLAN_BY_AGENT_IDS = "SELECT DISTINCT C.* FROM AGENT A JOIN agent_authorized_plan b " +
            "ON A.`agent_id`=B.`agent_id` JOIN plan_coverage_benefit_assoc C " +
            "ON B.`plan_id`=C.`plan_id` where A.agent_id IN (:agentIds) and c.line_of_business=:lineOfBusiness group by C.plan_id ";
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private MongoTemplate mongoTemplate;

    @Autowired
    private IPremiumCalculator premiumCalculator;
    @Autowired
    private PremiumFinder premiumFinder;
    @Autowired
    private PlanFinder planFinder;
    @Autowired
    private CoverageFinder coverageFinder;


    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public String getAgentFullNameById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        Map<String, Object> map = namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        if (map.get("lastName") != null)
            return map.get("firstName") + " " + map.get("lastName");
        else
            return map.get("firstName").toString();
    }

    public String getOccupationClass(String occupation) {
        List<Map<String, Object>> occupationClassList = namedParameterJdbcTemplate.query(FIND_OCCUPATION_CLASS_QUERY, new MapSqlParameterSource().addValue("occupation", occupation), new ColumnMapRowMapper());
        if (isNotEmpty(occupationClassList)) {
            Map<String, Object> occupationClassMap = occupationClassList.get(0);
            return (String) occupationClassMap.get("code");
        }
        return "";
    }

    public List<RiderDetailDto> findAllOptionalCoverages(String planId,int age) {
        List<String> coverageIds = getCoverageIds(planId, age);
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



    public List<ILSearchProposalDto> searchProposalToApprove(ILSearchProposalForApprovalDto dto, String[] statuses) {
        Criteria criteria = Criteria.where("proposalStatus").in(statuses);
        String proposalNumber = dto.getProposalNumber();
        String proposalId = dto.getProposalId();
        String agentCode = dto.getAgentCode();
        String proposerName = dto.getProposerName();
        String agentName = dto.getAgentName();
        String proposerNrcNumber =  dto.getProposerNrcNumber();
        if (isEmpty(proposalNumber) && isEmpty(proposalId) && isEmpty(agentCode) && isEmpty(proposerName) && isEmpty(agentName) && isEmpty(proposerNrcNumber)) {
            return Lists.newArrayList();
        }
        if (isNotEmpty(proposalId)) {
            criteria = criteria.and("_id").is(new ProposalId(proposalId));
        }
        if (isNotEmpty(proposalNumber)) {
            criteria = criteria.and("proposalNumber").is(proposalNumber);
        }
        if (isNotEmpty(agentCode)) {
            criteria = criteria != null ? criteria.and("agentCommissionShareModel.commissionShare").elemMatch(Criteria.where("agentId.agentId").is(agentCode)) : Criteria.where("agentCommissionShareModel.commissionShare").elemMatch(Criteria.where("agentId.agentId").is(agentCode));
        }
        if (isNotEmpty(proposerName)) {
            String proposerPattern = "^"+proposerName;
            criteria = criteria != null ? criteria.and("proposer.firstName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.firstName").regex(Pattern.compile(proposerPattern,Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(proposerNrcNumber)) {
            String proposerNrcPattern = "^"+proposerNrcNumber;
            criteria = criteria != null ? criteria.and("proposer.nrc").regex(Pattern.compile(proposerNrcPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.nrc").regex(Pattern.compile(proposerNrcPattern,Pattern.CASE_INSENSITIVE));
        }
        Set<String> agentIds = null;
        if (isNotEmpty(agentName)) {
            List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY, new MapSqlParameterSource().addValue("firstName", agentName));
            agentIds = agentList.stream().map(new Function<Map<String, Object>, String>() {
                @Override
                public String apply(Map<String, Object> stringObjectMap) {
                    return (String) stringObjectMap.get("agentId");
                }
            }).collect(Collectors.toSet());
        }
        if (isNotEmpty(agentIds)) {
            criteria = criteria.and("agentId.agentId").in(agentIds);
        }
        criteria = criteria.and("routinglevel").is(dto.getRoutingLevel());
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC,"proposalNumber"));
        query.with(new Sort(Sort.Direction.DESC,"versionNumber"));
        List<Map> allProposals = mongoTemplate.find(query, Map.class, "individual_life_proposal");
        List<ILSearchProposalDto> ilProposalDtoList = allProposals.stream().map(new TransformToILSearchProposalDto()).collect(Collectors.toList());
        return ilProposalDtoList;
    }


    public List<ILSearchProposalDto> searchProposal(ILSearchProposalDto ilSearchProposalDto, String[] statuses) {
        Criteria criteria = Criteria.where("proposalStatus").in(statuses);
        String proposalNumber = ilSearchProposalDto.getProposalNumber();
        String proposalId = ilSearchProposalDto.getProposalId();
        String agentCode = ilSearchProposalDto.getAgentCode();
        String proposerName = ilSearchProposalDto.getProposerName();
        String agentName = ilSearchProposalDto.getAgentName();
        String proposerNrcNumber =  ilSearchProposalDto.getProposerNrcNumber();
        if (isEmpty(proposalNumber) && isEmpty(proposalId) && isEmpty(agentCode) && isEmpty(proposerName) && isEmpty(agentName) && isEmpty(proposerNrcNumber)) {
            return Lists.newArrayList();
        }
        if (isNotEmpty(proposalId)) {
            criteria = criteria.and("_id").is(new ProposalId(proposalId));
        }
        if (isNotEmpty(proposalNumber)) {
            criteria = criteria.and("proposalNumber").is(proposalNumber);
        }
        if (isNotEmpty(agentCode)) {
            criteria = criteria != null ? criteria.and("agentCommissionShareModel.commissionShare").elemMatch(Criteria.where("agentId.agentId").is(agentCode)) : Criteria.where("agentCommissionShareModel.commissionShare").elemMatch(Criteria.where("agentId.agentId").is(agentCode));
        }
        if (isNotEmpty(proposerName)) {
            String proposerPattern = "^"+proposerName;
            criteria = criteria != null ? criteria.and("proposer.firstName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.firstName").regex(Pattern.compile(proposerPattern,Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(proposerNrcNumber)) {
            String proposerNrcPattern = "^"+proposerNrcNumber;
            criteria = criteria != null ? criteria.and("proposer.nrc").regex(Pattern.compile(proposerNrcPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.nrc").regex(Pattern.compile(proposerNrcPattern,Pattern.CASE_INSENSITIVE));
        }
        Set<String> agentIds = null;
        if (isNotEmpty(agentName)) {
            List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY, new MapSqlParameterSource().addValue("firstName", agentName));
            agentIds = agentList.stream().map(new Function<Map<String, Object>, String>() {
                @Override
                public String apply(Map<String, Object> stringObjectMap) {
                    return (String) stringObjectMap.get("agentId");
                }
            }).collect(Collectors.toSet());
        }
        if (isNotEmpty(agentIds)) {
            criteria = criteria.and("agentId.agentId").in(agentIds);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC,"proposalNumber"));
        query.with(new Sort(Sort.Direction.DESC,"versionNumber"));
        List<Map> allProposals = mongoTemplate.find(query, Map.class, "individual_life_proposal");
        List<ILSearchProposalDto> ilProposalDtoList = allProposals.stream().map(new TransformToILSearchProposalDto()).collect(Collectors.toList());
        return ilProposalDtoList;
    }

    /*
    * @TODO premium with policy fee bug Fix
    * */
    public PremiumDetailDto getPremiumDetail(String proposalId) {

        PremiumDetailDto premiumDetailDto = new PremiumDetailDto();

        Set<RiderPremiumDto> riderPremiumDtoSet = new HashSet<RiderPremiumDto>();

        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");

        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        if (planDetail==null){
            return new PremiumDetailDto();
        }
        if (planDetail!=null){
            if (planDetail.getSumAssured()==null){
                return new PremiumDetailDto();
            }
        }
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);

        DateTime dob = new DateTime(((ProposedAssured) proposal.get("proposedAssured")).getDateOfBirth());
        Integer age = Years.yearsBetween(dob, DateTime.now()).getYears() + 1;
        String occupationCode = getOccupationClass(((ProposedAssured) proposal.get("proposedAssured")).getEmploymentDetail().getOccupationClass());
        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        List<PremiumInfluencingFactor> premiumInfluencingFactors = premium.getPremiumInfluencingFactors();
        String premiumPaymentTerm = "0";
        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, planDetail.getSumAssured()!=null?planDetail.getSumAssured().setScale(0) .toString():"");
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, ((ProposedAssured) proposal.get("proposedAssured")).getGender().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, planDetail.getPolicyTerm().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM))) {
                premiumPaymentTerm = planDetail.getPremiumPaymentTerm().toString();
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, premiumPaymentTerm);
            }
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, occupationCode);
        }

        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremiumWithPolicyFee(premiumCalculationDto, planDetail.getSumAssured()!=null?planDetail.getSumAssured().setScale(0,BigDecimal.ROUND_FLOOR):BigDecimal.ONE);
        premiumDetailDto.setPlanAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));

        BigDecimal totalPremium = premiumDetailDto.getPlanAnnualPremium();
        BigDecimal semiAnnualPremium = ComputedPremiumDto.getSemiAnnualPremium(computedPremiums);
        BigDecimal quarterlyPremium = ComputedPremiumDto.getQuarterlyPremium(computedPremiums);
        BigDecimal monthlyPremium = ComputedPremiumDto.getMonthlyPremium(computedPremiums);

        Set<ILRiderDetail> riderList = planDetail.getRiderDetails();

        if (riderList != null) {
            for (ILRiderDetail rider : riderList) {
                if (rider.getSumAssured()==null)
                    continue;
                if ((new BigDecimal(rider.getSumAssured().toString()).compareTo(new BigDecimal("0.0")) != 0) || (rider.getCoverTerm() != 0)) {
                    premiumCalculationDto = new PremiumCalculationDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);
                    premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(rider.getCoverageId()));
                    premiumInfluencingFactors = premiumFinder.findPremium(premiumCalculationDto).getPremiumInfluencingFactors();

                    for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, rider.getSumAssured()!=null?rider.getSumAssured().setScale(0).toString():"");
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, ((ProposedAssured) proposal.get("proposedAssured")).getGender().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, rider.getCoverTerm().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, premiumPaymentTerm);
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, occupationCode);
                    }
                    RiderPremiumDto rd = new RiderPremiumDto();
                    rd.setCoverageId(new CoverageId(rider.getCoverageId()));
                    rd.setCoverageName(new CoverageName(coverageFinder.getCoverageDetail(rider.getCoverageId()).get("coverageName").toString()));
                    computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto,planDetail.getSumAssured()!=null?planDetail.getSumAssured().setScale(0):BigDecimal.ONE);
                    rd.setAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));
                    rd.setSemiAnnualPremium(ComputedPremiumDto.getSemiAnnualPremium(computedPremiums));
                    rd.setQuarterlyPremium(ComputedPremiumDto.getQuarterlyPremium(computedPremiums));
                    rd.setMonthlyPremium(ComputedPremiumDto.getMonthlyPremium(computedPremiums));
                    totalPremium = totalPremium.add(ComputedPremiumDto.getAnnualPremium(computedPremiums));
                    semiAnnualPremium = semiAnnualPremium.add(ComputedPremiumDto.getSemiAnnualPremium(computedPremiums));
                    quarterlyPremium = quarterlyPremium.add(ComputedPremiumDto.getQuarterlyPremium(computedPremiums));
                    monthlyPremium = monthlyPremium.add(ComputedPremiumDto.getMonthlyPremium(computedPremiums));
                    riderPremiumDtoSet.add(rd);
                }
            }
        }
        premiumDetailDto.setRiderPremiums(riderPremiumDtoSet);
        premiumDetailDto.setTotalPremium(totalPremium.add(ComputedPremiumDto.getAnnualPolicyFee(computedPremiums)));
        premiumDetailDto.setPlanName(planFinder.getPlanName(new PlanId(planDetail.getPlanId())));
        premiumDetailDto.setAnnualPremium(totalPremium.add(ComputedPremiumDto.getAnnualPolicyFee(computedPremiums)).setScale(0, BigDecimal.ROUND_HALF_UP));
        premiumDetailDto.setMonthlyPremium(monthlyPremium.add(ComputedPremiumDto.getMonthlyFee(computedPremiums)).setScale(0, BigDecimal.ROUND_HALF_UP));
        premiumDetailDto.setQuarterlyPremium(quarterlyPremium.add(ComputedPremiumDto.getQuarterlyFee(computedPremiums)).setScale(0, BigDecimal.ROUND_HALF_UP));
        premiumDetailDto.setSemiannualPremium(semiAnnualPremium.add(ComputedPremiumDto.getSemiAnnualPolicyFee(computedPremiums)).setScale(0, BigDecimal.ROUND_HALF_UP));
        return premiumDetailDto;
    }

    public ILProposalDto getProposalById(String proposalId) {
        ILProposalDto dto = new ILProposalDto();
        Map proposal = getProposalByProposalId(proposalId);
        if (proposal.get("proposedAssured") != null) {
            dto.setProposedAssured(ProposedAssuredBuilder.getProposedAssuredBuilder((ProposedAssured) proposal.get("proposedAssured")).createProposedAssuredDto());
        }
        if (proposal.get("proposer") != null) {
            dto.setProposer(ProposerBuilder.getProposerBuilder((Proposer) proposal.get("proposer")).createProposerDto());
        }

        Quotation quotation =  proposal.get("quotation")!=null? (Quotation) proposal.get("quotation"):null;
        if (quotation!=null)
            dto.setHasQuotationNumber(Boolean.TRUE);
        dto.setProposalPlanDetail((ProposalPlanDetail) proposal.get("proposalPlanDetail"));
        if (dto.getProposalPlanDetail() != null) {
            if (dto.getProposalPlanDetail().getSumAssured() != null) {
                List<Map<String, Object>> coverageNameList = planFinder.findAllOptionalCoverage(dto.getProposalPlanDetail().getPlanId());
                dto.getProposalPlanDetail().setPlanName(planFinder.getPlanName(new PlanId(dto.getProposalPlanDetail().getPlanId())));
                ProposalPlanDetail proposalPlanDetail = dto.getProposalPlanDetail();
                proposalPlanDetail.setRiderDetails(proposalPlanDetail.getRiderDetails().parallelStream().map(new Function<ILRiderDetail, ILRiderDetail>() {
                    @Override
                    public ILRiderDetail apply(ILRiderDetail ilRiderDetail) {
                        Optional<Map<String, Object>> coverageNameMap = coverageNameList.parallelStream().filter(new Predicate<Map<String, Object>>() {
                            @Override
                            public boolean test(Map<String, Object> coverageNameMap) {
                                return ilRiderDetail.getCoverageId().equals(coverageNameMap.get("coverageId"));
                            }
                        }).findAny();
                        if (coverageNameMap.isPresent()) {
                            String coverageName = (String) coverageNameMap.get().get("coverageName");
                            ilRiderDetail.setCoverageName(coverageName);
                        }
                        return ilRiderDetail;
                    }
                }).collect(Collectors.toSet()));
            }
        }
        dto.setBeneficiaries((List<Beneficiary>) proposal.get("beneficiaries"));
        dto.setTotalBeneficiaryShare(new BigDecimal(proposal.get("totalBeneficiaryShare").toString()) );
        dto.setGeneralDetails((GeneralDetails) proposal.get("generalDetails"));
        dto.setCompulsoryHealthStatement((List<Question>) proposal.get("compulsoryHealthStatement"));
        dto.setFamilyPersonalDetail((FamilyPersonalDetail) proposal.get("familyPersonalDetail"));
        dto.setAdditionaldetails((AdditionalDetails) proposal.get("additionalDetails"));
        dto.setPremiumPaymentDetails((PremiumPaymentDetails) proposal.get("premiumPaymentDetails"));
        /*if(dto.getProposalPlanDetail() != null)
            dto.setPremiumDetailDto(getPremiumDetail(proposalId));*/
        // TODO : need to set document details once it is ready
        AgentCommissionShareModel model = (AgentCommissionShareModel) proposal.get("agentCommissionShareModel");

        Set<AgentDetailDto> agentCommissionDetails = new HashSet<AgentDetailDto>();
        model.getCommissionShare().forEach(commissionShare -> agentCommissionDetails.add(new AgentDetailDto(commissionShare.getAgentId().toString(), getAgentFullNameById(commissionShare.getAgentId().toString()), commissionShare.getAgentCommission())));
        dto.setAgentCommissionDetails(agentCommissionDetails);
        dto.setProposalId(proposalId);
        dto.setProposalStatus(proposal.get("proposalStatus").toString());
        dto.setProposalNumber(proposal.get("proposalNumber").toString());
        dto.setSubmittedOn(proposal.get("submittedOn") != null ?  proposal.get("submittedOn").toString() : "");
        return dto;
    }

    public String getProposalNumberById(String proposalId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");
        return proposal.get("proposalNumber").toString();
    }

    public Set<Map<String, Object>> getPlans(List<String> agentIds,Integer proposedAssuredAge) {
        List<List<Map<String ,Object>>> agentAuthorisedPlan = agentIds.parallelStream().map(new Function<String, List<Map<String,Object>>>() {
            @Override
            public List<Map<String, Object>> apply(String agentId) {
                return namedParameterJdbcTemplate.query(SEARCH_PLAN_BY_AGENT_IDS, new MapSqlParameterSource().addValue("agentIds", agentId).addValue("lineOfBusiness", "Individual Life"), new ColumnMapRowMapper());
            }
        }).collect(Collectors.toList());
        return findCommonAuthorisedPlanByAgent(agentAuthorisedPlan, proposedAssuredAge);

    }

    private List<String> getPlanIds(Integer age ){
        Criteria planCriteria = Criteria.where("status").nin(PlanStatus.WITHDRAWN, PlanStatus.DRAFT, PlanStatus.PREMIUM_CONFIGURED);
        Query query = new Query(planCriteria);
        List<Map> allPlans = mongoTemplate.find(query,Map.class, "PLAN");
        return allPlans.parallelStream().filter(new Predicate<Map>() {
            @Override
            public boolean test(Map map) {
                PlanDetail planDetail = (PlanDetail) map.get("planDetail");
                int minEntryAge = planDetail.getMinEntryAge();
                int maxEntryAge = planDetail.getMaxEntryAge();
                Set<Relationship> relationships = planDetail.getApplicableRelationships();
                Optional<Relationship> relationship = relationships.parallelStream().filter(new Predicate<Relationship>() {
                    @Override
                    public boolean test(Relationship relationship) {
                        return relationship.equals(Relationship.SELF);
                    }
                }).findAny();
                if (age >= minEntryAge && age <= maxEntryAge)
                    if (relationship.isPresent())
                        return true;
                return false;
            }
        }).map(new Function<Map, String>() {
            @Override
            public String apply(Map map) {
                return  map.get("_id").toString();
            }
        }).collect(Collectors.toList());
    }

    public Map findProposalByQuotationNumber(String quotationNumber) {
        Criteria proposalCriteria = Criteria.where("quotation.quotationNumber").is(quotationNumber);
        Query query = new Query(proposalCriteria);
        Map proposalMap = mongoTemplate.findOne(query, Map.class, "individual_life_proposal");
        return proposalMap;
    }

    public Map getProposalByProposalId(String proposalId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");
        return proposal;
    }

    public Map<String, Object> getAgentByAgentId(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        return isNotEmpty(agentList) ? agentList.get(0) : Maps.newHashMap();
    }

    private Set<Map<String ,Object>> findCommonAuthorisedPlanByAgent(List<List<Map<String, Object>>> agentAuthorisedPlan, Integer proposedAssuredAge){
        if (isEmpty(agentAuthorisedPlan)){
            return Collections.EMPTY_SET;
        }
        Set<Map<String ,Object>> commonAuthorisedPlan = Sets.newHashSet(agentAuthorisedPlan.get(0));
        for (List<Map<String,Object>> agentPlanList : agentAuthorisedPlan) {
            commonAuthorisedPlan = Sets.intersection(commonAuthorisedPlan, Sets.newHashSet(agentPlanList));
        }
        if (proposedAssuredAge==null || proposedAssuredAge==0){
            return commonAuthorisedPlan;
        }
        List<String> planIds = getPlanIds(proposedAssuredAge);
        return commonAuthorisedPlan.parallelStream().filter(new Predicate<Map<String, Object>>() {
            @Override
            public boolean test(Map<String, Object> planDetails) {
                return  planIds.contains(planDetails.get("plan_id"));
            }
        }).map(new Function<Map<String,Object>, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(Map<String, Object> planDetailMap) {
                return planDetailMap;
            }
        }).collect(Collectors.toSet());
    }

    private class TransformToILSearchProposalDto implements Function<Map, ILSearchProposalDto> {

        @Override
        public ILSearchProposalDto apply(Map map) {
            String proposalId = map.get("_id").toString();
            String submittedOn = map.get("submittedOn") != null ? AppUtils.toString(new DateTime(map.get("submittedOn"))): "";
            String proposalStatus = map.get("proposalStatus") != null ? ILProposalStatus.valueOf(map.get("proposalStatus").toString()).getDescription() : "";
            String proposalNumber = map.get("proposalNumber") != null ? (String) map.get("proposalNumber") : "";
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getFirstName() + " " + proposerMap.getSurname() : "";
            String agentNames = ((AgentCommissionShareModel) map.get("agentCommissionShareModel")).getCommissionShare().stream().map(x -> getAgentFullNameById(x.getAgentId().toString())).collect(Collectors.joining(" , "));
            ILSearchProposalDto ilSearchProposalDto = new ILSearchProposalDto(proposalNumber, proposerName, "NRCNumber", agentNames, "Agent Code", proposalId, submittedOn, proposalStatus);
            return ilSearchProposalDto;
        }
    }


}
