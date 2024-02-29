package com.sms.monitoring.MailService;

import com.sms.monitoring.Massage;

public class SendMail {
    String api;
    public SendMail(String api){
        this.api=api;
    }
    // Method to send an email
public void sendEmail(String body1, String body2) {
   
    System.out.println("send Email "+api+body1+body2);
    Massage.mailBody = Massage.mailBody+" '"+api+"' "+body1+" "+body2+"\n";

    }
}
