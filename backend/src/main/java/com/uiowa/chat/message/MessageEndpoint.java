package com.uiowa.chat.message;

import com.google.android.gcm.server.*;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.uiowa.chat.thread.ThreadRecord;
import com.uiowa.chat.thread.ThreadUtils;
import com.uiowa.chat.user.UserRecord;
import com.uiowa.chat.user.UserUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import sun.rmi.runtime.Log;

@Api(name = "messaging", version = "v1", namespace = @ApiNamespace(ownerDomain = "sai_chat.android.klinker.com", ownerName = "sai_chat.android.klinker.com", packagePath = ""))
public class MessageEndpoint {

    private static final Logger logger = Logger.getLogger(MessageEndpoint.class.getName());
    private static final String API_KEY = System.getProperty("gcm.api.key");
    private static final String DELETE_PASSWORD = "sai_admin";

    @ApiMethod(name = "sendNewMessage")
    public CollectionResponse<String> sendNewMessage(@Named("to_user") String toUser, @Named("from_user") String fromUser, @Named("message") String message) throws IOException {

        List<String> response = new ArrayList<String>();

        if (message == null || message.trim().length() == 0) {
            response.add("Error - No content");
            return CollectionResponse.<String>builder().setItems(response).build();
        }

        toUser = java.net.URLDecoder.decode(toUser, "UTF-8");
        fromUser = java.net.URLDecoder.decode(fromUser, "UTF-8");
        message = java.net.URLDecoder.decode(message, "UTF-8");

        List<Long> usersInThread = new ArrayList<Long>();
        usersInThread.add(Long.parseLong(toUser));
        usersInThread.add(Long.parseLong(fromUser));

        UserRecord toUserRecord = UserUtils.findUserById(usersInThread.get(0));

        String deviceId = toUserRecord.getDeviceId();

        ThreadRecord threadRecord = ThreadUtils.createNewThread(usersInThread);
        MessageRecord messageRecord = MessageUtils.createMessageRecord(threadRecord.getId(), Long.parseLong(fromUser), message);

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("message", messageRecord.getText())
                .addData("thread_id", messageRecord.getThreadId().toString())
                .addData("time", messageRecord.getTime().toString())
                .addData("sender_id", messageRecord.getSenderId().toString())
                .addData("message_id", messageRecord.getId().toString())
                .build();

        sender.send(msg, deviceId, 5);

        response.add(messageRecord.getThreadId() + "");

        return CollectionResponse.<String>builder().setItems(response).build();
    }

    @ApiMethod(name = "sendMessageToEveryone")
    public CollectionResponse<String> sendMessageToEveryone(@Named("message") String message) throws IOException {

        List<String> response = new ArrayList<String>();

        if (message == null || message.trim().length() == 0) {
            response.add("Error - No content");
            return CollectionResponse.<String>builder().setItems(response).build();
        }

        message = java.net.URLDecoder.decode(message, "UTF-8");

        MessageRecord messageRecord = MessageUtils.createMessageRecord(-1l, -1l, message);

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("message", messageRecord.getText())
                .addData("thread_id", messageRecord.getThreadId().toString())
                .addData("time", messageRecord.getTime().toString())
                .addData("sender_id", messageRecord.getSenderId().toString())
                .addData("message_id", messageRecord.getId().toString())
                .build();

        for (UserRecord user : UserUtils.findUsers()) {
            sender.send(msg, user.getDeviceId(), 5);
        }

        response.add(messageRecord.getThreadId() + "");

        return CollectionResponse.<String>builder().setItems(response).build();
    }

    @ApiMethod(name = "sendThreadedMessage")
    public CollectionResponse<MessageRecord> sendThreadedMessage(@Named("thread_id") String threadId, @Named("sender_id") String senderId, @Named("message") String message) throws IOException {

        List<MessageRecord> response = new ArrayList<MessageRecord>();

        if (message == null || message.trim().length() == 0) {
            return CollectionResponse.<MessageRecord>builder().setItems(response).build();
        }

        threadId = java.net.URLDecoder.decode(threadId, "UTF-8");
        senderId = java.net.URLDecoder.decode(senderId, "UTF-8");
        message = java.net.URLDecoder.decode(message, "UTF-8");

        UserRecord fromUser = UserUtils.findUserById(Long.parseLong(senderId));
        ThreadRecord threadRecord = ThreadUtils.findThreadById(Long.parseLong(threadId));
        MessageRecord messageRecord = MessageUtils.createMessageRecord(threadRecord.getId(), Long.parseLong(senderId), message);

        Long toUserId = threadRecord.getUserIds().get(0);
        Long senderIdLong = Long.parseLong(senderId);
        if (senderIdLong.equals(toUserId)) {
            toUserId = threadRecord.getUserIds().get(1);
        }

        String deviceId = UserUtils.findUserById(toUserId).getDeviceId();
        logger.log(Level.INFO, "device id: " + deviceId);

        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder()
                .addData("message", messageRecord.getText())
                .addData("thread_id", messageRecord.getThreadId().toString())
                .addData("time", messageRecord.getTime().toString())
                .addData("sender_id", messageRecord.getSenderId().toString())
                .addData("message_id", messageRecord.getId().toString())
                .build();

        sender.send(msg, deviceId, 5);

        response.add(messageRecord);

        return CollectionResponse.<MessageRecord>builder().setItems(response).build();
    }

    @ApiMethod(name = "findMessages")
    public CollectionResponse<MessageRecord> findMessages(@Named("thread_id") String threadId) throws IOException {
        List<MessageRecord> records = MessageUtils.findAllMessages(Long.parseLong(threadId));
        return CollectionResponse.<MessageRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "findUsersMessages")
    public CollectionResponse<MessageRecord> findUsersMessages(@Named("user_id") String userId) throws IOException {
        List<MessageRecord> records = MessageUtils.findUserMessages(Long.parseLong(userId));
        return CollectionResponse.<MessageRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "findMessage")
    public CollectionResponse<MessageRecord> findMessage(@Named("message_id") String messageId) throws IOException {
        MessageRecord record = MessageUtils.findMessage(Long.parseLong(messageId));

        List<MessageRecord> records = new ArrayList<MessageRecord>();
        records.add(record);

        return CollectionResponse.<MessageRecord>builder().setItems(records).build();
    }

    // separate message ids with a space.
    @ApiMethod(name = "deleteMessages")
    public CollectionResponse<String> deleteMessage(@Named("message_id") String messageIds) throws IOException{

        messageIds = java.net.URLDecoder.decode(messageIds, "UTF-8");

        int i = 0;
        if (messageIds.contains(" ")) {
            for (String s : messageIds.split(" ")) {
                MessageUtils.deleteMessage(Long.parseLong(s));
                i++;
            }
        } else {
            MessageUtils.deleteMessage(Long.parseLong(messageIds));
            i++;
        }

        List<String> records = new ArrayList<String>();
        records.add("Deleted " + i + " messages");

        return CollectionResponse.<String>builder().setItems(records).build();
    }

    @ApiMethod(name = "deleteAllMessages")
    public CollectionResponse<String> deleteAllMessage(@Named("password") String password) throws IOException {

        List<String> records = new ArrayList<String>();

        if (!password.equals(DELETE_PASSWORD)) {
            records.add("Error: Invalid password");
            return CollectionResponse.<String>builder().setItems(records).build();
        }

        MessageUtils.deleteAllMessages();

        records.add("Successfully deleted all records.");

        return CollectionResponse.<String>builder().setItems(records).build();
    }
}
