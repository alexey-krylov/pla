package com.pla.grouphealth.proposal.domain.service;

import com.pla.grouphealth.proposal.domain.model.GHProposalApprover;
import com.pla.grouphealth.proposal.domain.model.GHProposalProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasGroupHealthProposalApproverRole;
import static com.pla.sharedkernel.util.RolesUtil.hasGroupHealthProposalProcessorRole;

/**
 * Created by Samir on 7/6/2015.
 */
@Component
public class GHProposalRoleAdapter {

    public GHProposalProcessor userToProposalProcessor(UserDetails userDetails) {

        boolean hasQuotationProcessorRole = hasGroupHealthProposalProcessorRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            throw new AuthorizationServiceException("User does not have proposal processor(ROLE_GROUP_HEALTH_PROPOSAL_PROCESSOR) authority");
        }
        return new GHProposalProcessor(userDetails.getUsername());
    }

    public GHProposalApprover userToProposalApprover(UserDetails userDetails) {

        boolean hasQuotationProcessorRole = hasGroupHealthProposalApproverRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            throw new AuthorizationServiceException("User does not have proposal approver(ROLE_GROUP_HEALTH_PROPOSAL_APPROVER) authority");
        }
        return new GHProposalApprover(userDetails.getUsername());
    }
}
