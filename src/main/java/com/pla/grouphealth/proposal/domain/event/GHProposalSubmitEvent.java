package com.pla.grouphealth.proposal.domain.event;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Admin on 8/23/2015.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GHProposalSubmitEvent implements Serializable {

    private ProposalId proposalId;

}

