package com.example.cw.mediaplayerdemo.mediaplayer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.cw.mediaplayerdemo.R;

import java.io.IOException;
import java.util.Map;

/**
 * Created by cw on 2018/2/18.
 * TextureView + MediaPlayer
 */

public class QYVideoPlayer extends FrameLayout implements IQYVideoPlayer, TextureView.SurfaceTextureListener{

    private static final String TAG = "QYVideoPlayer";

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_BUFFERING_PLAYING = 5;
    public static final int STATE_BUFFERING_PAUSED = 6;
    public static final int STATE_COMPLETED = 7;

    public static final int MODE_NORMAL = 11;
    public static final int MODE_FULL_SCREEN = 12;

    private FrameLayout mContainer;
    private Context mContext;
    private QYTextureView mTextureView;
    private QYMediaPlayer mMediaPlayer;
    private String mUrl;
    private Map<String,String> mHeaders;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private AudioManager mAudioManager;
    private int mBufferedPercentage;
    private QYVideoPlayerController mController;
    private int mCurrentState = STATE_IDLE;
    private int mCurrentMode = MODE_NORMAL;

    private LinearLayout bottomControllerLayout;
    private Button centerStartBtn;
    private Button startPauseBtn;
    private Button normalFullScreenBtn;
    private TextView currentPositionTv;
    private TextView totalPositionTv;
    private SeekBar mSeekBar;

    private final Handler mHandler = new Handler();
    private long mDuration;

    public QYVideoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public QYVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public QYVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
    }

    public void setController(QYVideoPlayerController controller){
        mController = controller;
        int screenWidth = QYVideoPlayerUtil.getScreenWidth(mContext);
        //小视频 默认16：9
        LayoutParams params = new LayoutParams(screenWidth, screenWidth *9/16);
        this.addView(mContainer,params);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View relativeLayout = inflater.inflate(R.layout.video_controller_layout, mContainer);
        bottomControllerLayout = relativeLayout.findViewById(R.id.bottom_controller_layout);
        centerStartBtn = relativeLayout.findViewById(R.id.center_start_btn);
        startPauseBtn = relativeLayout.findViewById(R.id.start_pause_btn);
        normalFullScreenBtn = relativeLayout.findViewById(R.id.normal_full_screen_btn);
        currentPositionTv = relativeLayout.findViewById(R.id.current_position);
        totalPositionTv = relativeLayout.findViewById(R.id.total_position);
        mSeekBar = relativeLayout.findViewById(R.id.seekBar);
        setClickListeners();
    }

    private void setClickListeners(){
        centerStartBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentState == STATE_PREPARED) {
                    startMediaPlayer();
                }
            }
        });
        startPauseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED){
                    startMediaPlayer();
                }else if (mCurrentState == STATE_PLAYING){
                    pauseMediaPlayer();
                    mCurrentState = STATE_PAUSED;
                }
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long duration = getDuration();
                long currentPosition = getCurrentPosition();
                currentPositionTv.setText(QYVideoPlayerUtil.formatTime(currentPosition));
                totalPositionTv.setText(QYVideoPlayerUtil.formatTime(duration));
                mHandler.postDelayed(this,1000);
                mSeekBar.setProgress((int)(currentPosition/ duration));
            }
        }, 200);
    }

    private void startMediaPlayer(){
        mMediaPlayer.start();
        startPauseBtn.setBackgroundResource(R.drawable.ic_player_pause);
        centerStartBtn.setVisibility(INVISIBLE);
    }

    private void pauseMediaPlayer(){
        mMediaPlayer.pause();
        startPauseBtn.setBackgroundResource(R.drawable.ic_player_start);
    }

    private void initAudioManager(){
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            if (mAudioManager != null) {
                mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }
        }
    }

    private void initTextureView(){
        mTextureView = new QYTextureView(mContext);
        mTextureView.setSurfaceTextureListener(this);
    }

    private void addTextureView(){
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mTextureView,layoutParams);
    }

    private void openMediaPlayer(){
        this.setKeepScreenOn(true);
        mMediaPlayer = new QYMediaPlayer();
        try {
            mMediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl), mHeaders);
            if (mSurface == null){
                mSurface = new Surface(mSurfaceTexture);
            }
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setMediaPlayerListener(new IMediaPlayerListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mCurrentState = STATE_PREPARED;
            }

            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {

            }

            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int position) {
                mBufferedPercentage = position;
                Log.d(TAG, "position -> :" + position);
            }

            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mCurrentState = STATE_COMPLETED;
            }

            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {

            }

            @Override
            public boolean onError(MediaPlayer mediaPlayer, int width, int height) {
                mCurrentState = STATE_ERROR;
                return false;
            }

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                switch (what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START :{
                        mCurrentState = STATE_PLAYING;
                        break;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START : {
                        mCurrentState = STATE_BUFFERING_PAUSED;
                        break;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END : {
                        mCurrentState = STATE_BUFFERING_PLAYING;
                        break;
                    }
                    default:
                        break;
                }

                return false;
            }
        });
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurfaceTexture == null){
            mSurfaceTexture = surfaceTexture;
        }else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
        openMediaPlayer();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void setUp(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
    }

    @Override
    public void start() {
        if (mCurrentState == STATE_IDLE) {
            initAudioManager();
            initTextureView();
            addTextureView();
        }else {
            Log.d(TAG, "start: 只有在mCurrent == STATE_IDLE时才能调用start方法");
        }
    }

    @Override
    public void start(long position) {

    }

    @Override
    public void restart() {

    }

    @Override
    public void seekTo(long position) {
        mMediaPlayer.seekTo((int) position);
    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {

    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isComplete() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public int getMaxVolume() {
        if (mAudioManager != null){
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int getVolume() {
        if (mAudioManager != null){
            return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mMediaPlayer != null){
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (mMediaPlayer != null){
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferedPercentage;
    }

    @Override
    public float getSpeed() {
        //IjkMediaPlayer才提供
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        //IjkMediaPlayer才提供
        return 0;
    }

    @Override
    public void enterFullScreenMode() {

    }

    @Override
    public void enterWindowMode() {

    }

    @Override
    public boolean isFullScreenMode() {
        return mCurrentMode == MODE_FULL_SCREEN;
    }

    @Override
    public void releasePlayer() {
        if (mAudioManager != null){
            mAudioManager.abandonAudioFocus(null);
            mAudioManager = null;
        }
        if (mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurfaceTexture != null){
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null){
            mSurface.release();
            mSurface = null;
        }
        mCurrentState = STATE_IDLE;
    }

    @Override
    public void release() {
        if (isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused()){
            QYVideoPlayerUtil.saveCurrentPercentage(mContext, mUrl, mBufferedPercentage);
        }else if (isComplete()){
            QYVideoPlayerUtil.saveCurrentPercentage(mContext, mUrl, 0);
        }
        if (isFullScreenMode()){

        }
        mCurrentMode = MODE_NORMAL;
        releasePlayer();
    }
}
