package com.uiowa.chat.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.uiowa.chat.R;
import com.uiowa.chat.utils.RegistrationUtils;

/**
 * This page acts as login and register.
 *
 * We will set up "Real Name" and "username" messageText input boxes as well as a login button
 *
 * When you hit the login button, it will send your device and user info to the Google App Engine
 * server. If you already had an account with the supplied username, it will update your name
 * and device info. If you didn't have an account, it will create one for you
 */
public class LoginActivity extends GCMRegisterActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the layout for the activity
        setContentView(R.layout.activity_login);

        // finds the views you created
        final EditText realName = (EditText) findViewById(R.id.screen_name) ;
        final EditText userName = (EditText) findViewById(R.id.user_name);
        final Button loginBtn = (Button) findViewById(R.id.login_button);

        // sets up the button click for when you login
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String realNameText = realName.getText().toString();
                String userNameText = userName.getText().toString();

                if(!hasInputErrors(realName, userName, realNameText, userNameText)) {
                    loginBtn.setEnabled(false);
                    registerDevice(userNameText, realNameText);
                }

            }
        });
    }

    // checks if either field is blank.
    // it is is, we can set an error on that edit messageText so the user knows to fill it in.
    private boolean hasInputErrors(EditText realName, EditText userName, String realNameText, String userNameText) {

        boolean hasError = false;
        if(TextUtils.isEmpty(realNameText)) {
            realName.setError(getString(R.string.screen_name_missing));
            hasError = true;
        }
        if(TextUtils.isEmpty(userNameText)) {
            userName.setError(getString(R.string.user_name_missing));
            hasError = true;
        }

        return hasError;
    }

    // call this from the button on click listener
    private void registerDevice(String username, String realName) {
        // calls method in GCMRegisterActivity to close the login activity after the login/registration
        // is successful.
        registerCompleteReceiver();

        // send the registration info to the server
        // works on a background thread, which is why we need the callback
        new RegistrationUtils().registerInBackground(this, username, realName);
    }
}
