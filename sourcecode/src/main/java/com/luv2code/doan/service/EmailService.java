package com.luv2code.doan.service;

import com.luv2code.doan.entity.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;


public interface EmailService {

    public void sendVerificationEmail(User user)
            throws UnsupportedEncodingException, MessagingException;

    public void sendEmailForgotPassword(User user)
            throws UnsupportedEncodingException, MessagingException;
}
