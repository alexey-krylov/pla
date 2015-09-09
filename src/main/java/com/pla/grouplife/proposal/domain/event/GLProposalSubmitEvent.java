package com.pla.grouplife.proposal.domain.event;

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
public class GLProposalSubmitEvent implements Serializable {

    private ProposalId proposalId;
}