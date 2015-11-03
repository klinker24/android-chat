package com.uiowa.chat.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.uiowa.chat.R;
import com.uiowa.chat.fragments.MessageListFragment;

/**
 * Simple Activity to hold the list of messages in the selected conversation
 *
 * What to learn:
 *      - When creating an intent, we can add extras to that intent. You will notice that we get some extras
 *          here. This is just data that we need to send between the two activities/fragments.
 *          This activity gets an extra that contains a user name to put in the app bar as well as an extra
 *          for the thread that we want to view (which is sent along to our fragment)
 *      - What if the previous activity didn't send the correct data to this one? There is a not about
 *          defaults below as well.
 */
public class MessageListActivity extends AbstractToolbarActivity {

    private MessageListFragment messageListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment_holder);

        // Here is where we get the extra conversation name to put in the app bar.
        // You don't need to provide a default for getting a string extra (the system makes it null)
        String convoTitle = getIntent().getStringExtra(MessageListFragment.EXTRA_CONVO_NAME);

        // you do need to provide a default for other types. For the long here, we just use a -1.
        // you should use some error handling on these defaults of course, but, i didn't worry about it here.
        long threadId = getIntent().getLongExtra(MessageListFragment.EXTRA_THREAD_ID, -1);


        // create and attach the message list fragment
        messageListFragment =  MessageListFragment.getInstance(threadId);

        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.conversation_fragment, messageListFragment);
        trans.commit();

        // set the activity title to what we found earlier
        setActivityTitle(convoTitle);


        // set the activity to display the "<-" arrow in the top left of the app bar
        setDisplayHomeAsUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // when they hit the "<-" arrow on the app bar, close the activity.
        switch (item.getItemId()) {
            case android.R.id.home:

                // handles the '<-' arrow in the app bar
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
