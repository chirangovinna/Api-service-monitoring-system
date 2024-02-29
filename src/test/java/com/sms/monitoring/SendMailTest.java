package com.sms.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.sms.monitoring.MailService.SendMail;

import org.junit.Before;

public class SendMailTest {
    private SendMail sendMail;

    @Before
    public void setUp() {
        // Initialize SendMail instance with a dummy API value
        sendMail = new SendMail("dummy-api");
    }

    @org.junit.Test
    public void testSendEmail() {
        String body1 = "Hello, ";
        String body2 = "this is a test email.";

        // Call the method being tested
        sendMail.sendEmail(body1, body2);

        // Check if the mail body is updated as expected
        assertEquals("'dummy-api'" + body1 + body2 + "\n", Massage.mailBody);
    }
}
