package org.nthdimenzion.security.service;

import com.google.common.collect.Lists;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author: Samir
 * @since 1.0 13/02/2015
 */
//@Service
//@Profile("dev")
public class StubUserService implements UserDetailsService {

    @Value("${login.username}")
    private String userName;

    @Value("${login.password}")
    private String password;

    StubUserService() {
    }

    @Override
    public UserDetails loadUserByUsername(String userName) {
        if(UtilValidator.isEmpty(userName) || UtilValidator.isEmpty(this.userName) || !userName.equals(this.userName)){
            throw new UsernameNotFoundException("User Name "+ userName +"Not Found");
        }
        UserLoginDetailDto userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto(this.userName, password);
        userLoginDetailDto = userLoginDetailDto.populateAuthorities(Lists.newArrayList("ROLE_ADMIN"));
        return userLoginDetailDto;
    }
}
