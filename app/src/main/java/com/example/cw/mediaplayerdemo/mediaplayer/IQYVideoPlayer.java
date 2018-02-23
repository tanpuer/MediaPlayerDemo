package com.example.cw.mediaplayerdemo.mediaplayer;

import java.util.Map;

/**
 * Created by cw on 2018/2/18.
 */

public interface IQYVideoPlayer {

    /**
     * set request url and request headers
     * @param url  remote or local url
     * @param headers request headers
     */
    void setUp(String url, Map<String,String> headers);

    /**
     * start to play video
     */
    void start();

    /**
     * start to play video according to the position
     * @param position selected position
     */
    void start(long position);

    /**
     * replay video from beginning
     */
    void restart();

    /**
     * continue to play according to the position
     * @param position selected position
     */
    void seekTo(long position);

    /**
     * set play speed
     * @param speed selected speed
     */
    void setSpeed(float speed);

    /**
     * restart or play from last position
     * @param continueFromLastPosition true: from last position
     */
    void continueFromLastPosition(boolean continueFromLastPosition);

    /**
     * current status of the player
     */
    boolean isIdle();
    boolean isPreparing();
    boolean isPrepared();
    boolean isBufferingPlaying();
    boolean isBufferingPaused();
    boolean isPlaying();
    boolean isPaused();
    boolean isError();
    boolean isComplete();

    /**
     * get the maximum volume
     */
    int getMaxVolume();

    /**
     * get current volume
     */
    int getVolume();

    /**
     * get playing duration of the video /ms
     */
    long getDuration();

    /**
     * get current playing position of the video /ms
     */
    long getCurrentPosition();

    /**
     * get the buffering percentage of the video
     */
    int getBufferPercentage();

    /**
     * get play speed
     */
    float getSpeed();

    /**
     * get the network speed
     */
    long getTcpSpeed();

    /**
     * enter full screen mode
     */
    void enterFullScreenMode();

    /**
     * enter window mode
     */
    void enterWindowMode();

    /**
     * check whether current mode is full screen mode
     */
    boolean isFullScreenMode();

    /**
     * release the player and reset to initial state
     */
    void releasePlayer();

    /**
     * release the player but save current state
     */
    void release();
}
