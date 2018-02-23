package com.example.cw.mediaplayerdemo.mediaplayer;

import android.media.MediaPlayer;

/**
 * Created by cw on 2018/2/19.
 */

public class QYMediaPlayer extends MediaPlayer{

    public void setMediaPlayerListener(final IMediaPlayerListener mediaPlayerListener) {
        this.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayerListener.onPrepared(mediaPlayer);
            }
        });
        this.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                mediaPlayerListener.onVideoSizeChanged(mediaPlayer,i,i1);
            }
        });
        this.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                mediaPlayerListener.onBufferingUpdate(mediaPlayer,i);
            }
        });
        this.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayerListener.onCompletion(mediaPlayer);
            }
        });
        this.setOnSeekCompleteListener(new OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                mediaPlayerListener.onSeekComplete(mediaPlayer);
            }
        });
        this.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return mediaPlayerListener.onError(mediaPlayer,i,i1);
            }
        });
        this.setOnInfoListener(new OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                return mediaPlayerListener.onInfo(mediaPlayer,i,i1);
            }
        });
    }

}
