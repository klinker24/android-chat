package com.uiowa.chat.message;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class MessageRecord {

    @Id
    Long messageId;

    @Index
    private Long threadId;

    @Index
    private Long time;

    @Index
    private String text;

    @Index
    private Long senderId;

    public MessageRecord() { }

    public Long getId() {
        return messageId;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long id) {
        this.senderId = id;
    }
}
