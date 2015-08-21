package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by Samir on 6/25/2015.
 */
@EqualsAndHashCode
@Getter
public class ProposalNumber {

    private String proposalNumber;

    public ProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }
}
