package com.pla.grouplife.proposal.domain.service;

import com.pla.grouplife.proposal.domain.model.GLProposalProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasGroupLifeProposalProcessorRole;

/**
 * Created by Samir on 4/8/2015.
 */
@Component
public class GroupLifeProposalRoleAdapter {

    public GLProposalProcessor userToProposalProcessor(UserDetails userDetails) {

        boolean haspProposalProcessorRole = hasGroupLifeProposalProcessorRole(userDetails.getAuthorities());
        if (!haspProposalProcessorRole) {
            throw new AuthorizationServiceException("User does not have Proposal processor(ROLE_GROUP_LIFE_PROPOSAL_PROCESSOR) authority");
        }
        return new GLProposalProcessor(userDetails.getUsername());
    }
}
