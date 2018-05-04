package com.summer.chxplayer.widght.hxplayer;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
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
import com.summer.chxplayer.widght.utils.AnimalUtils;
import com.summer.chxplayer.widght.utils.Param;
import com.summer.chxplayer.widght.adapter.PlayerListAdapter;
import com.summer.chxplayer.widght.listener.OnControllerListener;
import com.summer.chxplayer.widght.listener.OnPlayListItemListener;
import com.summer.chxplayer.widght.listener.OnProgressBarListener;
import com.summer.chxplayer.widght.model.MediaModel;
import com.summer.chxplayer.widght.utils.StringUtils;

import java.util.ArrayList;

/**
 * controller player控制器
 * Created by huaxia on 2017/12/28.
 */
public class PlayerController extends FrameLayout implements PlayerControllerMethod, View.OnClickListener, SeekBar.OnSeekBarChangeListener, PlayerListAdapter.OnPlayListItemListener {
    private ViewGroup controller;
    private SeekBar mSbProgress;
    private TextView mTvMusicTime;
    private TextView mTvMoveTime;
    private RelativeLayout mRl;
    private ImageView mIbBack;
    private ImageView mIvSmallPlayer;
    private ImageView mIvBigPlay;
    private ImageView mIvList;
    private ImageView mIvZoom;
    private ImageView mIvVolume;
    private ImageView mIvScreenLuminance;
    private RecyclerView mRecyclerView;
    private LinearLayout mLlVolume;
    private LinearLayout mLlLuminance;
    private ProgressBar mPbVolume;
    private TextView mTvScreenLuminance;
    private TextView mName;
    private PlayerListAdapter mPlayListAdapter;
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
        mIvBigPlay = (ImageView) findViewById(R.id.iv_play);
        mIvZoom = (ImageView) findViewById(R.id.iv_zoom);
        mIvVolume = (ImageView) findViewById(R.id.iv_volume);
        mIvSmallPlayer = (ImageView) findViewById(R.id.iv_play_pause);
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
    @Override
    public void hideController() {
        if (mRl.getVisibility() != View.GONE)
            mRl.setVisibility(View.GONE);
        if (mRecyclerView.getVisibility() != INVISIBLE)
            mRecyclerView.setVisibility(INVISIBLE);
    }

    /**
     * 显示controller
     */
    @Override
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
     * 显示Or隐藏PlayerList
     */
    @Override
    public void showOrHidePlayerList() {
        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        } else if (mRecyclerView.getVisibility() != View.INVISIBLE) {
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setIvPlayImageResource(int src) {
        mIvBigPlay.setImageResource(src);
        mIvSmallPlayer.setImageResource(src);
    }

    @Override
    public void setIvZoomImageResource(int src) {
        mIvZoom.setImageResource(src);
    }

    @Override
    public void setIvPlayVisibility(int visibility) {
        mIvBigPlay.setVisibility(visibility);
    }

    @Override
    public void setIbBackVisibility(int visibility) {
        mIbBack.setVisibility(visibility);
    }

    @Override
    public void setName(String name) {
        mName.setText(name);
    }

    @Override
    public void setTvMusicTime(String musicTime) {
        mTvMusicTime.setText(musicTime);
    }

    @Override
    public void setIvPlayAlphaAnimator(int from, int to) {
        AnimalUtils.setAlphaAnimator(mIvBigPlay, from, to);

    }

    @Override
    public void setSbProgress(int time) {
        mSbProgress.setProgress(time);
    }

    @Override
    public void requestFocusPlayPause() {
        mIvSmallPlayer.requestFocus();
    }


    //设置一系列监听
    private void setControllerListener() {
        mIvSmallPlayer.setOnClickListener(this);
        mIvBigPlay.setOnClickListener(this);
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

    //动态改变Controller里控件的Size；在小屏时调用（确保屏幕适配）
    @Override
    public void changeControllerSize(boolean isNotFullScreen, int height, int width) {
        if (isNotFullScreen) {
            int windowWidth = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
            final int tempSizeText = (25 * height + windowWidth / 2) / windowWidth;
            final int tempSize20 = (20 * height + windowWidth / 2) / windowWidth;
            final int tempSizeListImage = (80 * height + windowWidth / 2) / windowWidth;
            final int tempSizeSmallPlay = (90 * height + windowWidth / 2) / windowWidth;
            final int tempSize160 = (160 * height + windowWidth / 2) / windowWidth;
            final int tempSize130 = (130 * height + windowWidth / 2) / windowWidth;
            final int tempSize200 = (200 * height + windowWidth / 2) / windowWidth;
            final int tempSize10 = (10 * height + windowWidth / 2) / windowWidth == 0 ? 1 : (10 * height + windowWidth / 2) / windowWidth;
            final int tempSize5 = (5 * height + windowWidth / 2) / windowWidth == 0 ? 1 : (5 * height + windowWidth / 2) / windowWidth;

            RelativeLayout.LayoutParams layoutParamsWRAPR = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsWRAP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams layoutParamsBigPlay = new RelativeLayout.LayoutParams(tempSize200, tempSize200);
            RelativeLayout.LayoutParams layoutParamsSmallPlay = new RelativeLayout.LayoutParams(tempSizeSmallPlay, tempSizeSmallPlay);
            LinearLayout.LayoutParams layoutParamsListImage = new LinearLayout.LayoutParams(tempSizeListImage, tempSizeListImage);
            LinearLayout.LayoutParams layoutParamsPbVolume = new LinearLayout.LayoutParams(tempSize10, tempSize160);
            LinearLayout.LayoutParams layoutParamsSbProgress = new LinearLayout.LayoutParams(width - tempSize160, 7);
            layoutParamsListImage.setMargins(tempSize5, tempSize5, tempSize5, tempSize5);
            layoutParamsSmallPlay.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParamsBigPlay.addRule(RelativeLayout.CENTER_IN_PARENT);
            layoutParamsWRAP.setMargins(tempSize20, tempSize20, tempSize20, tempSize20);
            layoutParamsWRAPR.setMargins(tempSizeListImage, 0, 0, 0);
            layoutParamsWRAPR.addRule(RelativeLayout.CENTER_VERTICAL);

            mIvBigPlay.setLayoutParams(layoutParamsBigPlay);
            mIbBack.setLayoutParams(layoutParamsListImage);
            mIvList.setLayoutParams(layoutParamsListImage);
            mIvZoom.setLayoutParams(layoutParamsListImage);
            mIvZoom.setPadding(tempSize5, tempSize5, tempSize5, tempSize5);
            mIvSmallPlayer.setLayoutParams(layoutParamsSmallPlay);
            mIvVolume.setLayoutParams(layoutParamsListImage);
            mIvScreenLuminance.setLayoutParams(layoutParamsListImage);
            mPbVolume.setLayoutParams(layoutParamsPbVolume);

            mLlVolume.setPadding(tempSize5, tempSize5, tempSize5, tempSize5);
            mLlVolume.setLayoutParams(layoutParamsWRAPR);

            mLlLuminance.setPadding(0, 0, tempSizeListImage, 0);

            mSbProgress.setLayoutParams(layoutParamsSbProgress);
            mSbProgress.setThumb(mContext.getResources().getDrawable(R.mipmap.audio_seek_thumb2));
            mSbProgress.setPadding(2, 1, 2, 1);

            mName.setLayoutParams(layoutParamsWRAP);
            mName.setTextSize(tempSizeText);
            mTvMusicTime.setTextSize(tempSizeText);
            mTvMoveTime.setTextSize(tempSizeText);
            mTvScreenLuminance.setTextSize(tempSizeText);
            mPlayListAdapter.setChangeSizeListener(new PlayerListAdapter.ChangeSizeListener() {
                @Override
                public void setChangeTextSizeListener(TextView view) {
                    view.setTextSize(tempSize20);
                }

                @Override
                public void setChangeImageSizeListener(ImageView view) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tempSize160, tempSize130);
                    view.setLayoutParams(layoutParams);
                }

                @Override
                public void setChangeItemViewSizeListener(LinearLayout view) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(tempSize200, tempSize200);
                    layoutParams.setMargins(1, 1, 1, 1);
                    view.setLayoutParams(layoutParams);
                }
            });
        }
    }

    /////////屏幕滑动效果
    @Override
    public void hideMoveView() {
        mTvMoveTime.setVisibility(GONE);
        mLlVolume.setVisibility(GONE);
        mLlLuminance.setVisibility(GONE);
    }

    //set声音
    @Override
    public void setVolumeView(int moveVolume) {
        mLlVolume.setVisibility(VISIBLE);
        mPbVolume.setProgress(moveVolume);
    }
    //set亮度
    @Override
    public void setLuminanceView(String moveBrightness) {
        mLlLuminance.setVisibility(VISIBLE);
        mTvScreenLuminance.setText(moveBrightness);
    }
    //set移动至
    @Override
    public void setMoveTimeView(int move) {
        mTvMoveTime.setVisibility(VISIBLE);
        mTvMoveTime.setText(StringUtils.formatPlayTime(move));
    }
}
