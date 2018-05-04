package com.summer.chxplayer.widght.listener;

import android.view.View;

/**
 * 设置PlayListItem监听
 */

public interface OnPlayListItemListener {
    void setOnFocusChangeListener(View view, boolean b);

    void setOnClickListener(View view, int index);
}
