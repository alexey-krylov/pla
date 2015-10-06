package com.pla.individuallife.policy.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.individuallife.policy.domain.model.IndividualLifePolicy;
import com.pla.individuallife.proposal.presentation.dto.PremiumDetailDto;
import com.pla.individuallife.proposal.query.ILProposalFinder;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 8/3/2015.
 */
@Component
public class ILPolicyFactory {

    private ILProposalFinder ilProposalFinder;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    private ILPolicyNumberGenerator ilPolicyNumberGenerator;

    @Autowired
    public ILPolicyFactory(ILProposalFinder ilProposalFinder,ILPolicyNumberGenerator ilPolicyNumberGenerator){
        this.ilProposalFinder = ilProposalFinder;
        this.ilPolicyNumberGenerator =  ilPolicyNumberGenerator;
    }

    public IndividualLifePolicy createPolicy(ProposalId proposalId) throws InvocationTargetException, IllegalAccessException {
        Map proposalMap = ilProposalFinder.getProposalByProposalId(proposalId.getProposalId());
        ProposedAssured proposedAssured = (ProposedAssured) proposalMap.get("proposedAssured");
        Proposer proposer = (Proposer) proposalMap.get("proposer");
        List<Beneficiary> beneficiaries = (List<Beneficiary>) proposalMap.get("beneficiaries");
        List<Question> questions = (List<Question>)proposalMap.get("compulsoryHealthStatement");
        GeneralDetails generalDetails =  (GeneralDetails) proposalMap.get("generalDetails");
        FamilyPersonalDetail familyPersonalDetail  =(FamilyPersonalDetail) proposalMap.get("familyPersonalDetail");
        AdditionalDetails additionaldetails = (AdditionalDetails) proposalMap.get("additionalDetails");
        PremiumPaymentDetails premiumPaymentDetails = (PremiumPaymentDetails) proposalMap.get("premiumPaymentDetails");
        PremiumDetailDto premiumDetailDto =  ilProposalFinder.getPremiumDetail(proposalId.getProposalId());
        PremiumDetail premiumDetail = new PremiumDetail();
        BeanUtils.copyProperties(premiumDetail, premiumDetailDto);
        premiumPaymentDetails.setPremiumDetail(premiumDetail);
        List<ILProposerDocument> ilProposerDocuments = (List<ILProposerDocument>) proposalMap.get("proposalDocuments");
        ProposalPlanDetail proposalPlanDetail = (ProposalPlanDetail) proposalMap.get("proposalPlanDetail");
        ProposalNumber proposalNumber = new ProposalNumber((String)proposalMap.get("proposalNumber"));
        AgentCommissionShareModel agentCommissionShareModel = (AgentCommissionShareModel) proposalMap.get("agentCommissionShareModel");
        PolicyNumber policyNumber = generatePolicyNumber(agentCommissionShareModel.getCommissionShare());
        PolicyId policyId = new PolicyId(ObjectId.get().toString());
        DateTime policyInceptionDate = DateTime.now();
        DateTime policyExpireDate = policyInceptionDate.plusYears(proposalPlanDetail.getPolicyTerm());
        Proposal proposal = new Proposal(proposalId, proposalNumber);
        IndividualLifePolicy  individualLifePolicy = IndividualLifePolicy.createPolicy(policyId,policyNumber,proposal,policyInceptionDate,policyExpireDate);
        individualLifePolicy.withProposedAssured(proposedAssured)
                .withAgentCommissionShareModel(agentCommissionShareModel)
                .withProposer(proposer)
                .withBeneficiaries(beneficiaries)
                .withCompulsoryHealthStatement(questions)
                .withGeneralDetails(generalDetails)
                .withFamilyPersonalDetail(familyPersonalDetail)
                .withAdditionalDetails(additionaldetails)
                .withPremiumPaymentDetails(premiumPaymentDetails)
                .withProposalPlanDetail(proposalPlanDetail)
                .withProposalDocuments(ilProposerDocuments);
        return individualLifePolicy;
    }

    private PolicyNumber generatePolicyNumber(List<AgentCommissionShareModel.AgentCommissionShare> agentCommissionShares){
        AgentId agentId = null;
        if (isNotEmpty(agentCommissionShares)){
            agentId =  getDominantAgentId(agentCommissionShares);
        }
        String policyNumberInString = ilPolicyNumberGenerator.getPolicyNumber(IndividualLifePolicy.class, agentId);
        return new PolicyNumber(policyNumberInString);
    }


    private AgentId getDominantAgentId(List<AgentCommissionShareModel.AgentCommissionShare> agentCommissionShares){
        Optional<AgentCommissionShareModel.AgentCommissionShare> agentCommissionShareOptional =  agentCommissionShares.parallelStream().max(new Comparator<AgentCommissionShareModel.AgentCommissionShare>() {
            @Override
            public int compare(AgentCommissionShareModel.AgentCommissionShare agentLeft, AgentCommissionShareModel.AgentCommissionShare agentRight) {
                return agentLeft.getAgentCommission().compareTo(agentRight.getAgentCommission());
            }
        });
        if (agentCommissionShareOptional.isPresent()){
            return agentCommissionShareOptional.get().getAgentId();
        }
        return null;
    }

    private Set<GHInsured> populateFamilyId(Set<GHInsured> insureds) {
        Map<String, Object> entitySequenceMap = sequenceGenerator.getEntitySequenceMap(GHInsured.class);
        final Integer[] sequenceNumber = {((Integer) entitySequenceMap.get("sequenceNumber")) + 1};
        insureds.forEach(insured -> {
            if (insured.getNoOfAssured() == null) {
                String selfFamilySequence = sequenceNumber[0] + "01";
                sequenceNumber[0] = sequenceNumber[0] + 1;
                FamilyId familyId = new FamilyId(selfFamilySequence);
                insured = insured.updateWithFamilyId(familyId);
            }
        });
        sequenceGenerator.updateSequence(sequenceNumber[0], (Integer) entitySequenceMap.get("sequenceId"));
        return insureds;
    }

}
