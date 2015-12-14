package com.pla.grouphealth.proposal.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.proposal.query.GHProposalFinder;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHPremiumDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.sharedkernel.domain.model.Quotation;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
import com.pla.sharedkernel.identifier.QuotationId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Samir on 6/25/2015.
 */
@Service
public class GroupHealthProposalFactory {

    private GHFinder ghFinder;

    private GHProposalFinder ghProposalFinder;

    private GHProposalNumberGenerator ghProposalNumberGenerator;

    @Autowired
    public GroupHealthProposalFactory(GHFinder ghFinder, GHProposalFinder ghProposalFinder, GHProposalNumberGenerator ghProposalNumberGenerator) {
        this.ghFinder = ghFinder;
        this.ghProposalFinder = ghProposalFinder;
        this.ghProposalNumberGenerator = ghProposalNumberGenerator;
    }

    public GroupHealthProposal createProposal(QuotationId quotationId,ProposalId proposalId) {
        Map quotationMap = ghFinder.searchQuotationById(quotationId);
        boolean moratoriumPeriodApplicable = quotationMap.get("moratoriumPeriodApplicable") != null ? (boolean) quotationMap.get("moratoriumPeriodApplicable") : false;
        boolean samePlanForAllRelation = quotationMap.get("samePlanForAllRelation") != null ? (boolean) quotationMap.get("samePlanForAllRelation") : false;
        boolean samePlanForAllCategory = quotationMap.get("samePlanForAllCategory") != null ? (boolean) quotationMap.get("samePlanForAllCategory") : false;
        String quotationNumber = (String) quotationMap.get("quotationNumber");
        Integer versionNumber = (Integer) quotationMap.get("versionNumber");
        String schemeName = quotationMap.get("schemeName")!=null?(String) quotationMap.get("schemeName"):"";
        Map proposalMap = ghProposalFinder.findProposalByQuotationNumber(quotationNumber);
        ProposalNumber proposalNumber = proposalMap != null ? (ProposalNumber) proposalMap.get("proposalNumber") : new ProposalNumber(ghProposalNumberGenerator.getProposalNumber(GroupHealthProposal.class, LocalDate.now()));
        Quotation quotation = new Quotation(quotationNumber, versionNumber,quotationId);
        AgentId agentId = (AgentId) quotationMap.get("agentId");
        Set<GHInsured> insureds = new HashSet((List) quotationMap.get("insureds"));
        GHPremiumDetail premiumDetail = (GHPremiumDetail) quotationMap.get("premiumDetail");
        GHProposer proposer = (GHProposer) quotationMap.get("proposer");
        GroupHealthProposal groupHealthProposal = new GroupHealthProposal(proposalId, quotation, proposalNumber);
        groupHealthProposal.updateFlagSamePlanForAllRelation(samePlanForAllRelation);
        groupHealthProposal.updateFlagSamePlanForAllCategory(samePlanForAllCategory);
        groupHealthProposal.updateFlagMoratoriumPeriodApplicable(moratoriumPeriodApplicable);
        groupHealthProposal = groupHealthProposal.updateWithAgent(agentId, BigDecimal.ZERO,Boolean.FALSE).updateWithProposer(proposer).updateWithInsureds(insureds).updateWithPremiumDetail(premiumDetail).updateWithSchemeName(schemeName);
        return groupHealthProposal;
    }
}
