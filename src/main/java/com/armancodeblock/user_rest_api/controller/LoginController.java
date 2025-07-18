package com.armancodeblock.user_rest_api.controller;

import com.armancodeblock.user_rest_api.UserRestApiApplication;
import com.armancodeblock.user_rest_api.enity.AccountCredentials;
import com.armancodeblock.user_rest_api.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
@PostMapping("/login")
    public ResponseEntity<?> getToken(@RequestBody AccountCredentials credentials){
    logger.info("Login attempt for user {}", credentials.getUsername());
    UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(credentials.getUsername(),credentials.getPassword());
    logger.warn("Creds Object: " + creds);

     Authentication auth = authenticationManager.authenticate(creds);

     //Generate JWT token
    String jwtToken = jwtService.getToken(auth.getName());
    return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION,"Bearer" + jwtToken)
            .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"AUTHORIZATION")
            .build();
}
}
