package com.luv2code.doan.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.UserService;
import com.luv2code.doan.service.impl.UserServiceImpl;
import com.luv2code.doan.utils.TokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Arrays.stream;


public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Autowired
    private UserServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try{
                String token = authorizationHeader.substring("Bearer ".length());

                TokenUtils tokenUtils = new TokenUtils();
                DecodedJWT decodedJWT = tokenUtils.decodedToken(token);

                String username = decodedJWT.getSubject();

                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                stream(roles).forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role));
                });
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
            catch (Exception e){
                logger.error("Error logging in: " + e.getMessage());
                response.setHeader("error", e.getMessage());
                Map<String, Object> errors = new HashMap<>();
                errors.put("timestamp", new Date());
                errors.put("status", HttpStatus.UNAUTHORIZED.getReasonPhrase());
                errors.put("msg", e.getMessage());
                errors.put("method", request.getMethod());
                errors.put("code", HttpStatus.UNAUTHORIZED.value());
                errors.put("result", 0);

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), errors);
            }
        }
        else {
            filterChain.doFilter(request, response);
        }

    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        List<String> pathsToSkip = Arrays.asList("/api/auth", "/api/category", "/api/brand",
                "/api/product", "/api/poster",
                "/api/review/get",
                "/api/address/province", "/api/address/district", "/api/address/ward",
                "/api/otp/forgot/generateOTP",
                "/api/otp/forgot/validateOTP",
                "/v2/api-docs",
                "/configuration",
                "/swagger",
                "/webjars");
        return pathsToSkip.stream().anyMatch(path::startsWith);
    }
}
