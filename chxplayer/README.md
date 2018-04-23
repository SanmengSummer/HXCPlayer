# HXPlayer 使用
1、主要有五个Player可供使用：
    1.0 BasePlayer：View控件，类似mediaPlayer 是ijkPlayer最基础的形态 不可用
    1.1 SimplePlayer：View控件，类似mediaPlayer 是HXPlayer最基础的形态 可用
        MediaModel  媒体资源（包括name videoPath imagPath）
        setVideoList(ArrayList<MediaModel> playList) 设置媒体资源
        setVideoPath(String path) 只设置播放地址
        playNext() 播放下一首
        playPre() 播放上一首
        playPause() 播放暂停
        pause() 暂停
        start() 播放
        setShowPoint() //设置是否展示播放错误Point
        setShowDialog() //设置是否展示播放错误dialog
        setShowDialogOrPoint() 设置是否展示dialog或Point之一
        setLoadingNone()设置是否展示loading
        setImageNone()//设置是否展示playerImage(播放前图片)
    1.2 PhonePlayer：View控件，phone版player 继承TransitPlayer 加上controller的壳 
        TransitPlayer中处理一些phonePlayer中混乱的逻辑；
        showZoom()全屏 
        setOnVideoScreenMoveListener()屏幕滑动监听
    1.3 TvPlayer；View控件，Tv版player 以焦点为主 继承SimplePlayer 加上controller的壳
        showZoom()全屏 
        setOnVideoScreenMoveListener()屏幕滑动监听
    1.4 FullScreenPlayer；全屏player
    1.5 TvFullScreenPlayer；全屏player