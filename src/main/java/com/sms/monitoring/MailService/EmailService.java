package com.sms.monitoring.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sms.monitoring.API.LoginRequest;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService{
    private static final Logger logger = LoggerFactory.getLogger(LoginRequest.class);
    @Autowired
    private JavaMailSender mailSender; 
    public void sendSimpleEmail(String toEmail,
                                String subject,
                                String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
 
        // message.setFrom("fromemail@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);

        // System.out.println("Mail Send...");
        // logger.info(" MAIL SEND....: {}");


    }

}
