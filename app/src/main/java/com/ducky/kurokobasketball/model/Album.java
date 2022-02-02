package com.ducky.kurokobasketball.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.annotations.SerializedName;
import com.ducky.kurokobasketball.R;

@Entity(tableName = "album")
public class Album {

    @NonNull
    @PrimaryKey
    @SerializedName("mTitle")
    private String mTitle;

    @SerializedName("mCoverPath")
    private String mCoverPath;

    @SerializedName("mCount")
    private String mCount;

    public String getTitle() {
        return mTitle;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public String getCount() {
        return mCount;
    }

    public void setCoverPath(String coverPath) {
        mCoverPath = coverPath;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setCount(String mCount) {
        this.mCount = mCount;
    }

    // important code for loading image here
    @BindingAdapter({"coverAlbum"})
    public static void loadImage(ImageView imageView, String imageURL) {
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(new RequestOptions()
                        .centerInside())
                .load(imageURL)
                .placeholder(R.drawable.album_empty)
                .into(imageView);
    }

    @Override
    public String toString() {
        return "Album{" +
                "mTitle='" + mTitle + '\'' +
                ", mCoverPath='" + mCoverPath + '\'' +
                ", mCount='" + mCount + '\'' +
                '}';
    }
}
