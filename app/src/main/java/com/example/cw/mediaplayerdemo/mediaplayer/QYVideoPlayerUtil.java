package com.example.cw.mediaplayerdemo.mediaplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by cw on 2018/2/22.
 * Utils for QYVideoPlayer
 */

public class QYVideoPlayerUtil {

    public static final String QYVIDEO_PLAYER_POSITION = "QYVIDEO_PLAYER_POSITION";

    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 保存播放进度，以便下次继续播放
     * @param url 视频连接url
     * @param percentage 视频播放进度
     */
    public static void saveCurrentPercentage(Context context, String url, int percentage){
        context.getSharedPreferences(QYVIDEO_PLAYER_POSITION, Context.MODE_PRIVATE).edit().putInt(url,percentage).apply();
    }

    /**
     * 取出视频上次播放的进度
     * @param url 视频连接url
     */
    public static int getCurrentPercentage(Context context, String url){
        return context.getSharedPreferences(QYVIDEO_PLAYER_POSITION, Context.MODE_PRIVATE).getInt(url, 0);
    }

    /**
     * 毫秒转变成 11:11:11格式
     * @param milliSeconds 视频时常毫秒
     */
    public static String formatTime(long milliSeconds){
        if (milliSeconds <= 0 || milliSeconds >= 24*60*60*1000){
            return "00:00";
        }
        long totalSeconds = milliSeconds/1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds/60) %60;
        long hours = totalSeconds/3600;
        Formatter formatter = new Formatter(new StringBuilder(), Locale.getDefault());
        if (hours > 0){
            return formatter.format("%d:%02d:%02d",hours,minutes,seconds).toString();
        }else {
            return formatter.format("%02d:%02d",minutes,seconds).toString();
        }
    }

    public static void hideActionBar(Activity context){
        ActionBar ab = context.getActionBar();
        if (ab != null){
            ab.hide();
        }
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void showActionBar(Activity context){
        ActionBar ab = context.getActionBar();
        if (ab != null){
            ab.show();
        }
    }
}
