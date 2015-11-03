package com.uiowa.chat.api_objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserApi extends APIObject {
    private static final String USER_ENDPOINT = TOP_LEVEL + "user/" + API_VERSION + "/";

    // USER ENDPOINTS
    private static final String USER_CREATE_NEW        = USER_ENDPOINT + "createNewUser/";
    private static final String USER_UPDATE_DEVICE     = USER_ENDPOINT + "collectionresponse_userrecord/";
    private static final String USER_FIND_ALL          = USER_ENDPOINT + "findAllUsers/";
    private static final String USER_FIND_USER_BY_ID   = USER_ENDPOINT + "findUserById/";
    private static final String USER_FIND_USER_BY_NAME = USER_ENDPOINT + "findUserByUsername/";
    private static final String USER_DELETE_ALL        = USER_ENDPOINT + "allusers/";
    private static final String USER_DELETE_BY_ID      = USER_ENDPOINT + "userbyid/";
    private static final String USER_DELETE_BY_NAME    = USER_ENDPOINT + "userbyusername/";

    public JsonObject createUser(String username, String realName, String deviceId) {
        String url = USER_CREATE_NEW + encodeParameters(username, realName, deviceId);
        return parseJsonObject(postRequest(url));
    }

    public JsonObject updateDeviceId(Long userId, String deviceId) {
        String url = USER_UPDATE_DEVICE + encodeParameters(userId + "", deviceId);
        return parseJsonObject(putRequest(url));
    }

    public JsonArray findAllUsers() {
        return parseJsonArray(postRequest(USER_FIND_ALL));
    }

    public JsonObject findUserById(Long userId) {
        String url = USER_FIND_USER_BY_ID + encodeParameters(userId + "");
        return parseJsonObject(postRequest(url));
    }

    public JsonObject findUserByName(String username) {
        String url = USER_FIND_USER_BY_NAME + encodeParameters(username + "");
        return parseJsonObject(postRequest(url));
    }

    public JsonElement deleteAllUsers() {
        String url = USER_DELETE_ALL + encodeParameters(DELETE_PASSWORD);
        return parseJsonElement(deleteRequest(url));
    }

    public JsonElement deleteUserById(Long userId) {
        String url = USER_DELETE_BY_ID + encodeParameters(userId + "");
        return parseJsonElement(deleteRequest(url));
    }

    public JsonElement deleteUserByName(String username) {
        String url = USER_DELETE_BY_NAME + encodeParameters(username);
        return parseJsonElement(deleteRequest(url));
    }
}
