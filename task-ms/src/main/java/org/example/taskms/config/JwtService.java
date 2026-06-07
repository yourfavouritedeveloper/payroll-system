package org.example.taskms.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class    JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;


    public String generateAccessToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, getSecretKey())
                .compact();
    }



    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, getSecretKey())
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isExpired(token));

    }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        Claims claims = extractClaims(token);
        return claims.getExpiration();
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getBody();
    }

    private Key getSecretKey() {
        byte[] keyBytes = Hex.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
