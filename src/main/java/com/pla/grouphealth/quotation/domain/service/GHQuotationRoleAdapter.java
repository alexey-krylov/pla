package com.pla.grouphealth.quotation.domain.service;

import com.pla.grouphealth.quotation.domain.model.GHQuotationProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasGroupHealthQuotationProcessorRole;

/**
 * Created by Samir on 4/8/2015.
 */
@Component
public class GHQuotationRoleAdapter {

    public GHQuotationProcessor userToQuotationProcessor(UserDetails userDetails) {

        boolean hasQuotationProcessorRole = hasGroupHealthQuotationProcessorRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            throw new AuthorizationServiceException("User does not have Quotation processor(GROUP_HEALTH_QUOTATION_PROCESSOR_ROLE) authority");
        }
        return new GHQuotationProcessor(userDetails.getUsername());
    }


}
