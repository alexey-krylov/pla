package com.pla.individuallife.proposal.domain.event;

/**
 * Created by Admin on 7/29/2015.
 */

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ILProposalPurgeEvent implements Serializable {
    private ProposalId proposalId;
}

