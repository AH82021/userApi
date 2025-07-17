package com.armancodeblock.user_rest_api.service;

import com.armancodeblock.user_rest_api.enity.AuthUser;
import com.armancodeblock.user_rest_api.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// @Service  // Temporarily disabled to avoid conflict with InMemoryUserDetailsManager
public class AuthUserService implements UserDetailsService {
    @Autowired
    private AuthUserRepository authUserRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AuthUser> authUser = authUserRepository.findByUsername(username);
        User.UserBuilder builder= null;
        if(authUser.isPresent()){
            AuthUser currentUser = authUser.get();
            builder = User.withUsername(username);
            builder.password(currentUser.getPassword());
            builder.roles(currentUser.getRole());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return builder.build();
    }
}
