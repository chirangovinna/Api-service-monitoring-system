package com.sms.monitoring.API;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sms.monitoring.Constants;
import com.sms.monitoring.ErrorHandel;
import com.sms.monitoring.MailService.SendMail;

public class CheckBalance {
    String apiname = "Check account balance API ";
    private static final Logger logger = LoggerFactory.getLogger(CheckBalance.class);

    private static final String API_URL = "https://e-sms.dialog.lk/api/v1/message-via-url/check/balance";
    private static final String ESMSQK_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTE5MjQsImlhdCI6MTY5MTEyMDM5NywiZXhwIjo0ODE1MzIyNzk3fQ.xsmRopTcUmz_AAxnhG_T00AmQvf7SRtcDcU29-6YCH0";

    public void checkBalance() throws URISyntaxException, IOException, InterruptedException {
        String requestId = Integer.toString(((int) java.time.Instant.now().getEpochSecond()))+ Integer.toString((new Random()).nextInt(100));
        
        String endpoint = API_URL + "?esmsqk=" + ESMSQK_KEY;

        HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(Constants.THRESHOLD_SECONDS)) // Set a timeout of 10 seconds TODO
        .build();

        Instant requestTime = Instant.now(); // Capture the current time before making the request

        logger.info("1,{},{},,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname,requestId);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(endpoint))
                .GET()
                .timeout(Duration.ofSeconds(Constants.THRESHOLD_SECONDS))
                .build();

        HttpResponse<String> response = null;
        Instant responseTime = null;

        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            responseTime = Instant.now();
        } catch (IOException | InterruptedException e) {
            // Handle the exception if something goes wrong while sending the request
            e.printStackTrace();
        }
        // Send email based on time difference
        SendMail mail = new SendMail(apiname);
        if (responseTime != null) {
            Duration timeDifference = Duration.between(requestTime, responseTime);
            long timeElapsedMillis = timeDifference.toMillis();
            // System.out.println("Campaign List Endpoint Time taken: " + timeElapsedMillis + " milliseconds");
            
            // Continue with the rest of your code for processing the response
            int statusCode = response.statusCode();
            String responseBody = response.body();
            // Log the unique ID, API name, and other details
            if (statusCode >= 200 && statusCode < 300) {
                logger.info("2,{},{},{} milliseconds ,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), apiname,timeElapsedMillis, requestId);
            } else {
                logger.info("3,{} ,{},{},{} milliseconds ,{}", statusCode,LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname,timeElapsedMillis, requestId);
            }
            ErrorHandel errorchecker = new ErrorHandel(mail);
            errorchecker.statusCodeHandel(statusCode);
            errorchecker.responeseCodeHandel(responseBody.split("\\|")[0]);

        }
        else{
            logger.info("4,{},{} ,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname , requestId);
            mail.sendEmail("Loging request API Request Took Longer than Expected", "Time Out("+Constants.THRESHOLD_SECONDS +"sec).");
        }
    }
}
