/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.sharedkernel.util.RolesUtil;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Component
public class AdminRoleAdapter {

    private Logger logger = LoggerFactory.getLogger(AdminRoleAdapter.class);

    public Admin userToAdmin(UserDetails userDetails) {
        if (logger.isDebugEnabled()) {
            logger.debug("User details received" + userDetails);
        }
        boolean hasAdminRole = RolesUtil.hasAdminRole(userDetails.getAuthorities());
        if (!hasAdminRole) {
            logger.error("user does not have ROLE_ADMIN");
            throw new AuthorizationServiceException("User does not have ROLE_ADMIN authority");
        }
        Admin admin = new Admin(userDetails.getUsername());
        return admin;
    }

}
