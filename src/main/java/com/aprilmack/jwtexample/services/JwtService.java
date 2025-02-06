package com.aprilmack.jwtexample.services;

import com.aprilmack.jwtexample.services.model.TokenModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time-ms}")
    private long jwtExpiration;

    private Clock clock;

    @Autowired
    public JwtService(final Clock clock) {
        this.clock = clock;
    }

    public TokenModel generateToken(final UserDetails userDetails) {
        final Instant expiresAt = Instant.now(clock).plusMillis(jwtExpiration);
        return TokenModel.builder()
                .token(buildToken(Collections.emptyMap(), userDetails, expiresAt))
                .expiresAt(expiresAt)
                .build();
    }

    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) &&
                !extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private String buildToken(
            final Map<String, Object> extraClaims,
            final UserDetails userDetails,
            final Instant expiresAt
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now(clock)))
                .setExpiration(Date.from(expiresAt))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(final String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
