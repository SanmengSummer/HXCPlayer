package com.summer.chxplayer.widght.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huan.huaxia.chxplayer.R;


public class DialogUtil {
    //new dialog
    public static void showUnSubscribeDialog(final Context mContext, boolean isTV, boolean isFullScreen) {
        View view = LayoutInflater.from(mContext).inflate(isTV ? R.layout.dialog_layout : R.layout.simple_dialog_layout, null);
        final Dialog dialog = new Dialog(mContext, R.style.common_dialog);
        dialog.setContentView(view);

        Button exit = (Button) view.findViewById(R.id.btn_exit);
        Button loading = (Button) view.findViewById(R.id.btn_re_loading);
        focusChangeListenerHepler(loading, mContext);
        focusChangeListenerHepler(exit, mContext);
        loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (null != listener)
                    listener.reLoading();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                ((Activity) mContext).finish();
            }
        });

        if (null != dialog.getWindow() && !((Activity) mContext).isFinishing()) {
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            if (isTV || isFullScreen)
                dialog.getWindow().setLayout(display.getWidth() * 3 / 5, display.getHeight() / 2);
            else
                dialog.getWindow().setLayout(display.getWidth() * 2 / 3, display.getHeight() / 3);
            /*else
                dialog.getWindow().setLayout(display.getWidth() * 2 / 3, display.getHeight() / 3);*/
            dialog.show();
        }
    }

    //设置一个焦点改变帮助器,只用于当前dialog使用;
    private static void focusChangeListenerHepler(View view, final Context mContext) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                ((TextView) view).setTextColor(b ? mContext.getResources().getColor(R.color.white) : mContext.getResources().getColor(R.color.focus));
            }
        });

    }

    private static OnReLoadingListener listener;

    public static void setOnReLoadingListener(OnReLoadingListener reLoadingListener) {
        listener = reLoadingListener;
    }

    public interface OnReLoadingListener {
        void reLoading();
    }
}
