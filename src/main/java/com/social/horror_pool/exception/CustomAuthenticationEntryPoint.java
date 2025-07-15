package com.social.horror_pool.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        logger.debug("Authentification error : {}",authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        final Map<String,Object> body = new HashMap<>();
        body.put("message","You are not authorized to perform this action. Please sign in");
        body.put("status",HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error",authException.getMessage());
        body.put("path",request.getRequestURI());
        body.put("timestamp", Instant.now().toString());

       final ObjectMapper mapper = new ObjectMapper();
       mapper.writeValue(response.getOutputStream(),body);
    }
}
