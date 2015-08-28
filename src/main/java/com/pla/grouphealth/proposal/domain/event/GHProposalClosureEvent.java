package com.pla.grouphealth.proposal.domain.event;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by Admin on 8/23/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GHProposalClosureEvent implements Serializable {
    private ProposalId proposalId;
}
