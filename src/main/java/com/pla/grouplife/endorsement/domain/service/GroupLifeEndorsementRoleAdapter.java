package com.pla.grouplife.endorsement.domain.service;

import com.pla.grouplife.endorsement.domain.model.GLEndorsementApprover;
import com.pla.grouplife.endorsement.domain.model.GLEndorsementProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasGroupLifeEndorsementApproverRole;
import static com.pla.sharedkernel.util.RolesUtil.hasGroupLifeEndorsementProcessorRole;

/**
 * Created by Samir on 8/27/2015.
 */
@Component
public class GroupLifeEndorsementRoleAdapter {

    public GLEndorsementProcessor userToEndorsementProcessor(UserDetails userDetails) {

        boolean haspEndorsementProcessorRole = hasGroupLifeEndorsementProcessorRole(userDetails.getAuthorities());
        if (!haspEndorsementProcessorRole) {
            throw new AuthorizationServiceException("User does not have Endorsement processor(ROLE_GROUP_LIFE_ENDORSEMENT_PROCESSOR) authority");
        }
        return new GLEndorsementProcessor(userDetails.getUsername());
    }

    public GLEndorsementApprover userToEndorsmentApprover(UserDetails userDetails) {

        boolean hasEndorsementApproverRole = hasGroupLifeEndorsementApproverRole(userDetails.getAuthorities());
        if (!hasEndorsementApproverRole) {
            throw new AuthorizationServiceException("User does not have Endorsement approver(ROLE_GROUP_LIFE_ENDORSEMENT_APPROVER) authority");
        }
        return new GLEndorsementApprover(userDetails.getUsername());
    }
}
