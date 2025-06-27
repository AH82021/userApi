package com.armancodeblock.user_rest_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.swing.plaf.PanelUI;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // basic security  username and password
     return   http
             .csrf(c->c.disable())
             .authorizeHttpRequests(auth-> auth
                             .requestMatchers(HttpMethod.GET,"/api/v1/users/**").permitAll()
                             .requestMatchers(HttpMethod.POST,"/api/v1/users").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT,"/api/v1/users/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE,"/api/v1/users/**").hasRole("ADMIN")
                             .anyRequest().authenticated()
                     )
                .httpBasic(Customizer.withDefaults())
             .build();
    }
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails admin = User.builder()
                .username("admin")
                .password(bCryptPasswordEncoder().encode("adminPass"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(bCryptPasswordEncoder().encode("userPass"))
                .roles("USER")
                .build();

        return  new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
