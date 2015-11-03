package com.uiowa.chat.user;

import com.uiowa.chat.OfyService;

import java.util.List;

public class UserUtils {

    public static UserRecord updateDeviceId(Long userId, String newDeviceId) {
        UserRecord record = findUserById(userId);
        record.setDeviceId(newDeviceId);
        saveUser(record);

        return record;
    }
    public static UserRecord findUserById(Long userId) {
        return OfyService.ofy().load()
                .type(UserRecord.class)
                .id(userId)
                .now();
    }

    public static UserRecord findUserByUsername(String username) {
        return OfyService.ofy().load()
                .type(UserRecord.class)
                .filter("username", username)
                .first()
                .now();
    }

    public static List<UserRecord> findUsers() {
        return OfyService.ofy().load()
                .type(UserRecord.class)
                .list();
    }

    public static void saveUser(UserRecord user) {
        OfyService.ofy().save()
                .entity(user)
                .now();
    }

    public static void deleteUser(Long userId) {
        UserRecord user = findUserById(userId);
        try {
            OfyService.ofy().delete()
                    .entity(user)
                    .now();
        } catch (Exception e) {

        }
    }

    public static void deleteUser(String username) {
        UserRecord user = findUserByUsername(username);
        try {
            OfyService.ofy().delete()
                    .entity(user)
                    .now();
        } catch (Exception e) {

        }
    }

    public static void deleteAllUsers() {
        OfyService.ofy().delete()
                .entities(findUsers())
                .now();
    }

    public static UserRecord createUser(String username, String realName, String deviceId) {
        UserRecord record = new UserRecord();
        record.setUsername(username);
        record.setRealName(realName);
        record.setDeviceId(deviceId);

        return record;
    }
}
