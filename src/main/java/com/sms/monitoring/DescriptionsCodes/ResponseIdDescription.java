package com.sms.monitoring.DescriptionsCodes;

import java.util.HashMap;
import java.util.Map;

public class ResponseIdDescription {
    private static final Map<String, String> responseIdDescriptionMap = new HashMap<>();

    static {
        // Populate the response ID-description mappings in the static block
         responseIdDescriptionMap.put("", "-");
        responseIdDescriptionMap.put("1", "-");// Success
        responseIdDescriptionMap.put("2001", "Error occurred during campaign creation");
        responseIdDescriptionMap.put("2002", "Bad request");
        responseIdDescriptionMap.put("2002|0", "Bad request");
        responseIdDescriptionMap.put("2003", "Empty number list");
        responseIdDescriptionMap.put("2004", "Empty message body");
        responseIdDescriptionMap.put("2005", "Invalid number list format");
        responseIdDescriptionMap.put("2006", "Not eligible to send messages via get requests (Admin haven’t provided the access level)");
        responseIdDescriptionMap.put("2007", "Invalid key (esmsqk parameter is invalid)");
        responseIdDescriptionMap.put("2008", "-");// Not enough money in the user's wallet or not enough messages left in the package for the user. (When consuming package payments)
        responseIdDescriptionMap.put("2009", "-"); // No valid numbers found after the removal of mask blocked numbers.
        responseIdDescriptionMap.put("2010", "Not eligible to consume packaging");
        responseIdDescriptionMap.put("2011", "Transactional error");
        responseIdDescriptionMap.put("2012", "Doesn’t have access for the mask.");
        responseIdDescriptionMap.put("2020", "Too many requests");
    }

    public static String getDescriptionForResponseId(String responseId) {
        return responseIdDescriptionMap.getOrDefault(responseId, "-");
    }
}
