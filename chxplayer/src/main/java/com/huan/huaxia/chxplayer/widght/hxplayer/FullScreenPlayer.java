package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.utils.Param;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;
import com.huan.huaxia.chxplayer.widght.utils.ToastUtil;

import java.util.ArrayList;

/**
 * Created by huaxia on 2017.10.12
 */
public class FullScreenPlayer extends AppCompatActivity implements TransitPlayer.ExitListener {
    private PhonePlayer player;
    private ArrayList<MediaModel> playList;
    private boolean isPlaying;
    private int mDuration;
    private int index;
    private String path;
    private boolean showDialog;
    private boolean showPoint;
    private long currentTime;

    public static Intent newIntent(Context mContext, Bundle savedInstanceState) {
        Intent intent = new Intent(mContext, FullScreenPlayer.class);
        intent.putExtra(Param.Constants.transBundle, savedInstanceState);
        return intent;
    }

    public static Intent newIntent(Context mContext, ArrayList<MediaModel> playList) {
        Intent intent = new Intent(mContext, FullScreenPlayer.class);
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
        setContentView(R.layout.activity_player);
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
            setPlayer();
        }
    }

    private void setPlayer() {
        player = (PhonePlayer) findViewById(R.id.player);
        player.findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        player.index = index;
        player.isTV = false;
        player.setFullScreen(true);
        player.showDialog = showDialog;
        player.showPoint = showPoint;
        player.hide();
        player.setOnExitListener(this);
        if (null != playList && playList.size() > 0) {
            player.setVideoList(playList);
        } else if (!TextUtils.isEmpty(path)) {
            player.setVideoPath(path);
        } else return;
        player.seekTo(mDuration);
        if (isPlaying) player.start();
        else player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//强制为动态横屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
    }

    @Override
    protected void onPause() {
        player.pause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ESCAPE:
            case KeyEvent.KEYCODE_BACK:
                back();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void back() {
        if ((System.currentTimeMillis() - currentTime) > 2000) {
            ToastUtil.getInstance(this).Short(getString(R.string.exit_player)).show();
            currentTime = System.currentTimeMillis();
        } else {
            sendPlayBroadcast();
            player.showZoom(false);
        }
    }

    public void sendPlayBroadcast() {
        Intent intent = new Intent();
        intent.setAction("com.huaxia.play");
        intent.putExtra(Param.Constants.mDuration, player.getCurrentPosition());
        intent.putExtra(Param.Constants.index, player.index);
        intent.putExtra(Param.Constants.isPlaying, player.isPlaying());
        sendBroadcast(intent);
    }

    @Override
    public void setOnExitListener() {
        sendPlayBroadcast();
    }
}
