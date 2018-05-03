package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.utils.Param;
import com.huan.huaxia.chxplayer.widght.utils.StringUtils;
import com.huan.huaxia.chxplayer.widght.listener.OnControllerListener;
import com.huan.huaxia.chxplayer.widght.listener.OnPlayListItemListener;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;
import com.huan.huaxia.chxplayer.widght.utils.ToastUtil;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.huan.huaxia.chxplayer.widght.utils.AnimalUtils.setAlphaAnimator;

/**
 * tv版全屏player
 */
public class TvFullScreenPlayer extends AppCompatActivity implements OnPlayListItemListener, OnControllerListener, TvPlayer.PointListener {
    private TvPlayer player;
    private ArrayList<MediaModel> playList;
    private boolean isPlaying;
    private int mDuration;
    private int index;
    private String path;
    private PlayerController controller;
    private boolean showDialog;
    private boolean showPoint;
    private long currentTime;
    private boolean isRe;

    public static Intent newIntent(Context mContext, Bundle savedInstanceState) {
        Intent intent = new Intent(mContext, TvFullScreenPlayer.class);
        intent.putExtra(Param.Constants.transBundle, savedInstanceState);
        return intent;
    }

    public static Intent newIntent(Context mContext, ArrayList<MediaModel> playList) {
        Intent intent = new Intent(mContext, TvFullScreenPlayer.class);
        intent.putExtra(Param.Constants.playlist, playList);
        return intent;
    }

    public static Intent newIntent(Context mContext, String path) {
        Intent intent = new Intent(mContext, TvFullScreenPlayer.class);
        intent.putExtra(Param.Constants.path, path);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_full_screen);
        if (getIntent().hasExtra(Param.Constants.playlist))
            playList = getIntent().getParcelableArrayListExtra(Param.Constants.playlist);
        if (getIntent().hasExtra(Param.Constants.path))
            path = getIntent().getStringExtra(Param.Constants.path);
        if (getIntent().hasExtra(Param.Constants.transBundle)) {
            Bundle transBundle = getIntent().getBundleExtra(Param.Constants.transBundle);
            if (null == playList)
                playList = transBundle.getParcelableArrayList(Param.BundleParam.mPlayList);
            index = transBundle.getInt(Param.BundleParam.index, 0);
            mDuration = transBundle.getInt(Param.BundleParam.currentPosition, 0);
            isPlaying = transBundle.getBoolean(Param.BundleParam.isPlaying, false);
            showDialog = transBundle.getBoolean(Param.BundleParam.showDialog, false);
            showPoint = transBundle.getBoolean(Param.BundleParam.showPoint, false);
        }
        setPlayer();
        setController();
    }

    private void setPlayer() {
        player = (TvPlayer) findViewById(R.id.player);
        player.setOnPointListener(this);
        player.index = index;
        player.isTV = true;
        player.isFullScreen = true;
        player.showDialog = showDialog;
        player.showPoint = showPoint;
        if (null != playList) {
            player.setVideoList(playList);
        } else if (!TextUtils.isEmpty(path)) {
            player.setVideoPath(path);
        } else return;
        player.seekTo(mDuration);
        if (isPlaying) player.start();
        else player.pause();

    }

    private void setController() {
        controller = (PlayerController) findViewById(R.id.controller);
        controller.findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        controller.setOnPlayListItemListener(this);
        controller.setControllerListener(this);
        controller.showController();
        if (null != playList) {
            controller.showListData(player, playList, index);
            controller.mName.setText(playList.get(index).name);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ESCAPE:
            case KeyEvent.KEYCODE_BACK:
                if (GONE != controller.getController()) {
                    controller.hideController();
                } else {
                    back();
                }
                return true;
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

    private void back() {
        if ((System.currentTimeMillis() - currentTime) > 2000) {
            ToastUtil.getInstance(this).Short(getString(R.string.exit_player)).show();
            currentTime = System.currentTimeMillis();
        } else {
            sendPlayBroadcast();
        }
    }

    public void sendPlayBroadcast() {
        Intent intent = new Intent();
        intent.setAction("com.huaxia.play");
        intent.putExtra(Param.Constants.mDuration, player.getCurrentPosition());
        intent.putExtra(Param.Constants.index, player.index);
        intent.putExtra(Param.Constants.isPlaying, player.isPlaying());
        sendBroadcast(intent);
        player.showZoom(false);
    }

    @Override
    protected void onResume() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制横屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        mHandler.sendEmptyMessage(UPDATE_PLAYTIME);
        super.onResume();
    }

    @Override
    protected void onPause() {
        player.pause();
        mHandler.removeMessages(UPDATE_PLAYTIME);
        controller.mIvPlay.setImageResource(R.drawable.play_selector);
        controller.mIvPlayPause.setImageResource(R.drawable.play_selector);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        player.isSkip = false;
        mHandler.removeCallbacksAndMessages(UPDATE_PLAYTIME);
        super.onDestroy();
    }
    //////////////////////////////////////

    /**
     * playList的监听（焦点，点击）
     */
    @Override
    public void setOnFocusChangeListener(View view, boolean b) {

    }

    @Override
    public void setOnClickListener(View view, int index) {
        if (null != playList && this.index != index) {
            controller.mName.setText(playList.get(index).name);
            controller.mIvPlay.setImageResource(R.drawable.play_selector);
            controller.mIvPlayPause.setImageResource(R.drawable.play_selector);
        }
    }

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
            if (isRe) {
                player.playError = false;
                player.setLoadingVisibility(VISIBLE);
                if (null != playList) player.setVideoPath(playList.get(index).videoPath);
            }
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
        sendPlayBroadcast();
    }

    @Override
    public void controllerBack() {
        sendPlayBroadcast();
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
        int currentPosition = player.getCurrentPosition();
        int ijkPlayerDuration = player.getDuration();
        String position = StringUtils.formatPlayTime(currentPosition);
        String duration = " / " + StringUtils.formatPlayTime(ijkPlayerDuration);//格式转换
        controller.mTvMusicTime.setText(position + duration);//即时更新时间文本
        if (player.isPlaying())
            controller.mSbProgress.setProgress(currentPosition * Param.Constants.maxProgress / ijkPlayerDuration);//即时更新ProgressBar进程
        mHandler.sendEmptyMessageDelayed(UPDATE_PLAYTIME, 100);
    }

    @Override
    public void setOnPointListener() {
        isRe = true;
        setAlphaAnimator(controller.mIvPlay, 0, 1);
        player.setPlayerImageVisibility(VISIBLE);
        controller.mIvPlay.setImageResource(R.drawable.restart_selector);
        controller.mIvPlayPause.setImageResource(R.drawable.restart_selector);
        ToastUtil.getInstance(this).Long("没有搜索有效的视频源，请重试").show();
    }
}
