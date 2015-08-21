package com.pla.individuallife.proposal.domain.event;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Admin on 7/29/2015.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ILProposalClosureEvent implements Serializable {
    private ProposalId proposalId;
}
