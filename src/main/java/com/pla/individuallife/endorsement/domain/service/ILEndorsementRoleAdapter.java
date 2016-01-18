package com.pla.individuallife.endorsement.domain.service;

import com.pla.individuallife.endorsement.domain.model.ILEndorsementApprover;
import com.pla.individuallife.endorsement.domain.model.ILEndorsementProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeEndorsementApproverRole;
import static com.pla.sharedkernel.util.RolesUtil.hasIndividualLifeEndorsementProcessorRole;

/**
 * Created by Raghu Bandi on 8/27/2015.
 */
@Component
public class ILEndorsementRoleAdapter {

    public ILEndorsementProcessor userToEndorsementProcessor(UserDetails userDetails) {

        boolean haspEndorsementProcessorRole = hasIndividualLifeEndorsementProcessorRole(userDetails.getAuthorities());
        if (!haspEndorsementProcessorRole) {
            throw new AuthorizationServiceException("User does not have Endorsement processor(ROLE_INDIVIDUAL_LIFE_ENDORSEMENT_PROCESSOR) authority");
        }
        return new ILEndorsementProcessor(userDetails.getUsername());
    }

    public ILEndorsementApprover userToEndorsmentApprover(UserDetails userDetails) {

        boolean hasEndorsementApproverRole = hasIndividualLifeEndorsementApproverRole(userDetails.getAuthorities());
        if (!hasEndorsementApproverRole) {
            throw new AuthorizationServiceException("User does not have Endorsement approver(ROLE_INDIVIDUAL_LIFE_ENDORSEMENT_APPROVER) authority");
        }
        return new ILEndorsementApprover(userDetails.getUsername());
    }
}
