package com.luv2code.doan.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        log.error("Error logging in: " + e.getMessage());
        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", new Date().getTime());
        body.put("status", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.put("code", HttpStatus.UNAUTHORIZED.value());
        body.put("msg", e.getMessage());
        body.put("result", 0);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
