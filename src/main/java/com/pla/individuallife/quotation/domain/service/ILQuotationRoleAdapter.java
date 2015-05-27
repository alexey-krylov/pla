package com.pla.individuallife.quotation.domain.service;

import com.pla.individuallife.proposal.domain.model.ProposalProcessor;
import com.pla.individuallife.quotation.domain.model.ILQuotationProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeProposalProcessorRole;
import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeQuotationProcessorRole;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Component
public class ILQuotationRoleAdapter {

    public ILQuotationProcessor userToQuotationProcessor(UserDetails userDetails) {
        boolean hasQuotationProcessorRole = hasIndividualLifeQuotationProcessorRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Quotation processor(ROLE_QUOTATION_PROCESSOR) authority");
        }
        return new ILQuotationProcessor(userDetails.getUsername());
    }

    public ProposalProcessor userToProposalProcessor(UserDetails userDetails) {
        boolean hasProposalPreprocessorRole = hasIndividualLifeProposalProcessorRole(userDetails.getAuthorities());
        if (!hasProposalPreprocessorRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Proposal processor(ROLE_PROPOSAL_PROCESSOR) authority");
        }
        return new ProposalProcessor(userDetails.getUsername());
    }
}
