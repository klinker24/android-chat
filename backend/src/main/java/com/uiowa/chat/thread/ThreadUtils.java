package com.uiowa.chat.thread;

import com.uiowa.chat.OfyService;
import com.uiowa.chat.user.UserRecord;

import java.util.ArrayList;
import java.util.List;

public class ThreadUtils {

    public static ThreadRecord findThreadById(Long threadId) {
        return OfyService.ofy().load()
                .type(ThreadRecord.class)
                .id(threadId)
                .now();
    }

    public static ThreadRecord findThreadByTitle(String title) {
        return OfyService.ofy().load()
                .type(ThreadRecord.class)
                .filter("title", title)
                .first()
                .now();
    }

    public static List<ThreadRecord> findThreads(Long userId) {
        List<ThreadRecord> allRecords = findThreads();

        List<ThreadRecord> userThreads = new ArrayList<ThreadRecord>();
        for (ThreadRecord r : allRecords) {
            if (r.getUserIds().contains(userId)) {
                userThreads.add(r);
            }
        }

        return userThreads;
    }

    public static ThreadRecord findThreads(List<Long> userIds) {
        List<ThreadRecord> allRecords = findThreads(userIds.get(0));

        Long otherId = userIds.get(1);
        for (ThreadRecord record : allRecords) {
            for (Long id : record.getUserIds()) {
                if (id.equals(otherId)) {
                    return record;
                }
            }
        }

        return null;
    }

    public static List<ThreadRecord> findThreads() {
        return OfyService.ofy().load()
                .type(ThreadRecord.class)
                .list();
    }

    public static void saveThread(ThreadRecord thread) {
        OfyService.ofy().save()
                .entity(thread)
                .now();
    }

    public static void deleteThread(Long threadId) {
        ThreadRecord user = findThreadById(threadId);
        try {
            OfyService.ofy().delete()
                    .entity(user)
                    .now();
        } catch (Exception e) {

        }
    }

    public static void deleteUsersThreads(Long userId) {
        try {
            OfyService.ofy().delete()
                    .entities(findThreads(userId))
                    .now();
        } catch (Exception e) {

        }
    }

    public static void deleteAllThreads() {
        OfyService.ofy().delete()
                .entities(findThreads())
                .now();
    }

    public static ThreadRecord createNewThread(List<Long> userIds) {
        ThreadRecord record = new ThreadRecord();
        record.setUserIds(userIds);

        saveThread(record);
        record = findThreads(userIds);

        return record;
    }
}
