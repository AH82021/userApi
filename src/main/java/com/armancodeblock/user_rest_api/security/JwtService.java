package com.armancodeblock.user_rest_api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;


import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final long EXPIRATION_TIME= 86400000;// 1 day in milliseconds
    private  final String PREFIX = "Bearer";
    static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public String getToken(String username){
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                .signWith(key)
                .compact();
        return  token;
    }

    // Method to validate the token and extract it form request header

    public String getAuthUser(HttpServletRequest request){
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(token != null){
            String user = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody()
                    .getSubject();
            if(user != null && !user.isEmpty()) return  user;
        }
        return null; // Return null if token is invalid or not present
    }
}
