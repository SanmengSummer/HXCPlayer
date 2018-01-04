package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.utils.Param;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;
import com.huan.huaxia.chxplayer.widght.event.PlayEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by huaxia on 2017.10.12
 */
public class FullScreenPlayer extends AppCompatActivity {
    private PhonePlayer player;
    private ArrayList<MediaModel> playList;
    private boolean isPlaying;
    private int mDuration;
    private int index;
    private PlayEvent playEvent;
    private String path;

    public static Intent newIntent(Context mContext, ArrayList<MediaModel> playList, int index, int mDuration, boolean isPlaying) {
        Intent intent = new Intent(mContext, FullScreenPlayer.class);
        intent.putExtra(Param.Constants.playlist, playList);
        intent.putExtra(Param.Constants.mDuration, mDuration);
        intent.putExtra(Param.Constants.index, index);
        intent.putExtra(Param.Constants.isPlaying, isPlaying);
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
        if (getIntent().hasExtra(Param.Constants.mDuration))
            mDuration = getIntent().getIntExtra(Param.Constants.mDuration, -1);
        if (getIntent().hasExtra(Param.Constants.isPlaying))
            isPlaying = getIntent().getBooleanExtra(Param.Constants.isPlaying, false);
        if (getIntent().hasExtra(Param.Constants.index))
            index = getIntent().getIntExtra(Param.Constants.index, -1);
        if (getIntent().hasExtra(Param.Constants.path))
            path = getIntent().getStringExtra(Param.Constants.path);
        setPlayer();
    }

    private void setPlayer() {
        playEvent = new PlayEvent();
        player = (PhonePlayer) findViewById(R.id.player);
        player.findViewById(R.id.iv_back).setVisibility(View.VISIBLE);
        player.isSkipFullScreenPlayer = true;
        player.index = index;
        player.isTV=false;
        player.setFullScreen(true);
        player.hide();
        if (null != playList && playList.size() > 0) {
            player.setVideoList(playList, false);
        }
        else if (!TextUtils.isEmpty(path)) {
            player.setVideoPath(path);
        }
        else return;
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
    protected void onDestroy() {
        playEvent.setDuration(player.getCurrentPosition());
        playEvent.setIndex(player.index);
        playEvent.setPlaying(player.isPlaying());
        EventBus.getDefault().post(playEvent);
        super.onDestroy();
    }
}
