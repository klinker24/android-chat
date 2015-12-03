package com.uiowa.chat.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.uiowa.chat.R;
import com.uiowa.chat.data.DatabaseHelper;
import com.uiowa.chat.data.Thread;
import com.uiowa.chat.data.User;
import com.uiowa.chat.fragments.MessageListFragment;
import com.uiowa.chat.utils.RegistrationUtils;
import com.uiowa.chat.utils.api.Sender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This activity is used to start a new thread with a user.
 *
 * What to check out:
 *      - AutoCompleteTextView is a really cool widget. You can define some kind of list adapter
 *           for it and it will automatically create an autocomplete from that list as the user types.
 *           It works great for simple string lists, but can get pretty complex when defining your own
 *           custom auto complete classes.
 */
public class NewMessageActivity extends AbstractToolbarActivity {

    private static final String TAG = "NewMessageActivity";

    private AutoCompleteTextView userAutoComplete;
    private EditText messageText;
    private ImageButton sendButton;

    private User sendTo = null;

    private Map<String, User> userNameMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the layout for the activity
        setContentView(R.layout.activity_new_message);

        // find our views
        userAutoComplete = (AutoCompleteTextView) findViewById(R.id.user_auto_complete);
        messageText = (EditText) findViewById(R.id.message_text);
        sendButton = (ImageButton) findViewById(R.id.send_button);

        setUpAutoComplete();

        // set a click listener on the send button to send the message to the user.
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // since I said things got more complex with custom classes on the auto complete,
                // we can simply map the string list we use to the user objects in our database
                sendTo = userNameMap.get(userAutoComplete.getText().toString());

                if (sendTo != null) {
                    // used to get the current users id
                    RegistrationUtils registrationUtils = new RegistrationUtils();

                    // send the message in the background thread.
                    // after the message is successfully sent, the Sender will send a broadcast for the fragments to update
                    // with the action Sender.SENT_BROADCAST.
                    // we should listen for this broadcast on our fragments and update when necessary.
                    Sender sender = new Sender(NewMessageActivity.this);
                    sender.sendNewMessage(sendTo.getUserId(), registrationUtils.getMyUserId(NewMessageActivity.this), messageText.getText().toString(), null);

                    // we don't want to keep the user waiting on the new message screen, so finish this
                    // activity and kick them back to the conversation list.
                    finish();
                }
            }
        });
    }

    private void setUpAutoComplete() {

        final List<User> users = getUserList();
        final List<String> autoCompleteList = getAutoCompleteList(users);

        // create a generic string adapter. We dont even have to write this,
        // android has the most basic ones build in. Phew.
        ArrayAdapter<String> adapter = new ArrayAdapter<> (
                this,
                android.R.layout.select_dialog_item,
                autoCompleteList
        );

        userAutoComplete.setThreshold(1);
        userAutoComplete.setAdapter(adapter);
        userAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RegistrationUtils utils = new RegistrationUtils();
                final long currentUserId = utils.getMyUserId(NewMessageActivity.this);
                final DatabaseHelper helper = new DatabaseHelper(NewMessageActivity.this);

                User user = userNameMap.get(userAutoComplete.getText().toString());
                List<Thread> threads = helper.findAllConversations();
                Thread thread = null;
                for (Thread t : threads) {
                    if (t.getUser1Id() == user.getUserId() || t.getUser2Id() == user.getUserId()) {
                        thread = t;
                        break;
                    }
                }

                if (thread == null) {
                    final ProgressDialog dialog = new ProgressDialog(NewMessageActivity.this);
                    dialog.setMessage("Creating new conversation...");
                    dialog.show();

                    // create a new thread for this conversation to be on, then start the activity
                    Sender sender = new Sender(NewMessageActivity.this);
                    sender.sendNewMessage(user.getUserId(), currentUserId, " ", new Sender.MessageSentListener() {
                        @Override
                        public void onMessageSent(long threadId) {
                            dialog.hide();
                            Thread thread = helper.findConversation(threadId);
                            startConversationActivity(thread, currentUserId);
                        }
                    });
                } else {
                    startConversationActivity(thread, currentUserId);
                }
            }
        });
    }

    private void startConversationActivity(Thread thread, long currentUserId) {
        Intent messageList = new Intent(NewMessageActivity.this, MessageListActivity.class);
        messageList.putExtra(MessageListFragment.EXTRA_THREAD_ID, thread.getThreadId());

        if (thread.getUser1Id() == currentUserId) {
            messageList.putExtra(MessageListFragment.EXTRA_CONVO_NAME, thread.getUser2().getRealName());
        } else {
            messageList.putExtra(MessageListFragment.EXTRA_CONVO_NAME, thread.getUser1().getRealName());
        }

        finish();
        startActivity(messageList);
    }

    private List<User> getUserList() {
        return new DatabaseHelper(this).findAllUsers();
    }

    // returns a string list for the auto complete and maps those strings in a HashMap
    // so that we can use them to find the User objects to send the message to.
    private List<String> getAutoCompleteList(List<User> users) {
        List<String> strings = new ArrayList<>();

        for (User u : users) {
            String text = u.getRealName() + " (" + u.getUsername() + ")";
            strings.add(text);

            userNameMap.put(text, u);
        }

        return strings;
    }
}
