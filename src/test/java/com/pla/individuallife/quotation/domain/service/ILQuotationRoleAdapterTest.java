package com.pla.individuallife.quotation.domain.service;

import com.google.common.collect.Lists;
import com.pla.individuallife.quotation.domain.model.ILQuotationProcessor;
import com.pla.sharedkernel.util.RolesUtil;
import org.junit.Before;
import org.junit.Test;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Karunakar on 05/15/2015.
 */
public class ILQuotationRoleAdapterTest {

    UserLoginDetailDto userLoginDetailDto;

    @Before
    public void setUp() {
        userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
    }

    @Test
    public void shouldReturnQuotationProcessorWhenUserHasQuotationProcessorRole() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(RolesUtil.INDIVIDUAL_LIFE_QUOTATION_PROCESSOR_ROLE);
        List<SimpleGrantedAuthority> authorities = Lists.newArrayList();
        authorities.add(simpleGrantedAuthority);
        userLoginDetailDto.setAuthorities(authorities);
        RoleAdapter ilquotationRoleAdapter = new RoleAdapter();
        ILQuotationProcessor ilQuotationProcessor = ilquotationRoleAdapter.userToQuotationProcessor(userLoginDetailDto);
        assertNotNull(ilQuotationProcessor);
    }

    @Test(expected = AuthorizationServiceException.class)
    public void shouldReturnAuthorizationExceptionUserHasNotAdminRole() {
        UserLoginDetailDto userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        List<SimpleGrantedAuthority> authorities = Lists.newArrayList();
        authorities.add(simpleGrantedAuthority);
        userLoginDetailDto.setAuthorities(authorities);
        RoleAdapter ilquotationRoleAdapter = new RoleAdapter();
        ilquotationRoleAdapter.userToQuotationProcessor(userLoginDetailDto);
    }
}
