package com.pla.grouplife.proposal.application.command;

import com.pla.grouplife.quotation.query.PremiumDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateGLProposalWithPremiumDetailCommand {

    private String quotationId;

    private PremiumDetailDto premiumDetailDto;

    private UserDetails userDetails;
}
