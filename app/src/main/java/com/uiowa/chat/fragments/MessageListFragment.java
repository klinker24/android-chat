package com.uiowa.chat.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.uiowa.chat.R;
import com.uiowa.chat.adapters.MessageArrayAdapter;
import com.uiowa.chat.data.DatabaseHelper;
import com.uiowa.chat.data.Message;
import com.uiowa.chat.data.User;
import com.uiowa.chat.data.sql.MessageDataSource;
import com.uiowa.chat.utils.RegistrationUtils;
import com.uiowa.chat.utils.api.Sender;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment holds the message list and the reply bar for a threaded conversation.
 *
 * What to learn:
 *      - Using a bundle to pass information from an activity to a fragment as arguments
 */
public class MessageListFragment extends Fragment {

    public static final String EXTRA_THREAD_ID = "thread_id";
    public static final String EXTRA_CONVO_NAME = "convo_name";

    /*
        Here, we create the bundle and add the data to it, then
        end that bundle along with the fragment when it is created.

        You NEVER want to use a constructor with arguements for something like
        this on a fragment. This is because when a savedInstanceState of the fragment is restored
        (which can be done after changing orientation, or when the activity is kicked out of memory for
        whatever reason and the fragment isn't), it will call the default constructor and your
        fragment won't contain the data that you needed to send with it.
     */
    public static MessageListFragment getInstance(long threadId) {
        Bundle b = new Bundle();
        b.putLong(EXTRA_THREAD_ID, threadId);

        MessageListFragment fragment = new MessageListFragment();
        fragment.setArguments(b);
        return fragment;
    }

    private BroadcastReceiver sentBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new GetMessages().execute();
        }
    };

    private Sender sender;
    private RegistrationUtils registrationUtils;

    private long threadId;

    private ListView listView;
    private EditText replyBar;
    private ImageButton sendButton;
    private ProgressDialog progressDialog;

    // We actually need to make a layout for this fragment, so we override this method and return
    // the view containing our inflated layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        sender = new Sender(getActivity());
        registrationUtils = new RegistrationUtils();

        // inflate the view and set the container ViewGroup as the parent
        View v = inflater.inflate(R.layout.fragment_message_list, container, false);

        // find the views from the inflated layout
        listView = (ListView) v.findViewById(R.id.listview);
        replyBar = (EditText) v.findViewById(R.id.reply_text);
        sendButton = (ImageButton) v.findViewById(R.id.send_button);


        // get the arguements and start loading the data and filling the list
        threadId = getArguments().getLong(EXTRA_THREAD_ID);
        new GetMessages().execute();

        // set the functionality of the send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(replyBar.getText().toString());
                replyBar.setText(null);
            }
        });

        final EditText input = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setView(input)
                .setTitle("New Encrypted Conversations")
                .setMessage("Ready to start a new encrypted conversation with this person? Enter " +
                "their phone number.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RegistrationUtils utils = new RegistrationUtils();
                        DatabaseHelper helper = new DatabaseHelper(getActivity());
                        final long currentUserId = utils.getMyUserId(getActivity());
                        final User currentUser = helper.findUser(currentUserId);

                        String text = input.getText().toString();
                        SmsManager manager = SmsManager.getDefault();
                        manager.sendTextMessage(text, null, "Hey from encrypted chat, want to " +
                                "start a conversation with " + currentUser.getUsername() + "?",
                                null, null);

                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Waiting for response...");
                        progressDialog.show();
                    }
                })
                .show();

        // return the view that we inflated
        return v;
    }

    // Same idea as the ConversationFragment with the onResume and onPause methods
    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Sender.SENT_BROADCAST);

        getActivity().registerReceiver(sentBroadcastReceiver, filter);

        new GetMessages().execute();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(sentBroadcastReceiver);
        
        super.onPause();
    }

    // uses the sender object to send a new message in the current thread.
    // when the sending is complete, it will broadcast the Sender.SENT_BROADCAST,
    // which is picked up by our receiver to update the list.
    private void sendMessage(String message) {
        sender.sendThreadedMessage(
                threadId,
                registrationUtils.getMyUserId(getActivity()),
                message
        );
    }

    private void setMessageAdapter(List<Message> messages) {
        MessageArrayAdapter adapter = new MessageArrayAdapter(getActivity(), messages);
        listView.setAdapter(adapter);
    }

    /*
        Another subclassed AsyncTask to load our data. Same idea as on the Conversation fragment.
     */
    class GetMessages extends AsyncTask<Void, Void, List<Message>> {
        @Override
        protected List<Message> doInBackground(Void... arg0) {
            return new DatabaseHelper(getActivity()).findThreadMessages(threadId);
        }

        @Override
        protected void onPostExecute(List<Message> result) {
            setMessageAdapter(result);
        }
    }
}
