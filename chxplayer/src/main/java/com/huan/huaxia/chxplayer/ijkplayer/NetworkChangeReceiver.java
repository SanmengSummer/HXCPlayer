package com.huan.huaxia.chxplayer.ijkplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.huan.huaxia.chxplayer.widght.HXPlayerActivity;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private boolean isRegisterReceiver = false;
    private int lastType = -2;
    private HXPlayerActivity mActivity = null;

    public NetworkChangeReceiver(HXPlayerActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        String action = arg1.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.i("onReceive", "网络状态改变");
            // 获得网络连接服务
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connManager) {
                NetworkInfo info = connManager.getActiveNetworkInfo();
                if ((info == null || !info.isAvailable()) && lastType != -1) {
                    Log.i("onReceive", "网络连接已中断");
                    lastType = -1;
                    //退出播放
                    if (null != mActivity) {
                        mActivity.finish();
                    }
                }
            }
        }
    }

    public void registerReceiver() {
        if (!isRegisterReceiver && null != mActivity) {
            isRegisterReceiver = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mActivity.registerReceiver(this, filter);
            Log.i("registerReceiver", "注册网络广播接收者...");
        }
    }

    public void unRegisterReceiver() {
        if (isRegisterReceiver && null != mActivity) {
            isRegisterReceiver = false;
            mActivity.unregisterReceiver(this);
            mActivity = null;
            Log.i("unRegisterReceiver", "注销网络广播接收者...");
        }
    }
}