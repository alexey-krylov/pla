package com.pla.individuallife.policy.finder;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.query.PremiumFinder;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
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

}
