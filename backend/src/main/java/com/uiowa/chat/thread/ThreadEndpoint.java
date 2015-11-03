/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package com.uiowa.chat.thread;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.uiowa.chat.OfyService;
import com.uiowa.chat.message.MessageUtils;
import com.uiowa.chat.user.UserRecord;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

@Api(name = "thread", version = "v1", namespace = @ApiNamespace(ownerDomain = "sai_chat.android.klinker.com", ownerName = "sai_chat.android.klinker.com", packagePath = ""))
public class ThreadEndpoint {

    private static final Logger log = Logger.getLogger(ThreadEndpoint.class.getName());
    private static final String DELETE_PASSWORD = "sai_admin";

    @ApiMethod(name = "findUserThreads")
    public CollectionResponse<ThreadRecord> findUserThreads(@Named("user_id") String userId) throws IOException {
        List<ThreadRecord> records = ThreadUtils.findThreads(Long.parseLong(userId));
        return CollectionResponse.<ThreadRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "findThread")
    public CollectionResponse<ThreadRecord> findThread(@Named("thread_id") String threadId) throws IOException {
        ThreadRecord record = ThreadUtils.findThreadById(Long.parseLong(threadId));

        List<ThreadRecord> records = new ArrayList<ThreadRecord>();
        records.add(record);

        return CollectionResponse.<ThreadRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "deleteAllThreads")
    public CollectionResponse<String> deleteAllThreads(@Named("password") String password) throws IOException {

        List<String> records = new ArrayList<String>();

        if (!password.equals(DELETE_PASSWORD)) {
            records.add("Error: Invalid password");
            return CollectionResponse.<String>builder().setItems(records).build();
        }

        ThreadUtils.deleteAllThreads();
        MessageUtils.deleteAllMessages();

        records.add("Successfully deleted all records.");

        return CollectionResponse.<String>builder().setItems(records).build();
    }

    @ApiMethod(name = "deleteUserThreads")
    public CollectionResponse<String> deleteUserThreads(@Named("user_id") String userId) throws IOException {

        List<String> records = new ArrayList<String>();
        Long longId = Long.parseLong(userId);

        List<ThreadRecord> threads = ThreadUtils.findThreads(longId);
        ThreadUtils.deleteUsersThreads(longId);

        for (ThreadRecord r : threads) {
            MessageUtils.deleteThreadMessages(r.getId());
        }

        records.add("Successfully deleted " + threads.size() + " threads for user id " + userId);

        return CollectionResponse.<String>builder().setItems(records).build();
    }

    @ApiMethod(name = "deleteThread")
    public CollectionResponse<String> deleteThread(@Named("thread_id") String threadId) throws IOException {

        List<String> records = new ArrayList<String>();
        Long longId = Long.parseLong(threadId);

        ThreadRecord thread = ThreadUtils.findThreadById(longId);
        ThreadUtils.deleteThread(longId);
        MessageUtils.deleteThreadMessages(thread.getId());

        records.add("Successfully deleted thread");

        return CollectionResponse.<String>builder().setItems(records).build();
    }
}