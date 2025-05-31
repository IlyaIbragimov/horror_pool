package com.social.horror_pool.security.jwt;

import com.social.horror_pool.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public JwtAuthenticationFilter(final JwtTokenProvider tokenProvider, final UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtTokenProvider = tokenProvider;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        this.log.debug("JWTAuthenticationFilter called for URI: {}", request.getRequestURI());

        try {
            String token = getJwtFromRequest(request);

            if (token != null && this.jwtTokenProvider.validateToken(token)) {
                String username = this.jwtTokenProvider.getUsernameFromToken(token);

                UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

                this.log.debug("Authenticated user: {}", username);
            }
        } catch (Exception e) {
            this.log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.trim().startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
        String jwtToken = this.jwtTokenProvider.getJwtFromCookie(request);
        this.log.debug("JWT token: {}", jwtToken);
        return jwtToken;
    }
}
