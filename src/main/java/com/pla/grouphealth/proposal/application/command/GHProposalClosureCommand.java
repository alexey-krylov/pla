package com.pla.grouphealth.proposal.application.command;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 8/23/2015.
 */
@AllArgsConstructor
@Setter
@Getter
public class GHProposalClosureCommand {

    private ProposalId proposalId;
}
