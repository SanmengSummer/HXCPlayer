package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.utils.StringUtils;
import com.huan.huaxia.chxplayer.widght.adapter.PlayerListAdapter;
import com.huan.huaxia.chxplayer.widght.event.PlayEvent;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static com.huan.huaxia.chxplayer.widght.utils.AnimalUtils.setAlphaAnimator;

/**
 * 过时的player
 * Created by huaxia on 2017/11/9.
 */

public class OldPlayer extends SimplePlayer {
    private static final int UPDATA_PLAYTIME = 1;
    private static final int HIDE_PLAYER = 2;
    private boolean mDoublePressed;
    private boolean isFullScreen;
    private boolean isMovePlaying;
    private boolean isSimpleFullScreen;
    private boolean isSbProgressChange;
    private float downX;
    private float downY;
    private int mCurVolume;
    private Timer mTimer;
    private int move;
    private int width;
    private int height;
    private float moveX;
    private float moveY;
    private float absMoveX;
    private float absMoveY;
    private float systemBrightness;
    private float x;
    private float y;
    private PlayerListAdapter playListAdapter;
    private ViewGroup controller;
    private SeekBar mSbProgress;
    private TextView mTvMusicTime;
    private TextView mTvMoveTime;
    private RelativeLayout mRl;
    private ImageView mIbBack;
    private ImageView mIvPlayPause;
    private ImageView mIvPlay;
    private ImageView mIvList;
    private ImageView mIvZoom;
    private RecyclerView mRclv;
    private ViewGroup vp;
    private int color;
    private LinearLayout mLlVolume;
    private LinearLayout mLlLuminance;
    private ProgressBar mPbVolume;
    private TextView mTvScreenLuminance;
    private String duration;
    private String poisiton;
    private int ijkPlayerDuration;
    private CountTimeThread countTimeThread;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATA_PLAYTIME:
                    updatePlayTime();
                    break;
                case HIDE_PLAYER:
                    hide();
                    break;
            }
        }
    };

    public OldPlayer(Context context) {
        super(context);
        addView();
    }

    public OldPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView();
    }

    public OldPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView();
    }

    /**
     * 解除消息订阅
     */
    public void unRegister() {
        EventBus.getDefault().unregister(this);//解除订阅
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onSynPlayerInfo(PlayEvent event) {
        if (null != event) {
            this.index = event.index;
            stopPlayback();
            setVideoPath(mPlayList.get(index).videoPath);
            seekTo(event.duration);
            if (event.isPlaying) this.start();
            else this.pause();
        }
    }

    private void addView() {
        EventBus.getDefault().register(this);
        vp = ((Activity) mContext).findViewById(Window.ID_ANDROID_CONTENT);//获取当前window
        color = vp.getDrawingCacheBackgroundColor();//获取当前页面背景颜色
        controller = (ViewGroup) LayoutInflater.from(mContext).inflate(isTV ? R.layout.contraller_layout : R.layout.contraller_layout_phone, (ViewGroup) getRootView(), false);
        controller.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int dimension = (int) getResources().getDimension(R.dimen.x10);
        params.setMargins(dimension, dimension, dimension, dimension);
        this.addView(controller, params);
        mIvPlayPause = (ImageView) controller.findViewById(R.id.iv_play_pause);
        mIbBack = (ImageView) controller.findViewById(R.id.iv_back);
        mSbProgress = (SeekBar) controller.findViewById(R.id.sb_progress);
        mTvMusicTime = (TextView) controller.findViewById(R.id.tvMusicTime);
        mTvMoveTime = (TextView) controller.findViewById(R.id.tv_text_move_time);
        mRclv = (RecyclerView) controller.findViewById(R.id.rclv);
        mRl = (RelativeLayout) controller.findViewById(R.id.rl);
        mIvPlay = (ImageView) controller.findViewById(R.id.iv_play);
        mIvList = (ImageView) controller.findViewById(R.id.iv_list);
        mIvZoom = (ImageView) controller.findViewById(R.id.iv_zoom);
        mLlVolume = (LinearLayout) controller.findViewById(R.id.ll_volume);
        mLlLuminance = (LinearLayout) controller.findViewById(R.id.ll_screen_luminance);
        mPbVolume = (ProgressBar) controller.findViewById(R.id.pb_volume);
        mTvScreenLuminance = (TextView) controller.findViewById(R.id.tv__screen_luminance);
        setControllerListener();
        mIbBack.setVisibility(GONE);
        hide();
    }


    private void initThisView() {
        if (null != mPlayList) {
            mRclv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            playListAdapter = new PlayerListAdapter(mContext, this, mPlayList, index);
            mRclv.setAdapter(playListAdapter);
            mIvZoom.setImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
            initView();
            updatePlayTime();
            startCountTimeThread();
        }
    }

    //设置一系列监听
    private void setControllerListener() {
        mIvPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setCountTimeThreadReset();
                playPause();
            }
        });
        mIvPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setCountTimeThreadReset();
                playPause();
            }
        });
        mIvList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setCountTimeThreadReset();
                mRclv.setVisibility(mRclv.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
                if (mRclv.getVisibility() == View.VISIBLE) mRclv.requestFocus();
            }
        });
        mIbBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSimpleFullScreen)
                    startSimpleFullscreen(false);
                else
                    ((Activity) mContext).finish();
            }
        });

        mIvZoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSimpleFullScreen)
                    startSimpleFullscreen(!isFullScreen);
                else {
                    if (isFullScreen) {
                        ((Activity) mContext).finish();
                    } else {
                        mIbBack.setVisibility(View.VISIBLE);
                        mContext.startActivity(FullScreenPlayer.newIntent(mContext,
                                mPlayList, index, getCurrentPosition(), isPlaying()));
                        pause();
                    }
                }
            }
        });
        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int i;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isTV) {
                    this.i = i;
                    isSbProgressChange = !b;
                    seekTo(i * ijkPlayerDuration / 100);
                } else {
                    isSbProgressChange = b;
                    if (b) {
                        this.i = i;
                        setCountTimeThreadReset();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(i * ijkPlayerDuration / 100);//设置毫秒值
                isSbProgressChange = false;
            }
        });
        if (null != mPlayList)
            playListAdapter.setOnPlayListItemListener(new PlayerListAdapter.OnPlayListItemListener() {
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (null != mPlayList) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    showDownMethod(motionEvent);
                    break;
                case MotionEvent.ACTION_MOVE:
                    showMoveMethod(motionEvent);
                    break;
                case MotionEvent.ACTION_UP:
                    showUpMethod();
                    break;
            }
            setCountTimeThreadReset();
        }
        return true;
    }

    private void showMoveMethod(MotionEvent motionEvent) {
        moveX = motionEvent.getX() - downX;
        moveY = downY - motionEvent.getY();
        absMoveX = Math.abs(moveX);
        absMoveY = Math.abs(moveY);
        x = (moveX * 100 / getWidth());
        y = (moveY * 100 / getHeight());
        if (absMoveX > absMoveY + 20) {
            setSeekVideoDuration(moveX, x);
            isMovePlaying = true;
        } else if (absMoveY > absMoveX + 20) {
            if (downX < getWidth() / 2) {
                setScreenLuminance(y);
            } else {
                setVolume(y);
            }
            isMovePlaying = false;
        }
    }

    private void showDownMethod(MotionEvent motionEvent) {
        downX = motionEvent.getX();
        downY = motionEvent.getY();
        //得到当前手机的音量
        mCurVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        systemBrightness = ((Activity) (mContext)).getWindow().getAttributes().screenBrightness;
        if (systemBrightness <= 0.00f) {
            systemBrightness = 0.50f;
        } else if (systemBrightness < 0.01f) {
            systemBrightness = 0.01f;
        }
    }

    private void showUpMethod() {
        downX = 0;
        downY = 0;
        if (isMovePlaying) {
            seekTo(move);
        }
        isMovePlaying = false;
        setPlayOrPause();
        hideExtraView();
    }

    private void hideExtraView() {
        mTvMoveTime.setVisibility(GONE);
        mLlVolume.setVisibility(GONE);
        mLlLuminance.setVisibility(GONE);
    }

    /**
     * DoublePressed 双击流；暂停或播放
     * TriplePressed 三击流；全屏或退出全屏(体现效果奇差，已取消）
     */
    private void setPlayOrPause() {
        if (null != mPlayList)
            if (mDoublePressed) {
                if (isPlaying()) pause();
                else start();
                mTimer.cancel();
                mDoublePressed = false;
            } else {
                mTimer = new Timer();
                mDoublePressed = true;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mDoublePressed = false;
                    }
                }, 200);
            }
    }

    /**
     * 更改声音大小
     * mLlVolume;mLlLuminance;mPbVolume;mTvScreenLuminance;
     */
    private void setVolume(float y) {
        if (null != mListener)
            mListener.moveRightY(y);
        //得到手机音乐音量的最大值
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //设置音量大小
        int currentVolume = (int) (y * maxVolume / 50);//折算成系统音量的单位
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurVolume + currentVolume, FLAG_PLAY_SOUND);//FLAG_SHOW_UI显示音量；FLAG_PLAY_SOUND 调整音量时播放声音；0；
        mLlVolume.setVisibility(VISIBLE);
        mPbVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVolume);
    }

    /**
     * 更改当前屏幕亮度
     */
    private void setScreenLuminance(float brightness) {
        if (null != mListener)
            mListener.moveLeftY(brightness);
        brightness = brightness / 100;
        Window window = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = systemBrightness + brightness;
        if (lp.screenBrightness > 1.0f) {
            lp.screenBrightness = 1.0f;
        } else if (lp.screenBrightness < 0.01f) {
            lp.screenBrightness = 0.01f;
        }
        window.setAttributes(lp);
        mLlLuminance.setVisibility(VISIBLE);
        mTvScreenLuminance.setText((int) (lp.screenBrightness * 100) + "%");
    }

    /**
     * 快进和后退
     */
    private void setSeekVideoDuration(float moveX, float x) {
        if (null != mListener)
            mListener.moveX(x);
        move = (int) (getCurrentPosition() + x * getDuration() / 100);
        if (move <= 0) {
            move = 0;
        } else if (move > getDuration()) {
            move = getDuration();
        }
        mTvMoveTime.setVisibility(VISIBLE);
        mTvMoveTime.setText(StringUtils.formatPlayTime(move));
//        seekTo(move);
    }

    /**
     * 传入播放源3
     *
     * @param playList           播放列表
     * @param mIsTv              是否是tv版的播放器
     * @param isSimpleFullScreen 是否是简单全屏
     */
    public void setVideoList(ArrayList<MediaModel> playList, boolean mIsTv, boolean isSimpleFullScreen) {
        mPlayList = playList;
        setVideoPath(mPlayList.get(index).videoPath);
        isTV = mIsTv;
        this.isSimpleFullScreen = isSimpleFullScreen;
        initThisView();
    }

    /**
     * 传入播放源2
     */
    public void setVideoList(ArrayList<MediaModel> playList, boolean mIsTv) {
        mPlayList = playList;
        setVideoPath(mPlayList.get(index).videoPath);
        isTV = mIsTv;
        initThisView();
    }

    @Override
    //如果有key事件，则重置显示时间//
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setCountTimeThreadReset();
        return super.onKeyDown(keyCode, event);
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setXY();
    }

    //获取一下宽高
    public void setXY() {
        if (!isFullScreen) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
    }

    @Override
    public void pause() {
        super.pause();
        mHandler.removeMessages(UPDATA_PLAYTIME);
        mIvPlay.setImageResource(R.drawable.pause_selector);
        mIvPlayPause.setImageResource(R.drawable.pause_selector);
        setAlphaAnimator(mIvPlay, 0, 1);
    }

    @Override
    public void start() {
        super.start();
        mHandler.sendEmptyMessage(UPDATA_PLAYTIME);
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
        unRegister();
    }

    /**
     * 设置屏幕滑动监听
     *
     * @param listener
     */
    private OnXHIjkVideoListener mListener;

    public void setOnXHIjkVideoListener(OnXHIjkVideoListener listener) {
        mListener = listener;
    }

    public interface OnXHIjkVideoListener {
        void moveX(float percent);

        void moveLeftY(float percent);

        void moveRightY(float percent);
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
        poisiton = StringUtils.formatPlayTime(currentPosition);
        duration = " / " + StringUtils.formatPlayTime(ijkPlayerDuration);//格式转换
        mTvMusicTime.setText(poisiton + duration);//即时更新时间文本
        if (!isSbProgressChange)
            mSbProgress.setProgress(currentPosition * 100 / ijkPlayerDuration);//即时更新ProgressBar进程
        mHandler.sendEmptyMessageDelayed(UPDATA_PLAYTIME, 100);
    }

    /**
     * 开启监听控件隐藏的线程
     */
    private void startCountTimeThread() {
        if (null == countTimeThread) {
            countTimeThread = new CountTimeThread(4);
            countTimeThread.start();
        }
    }

    /**
     * 隐藏需要隐藏的按钮
     */
    public void hide() {
        if (mRl.getVisibility() == View.VISIBLE) {
            mRl.setVisibility(View.GONE);
        }
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

    /**
     * @param fullScreen 全屏（如果目前是全屏则退出全屏，否则全屏）
     */
    public void startSimpleFullscreen(boolean fullScreen) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                vp.getHeight(), vp.getWidth());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                width, height);
        this.setLayoutParams(fullScreen ? lp : lp1);
        if (fullScreen) {
            ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//强制为动态横屏
            vp.setBackgroundColor(Color.BLACK);
        } else {
            ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//clear flags
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
            vp.setBackgroundColor(color);
        }
        setFullScreen(fullScreen);
    }

    public void setFullScreen(boolean isFullScreen) {
        mIbBack.setVisibility(isFullScreen ? VISIBLE : GONE);
        mIvZoom.setImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
        this.isFullScreen = isFullScreen;
    }
}
