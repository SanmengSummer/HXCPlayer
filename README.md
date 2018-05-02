#HXPlayer 使用说明
#0、使用初始化：
0.1 添加依赖    compile 'com.huan.huaxia:chxplayer:1.2.1'
0.2 具体Demo    https://github.com/SanmengSummer/HXCPlayer
0.3 使用view    可使用最基础的SimplePlayer
                亦可使用phone版的PhonePlayer的View控件和FullScreenPlayer的Activity界面
                亦可使用tv版的TvPlayer的View控件和TvFullScreenPlayer的Activity界面
                具体使用可参照以下使用介绍：
    
#1、主要有五个Player可供使用：
1.1 SimplePlayer：View控件，类似mediaPlayer 是HXPlayer最基础的形态 可用
    #
    在layout的xml文件相应位置
    添加View控件：com.huan.huaxia.chxplayer.widght.hxplayer.SimplePlayer
    #
    1)MediaModel  媒体资源（包括name（视频名） videoPath（视频地址） imagPath（视频播放前图片））
    2)setVideoList(ArrayList<MediaModel> playList) 设置媒体资源
    3)setVideoPath(String path) 只设置播放地址（name默认noName,imagPath默认为空,图片为默认的本地图片）
    4)playNext() 播放下一个
    5)playPre() 播放上一个
    6)playPause() 播放暂停
    7)pause() 暂停
    8)start() 播放
    9)showZoom(boolean isChangePlayerSize)手动全屏（大小屏切换）（false则使用FullScreenPlayer,true是改变PhonePlayer尺寸至全屏）
      showZoom()手动全屏（大小屏切换）默认false
    10)setShowPoint() 设置是否展示播放错误Point(点击可重新加载播放的小图标)
    11)setShowDialog() 设置是否展示播放错误提示dialog
    12)setShowDialogOrPoint() 设置是否展示dialog或Point之一（false为Point，true为dialog）
    13)setLoadingNone() 设置是否展示loading
    14)setImageNone() 设置是否展示playerImage(播放前图片)
    15)setIsChangePlayerSize() 设置controller全屏方式 和 showZoom()不同；
    （false为跳转到FullScreenPlayer或TVScreenPlayer，true则是改变player尺寸为全屏）
        （两者利弊：前者直接跳转全新的Player，不会产生界面混乱，使用广播进行参数的互传；
                   后者是直接在原player基础上改变尺寸，但无法会在ScrollerView、ListView、RecyclerView
                   等View中产生界面卡屏混乱，以及其他bug等现象。
                   该bug有时间再改进，暂时使用前者即可）
#*效果：是最基本的player,可用方法暂停播放，播放下一个视频和上一个视频，可主动设置是否提示或选择播放错误提示（dialog和小图标两种）

1.2 PhonePlayer：View控件，phone版player 继承TransitPlayer继承SimplePlayer 加上controller的壳 
    #
    在layout的xml文件相应位置
    添加View控件：com.huan.huaxia.chxplayer.widght.hxplayer.PhonePlayer
    #
    2)TransitPlayer中处理一些phonePlayer中混乱的逻辑；
    void moveX(float percent);垂直方向，向上为正，向下为负
    void moveLeftY(float percent);水平向左，>0
    void moveRightY(float percent);水平向右,>0
#效果：继承SimplePlayer，可设置全屏和大小屏切换，可监听屏幕滑动事件，可双击暂停或播放

1.3 TvPlayer；View控件，Tv版player 以焦点为主 继承SimplePlayer
    #
    在layout的xml文件相应位置
    添加View控件：com.huan.huaxia.chxplayer.widght.hxplayer.PhonePlayer
    #
    1)setOnVideoScreenMoveListener()屏幕滑动监听
    void moveX(float percent);垂直方向，向上为正，向下为负
    void moveLeftY(float percent);水平向左，>0
    void moveRightY(float percent);水平向右,>0
#效果：继承SimplePlayer，可设置全屏和大小屏切换，可监听屏幕滑动事件，暂未添加其他方法

1.4 FullScreenPlayer；phone版全屏player，Activity
#效果：使用了PhonePlayer

1.5 TvFullScreenPlayer；tv版全屏player，Activity
#效果：使用了tvPlayer 着重controller的焦点的控制


#2、跳转全屏player的方法：PlayerUtils.skipFullScreenPlayer(...):
2.1 skipFullScreenPlayer(Activity activity, String path)
    直接打开并播放path地址的视频（本地和网络），phone版
    
2.2 skipFullScreenPlayer(Activity activity, String path, boolean isTv)
    直接打开并播放path地址的视频（本地和网络），tv版（isTv为true时）
    
2.3 skipFullScreenPlayer(Activity activity, ArrayList<MediaModel> mPlayList)
    直接打开并传入媒体资源集合mPlayList，phone版
    
2.4 skipFullScreenPlayer(Activity activity, ArrayList<MediaModel> mPlayList, boolean isTv)
    直接打开并传入媒体资源集合mPlayList，tv版（isTv为true时）
