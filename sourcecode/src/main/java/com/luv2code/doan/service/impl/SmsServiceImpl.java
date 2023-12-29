package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.User;
import com.luv2code.doan.service.OTPService;
import com.luv2code.doan.strategy.OtpSendingStrategy;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements OtpSendingStrategy {

    private final OTPService otpService;

    private String ACCOUNT_SID = "AC652aa7134ca9b693783a43922a7e5f31";

    private String AUTH_TOKEN = "cae57ae717fe6ebbe244f090f694f903";

    private String FROM_PHONE_NUMBER = "+16073501496";


    public SmsServiceImpl(OTPService otpService) {
        this.otpService = otpService;
    }

    @Override
    public void sendOtp(User user) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String code = String.valueOf(otpService.generateOTP(user.getEmail() + OTPService.KEY_FORGOT));
        System.out.println(code);

        String message = "Your OTP is " + code;

        Message sms = Message.creator(
                new PhoneNumber("+84388820835"),
                new PhoneNumber(FROM_PHONE_NUMBER),
                message)
                .create();

    }

}
