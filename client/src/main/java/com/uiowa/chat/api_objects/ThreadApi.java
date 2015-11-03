package com.uiowa.chat.api_objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ThreadApi extends APIObject {

    private static final String THREAD_ENDPOINT = TOP_LEVEL + "thread/" + API_VERSION + "/";

    // THREAD ENDPOINTS
    private static final String THREAD_FIND_THREAD         = THREAD_ENDPOINT + "findThread/";
    private static final String THREAD_FIND_USER_THREADS   = THREAD_ENDPOINT + "findUserThreads/";
    private static final String THREAD_DELETE_ALL_THREADS  = THREAD_ENDPOINT + "allthreads/";
    private static final String THREAD_DELETE_USER_THREADS = THREAD_ENDPOINT + "userthreads/";
    private static final String THREAD_DELETE_THREAD       = THREAD_ENDPOINT + "thread/";

    public JsonObject findThread(Long threadId) {
        String url = THREAD_FIND_THREAD + encodeParameters(threadId + "");
        return parseJsonObject(postRequest(url));
    }

    public JsonArray findUserThreads(Long userId) {
        String url = THREAD_FIND_USER_THREADS + encodeParameters(userId + "");
        return parseJsonArray(postRequest(url));
    }

    public JsonElement deleteAllThreads() {
        String url = THREAD_DELETE_ALL_THREADS + encodeParameters(DELETE_PASSWORD);
        return parseJsonElement(deleteRequest(url));
    }

    public JsonElement deleteUserThreads(Long userId) {
        String url = THREAD_DELETE_USER_THREADS + encodeParameters(userId + "");
        return parseJsonElement(deleteRequest(url));
    }

    public JsonElement deleteThread(Long threadId) {
        String url = THREAD_DELETE_THREAD + encodeParameters(threadId + "");
        return parseJsonElement(deleteRequest(url));
    }
}
