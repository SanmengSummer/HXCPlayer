package com.huan.huaxia.chxplayer.widght;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.ijkplayer.media.IjkVideoView;
import com.huan.huaxia.chxplayer.ijkplayer.utils.DialogUtil;
import com.huan.huaxia.chxplayer.ijkplayer.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.media.AudioManager.FLAG_PLAY_SOUND;

/**
 * Created by huaxia on 2017/11/9.
 */

public class SimpleVideoView extends IjkVideoView {
    private Context mContext;
    private boolean mDoublePressed;
    private float downX;
    private float downY;
    private AudioManager audioManager;
    private int mCurVolume;
    private ArrayList<MediaModel> mPlayList;
    private Timer mTimer;
    private float moveX;
    private float moveY;
    private float absMoveX;
    private float absMoveY;
    private int systemBrightness;
    private ViewGroup loading;
    private int x;
    private int y;
    private boolean isMovePlaying;
    private int index;
    private MyListener myListener;
    private String videoPath;

    public SimpleVideoView(Context context) {
        this(context, null);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setBackgroundColor(getResources().getColor(R.color.black));
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        setFocusable(true);
        loading = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.tv_player_layout_loading, (ViewGroup) getRootView(), false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(loading, params);
    }

    private void initView() {
        if (null != mPlayList) {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libmIjkplayer.so");
            setControllerListener();
        }
    }

    @Override
    public void setVideoPath(String path) {
        videoPath = path;
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libmIjkplayer.so");
        setControllerListener();
        super.setVideoPath(path);
    }

    private void setControllerListener() {
        myListener = new MyListener();
        setOnCompletionListener(myListener.iMediaPlayerCompletionListener);
        setOnErrorListener(myListener.iMediaPlayerOnErrorListener);
        setOnPreparedListener(myListener.onPreparedListener);
        setOnInfoListener(myListener.onInfoListener);
        DialogUtil.setOnReLoadingListener(myListener.onReLoadingListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
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
                    start();
                }
                isMovePlaying = false;
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
        seekTo((moveX > 0 ? getCurrentPosition() + x * getDuration() / getWidth() : getCurrentPosition() - x * getDuration() / getWidth()));
        Log.e("hx", getCurrentPosition() + "  " + (moveX > 0 ? getCurrentPosition() + x * getDuration() / getWidth() : getCurrentPosition() - x * getDuration() / getWidth()) + "");
    }

    /**
     * 传入播放源
     */
    public void setVideoList(ArrayList<MediaModel> playList) {
        mPlayList = playList;
        setVideoPath(mPlayList.get(index).videoPath);
        initView();
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
                DialogUtil.showUnSubscribeDialog(mContext, false);
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
                loading.setVisibility(VISIBLE);
                if (null != mPlayList) {
                    setVideoPath(mPlayList.get(index).videoPath);
                    playPause();
                }
            }
        };
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
}
