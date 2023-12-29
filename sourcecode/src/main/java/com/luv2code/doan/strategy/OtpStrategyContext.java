package com.luv2code.doan.strategy;

import com.luv2code.doan.entity.User;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public class OtpStrategyContext {

    private OtpSendingStrategy otpSendingStrategy;

    public OtpStrategyContext() {
    }

    public OtpStrategyContext(OtpSendingStrategy otpSendingStrategy){
        this.otpSendingStrategy = otpSendingStrategy;
    }


    public void sendOtp(User user) throws UnsupportedEncodingException, MessagingException {
        otpSendingStrategy.sendOtp(user);
    }
}
