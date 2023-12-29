package com.luv2code.doan.strategy;

import com.luv2code.doan.entity.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface OtpSendingStrategy {
    void sendOtp(User user) throws UnsupportedEncodingException, MessagingException;
}
