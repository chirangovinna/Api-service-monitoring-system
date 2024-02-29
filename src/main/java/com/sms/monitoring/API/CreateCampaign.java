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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sms.monitoring.Constants;
import com.sms.monitoring.ErrorHandel;
import com.sms.monitoring.MailService.SendMail;

public class CreateCampaign {
    String apiname = "Create Campaign API ";
    private static final Logger logger = LoggerFactory.getLogger(CreateCampaign.class);
    private static final String API_URL = "https://esms.dialog.lk/api/v1/common-campaigns/create-campaign/via-json";
    String errorCode;
    private String BEARER_TOKEN ;
    public void setAccessToken(String accessToken) {
        BEARER_TOKEN = accessToken;
    }

    public int CreatingCampaigns() throws IOException, URISyntaxException, InterruptedException{
        String requestId = Integer.toString(((int) java.time.Instant.now().getEpochSecond()))+ Integer.toString((new Random()).nextInt(100));

        String sourceAddress = "TestAdeona";
        String campaignName = "checking created campaign";
        String mobileNumber = "0771531590";
        String scheduleDate = "2023-08-09";
        String scheduleTime = "11:05:31";
        String message = "Hi, Chiran got the sms.";
        String scheduleNow = "1";  // 1 for immediate, 0 for scheduled

        List<String> msisdnList = new ArrayList<>();
        msisdnList.add(mobileNumber);

        String payload = "{" +
                "\"sourceAddress\": \"" + sourceAddress + "\"," +
                "\"campaign_name\": \"" + campaignName + "\"," +
                "\"sampleNumberList\": [" +
                "{\"mobile\": \"" + msisdnList.get(0) + "\"}" +
                "]," +
                "\"scheduleDate\": \"" + scheduleDate + "\"," +
                "\"scheduleTime\": \"" + scheduleTime + "\"," +
                "\"message\": \"" + message + "\"," +
                "\"schedule_now\": \"" + scheduleNow + "\"" +
                "}";

        HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(Constants.THRESHOLD_SECONDS)) // Set a timeout of 10 seconds TODO
        .build();

        Instant requestTime = Instant.now();

        logger.info("1,{},{},,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname,requestId);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .timeout(Duration.ofSeconds(Constants.THRESHOLD_SECONDS))
                .build();

        // System.out.println("httpRequest: "+httpRequest);
        HttpResponse<String> response = null;
        Instant responseTime = null;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            responseTime = Instant.now();
        } catch (IOException | InterruptedException e) {
            // Handle the exception if something goes wrong while sending the request
            e.printStackTrace();
        }

        // System.out.println("responseTime: "+responseTime);
        // Send email based on time difference
        SendMail mail = new SendMail(apiname);
        if (responseTime != null) {
            Duration timeDifference = Duration.between(requestTime, responseTime);
            long timeElapsedMillis = timeDifference.toMillis();
            System.out.println("Creating Campaigns Endpoint Time taken: " + timeElapsedMillis + " milliseconds");
            
            // Continue with the rest of your code for processing the response
            int statusCode = response.statusCode();
            String responseBody = response.body();

            // Handle the response according to your requirements
            // System.out.println("Status Code: " + statusCode);
            // System.out.println("Response Body: " + responseBody);

            int campaignId=0;

            // Log the unique ID, API name, and other details
            if (statusCode >= 200 && statusCode < 300) {
                // Parse the response body using Gson
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                campaignId = jsonResponse.get("campaignId").getAsInt();
                // System.out.println("Campaign ID: " + campaignId);
                logger.info("2,{},{},{} milliseconds ,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), apiname,timeElapsedMillis, requestId);
            } else {
                logger.info("3,{} ,{},{},{} milliseconds ,{}", statusCode,LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname,timeElapsedMillis, requestId);
            }
            ErrorHandel errorchecker = new ErrorHandel(mail);
            errorchecker.statusCodeHandel(statusCode);
            // errorchecker.errorCodeHandel(responseBody); 

            return campaignId;

        }
        else{
            logger.info("4,{},{} ,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname , requestId);
            mail.sendEmail("Loging request API Request Took Longer than Expected", "Time Out("+Constants.THRESHOLD_SECONDS +" sec).");
            return 0;
        }

    }
}
