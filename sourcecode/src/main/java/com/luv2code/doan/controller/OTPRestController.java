package com.luv2code.doan.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.luv2code.doan.entity.User;
import com.luv2code.doan.exceptions.*;
import com.luv2code.doan.principal.UserPrincipal;
import com.luv2code.doan.request.ForgotPasswordRequest;
import com.luv2code.doan.request.ForgotPasswordSMSRequest;
import com.luv2code.doan.request.OTPRequest;
import com.luv2code.doan.request.VerifyForgotPasswordRequest;
import com.luv2code.doan.response.BaseResponse;
import com.luv2code.doan.response.OtpResponse;
import com.luv2code.doan.service.EmailService;
import com.luv2code.doan.service.OTPService;
import com.luv2code.doan.service.UserService;
import com.luv2code.doan.service.impl.EmailServiceImpl;
import com.luv2code.doan.service.impl.SmsServiceImpl;
import com.luv2code.doan.strategy.OtpStrategyContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Slf4j
public class OTPRestController {


    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPService otpService;

    @Autowired
    private org.thymeleaf.spring5.SpringTemplateEngine templateEngine;

    @Autowired
    private JavaMailSender emailSender;



    @RequestMapping(value = "/generateOTP", method = RequestMethod.GET)
    @ResponseBody
    private ResponseEntity<?> generateOTP(Authentication authentication, HttpServletRequest request) throws UserNotFoundException, UnsupportedEncodingException, MessagingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();


        User user = userService.getUserByEmail(userPrincipal.getEmail());

        emailService.sendVerificationEmail(user);


        BaseResponse result = new BaseResponse(1, "Generate OTP successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value()
        );

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/validateOTP", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private ResponseEntity<?> validateOTP(Authentication authentication, @Valid @RequestBody OTPRequest otpRequest, HttpServletRequest request) throws NotFoundException, UserNotFoundException, AddressNotFoundException, CartMoreThanProductInStock, ProductNotFoundException, JsonProcessingException, UnsupportedEncodingException, MessagingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();


        User user = userService.getUserByEmail(userPrincipal.getEmail());

        if(otpRequest.getOtp() >= 0) {
            int otpCache = otpService.getOtp(user.getEmail());
            if(otpCache == otpRequest.getOtp()) {
                otpService.clearOTP(user.getEmail());

                BaseResponse result = new BaseResponse(1, "Validate OTP successfully!",
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

    @RequestMapping(value = "/sms", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> sendSms(@Valid @RequestBody ForgotPasswordSMSRequest forgotPasswordSMSRequest, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException, UserNotFoundException, NotFoundOtpStrategy {
        User user = userService.getUserByPhone(forgotPasswordSMSRequest.getPhone());


        if(user == null) {
            throw new UserNotFoundException("Could not find any user with phone " + forgotPasswordSMSRequest.getPhone());
        }

        OtpStrategyContext otpStrategyContext = new OtpStrategyContext(new SmsServiceImpl(otpService));
        otpStrategyContext.sendOtp(user);

        BaseResponse result = new BaseResponse(1, "Generate OTP successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value()
        );
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/forgot/generateOTP", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private ResponseEntity<?> forgotGenerateOTP(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest, HttpServletRequest request) throws UserNotFoundException, UnsupportedEncodingException, MessagingException, NotFoundOtpStrategy {

        User user = userService.getUserByEmail(forgotPasswordRequest.getEmail());

        OtpStrategyContext otpStrategyContext = new OtpStrategyContext(new EmailServiceImpl(otpService, emailSender, templateEngine));
        otpStrategyContext.sendOtp(user);

        BaseResponse result = new BaseResponse(1, "Generate OTP successfully!",
                request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value()
        );

        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/forgot/validateOTP", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private ResponseEntity<?> validateOTP(@Valid @RequestBody VerifyForgotPasswordRequest verifyForgotPasswordRequest, HttpServletRequest request) throws NotFoundException, UserNotFoundException, AddressNotFoundException, CartMoreThanProductInStock, ProductNotFoundException, JsonProcessingException, UnsupportedEncodingException, MessagingException {


        User user = userService.getUserByEmail(verifyForgotPasswordRequest.getEmail());

        if(verifyForgotPasswordRequest.getOtp() >= 0) {
            int otpCache = otpService.getOtp(user.getEmail() + OTPService.KEY_FORGOT);
            if(otpCache == verifyForgotPasswordRequest.getOtp()) {
                otpService.clearOTP(user.getEmail() + OTPService.KEY_FORGOT);

                int newOtpVerify = otpService.generateOTP(user.getEmail() + OTPService.KEY_FORGOT_VERIFY);
                OtpResponse result = new OtpResponse(1, "Validate OTP successfully!",
                        request.getMethod(), new Date().getTime(), HttpStatus.OK.getReasonPhrase(), HttpStatus.OK.value(),
                        user.getEmail(), newOtpVerify
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
