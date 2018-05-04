package com.summer.chxplayer.widght.hxplayer;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Tv版player可追加新功能
 * Created by huaxia on 2017/11/9.
 */

public class TvPlayer extends SimplePlayer implements SimplePlayer.PointListener {
    public TvPlayer(Context context) {
        super(context);
        initExtra();
    }

    public TvPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initExtra();
    }

    public TvPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initExtra();
    }

    private void initExtra() {
        onPointListener(this);
        isTV = true;
    }

    @Override
    public void showZoom(boolean isChangePlayerSize) {
        super.showZoom(isChangePlayerSize);
    }

    @Override
    public void onPointListener() {
        if (null != mListener) {
            mListener.setOnPointListener();
        }
    }

    private PointListener mListener;

    public void setOnPointListener(PointListener listener) {
        mListener = listener;
    }

    interface PointListener {
        void setOnPointListener();
    }

}
