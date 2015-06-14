package com.pla.individuallife.quotation.domain.service;

import com.pla.individuallife.quotation.domain.model.ILQuotationProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;

import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeQuotationProcessorRole;

/**
 * Created by Karunakar on 5/13/2015.
 */
public class ILQuotationRoleAdapter {

    public static ILQuotationProcessor userToQuotationProcessor(UserDetails userDetails) {
        boolean hasQuotationProcessorRole = hasIndividualLifeQuotationProcessorRole(userDetails.getAuthorities());
        if (!hasQuotationProcessorRole) {
            throw new AuthorizationServiceException("User does not have Individual Life Quotation processor(ROLE_QUOTATION_PROCESSOR) authority");
        }
        return new ILQuotationProcessor(userDetails.getUsername());
    }

}
