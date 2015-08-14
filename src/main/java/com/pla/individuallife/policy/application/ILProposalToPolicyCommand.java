package com.pla.individuallife.policy.application;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Admin on 8/3/2015.
 */
@Getter
@NoArgsConstructor
public class ILProposalToPolicyCommand {

    private ProposalId proposalId;

    public ILProposalToPolicyCommand(ProposalId proposalId) {
        this.proposalId = proposalId;
    }

}
