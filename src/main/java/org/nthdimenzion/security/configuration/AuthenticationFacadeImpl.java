package org.nthdimenzion.security.configuration;

import org.nthdimenzion.security.service.IAuthenticationFacade;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Author - Mohan Sharma Created on 1/27/2016.
 */
@Configuration
public class AuthenticationFacadeImpl implements IAuthenticationFacade {
    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
