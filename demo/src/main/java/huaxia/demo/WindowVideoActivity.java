package huaxia.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.huan.edu.tvplayer.EduPlayerFragment;
import com.huan.edu.tvplayer.PlayerSettings;

/**
 * Created by owen on 16/8/5.
 */
public class WindowVideoActivity extends FragmentActivity {

    private EduPlayerFragment fragment;
    private View mContentView;
    private boolean isFullScreen = false;
    private int windowWidth;
    private int windowHeight;
    private int padding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_video);
        
        mContentView = findViewById(R.id.fragment_content);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFullScreen && null != mContentView) {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    mContentView.setLayoutParams(lp);
                    mContentView.setPadding(0, 0, 0, 0);
                    isFullScreen = true;
                    if(null != fragment) {
                        fragment.setControllerEnabled(isFullScreen);
                    }
                }
                
            }
        });

        fragment = EduPlayerFragment.create(getApplicationContext(), false);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_content, fragment)
                .commit();

        // 初始化焦点
        mContentView.post(new Runnable() {
            @Override
            public void run() {
                mContentView.requestFocus();
                windowWidth = mContentView.getWidth();
                windowHeight = mContentView.getHeight();
                padding = mContentView.getPaddingTop();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isFullScreen && null != mContentView) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(windowWidth, windowHeight);
            lp.gravity = Gravity.CENTER;
            mContentView.setPadding(padding, padding, padding, padding);
            mContentView.setLayoutParams(lp);
            isFullScreen = false;
            if (null != fragment) {
                fragment.setControllerEnabled(isFullScreen);
            }
            return;
        }
        // 回传播放数据
        if(null != fragment) {
            setResult(PlayerSettings.RESULT_CODE, fragment.getResultIntent());
        }
        super.onBackPressed();
    }

}
