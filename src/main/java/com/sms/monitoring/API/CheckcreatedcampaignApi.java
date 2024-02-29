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

/**
 * End point: https://e-sms.dialog.lk/api/v2/sms/check-transaction
 * Method: HTTP POST
 * Type: application/json
 */
public class CheckcreatedcampaignApi {
    String apiname = "Check created campaign Api ";
    private static final Logger logger = LoggerFactory.getLogger(CheckcreatedcampaignApi.class);
    private static final String API_URL = "https://e-sms.dialog.lk/api/v2/sms/check-transaction";
    private String BEARER_TOKEN ;
    private String transactionId;
    String errorCode;
    public void setAccessToken(String accessToken) {
        BEARER_TOKEN = accessToken;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void checkTransaction() throws URISyntaxException, IOException, InterruptedException {
        String requestId = Integer.toString(((int) java.time.Instant.now().getEpochSecond()))+ Integer.toString((new Random()).nextInt(100));

        String payload = "{\"transaction_id\": \"" + transactionId + "\"}";
        HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(Constants.THRESHOLD_SECONDS)) // Set a timeout of 10 seconds TODO
        .build(); 

        Instant requestTime = Instant.now(); // Capture the current time before sending the request

        logger.info("1,{},{},,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname,requestId);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
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
            errorchecker.errorCodeHandel(responseBody);

        }
        else{
            logger.info("4,{},{} ,{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),apiname , requestId);
            mail.sendEmail("Loging request API Request Took Longer than Expected", "Time Out("+Constants.THRESHOLD_SECONDS +"sec).");
        }
        
    }

}
