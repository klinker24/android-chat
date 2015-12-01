package com.uiowa.chat.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.JsonArray;
import com.uiowa.chat.R;
import com.uiowa.chat.api_objects.MessagingApi;
import com.uiowa.chat.api_objects.ThreadApi;
import com.uiowa.chat.api_objects.UserApi;
import com.uiowa.chat.data.sql.ThreadDataSource;
import com.uiowa.chat.data.sql.MessageDataSource;
import com.uiowa.chat.data.sql.UserDataSource;
import com.uiowa.chat.fragments.ConversationFragment;
import com.uiowa.chat.services.PushNotificationService;
import com.uiowa.chat.utils.BaseUtils;
import com.uiowa.chat.utils.RegistrationUtils;

/**
 * This class holds the conversation list for the messages.
 *
 * Functionality:
 *      - It is the launcher class (what we open when clicking the item on the homescreen.)
 *      - It opens the login activity if the user has not signed up
 *      - It fetches the data for the app
 *      - It overrides the options menu and adds the "new message" and "sync" buttons
 */
public class ConversationListActivity extends GCMRegisterActivity {

    // reference to the fragment for the conversation list.
    // Android uses fragments more often than activies now a days because they are easier to extend off of.
    // for example, changing this to a tablet layout, we could easily place the conversation list on the left
    // half and one of the open conversations on the right half of the screen instead of filling
    // the entire screen with the conversation list.
    private ConversationFragment conversationFragment;

    // Callback id for after the user returns from the login screen.
    private static final int RESULT_LOGIN = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sets the layout for the activity. this layout just holds a fragment
        setContentView(R.layout.activity_fragment_holder);

        // Checks if the user is logged in.
        // if not, open the login screen, otherwise, load the conversations
        BaseUtils utils = new BaseUtils();
        if (!utils.hasRegistered(this)) {

            // start the activity for a result so that this activity knows when the user is logged in
            // and can refresh the data.
            startActivityForResult(new Intent(this, LoginActivity.class), RESULT_LOGIN);
        }

        // create the conversation list fragment
        conversationFragment = ConversationFragment.newInstance();

        // add the fragment to the UI in place of the conversation_fragment layout
        FragmentTransaction trans = getFragmentManager().beginTransaction();
        trans.replace(R.id.conversation_fragment, conversationFragment);
        trans.commit();

        // marshmallow, request the necessary permissions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = checkSelfPermission(Manifest.permission.SEND_SMS);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.SEND_SMS}, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // this is called when the user returns from the login activity
        switch (requestCode) {
            case RESULT_LOGIN:
                fetchData();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // cancels the notifications for new messages
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(PushNotificationService.ADMIN_NOTIFICATION_ID);
        mNotifyMgr.cancel(PushNotificationService.MESSAGE_NOTIFICATION_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // "inflate" the options menu to add our "new message" and "sync" buttons
        // these buttons are defined in the /res/menu/menu_conversation_list.xml file.
        getMenuInflater().inflate(R.menu.menu_conversation_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // when the user clicks one of our menu options, this is where we define the functionality
        switch (item.getItemId()) {
            case R.id.menu_new_message:
                // a new intent that will open a screen to input a new message
                Intent newMessage = new Intent(this, NewMessageActivity.class);
                startActivity(newMessage);
                return true;
            case R.id.menu_refresh_conversations:
                refreshData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshData() {
        // not very efficient, but sometimes we are in a rush,
        // delete everything off device
        ThreadDataSource.getInstance(this).deleteAllThreads();
        MessageDataSource.getInstance(this).deleteAllMessages();
        UserDataSource.getInstance(this).deleteAllUsers();

        // fetch everything from server
        fetchData();
    }

    private void fetchData() {

        /*
            ASYNC TASKS:

            Async Task's preform background work in Android.

            They start up a new thread during the doInBackground(...) function,
            but remain on the UI thread during the onPreExecute() and onPostExecute() functions.

            This is important because Android doesn't allow you to make any data calls while on the UI
            thread, but they also do not let you update your views on a background thread!
            Both will throw an exception and kill the activity.


            For background work, you can also create a generic Thread(Runnable) object and use its start() method
            to do background work. Since AsyncTask's have built in callbacks to the UI thread though,
            they are much more widely used.
         */
        new AsyncTask() {

            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                // show a progress dialog so the user can't interact and knows a background
                // task is going on.

                dialog = new ProgressDialog(ConversationListActivity.this);
                dialog.setMessage("Loading Data...");
                dialog.setCancelable(false);
                dialog.show();
            }

            @Override
            protected Object doInBackground(Object[] params) {

                // get the current user's id
                RegistrationUtils registrationUtils = new RegistrationUtils();
                long userId = registrationUtils.getMyUserId(ConversationListActivity.this);

                // find and save all the threads for that user id.
                ThreadApi threadApi = new ThreadApi();
                JsonArray threadList = threadApi.findUserThreads(userId);
                if(threadList != null) {
                    ThreadDataSource dataSource = ThreadDataSource.getInstance(ConversationListActivity.this);
                    dataSource.createThreads(threadList);
                }

                // find and save all the current users for the app so that we can interact with them
                UserApi userApi = new UserApi();
                JsonArray userList = userApi.findAllUsers();
                if(userList != null) {
                    UserDataSource dataSource = UserDataSource.getInstance(ConversationListActivity.this);
                    dataSource.createUsers(userList);
                }

                // find and save all the messages for the user
                MessagingApi messagingApi = new MessagingApi();
                JsonArray messages = messagingApi.findUserMessages(userId);
                if (messages != null) {
                    MessageDataSource dataSource = MessageDataSource.getInstance(ConversationListActivity.this);
                    dataSource.createMessages(messages);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                // dismiss the dialog
                dialog.dismiss();

                // recreates the listview in our fragment to show all the new data
                conversationFragment.fillThreadList();
            }

        }.execute(null, null, null);
    }
}
