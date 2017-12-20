package com.huan.huaxia.chxplayer.widght;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.huan.huaxia.chxplayer.ijkplayer.utils.Param;
import com.huan.huaxia.mylibrary.R;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by huaxia on 2017.10.12
 */
public class HXPlayerActivity extends AppCompatActivity {

    private HXIjkVideoView player;
    private ArrayList<MediaModel> playList;
    private boolean isPlaying;
    private int mDuration;
    private int index;
    private PlayEvent playEvent;

    public static Intent newIntent(Context mContext, ArrayList<MediaModel> playList, int index, int mDuration, boolean isPlaying) {
        Intent intent = new Intent(mContext, HXPlayerActivity.class);
        intent.putExtra(Param.Constants.playlist, playList);
        intent.putExtra(Param.Constants.mDuration, mDuration);
        intent.putExtra(Param.Constants.index, index);
        intent.putExtra(Param.Constants.isPlaying, isPlaying);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playList = getIntent().getParcelableArrayListExtra(Param.Constants.playlist);
        mDuration = getIntent().getIntExtra(Param.Constants.mDuration, -1);
        isPlaying = getIntent().getBooleanExtra(Param.Constants.isPlaying, false);
        index = getIntent().getIntExtra(Param.Constants.index, -1);
        playEvent = new PlayEvent();
        player = (HXIjkVideoView) findViewById(R.id.player);
        player.index = index;
        player.mIbBack.setVisibility(View.VISIBLE);
        player.hide();
        player.setVideoList(playList, true, false);
        player.seekTo(mDuration);
        player.setFullScreen(true);
        if (isPlaying) player.start();
        else player.pause();
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player.isPlaying()) player.pause();
                else player.start();
            }
        });
    }

    @Override
    protected void onPause() {
        playEvent.setDuration(player.getCurrentPosition());
        playEvent.setIndex(player.index);
        playEvent.setPlaying(player.isPlaying());
        EventBus.getDefault().post(playEvent);
        player.pause();
        super.onPause();
    }
}
