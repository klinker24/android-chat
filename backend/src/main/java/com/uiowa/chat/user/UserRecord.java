package com.uiowa.chat.user;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class UserRecord {

    @Id
    Long userId;

    @Index
    private String deviceId;

    @Index
    private String username;

    @Index
    private String realName;

    public UserRecord() { }

    public Long getId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String id) {
        this.deviceId = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String name) {
        this.realName = name;
    }
}
