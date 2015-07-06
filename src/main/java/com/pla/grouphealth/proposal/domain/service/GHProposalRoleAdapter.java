package com.pla.grouphealth.proposal.domain.service;

import com.pla.grouphealth.proposal.domain.model.GHProposalProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasGroupHealthProposalProcessorRole;

/**
 * Created by Samir on 7/6/2015.
 */
@Component
public class GHProposalRoleAdapter {

    public GHProposalProcessor userToProposalProcessor(UserDetails userDetails) {

        boolean hasQuotationProcessorRole = hasGroupHealthProposalProcessorRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            throw new AuthorizationServiceException("User does not have Quotation processor(GROUP_HEALTH_PROPOSAL_PROCESSOR_ROLE) authority");
        }
        return new GHProposalProcessor(userDetails.getUsername());
    }
}
