package com.pla.sharedkernel.service;

import com.pla.publishedlanguage.contract.IAuthenticationFacade;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Author - Mohan Sharma Created on 1/27/2016.
 */
@Component(value = "authenticationFacade")
public class AuthenticationFacade implements IAuthenticationFacade {
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
