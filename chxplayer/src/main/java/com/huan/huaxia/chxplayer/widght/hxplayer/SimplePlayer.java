package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.player.media.BasePlayer;
import com.huan.huaxia.chxplayer.widght.utils.DialogUtil;
import com.huan.huaxia.chxplayer.widght.utils.ToastUtil;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Created by huaxia on 2017/11/9.
 */

public class SimplePlayer extends BasePlayer {
    protected int height;
    protected int width;
    protected boolean isSbProgressChange;
    protected boolean isSkipFullScreenPlayer;
    protected boolean isFullScreen;
    protected boolean isTV;//是否是TV端播放器，默认不是；
    protected ArrayList<MediaModel> mPlayList;
    protected AudioManager audioManager;
    protected Context mContext;
    protected int index;
    private ViewGroup loading;
    private MyListener myListener;

    public SimplePlayer(Context context) {
        super(context);
        init(context);
    }

    public SimplePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SimplePlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isFullScreen) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
    }
    private void init(Context context) {
        mContext = context;
        setBackgroundColor(getResources().getColor(R.color.light_black));
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setFocusable(true);
        loading = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.tv_player_layout_loading, (ViewGroup) getRootView(), false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(loading, params);
    }

    /**
     * 传入播放源1
     */
    public void setVideoList(ArrayList<MediaModel> playList) {
        mPlayList = playList;
        setVideoPath(mPlayList.get(index).videoPath);
        initView();
    }

    @Override
    public void setVideoPath(String path) {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libmIjkplayer.so");
        setListener();
        super.setVideoPath(path);
    }

    public void initView() {
        if (null != mPlayList) {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libmIjkplayer.so");
            setListener();
        }
    }

    private void setListener() {
        myListener = new MyListener();
        setOnCompletionListener(myListener.iMediaPlayerCompletionListener);
        setOnErrorListener(myListener.iMediaPlayerOnErrorListener);
        setOnPreparedListener(myListener.onPreparedListener);
        setOnInfoListener(myListener.onInfoListener);
        DialogUtil.setOnReLoadingListener(myListener.onReLoadingListener);
    }


    //播放后一个视频
    public void playNext() {
        if (index < mPlayList.size() - 1) {
            index++;
            setVideoPath(mPlayList.get(index).videoPath);
            if (!isPlaying()) start();
        } else
            ToastUtil.getInstance(mContext).Short(getContext().getString(R.string.text_last_video)).show();

    }

    //播放前一个视频
    public void playPre() {
        if (index > 0) {
            index--;
            setVideoPath(mPlayList.get(index).videoPath);
            if (!isPlaying()) start();
        } else
            ToastUtil.getInstance(mContext).Short(getContext().getString(R.string.text_first_video)).show();
    }

    //暂停或播放
    public void playPause() {
        if (isPlaying()) pause();
        else start();

    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void start() {
        super.start();
    }

    //设置监听
    private class MyListener {
        //播放完调用
        IMediaPlayer.OnCompletionListener iMediaPlayerCompletionListener = new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                loading.setVisibility(View.GONE);
                playNext();
                if (index == mPlayList.size()) ((Activity) mContext).finish();
            }
        };
        //播放错误调用
        IMediaPlayer.OnErrorListener iMediaPlayerOnErrorListener = new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                loading.setVisibility(View.GONE);
                if (!isPlaying())
                    DialogUtil.showUnSubscribeDialog(mContext, isTV, isFullScreen);
                return true;
            }
        };
        //播放准备时调用
        IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                loading.setVisibility(View.GONE);
            }
        };
        //播放缓冲
        IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (i == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    loading.setVisibility(View.VISIBLE);
                } else if (i == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    loading.setVisibility(View.GONE);
                }
                return true;
            }
        };
        DialogUtil.OnReLoadingListener onReLoadingListener = new DialogUtil.OnReLoadingListener() {
            @Override
            public void reLoading() {
                loading.setVisibility(View.VISIBLE);
                if (null != mPlayList) {
                    setVideoPath(mPlayList.get(index).videoPath);
                    start();
                }
            }
        };
    }

    @Override
    public void releaseWithoutStop() {
        super.releaseWithoutStop();
    }
}
