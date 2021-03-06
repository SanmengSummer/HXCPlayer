/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huan.edu.tvplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.huan.edu.tvplayer.bean.MediaBean;

import java.util.ArrayList;
import java.util.List;


public class PlayerSettings {
    public static final int REQUEST_CODE = 9001;
    public static final int RESULT_CODE = 9002;
    public static final int RESULT_CODE_PAY = 9003;

    public static final int PLAYER_TYPE_AUTO = 0;
    public static final int PLAYER_TYPE_ANDROID = 1;//硬解
    public static final int PLAYER_TYPE_IJK = 2;//软解
    public static final int PLAYER_TYPE_EXO = 3;

    public static final int PAY_JUMP_MODE_RESULT = 1;
    public static final int PAY_JUMP_MODE_ACTIVITY = 2;
    public static boolean isChangeVideoSize;

    private Context mAppContext;
    private SharedPreferences mSharedPreferences;
    private static PlayerSettings mInstance;

    private String mHuanId;

    private int mPlayIndex;
    private List<MediaBean> mMediaBeanList;

    private int mPayJumpMode = -1;
    private Bundle mPayBundle;
    private String mPayClassName;

    private boolean mIsRequestPlayAddress = false;
    private String mRequestPlayAddressUrl;
    private String mRequestCheckOrderUrl;

    private PlayerSettings(Context context) {
        mAppContext = context.getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public synchronized static PlayerSettings getInstance(Context context) {
        if (null == mInstance) {
            mInstance = new PlayerSettings(context);
        }
        return mInstance;
    }

    public void saveState(Bundle outState) {
        if (null == outState) return;
        outState.putString("huan_id", mHuanId);
        outState.putInt("play_index", mPlayIndex);
        outState.putParcelableArrayList("media_list", new ArrayList<>(mMediaBeanList));
        outState.putInt("pay_jump_mode", mPayJumpMode);
        if (null != mPayBundle)
            outState.putBundle("pay_bundle", mPayBundle);
        if (!TextUtils.isEmpty(mPayClassName))
            outState.putString("pay_class_name", mPayClassName);
        outState.putBoolean("is_request_play_address", mIsRequestPlayAddress);
        outState.putString("request_play_address_url", mRequestPlayAddressUrl);
    }

    public void restoreState(@Nullable Bundle savedInstanceState) {
        if (null == savedInstanceState) return;
        mHuanId = savedInstanceState.getString("huan_id");
        mPlayIndex = savedInstanceState.getInt("play_index", 0);
        mMediaBeanList = savedInstanceState.getParcelableArrayList("media_list");
        mPayJumpMode = savedInstanceState.getInt("pay_jump_mode", -1);
        mPayBundle = savedInstanceState.getBundle("pay_bundle");
        mPayClassName = savedInstanceState.getString("pay_class_name");
        mIsRequestPlayAddress = savedInstanceState.getBoolean("is_request_play_address", false);
        mRequestPlayAddressUrl = savedInstanceState.getString("request_play_address_url");
    }

    public String getHuanId() {
        return mHuanId;
    }

    public PlayerSettings setHuanId(String huanId) {
        this.mHuanId = huanId;
        return this;
    }

    public boolean getUsingHardwareDecoder() {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec);
        return mSharedPreferences.getBoolean(key, false);
    }

    public PlayerSettings setUsingHardwareDecoder(boolean value) {
        String key = mAppContext.getString(R.string.pref_key_using_media_codec);
        mSharedPreferences.edit().putBoolean(key, value).apply();
        return this;
    }

    public PlayerSettings setMediaCodecHandleResolutionChange(boolean value) {
        String key = mAppContext.getString(R.string.pref_key_media_codec_handle_resolution_change);
        mSharedPreferences.edit().putBoolean(key, value).apply();
        return this;
    }

    public PlayerSettings setPlayerType(int type) {
        String key = mAppContext.getString(R.string.pref_key_player);
        mSharedPreferences.edit().putString(key, String.valueOf(type)).apply();
        return this;
    }

    public boolean isAndroidPlayer() {
        String key = mAppContext.getString(R.string.pref_key_player);
        String value = mSharedPreferences.getString(key, "");
        try {
            return PLAYER_TYPE_ANDROID == Integer.valueOf(value).intValue();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public PlayerSettings setPlayIndex(int index) {
        mPlayIndex = index;
        return this;
    }

    public int getPlayIndex() {
        return mPlayIndex;
    }

    public PlayerSettings setMediaList(List<MediaBean> list) {
        this.mMediaBeanList = list;
        return this;
    }

    public List<MediaBean> getMediaList() {
        return mMediaBeanList;
    }

    public MediaBean getCurrMedia() {
        if (null != mMediaBeanList) {
            if (mPlayIndex < mMediaBeanList.size()) {
                return mMediaBeanList.get(mPlayIndex);
            }
        }
        return null;
    }

    public MediaBean getNextMedia() {
        if (null != mMediaBeanList) {
            int temp = mPlayIndex + 1;
            MediaBean mediaBean = mMediaBeanList.get(temp);
            if (temp < mMediaBeanList.size()) {
                if (TextUtils.equals("0", mediaBean.isBuy)) {
                    mPlayIndex += 1;
                }
                return mediaBean;
            }
        }
        return null;
    }

    public void startPlayer(Activity activity) {
        activity.startActivityForResult(new Intent(mAppContext, EduPlayerActivity.class), REQUEST_CODE);
    }


    public PlayerSettings setPayJumpMode(int jumpMode) {
        mPayJumpMode = jumpMode;
        return this;
    }

    public int getPayJumpMode() {
        return mPayJumpMode;
    }

    public PlayerSettings setPayClassName(String className) {
        mPayClassName = className;
        return this;
    }

    public String getPayClassName() {
        return mPayClassName;
    }

    public PlayerSettings setPayBundle(Bundle payBundle) {
        mPayBundle = payBundle;
        return this;
    }

    public Bundle getPayBundle() {
        return mPayBundle;
    }

    /////////////////////////////////////////////////////////////////////////////////

    public PlayerSettings setIsRequestPlayAddress(boolean isRequest) {
        mIsRequestPlayAddress = isRequest;
        return this;
    }

    public boolean isRequestPlayAddress() {
        return mIsRequestPlayAddress;
    }

    public PlayerSettings setRequestPlayAddressUrl(String url) {
        mRequestPlayAddressUrl = url;
        return this;
    }

    public String getRequestPlayAddressUrl() {
        return mRequestPlayAddressUrl;
    }

    public String getRequestCheckOrderUrl() {
        return mRequestCheckOrderUrl;
    }

    public PlayerSettings setRequestCheckOrderUrl(String requestCheckOrderUrl) {
        mRequestCheckOrderUrl = requestCheckOrderUrl;
        return this;
    }

    public PlayerSettings changeVideoSize(boolean isChangeVideoSize) {
        PlayerSettings.isChangeVideoSize = isChangeVideoSize;
        return this;
    }
}
