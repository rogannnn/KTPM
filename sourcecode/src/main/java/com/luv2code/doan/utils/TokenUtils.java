package com.luv2code.doan.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.luv2code.doan.bean.Token;
import com.luv2code.doan.entity.User;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

public class TokenUtils {

    final static long TIME_EXP_ACCESS_TOKEN = 365L * 24 * 60 * 60 * 1000; // 7 days
    final static long TIME_EXP_REFRESH_TOKEN = 365L * 24 * 60 * 60 * 1000; // 60 days

    public Token generateToken(User user, HttpServletRequest request) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));
        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + TIME_EXP_ACCESS_TOKEN))
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles", user.getRoles().stream().map(roleUser -> "ROLE_".concat(roleUser.getName()).toUpperCase())
                        .collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + TIME_EXP_REFRESH_TOKEN))
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles", user.getRoles().stream().map(roleUser -> "ROLE_".concat(roleUser.getName()).toUpperCase())
                        .collect(Collectors.toList()))
                .sign(algorithm);

        return new Token(accessToken, refreshToken);
    }

    public Token refreshAccessToken(User user , String refreshToken,  HttpServletRequest request) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));
        String accessToken = JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + TIME_EXP_ACCESS_TOKEN)) //1 hour
                .withIssuer(request.getRequestURI().toString())
                .withClaim("roles", user.getRoles().stream().map(roleUser -> "ROLE_".concat(roleUser.getName()).toUpperCase())
                        .collect(Collectors.toList()))
                .sign(algorithm);
        return new Token(accessToken, refreshToken);
    }

    public DecodedJWT decodedToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }



}
