package com.luv2code.doan.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.luv2code.doan.bean.Token;
import com.luv2code.doan.dto.UserDto;
import com.luv2code.doan.entity.Role;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.DuplicateException;
import com.luv2code.doan.exceptions.UserNotFoundException;
import com.luv2code.doan.request.*;
import com.luv2code.doan.response.BaseResponse;
import com.luv2code.doan.response.SignupResponse;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.service.EmailService;
import com.luv2code.doan.service.OTPService;
import com.luv2code.doan.service.UserService;
import com.luv2code.doan.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class AuthRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final OTPService otpService;
    private final String defaultAvatar = "https://res.cloudinary.com/dmriwkfll/image/upload/v1656155271/f0w0qgwpe8wxo1ceafhm.jpg";

    @RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestBody body, HttpServletRequest request) throws DuplicateException, UserNotFoundException {
        if (userService.existsByEmail(body.getEmail())) {
            throw new DuplicateException("This email address is already being used");
        }
        else if(userService.existsByPhone(body.getPhone())) {
            throw new DuplicateException("This phone number is already being used");
        }

        log.info("AuthController body send email: " + body.getEmail());

        User user = new User();

        String hashPass = passwordEncoder.encode(body.getPassword());
        user.setPassword(hashPass);
        user.setIsActive(true);
        user.setEmail(body.getEmail());
        user.setName(body.getName());
        user.setPhone(body.getPhone());

        user.setAvatar(defaultAvatar);
        user.setRegistrationDate(new Date());
        Role role = userService.getRole("USER");
        user.getRoles().add(role);
        log.info(user.toString());
        userService.saveUser(user);

        TokenUtils tokenUtils = new TokenUtils();
        Token token = tokenUtils.generateToken(user, request);

        UserDto userDto = new UserDto(user);
        SignupResponse result = new SignupResponse(1, "Your account has been created successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.CREATED.getReasonPhrase(), HttpStatus.CREATED.value(),
                userDto, token.getAccessToken(), token.getRefreshToken());

        return new ResponseEntity(result, HttpStatus.CREATED);
    }




    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) throws UserNotFoundException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUserByID(userDetails.getId());


        TokenUtils tokenUtils = new TokenUtils();
        Token token = tokenUtils.generateToken(user, request);

        UserDto userDto = new UserDto(user);

        SignupResponse result = new SignupResponse(1, "Your account has been logged successfully!",
                request.getMethod(),new Date().getTime(),HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                userDto, token.getAccessToken(), token.getRefreshToken());
        return new ResponseEntity(result, HttpStatus.OK);

    }


    @GetMapping("/token/refresh")
    public ResponseEntity<Object> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try{
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                TokenUtils tokenUtils = new TokenUtils();

                DecodedJWT decodedJWT = tokenUtils.decodedToken(refreshToken);

                String email = decodedJWT.getSubject();

                User user = userService.getUserByEmail(email);

                Token token = tokenUtils.refreshAccessToken(user, refreshToken, request);

                UserDto userDto = new UserDto(user);

                Map<String, Object> tokens = new LinkedHashMap<>();
                tokens.put("result", 1);
                tokens.put("method", "GET");
                tokens.put("msg","Your token has been refresh successfully!");
                tokens.put("timestamp", new Date().getTime());
                tokens.put("accessToken", token.getAccessToken());
                tokens.put("refreshToken", token.getRefreshToken());
                tokens.put("data", userDto);
                tokens.put("status", HttpStatus.OK.getReasonPhrase());
                tokens.put("code", HttpStatus.OK.value());
                return new ResponseEntity(tokens, HttpStatus.OK);

            }
            catch (Exception e){
                response.setHeader("error", e.getMessage());
                Map<String, Object> errors = new HashMap<>();
                errors.put("method", request.getMethod());
                errors.put("timestamp", new Date().getTime());
                errors.put("status", HttpStatus.BAD_REQUEST.getReasonPhrase());
                errors.put("msg", "refresh_token");
                errors.put("code", HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);

            }
        }
        else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

    @PostMapping("/forgot/verify")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ChangePasswordForgotRequest changePasswordForgotRequest, HttpServletRequest request) throws UserNotFoundException {

        User user = userService.getUserByEmail(changePasswordForgotRequest.getEmail());


        if(changePasswordForgotRequest.getOtp() >= 0) {
            int otpCache = otpService.getOtp(user.getEmail() + OTPService.KEY_FORGOT_VERIFY);

            if(otpCache == changePasswordForgotRequest.getOtp()) {
                otpService.clearOTP(user.getEmail() + OTPService.KEY_FORGOT_VERIFY);


                String hashPass = passwordEncoder.encode(changePasswordForgotRequest.getNewPassword());
                user.setPassword(hashPass);
                userService.saveUser(user);


                BaseResponse result = new BaseResponse(1, "Change password successfully!",
                        request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value()
                );
                return new ResponseEntity(result, HttpStatus.OK);
            }
            else {
                BaseResponse result = new BaseResponse(0, "OTP is not valid!",
                        request.getMethod(), new Date().getTime(), HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value()
                );
                return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
            }

        }
        else {
            BaseResponse result = new BaseResponse(0, "OTP is not valid!",
                    request.getMethod(), new Date().getTime(), HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value()
            );
            return new ResponseEntity(result, HttpStatus.BAD_REQUEST);
        }

    }



}
