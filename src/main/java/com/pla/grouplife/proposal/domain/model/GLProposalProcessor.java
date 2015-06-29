package com.pla.grouplife.proposal.domain.model;

import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Samir on 4/7/2015.
 */
@EqualsAndHashCode(of = "userName")
@Getter
public class GLProposalProcessor {

    private String userName;

    public GLProposalProcessor(String userName) {
        this.userName = userName;
    }


    public GroupLifeProposal createGroupLifeProposal(ProposalId proposalId,String proposalNumber,GroupLifeQuotation groupLifeQuotation) {

            GroupLifeProposal groupLifeProposal = GroupLifeProposal.createGroupLifeProposal(proposalId,groupLifeQuotation.getQuotationId(),proposalNumber,
                    groupLifeQuotation.getAgentId(),groupLifeQuotation.getProposer(),groupLifeQuotation.getInsureds(),groupLifeQuotation.getPremiumDetail(),
                    GLProposalStatus.PENDING_FIRST_PREMIUM);

        return null;
    }
}
