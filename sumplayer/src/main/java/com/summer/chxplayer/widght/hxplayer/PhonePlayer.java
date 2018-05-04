package com.summer.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;


import static android.media.AudioManager.FLAG_PLAY_SOUND;

/**
 * Phone版player
 * Created by huaxia on 2017/11/9.
 */

public class PhonePlayer extends TransitPlayer {
    private boolean mDoublePressed;
    private boolean isMovePlaying;
    private float downX;
    private float downY;
    private int mCurVolume;
    private Timer mTimer;
    private int move;
    private float moveX;
    private float moveY;
    private float absMoveX;
    private float absMoveY;
    private float systemBrightness;
    private float x;
    private float y;

    public PhonePlayer(Context context) {
        super(context);
        initExtra();
    }

    public PhonePlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initExtra();
    }

    public PhonePlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initExtra();
    }


    private void initExtra() {
        isTV = false;
        startCountTimeThread();//开启监听控件隐藏的线程
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
            setSeekVideoDuration(x);
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
        controller.hideMoveView();
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
        if (null != mOnVideoScreenMoveListener)
            mOnVideoScreenMoveListener.moveRightY(y);
        else {
            //得到手机音乐音量的最大值
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            //设置音量大小
            int currentVolume = (int) (y * maxVolume / 50);//折算成系统音量的单位
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurVolume + currentVolume, FLAG_PLAY_SOUND);//FLAG_SHOW_UI显示音量；FLAG_PLAY_SOUND 调整音量时播放声音；0；
            int moveVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVolume;
            controller.setVolumeView(moveVolume);
        }
    }

    /**
     * 更改当前屏幕亮度
     */
    private void setScreenLuminance(float brightness) {
        if (null != mOnVideoScreenMoveListener)
            mOnVideoScreenMoveListener.moveLeftY(brightness);
        else {
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
            String moveBrightness = (int) (lp.screenBrightness * 100) + "%";
            controller.setLuminanceView(moveBrightness);
        }
    }

    /**
     * 快进和后退
     */
    private void setSeekVideoDuration(float x) {
        if (null != mOnVideoScreenMoveListener)
            mOnVideoScreenMoveListener.moveX(x);
        else {
            move = (int) (getCurrentPosition() + x * getDuration() / 100);
            if (move <= 0) {
                move = 0;
            } else if (move > getDuration()) {
                move = getDuration();
            }
            controller.setMoveTimeView(move);
        }
    }


}
