package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.domain.model.FamilyPersonalDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Prasant on 12-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor

public class ILProposalUpdateFamilyPersonalDetailsCommand {
    private FamilyPersonalDetail familyPersonalDetail;
    private UserDetails userDetails;
    private String proposalId;
}
