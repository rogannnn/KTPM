package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.User;
import com.luv2code.doan.service.EmailService;
import com.luv2code.doan.service.OTPService;
import com.luv2code.doan.strategy.OtpSendingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements OtpSendingStrategy, EmailService {
    private final OTPService otpService;
    private final JavaMailSender emailSender;
    private final org.thymeleaf.spring5.SpringTemplateEngine templateEngine;
    private String senderEmail = "fannaruto09@gmail.com";

    public EmailServiceImpl(OTPService otpService, JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.otpService = otpService;
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendOtp(User user) throws UnsupportedEncodingException, MessagingException {
        String subject = "Vui lòng xác thực tài khoản";
        String senderName = "ShopWise Team";

        String code = String.valueOf(otpService.generateOTP(user.getEmail() + OTPService.KEY_FORGOT));

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("code", code);
        model.put("name", user.getName());
        Context context = new Context();
        context.setVariables(model);

        String html = templateEngine.process("email/verify-code", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(senderEmail, senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public void sendVerificationEmail(User user)
            throws UnsupportedEncodingException, MessagingException {
        String subject = "Vui lòng xác thực tài khoản";
        String senderName = "ShopWise Team";

        String code = String.valueOf(otpService.generateOTP(user.getEmail()));

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("code", code);
        model.put("name", user.getName());
        Context context = new Context();
        context.setVariables(model);

        String html = templateEngine.process("email/verify-code", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(senderEmail, senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(html, true);
        emailSender.send(message);
    }

    @Override
    public void sendEmailForgotPassword(User user)
            throws UnsupportedEncodingException, MessagingException {
        String subject = "Vui lòng xác thực tài khoản";
        String senderName = "ShopWise Team";

        String code = String.valueOf(otpService.generateOTP(user.getEmail() + OTPService.KEY_FORGOT));

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("code", code);
        model.put("name", user.getName());
        Context context = new Context();
        context.setVariables(model);

        String html = templateEngine.process("email/verify-code", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(senderEmail, senderName);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(html, true);
        emailSender.send(message);
    }


}
