package com.uiowa.chat.activities;

import android.app.ActionBar;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.uiowa.chat.R;

/**
 * This class provides the "Material Design" style UI for the app.
 *
 * Material Design is Google's latest design language:
 * http://www.google.com/design/spec/material-design/introduction.html
 *
 * It draws the blue app bar at the top using the AppCompat libraries' Toolbar widget.
 * On devices that use Lollipop, it will also color the status bar a darker shade of the app bar's blue.
 *
 *
 * We won't be touching this class at all.
 */
public abstract class AbstractToolbarActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(getResources().getColor(R.color.blue_primary_color_dark));
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View view) {

        LinearLayout layout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_toolbar, null, false);

        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar, null, false);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        layout.addView(toolbar);
        layout.addView(view);

        super.setContentView(layout);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null, false));
    }

    public void setDisplayHomeAsUp() {
        getToolbar().setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public Toolbar getToolbar() {
        return this.toolbar;
    }

    public void setActivityTitle(String title) {
        this.toolbar.setTitle(title);
    }

}
