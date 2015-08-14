package com.pla.grouplife.policy.application.command;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
@NoArgsConstructor
public class GLProposalToPolicyCommand {

    private ProposalId proposalId;

    public GLProposalToPolicyCommand(ProposalId proposalId) {
        this.proposalId = proposalId;
    }

}
