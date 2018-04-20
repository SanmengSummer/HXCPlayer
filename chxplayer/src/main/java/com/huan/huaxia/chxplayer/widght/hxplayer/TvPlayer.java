package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.huan.huaxia.chxplayer.widght.utils.PlayerUtils;
import com.huan.huaxia.chxplayer.widght.event.PlayEvent;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 可加新功能
 * Created by huaxia on 2017/11/9.
 */

public class TvPlayer extends SimplePlayer {
    public TvPlayer(Context context) {
        super(context);
        initExtra();
    }

    public TvPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initExtra();
    }

    public TvPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initExtra();
    }

    private void initExtra() {
        EventBus.getDefault().register(this);
        isTV = true;
    }

    public void setVideoList(ArrayList<MediaModel> playList) {
        setVideoList(playList, false);
    }

    public void setVideoList(ArrayList<MediaModel> playList, boolean isSkipFullScreenPlayer) {
        super.setVideoList(playList);
        this.isSkipFullScreenPlayer = isSkipFullScreenPlayer;
    }

    public void zoom() {
        PlayerUtils.skipFullScreenPlayer((Activity) mContext, this, isSkipFullScreenPlayer, isFullScreen, true, width, height, mPlayList, index, getCurrentPosition(), isPlaying());
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onSynPlayer(PlayEvent event) {
        if (null != event) {
            this.index = event.index;
            stopPlayback();
            super.setVideoPath(mPlayList.get(index).videoPath);
            seekTo(event.duration);
            if (event.isPlaying) start();
            else pause();
        }
    }

    /**
     * 界面销毁时调用方法
     */
    @Override
    public void releaseWithoutStop() {
        super.releaseWithoutStop();
        EventBus.getDefault().unregister(this);//解除订阅
    }

}
