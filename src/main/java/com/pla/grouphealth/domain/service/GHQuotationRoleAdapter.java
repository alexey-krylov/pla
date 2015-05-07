package com.pla.grouphealth.domain.service;

import com.pla.grouphealth.domain.model.quotation.GHQuotationProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasGroupHealthQuotationProcessorRole;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Component
public class GHQuotationRoleAdapter {

    public GHQuotationProcessor userToQuotationProcessor(UserDetails userDetails) {

        boolean hasQuotationProcessorRole = hasGroupHealthQuotationProcessorRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            throw new AuthorizationServiceException("User does not have Group Health Quotation processor(ROLE_QUOTATION_PROCESSOR) authority");
        }
        return new GHQuotationProcessor(userDetails.getUsername());
    }


}
