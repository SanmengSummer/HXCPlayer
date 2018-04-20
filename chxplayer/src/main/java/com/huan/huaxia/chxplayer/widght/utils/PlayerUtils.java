package com.huan.huaxia.chxplayer.widght.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.huan.huaxia.chxplayer.widght.hxplayer.FullScreenPlayer;
import com.huan.huaxia.chxplayer.widght.hxplayer.SimplePlayer;
import com.huan.huaxia.chxplayer.widght.hxplayer.TvFullScreenPlayer;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;


/**
 * Created by huanxia on 2017/12/29.
 */

public class PlayerUtils {
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
     * 全屏和小屏的切换（不附带当前情况，重新开始播放）
     *
     * @param activity         所在activity
     * @param player           player
     * @param isSkipFullScreen 是否调到全Activity播放页面
     * @param isFullScreen     目前是否是全屏
     * @param isTV             是否是tv播放
     * @param width            小屏player宽
     * @param height           小屏player高
     * @param mPlayList        播放列表
     */
    public static void skipFullScreenPlayer(Activity activity, SimplePlayer player, boolean isSkipFullScreen, boolean isFullScreen, boolean isTV, int width, int height, ArrayList<MediaModel> mPlayList) {
        if (isSkipFullScreen && !isFullScreen) {
            activity.startActivity(isTV ? TvFullScreenPlayer.newIntent(activity, mPlayList)
                    : FullScreenPlayer.newIntent(activity, mPlayList));
            player.pause();
        } else {
            skipFullScreenPlayer(activity, player, isSkipFullScreen, isFullScreen, isTV, width, height, mPlayList, 0, 0, false);
        }
    }

    /**
     * 全屏和小屏的切换（全参数）
     *
     * @param activity         所在activity
     * @param player           player
     * @param isSkipFullScreen 是否调到全Activity播放页面
     * @param isFullScreen     目前是否是全屏
     * @param isTV             是否是tv播放
     * @param width            小屏player宽
     * @param height           小屏player高
     * @param mPlayList        播放列表
     * @param index            当前在播放列表中的位置
     * @param currentPosition  当前播放的时间
     * @param isPlaying        当前是否在播放
     */
    public static void skipFullScreenPlayer(Activity activity, SimplePlayer player, boolean isSkipFullScreen, boolean isFullScreen, boolean isTV, int width, int height, ArrayList<MediaModel> mPlayList, int index, int currentPosition, boolean isPlaying) {
        if (isSkipFullScreen) {
            if (isFullScreen) {
                activity.finish();
            } else {
                activity.startActivity(isTV ? TvFullScreenPlayer.newIntent(activity, mPlayList, index, currentPosition, isPlaying)
                        : FullScreenPlayer.newIntent(activity, mPlayList, index, currentPosition, isPlaying));
                player.pause();
            }
        } else {
            setNativeFull(activity, player, isFullScreen, isTV, width, height);
        }
    }

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
}
