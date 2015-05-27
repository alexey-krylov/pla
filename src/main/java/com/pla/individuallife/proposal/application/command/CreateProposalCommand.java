package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.proposal.presentation.dto.ProposerDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 26-May-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProposalCommand {

    private ProposedAssuredDto proposedAssured;

    private ProposerDto proposer;

}
