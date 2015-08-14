/*
 * Copyright (c) 3/10/15 3:23 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.util;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
public class RolesUtilUnitTest {


    private List<SimpleGrantedAuthority> authorities = Lists.newArrayList();

    @Test
    public void shouldReturnTrueForAdminRole() {
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        boolean isAdmin = RolesUtil.hasAdminRole(authorities);
        assertTrue(isAdmin);
    }

    @Test
    public void shouldReturnFalseForAdminRole() {
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        boolean isAdmin = RolesUtil.hasAdminRole(authorities);
        assertFalse(isAdmin);
    }
}
