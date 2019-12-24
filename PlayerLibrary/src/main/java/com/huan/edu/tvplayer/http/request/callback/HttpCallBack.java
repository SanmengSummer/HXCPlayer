package com.huan.edu.tvplayer.http.request.callback;


import com.huan.edu.tvplayer.http.util.DebugUtils;

/**
 * Created by linlongxin on 2015/12/26.
 */
public abstract class HttpCallBack {


    public abstract void success(String info);

    public void failure(int status, String info) {

    }

    public void getRequestTimes(int responseCode, String info, int requestNum) {
        if (DebugUtils.isDebug)
            DebugUtils.responseLog(responseCode + "\n" + info, requestNum);
    }

    public void getRequestTimes(String info, int requestNum) {
        if (DebugUtils.isDebug)
            DebugUtils.responseLog(info, requestNum);
    }
}
