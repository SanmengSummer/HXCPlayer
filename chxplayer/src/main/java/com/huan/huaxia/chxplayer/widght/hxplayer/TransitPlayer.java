package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.adapter.PlayerListAdapter;
import com.huan.huaxia.chxplayer.widght.utils.Param;
import com.huan.huaxia.chxplayer.widght.utils.StringUtils;
import com.huan.huaxia.chxplayer.widght.listener.OnControllerListener;
import com.huan.huaxia.chxplayer.widght.listener.OnPlayListItemListener;
import com.huan.huaxia.chxplayer.widght.listener.OnProgressBarListener;
import com.huan.huaxia.chxplayer.widght.listener.OnVideoScreenMoveListener;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;
import com.huan.huaxia.chxplayer.widght.utils.ToastUtil;

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
    protected ImageView mIvVolume;
    protected ImageView mIvScreenLuminance;
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
        setAlphaAnimator(mIvPlay, 0, 1);
        setPlayerImageVisibility(VISIBLE);
        mIvPlay.setImageResource(R.drawable.restart_selector);
        mIvPlayPause.setImageResource(R.drawable.restart_selector);
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
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(controller, params);
        mRecyclerView = controller.mRecyclerView;
        mRl = controller.mRl;
        mIbBack = controller.mIbBack;
        mName = controller.mName;
        mIvPlayPause = controller.mIvPlayPause;
        mSbProgress = controller.mSbProgress;
        mTvMusicTime = controller.mTvMusicTime;
        mTvMoveTime = controller.mTvMoveTime;
        mIvList = controller.mIvList;
        mIvPlay = controller.mIvPlay;
        mIvZoom = controller.mIvZoom;
        mIvVolume = controller.mIvVolume;
        mIvScreenLuminance = controller.mIvScreenLuminance;
        mLlVolume = controller.mLlVolume;
        mLlLuminance = controller.mLlLuminance;
        mPbVolume = controller.mPbVolume;
        mTvScreenLuminance = controller.mTvScreenLuminance;
        mPlayListAdapter = controller.mPlayListAdapter;
        onPointListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        changeControllerSize();
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
    }

    @Override
    public void controllerPlayPause() {
        setCountTimeThreadReset();
        if (playError && showPoint) {
            playError = false;
            setLoadingVisibility(VISIBLE);
            if (null != mPlayList) setVideoPath(mPlayList.get(index).videoPath);

        }
        playPause();

    }

    @Override
    public void controllerList() {
        setCountTimeThreadReset();
        mRecyclerView.setVisibility(mRecyclerView.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
        if (mRecyclerView.getVisibility() == View.VISIBLE) mRecyclerView.requestFocus();
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

    private ExitListener mListener;

    public void setOnExitListener(ExitListener listener) {
        mListener = listener;
    }

    interface ExitListener {
        void setOnExitListener();
    }

    //动态改变Controller里控件的Size
    private void changeControllerSize() {
        if (!isFullScreen) {
            int windowWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
            final int tempSizeText = (25 * height + windowWidth / 2) / windowWidth;
            final int tempSize20 = (20 * height + windowWidth / 2) / windowWidth;
            final int tempSizeListImage = (80 * height + windowWidth / 2) / windowWidth;
            final int tempSizeSmallPlay = (90 * height + windowWidth / 2) / windowWidth;
            final int tempSize160 = (160 * height + windowWidth / 2) / windowWidth;
            final int tempSize130 = (130 * height + windowWidth / 2) / windowWidth;
            final int tempSize200 = (200 * height + windowWidth / 2) / windowWidth;
            final int tempSize10 = (10 * height + windowWidth / 2) / windowWidth == 0 ? 1 : (10 * height + windowWidth / 2) / windowWidth;
            final int tempSize5 = (5 * height + windowWidth / 2) / windowWidth == 0 ? 1 : (5 * height + windowWidth / 2) / windowWidth;

            RelativeLayout.LayoutParams layoutParamsWRAPR = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsWRAP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams layoutParamsBigPlay = new RelativeLayout.LayoutParams(tempSize200, tempSize200);
            RelativeLayout.LayoutParams layoutParamsSmallPlay = new RelativeLayout.LayoutParams(tempSizeSmallPlay, tempSizeSmallPlay);
            LinearLayout.LayoutParams layoutParamsListImage = new LinearLayout.LayoutParams(tempSizeListImage, tempSizeListImage);
            LinearLayout.LayoutParams layoutParamsPbVolume = new LinearLayout.LayoutParams(tempSize10, tempSize160);
            LinearLayout.LayoutParams layoutParamsSbProgress = new LinearLayout.LayoutParams(this.width - tempSize160, 7);
            layoutParamsListImage.setMargins(tempSize5, tempSize5, tempSize5, tempSize5);
            layoutParamsSmallPlay.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParamsBigPlay.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParamsWRAP.setMargins(tempSize20, tempSize20, tempSize20, tempSize20);
            layoutParamsWRAPR.setMargins(tempSizeListImage, 0, 0, 0);
            layoutParamsWRAPR.addRule(RelativeLayout.CENTER_VERTICAL);

            mIvPlay.setLayoutParams(layoutParamsBigPlay);
            mIbBack.setLayoutParams(layoutParamsListImage);
            mIvList.setLayoutParams(layoutParamsListImage);
            mIvZoom.setLayoutParams(layoutParamsListImage);
            mIvZoom.setPadding(tempSize5, tempSize5, tempSize5, tempSize5);
            mIvPlayPause.setLayoutParams(layoutParamsSmallPlay);
            mIvVolume.setLayoutParams(layoutParamsListImage);
            mIvScreenLuminance.setLayoutParams(layoutParamsListImage);
            mPbVolume.setLayoutParams(layoutParamsPbVolume);

            mLlVolume.setPadding(tempSize5, tempSize5, tempSize5, tempSize5);
            mLlVolume.setLayoutParams(layoutParamsWRAPR);

            mLlLuminance.setPadding(0, 0, tempSizeListImage, 0);

            mSbProgress.setLayoutParams(layoutParamsSbProgress);
            mSbProgress.setThumb(mContext.getResources().getDrawable(R.mipmap.audio_seek_thumb2));
            mSbProgress.setPadding(2, 1, 2, 1);

            mName.setLayoutParams(layoutParamsWRAP);
            mName.setTextSize(tempSizeText);
            mTvMusicTime.setTextSize(tempSizeText);
            mTvMoveTime.setTextSize(tempSizeText);
            mTvScreenLuminance.setTextSize(tempSizeText);
            mPlayListAdapter.setChangeSizeListener(new PlayerListAdapter.ChangeSizeListener() {
                @Override
                public void setChangeTextSizeListener(TextView view) {
                    view.setTextSize(tempSize20);
                }

                @Override
                public void setChangeImageSizeListener(ImageView view) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tempSize160, tempSize130);
                    view.setLayoutParams(layoutParams);
                }

                @Override
                public void setChangeItemViewSizeListener(LinearLayout view) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tempSize200, tempSize200);
                    layoutParams.setMargins(1, 1, 1, 1);
                    view.setLayoutParams(layoutParams);
                }
            });
        }
    }

}
