package com.sms.monitoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.monitoring.DescriptionsCodes.ErrorCodeDescription;
import com.sms.monitoring.DescriptionsCodes.ResponseIdDescription;
import com.sms.monitoring.MailService.SendMail;

public class ErrorHandel {
    String errorCode;
    String comment;
    String responseId;
    // SendMail mail = new SendMail();
    String api;
    SendMail mail;
    public ErrorHandel (SendMail mail){
        this.mail=mail;
    }
    public void setApi(String api) {
        this.api = api;
    }
    public void statusCodeHandel(int statusCode){
        //if 500 send mail according to that
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }
        else{
             mail.sendEmail(" Status code is "+statusCode, " Invalid status code.");
        }
        System.out.println(statusCode);
    }
    public void errorCodeHandel(String responseBody){
        String description="-";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (jsonNode.has("errCode")) {
                errorCode = jsonNode.get("errCode").asText();
                if (jsonNode.has("comment")) {
                comment = jsonNode.get("comment").asText();
                } else {
                    System.out.println("comment not found in the JSON data.");
                }
            } else {
                System.out.println("errCode not found in the JSON data.");
            }
            
            description = ErrorCodeDescription.getDescriptionForErrorCode(errorCode);
            System.out.println("Description: " + description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(description.equals("-")){
            
        }
        else{
            description = description+" 'Comment': "+comment;
            mail.sendEmail("errCode code is "+errorCode, description);
        }

    }
    public void responeseCodeHandel(String responseBody){
        String description="-";
        try {
            description = ResponseIdDescription.getDescriptionForResponseId(responseBody);
            System.out.println("Description: " + description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(description.equals("-")){
            
        }
        else{
            mail.sendEmail("errCode code is "+responseBody," "+description);
        }

    }
}
