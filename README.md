####Mediaplayer + TextureView 封装播放器

#####TextureView
在android视频播放时可以直接使用VideoView，VideoView继承自SurfaceView，SurfaceView在现有的View位置上创建一个新的window，内容的显示和渲染都在新的window当中。这使得SurfaceView的绘制和刷新都可以在单独的线程中完成，从而大大提高效率。
但是由于，SurfaceView的内容没有显示在View中而是现实在新建的window当中，使得SurfaceView的显示不受view的属性控制，不能平移，缩放等变换，也不能放在RecycylerView中，一些View的特性也无法使用。
TextureView是在androd4.0引入的，与SurfaceView相比，它不会创建在新的window来显示内容，而是将内容直接投放到View中，并且可以和其他View一样进行操作，但TextureView的操作必须在使用硬件加速的窗口中使用。
TextureView不能直接使用，须待TextureView准备好才能使用
```java
mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
```
当TextureView准备好后，用其来关联MediaPlayer，作为播放视频的图像视频来源。

####MediaPlayer
MediaPlayer是Android原生的多媒体播放器，可以用它来实现本地或者在线视频音频的播放，同时支持https和rtsp。

MediaPlayer相关接口回调

内容接口回调 | 介绍 | 状态 
- | :-: | -: 
onPreparedListener | 准备监听| Preparing -> Prepared 
OnVideoSizeChangedListener | 视频尺寸变化监听 | -- 
OnInfoListener | 指示信息和警告信息监听 | --
OnCompletionListener | 播放完成监听 | PlaybackCompleted
OnErrorListener | 播放错误监听 | Error
OnBufferUpdateListener | 缓冲更新监听 | --

MediaPlayer相关方法

方法 | 介绍 | 状态
- | :-: | -:
setDataSource | 设置数据源 | Initialized
prepare | 准备同步播放 | Preparing -> Prepared
AsyncPrepare | 准备异步 | PreParing -> Prepared
start | 开始或恢复播放 | Started
pause | 暂停 | Paused
stop | 暂停 | Stopped
seekTo | 到指定时间 | PrePared/Started
reset | 重置 | Idle
setAudioStreamType | 设置音频类型 | --
setDisplay | 设置播放视频的Surface | --
setVolume | 设置声音 | --
getBufferPercentage | 获取缓冲百分比 | --
getCurrentPosition | 获取当前播放位置 | --
getDuration | 获取播放总时间 | --

由于MediaPlayer接口和方法众多，不同状态调用各个方法后状态变化情况也比较复杂，为了解耦和方便定制，对MediaPlayer进行封装。 直接将MediaPlayer封装到QYVideoPlayer里，各种UI状态和操作回调都封装到QYVideoPlayerController里。

####Demo
首先MediaPlayer有众多的监听，将这些监听封装到一起
```java
public interface IMediaPlayerListener {

    void onPrepared(MediaPlayer mediaPlayer);

    void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height);

    void onBufferingUpdate(MediaPlayer mediaPlayer, int position);

    void onCompletion(MediaPlayer mediaPlayer);

    void onSeekComplete(MediaPlayer mediaPlayer);

    boolean onError(MediaPlayer mediaPlayer, int width, int height);

    boolean onInfo(MediaPlayer mp, int what, int extra);

}

//再自定义个VideoPlayer
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
```
这样就避免了创建一个MediaPlayer后进行设置一系列监听

在onSurfaceTextureAvailable回调中拿到surfaceTexture，然后将mediaplayer和mTextureView进行关联即可
```java
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        if (mSurfaceTexture == null){
            mSurfaceTexture = surfaceTexture;
        }else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
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
    }

    private void addTextureView(){
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mTextureView,layoutParams);
    }
```
接下来适时地调用MediaPlayer.start，同时不能忘记mTextureView是要加入container当中，视频就能正常播放了。接下来的就是添加控制UI了，大约的功能有暂停，开始，显示当前进度，显示进度条，显示总时间，全屏切换，手势音量、进度、亮度调节。


