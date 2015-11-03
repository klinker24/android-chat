package com.uiowa.chat;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.uiowa.chat.message.MessageRecord;
import com.uiowa.chat.thread.ThreadRecord;
import com.uiowa.chat.user.UserRecord;


public class OfyService {

    static {
        ObjectifyService.register(UserRecord.class);
        ObjectifyService.register(MessageRecord.class);
        ObjectifyService.register(ThreadRecord.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
