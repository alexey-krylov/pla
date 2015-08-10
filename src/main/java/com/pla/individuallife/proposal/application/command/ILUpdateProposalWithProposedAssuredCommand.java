package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
import com.pla.individuallife.sharedresource.model.vo.ProposalPlanDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakafr on 26-May-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILUpdateProposalWithProposedAssuredCommand {
    private ProposedAssuredDto proposedAssured;
    private ProposalPlanDetail planDetail;
    private UserDetails userDetails;
    private String proposalId;
}
