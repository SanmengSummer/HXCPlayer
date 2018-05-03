package com.huan.huaxia.chxplayer.widght.utils;

/**
 * Created by fullcircle on 2017/7/16.
 */

public class StringUtils {
    /**
     * 格式化字符串 传入毫秒值 返回 mm:ss这样的格式
     * @param time
     * @return
     */
    public static String formatPlayTime(int time){
//        01:02:03   03:44
        int hour = 60*60*1000;
        int minute = 60*1000;
        int second = 1000;

        int h = time/hour;
        int min = time%hour/minute;
        int sec = time%minute/second;

        if(h>0){
            //时间超出1小时了  %d 占位符 对应int类型 02d 不足两位的表示成2位 用0补位
            return String.format("%02d:%02d:%02d",h,min,sec);
        }else{
            return String.format("%02d:%02d",min,sec);
        }


    }
}
