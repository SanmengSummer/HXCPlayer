package com.huan.huaxia.chxplayer.widght.event;

import java.io.Serializable;

/**
 * Created by huaxia on 2017/12/14.
 */

public class PlayEvent implements Serializable{
    public int index;
    public int duration;
    public boolean isPlaying;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Override
    public String toString() {
        return "PlayEvent{" +
                "index=" + index +
                ", duration=" + duration +
                ", isPlaying=" + isPlaying +
                '}';
    }
}
