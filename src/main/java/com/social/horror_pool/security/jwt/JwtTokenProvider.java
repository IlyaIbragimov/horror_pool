package com.social.horror_pool.security.jwt;

import com.social.horror_pool.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.cookieName}")
    private String cookieName;

    private Key key;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.jwtSecret));
    }

    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + this.jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(this.key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) this.key)
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) this.key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            log.error("Invalid JWT signature.");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token.");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token.");
        } catch (IllegalArgumentException ex) {
            log.error("Empty JWT token.");
        }
        return false;
    }

    // Cookie things

    public ResponseCookie generateCookie(CustomUserDetails customUserDetails) {
        String jwtToken = generateTokenFromUsername(customUserDetails.getUsername());
        return ResponseCookie.from(this.cookieName, jwtToken)
                .path("/")
                .maxAge(24*60*60)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie generateCleanCookie() {
        return ResponseCookie.from(this.cookieName, null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    public String getJwtFromCookie(HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals(this.cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
