package com.pla.sharedkernel.domain.model;

import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.ProposalNumber;
import lombok.Getter;

/**
 * Created by Samir on 7/8/2015.
 */
@Getter
public class Proposal {

    private ProposalId proposalId;

    private ProposalNumber proposalNumber;


    public Proposal(ProposalId proposalId, ProposalNumber proposalNumber) {
        this.proposalId = proposalId;
        this.proposalNumber = proposalNumber;
    }
}
