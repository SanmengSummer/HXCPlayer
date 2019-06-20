package com.summer.chxplayer.widght.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.summer.chxplayer.widght.hxplayer.FullScreenPlayer;
import com.summer.chxplayer.widght.hxplayer.PhonePlayer;
import com.summer.chxplayer.widght.hxplayer.SimplePlayer;
import com.summer.chxplayer.widght.hxplayer.TvFullScreenPlayer;
import com.summer.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;


/**
 * Created by huanxia on 2017/12/29.
 */

public class PlayerUtils {
    private static PlayerUtils mInstance;
    private int width;
    private int height;
    private boolean isChangePlayerSize;
    private boolean isFullScreen;
    private boolean isTV;
    private Bundle instanceState;
    private static Context mContext;

    /**
     * 仅跳转到播放Activity
     *
     * @param activity 所在activity
     * @param path     播放地址
     */
    public static void skipFullScreenPlayer(Activity activity, String path) {
        skipFullScreenPlayer(activity, path, false);
    }

    /**
     * 仅跳转到播放Activity
     *
     * @param activity 所在activity
     * @param path     播放地址
     * @param isTv     是否是tv
     */
    public static void skipFullScreenPlayer(Activity activity, String path, boolean isTv) {
        activity.startActivity(isTv ? TvFullScreenPlayer.newIntent(activity, path)
                : FullScreenPlayer.newIntent(activity, path));
    }

    /**
     * 仅跳转到播放Activity
     *
     * @param activity  所在activity
     * @param mPlayList 播放列表
     */
    public static void skipFullScreenPlayer(Activity activity, ArrayList<MediaModel> mPlayList) {
        skipFullScreenPlayer(activity, mPlayList, false);
    }

    /**
     * 仅跳转到播放Activity
     *
     * @param activity  所在activity
     * @param mPlayList 播放列表
     * @param isTv      是否是tv
     */
    public static void skipFullScreenPlayer(Activity activity, ArrayList<MediaModel> mPlayList, boolean isTv) {
        activity.startActivity(isTv ? TvFullScreenPlayer.newIntent(activity, mPlayList) : FullScreenPlayer.newIntent(activity, mPlayList));
    }


    /**
     * 全屏和小屏的切换（全参数）
     *
     * @param activity 所在activity
     * @param player   player
     */
    public void skipFullScreenPlayer(Activity activity, SimplePlayer player) {
        if (!isChangePlayerSize) {
            if (isFullScreen) {
                activity.finish();
            } else {
                activity.startActivity(isTV ? TvFullScreenPlayer.newIntent(activity, instanceState)
                        : FullScreenPlayer.newIntent(activity, instanceState));
                player.pause();
            }
        } else {
            setNativeFull(activity, player, isFullScreen, isTV, width, height);
        }
    }

    //使用原生改变控件尺寸
    private static void setNativeFull(Activity activity, SimplePlayer player, boolean isFullScreen, boolean isTV, int width, int height) {
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int windowHeight = metrics.heightPixels;//获取当前windowHeight
        int windowWidth = metrics.widthPixels;//windowWidth
//        View vp = activity.findViewById(Window.ID_ANDROID_CONTENT);//获取当前window
//        color = vp.getDrawingCacheBackgroundColor();//获取当前页面背景颜色RelativeLayout
        LinearLayout.LayoutParams horizontalFull = new LinearLayout.LayoutParams(
                windowHeight, windowWidth);//phone版横屏；
        LinearLayout.LayoutParams verticalFull = new LinearLayout.LayoutParams(
                windowWidth, windowHeight);//tv版不变；
        LinearLayout.LayoutParams normalSize = new LinearLayout.LayoutParams(
                width, height);//原player尺寸；
        player.setLayoutParams(isFullScreen ? normalSize : isTV ? verticalFull : horizontalFull);
        if (!isFullScreen) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//强制为动态横屏
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//clear flags
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
        }
    }

    public static PlayerUtils getInstance(Context context) {
        mContext = context;
        if (null == mInstance) {
            mInstance = new PlayerUtils();
        }
        return mInstance;
    }

    public void saveState(@Nullable Bundle savedInstanceState) {
        if (null == savedInstanceState) return;
        width = savedInstanceState.getInt(Param.BundleParam.width, 0);
        height = savedInstanceState.getInt(Param.BundleParam.height, 0);
        isChangePlayerSize = savedInstanceState.getBoolean(Param.BundleParam.isChangePlayerSize, false);
        isFullScreen = savedInstanceState.getBoolean(Param.BundleParam.isFullScreen, false);
        isTV = savedInstanceState.getBoolean(Param.BundleParam.isTV, false);
        instanceState = savedInstanceState;
    }
}
