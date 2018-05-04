package com.summer.chxplayer.widght.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;


/**
 * @title: CacheUtil.java
 * @author: huaxia
 * @description: 获取各种缓存目录的工具类
 */

public class CacheUtil {

    public static Boolean existsSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取图片缓存目录
     */
    public static String getImgCachePath(Context appContext) {
        if (existsSdcard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appContext.getPackageName() + "/ImgCache";
        } else {
            String extStorageDirectory = appContext.getApplicationContext().getFilesDir().getAbsolutePath();
            return extStorageDirectory + "/ImgCache";
        }
    }

    /**
     * 获取视频缓存目录
     */
    public static String getMovieCachePath(Context appContext) {
        if (existsSdcard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appContext.getPackageName() + "/ImgCache";
        } else {
            String extStorageDirectory = appContext.getApplicationContext().getFilesDir().getAbsolutePath();
            return extStorageDirectory + "/MovieCache";
        }
    }

    /**
     * 获取APK下载保存目录
     */
    public static String getUpdateApkCachePath(Context appContext) {
        if (existsSdcard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appContext.getPackageName() + "/ApkCache";
        } else {
            String extStorageDirectory = appContext.getApplicationContext().getFilesDir().getAbsolutePath();
            return extStorageDirectory + "/ApkCache";
        }
    }

    /**
     * 检查磁盘空间是否大于指定值
     *
     * @return true 大于
     */
    public static boolean isDiskAvailable(Context context, long size) {
        String path;
        if (existsSdcard()) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            path = context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
        long freeSize = getAvailableSize(path);
        return freeSize > size;
    }

    /**
     * 获取磁盘可用空间
     *
     * @return byte 单位 kb
     */
    public static long getAvailableSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            availableBlocks = stat.getAvailableBlocks();
        }
        return availableBlocks * blockSize;
        // (availableBlocks * blockSize)/1024 KIB 单位
        // (availableBlocks * blockSize)/1024 /1024 MIB单位
    }

    /**
     * 获取文件大小
     *
     * @return
     */
    public static long getFileOrDirSize(File file) {
        if (!file.exists()) return 0;
        if (!file.isDirectory()) return file.length();

        long length = 0;
        File[] list = file.listFiles();
        if (list != null) { // 文件夹被删除时, 子文件正在被写入, 文件属性异常返回null.
            for (File item : list) {
                length += getFileOrDirSize(item);
            }
        }

        return length;
    }

    /**
     * 删除指定文件或文件夹下的所有文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFileOrDir(String path) {
        File file = new File(path);
        if (file.isFile()) {
            if (file.exists())
                return file.delete();
        } else if (file.isDirectory()) {
            return delAllFile(path);
        }
        return false;
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        if (null == tempList)
            return true;
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String e = folderPath.toString();
            File myFilePath = new File(e);
            myFilePath.delete();
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
