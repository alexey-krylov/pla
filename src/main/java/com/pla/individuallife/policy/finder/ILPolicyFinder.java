package com.pla.individuallife.policy.finder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.pla.core.domain.model.CoverageName;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.query.PremiumFinder;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.proposal.presentation.dto.PremiumDetailDto;
import com.pla.individuallife.proposal.presentation.dto.RiderPremiumDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 8/4/2015.
 */
@Service
public class ILPolicyFinder {

    private MongoTemplate mongoTemplate;

    @Autowired
    private IPremiumCalculator premiumCalculator;
    @Autowired
    private PremiumFinder premiumFinder;
    @Autowired
    private PlanFinder planFinder;
    @Autowired
    private CoverageFinder coverageFinder;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public static final String FIND_AGENT_BY_ID_QUERY = "SELECT agent_id as agentId, first_name as firstName, last_name as lastName FROM agent WHERE agent_id=:agentId AND agent_status = 'ACTIVE'";

    private static final String IL_POLICY_COLLECTION_NAME = "individual_life_policy";

    public List<Map> findAllPolicy() {
        return mongoTemplate.findAll(Map.class, IL_POLICY_COLLECTION_NAME);
    }


    public Map findPolicyById(String policyId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(policyId)), Map.class, IL_POLICY_COLLECTION_NAME);
    }

    public List<Map> searchPolicy(String policyNumber, String policyHolderName, String proposalNumber) {
        if (isEmpty(policyHolderName) && isEmpty(policyNumber) && isEmpty(proposalNumber)) {
            return Lists.newArrayList();
        }
        Criteria criteria = Criteria.where("policyStatus").is("IN_FORCE");
        if (isNotEmpty(policyHolderName)) {
            String proposerPattern = "^" + policyHolderName;
            criteria = criteria.and("proposer.firstName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(proposalNumber)) {
            criteria = criteria.and("proposal.proposalNumber.proposalNumber").is(proposalNumber);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "policyNumber.policyNumber"));
        return mongoTemplate.find(query, Map.class, IL_POLICY_COLLECTION_NAME);
    }

    /*
    *
    *Refactor the Premium Detail service
    * */
    public PremiumDetailDto getPremiumDetail(PolicyId policyId) {

        PremiumDetailDto premiumDetailDto = new PremiumDetailDto();

        Set<RiderPremiumDto> riderPremiumDtoSet = new HashSet<RiderPremiumDto>();

        BasicDBObject query = new BasicDBObject();
        query.put("_id", policyId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, IL_POLICY_COLLECTION_NAME);

        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        if (planDetail==null){
            return new PremiumDetailDto();
        }
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);

        DateTime dob = new DateTime(((ProposedAssured) proposal.get("proposedAssured")).getDateOfBirth());
        Integer age = Years.yearsBetween(dob, DateTime.now()).getYears() + 1;

        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        List<PremiumInfluencingFactor> premiumInfluencingFactors = premium.getPremiumInfluencingFactors();

        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, planDetail.getSumAssured().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, ((ProposedAssured) proposal.get("proposedAssured")).getGender().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, planDetail.getPolicyTerm().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, planDetail.getPremiumPaymentTerm().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, ((ProposedAssured) proposal.get("proposedAssured")).getEmploymentDetail().getOccupationClass());
        }

        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremiumWithPolicyFee(premiumCalculationDto);
        premiumDetailDto.setPlanAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));

        BigDecimal totalPremium = premiumDetailDto.getPlanAnnualPremium();
        BigDecimal semiAnnualPremium = ComputedPremiumDto.getSemiAnnualPremium(computedPremiums);
        BigDecimal quarterlyPremium = ComputedPremiumDto.getQuarterlyPremium(computedPremiums);
        BigDecimal monthlyPremium = ComputedPremiumDto.getMonthlyPremium(computedPremiums);

        Set<ILRiderDetail> riderList = planDetail.getRiderDetails();

        if (riderList != null) {
            for (ILRiderDetail rider : riderList) {
                if ((new BigDecimal(rider.getSumAssured().toString()).compareTo(new BigDecimal("0.0")) != 0) || (rider.getCoverTerm() != 0)) {
                    premiumCalculationDto = new PremiumCalculationDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);
                    premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(rider.getCoverageId()));
                    premiumInfluencingFactors = premiumFinder.findPremium(premiumCalculationDto).getPremiumInfluencingFactors();

                    for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, rider.getSumAssured().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, ((ProposedAssured) proposal.get("proposedAssured")).getGender().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, rider.getCoverTerm().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, rider.getWaiverOfPremium().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, ((ProposedAssured) proposal.get("proposedAssured")).getEmploymentDetail().getOccupationClass());
                    }
                    RiderPremiumDto rd = new RiderPremiumDto();
                    rd.setCoverageId(new CoverageId(rider.getCoverageId()));
                    rd.setCoverageName(new CoverageName(coverageFinder.getCoverageDetail(rider.getCoverageId()).get("coverageName").toString()));
                    computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
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
        premiumDetailDto.setRiderPremiumDtos(riderPremiumDtoSet);
        premiumDetailDto.setTotalPremium(totalPremium);
        premiumDetailDto.setPlanName(planFinder.getPlanName(new PlanId(planDetail.getPlanId())));
        premiumDetailDto.setAnnualPremium(totalPremium);
        premiumDetailDto.setMonthlyPremium(monthlyPremium);
        premiumDetailDto.setQuarterlyPremium(quarterlyPremium);
        premiumDetailDto.setSemiannualPremium(semiAnnualPremium);
        return premiumDetailDto;
    }

    public ILPolicyDto getPolicyById(PolicyId policyId) {
        ILPolicyDto dto = new ILPolicyDto();
        Map policy = getPolicyByPolicyId(policyId);
        if (policy.get("proposedAssured") != null) {
            dto.setProposedAssured(ProposedAssuredBuilder.getProposedAssuredBuilder((ProposedAssured) policy.get("proposedAssured")).createProposedAssuredDto());
        }
        if (policy.get("proposer") != null) {
            dto.setProposer(ProposerBuilder.getProposerBuilder((Proposer) policy.get("proposer")).createProposerDto());
        }
        dto.setProposalPlanDetail((ProposalPlanDetail) policy.get("proposalPlanDetail"));
        if (dto.getProposalPlanDetail() != null) {
            dto.getProposalPlanDetail().setPlanName(planFinder.getPlanName(new PlanId(dto.getProposalPlanDetail().getPlanId())));
        }
        dto.setBeneficiaries((List<Beneficiary>) policy.get("beneficiaries"));
        dto.setTotalBeneficiaryShare(new BigDecimal(policy.get("totalBeneficiaryShare").toString()) );
        dto.setGeneralDetails((GeneralDetails) policy.get("generalDetails"));
        dto.setCompulsoryHealthStatement((List<Question>) policy.get("compulsoryHealthStatement"));
        dto.setFamilyPersonalDetail((FamilyPersonalDetail) policy.get("familyPersonalDetail"));
        dto.setAdditionaldetails((AdditionalDetails) policy.get("additionalDetails"));
        dto.setPremiumPaymentDetails((PremiumPaymentDetails) policy.get("premiumPaymentDetails"));
        if(dto.getProposalPlanDetail() != null)
            dto.setPremiumDetailDto(getPremiumDetail(policyId));
        AgentCommissionShareModel model = (AgentCommissionShareModel) policy.get("agentCommissionShareModel");
        Set<AgentDetailDto> agentCommissionDetails = new HashSet<AgentDetailDto>();
        model.getCommissionShare().forEach(commissionShare -> agentCommissionDetails.add(new AgentDetailDto(commissionShare.getAgentId().toString(), getAgentFullNameById(commissionShare.getAgentId().toString()), commissionShare.getAgentCommission())));
        dto.setAgentCommissionDetails(agentCommissionDetails);
        dto.setPolicyId(policyId.getPolicyId());
        dto.setPolicyStatus(policy.get("policyStatus").toString());
        dto.setProposal((Proposal)policy.get("proposal"));
        dto.setPolicyNumber((PolicyNumber) policy.get("policyNumber"));
        dto.setSubmittedOn(policy.get("submittedOn") != null ? policy.get("submittedOn").toString() : "");
        dto.setProposerDocuments(policy.get("proposalDocuments") != null ? (List) policy.get("proposalDocuments") : Collections.EMPTY_LIST);
        return dto;
    }

    public Map getPolicyByPolicyId(PolicyId proposalId){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, IL_POLICY_COLLECTION_NAME);
        return proposal;
    }

    public String getAgentFullNameById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        Map<String, Object> map = namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        if (map.get("lastName") != null)
            return map.get("firstName") + " " + map.get("lastName");
        else
            return map.get("firstName").toString();
    }

    public String getProposalIdByPolicyId(PolicyId policyId) {
        Criteria policyCriteria = Criteria.where("_id").is(policyId);
        Query query = new Query(policyCriteria);
        query.fields().include("proposal.proposalId");
        Map proposalMap = mongoTemplate.findOne(query, Map.class, IL_POLICY_COLLECTION_NAME);
        if (isEmpty(proposalMap)){
            return null;
        }
        Map proposal = (Map) proposalMap.get("proposal");
        return proposal.get("proposalId").toString();
    }
}
