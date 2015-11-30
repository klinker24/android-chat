package com.uiowa.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uiowa.chat.api_objects.MessagingApi;
import com.uiowa.chat.api_objects.ThreadApi;
import com.uiowa.chat.api_objects.UserApi;

import java.util.ArrayList;
import java.util.List;

public class DataPopulator {

    public static final boolean KEEP_DATA = true;

    public static void main(String[] args) {
        doInitialData();

        UserApi user = new UserApi();
        JsonArray array = user.findAllUsers();
        List<Long> userIds =  new ArrayList<Long>();

        if (KEEP_DATA) {
            for (JsonElement u : array) {
                userIds.add(u.getAsJsonObject().get("id").getAsLong());
            }

            for (Long userId : userIds) {
                makeUserData(userId);
            }
        }
    }

    private static void makeUserData(long userId) {

        MessagingApi messaging = new MessagingApi();
        UserApi user = new UserApi();

        // get luke and jake id's
        JsonObject lklink = user.findUserByName("lklink");
        JsonObject jklink = user.findUserByName("jklink");
        Long lklinkId = lklink.get("id").getAsLong();
        Long jklinkId = jklink.get("id").getAsLong();

        // start a thread between user and lklink
        JsonElement threadObject = messaging.sendNewMessage(lklinkId, userId, "message+to+luke");
        Long lklinkerAndUserThread = threadObject.getAsLong();

        System.out.println("thread id for luke and user: " + lklinkerAndUserThread);
        System.out.println();

        for (int i = 0; i < 10; i++) {
            JsonElement messageObject = messaging.sendThreadedMessage(lklinkerAndUserThread, lklinkId, "message+" + i + "+from+luke");
            //System.out.println("message " + i + ": " + messageObject.getAsString());
        }
        System.out.println();

        // start a thread between user and jklink
        threadObject = messaging.sendNewMessage(jklinkId, userId, "message+to+jake");
        Long jklinkAndUserThread = threadObject.getAsLong();

        System.out.println("thread id for jake and user: " + jklinkAndUserThread);
        System.out.println();

        for (int i = 0; i < 10; i++) {
            JsonElement messageObject = messaging.sendThreadedMessage(jklinkAndUserThread, jklinkId, "message+" + i + "+from+jake");
            //System.out.println("message " + i + ": " + messageObject.getAsString());
        }
        System.out.println();
    }

    private static void doInitialData() {
        MessagingApi messaging = new MessagingApi();
        ThreadApi thread = new ThreadApi();
        UserApi user = new UserApi();

        JsonObject lklink = user.createUser("lklink", "Luke Klinker", "aaaaa");
        JsonObject lklink2 = user.createUser("lklinker", "Luke Klinker", "bbbbb");
        JsonObject lklink3 = user.createUser("lukeklinker", "Luke Klinker", "ddddd");
        JsonObject jklink = user.createUser("jklink", "Jake Klinker", "ccccc");

        System.out.println();

        if (!KEEP_DATA) {
            System.out.println("Testing delete methods");

            user.deleteAllUsers();
            messaging.deleteAllMessages();
            thread.deleteAllThreads();
        }
    }
}
