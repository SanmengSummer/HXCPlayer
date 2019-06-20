package huaxia.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.summer.chxplayer.widght.hxplayer.PhonePlayer
import com.summer.chxplayer.widght.model.MediaModel
import kotlinx.android.synthetic.main.activity_demo_test.*

import java.util.ArrayList

class DemoTestActivity : AppCompatActivity() {
    companion object {
        private var list = ArrayList<String>()
        private var playList = ArrayList<MediaModel>()
    }

    init {
//        list.add("http://192.168.80.78:8080/hh.mp4")
//        list.add("http://192.168.80.78:8080/papa.mp4")
//        list.add("http://192.168.80.78:8080/yij.mp4")
        list.add("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4")
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_848x480.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_1280x720.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_848x480.ts")
        list.add("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_1280x720.ts")

        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_master_playlist.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_848x480.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_1280x720.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_848x480.ts")
        list.add("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_1280x720.ts")

        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_master_playlist.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_848x480.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_1280x720.m3u8")
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_848x480.ts")
        list.add("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_1280x720.ts")

        // 教育自身CDN
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/BDFZ-A/CDNBDFZ01202.ts")
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151120/CDN2015112000307.ts")
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000021.ts")
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000028.ts")
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000033.ts")
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/20130328-B/CDN2013032801080.ts")
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/AK/CDN2016051800171.mp4")
        list.add("http://lecloud.educdn.huan.tv/mediadns/ts/AK/CDN2016051800228.mp4")
        for (path in list) {
            var mediaModel = MediaModel()
            mediaModel.setName(path.substring(path.lastIndexOf("/") + 1, if (path.lastIndexOf("/") < path.lastIndexOf(".")) path.lastIndexOf(".") else path.length))
            mediaModel.setImagPath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1514542212294&di=6bce8e8e474a1bd472b87af138992eee&imgtype=0&src=http%3A%2F%2Ff.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F94cad1c8a786c91702b252f3c03d70cf3ac75742.jpg")
            mediaModel.setVideoPath(path)
            playList.add(mediaModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_test)
        btn.setOnClickListener { player_video2?.showZoom() }
        btn2.setOnClickListener { player_video2?.playPause() }
        btn3.setOnClickListener {
            startActivity(Intent(this, NewActivity::class.java))
        }
        re_video?.layoutManager = LinearLayoutManager(this)
        var myAdapter = re_video?.let { MyAdapter(it) }
        re_video?.adapter = myAdapter
    }

    override fun onPause() {
        super.onPause()
        player_video?.pause()
        player_video2?.pause()
        player_video?.pause()
    }

    override fun onDestroy() {
        System.exit(0)
        super.onDestroy()
    }

    private class MyAdapter(videoView: RecyclerView) : RecyclerView.Adapter<MyHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder {
            var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_list, parent, false)
            return MyHolder(view)
        }

        override fun getItemCount(): Int = 10

        override fun onBindViewHolder(holder: MyHolder?, position: Int) {
            holder!!.video.setShowDialogOrPoint(false)
            holder!!.video.setVideoPath("http://192.168.80.59:8080/papa.mp4")
        }
    }

    class MyHolder : RecyclerView.ViewHolder {
        internal var video: PhonePlayer

        constructor  (itemView: View) : super(itemView) {
            video = itemView.findViewById(R.id.pp)
        }
    }
}
