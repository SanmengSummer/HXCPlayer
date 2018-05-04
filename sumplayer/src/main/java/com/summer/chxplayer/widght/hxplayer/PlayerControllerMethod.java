package com.summer.chxplayer.widght.hxplayer;


/**
 * Created by admin on 2018/5/3.
 */

interface PlayerControllerMethod {
    int getController();

    void hideController();

    void showController();

    void showOrHidePlayerList();

    void setIvPlayImageResource(int src);

    void setIvZoomImageResource(int src);

    void setIvPlayVisibility(int visibility);

    void setIbBackVisibility(int visibility);

    void setName(String name);

    void setTvMusicTime(String musicTime);

    void setIvPlayAlphaAnimator(int from, int to);

    void setSbProgress(int time);

    void requestFocusPlayPause();

    void changeControllerSize(boolean isNotFullScreen, int height, int width);

    void hideMoveView();

    void setVolumeView(int moveVolume);

    void setLuminanceView(String moveBrightness);

    void setMoveTimeView(int move);
}
