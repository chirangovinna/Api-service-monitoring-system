package com.sms.monitoring.API;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sms.monitoring.Constants;
import com.sms.monitoring.ErrorHandel;
import com.sms.monitoring.MailService.SendMail;
import org.slf4j.Logger;

public class LoginRequest {
    private static final Logger logger = LoggerFactory.getLogger(LoginRequest.class);
    String apiname = " Login API ";
    private static final String API_URL = "https://esms.dialog.lk/api/v1/user/login";
    

    public String getAccessToken() {
        // Random random = new Random();
        String requestId = Integer.toString(((int) java.time.Instant.now().getEpochSecond()))+ Integer.toString((new Random()).nextInt(100));

        String payload = "{\"username\":\"" + Constants.USERNAME + "\", \"password\":\"" + Constants.PASSWORD + "\"}";

        HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(Constants.THRESHOLD_SECONDS)) // .connectTimeout(Duration.ofSeconds(1)) // Set a timeout of 10 seconds TODO
                    .build();

        Instant requestTime = Instant.now(); // Capture the current time before making the request
        // Log the unique ID, API name, and other details
        logger.info("1,{},{},,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname,requestId);


        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .timeout(Duration.ofSeconds(Constants.THRESHOLD_SECONDS)) // Set a timeout of 10 seconds TODO
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
        // logger.info(apiname+"Response ID: {}", requestId);

        // Send email based on time difference
        SendMail mail = new SendMail(apiname);
        if (responseTime != null) {
            // Calculate the time difference between request and response
            Duration timeDifference = Duration.between(requestTime, responseTime);
            long timeElapsedMillis = timeDifference.toMillis();
            System.out.println(timeElapsedMillis+"ms");

            // Continue with the rest of code for processing the response
            int statusCode = response.statusCode();
            String responseBody = response.body();

            // Log the unique ID, API name, and other details
            // Log the appropriate response event
            if (statusCode >= 200 && statusCode < 300) {
                logger.info("2,{},{},{} milliseconds ,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), apiname,timeElapsedMillis, requestId);
            } else{
                logger.info("3,{},{},{},{} milliseconds ,{}", statusCode,LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname,timeElapsedMillis, requestId);
            }


            // Parse the response body using Gson
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            String accessToken = jsonResponse.get("token").getAsString();

            // Handle the response according to your requirements
            // System.out.println("Status Code: " + statusCode);
            // System.out.println("Response Body: " + responseBody);           

            ErrorHandel errorchecker = new ErrorHandel(mail);
            errorchecker.statusCodeHandel(statusCode);
            errorchecker.errorCodeHandel(responseBody);
            return accessToken;

        } else {
            // Handle the case where there was no response received within 30 seconds
            logger.info("4,{},{},{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname , requestId);
            mail.sendEmail("Loging request API Request Took Longer than Expected", "Time Out("+Constants.THRESHOLD_SECONDS +" sec).");
            return null; // You can return null or some other appropriate value
        }
        
    }

}
