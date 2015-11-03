package com.uiowa.chat.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.uiowa.chat.R;

/**
 * This is a frame layout that we can host an EditText in.
 *
 * When you wrap your edit text in this layout (example in activity_login.xml),
 * it will automatically move the hint text above what you are typing so that it is still
 * available to view even when you have text in your edit text.
 *
 * Custom views like this can be more complex, I doubt that it is something we will
 * have time to get into in this workshop, but they are really fun and powerful
 * if you would like to experiment with them more on your own.
 */
public class LabeledEditText extends FrameLayout {

    private static final long ANIMATION_LENGTH = 150;
    private static final int LABEL_TEXT_SIZE = 13;
    private static final float DEFAULT_LABEL_PADDING = 12f;

    private TextView mLabel;
    private EditText mEditText;

    public LabeledEditText(Context context) {
        this(context, null);
    }

    public LabeledEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabeledEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int padding = dipsToPix(DEFAULT_LABEL_PADDING);
        mLabel = new TextView(context);
        mLabel.setPadding(padding, 0, padding, 0);
        mLabel.setVisibility(INVISIBLE);
        mLabel.setTextSize(LABEL_TEXT_SIZE);
        mLabel.setTextColor(context.getResources().getColor(R.color.blue_accent_color));

        addView(mLabel, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            if (mEditText != null) {
                return;
            }

            final LayoutParams lp = new LayoutParams(params);
            //lp.gravity = Gravity.BOTTOM;
            lp.topMargin = (int) mLabel.getTextSize();
            params = lp;

            setEditText((EditText) child);
        }

        super.addView(child, index, params);
    }

    public void setEditText(EditText child) {
        mEditText = child;

        mEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    if (mLabel.getVisibility() == View.VISIBLE) {
                        hideLabel();
                    }
                } else {
                    if (mLabel.getVisibility() != View.VISIBLE) {
                        showLabel();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

        mLabel.setText(mEditText.getHint());
    }

    private void showLabel() {
        mLabel.setVisibility(View.VISIBLE);
        mLabel.setAlpha(0f);
        mLabel.setTranslationY(mLabel.getHeight());
        mLabel.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(ANIMATION_LENGTH)
                .setListener(null)
                .start();
    }

    private void hideLabel() {
        mLabel.setAlpha(1f);
        mLabel.setTranslationY(0f);
        mLabel.animate()
                .alpha(0f)
                .translationY(mLabel.getHeight())
                .setDuration(ANIMATION_LENGTH)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLabel.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private int dipsToPix(float dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
                getResources().getDisplayMetrics());
    }
}
