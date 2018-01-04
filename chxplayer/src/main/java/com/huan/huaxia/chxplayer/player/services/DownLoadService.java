package com.huan.huaxia.chxplayer.player.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.huan.huaxia.chxplayer.widght.model.FileInfo;
import com.huan.huaxia.chxplayer.widght.utils.CacheUtil;
import com.huan.huaxia.chxplayer.widght.utils.Param;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownLoadService extends Service {

    private FileInfo fileInfo;

    public DownLoadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fileInfo = (FileInfo) intent.getSerializableExtra(Param.Constants.fileUrl);
        DownLoadThead downLoadThead = new DownLoadThead();
        downLoadThead.start();
        downLoadThead.stop();
        downLoadThead.destroy();
        return super.onStartCommand(intent, flags, startId);
    }

    public class DownLoadThead extends Thread {
        @Override
        public void run() {
            super.run();

        }

    }
}
