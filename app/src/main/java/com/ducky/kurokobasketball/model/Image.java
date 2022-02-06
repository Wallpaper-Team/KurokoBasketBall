package com.ducky.kurokobasketball.model;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducky.kurokobasketball.BR;
import com.ducky.kurokobasketball.R;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "image")
public class Image extends BaseObservable {
    @NonNull
    @PrimaryKey
    @SerializedName("mId")
    private String id;

    @SerializedName("albumId")
    private String albumId;

    @SerializedName("fullSize")
    private String fullSize;

    @SerializedName("mediumSize")
    private String mediumSize;

    @SerializedName("path")
    private String path;

    @Ignore
    @Bindable
    @SerializedName("downloadState")
    private int downloadState;

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
        notifyChange();
    }

    public int getDownloadState() {
        return downloadState;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setFullSize(String fullSize) {
        this.fullSize = fullSize;
    }

    public String getFullSize() {
        return fullSize;
    }

    public void setMediumSize(String mediumSize) {
        this.mediumSize = mediumSize;
    }

    public String getMediumSize() {
        return mediumSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", fullSize='" + fullSize + '\'' +
                ", mediumSize='" + mediumSize + '\'' +
                '}';
    }

    @BindingAdapter({"btnVisibility"})
    public static void btnVisibility(View view, Image image) {
        int visibility = View.GONE;
        if(!TextUtils.isEmpty(image.path)){
            image.downloadState = State.DOWNLOADED;
        }
        switch (view.getId()) {
            case R.id.btnDownload:
                visibility = image.downloadState == State.NORMAL ? View.VISIBLE : View.GONE;
                break;
            case R.id.btnProgress:
                visibility = image.downloadState == State.DOWNLOADING ? View.VISIBLE : View.GONE;
                break;
            case R.id.done:
                visibility = image.downloadState == State.DOWNLOADED ? View.VISIBLE : View.GONE;
                break;
        }
        view.setVisibility(visibility);
    }

    // important code for loading image here
    @BindingAdapter({"coverImage"})
    public static void loadImage(ImageView imageView, String imageURL) {
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(new RequestOptions()
                        .centerCrop())
                .load(imageURL)
                .placeholder(R.drawable.album_empty)
                .into(imageView);
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
