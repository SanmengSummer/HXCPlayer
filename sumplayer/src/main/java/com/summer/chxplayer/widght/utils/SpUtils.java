package com.summer.chxplayer.widght.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by huaxia on 2017/7/23.
 */

public class SpUtils {

    private static SharedPreferences sp;
    private static final String IM_CONFIG = "IM_Config";

    public static void putString(Context context, String key, String value) {
        if (sp == null) {
            sp = context.getSharedPreferences(IM_CONFIG, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).apply();
    }

    public static String getString(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(IM_CONFIG, Context.MODE_PRIVATE);
        }
        return sp.getString(key, "");
    }
    public static void putBoolean(Context context, String key, boolean value) {
        if (sp == null) {
            sp = context.getSharedPreferences(IM_CONFIG, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences(IM_CONFIG, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, false);
    }
}
