package com.hospital.web;

import com.alibaba.fastjson2.JSONObject;

/**
 * Builds the legacy message-only response consumed by the existing front end.
 * Keep JSONObject, the message key, and null values unchanged for compatibility.
 */
public final class MessageResponses {
    private MessageResponses() {
    }

    public static JSONObject message(String message) {
        JSONObject response = new JSONObject();
        response.put("message", message);
        return response;
    }
}
