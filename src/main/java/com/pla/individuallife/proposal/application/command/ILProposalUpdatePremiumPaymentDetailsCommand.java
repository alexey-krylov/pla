package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.model.vo.PremiumPaymentDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 7/2/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdatePremiumPaymentDetailsCommand {
    private PremiumPaymentDetails premiumPaymentDetails;
    private UserDetails userDetails;
    private String proposalId;
}
