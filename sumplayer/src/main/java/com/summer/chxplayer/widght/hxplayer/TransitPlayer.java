package com.summer.chxplayer.widght.hxplayer;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.huan.huaxia.chxplayer.R;
import com.summer.chxplayer.widght.adapter.PlayerListAdapter;
import com.summer.chxplayer.widght.utils.Param;
import com.summer.chxplayer.widght.utils.StringUtils;
import com.summer.chxplayer.widght.listener.OnControllerListener;
import com.summer.chxplayer.widght.listener.OnPlayListItemListener;
import com.summer.chxplayer.widght.listener.OnProgressBarListener;
import com.summer.chxplayer.widght.listener.OnVideoScreenMoveListener;
import com.summer.chxplayer.widght.model.MediaModel;
import com.summer.chxplayer.widght.utils.ToastUtil;

import java.util.ArrayList;

/**
 * phonePlayer过渡的player（处理些混乱的逻辑）
 * Created by huaxia on 2017/11/9.
 */
public class TransitPlayer extends SimplePlayer implements OnControllerListener, SimplePlayer.PointListener {
    private static final int UPDATE_PLAYTIME = 10;
    private static final int HIDE_PLAYER = 20;
    protected PlayerController controller;
    //    protected SeekBar mSbProgress;
//    protected TextView mTvMusicTime;
//    protected TextView mTvMoveTime;
//    protected RelativeLayout mRl;
//    protected ImageView mIbBack;
//    protected ImageView mIvPlayPause;
//    protected ImageView mIvPlay;
//    protected ImageView mIvList;
//    protected ImageView mIvZoom;
//    protected ImageView mIvVolume;
//    protected ImageView mIvScreenLuminance;
//    protected RecyclerView mRecyclerView;
//    protected LinearLayout mLlVolume;
//    protected LinearLayout mLlLuminance;
//    protected ProgressBar mPbVolume;
//    protected TextView mTvScreenLuminance;
//    protected TextView mName;
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
    private PlayerListAdapter mPlayListAdapter;

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

    /**
     * @param playList 播放列表
     */
    public void setVideoList(ArrayList<MediaModel> playList) {
        super.setVideoList(playList);
        initThisView();
    }

    @Override
    public void onPointListener() {
        setPlayerImageVisibility(View.VISIBLE);
        controller.setIvPlayImageResource(R.drawable.restart_selector);
        controller.setIvPlayAlphaAnimator(0, 1);
        ToastUtil.getInstance(mContext).Long("没有搜索有效的视频源，请重试").show();
    }

    private void addView() {
        addController();
        setControllerListener();
        hide();
    }

    private void addController() {
        controller = new PlayerController(mContext);
        controller.setBackgroundColor(Color.TRANSPARENT);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(controller, params);
//        mRecyclerView = controller.mRecyclerView;
//        mRl = controller.mRl;
//        mIbBack = controller.mIbBack;
//        mName = controller.mName;
//        mIvPlayPause = controller.mIvPlayPause;
//        mSbProgress = controller.mSbProgress;
//        mTvMusicTime = controller.mTvMusicTime;
//        mTvMoveTime = controller.mTvMoveTime;
//        mIvList = controller.mIvList;
//        mIvPlay = controller.mIvPlay;
//        mIvZoom = controller.mIvZoom;
//        mIvVolume = controller.mIvVolume;
//        mIvScreenLuminance = controller.mIvScreenLuminance;
//        mLlVolume = controller.mLlVolume;
//        mLlLuminance = controller.mLlLuminance;
//        mPbVolume = controller.mPbVolume;
//        mTvScreenLuminance = controller.mTvScreenLuminance;
//        mPlayListAdapter = controller.mPlayListAdapter;
        onPointListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        controller.changeControllerSize(!isFullScreen, height, width);
    }


    private void initThisView() {
        if (null != mPlayList) {
            controller.showListData(this, mPlayList, index);
            controller.setIvZoomImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
            updatePlayTime();
        }
    }

    //设置一系列监听
    private void setControllerListener() {
        controller.setControllerListener(this);
        controller.setOnPlayListItemListener(new OnPlayListItemListener() {
            @Override
            public void setOnFocusChangeListener(View view, boolean b) {
                // 播放列表各条目焦点变化回调
            }

            @Override
            public void setOnClickListener(View view, int mIndex) {
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
        controller.showController();
    }

    @Override
    public void pause() {
        super.pause();
        mHandler.removeMessages(UPDATE_PLAYTIME);
        controller.setIvPlayImageResource(R.drawable.pause_selector);
        controller.setIvPlayAlphaAnimator(0, 1);
    }

    @Override
    public void start() {
        super.start();
        if (null != mPlayList) {
            controller.setName(mPlayList.get(index).name);
        }
        mHandler.sendEmptyMessage(UPDATE_PLAYTIME);
        controller.setIvPlayImageResource(R.drawable.play_selector);
        controller.setIvPlayAlphaAnimator(1, 0);
    }

    /**
     * 界面销毁时调用方法
     */
    @Override
    public void releaseWithoutStop() {
        super.releaseWithoutStop();
        countTimeThread = null;
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void controllerPlayPause() {
        setCountTimeThreadReset();
        if (playError && showPoint) {
            playError = false;
            setLoadingVisibility(View.VISIBLE);
            if (null != mPlayList) setVideoPath(mPlayList.get(index).videoPath);

        }
        playPause();

    }

    @Override
    public void controllerList() {
        setCountTimeThreadReset();
        controller.showOrHidePlayerList();
    }

    @Override
    public void controllerZoom() {
        if (null != mListener)
            mListener.setOnExitListener();
        showZoom(isChangePlayerSize);
    }

    @Override
    public void controllerBack() {
        if (null != mListener)
            mListener.setOnExitListener();
        showZoom(isChangePlayerSize);
    }

    @Override
    public void controllerBackward() {
        //phone版无用
    }

    @Override
    public void controllerForward() {
        //phone版无用
    }

    @Override
    public void showZoom(boolean isChangePlayerSize) {
        super.showZoom(isChangePlayerSize);
        if (isChangePlayerSize) setFullScreen(!isFullScreen);
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
        controller.setIvZoomImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
        int currentPosition = getCurrentPosition();
        ijkPlayerDuration = getDuration();
        position = StringUtils.formatPlayTime(currentPosition);
        duration = " / " + StringUtils.formatPlayTime(ijkPlayerDuration);//格式转换
        controller.setTvMusicTime(position + duration);//即时更新时间文本(position + duration);//即时更新时间文本
        if (!isSbProgressChange)
            controller.setSbProgress(currentPosition * Param.Constants.maxProgress / ijkPlayerDuration);//即时更新ProgressBar进程
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
    //设置全屏信息
    public void setFullScreen(boolean isFullScreen) {
        controller.setIbBackVisibility(isFullScreen ? View.VISIBLE : View.GONE);
        controller.setIvZoomImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
        this.isFullScreen = isFullScreen;
    }

    private ExitListener mListener;

    public void setOnExitListener(ExitListener listener) {
        mListener = listener;
    }

    interface ExitListener {
        void setOnExitListener();
    }
}
