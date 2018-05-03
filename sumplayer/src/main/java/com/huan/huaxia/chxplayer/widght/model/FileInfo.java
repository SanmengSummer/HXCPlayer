package com.huan.huaxia.chxplayer.widght.model;

import java.io.Serializable;

/**
 * Created by huaxia on 2018/1/3.
 */

public class FileInfo implements Serializable {
    private String url; //URL
    private String fileName; //文件名
    private int length; //长度或结束位置
    private int start; //开始位置
    private int now;//当前进度

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getNow() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }

}