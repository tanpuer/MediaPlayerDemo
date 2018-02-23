package com.example.cw.mediaplayerdemo.mediaplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by cw on 2018/2/19.
 */

public class QYTextureView extends TextureView {

    public QYTextureView(Context context) {
        this(context, null);
    }

    public QYTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QYTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
