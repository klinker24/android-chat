package com.uiowa.chat.fragments;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.uiowa.chat.activities.MessageListActivity;
import com.uiowa.chat.adapters.ThreadArrayAdapter;
import com.uiowa.chat.data.Thread;
import com.uiowa.chat.data.DatabaseHelper;
import com.uiowa.chat.utils.RegistrationUtils;
import com.uiowa.chat.utils.api.Sender;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment to hold the conversation list.
 *
 * What to learn:
 *      - Android provides a ListFragment for basic lists like this, it handles the loading message
 *          for you, as well as provides access to the list view. So we don't even have to make a layout
 *          for this fragment.
 *       - There is a broadcast receiver to pick up when a message is sent or received... This receiver
 *          doesn't do anything but refresh the thread list and reset the adapter.
 *       - It is common to register and unregister the receivers in the onResume() and onPause() function.
 *          If you keep them registered all the time, there are memory leaks that happen and it actually
 *          really can slow down the startup time and performance of your app.
 *       - onPause() and onResume() are part of a fragment and activities lifecycle. Google does a much better
 *          job talking about lifecycle than I can do:
 *          http://developer.android.com/training/basics/activity-lifecycle/index.html
 */
public class ConversationFragment extends ListFragment {

    // With fragments, we always use a default constructor, when you have things you want to send from
    // the activity to the fragment, add a bundle of 'arguments' to it. You can see an example of this
    // under the MessageListFragment.

    // a static newInstance() method like this one, while not required, is the most common convention
    // because it allows you to easily manage the bundles that I talked about.
    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    private BroadcastReceiver sentBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            fillThreadList();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        // An small explanation of Intent filters and these types of broadcast receivers can be
        // found in the GCMRegisterActivity class
        IntentFilter filter = new IntentFilter();
        filter.addAction(Sender.SENT_BROADCAST);

        getActivity().registerReceiver(sentBroadcastReceiver, filter);



        // not very efficient, but easy. We are just telling the fragment to update the list every time
        // this fragment is brought to the foreground. That way, the list will always be up to date.
        fillThreadList();
    }

    @Override
    public void onPause() {
        // remember to unregister so that we don't get those memory leaks.
        getActivity().unregisterReceiver(sentBroadcastReceiver);

        super.onPause();
    }

    @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // we don't need to set a layout, since we are just using the provided list, so we
        // just fill those lists
        fillThreadList();
    }

    private void setConversationAdapter(List<Thread> conversations) {
        final ThreadArrayAdapter adapter = new ThreadArrayAdapter(getActivity(), conversations);
        setListAdapter(adapter);

        // We want to be able to open up the thread and view the messages after it is clicked.
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                RegistrationUtils utils = new RegistrationUtils();
                long currentUserId = utils.getMyUserId(getActivity());


                Thread thread = adapter.getItem(position);

                // I talk about adding these extras to the intent in the MessageListActivity.
                // basically this is the way that we quickly send data between different
                // portions of the app.
                Intent messageList = new Intent(getActivity(), MessageListActivity.class);
                messageList.putExtra(MessageListFragment.EXTRA_THREAD_ID, thread.getThreadId());

                if (thread.getUser1Id() == currentUserId) {
                    messageList.putExtra(MessageListFragment.EXTRA_CONVO_NAME, thread.getUser2().getRealName());
                } else {
                    messageList.putExtra(MessageListFragment.EXTRA_CONVO_NAME, thread.getUser1().getRealName());
                }

                startActivity(messageList);
            }
        });
    }

    public void fillThreadList() {
        // uses a subclassed AsyncTask so that we can specify the type of data we are sending
        // across the threads
        new GetThreads().execute();
    }

    /*
        We saw a very generic AsyncTask earlier in the ConversationListActivity class.

        This one is different than that because it allows us to specify what type of data we
        want to send between the background thread and the UI thread. Useful if you want to be specific
        about things, but we also cannot call this type of thread in-line like we were able to do
        with the generic, 'Object' AsyncTask.

        We want to do any database calls on a background thread so that we don't bog down the UI thread.
        This is important on mobile devices without as much power (may not be true now a days), but we
        don't want the user to realize their devices are slow or laggy!
     */
    class GetThreads extends AsyncTask<Void, Void, List<Thread>> {
        @Override
        protected List<Thread> doInBackground(Void... arg0) {
            return getSortedList(new DatabaseHelper(getActivity()).findAllConversations());
        }

        @Override
        protected void onPostExecute(List<Thread> result) {
            // the result comes into the UI thread so we can update the list.
            setConversationAdapter(result);
        }
    }

    // Basic Java sort method by comparing the timestamps
    private List<Thread> getSortedList(List<Thread> threads) {
        Collections.sort(threads, new Comparator<Thread>() {
            public int compare(Thread result1, Thread result2) {
                long time1 = result1.getLatestMessage().getTimestamp();
                long time2 = result2.getLatestMessage().getTimestamp();

                if (time2 < time1) {
                    return -1;
                } else if (time1 < time2) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        return threads;
    }
}
