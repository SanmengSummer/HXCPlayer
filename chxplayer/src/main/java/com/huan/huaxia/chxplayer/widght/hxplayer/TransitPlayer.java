package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.utils.Param;
import com.huan.huaxia.chxplayer.widght.utils.PlayerUtils;
import com.huan.huaxia.chxplayer.widght.utils.StringUtils;
import com.huan.huaxia.chxplayer.widght.listener.OnControllerListener;
import com.huan.huaxia.chxplayer.widght.listener.OnPlayListItemListener;
import com.huan.huaxia.chxplayer.widght.listener.OnProgressBarListener;
import com.huan.huaxia.chxplayer.widght.listener.OnVideoScreenMoveListener;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;
import com.huan.huaxia.chxplayer.widght.event.PlayEvent;
import com.huan.huaxia.chxplayer.widght.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static com.huan.huaxia.chxplayer.widght.utils.AnimalUtils.setAlphaAnimator;

/**
 * phonePlayer过渡的player（处理些混乱的逻辑）
 * Created by huaxia on 2017/11/9.
 */
public class TransitPlayer extends SimplePlayer implements OnControllerListener, SimplePlayer.PointListener {
    private static final int UPDATE_PLAYTIME = 10;
    private static final int HIDE_PLAYER = 20;
    protected PlayerController controller;
    protected SeekBar mSbProgress;
    protected TextView mTvMusicTime;
    protected TextView mTvMoveTime;
    protected RelativeLayout mRl;
    protected ImageView mIbBack;
    protected ImageView mIvPlayPause;
    protected ImageView mIvPlay;
    protected ImageView mIvList;
    protected ImageView mIvZoom;
    protected RecyclerView mRecyclerView;
    protected LinearLayout mLlVolume;
    protected LinearLayout mLlLuminance;
    protected ProgressBar mPbVolume;
    protected TextView mTvScreenLuminance;
    protected TextView mName;
    private String duration;
    private String position;
    private int ijkPlayerDuration;
    private CountTimeThread countTimeThread;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PLAYTIME:
                    updatePlayTime();
                    break;
                case HIDE_PLAYER:
                    hide();
                    break;
            }
        }
    };

    public TransitPlayer(Context context) {
        super(context);
        addView();
    }

    public TransitPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView();
    }

    public TransitPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onSynPlayerInfo(PlayEvent event) {
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
     * 传入播放源1
     */
    public void setVideoList(ArrayList<MediaModel> playList) {
        setVideoList(playList, false);
    }

    /**
     * 传入播放源2
     *
     * @param playList               播放列表
     * @param isSkipFullScreenPlayer 是否是简单全屏
     */

    public void setVideoList(ArrayList<MediaModel> playList, boolean isSkipFullScreenPlayer) {
        super.setVideoList(playList);
        this.isSkipFullScreenPlayer = isSkipFullScreenPlayer;
        initThisView();
    }

    @Override
    public void onPointListener() {
        setAlphaAnimator(mIvPlay, 0, 1);
        setPlayerImageVisibility(VISIBLE);
        mIvPlay.setImageResource(R.drawable.restart_selector);
        mIvPlayPause.setImageResource(R.drawable.restart_selector);
        ToastUtil.getInstance(mContext).Long("没有搜索有效的视频源，请重试").show();
    }

    private void addView() {
        EventBus.getDefault().register(this);
        addController();
        setControllerListener();
        hide();
    }

    private void addController() {
        controller = new PlayerController(mContext);
        controller.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int dimension = (int) getResources().getDimension(R.dimen.x10);
        params.setMargins(dimension, dimension, dimension, dimension);
        this.addView(controller, params);
        mRecyclerView = controller.mRecyclerView;
        mRl = controller.mRl;
        mIbBack = controller.mIbBack;
        mName = controller.mName;
        mIvList = controller.mIvList;
        mIvPlayPause = controller.mIvPlayPause;
        mSbProgress = controller.mSbProgress;
        mTvMusicTime = controller.mTvMusicTime;
        mTvMoveTime = controller.mTvMoveTime;
        mIvPlay = controller.mIvPlay;
        mIvZoom = controller.mIvZoom;
        mLlVolume = controller.mLlVolume;
        mLlLuminance = controller.mLlLuminance;
        mPbVolume = controller.mPbVolume;
        mTvScreenLuminance = controller.mTvScreenLuminance;
        onPointListener(this);
    }

    private void initThisView() {
        if (null != mPlayList) {
            controller.showListData(this, mPlayList, index);
            mIvZoom.setImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
            updatePlayTime();
        }
    }

    //设置一系列监听
    private void setControllerListener() {
        controller.setControllerListener(this);
        if (null != mPlayList)
            controller.setOnPlayListItemListener(new OnPlayListItemListener() {
                @Override
                public void setOnFocusChangeListener(View view, boolean b) {
                    // 播放列表各条目焦点变化回调
                }

                @Override
                public void setOnClickListener(View view, int mIndex) {
                    index = mIndex;
                    // 播放列表各条目点击回调
                }
            });

        controller.setOnProgressBarListener(new OnProgressBarListener() {
            private int percent;

            @Override
            public void onProgressChanged(SeekBar seekBar, int percent, boolean b) {
                isSbProgressChange = b;
                if (b) {
                    this.percent = percent;
                    setCountTimeThreadReset();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(percent * getDuration() / Param.Constants.maxProgress);//设置毫秒值
                isSbProgressChange = false;
            }
        });
    }

    public void setCountTimeThreadReset() {
        if (null != mPlayList && null != countTimeThread)
            countTimeThread.reset();//重置时间
        if (mRl.getVisibility() != View.VISIBLE) {
            mRl.setVisibility(View.VISIBLE);
            mSbProgress.requestFocus();
        }
    }

    @Override
    public void pause() {
        super.pause();
        mHandler.removeMessages(UPDATE_PLAYTIME);
        mIvPlay.setImageResource(R.drawable.pause_selector);
        mIvPlayPause.setImageResource(R.drawable.pause_selector);
        setAlphaAnimator(mIvPlay, 0, 1);
    }

    @Override
    public void start() {
        super.start();
        if (null != mPlayList) {
            mName.setText(mPlayList.get(index).name);
        }
        mHandler.sendEmptyMessage(UPDATE_PLAYTIME);
        mIvPlay.setImageResource(R.drawable.play_selector);
        mIvPlayPause.setImageResource(R.drawable.play_selector);
        setAlphaAnimator(mIvPlay, 1, 0);
    }

    /**
     * 界面销毁时调用方法
     */
    @Override
    public void releaseWithoutStop() {
        super.releaseWithoutStop();
        countTimeThread = null;
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);//解除订阅
    }

    @Override
    public void controllerPlayPause() {
        setCountTimeThreadReset();
        if (!playError && showPoint) {
            playPause();
        } else {
            playError = false;
            if (null != loading)
                loading.setVisibility(View.VISIBLE);
            if (null != mPlayList) {
                super.setVideoPath(mPlayList.get(index).videoPath);
                start();
            }
        }
    }

    @Override
    public void controllerList() {
        setCountTimeThreadReset();
        mRecyclerView.setVisibility(mRecyclerView.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        if (mRecyclerView.getVisibility() == View.VISIBLE) mRecyclerView.requestFocus();
    }

    @Override
    public void controllerZoom() {
        showZoom();
    }

    @Override
    public void controllerBack() {
        showZoom();
    }

    @Override
    public void controllerBackward() {
        //phone版无用
    }

    @Override
    public void controllerForward() {
        //phone版无用
    }

    public void showZoom() {
        PlayerUtils.skipFullScreenPlayer((Activity) mContext, this, isSkipFullScreenPlayer, isFullScreen, isTV, width, height, mPlayList, index, getCurrentPosition(), isPlaying());
        if (!isSkipFullScreenPlayer) setFullScreen(!isFullScreen);

    }

    protected OnVideoScreenMoveListener mOnVideoScreenMoveListener;

    /**
     * 设置屏幕滑动监听
     */
    public void setOnVideoScreenMoveListener(OnVideoScreenMoveListener listener) {
        mOnVideoScreenMoveListener = listener;
    }
    //////////////////////////////////////////////////////////

    /**
     * 以下是用户无操作后一段时间后隐藏相关控件和更新进度时间等功能
     */
    //播放时更新时间(包含进度条和播放时间更新)
    private void updatePlayTime() {
        mIvZoom.setImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
        int currentPosition = getCurrentPosition();
        ijkPlayerDuration = getDuration();
        position = StringUtils.formatPlayTime(currentPosition);
        duration = " / " + StringUtils.formatPlayTime(ijkPlayerDuration);//格式转换
        mTvMusicTime.setText(position + duration);//即时更新时间文本
        if (!isSbProgressChange)
            mSbProgress.setProgress(currentPosition * Param.Constants.maxProgress / ijkPlayerDuration);//即时更新ProgressBar进程
        mHandler.sendEmptyMessageDelayed(UPDATE_PLAYTIME, 100);
    }

    /**
     * 开启监听控件隐藏的线程
     */
    protected void startCountTimeThread() {
        if (null == countTimeThread) {
            countTimeThread = new CountTimeThread(4);
            countTimeThread.start();
        }
    }

    /**
     * 隐藏需要隐藏的按钮
     */
    public void hide() {
        controller.hideController();
    }


    private class CountTimeThread extends Thread {
        private final long maxVisibleTime;

        private long startVisibleTime;

        /**
         * 设置控件显示时间 second单位是秒
         *
         * @param second
         */


        public CountTimeThread(int second) {
            maxVisibleTime = second * 1000;//换算为毫秒
            setDaemon(true);//设置为后台进程
        }

        /**
         * 如果用户有操作，则重新开始计时隐藏时间
         */
        private synchronized void reset() {
            startVisibleTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            startVisibleTime = System.currentTimeMillis();//初始化开始时间
            while (true) {
                //如果时间达到最大时间，则发送隐藏消息
                if (startVisibleTime + maxVisibleTime < System.currentTimeMillis()) {
                    mHandler.sendEmptyMessage(HIDE_PLAYER);
                    startVisibleTime = System.currentTimeMillis();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
        }
    }

///////////////////////////////////////////////////////////////////////////////////////

    public void setFullScreen(boolean isFullScreen) {
        mIbBack.setVisibility(isFullScreen ? VISIBLE : GONE);
        mIvZoom.setImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
        this.isFullScreen = isFullScreen;
    }
}
