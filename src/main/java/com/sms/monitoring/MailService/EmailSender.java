package com.sms.monitoring.MailService;

import org.springframework.stereotype.Component;

import com.sms.monitoring.Constants;
import com.sms.monitoring.Massage;
 
@Component
public class EmailSender {
    private final EmailService emailService;

    public EmailSender(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendCustomEmail() {
        String to = Constants.MAILTO;
        String subject = Massage.mailSubject;
        String text = Massage.mailBody; ;

        emailService.sendSimpleEmail(to, subject, text);
    }
    
}
