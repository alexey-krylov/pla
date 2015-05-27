package com.pla.individuallife.proposal.domain.service;

import com.pla.individuallife.proposal.domain.model.ProposalProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeProposalProcessorRole;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Component
public class RoleAdapter {

    public ProposalProcessor userToProposalProcessor(UserDetails userDetails) {
        boolean hasProposalPreprocessorRole = hasIndividualLifeProposalProcessorRole(userDetails.getAuthorities());
        if (!hasProposalPreprocessorRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal processor(ROLE_PROPOSAL_PROCESSOR) authority");
        }
        return new ProposalProcessor(userDetails.getUsername());
    }
}
