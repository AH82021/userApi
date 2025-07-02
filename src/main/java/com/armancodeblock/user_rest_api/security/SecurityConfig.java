package com.armancodeblock.user_rest_api.security;

import com.armancodeblock.user_rest_api.service.AuthUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

// JWT (JSON Web Token)  method for representing claims securely between two parties.
//Steps
// 1. User login with username and password
// 2. Server validates the credentials and generates a JWT token, singing it with a secret key.
// 3. Client receives the token and includes it in the Authorization header of subsequent requests.
// 4. Server verifies the token's signature and extracts the claims to authenticate and authorize the user.
// 5. If the token is valid, the server processes the request; otherwise, it returns an error response.
// -- ex amazon.com. login (username,password)-> server validates credentials -> generates JWT token-> send token to client->
import javax.swing.plaf.PanelUI;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private AuthUserService authUserService;

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
                .password("$2y$10$VKlPOmlBxacyOtoGuASJuu6F0E4Gf/VfiWsVZSHlr3xHzjn9DQ68W")
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password("$2y$10$dc3e3saiBRuwAmihOB0cWe8cI./MVgKIrdq9uCukCnRXYoNgYR1e6")
                .roles("USER")
                .build();

        return  new InMemoryUserDetailsManager(admin, user);
    }
@Autowired
    public  void configGlobal(AuthenticationManagerBuilder auth) throws Exception {
         auth.userDetailsService(authUserService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
