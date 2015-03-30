/*
 * Copyright (c) 1/23/15 2:46 PM.Nth Dimenzion, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package org.nthdimenzion.security.service;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author: Samir
 * @since 1.0 23/01/2015
 */
@Service
public class UserService implements UserDetailsService {

    @Value("${spring.smeServer.${spring.profiles.active}.url}")
    private String serverUrl;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Preconditions.checkNotNull(serverUrl);
        RestTemplate restTemplate = new RestTemplate();
        String userDetailUrl = serverUrl + "/getuserdetail?username=admin";
        UserLoginDetailDto userLoginDetailDto = restTemplate.getForObject(userDetailUrl, UserLoginDetailDto.class);
        Preconditions.checkNotNull(userLoginDetailDto);
        userLoginDetailDto = userLoginDetailDto.populateAuthorities(userLoginDetailDto.getPermissions());
        return userLoginDetailDto;
    }

}
