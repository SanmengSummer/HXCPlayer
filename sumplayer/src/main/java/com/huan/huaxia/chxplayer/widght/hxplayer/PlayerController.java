package com.huan.huaxia.chxplayer.widght.hxplayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.widght.utils.Param;
import com.huan.huaxia.chxplayer.widght.adapter.PlayerListAdapter;
import com.huan.huaxia.chxplayer.widght.listener.OnControllerListener;
import com.huan.huaxia.chxplayer.widght.listener.OnPlayListItemListener;
import com.huan.huaxia.chxplayer.widght.listener.OnProgressBarListener;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;

/**
 * controller player控制器
 * Created by huaxia on 2017/12/28.
 */
public class PlayerController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, PlayerListAdapter.OnPlayListItemListener {
    protected ViewGroup controller;
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
    protected PlayerListAdapter mPlayListAdapter;
    private Context mContext;
    private OnPlayListItemListener mOnPlayListItemListener;
    private OnProgressBarListener mOnProgressBarListener;
    private OnControllerListener mControllerListener;
    private boolean isTvValue;
    private ImageView mForward;
    private ImageView mBackward;

    public PlayerController(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public PlayerController(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
        initView();
    }

    public PlayerController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
        initView();
    }

    private void initView() {
        controller = (ViewGroup) LayoutInflater.from(mContext).inflate(isTvValue ? R.layout.contraller_layout : R.layout.contraller_layout_phone, (ViewGroup) getRootView(), false);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(controller, params);
        findViewById();
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.test);
        isTvValue = ta.getBoolean(R.styleable.test_isTv, true);
        ta.recycle();
    }

    private void findViewById() {
        mRl = (RelativeLayout) findViewById(R.id.rl);
        mRecyclerView = (RecyclerView) findViewById(R.id.rclv);
        mIbBack = (ImageView) findViewById(R.id.iv_back);
        mName = (TextView) findViewById(R.id.iv_name);
        mIvList = (ImageView) findViewById(R.id.iv_list);
        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mIvZoom = (ImageView) findViewById(R.id.iv_zoom);
        mIvVolume = (ImageView) findViewById(R.id.iv_volume);
        mIvPlayPause = (ImageView) findViewById(R.id.iv_play_pause);
        mIvScreenLuminance = (ImageView) findViewById(R.id.iv_screen_luminance);
        mSbProgress = (SeekBar) findViewById(R.id.sb_progress);
        mTvMusicTime = (TextView) findViewById(R.id.tvMusicTime);
        mSbProgress.setMax(Param.Constants.maxProgress);
        if (isTvValue) {
            mForward = (ImageView) findViewById(R.id.iv_forward);
            mBackward = (ImageView) findViewById(R.id.iv_backward);
            mForward.setOnClickListener(this);
            mBackward.setOnClickListener(this);
        } else {
            mTvMoveTime = (TextView) findViewById(R.id.tv_text_move_time);
            mLlVolume = (LinearLayout) findViewById(R.id.ll_volume);
            mLlLuminance = (LinearLayout) findViewById(R.id.ll_screen_luminance);
            mPbVolume = (ProgressBar) findViewById(R.id.pb_volume);
            mTvScreenLuminance = (TextView) findViewById(R.id.tv_screen_luminance);
        }
        setControllerListener();
        showPlayerList(null, null, 0);
    }

    /**
     * 展示player的列表
     *
     * @param basePlayer 当前的player（基于SimplePlayer）
     * @param mPlayList  播放列表（MediaModel）
     * @param index      当前视频的index
     */
    private void showPlayerList(SimplePlayer basePlayer, ArrayList<MediaModel> mPlayList, int index) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mPlayListAdapter = new PlayerListAdapter(mContext, basePlayer, mPlayList, index);
        mRecyclerView.setAdapter(mPlayListAdapter);
        mPlayListAdapter.setOnPlayListItemListener(this);
    }


    public void showListData(SimplePlayer basePlayer, ArrayList<MediaModel> mPlayList, int index) {
        mPlayListAdapter.setData(basePlayer, mPlayList, index);
    }

    /**
     * 隐藏controller
     */
    public void hideController() {
        if (mRl.getVisibility() != View.GONE)
            mRl.setVisibility(View.GONE);
        if (mRecyclerView.getVisibility() != INVISIBLE)
            mRecyclerView.setVisibility(INVISIBLE);
    }

    /**
     * 显示controller
     */
    public void showController() {
        if (mRl.getVisibility() != View.VISIBLE)
            mRl.setVisibility(View.VISIBLE);
    }

    /**
     * 获取controller Visibility
     */
    public int getController() {
        return mRl.getVisibility();
    }

    /**
     * 隐藏PlayerList
     */
    public void hidePlayerList() {
        if (mRecyclerView.getVisibility() != View.INVISIBLE) {
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示PlayerList
     */
    public void showPlayerList() {
        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取PlayerList Visibility
     */
    public int getPlayerList() {
        return mRecyclerView.getVisibility();
    }


    //设置一系列监听
    private void setControllerListener() {
        mIvPlayPause.setOnClickListener(this);
        mIvPlay.setOnClickListener(this);
        mIvList.setOnClickListener(this);
        mIbBack.setOnClickListener(this);
        mIvZoom.setOnClickListener(this);
        mSbProgress.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (null != mOnProgressBarListener)
            mOnProgressBarListener.onProgressChanged(seekBar, i, b);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (null != mOnProgressBarListener)
            mOnProgressBarListener.onStartTrackingTouch(seekBar);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (null != mOnProgressBarListener)
            mOnProgressBarListener.onStopTrackingTouch(seekBar);
    }

    @Override
    public void onClick(View view) {
        if (null != mControllerListener)
            if (view.getId() == R.id.iv_play_pause || view.getId() == R.id.iv_play)
                mControllerListener.controllerPlayPause();
            else if (view.getId() == R.id.iv_list)
                mControllerListener.controllerList();
            else if (view.getId() == R.id.iv_zoom)
                mControllerListener.controllerZoom();
            else if (view.getId() == R.id.iv_back)
                mControllerListener.controllerBack();
            else if (view.getId() == R.id.iv_backward)
                mControllerListener.controllerBackward();
            else if (view.getId() == R.id.iv_forward)
                mControllerListener.controllerForward();


    }

    @Override
    public void setOnFocusChangeListener(View view, boolean b) {
        if (null != mOnPlayListItemListener)
            mOnPlayListItemListener.setOnFocusChangeListener(view, b);
    }

    @Override
    public void setOnClickListener(View view, int index) {
        if (null != mOnPlayListItemListener)
            mOnPlayListItemListener.setOnClickListener(view, index);
    }

    public void setOnPlayListItemListener(OnPlayListItemListener listener) {
        mOnPlayListItemListener = listener;
    }

    public void setOnProgressBarListener(OnProgressBarListener listener) {
        mOnProgressBarListener = listener;
    }

    public void setControllerListener(OnControllerListener listener) {
        mControllerListener = listener;
    }
}
