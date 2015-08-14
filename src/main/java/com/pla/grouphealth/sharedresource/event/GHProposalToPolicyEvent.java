package com.pla.grouphealth.sharedresource.event;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
@NoArgsConstructor
public class GHProposalToPolicyEvent {

    private ProposalId proposalId;

    public GHProposalToPolicyEvent(ProposalId proposalId) {
        this.proposalId = proposalId;
    }
}
