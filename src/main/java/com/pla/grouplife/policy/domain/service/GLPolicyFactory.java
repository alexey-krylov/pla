package com.pla.grouplife.policy.domain.service;

import com.google.common.collect.Maps;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.policy.domain.model.GroupLifePolicy;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 7/8/2015.
 */
@Component
public class GLPolicyFactory {

    private GLFinder glFinder;

    private SequenceGenerator sequenceGenerator;

    private GLPolicyNumberGenerator glPolicyNumberGenerator;

    @Autowired
    public GLPolicyFactory(GLFinder glFinder, SequenceGenerator sequenceGenerator, GLPolicyNumberGenerator glPolicyNumberGenerator) {
        this.glFinder = glFinder;
        this.sequenceGenerator = sequenceGenerator;
        this.glPolicyNumberGenerator = glPolicyNumberGenerator;
    }

    public GroupLifePolicy createPolicy(ProposalId proposalId) {
        Map proposalMap = glFinder.findProposalById(proposalId.getProposalId());
        boolean samePlanForAllRelation = proposalMap.get("samePlanForAllRelation") != null ? (boolean) proposalMap.get("samePlanForAllRelation") : false;
        boolean samePlanForAllCategory = proposalMap.get("samePlanForAllCategory") != null ? (boolean) proposalMap.get("samePlanForAllCategory") : false;
        AgentId agentId = (AgentId) proposalMap.get("agentId");
        String  schemeName =proposalMap.get("schemeName")!=null? (String) proposalMap.get("schemeName"):"";
        BigDecimal freeCoverLimit =proposalMap.get("freeCoverLimit")!=null? new BigDecimal((String) proposalMap.get("freeCoverLimit")):BigDecimal.ZERO;
        Set<Insured> insureds = new HashSet((List) proposalMap.get("insureds"));
        PremiumDetail premiumDetail = (PremiumDetail) proposalMap.get("premiumDetail");
        Industry industry = (Industry) proposalMap.get("industry");
        OpportunityId opportunityId = proposalMap.get("opportunityId")!=null?(OpportunityId) proposalMap.get("opportunityId"):null;
        Proposer proposer = (Proposer) proposalMap.get("proposer");
        ProposalNumber proposalNumber = (ProposalNumber) proposalMap.get("proposalNumber");
        Set<GLProposerDocument> proposerDocuments = proposalMap.get("proposerDocuments") != null ? new HashSet<>((List) proposalMap.get("proposerDocuments")) : null;
        Boolean isCommissionOverridden =  proposalMap.get("isCommissionOverridden")!=null?(Boolean) proposalMap.get("isCommissionOverridden"):false;
        BigDecimal agentCommissionPercentage =  proposalMap.get("agentCommissionPercentage")!=null?new BigDecimal((String) proposalMap.get("agentCommissionPercentage")):BigDecimal.ZERO;
        String policyNumberInString = glPolicyNumberGenerator.getPolicyNumber(GroupLifePolicy.class, agentId);
        PolicyNumber policyNumber = new PolicyNumber(policyNumberInString);
        PolicyId policyId = new PolicyId(ObjectId.get().toString());
        DateTime policyInceptionDate = DateTime.now();
        DateTime policyExpireDate = policyInceptionDate.plusDays(premiumDetail.getPolicyTermValue());
        Proposal proposal = new Proposal(proposalId, proposalNumber);
        GroupLifePolicy groupLifePolicy = new GroupLifePolicy(policyId, policyNumber, proposal, policyInceptionDate, policyExpireDate);
        groupLifePolicy.updateFlagSamePlanForAllRelation(samePlanForAllRelation);
        groupLifePolicy.updateFlagSamePlanForAllCategory(samePlanForAllCategory);
        insureds = populateFamilyId(insureds);
        groupLifePolicy = groupLifePolicy.addAgentId(agentId).addProposer(proposer).addInsured(insureds)
                .addPremium(premiumDetail).addIndustry(industry).addDocuments(proposerDocuments)
                .updateWithCommissionPercentage(isCommissionOverridden,agentCommissionPercentage)
                .addOpportunityId(opportunityId)
                .withSchemeName(schemeName)
                .withFCL(freeCoverLimit);
        return groupLifePolicy;
    }


    private Set<Insured> populateFamilyId(Set<Insured> insureds) {
        Map<String, Object> entitySequenceMap = sequenceGenerator.getEntitySequenceMap(Insured.class);
        final Integer[] sequenceNumber = {((Integer) entitySequenceMap.get("sequenceNumber")) + 1};
        Integer dependentSequenceNumber = 0 ;
        for(Insured insured : insureds){
            if (insured.getNoOfAssured() == null) {
                dependentSequenceNumber = sequenceNumber[0];
                String selfFamilySequence = sequenceNumber[0] + "01";
                sequenceNumber[0] = sequenceNumber[0] + 1;
                FamilyId familyId = new FamilyId(selfFamilySequence);
                insured = insured.updateWithFamilyId(familyId);
            }
            if (isNotEmpty(insured.getInsuredDependents())) {
                Map<Relationship, List<Integer>> relationshipSequenceMap = groupDependentSequenceByRelation(insured.getInsuredDependents());
                for (InsuredDependent insuredDependent : insured.getInsuredDependents()){
                    if (insuredDependent.getNoOfAssured() == null) {
                        List<Integer> sequenceList = relationshipSequenceMap.get(insuredDependent.getRelationship());
                        String selfFamilySequence = dependentSequenceNumber.toString() + sequenceList.get(0);
                        relationshipSequenceMap.put(insuredDependent.getRelationship(), sequenceList);
                        FamilyId familyId = new FamilyId(selfFamilySequence);
                        insuredDependent = insuredDependent.updateWithFamilyId(familyId);
                    }
                }
            }
        };
        sequenceGenerator.updateSequence(sequenceNumber[0], (Integer) entitySequenceMap.get("sequenceId"));
        return insureds;
    }

    private Map<Relationship, List<Integer>> groupDependentSequenceByRelation(Set<InsuredDependent> insuredDependents) {
        Map<Relationship, List<Integer>> dependentSequenceMap = Maps.newHashMap();
        final int[] currentSequence = {3};
        insuredDependents.forEach(insuredDependent -> {if (dependentSequenceMap.get(insuredDependent.getRelationship()) == null) {
            List<Integer> sequenceList = new ArrayList<Integer>();
            sequenceList.add((Relationship.SPOUSE.equals(insuredDependent.getRelationship()) ? 02 : currentSequence[0]));
            currentSequence[0] = currentSequence[0] + 1;
            dependentSequenceMap.put(insuredDependent.getRelationship(), sequenceList);
        } else {
            List<Integer> sequenceList = dependentSequenceMap.get(insuredDependent.getRelationship());
            currentSequence[0] = sequenceList.get((sequenceList.size() - 1));
            currentSequence[0] = currentSequence[0] + 1;
            sequenceList.add(currentSequence[0]);
        }
        });
        return dependentSequenceMap;
    }

}
