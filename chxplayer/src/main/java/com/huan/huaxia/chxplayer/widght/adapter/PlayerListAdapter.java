package com.huan.huaxia.chxplayer.widght.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huan.huaxia.chxplayer.R;
import com.huan.huaxia.chxplayer.player.media.BasePlayer;
import com.huan.huaxia.chxplayer.widght.hxplayer.SimplePlayer;
import com.huan.huaxia.chxplayer.widght.model.MediaModel;

import java.util.ArrayList;

import static com.huan.huaxia.chxplayer.widght.utils.AnimalUtils.setScaleAnimator;

/**
 * Created by huaxia on 2017/12/13.
 */

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.MyViewHolder> {
    private int index;
    private SimplePlayer mPlayerView;
    private ArrayList<MediaModel> mPlayList;
    private Context mContext;

    public PlayerListAdapter(Context mContext, SimplePlayer basePlayer, ArrayList<MediaModel> mPlayList, int index) {
        this.mContext = mContext;
        this.index = index;
        mPlayerView = basePlayer;
        this.mPlayList = mPlayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (null != mPlayList && 0 != mPlayList.size()) {
            final MediaModel mediaModel = mPlayList.get(position);
            holder.text.setText(TextUtils.isEmpty(mediaModel.name) ? "视频" + position : mediaModel.name);
            Glide.with(mContext).load(mediaModel.imagPath).placeholder(R.mipmap.icon_empty).into(holder.image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index != position) {
                        mPlayerView.setVideoPath(mediaModel.videoPath);
                        mPlayerView.start();
                        index = position;
                    }
                    if (null != mListener)
                        mListener.setOnClickListener(view, position);
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
                    if (null != mListener)
                        mListener.setOnFocusChangeListener(view, b);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (null == mPlayList) ? 0 : mPlayList.size();
    }

    public void setData(SimplePlayer basePlayer, ArrayList<MediaModel> mPlayList, int index) {
        this.index = index;
        mPlayerView = basePlayer;
        this.mPlayList = mPlayList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView text;

        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv);
            text = (TextView) itemView.findViewById(R.id.tv);
        }
    }

    private OnPlayListItemListener mListener;

    /**
     * 设置PlayListItem监听
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