package com.uiowa.chat.api_objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class MessagingApi extends APIObject {

    private static final String MESSAGING_ENDPOINT = TOP_LEVEL + "messaging/" + API_VERSION + "/";

    // MESSAGING ENDPOINTS
    private static final String MESSAGING_SEND_NEW           = MESSAGING_ENDPOINT + "sendNewMessage/";
    private static final String MESSAGING_SEND_THREADED      = MESSAGING_ENDPOINT + "sendThreadedMessage/";
    private static final String MESSAGING_FIND_USER_MESSAGES = MESSAGING_ENDPOINT + "findUsersMessages/";
    private static final String MESSAGING_FIND_MESSAGES      = MESSAGING_ENDPOINT + "findMessages/";
    private static final String MESSAGING_FIND_MESSAGE       = MESSAGING_ENDPOINT + "findMessage/";
    private static final String MESSAGING_DELETE_MESSAGES    = MESSAGING_ENDPOINT + "message/";
    private static final String MESSAGING_DELETE_ALL         = MESSAGING_ENDPOINT + "allmessage/";

    public JsonElement sendNewMessage(Long recipientId, Long senderId, String message) {
        String url = MESSAGING_SEND_NEW + encodeParameters(recipientId + "", senderId + "", message);
        return parseJsonElement(postRequest(url));
    }

    public JsonElement sendThreadedMessage(Long threadId, Long senderId, String message) {
        String url = MESSAGING_SEND_THREADED + encodeParameters(threadId + "", senderId + "", message);
        return parseJsonElement(postRequest(url));
    }

    public JsonArray findMessages(Long threadId) {
        String url = MESSAGING_FIND_MESSAGES + encodeParameters(threadId + "");
        return parseJsonArray(postRequest(url));
    }

    public JsonArray findUserMessages(Long userId) {
        String url = MESSAGING_FIND_USER_MESSAGES + encodeParameters(userId + "");
        return parseJsonArray(postRequest(url));
    }

    public JsonObject findMessage(Long messageId) {
        String url = MESSAGING_FIND_MESSAGE + encodeParameters(messageId + "");
        return parseJsonObject(postRequest(url));
    }

    public JsonElement deleteMessages(List<Long> messageIds) {
        String url = MESSAGING_DELETE_MESSAGES + encodeParameters(messageIds);
        return parseJsonElement(deleteRequest(url));
    }

    public JsonElement deleteAllMessages() {
        String url = MESSAGING_DELETE_ALL + encodeParameters(DELETE_PASSWORD);
        return parseJsonElement(deleteRequest(url));
    }
}
