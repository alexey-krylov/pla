package com.pla.quotation.domain.service;

import com.pla.quotation.domain.model.QuotationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasQuotationProcessorRole;

/**
 * Created by Samir on 4/8/2015.
 */
@Component
public class QuotationRoleAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotationRoleAdapter.class);

    public QuotationProcessor userToQuotationProcessor(UserDetails userDetails) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("User details received" + userDetails);
        }
        boolean hasQuotationProcessorRole = hasQuotationProcessorRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            LOGGER.error("user does not have ROLE_QUOTATION_PROCESSOR");
            throw new AuthorizationServiceException("User does not have Quotation processor(ROLE_QUOTATION_PROCESSOR) authority");
        }
        return new QuotationProcessor(userDetails.getUsername());
    }


}
