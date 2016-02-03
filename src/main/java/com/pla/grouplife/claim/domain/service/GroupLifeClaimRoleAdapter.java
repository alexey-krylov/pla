package com.pla.grouplife.claim.domain.service;

import com.pla.grouplife.claim.domain.model.GLClaimApprover;
import com.pla.grouplife.claim.domain.model.GLClaimProcessor;
import com.pla.grouplife.claim.domain.model.GLClaimRegistrationProcessor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static com.pla.sharedkernel.util.RolesUtil.hasGroupLifeClaimApproverRole;
import static com.pla.sharedkernel.util.RolesUtil.hasGroupLifeClaimProcessorRole;
import static com.pla.sharedkernel.util.RolesUtil.hasGroupLifeClaimRegistrationProcessorRole;
/**
 * Created by ak
 */

@Component
public class GroupLifeClaimRoleAdapter {
    public GLClaimProcessor userToGLClaimProcessor(UserDetails userDetails) {

        boolean hasClaimProcessorRole = hasGroupLifeClaimProcessorRole(userDetails.getAuthorities());
        if (!hasClaimProcessorRole) {
            throw new AuthorizationServiceException("User does not have Claim processor(ROLE_GROUP_LIFE_CLAIM_PROCESSOR) authority");
        }
        return new GLClaimProcessor(userDetails.getUsername());
    }
/*
    public GLClaimSettlementProcessor userToGLClaimSettlementProcessor(UserDetails userDetails) {

        boolean hasClaimProcessorRole = hasGroupLifeClaimProcessorRole(userDetails.getAuthorities());
        if (!hasClaimProcessorRole) {
            throw new AuthorizationServiceException("User does not have Claim Settlement  processor(ROLE_GROUP_LIFE_CLAIM_PROCESSOR) authority");
        }
        return new GLClaimSettlementProcessor(userDetails.getUsername());
    }
    */
public GLClaimRegistrationProcessor userToGLClaimRegistrationProcessor(UserDetails userDetails) {

    boolean hasClaimRegistrationProcessorRole = hasGroupLifeClaimRegistrationProcessorRole(userDetails.getAuthorities());
    if (!hasClaimRegistrationProcessorRole) {
        throw new AuthorizationServiceException("User does not have Claim Registration Processor(ROLE_GROUP_LIFE_CLAIM_REGISTRATION_PROCESSOR) authority");
    }
    return new GLClaimRegistrationProcessor(userDetails.getUsername());
}


    public GLClaimApprover userToClaimApprover(UserDetails userDetails) {

        boolean hasClaimApproverRole = hasGroupLifeClaimApproverRole(userDetails.getAuthorities());
        if (!hasClaimApproverRole) {
            throw new AuthorizationServiceException("User does not have Claim approver(ROLE_GROUP_LIFE_CLAIM_APPROVER) authority");
        }
        return new GLClaimApprover(userDetails.getUsername());
    }

}
