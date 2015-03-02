/*
 * Copyright (c) 1/23/15 5:07 PM.Nth Dimenzion, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.nthdimenzion.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: Samir
 * @since 1.0 23/01/2015
 */
@Component
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private enum AuthenticationFailureErrorCodes {
        BAD_CREDENTIALS {
            @Override
            String getDescription() {
                return "Invalid username or password.";
            }
        },
        ACCOUNT_LOCKED {
            @Override
            String getDescription() {
                return "Account is locked.Contact Administrator.";
            }
        },
        ACCOUNT_DISABLED {
            @Override
            String getDescription() {
                return "Account is disabled.Contact Administrator.";
            }
        }, UNKNOWN_AUTHENTICATION_EXCEPTION {
            @Override
            String getDescription() {
                return "Reason Unknown,try after some time.";
            }
        }, MAX_SESSION_REACHED {
            @Override
            String getDescription() {
                return "You already have an active session.End the session to login again.";
            }
        };

        abstract String getDescription();
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response, AuthenticationException authenticationException)
            throws IOException, ServletException {


        if (authenticationException instanceof BadCredentialsException) {
            if (isValidUserName(authenticationException)) {
            }
        }
        super.onAuthenticationFailure(request, response, authenticationException);
    }

    private String getUserName(AuthenticationException authenticationException) {
        return authenticationException.getAuthentication().getName();
    }

    private boolean isValidUserName(AuthenticationException authenticationException) {
        return authenticationException.getExtraInformation() != null;

    }

    private AuthenticationFailureErrorCodes transformExceptionIntoErrorCode(AuthenticationException
                                                                                    authenticationException) {
        if (authenticationException instanceof BadCredentialsException || authenticationException instanceof
                UsernameNotFoundException) {
            return AuthenticationFailureErrorCodes.BAD_CREDENTIALS;
        } else if (authenticationException instanceof DisabledException) {
            return AuthenticationFailureErrorCodes.ACCOUNT_DISABLED;
        } else if (authenticationException instanceof LockedException) {
            return AuthenticationFailureErrorCodes.ACCOUNT_LOCKED;
        } else if (authenticationException instanceof SessionAuthenticationException) {
            return AuthenticationFailureErrorCodes.MAX_SESSION_REACHED;
        }
        return AuthenticationFailureErrorCodes.UNKNOWN_AUTHENTICATION_EXCEPTION;
    }
}
