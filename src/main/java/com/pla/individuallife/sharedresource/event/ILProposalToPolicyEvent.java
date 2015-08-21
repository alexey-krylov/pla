package com.pla.individuallife.sharedresource.event;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Admin on 7/29/2015.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ILProposalToPolicyEvent {
    private ProposalId proposalId;
}
