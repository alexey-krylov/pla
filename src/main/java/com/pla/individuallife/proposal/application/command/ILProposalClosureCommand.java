package com.pla.individuallife.proposal.application.command;

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
public class ILProposalClosureCommand {

    private ProposalId proposalId;
}
