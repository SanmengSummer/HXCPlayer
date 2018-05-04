package com.summer.chxplayer.widght.listener;

import android.widget.SeekBar;

/**
 * 设置progressbar滑动监听
 */

public interface OnProgressBarListener {
    void onProgressChanged(SeekBar seekBar, int percent, boolean b);

    void onStartTrackingTouch(SeekBar seekBar);

    void onStopTrackingTouch(SeekBar seekBar);
}
