package com.huan.edu.tvplayer.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huan.edu.tvplayer.R;

public class SeekBarHint extends SeekBar implements SeekBar.OnSeekBarChangeListener {

    private int mPopupWidth;
    private int mPopupStyle;
    public static final int POPUP_FIXED = 1;
    public static final int POPUP_FOLLOW = 0;

    private PopupWindow mPopup;
    private TextView mPopupTextView;
    private int mYLocationOffset;

    private OnSeekBarChangeListener mInternalListener;
    private OnSeekBarChangeListener mExternalListener;

    private OnSeekBarHintProgressChangeListener mProgressChangeListener;

    public interface OnSeekBarHintProgressChangeListener {
        String onHintTextChanged(SeekBarHint seekBarHint, int progress);
    }

    public SeekBarHint (Context context) {
        super(context);
        init(context, null);
    }

    public SeekBarHint (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public SeekBarHint (Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){

        setOnSeekBarChangeListener(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarHint);

        mPopupWidth = (int) a.getDimension(R.styleable.SeekBarHint_popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        mYLocationOffset = (int) a.getDimension(R.styleable.SeekBarHint_yOffset, 0);
        mPopupStyle = a.getInt(R.styleable.SeekBarHint_popupStyle, POPUP_FOLLOW);

        a.recycle();
        initHintPopup();
    }

    public void setPopupStyle(int style){
        mPopupStyle = style;
    }

    public int getPopupStyle(){
        return mPopupStyle;
    }

    private void initHintPopup(){
        String popupText = null;

        if (mProgressChangeListener!=null){
            popupText = mProgressChangeListener.onHintTextChanged(this, getProgress());
        }

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View undoView = inflater.inflate(R.layout.edu_tvplayer_seekbar_hint_popup, null);
//        mPopupTextView = (TextView)undoView.findViewById(R.id.text);
//        mPopupTextView.setText(popupText!=null? popupText : String.valueOf(getProgress()));
        mPopupTextView = new TextView(getContext());
        mPopupTextView.setBackgroundResource(R.drawable.edu_tvplayer_seekbar_hint_popup_bg);
        mPopupTextView.setText("测试测试");

        mPopup = new PopupWindow(undoView, mPopupWidth, ViewGroup.LayoutParams.WRAP_CONTENT, false);

//        mPopup.setAnimationStyle(R.style.fade_animation);

//        attachSeekBar();
    }

    private void attachSeekBar() {
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                showPopup();
            }
        };
        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override public void onViewAttachedToWindow(View v) {
                getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
            }

            @Override public void onViewDetachedFromWindow(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                }
                hidePopup();
            }
        });
    }
    
    public void showPopup(){
        if(mPopupStyle==POPUP_FOLLOW){
            mPopup.showAtLocation(this, Gravity.LEFT | Gravity.BOTTOM, (int) (this.getX()+(int) getXPosition(this)), (int) (this.getY()+mYLocationOffset+this.getHeight()));
        }
        if (mPopupStyle==POPUP_FIXED){
            mPopup.showAtLocation(this, Gravity.CENTER | Gravity.BOTTOM, 0, (int) (this.getY()+mYLocationOffset+this.getHeight()));
        }
    }

    public void hidePopup(){
        if(mPopup.isShowing()) {
            mPopup.dismiss();
        }
    }

    public void setHintView(View view){
        //TODO
        //initHintPopup();
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        if (mInternalListener==null){
            mInternalListener = l;
            super.setOnSeekBarChangeListener(l);
        }else {
            mExternalListener = l;
        }
    }

    public void setOnProgressChangeListener(OnSeekBarHintProgressChangeListener l){
        mProgressChangeListener = l;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        String popupText = null;
        if (mProgressChangeListener!=null){
            popupText = mProgressChangeListener.onHintTextChanged(this, getProgress());
        }

        if(mExternalListener !=null){
            mExternalListener.onProgressChanged(seekBar, progress, b);
        }


        mPopupTextView.setText(popupText!=null? popupText : String.valueOf(progress));

        if(mPopupStyle==POPUP_FOLLOW){
            mPopup.update((int) (this.getX()+(int) getXPosition(seekBar)), (int) (this.getY()+mYLocationOffset+this.getHeight()), -1, -1);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mExternalListener !=null){
            mExternalListener.onStartTrackingTouch(seekBar);
        }

//        showPopup();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mExternalListener !=null){
            mExternalListener.onStopTrackingTouch(seekBar);
        }

//        hidePopup();
    }


    private float getXPosition(SeekBar seekBar){
        float val = (((float)seekBar.getProgress() * (float)(seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax());
        float offset = seekBar.getThumbOffset();

        int textWidth = mPopupWidth;
        float textCenter = (textWidth/2.0f);

        float newX = val+offset - textCenter;

        return newX;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        mPopupTextView.draw(canvas);
        super.onDraw(canvas);
    }
}
