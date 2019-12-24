package com.huan.edu.tvplayer.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.huan.edu.tvplayer.PlayerSettings;
import com.huan.edu.tvplayer.bean.MediaBean;
import com.huan.edu.tvplayer.http.request.HttpRequest;
import com.huan.edu.tvplayer.http.request.callback.HttpCallBack;
import com.huan.edu.tvplayer.ijk.media.IjkVideoView;
import com.huan.edu.tvplayer.utils.EPLog;
import com.huan.edu.tvplayer.utils.EPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkLibLoader;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by owen on 16/5/5.
 */
public class EduIjkVideoView extends IjkVideoView implements EduIVideoView {
    private static final String TAG = EduIjkVideoView.class.getSimpleName() + " %s";

    private EduIMediaController mEduIMediaController;

    private OnEduMediaListener mOnEduMediaListener;

    private MediaBean mMediaBean;

    private boolean mIsPlayingAd = false;

    /*---------
     * Listeners
     */
    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            if (null != mEduIMediaController && mIsPlayingAd) {
                mEduIMediaController.showAdTime();
            }
            if(null != mOnEduMediaListener) {
                mOnEduMediaListener.onPrepared(EduIjkVideoView.this);
            }
        }
    };
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            if(mMediaBean.isPlayAd && mIsPlayingAd) {
                mIsPlayingAd = false;
                if(null != mEduIMediaController) {
                    mEduIMediaController.hide();
                }
                playVideo(mMediaBean);
                return;
            }
            if(null != mOnEduMediaListener) {
                mOnEduMediaListener.onCompletion(EduIjkVideoView.this);
            }
        }
    };
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if(null != mOnEduMediaListener) {
                mOnEduMediaListener.onBufferingUpdate(EduIjkVideoView.this, percent);
            }
        }
    };
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if(mMediaBean.isPlayAd && mIsPlayingAd) {
                mIsPlayingAd = false;
                if(null != mEduIMediaController) {
                    mEduIMediaController.hideAdTime();
                }
                playVideo(mMediaBean);
                return true;
            }
            if(null != mOnEduMediaListener) {
                mOnEduMediaListener.onError(EduIjkVideoView.this, what, extra);
            }
            return true;
        }
    };
    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            if(null != mOnEduMediaListener) {
                mOnEduMediaListener.onInfo(EduIjkVideoView.this, what, extra);
            }
            return false;
        }
    };
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            if(null != mOnEduMediaListener) {
                mOnEduMediaListener.onSeekComplete(EduIjkVideoView.this);
            }
        }
    };


    public EduIjkVideoView(Context context) {
        super(context);
        init();
    }

    public EduIjkVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EduIjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public EduIjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setFocusable(false);
        setFocusableInTouchMode(false);
        initVideoListener();
    }

    @Override
    public void initVideoListener() {
        setOnPreparedListener(mOnPreparedListener);
        setOnCompletionListener(mOnCompletionListener);
        setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        setOnSeekCompleteListener(mOnSeekCompleteListener);
        setOnErrorListener(mOnErrorListener);
        setOnInfoListener(mOnInfoListener);
    }

    @Override
    public void setEduMediaController(EduIMediaController controller) {
        if(null == controller) return;
        if(null != mEduIMediaController) {
            mEduIMediaController.hide();
        }
        mEduIMediaController = controller;
        attachEduMediaController();
    }

    @Override
    public boolean isPlayingAd() {
        return mIsPlayingAd;
    }

    @Override
    public void initVideoLib() {
        // init player
        IjkMediaPlayer.loadLibrariesOnce(new IjkLibLoader() {
            @Override
            public void loadLibrary(String libName) {
                try {
                    System.loadLibrary("lexue"+libName);
                } catch (UnsatisfiedLinkError e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });
        IjkMediaPlayer.native_profileBegin("liblexueijkplayer.so");
    }

    @Override
    public void releaseBack() {
        stopPlayback();
        release(true);
        stopBackgroundPlay();
        if(!PlayerSettings.getInstance(getContext()).isAndroidPlayer()) {
            IjkMediaPlayer.native_profileEnd();
        }
    }

    @Override
    public boolean isBackgroundPlayEnabled() {
        return super.isBackgroundPlayEnabled();
    }

    @Override
    public void enterBackground() {
        super.enterBackground();
    }

    private void attachEduMediaController() {
        if (mEduIMediaController != null) {
            mEduIMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
            mEduIMediaController.setAnchorView(anchorView);
        }
    }

    @Override
    public void setOnEduMediaListener(OnEduMediaListener onEduMediaListener) {
        mOnEduMediaListener = onEduMediaListener;
    }

    @Override
    public void playVideoWithBean(MediaBean bean) {
        mMediaBean = bean;
        mIsPlayingAd = false;
        if(null == bean) {
            setVideoPath("");
            return;
        }
        if(TextUtils.equals("0", bean.isBuy)) {
            if(bean.isPlayAd) {
                playVideo(bean);//广告功能暂不实现
            } else {
                playVideo(bean);
            }
        } else {
            if (null != mOnEduMediaListener) {
                mOnEduMediaListener.onNoPermission(this, bean);
            }
        }
    }

    private void playVideo(final MediaBean bean) {
        if (null != mOnEduMediaListener) {
            mOnEduMediaListener.onInfo(this, EduIVideoView.MEDIA_INFO_BUFFERING_START, 0);
        }
        requestPlayAddress(bean, new HttpCallBack() {
            @Override
            public void success(String info) {
                setVideoPath(info);
                // 续播功能
//                int mCurrentPosition = EPUtils.getPreferences(getContext(), bean.id, -100);
//                if(mCurrentPosition > 0) {
//                    EPLog.i(TAG, "mCurrentPosition="+mCurrentPosition);
//                    seekTo(mCurrentPosition * 1000);
//                }
                start();
            }

            @Override
            public void failure(int status, String info) {
                Log.i("ygx","failure.....");
                if (null != mOnEduMediaListener) {
                    mOnEduMediaListener.onError(EduIjkVideoView.this, EduIVideoView.MEDIA_ERROR_UNKNOWN, 0);
                }
            }
        });
    }

    private void requestPlayAddress(MediaBean mediaBean, final HttpCallBack callBack) {
        if (PlayerSettings.getInstance(getContext()).isRequestPlayAddress()) {
            Map<String, String> params = new HashMap<>();
            params.put("id", mediaBean.id);
            params.put("huanId", PlayerSettings.getInstance(getContext()).getHuanId());
            HttpRequest.getInstance().post(
                    PlayerSettings.getInstance(getContext()).getRequestPlayAddressUrl(),
                    params, new HttpCallBack() {
                @Override
                public void success(String info) {
                    Log.i(TAG, "requestPlayAddress...result=" + info);
                    if (!TextUtils.isEmpty(info)) {
                        try {
                            JSONObject jsonObject = new JSONObject(info);
                            if ("200".equals(jsonObject.getString("code"))) {
                                jsonObject = jsonObject.getJSONObject("address");
                                if (null != jsonObject) {
                                    String addressHD = jsonObject.has("HD_main") ? jsonObject.getString("HD_main") : "";
                                    String addressSD = jsonObject.has("SD_main") ? jsonObject.getString("SD_main") : "";
                                    if (EPUtils.getScreenWidth(getContext()) > 1800) {
                                        callBack.success(TextUtils.isEmpty(addressHD) ? addressSD : addressHD);
                                    } else {
                                        callBack.success(TextUtils.isEmpty(addressSD) ? addressHD : addressSD);
                                    }
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    failure(0, "服务器返回数据异常");
                }

                @Override
                public void failure(int status, String info) {
                    EPLog.i(TAG, "requestPlayAddress...status=" + status + " ,info=" + info);
                    if (null != callBack)
                        callBack.failure(status, info);
                }
            });
        } else {
            if (null != callBack)
                callBack.success(mediaBean.playUrl);
        }
    }

    @Override
    public void seekTo(int msec) {
        if(null != mOnEduMediaListener) {
            mOnEduMediaListener.onSeekStart(EduIjkVideoView.this);
        }
        super.seekTo(msec);
    }

    @Override
    public void start() {
        if(null != mOnEduMediaListener) {
            mOnEduMediaListener.onInfo(EduIjkVideoView.this, MEDIA_INFO_VIDEO_RESUME, 0);
        }
        super.start();
    }

    @Override
    public void pause() {
        if(null != mOnEduMediaListener) {
            mOnEduMediaListener.onInfo(EduIjkVideoView.this, MEDIA_INFO_VIDEO_PAUSE, 0);
        }
        super.pause();
    }
}
