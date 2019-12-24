package huaxia.demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huan.edu.tvplayer.PlayerSettings;
import com.huan.edu.tvplayer.bean.MediaBean;
import com.huan.edu.tvplayer.bean.ParamBean;

import java.util.ArrayList;
import java.util.Random;

public class HuanPlayerDemo extends AppCompatActivity {
    private static final String TAG = HuanPlayerDemo.class.getSimpleName();
    
    private ListView mListView;

    private static ArrayList<MediaBean> playList = new ArrayList<MediaBean>();

    public static MediaBean get(String url, String name, String isBuy) {
        String id = String.valueOf(new Random().nextInt(9000) + 1000);
        return get(id, url, name, isBuy);
    }

    public static MediaBean get(String id, String url, String name, String isBuy) {
        MediaBean bean = new MediaBean();
        bean.id = id;
        bean.name = name;
        bean.playUrl = url;
        bean.isBuy = isBuy;
        bean.isPlayAd = true;
//        if (TextUtils.equals("-1", bean.isBuy)) {
//            Bundle bundle = new Bundle();
//            bundle.putString("k", "Bundle");
//            bean.buyBundle = bundle;
//            bean.buyAction = "com.huan.edu.tvplayer.demo.BUY";
//        }
        return bean;
    }


    static {
        
//        playList.add(get("http://baishancloud.lexue.huan.tv/4quanpin/xiaoxueshuxue%28563jie%29/renjiaoban254jie/ernianji37jie/ernianjishangce17/huoyanjinjingguanchawuti.ts", "baishanyun-test1", "0"));
//        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/20130328-B/CDN2013032801096.ts", "暴风验证", "0"));
        playList.add(get("https://www.xbztc.cn/2.mp4", "https视频", "0"));
        playList.add(get("http://baishancloud.lexue.huan.tv/20181122/dianfuketangdierpi/gaozhongzhengzhi/tongyongban-zhengzhi-jichuban-gaokaozongfuxi-yanghuaining-di2bufenjinengguifanhuikou-zhuantiyixuekejinengqianghuayuguifandati-1.huoquhejieduxinxidenengli-0.1.ts", "播放卡顿无法播放视频", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_master_playlist.m3u8", "CDN2016091200001_master_playlist.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_848x480.m3u8", "CDN2016091200001_848x480.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_1280x720.m3u8", "CDN2016091200001_1280x720.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_848x480.ts", "CDN2016091200001_848x480.ts", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/CDN2016091200001_1280x720.ts", "CDN2016091200001_1280x720.ts", "0"));
        
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_master_playlist.m3u8", "小老鼠打电话-教学版_master_playlist.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_848x480.m3u8", "小老鼠打电话-教学版_848x480.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_1280x720.m3u8", "小老鼠打电话-教学版_1280x720.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_848x480.ts", "小老鼠打电话-教学版_848x480.ts", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/小老鼠打电话-教学版_1280x720.ts", "小老鼠打电话-教学版_1280x720.ts", "0"));
        
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_master_playlist.m3u8", "龅牙妹的烦恼_master_playlist.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_848x480.m3u8", "龅牙妹的烦恼_848x480.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_1280x720.m3u8", "龅牙妹的烦恼_1280x720.m3u8", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_848x480.ts", "龅牙妹的烦恼_848x480.ts", "0"));
        playList.add(get("http://s2.i.qingcdn.com/edu-test-output/龅牙妹的烦恼_1280x720.ts", "龅牙妹的烦恼_1280x720.ts", "0"));
        
        // 教育自身CDN
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/BDFZ-A/CDNBDFZ01202.ts", "老视频快进退问题1", "0"));
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/20151120/CDN2015112000307.ts", "测试1", "0"));
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000021.ts", "测试2", "0"));
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000028.ts", "测试3", "0"));
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/20151130/CDN2015113000033.ts", "测试4", "0"));
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/20130328-B/CDN2013032801080.ts", "720x576 TS", "0"));
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/AK/CDN2016051800171.mp4", "梦幻车神-1080p", "0"));
        playList.add(get("http://lecloud.educdn.huan.tv/mediadns/ts/AK/CDN2016051800228.mp4", "精灵梦-1080p", "0"));
        

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Dialog alertDialog;
//    private final String[] dialogItems = new String[]{"全屏播放模式", "窗口播放模式"};
    private final String[] dialogItems = new String[]{"系统原生硬解码播放", "第三方软解码播放", "第三方硬解码播放"};
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huan_player_demo);
        
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setAdapter(new PlayListAdapter());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                showPlayModeDialog();
            }
        });
    }

    private void showPlayModeDialog() {
        if (null == alertDialog) {
            alertDialog = new AlertDialog.Builder(this)
                    .setItems(dialogItems, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            play(which);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
        }
        alertDialog.setTitle("准备播放第 " + (mPosition + 1) + " 条视频 - 请选择播放模式");
        alertDialog.show();
    }

    private void play(int mode) {
        PlayerSettings
                .getInstance(getApplicationContext())
                .setUsingHardwareDecoder(false) //设置是否硬解码（与PLAYER_TYPE_IJK配套使用）
                .setMediaCodecHandleResolutionChange(false) //设置是否解码器处理分辨率变化(暂时没发现有什么作用，与PLAYER_TYPE_IJK配套使用)
                .setPlayerType(PlayerSettings.PLAYER_TYPE_IJK) //设置播放器底层
                .setPlayIndex(mPosition) //设置播放index
                .setMediaList(playList) //设置播放列表
                .setHuanId("888888") //设置huanId
                
                .setIsRequestPlayAddress(false) //设置是否请求播放地址
                .setRequestPlayAddressUrl("http://www.baidu.com") //设置请求播放地址接口
                
                .setPayJumpMode(PlayerSettings.PAY_JUMP_MODE_RESULT) //设置支付跳转的方式
                .setPayClassName("com.huan.edu.PayActivity") //设置支付页面的className(与PAY_JUMP_MODE_ACTIVITY配套使用)
                .setPayBundle(new Bundle()) //设置跳转支付页面时所需的参数(与PAY_JUMP_MODE_ACTIVITY配套使用)
        ;
        
        switch (mode) {
            case 0:
                PlayerSettings
                        .getInstance(getApplicationContext())
                        .setPlayerType(PlayerSettings.PLAYER_TYPE_ANDROID);
                break;
            case 1:
                PlayerSettings
                        .getInstance(getApplicationContext())
                        .setUsingHardwareDecoder(false) //设置是否硬解码（与PLAYER_TYPE_IJK配套使用）
                        .setPlayerType(PlayerSettings.PLAYER_TYPE_IJK);
                break;
            
            case 2:
                PlayerSettings
                        .getInstance(getApplicationContext())
                        .setUsingHardwareDecoder(true) //设置是否硬解码（与PLAYER_TYPE_IJK配套使用）
                        .setPlayerType(PlayerSettings.PLAYER_TYPE_IJK);
                break;
        }

        PlayerSettings
                .getInstance(getApplicationContext())
                .startPlayer(HuanPlayerDemo.this);

//        startActivityForResult(
//                        new Intent(getApplicationContext(), WindowVideoActivity.class), 
//                        PlayerSettings.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PlayerSettings.REQUEST_CODE) {
            
            if(null != data) {
                int position = data.getIntExtra(ParamBean.KEY_PLAY_INDEX, -1);
                int time = data.getIntExtra(ParamBean.KEY_PLAYED_TIME, -1);
                int durationTime = data.getIntExtra(ParamBean.KEY_DURATION_TIME, -1);
                MediaBean mediaBean = data.getParcelableExtra(ParamBean.KEY_MEDIA_OBJECT);
                // do something
                Log.d(TAG, " position="+position+" ,time="+time+" ,durationTime="+durationTime);
            }
            Log.d(TAG, "resultCode="+resultCode);
            switch (resultCode) {
                case PlayerSettings.RESULT_CODE: // 正常回传
                    Log.d(TAG, "正常回传...");
                    break;
                case PlayerSettings.RESULT_CODE_PAY: // 支付回传
                    Log.d(TAG, "支付回传...");
                    break;
            }
            
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }
    

    private class PlayListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return playList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHold mViewHold;
            if (null == convertView) {
                convertView = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
                mViewHold = new ViewHold();
                mViewHold.mName = (TextView) convertView.findViewById(R.id.tv_media_name);
                mViewHold.mAddress = (TextView) convertView.findViewById(R.id.tv_media_address);
                convertView.setTag(mViewHold);
            } else {
                mViewHold = (ViewHold) convertView.getTag();
            }

            MediaBean mediaBean = playList.get(position);
            mViewHold.mName.setText(mediaBean.name);
            mViewHold.mAddress.setText(mediaBean.playUrl);
            return convertView;
        }

        private class ViewHold {
            public TextView mName;
            public TextView mAddress;
        }
    }

}
