package com.summer.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.huan.huaxia.chxplayer.R;
import com.summer.chxplayer.player.media.BasePlayer;
import com.summer.chxplayer.widght.utils.DialogUtil;
import com.summer.chxplayer.widght.utils.Param;
import com.summer.chxplayer.widght.utils.PlayerUtils;
import com.summer.chxplayer.widght.utils.ToastUtil;
import com.summer.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * 简单Player
 * Created by huaxia on 2017/11/9.
 */

public class SimplePlayer extends BasePlayer {
    protected int height;
    protected int width;
    protected boolean isSbProgressChange;
    protected boolean isChangePlayerSize;
    protected boolean isFullScreen;
    protected boolean isSkip;
    protected boolean isTV;//是否是TV端播放器，默认不是；
    protected ArrayList<MediaModel> mPlayList;
    protected AudioManager audioManager;
    protected Context mContext;
    public int index;
    protected ViewGroup loading;
    private MyListener myListener;
    protected ImageView playerImage;
    protected boolean showDialog;
    protected boolean showPoint;
    protected boolean playError;
    private boolean hidePlayerImage;
    private boolean hideLoading;
    protected boolean showZoomPlayer;

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
        if (!hidePlayerImage)
            playerImage = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.tv_player_image, (ViewGroup) getRootView(), false);
        if (!hideLoading)
            loading = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.tv_player_layout_loading, (ViewGroup) getRootView(), false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(playerImage, params);
        this.addView(loading, params);
        setLoadingVisibility(GONE);
    }

    /**
     * 传入播放源
     */
    public void setVideoList(ArrayList<MediaModel> playList) {
        mPlayList = playList;
        if (null != mPlayList)
            setVideoPath(mPlayList.get(index).videoPath);
    }

    @Override
    public void setVideoPath(String path) {
        if (null == mPlayList) {
            MediaModel mediaModel = new MediaModel();
            mediaModel.setVideoPath(path);
            mediaModel.setName("No name");
            mPlayList.add(mediaModel);
        }
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libmIjkplayer.so");
        setListener();
        Glide.with(mContext).load(mPlayList.get(index).getImagPath()).placeholder(R.mipmap.icon_empty).into(playerImage);
        super.setVideoPath(path);
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

    /**
     * 大小屏切换
     */
    public void showZoom(boolean isChangePlayerSize) {
        if (!isFullScreen) {
            showZoomPlayer = true;
            registerReceiver();
        }
        PlayerUtils mInstance = PlayerUtils.getInstance();
        Bundle outState = setBundle(isChangePlayerSize);
        mInstance.saveState(outState);
        mInstance.skipFullScreenPlayer((Activity) mContext, this);
        isSkip = true;
    }

    /**
     * 大小屏切换(默认isChangePlayerSize为false)
     */
    public void showZoom() {
        showZoom(false);
    }

    @NonNull//参数的传递
    private Bundle setBundle(boolean isChangePlayerSize) {
        Bundle outState = new Bundle();
        outState.putBoolean(Param.BundleParam.isChangePlayerSize, isChangePlayerSize);
        outState.putBoolean(Param.BundleParam.showDialog, showDialog);
        outState.putBoolean(Param.BundleParam.showPoint, showPoint);
        outState.putBoolean(Param.BundleParam.isFullScreen, isFullScreen);
        outState.putBoolean(Param.BundleParam.isTV, isTV);
        outState.putBoolean(Param.BundleParam.isPlaying, isPlaying());
        outState.putInt(Param.BundleParam.width, width);
        outState.putInt(Param.BundleParam.height, height);
        outState.putInt(Param.BundleParam.index, index);
        outState.putInt(Param.BundleParam.currentPosition, getCurrentPosition());
        outState.putParcelableArrayList(Param.BundleParam.mPlayList, mPlayList);
        return outState;
    }

    @Override
    public void pause() {
        super.pause();
        setLoadingVisibility(GONE);
    }

    @Override
    public void start() {
        playerImage.setVisibility(GONE);
        if (!playError)
        setLoadingVisibility(VISIBLE);
        super.start();
    }

    public void setLoadingVisibility(int visibility) {
        if (null != loading)
        loading.setVisibility(visibility);
    }

    public void setPlayerImageVisibility(int visibility) {
        playerImage.setVisibility(visibility);
    }

    //设置监听
    private class MyListener {
        //播放完调用
        IMediaPlayer.OnCompletionListener iMediaPlayerCompletionListener = new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                setLoadingVisibility(GONE);
                if (isFullScreen && index == mPlayList.size()) ((Activity) mContext).finish();
                playNext();
            }
        };
        //播放错误调用
        IMediaPlayer.OnErrorListener iMediaPlayerOnErrorListener = new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                playError = true;
                setLoadingVisibility(GONE);
                if (!isSkip)
                    setDialogOrPoint(i);
                return true;
            }
        };
        //播放准备时调用
        IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                setLoadingVisibility(GONE);
            }
        };
        //播放缓冲
//        int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频准备渲染
//        int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲
//        int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
//        int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频选择信息
//        int MEDIA_ERROR_SERVER_DIED = 100;//视频中断，一般是视频源异常或者不支持的视频类型。
//        int MEDIA_ERROR_IJK_PLAYER = -10000,//一般是视频源有问题或者数据格式不支持，比如音频不是AAC之类的
//        int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
        IMediaPlayer.OnInfoListener onInfoListener = new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                switch (i) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        setLoadingVisibility(VISIBLE);
                        break;
                    default:
                        setLoadingVisibility(GONE);
                        break;
                }
                return true;
            }
        };
        DialogUtil.OnReLoadingListener onReLoadingListener = new DialogUtil.OnReLoadingListener() {
            @Override
            public void reLoading() {
                playError = false;
                setLoadingVisibility(VISIBLE);
                if (null != mPlayList) {
                    setVideoPath(mPlayList.get(index).videoPath);
                    start();
                }
            }
        };
    }

    //设置一个播放错误提示dialog
    private void setDialogOrPoint(int error) {
        if (!isPlaying() && showDialog)
            DialogUtil.showUnSubscribeDialog(mContext, isTV, isFullScreen);
        else if (!isPlaying() && showPoint) {
            if (null != mListener)
                mListener.onPointListener();
        }
    }

    private PointListener mListener;

    public void onPointListener(PointListener listener) {
        mListener = listener;
    }

    interface PointListener {
        void onPointListener();
    }

    //设置是否展示播放错误Point 默认为false
    public void setShowPoint(boolean showPoint) {
        this.showPoint = showPoint;
    }

    //设置是否展示播放错误dialog或Point之一 （false为Point，true为dialog）
    public void setShowDialogOrPoint(boolean showDialogOrPoint) {
        this.showDialog = showDialogOrPoint;
        this.showPoint = !showDialogOrPoint;
    }

    //设置是否展示loading 默认为false
    public void setLoadingNone() {
        hideLoading = true;
        removeView(loading);
        loading = null;
    }

    //设置是否展示playerImage(播放前图片)默认为false
    public void setImageNone() {
        hidePlayerImage = true;
        removeView(loading);
        loading = null;
    }

    //设置是否展示播放错误dialog默认为false
    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    /**
     * @param isChangePlayerSize 设置controller全屏方式
     *                           （false为跳转到FullScreenPlayer或TVScreenPlayer，true则是改变player尺寸为全屏）
     */
    public void setIsChangePlayerSize(boolean isChangePlayerSize) {
        this.isChangePlayerSize = isChangePlayerSize;
    }

    @Override
    public void releaseWithoutStop() {
        super.releaseWithoutStop();
        IjkMediaPlayer.native_profileEnd();
        if (showZoomPlayer) {
            unRegisterReceiver();
        }
//        pause();
    }

    //发送全屏切小屏参数传递的广播
    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.huaxia.play".equals(action)) {
                if (showZoomPlayer) {
                    index = intent.getIntExtra(Param.Constants.index, 0);
                    seekTo(intent.getIntExtra(Param.Constants.mDuration, 0));
                    setVideoPath(mPlayList.get(index).videoPath);
                    if (intent.getBooleanExtra(Param.Constants.isPlaying, false)) start();
                    else pause();
                }
                showZoomPlayer = false;
            }
        }
    };
    private boolean isRegisterReceiver;

    public void registerReceiver() {
        if (!isRegisterReceiver && null != mContext) {
            IntentFilter filter_dynamic = new IntentFilter();
            filter_dynamic.addAction("com.huaxia.play");
            mContext.registerReceiver(dynamicReceiver, filter_dynamic);
        }
    }

    protected void unRegisterReceiver() {
        if (isRegisterReceiver && null != mContext) {
            isRegisterReceiver = false;
            mContext.unregisterReceiver(dynamicReceiver);
        }
    }
}
