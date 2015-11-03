package com.uiowa.chat.user;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.uiowa.chat.message.MessageUtils;
import com.uiowa.chat.thread.ThreadRecord;
import com.uiowa.chat.thread.ThreadUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

@Api(name = "user", version = "v1", namespace = @ApiNamespace(ownerDomain = "sai_chat.android.klinker.com", ownerName = "sai_chat.android.klinker.com", packagePath = ""))
public class UserEndpoint {

    private static final Logger log = Logger.getLogger(UserEndpoint.class.getName());
    private static final String DELETE_PASSWORD = "sai_admin";

    @ApiMethod(name = "createNewUser")
    public CollectionResponse<UserRecord> createNewUser(@Named("username") String username, @Named("real_name") String realName, @Named("device_id") String deviceId) throws IOException {

        List<UserRecord> records = new ArrayList<UserRecord>();

        realName = java.net.URLDecoder.decode(realName, "UTF-8");
        UserRecord record = UserUtils.findUserByUsername(username);

        if (record != null) {
            // the username exists. We will just update the device id
            records.add(UserUtils.updateDeviceId(record.getId(), deviceId));
            return CollectionResponse.<UserRecord>builder().setItems(records).build();
        } else {
            // want to create a new user
            record = UserUtils.createUser(username, realName, deviceId);
            UserUtils.saveUser(record);

            records.add(UserUtils.findUserByUsername(username));
            return CollectionResponse.<UserRecord>builder().setItems(records).build();
        }
    }

    @ApiMethod(name = "updateDeviceId")
    public CollectionResponse<UserRecord> updateDeviceId(@Named("user_id") String userId, @Named("device_id") String deviceId) {
        UserRecord record = UserUtils.updateDeviceId(Long.parseLong(userId), deviceId);

        List<UserRecord> records = new ArrayList<UserRecord>();
        records.add(record);

        return CollectionResponse.<UserRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "findAllUsers")
    public CollectionResponse<UserRecord> findAllUsers() throws IOException {
        List<UserRecord> records = UserUtils.findUsers();
        return CollectionResponse.<UserRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "findUserById")
    public CollectionResponse<UserRecord> findUserById(@Named("user_id") String userId) throws IOException {
        UserRecord record = UserUtils.findUserById(Long.parseLong(userId));

        List<UserRecord> records = new ArrayList<UserRecord>();
        records.add(record);

        return CollectionResponse.<UserRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "findUserByUsername")
    public CollectionResponse<UserRecord> findUserByUsername(@Named("username") String username) throws IOException {
        UserRecord record = UserUtils.findUserByUsername(username);

        List<UserRecord> records = new ArrayList<UserRecord>();
        records.add(record);

        return CollectionResponse.<UserRecord>builder().setItems(records).build();
    }

    @ApiMethod(name = "deleteAllUsers")
    public CollectionResponse<String> deleteAllUsers(@Named("password") String password) throws IOException {

        List<String> records = new ArrayList<String>();

        if (!password.equals(DELETE_PASSWORD)) {
            records.add("Error: Invalid password");
            return CollectionResponse.<String>builder().setItems(records).build();
        }

        UserUtils.deleteAllUsers();

        records.add("Successfully deleted all records.");

        return CollectionResponse.<String>builder().setItems(records).build();
    }

    @ApiMethod(name = "deleteUserById")
    public CollectionResponse<String> deleteUserById(@Named("user_id") String userId) throws IOException {

        List<String> records = new ArrayList<String>();

        UserUtils.deleteUser(Long.parseLong(userId));

        records.add("Successfully deleted user " + userId);

        return CollectionResponse.<String>builder().setItems(records).build();
    }

    @ApiMethod(name = "deleteUserByUsername")
    public CollectionResponse<String> deleteUserByUsername(@Named("username") String username) throws IOException {

        List<String> records = new ArrayList<String>();

        UserUtils.deleteUser(username);

        records.add("Successfully deleted " + username);

        return CollectionResponse.<String>builder().setItems(records).build();
    }
}
