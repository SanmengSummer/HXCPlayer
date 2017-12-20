package com.huan.huaxia.chxplayer.widght;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huan.huaxia.chxplayer.ijkplayer.media.IjkVideoView;
import com.huan.huaxia.mylibrary.R;

import java.util.ArrayList;

import static com.huan.huaxia.chxplayer.ijkplayer.utils.AnimalUtils.setScaleAnimator;

/**
 * Created by huaxia on 2017/12/13.
 */

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.MyViewHolder> {
    private int index;
    private IjkVideoView mPlayerView;
    private ArrayList<MediaModel> mPlayList;
    private Context mContext;

    public PlayerListAdapter(Context mContext, IjkVideoView ijkVideoView, ArrayList<MediaModel> mPlayList, int index) {
        this.mContext = mContext;
        this.index = index;
        mPlayerView = ijkVideoView;
        this.mPlayList = mPlayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MediaModel mediaModel = mPlayList.get(position);
        holder.text.setText(TextUtils.isEmpty(mediaModel.name) ? "视频" + position : mediaModel.name);
        Glide.with(mContext).load(mediaModel.imagPath).placeholder(R.mipmap.icon_empty).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index != position) {
                    mPlayerView.setVideoPath(mediaModel.videoPath);
                    index = position;
                }
                mListener.setOnClickListener(view,position);
            }
        });
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setScaleAnimator(view, 1.0f, 1.1f);
                } else {
                    setScaleAnimator(view, 1.1f, 1.0f);
                }
                mListener.setOnFocusChangeListener(view, b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView text;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.iv);
            text = itemView.findViewById(R.id.tv);
        }
    }

    private OnPlayListItemListener mListener;

    /**
     * 设置屏幕滑动监听
     *
     * @param listener
     */
    public void setOnPlayListItemListener(OnPlayListItemListener listener) {
        mListener = listener;
    }

    public interface OnPlayListItemListener {
        void setOnFocusChangeListener(View view, boolean b);

        void setOnClickListener(View view, int index);

    }
}