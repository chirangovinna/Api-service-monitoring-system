package com.sms.monitoring.DescriptionsCodes;

import java.util.HashMap;
import java.util.Map;

public class ErrorCodeDescription {
    private static final Map<String, String> errorCodeDescriptionMap = new HashMap<>();

    static {
        // Populate the error code-description mappings in the static block
        errorCodeDescriptionMap.put("100", "Invalid Token (Token Expired)");
        errorCodeDescriptionMap.put("101", "Invalid Request Parameters");
        errorCodeDescriptionMap.put("102", "User account not found or not a valid account");
        errorCodeDescriptionMap.put("103", "-");//Logical Error
        errorCodeDescriptionMap.put("104", "Transaction ID is already used");
        errorCodeDescriptionMap.put("105", "Invalid Token Signature");
        errorCodeDescriptionMap.put("106", "Token not found in the header (Or token is not attached as a bearer token)");
        errorCodeDescriptionMap.put("107", "One or more mandatory parameters in the request are either missing or invalid");
        errorCodeDescriptionMap.put("108", "User don’t have such active mask eligible to send messages");
        errorCodeDescriptionMap.put("109", "There is no single valid mobile number after removing invalids, duplicates, and mask-blocked numbers from the msisdn list.");
        errorCodeDescriptionMap.put("110", "Not eligible to consume packaging");
        errorCodeDescriptionMap.put("111", "-");// Package payments can only be used for campaigns scheduled for this month.
        errorCodeDescriptionMap.put("112", "-"); //Number of messages left in the package is less than the campaign messages.
        errorCodeDescriptionMap.put("113", "Package Maintenance Downtime.");
        errorCodeDescriptionMap.put("114", "-"); // Not enough wallet balance to run the campaign.
        errorCodeDescriptionMap.put("115", "Username or password invalid");
        errorCodeDescriptionMap.put("116", "Account locked");
        errorCodeDescriptionMap.put("117", "Too many requests");
        errorCodeDescriptionMap.put("999", "Internal Server Error");
    }

    public static String getDescriptionForErrorCode(String errorCode) {
        return errorCodeDescriptionMap.getOrDefault(errorCode, "-");
    }
}
