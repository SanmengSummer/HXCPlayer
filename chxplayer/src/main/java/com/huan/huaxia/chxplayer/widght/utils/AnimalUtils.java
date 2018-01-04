package com.huan.huaxia.chxplayer.widght.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by huaxia on 2017/11/3.
 */

public class AnimalUtils {
    //列表缩放动画
    public static void setScaleAnimator(View view, float attr1, float attr2) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", attr1, attr2);
        scaleX.setInterpolator(new AccelerateInterpolator());
        scaleX.setDuration(200).start();
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", attr1, attr2);
        scaleY.setInterpolator(new AccelerateInterpolator());
        scaleY.setDuration(200).start();
    }

    //淡化退出或展示动画
    public static void setAlphaAnimator(final View view, final float attr1, final float attr2) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", attr1, attr2);
        alpha.setInterpolator(new AccelerateInterpolator());
        alpha.setDuration(200).start();
        view.setVisibility(View.VISIBLE);
        alpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (attr1 == 1)
                    view.setVisibility(View.GONE);
            }

        });
    }
}
