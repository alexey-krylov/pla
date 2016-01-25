package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 6/25/2015.
 */
@EqualsAndHashCode
@NoArgsConstructor
@Getter
@Setter
public class ProposalNumber {

    private String proposalNumber;

    public ProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }
}
