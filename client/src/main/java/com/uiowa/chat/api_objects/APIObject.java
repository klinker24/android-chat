package com.uiowa.chat.api_objects;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class APIObject {

    protected static final String DELETE_PASSWORD = "sai_admin";

    protected static final String DOMAIN = "sai-chat-server";
    protected static final String API_VERSION = "v1";
    protected static final String TOP_LEVEL = "https://" + DOMAIN + ".appspot.com/_ah/api/";

    protected JsonObject postRequest(String url) {
        try {
            URL obj = new URL(url);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            return executeRequest(con);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    protected JsonObject putRequest(String url) {
        try {
            URL obj = new URL(url);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setDoOutput(true);

            return executeRequest(con);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    protected JsonObject getRequest(String url) {
        try {
            URL obj = new URL(url);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);

            return executeRequest(con);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    protected JsonObject deleteRequest(String url) {
        try {
            URL obj = new URL(url);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("DELETE");
            con.setDoOutput(true);

            return executeRequest(con);
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }

    private JsonObject executeRequest(HttpsURLConnection con) throws IOException {
        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
        wr.write("");
        wr.close();

        InputStream response = con.getInputStream();

        String responseString = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
        for (String line; (line = reader.readLine()) != null;) {
            responseString += line;
        }

        return new JsonParser().parse(responseString).getAsJsonObject();
    }

    protected String encodeParameters(String... values) {
        String parameters = "";

        for (String s : values) {
            try {
                parameters +=  java.net.URLEncoder.encode(s, "UTF-8") + "/";
            } catch (IOException e) {

            }
        }

        return parameters.substring(0, parameters.length() - 1);
    }

    protected String encodeParameters(List<Long> ids) {
        String parameters = "";

        for (Long l : ids) {
            try {
                parameters +=  java.net.URLEncoder.encode(l + "", "UTF-8") + "/";
            } catch (IOException e) {

            }
        }

        return parameters.substring(0, parameters.length() - 2);
    }

    protected JsonObject parseJsonObject(JsonObject object) {
        return parseJsonElement(object).getAsJsonObject();
    }

    protected JsonElement parseJsonElement(JsonObject object) {
        return object.getAsJsonArray("items").get(0);
    }

    protected JsonArray parseJsonArray(JsonObject object) {
        return object.getAsJsonArray("items");
    }
}
