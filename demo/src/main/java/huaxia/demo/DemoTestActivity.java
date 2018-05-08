package huaxia.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.summer.chxplayer.widght.hxplayer.PhonePlayer;
import com.summer.chxplayer.widght.hxplayer.TvPlayer;
import com.summer.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;

public class DemoTestActivity extends AppCompatActivity {
    private static ArrayList<String> list = new ArrayList<>();
    private static ArrayList<MediaModel> playList = new ArrayList<>();

    static {
        list.add("http://192.168.80.78:8080/hh.mp4");
        list.add("http://192.168.80.78:8080/papa.mp4");
        list.add("http://192.168.80.78:8080/yij.mp4");
        list.add("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
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
            mediaModel.setName(path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("/") < path.lastIndexOf(".") ? path.lastIndexOf(".") : path.length()));
            mediaModel.setImagPath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514542212294&di=6bce8e8e474a1bd472b87af138992eee&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F94cad1c8a786c91702b252f3c03d70cf3ac75742.jpg");
            mediaModel.setVideoPath(path);
            playList.add(mediaModel);
        }
    }

    private PhonePlayer mPlayerView;
    private TvPlayer mPlayerView2;
    private RecyclerView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_test);
        videoView = (RecyclerView) findViewById(R.id.re_video);
        mPlayerView = (PhonePlayer) findViewById(R.id.player_video);
        mPlayerView2 = (TvPlayer) findViewById(R.id.player_video2);
        Button btn = (Button) findViewById(R.id.btn);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        mPlayerView.setVideoList(playList);
        mPlayerView2.setVideoList(playList);
        mPlayerView.setShowPoint(true);
        mPlayerView2.setShowPoint(true);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerView2.showZoom();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayerView2.playPause();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DemoTestActivity.this, NewActivity.class));
            }
        });
        videoView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(videoView);
        videoView.setAdapter(myAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.pause();
        mPlayerView2.pause();
        mPlayerView.pause();
    }

    @Override
    protected void onDestroy() {
        System.exit(0);
        super.onDestroy();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        private final RecyclerView recyclerView;

        public MyAdapter(RecyclerView videoView) {
            recyclerView = videoView;
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(DemoTestActivity.this).inflate(R.layout.item_list, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            holder.video.setShowDialogOrPoint(false);
            holder.video.setVideoPath("http://192.168.80.78:8080/papa.mp4");
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private PhonePlayer video;

        public MyHolder(View itemView) {
            super(itemView);
            video = itemView.findViewById(R.id.pp);
        }
    }
}
