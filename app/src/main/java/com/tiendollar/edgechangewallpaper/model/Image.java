package com.tiendollar.edgechangewallpaper.model;

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
import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.database.DatabaseOpenHelper;

import java.io.Serializable;

@Entity(tableName = DatabaseOpenHelper.ImageEntry.TABLE_NAME)
public class Image implements Parcelable, Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseOpenHelper.ImageEntry._ID)
    @SerializedName("mId")
    private int id;

    @ColumnInfo(name = DatabaseOpenHelper.ImageEntry.ALBUM_ID)
    @SerializedName("albumId")
    private int albumId;

    @ColumnInfo(name = DatabaseOpenHelper.ImageEntry.PATH)
    @SerializedName("path")
    private String path;

    @ColumnInfo(name = DatabaseOpenHelper.ImageEntry.IMAGE_ORDER)
    @SerializedName("imageOrder")
    private int imageOrder;

    @ColumnInfo(name = DatabaseOpenHelper.ImageEntry.IMAGE_FAVORITED)
    @SerializedName("checked")
    private boolean checked;

    public Image(int albumId, String path, int imageOrder) {
        this.albumId = albumId;
        this.path = path;
        this.imageOrder = imageOrder;
    }

    public Image(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(DatabaseOpenHelper.ImageEntry._ID));
        albumId = cursor.getInt(cursor.getColumnIndex(DatabaseOpenHelper.ImageEntry.ALBUM_ID));
        path = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.ImageEntry.PATH));
        imageOrder = cursor.getInt(cursor.getColumnIndex(DatabaseOpenHelper.ImageEntry.IMAGE_ORDER));
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    protected Image(Parcel in) {
        id = in.readInt();
        albumId = in.readInt();
        path = in.readString();
        imageOrder = in.readInt();
    }

    public int getId() {
        return id;
    }

    public int getAlbumId() {
        return albumId;
    }

    public String getPath() {
        return path;
    }

    public int getImageOrder() {
        return imageOrder;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setImageOrder(int imageOrder) {
        this.imageOrder = imageOrder;
    }

    @Override
    public String toString() {
        return "Image (albumId=" + albumId + ", path=" + path + ", imageOrder=" + imageOrder + ")";
    }

    // important code for loading image here
    @BindingAdapter({ "coverImage" })
    public static void loadImage(ImageView imageView, String imageURL) {
        Glide.with(imageView.getContext())
                .setDefaultRequestOptions(new RequestOptions()
                        .centerCrop())
                .load(imageURL)
                .placeholder(R.drawable.album_empty)
                .into(imageView);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(albumId);
        dest.writeString(path);
        dest.writeInt(imageOrder);
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
