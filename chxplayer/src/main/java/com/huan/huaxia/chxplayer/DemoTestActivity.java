package com.huan.huaxia.chxplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.huan.huaxia.chxplayer.widght.hxplayer.PhonePlayer;
import com.huan.huaxia.chxplayer.widght.hxplayer.TvPlayer;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;

public class DemoTestActivity extends AppCompatActivity {
    private static ArrayList<String> list = new ArrayList<>();
    private static ArrayList<MediaModel> playList = new ArrayList<>();

    static {
        list.add("http://192.168.80.178:8080/hh.mp4");
        list.add("http://192.168.80.178:8080/yij.mp4");
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_master_playlist.m3u8");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/BBS/GXHZ/cdn2017042700001.ts");
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_848x480.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_1280x720.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_848x480.ts");
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_1280x720.ts");

        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_master_playlist.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_848x480.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_1280x720.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_848x480.ts");
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_1280x720.ts");

        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_master_playlist.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_848x480.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_1280x720.m3u8");
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_848x480.ts");
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_1280x720.ts");

        // 教育自身CDN
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/BDFZ-A/CDNBDFZ01202.ts");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151120/CDN2015112000307.ts");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000021.ts");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000028.ts");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000033.ts");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20130328-B/CDN2013032801080.ts");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/AK/CDN2016051800171.mp4");
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/AK/CDN2016051800228.mp4");
        for (String path : list) {
            MediaModel mediaModel = new MediaModel();
            mediaModel.setName(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf(".")));
            mediaModel.setImagPath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514542212294&di=6bce8e8e474a1bd472b87af138992eee&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F94cad1c8a786c91702b252f3c03d70cf3ac75742.jpg");
            mediaModel.setVideoPath(path);
            playList.add(mediaModel);
        }

    }

    private PhonePlayer mPlayerView;
    private TvPlayer mPlayerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_test);
//        mPlayerView = (PhonePlayer) findViewById(R.id.player_video);
        mPlayerView2 = (TvPlayer) findViewById(R.id.player_video2);
        Button btn = (Button) findViewById(R.id.btn);
        Button btn2 = (Button) findViewById(R.id.btn2);
//        mPlayerView.setVideoList(playList, false);
        mPlayerView2.setVideoList(playList, true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerView2.zoom();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerView2.playPause();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mPlayerView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
