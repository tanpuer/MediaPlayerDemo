package com.example.cw.mediaplayerdemo.mediaplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by cw on 2018/2/24.
 */

public class QYVideoPlayerContainer extends FrameLayout {

    public QYVideoPlayerContainer(@NonNull Context context) {
        super(context);
    }

    public QYVideoPlayerContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public QYVideoPlayerContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QYVideoPlayerContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
