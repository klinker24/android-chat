package com.uiowa.chat.thread;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

@Entity
public class ThreadRecord {

    @Id
    Long threadId;

    @Index
    private String title;

    @Index
    private List<Long> involvedUserIds;

    public ThreadRecord() { }

    public Long getId() {
        return threadId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getUserIds() {
        return involvedUserIds;
    }

    public void setUserIds(List<Long> ids) {
        this.involvedUserIds = ids;
    }
}
