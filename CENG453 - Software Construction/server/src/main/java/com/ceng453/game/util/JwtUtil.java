package com.ceng453.game.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(Authentication user){
        Integer expirationDay = 7;
        return Jwts.builder()
                .setSubject(user.getName())
                .claim("authorities",getAuthorities(user))
                .setIssuedAt(new Date())
                .setExpiration(calculateExpirationDate(expirationDay))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
    private List<String> getAuthorities(Authentication user){
       return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private Date calculateExpirationDate(Integer expirationDay){
        Instant expirationTime= LocalDate.now()
                .plusDays(expirationDay)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();
        return Date.from(expirationTime);
    }
    public String extractUsername(String jwtToken, String secretKey){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }
}
