package com.pla.individuallife.policy.finder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.BasicDBObject;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.policy.domain.model.IndividualLifePolicy;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.joda.time.DateTime;
import org.nthdimenzion.presentation.AppUtils;
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
    private PlanFinder planFinder;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public static final String FIND_ACTIVE_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId AND agentStatus='ACTIVE'";

    public static final String FIND_AGENT_BY_ID_QUERY = "SELECT agent_id as agentId, first_name as firstName, last_name as lastName FROM agent WHERE agent_id=:agentId AND agent_status = 'ACTIVE'";

    private static final String IL_POLICY_COLLECTION_NAME = "individual_life_policy";


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

    public List<Map> findAllPolicy() {
        return mongoTemplate.findAll(Map.class, IL_POLICY_COLLECTION_NAME);
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
        AgentCommissionShareModel model = (AgentCommissionShareModel) policy.get("agentCommissionShareModel");
        Set<AgentDetailDto> agentCommissionDetails = new HashSet<AgentDetailDto>();
        model.getCommissionShare().forEach(commissionShare -> agentCommissionDetails.add(new AgentDetailDto(commissionShare.getAgentId().toString(), getAgentFullNameById(commissionShare.getAgentId().toString()), commissionShare.getAgentCommission())));
        dto.setAgentCommissionDetails(agentCommissionDetails);
        dto.setPolicyId(policyId.getPolicyId());
        dto.setPolicyStatus(policy.get("policyStatus").toString());
        dto.setProposal((Proposal) policy.get("proposal"));
        dto.setPolicyNumber((PolicyNumber) policy.get("policyNumber"));
        dto.setInceptionOn(policy.get("inceptionOn") != null ? new DateTime(policy.get("inceptionOn")) : null);
        dto.setExpiryDate(policy.get("expiredOn") != null ? new DateTime(policy.get("expiredOn")) : null);

        //dto.setInceptionOn(policy.get("inceptionOn") != null ? AppUtils.toString(new DateTime(policy.get("inceptionOn"))): null);
        //dto.setExpiryDate(policy.get("expiredOn") != null ? AppUtils.toString(new DateTime(policy.get("expiredOn"))) : null);

        dto.setOpportunityId(policy.get("opportunityId")!=null?((OpportunityId)policy.get("opportunityId")).getOpportunityId():"");
        dto.setProposerDocuments(policy.get("proposalDocuments") != null ? (List) policy.get("proposalDocuments") : Collections.EMPTY_LIST);
        return dto;
    }

    public Map getPolicyByPolicyId(PolicyId proposalId){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, IL_POLICY_COLLECTION_NAME);
        return proposal;
    }

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        return isNotEmpty(agentList) ? agentList.get(0) : Maps.newHashMap();
    }


    public String getAgentFullNameById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        Map<String, Object> map = namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        if (map.get("lastName") != null)
            return map.get("firstName") + " " + map.get("lastName");
        else
            return map.get("firstName").toString();
    }

    public Map findProposalIdByPolicyNumber(String policyNumber) {
        Query query = new Query(Criteria.where("policyNumber.policyNumber").is(policyNumber));
        return mongoTemplate.findOne(query, Map.class, IL_POLICY_COLLECTION_NAME);
    }

    public ILPolicyDto searchByPolicyNumber(String policyNumber, String[] statuses) {
        Criteria criteria = Criteria.where("policyStatus").in(statuses);
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policyNumber.policyNumber").is(policyNumber);
        }
        Query query = new Query(criteria);
        Map policy = mongoTemplate.findOne(query, Map.class, IL_POLICY_COLLECTION_NAME);

        ILPolicyDto dto = new ILPolicyDto();
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
        AgentCommissionShareModel model = (AgentCommissionShareModel) policy.get("agentCommissionShareModel");
        Set<AgentDetailDto> agentCommissionDetails = new HashSet<AgentDetailDto>();
        model.getCommissionShare().forEach(commissionShare -> agentCommissionDetails.add(new AgentDetailDto(commissionShare.getAgentId().toString(), getAgentFullNameById(commissionShare.getAgentId().toString()), commissionShare.getAgentCommission())));
        dto.setAgentCommissionDetails(agentCommissionDetails);
        dto.setPolicyId(policy.get("_id").toString());
        dto.setPolicyStatus(policy.get("policyStatus").toString());
        dto.setProposal((Proposal) policy.get("proposal"));
        dto.setPolicyNumber((PolicyNumber) policy.get("policyNumber"));
        dto.setInceptionOn(policy.get("inceptionOn") != null ? new DateTime(policy.get("inceptionOn")) : null);
        dto.setExpiryDate(policy.get("expiredOn") != null ? new DateTime(policy.get("expiredOn")) : null);
        dto.setOpportunityId(policy.get("opportunityId") != null ? ((OpportunityId) policy.get("opportunityId")).getOpportunityId() : "");
        dto.setProposerDocuments(policy.get("proposalDocuments") != null ? (List) policy.get("proposalDocuments") : Collections.EMPTY_LIST);
        dto.setPolicyHolder((Proposer) policy.get("proposer"));
        //dto.setBeneficiaries((List<Beneficiary>) policy.get("beneficiaries"));
        dto.setLifeAssured((ProposedAssured) policy.get("proposedAssured"));
        return dto;


         /*  List<IndividualLifePolicy> searchedPolicy = ilFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(), new String[]{"IN_FORCE"});
          if (isNotEmpty(searchedPolicy)) {
            IndividualLifePolicy individualLifePolicy = searchedPolicy.get(0);
            ILPolicyDetailDto policyDetailDto = new ILPolicyDetailDto();
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            PlanId planId = new PlanId(individualLifePolicy.getProposalPlanDetail().getPlanId());
            Set<PlanId> planIds = Sets.newLinkedHashSet();
            planIds.add(planId);
            Set<String> endorsementTypes = iPlanAdapter.getConfiguredEndorsementType(planIds);
            ilPolicyDto.setProposal(individualLifePolicy.getProposal());
            ilPolicyDto.setPremiumPaymentDetails(individualLifePolicy.getPremiumPaymentDetails());
            //ilPolicyDto.setIlEndorsementType(individualLifePolicy.getIL)
            ilPolicyDto.setBeneficiaries(individualLifePolicy.getBeneficiaries());
            //ilPolicyDto.setAgentCommissionDetails(individualLifePolicy.getAgentCommissionShareModel());
            ilPolicyDto.setPolicyNumber(individualLifePolicy.getPolicyNumber());
            ilPolicyDto.setPolicyHolder(individualLifePolicy.getProposer());
            ilPolicyDto.setLifeAssured(individualLifePolicy.getProposedAssured());
            ilPolicyDto.setPolicyId(individualLifePolicy.getPolicyId().getPolicyId());
            ilPolicyDto.setEndorsementTypes(getAllEndorsementTypes(endorsementTypes));
            ilPolicyDto.setInceptionOn(individualLifePolicy.getInceptionOn());
            ilPolicyDto.setProposalPlanDetail(individualLifePolicy.getProposalPlanDetail());
            //ilPolicyDto.getPolicyNumber().setEndorsementTypes(getAllEndorsementTypes(endorsementTypes));
            *//*policyDetailDto.setEndorsementTypes(getAllEndorsementTypes(endorsementTypes));
            policyDetailDto.setPolicyHolderName(individualLifePolicy.getProposer().getFirstName());
            policyDetailDto.setPolicyId(individualLifePolicy.getPolicyId().getPolicyId());*//*
            return ilPolicyDto;
        }*/
    }

}
