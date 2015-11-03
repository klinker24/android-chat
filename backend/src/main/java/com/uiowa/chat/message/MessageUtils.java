package com.uiowa.chat.message;

import com.uiowa.chat.OfyService;
import com.uiowa.chat.thread.ThreadRecord;
import com.uiowa.chat.thread.ThreadUtils;
import com.uiowa.chat.user.UserRecord;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

    public static List<MessageRecord> findLatestMessages(Long threadId, Long createdAfter) {
        return OfyService.ofy().load()
                .type(MessageRecord.class)
                .filter("threadId", threadId)
                .filter("time >", createdAfter)
                .list();
    }

    public static List<MessageRecord> findUserMessages(Long userId) {
        List<MessageRecord> messages = new ArrayList<MessageRecord>();

        List<ThreadRecord> threads = ThreadUtils.findThreads(userId);
        for (ThreadRecord thread : threads) {
            messages.addAll(findAllMessages(thread.getId()));
        }

        return messages;
    }

    public static List<MessageRecord> findAllMessages(Long threadId) {
        return OfyService.ofy().load()
                .type(MessageRecord.class)
                .filter("threadId", threadId)
                .list();
    }

    public static List<MessageRecord> findAllMessages() {
        return OfyService.ofy().load()
                .type(MessageRecord.class)
                .list();
    }

    public static MessageRecord findMessage(Long messageId) {
        return OfyService.ofy().load()
                .type(MessageRecord.class)
                .id(messageId)
                .now();
    }

    public static void saveMessage(MessageRecord message) {
        OfyService.ofy().save()
                .entity(message)
                .now();
    }

    public static void deleteMessage(Long messageId) {
        try {
            OfyService.ofy().delete()
                    .entity(findMessage(messageId))
                    .now();
        } catch (NullPointerException e) {
            // message doesn't exist
        }
    }

    public static void deleteThreadMessages(Long threadId) {
        try {
            OfyService.ofy().delete()
                    .entity(findAllMessages(threadId))
                    .now();
        } catch (Exception e) {
            // doesn't exist
        }
    }

    public static void deleteAllMessages() {
        OfyService.ofy().delete()
                .entities(findAllMessages())
                .now();
    }

    public static MessageRecord createMessageRecord(Long threadId, Long senderId, String text) {
        MessageRecord record = new MessageRecord();
        record.setText(text);
        record.setThreadId(threadId);
        record.setTime(System.currentTimeMillis());
        record.setSenderId(senderId);

        saveMessage(record);

        return record;
    }
}
