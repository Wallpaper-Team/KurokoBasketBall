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
import com.ducky.kurokobasketball.database.DatabaseOpenHelper;

@Entity(tableName = DatabaseOpenHelper.AlbumEntry.TABLE_NAME)
public class Album implements Parcelable {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DatabaseOpenHelper.AlbumEntry._ID)
    @SerializedName("mId")
    private int mId;

    @ColumnInfo(name = DatabaseOpenHelper.AlbumEntry.BUCKET_ID)
    @SerializedName("mBucketId")
    private int mBucketId;

    @ColumnInfo(name = DatabaseOpenHelper.AlbumEntry.PATH)
    @SerializedName("thumbnail")
    private String mAlbumPath;

    @ColumnInfo(name = DatabaseOpenHelper.AlbumEntry.TITLE)
    @SerializedName("mTitle")
    private String mTitle;

    @ColumnInfo(name = DatabaseOpenHelper.AlbumEntry.COVER_PATH)
    @SerializedName("mCoverPath")
    private String mCoverPath;

    @ColumnInfo(name = DatabaseOpenHelper.AlbumEntry.ALBUM_ORDER)
    @SerializedName("mAlbumOrder")
    private int mAlbumOrder;

    @ColumnInfo(name = DatabaseOpenHelper.AlbumEntry.COUNT)
    @SerializedName("mCount")
    private String mCount;

    public Album(int bucketId, String albumPath, String title, String coverPath, int albumOrder, String count) {
        mBucketId = bucketId;
        mAlbumPath = albumPath;
        mTitle = title;
        mCoverPath = coverPath;
        mAlbumOrder = albumOrder;
        mCount = count;
    }

    public Album(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex(DatabaseOpenHelper.AlbumEntry._ID));
        mBucketId = cursor.getInt(cursor.getColumnIndex(DatabaseOpenHelper.AlbumEntry.BUCKET_ID));
        mAlbumPath = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.AlbumEntry.PATH));
        mTitle = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.AlbumEntry.TITLE));
        mCoverPath = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.AlbumEntry.COVER_PATH));
        mAlbumOrder = cursor.getInt(cursor.getColumnIndex(DatabaseOpenHelper.AlbumEntry.ALBUM_ORDER));
        mCount = cursor.getString(cursor.getColumnIndex(DatabaseOpenHelper.AlbumEntry.COUNT));
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    protected Album(Parcel in) {
        mId = in.readInt();
        mBucketId = in.readInt();
        mAlbumPath = in.readString();
        mTitle = in.readString();
        mCoverPath = in.readString();
        mAlbumOrder = in.readInt();
        mCount = in.readString();
    }

    public Album(int mAlbumId) {
        this.mId = mAlbumId;
    }


    public int getId() {
        return mId;
    }

    public int getBucketId() {
        return mBucketId;
    }

    public String getAlbumPath() {
        return mAlbumPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public int getAlbumOrder() {
        return mAlbumOrder;
    }

    public String getCount() {
        return mCount;
    }

    public void setId(int mId) {
        this.mId = mId;
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

    @NonNull
    @Override
    public String toString() {
        return "Album (mBucketId=" + mBucketId + ", mAlbumPath=" + mAlbumPath
                + ", mTitle=" + mTitle + ", mCoverPath=" + mCoverPath
                + ", mAlbumOrder=" + mAlbumOrder + ", mCount=" + mCount + ")"
                + ", ID = " + mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mBucketId);
        dest.writeString(mAlbumPath);
        dest.writeString(mTitle);
        dest.writeString(mCoverPath);
        dest.writeInt(mAlbumOrder);
        dest.writeString(mCount);
    }
}
