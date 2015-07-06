package com.pla.grouphealth.proposal.application.command;

import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 6/3/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GHProposalRecalculatedInsuredPremiumCommand {

    private String proposalId;

    private GHPremiumDetailDto premiumDetailDto;

    private UserDetails userDetails;
}
