package com.huan.huaxia.chxplayer.widght;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 2017/12/13.
 */

public class MediaModel implements Parcelable {
    public int id;
    public String name;
    public String videoPath;
    public String imagPath;

    public MediaModel() {
    }

    protected MediaModel(Parcel in) {
        id = in.readInt();
        name = in.readString();
        videoPath = in.readString();
        imagPath = in.readString();
    }

    public static final Creator<MediaModel> CREATOR = new Creator<MediaModel>() {
        @Override
        public MediaModel createFromParcel(Parcel in) {
            return new MediaModel(in);
        }

        @Override
        public MediaModel[] newArray(int size) {
            return new MediaModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getImagPath() {
        return imagPath;
    }

    public void setImagPath(String imagPath) {
        this.imagPath = imagPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(videoPath);
        parcel.writeString(imagPath);
    }

    @Override
    public String toString() {
        return "MediaModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", videoPath='" + videoPath + '\'' +
                ", imagPath='" + imagPath + '\'' +
                '}';
    }
}
