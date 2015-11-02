package com.pla.grouplife.sharedresource.util;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.proposal.domain.service.GLProposalNumberGenerator;
import com.pla.grouplife.proposal.query.GLProposalFinder;
import com.pla.grouplife.sharedresource.model.vo.Industry;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.PremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.Quotation;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
import com.pla.sharedkernel.identifier.QuotationId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by User on 6/30/2015.
 */
@Component
public class GroupLifeProposalFactory {
    private GLFinder glFinder;

    private GLProposalFinder glProposalFinder;

    private GLProposalNumberGenerator glProposalNumberGenerator;

    @Autowired
    public GroupLifeProposalFactory(GLFinder glFinder, GLProposalFinder glProposalFinder, GLProposalNumberGenerator glProposalNumberGenerator) {
        this.glFinder = glFinder;
        this.glProposalFinder = glProposalFinder;
        this.glProposalNumberGenerator = glProposalNumberGenerator;
    }

    public GroupLifeProposal createProposal(String quotationId, ProposalId proposalId) {
        Map quotationMap = glFinder.getQuotationById(quotationId);
        String quotationNumber = (String) quotationMap.get("quotationNumber");
        Industry industry = (Industry) quotationMap.get("industry");
        Integer versionNumber = (Integer) quotationMap.get("versionNumber");
        Map proposalMap = glProposalFinder.findProposalByQuotationNumber(quotationNumber);
        ProposalNumber proposalNumber = proposalMap != null ? (ProposalNumber) proposalMap.get("proposalNumber") : new ProposalNumber(glProposalNumberGenerator.getProposalNumber(GroupLifeProposal.class, LocalDate.now()));
        Quotation quotation = new Quotation(quotationNumber, versionNumber, new QuotationId(quotationId));
        AgentId agentId = (AgentId) quotationMap.get("agentId");
        Set<Insured> insureds = new HashSet<Insured>((List) quotationMap.get("insureds"));
        PremiumDetail premiumDetail = (PremiumDetail) quotationMap.get("premiumDetail");
        Proposer proposer = (Proposer) quotationMap.get("proposer");
        GroupLifeProposal groupLifeProposal = new GroupLifeProposal(proposalId, quotation, proposalNumber);
        groupLifeProposal = groupLifeProposal.updateWithAgentId(agentId).updateWithProposer(proposer)
                .updateWithInsureds(insureds).updateWithPremiumDetail(premiumDetail).updateWithIndustry(industry);
        return groupLifeProposal;
    }
}
