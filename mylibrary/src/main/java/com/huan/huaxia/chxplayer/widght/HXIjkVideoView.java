package com.huan.huaxia.chxplayer.widght;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.ijkplayer.media.IjkVideoView;
import com.huan.huaxia.chxplayer.ijkplayer.utils.DialogUtil;
import com.huan.huaxia.chxplayer.ijkplayer.utils.StringUtils;
import com.huan.huaxia.chxplayer.ijkplayer.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.media.AudioManager.FLAG_PLAY_SOUND;
import static com.huan.huaxia.chxplayer.ijkplayer.utils.AnimalUtils.setAlphaAnimator;

/**
 * Created by huaxia on 2017/11/9.
 */

public class HXIjkVideoView extends IjkVideoView {
    public ImageView mIbBack;
    private Context mContext;
    private FrameLayout mContainer;
    private boolean mDoublePressed;
    private float downX;
    private float downY;
    private AudioManager audioManager;
    private int mCurVolume;
    private ArrayList<MediaModel> mPlayList;
    private Timer mTimer;
    private boolean isFullScreen;
    private float moveX;
    private float moveY;
    private float absMoveX;
    private float absMoveY;
    private int systemBrightness;
    private ViewGroup loading;
    private ViewGroup controller;
    private int x;
    private int y;
    private PlayerListAdapter playListAdapter;
    private boolean isMovePlaying;
    private static final int UPDATA_PLAYTIME = 1;
    private static final int HIDE_PLAYER = 2;
    private SeekBar mSbProgress;
    private TextView mTvMusicTime;
    private RecyclerView mRclv;
    private RelativeLayout mRl;
    private ImageView mIvPlayPause;
    private ImageView mIvPlay;
    private ImageView mIvList;
    private ImageView mIvZoom;
    private String duration;
    private String poisiton;
    public int index;
    private int ijkPlayerDuration;
    private CountTimeThread countTimeThread;
    private MyListener myListener;
    private boolean isTv;
    private int width;
    private int height;
    private boolean isSimpleFullScreen = true;
    private TextView mTvMoveTime;
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

    public HXIjkVideoView(Context context) {
        this(context, null);
    }

    public HXIjkVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HXIjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        EventBus.getDefault().register(this);
        mContext = context;
        setBackgroundColor(getResources().getColor(R.color.black));
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setFocusable(true);
        init();
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

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.TRANSPARENT);
        controller = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.contraller_layout, (ViewGroup) getRootView(), false);
        loading = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.tv_player_layout_loading, (ViewGroup) getRootView(), false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
        this.addView(controller, params);
        this.addView(loading, params);
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
        mIbBack.setVisibility(GONE);
        hide();
    }


    private void initView() {
        if (null != mPlayList) {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libmIjkplayer.so");
            mRclv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            playListAdapter = new PlayerListAdapter(mContext, this, mPlayList, index);
            mRclv.setAdapter(playListAdapter);
            mIvZoom.setImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
            setControllerListener();
            updatePlayTime();
            startCountTimeThread();
        } else {
            DialogUtil.showUnSubscribeDialog(mContext, isTv);
        }
    }

    //设置一系列监听
    private void setControllerListener() {
        myListener = new MyListener();
        setOnCompletionListener(myListener.iMediaPlayerCompletionListener);
        setOnErrorListener(myListener.iMediaPlayerOnErrorListener);
        setOnPreparedListener(myListener.onPreparedListener);
        setOnInfoListener(myListener.onInfoListener);
        DialogUtil.setOnReLoadingListener(myListener.onReLoadingListener);
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
                ((Activity) mContext).finish();
            }
        });

        mIvZoom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSimpleFullScreen)
                    startSimpleFullscreen(mContext, !isFullScreen);
                else {
                    if (isFullScreen) {
                        mIbBack.setVisibility(VISIBLE);
                        ((Activity) mContext).finish();
                    } else {
                        mContext.startActivity(HXPlayerActivity.newIntent(mContext,
                                mPlayList, index, getCurrentPosition(), isPlaying()));
                        pause();
                    }
                }
            }
        });
        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    setCountTimeThreadReset();
                    seekTo(i * ijkPlayerDuration / 100);//设置毫秒值
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        if (null != mPlayList)
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = motionEvent.getX();
                    downY = motionEvent.getY();
                    //得到当前手机的音量
                    mCurVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    try {
                        systemBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                    } catch (Settings.SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                    setPlayOrPause();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - downX;
                    moveY = motionEvent.getY() - downY;
                    absMoveX = Math.abs(moveX);
                    absMoveY = Math.abs(moveY);
                    x = (int) (absMoveX * 100 / getWidth());
                    y = (int) (absMoveY * 100 / getHeight());
                    if (x > y + 2) {
                        pause();
                        setSeekVideoDuration(moveX, x);

                        isMovePlaying = true;
                    } else if (y > x + 2) {
                        if (downX < getWidth() / 2) {
                            setScreenLuminance(moveY, y);
                        } else {
                            setVolume(moveY, y);
                        }
                        isMovePlaying = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    downX = 0;
                    downY = 0;
                    if (isMovePlaying) {
                        mTvMoveTime.setVisibility(INVISIBLE);
                        start();
                    }
                    isMovePlaying = false;
                    setCountTimeThreadReset();
                    break;
            }
        return true;
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
     */
    private void setVolume(float moveY, int y) {
        if (null != mListener)
            mListener.moveRightY(moveY < 0, y);
        //得到手机音乐音量的最大值
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //设置音量大小
        y = y * maxVolume / 100;//折算成系统音量的单位
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, moveY < 0 ? mCurVolume + y : mCurVolume - y, FLAG_PLAY_SOUND);//FLAG_SHOW_UI显示音量；FLAG_PLAY_SOUND 调整音量时播放声音；0；
    }

    /**
     * 更改当前屏幕亮度
     */
    private void setScreenLuminance(float moveY, int brightness) {
        brightness = brightness * 255 / 100;
        Window window = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (moveY > 0)
            lp.screenBrightness = (systemBrightness + brightness) / 255f;
        else
            lp.screenBrightness = (systemBrightness - brightness) / 255f;
        window.setAttributes(lp);
        if (null != mListener)
            mListener.moveLeftY(moveY < 0, brightness);
    }

    /**
     * 快进和后退
     */
    private void setSeekVideoDuration(float moveX, int x) {
        if (null != mListener)
            mListener.moveX(moveX > 0, (int) moveX);
        mTvMoveTime.setVisibility(VISIBLE);
        mTvMoveTime.setText(StringUtils.formatPlayTime(getCurrentPosition()));
        seekTo((moveX > 0 ? getCurrentPosition() + x * getDuration() / getWidth() : getCurrentPosition() - x * getDuration() / getWidth()));
    }

    /**
     * 传入播放源1
     */
    public void setVideoList(ArrayList<MediaModel> playList, boolean isTv, boolean isSimpleFullScreen) {
        mPlayList = playList;
        setVideoPath(mPlayList.get(index).videoPath);
        this.isTv = isTv;
        this.isSimpleFullScreen = isSimpleFullScreen;
        initView();
    }

    /**
     * 传入播放源2
     */
    public void setVideoList(ArrayList<MediaModel> playList, boolean isTv) {
        mPlayList = playList;
        setVideoPath(mPlayList.get(index).videoPath);
        this.isTv = isTv;
        initView();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setXY();
    }

    public void setXY() {
        if (!isFullScreen) {
            width = getMeasuredWidth();
            height = getMeasuredHeight();
        }
    }

    /**
     * 设置播放器监听
     */
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
                DialogUtil.showUnSubscribeDialog(mContext, isTv);
                hide();
                return true;
            }
        };
        //播放准备时调用
        IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mRl.setVisibility(VISIBLE);
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
                loading.setVisibility(VISIBLE);
                if (null != mPlayList) {
                    setVideoPath(mPlayList.get(index).videoPath);
                    playPause();
                }
            }
        };
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
        void moveX(boolean isBigger, int x);

        void moveLeftY(boolean isBigger, int y);

        void moveRightY(boolean isBigger, int y);
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
     * @param context
     * @param fullScreen 全屏（如果目前是全屏则退出全屏，否则全屏）
     */
    public void startSimpleFullscreen(Context context, boolean fullScreen) {
        ViewGroup vp = ((Activity) context)//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                vp.getWidth(), vp.getHeight());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                width, height);
        this.setLayoutParams(fullScreen ? lp : lp1);
        setFullScreen(fullScreen);
    }

    public void setFullScreen(boolean isFullScreen) {
        mIbBack.setVisibility(isFullScreen ? VISIBLE : GONE);
        mIvZoom.setImageResource(isFullScreen ? R.mipmap.shrink : R.mipmap.enlarge);
        this.isFullScreen = isFullScreen;
    }
}
