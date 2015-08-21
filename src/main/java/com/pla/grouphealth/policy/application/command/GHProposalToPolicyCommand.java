package com.pla.grouphealth.policy.application.command;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
@NoArgsConstructor
public class GHProposalToPolicyCommand {

    private ProposalId proposalId;

    public GHProposalToPolicyCommand(ProposalId proposalId) {
        this.proposalId = proposalId;
    }

}
