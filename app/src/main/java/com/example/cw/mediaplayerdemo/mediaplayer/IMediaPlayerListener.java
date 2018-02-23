package com.example.cw.mediaplayerdemo.mediaplayer;

import android.media.MediaPlayer;

/**
 * Created by cw on 2018/2/19.
 * 整合MediaPlayer相关接口
 */

public interface IMediaPlayerListener {

    void onPrepared(MediaPlayer mediaPlayer);

    void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height);

    void onBufferingUpdate(MediaPlayer mediaPlayer, int position);

    void onCompletion(MediaPlayer mediaPlayer);

    void onSeekComplete(MediaPlayer mediaPlayer);

    boolean onError(MediaPlayer mediaPlayer, int width, int height);

    boolean onInfo(MediaPlayer mp, int what, int extra);

}
