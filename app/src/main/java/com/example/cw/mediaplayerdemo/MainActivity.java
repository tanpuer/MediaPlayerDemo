package com.example.cw.mediaplayerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.cw.mediaplayerdemo.mediaplayer.QYVideoPlayer;
import com.example.cw.mediaplayerdemo.mediaplayer.QYVideoPlayerController;

/**
 * Created by cw on 2018/2/18.
 */

public class MainActivity extends Activity {

    private QYVideoPlayer qyVideoPlayer;
    private QYVideoPlayerController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qyVideoPlayer = new QYVideoPlayer(this);
        qyVideoPlayer.setUp("http://10.5.162.46:8082/video", null);
        qyVideoPlayer.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setContentView(qyVideoPlayer);
        qyVideoPlayer.start();
        mController = new QYVideoPlayerController(qyVideoPlayer);
        qyVideoPlayer.setController(mController);
    }
}
