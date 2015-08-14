package com.pla.grouplife.sharedresource.event;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
@NoArgsConstructor
public class GLProposalToPolicyEvent {

    private ProposalId proposalId;

    public GLProposalToPolicyEvent(ProposalId proposalId) {
        this.proposalId = proposalId;
    }
}
