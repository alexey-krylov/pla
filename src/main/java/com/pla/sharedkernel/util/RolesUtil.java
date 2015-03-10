/*
 * Copyright (c) 3/9/15 11:25 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.util;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
public class RolesUtil {


    public static boolean hasAdminRole(Collection<? extends GrantedAuthority> authorities) {
        long count = hasRole("ROLE_ADMIN", authorities);
        boolean isAdmin = (count == 1);
        return isAdmin;
    }

    private static long hasRole(final String role, final Collection<? extends GrantedAuthority> authorities) {
        long count = authorities.stream().filter(new Predicate<GrantedAuthority>() {
            @Override
            public boolean test(GrantedAuthority grantedAuthority) {
                return role.equals(grantedAuthority.getAuthority());
            }
        }).count();
        return count;
    }
}
