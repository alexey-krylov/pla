/*
 * Copyright (c) 3/5/15 6:00 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.Admin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
public class AdminRoleAdapterTest {


    UserLoginDetailDto userLoginDetailDto;

    @Before
    public void setUp() {
        userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
    }

    @Test
    public void should_return_Admin_when_user_has_adminRole() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        List<GrantedAuthority> authorities = Lists.newArrayList();
        authorities.add(simpleGrantedAuthority);
        userLoginDetailDto.setAuthorities(authorities);
        AdminRoleAdapter adminRoleAdapter = new AdminRoleAdapter();
        Admin admin = adminRoleAdapter.userToAdmin(userLoginDetailDto);
        assertNotNull(admin);
    }

    @Test(expected = AuthorizationServiceException.class)
    public void should_return_Authorization_Exception_user_has_not_adminRole() {
        UserLoginDetailDto userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> authorities = Lists.newArrayList();
        authorities.add(simpleGrantedAuthority);
        userLoginDetailDto.setAuthorities(authorities);
        AdminRoleAdapter adminRoleAdapter = new AdminRoleAdapter();
        adminRoleAdapter.userToAdmin(userLoginDetailDto);
    }
}
