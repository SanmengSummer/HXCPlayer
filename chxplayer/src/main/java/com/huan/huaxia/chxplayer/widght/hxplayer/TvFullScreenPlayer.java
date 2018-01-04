package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.utils.Param;
import com.huan.huaxia.chxplayer.widght.utils.StringUtils;
import com.huan.huaxia.chxplayer.widght.event.PlayEvent;
import com.huan.huaxia.chxplayer.widght.listener.OnControllerListener;
import com.huan.huaxia.chxplayer.widght.listener.OnPlayListItemListener;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.huan.huaxia.chxplayer.widght.utils.AnimalUtils.setAlphaAnimator;

/**
 *
 */
public class TvFullScreenPlayer extends AppCompatActivity implements OnPlayListItemListener, OnControllerListener {
    private TvPlayer player;
    private ArrayList<MediaModel> playList;
    private boolean isPlaying;
    private int mDuration;
    private int index;
    private PlayEvent playEvent;
    private String path;
    private PlayerController controller;

    public static Intent newIntent(Context mContext, ArrayList<MediaModel> playList, int index, int mDuration, boolean isPlaying) {
        Intent intent = new Intent(mContext, TvFullScreenPlayer.class);
        intent.putExtra(Param.Constants.playlist, playList);
        intent.putExtra(Param.Constants.mDuration, mDuration);
        intent.putExtra(Param.Constants.index, index);
        intent.putExtra(Param.Constants.isPlaying, isPlaying);
        return intent;
    }

    public static Intent newIntent(Context mContext, ArrayList<MediaModel> playList) {
        Intent intent = new Intent(mContext, TvFullScreenPlayer.class);
        intent.putExtra(Param.Constants.playlist, playList);
        return intent;
    }

    public static Intent newIntent(Context mContext, String path) {
        Intent intent = new Intent(mContext, FullScreenPlayer.class);
        intent.putExtra(Param.Constants.path, path);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_full_screen);
        if (getIntent().hasExtra(Param.Constants.playlist))
            playList = getIntent().getParcelableArrayListExtra(Param.Constants.playlist);
        if (getIntent().hasExtra(Param.Constants.mDuration))
            mDuration = getIntent().getIntExtra(Param.Constants.mDuration, -1);
        if (getIntent().hasExtra(Param.Constants.isPlaying))
            isPlaying = getIntent().getBooleanExtra(Param.Constants.isPlaying, false);
        if (getIntent().hasExtra(Param.Constants.index))
            index = getIntent().getIntExtra(Param.Constants.index, -1);
        if (getIntent().hasExtra(Param.Constants.path))
            path = getIntent().getStringExtra(Param.Constants.path);
        setPlayer();
        setController();
    }

    private void setPlayer() {
        playEvent = new PlayEvent();
        player = (TvPlayer) findViewById(R.id.player);
        player.index = index;
        player.isFullScreen = true;
        player.isSkipFullScreenPlayer = true;
        if (null != playList) {
            player.setVideoList(playList, true);
        } else if (!TextUtils.isEmpty(path)) {
            player.setVideoPath(path);
            player.isTV = true;
        } else return;
        player.seekTo(mDuration);
        if (isPlaying) player.start();
        else player.pause();
    }

    private void setController() {
        controller = (PlayerController) findViewById(R.id.controller);
        controller.findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        controller.setOnPlayListItemListener(this);
//        controller.setOnProgressBarListener(this);
        controller.setControllerListener(this);
        controller.showController();
        if (null != playList) {
            controller.showListData(player, playList, index);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ESCAPE:
            case KeyEvent.KEYCODE_BACK:
                if (GONE != controller.getController()) {
                    controller.hideController();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                controller.showController();
                if (!controller.hasFocus())
                    controller.mIvPlayPause.requestFocus();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        mHandler.sendEmptyMessage(UPDATE_PLAYTIME);
    }

    @Override
    protected void onPause() {
        player.pause();
        super.onPause();
        mHandler.removeMessages(UPDATE_PLAYTIME);
    }

    @Override
    protected void onDestroy() {
        playEvent.setDuration(player.getCurrentPosition());
        playEvent.setIndex(player.index);
        playEvent.setPlaying(player.isPlaying());
        EventBus.getDefault().post(playEvent);
        super.onDestroy();
    }

    //////////////////////////////////////

    /*
     *controller的监听
     */

    /**
     * playList的监听
     */
    @Override
    public void setOnFocusChangeListener(View view, boolean b) {

    }

    @Override
    public void setOnClickListener(View view, int index) {
        player.index = index;
    }

//    /**
//     * Progressbar的监听（progressbar变化，开始碰触，停止碰触）（对tv版progress焦点选中后的变化不能监听）
//     */
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int percent, boolean b) {
//    }
//
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//    }

    /**
     * controller整体的监听（播放暂停，播放列表，放大缩小，返回）
     */
    @Override
    public void controllerPlayPause() {
        if (player.isPlaying()) {
            controller.mIvPlay.setImageResource(R.drawable.pause_selector);
            controller.mIvPlayPause.setImageResource(R.drawable.pause_selector);
            setAlphaAnimator(controller.mIvPlay, 0, 1);
        } else {
            controller.mIvPlay.setImageResource(R.drawable.play_selector);
            controller.mIvPlayPause.setImageResource(R.drawable.play_selector);
            setAlphaAnimator(controller.mIvPlay, 1, 0);
        }
        player.playPause();
    }

    @Override
    public void controllerList() {
        if (VISIBLE == controller.getPlayerList())
            controller.hidePlayerList();
        else controller.showPlayerList();
    }

    @Override
    public void controllerZoom() {
        player.zoom();
    }

    @Override
    public void controllerBack() {
        player.zoom();
    }

    @Override
    public void controllerBackward() {
        int i = player.getDuration() > 100000 ? 10 : 50;
        int move = player.getCurrentPosition() <= 0
                ? 0
                : player.getCurrentPosition() - i * player.getDuration() / Param.Constants.maxProgress;

        player.seekTo(move);
    }

    @Override
    public void controllerForward() {
        int i = player.getDuration() > 1800000 ? 10 : 50;
        int move = player.getCurrentPosition() >= player.getDuration()
                ? player.getCurrentPosition()
                : player.getCurrentPosition() + i * player.getDuration() / Param.Constants.maxProgress;
        player.seekTo(move);
    }

    private static final int UPDATE_PLAYTIME = 10;
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PLAYTIME:
                    updatePlayTime();
                    break;
            }
        }
    };

    private void updatePlayTime() {

        controller.mIvPlay.setVisibility(player.isPlaying() ? GONE : VISIBLE);
        controller.mIvPlay.setImageResource(player.isPlaying() ? R.drawable.play_selector : R.drawable.pause_selector);
        controller.mIvPlayPause.setImageResource(player.isPlaying() ? R.drawable.play_selector : R.drawable.pause_selector);
        int currentPosition = player.getCurrentPosition();
        int ijkPlayerDuration = player.getDuration();
        String position = StringUtils.formatPlayTime(currentPosition);
        String duration = " / " + StringUtils.formatPlayTime(ijkPlayerDuration);//格式转换
        controller.mTvMusicTime.setText(position + duration);//即时更新时间文本
        if (player.isPlaying())
            controller.mSbProgress.setProgress(currentPosition * Param.Constants.maxProgress / ijkPlayerDuration);//即时更新ProgressBar进程
        mHandler.sendEmptyMessageDelayed(UPDATE_PLAYTIME, 100);
    }
}
