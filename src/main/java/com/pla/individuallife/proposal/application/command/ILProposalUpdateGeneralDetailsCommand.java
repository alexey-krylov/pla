package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.domain.model.GeneralDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 7/1/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdateGeneralDetailsCommand {

    private GeneralDetails generalDetails;
    private UserDetails userDetails;
    private String proposalId;

}
