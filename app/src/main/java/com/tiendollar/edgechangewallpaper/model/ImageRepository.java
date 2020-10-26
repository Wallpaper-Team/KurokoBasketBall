package com.tiendollar.edgechangewallpaper.model;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tiendollar.edgechangewallpaper.database.DataLoaderFromFireBase;
import com.tiendollar.edgechangewallpaper.database.ImagesDatabase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ImageRepository {
    private ImagesDatabase imagesDatabase;
    private MutableLiveData<List<Image>> mutableLiveData = new MutableLiveData<>();
    private Album album;
    private Context mContext;
    private DataLoaderFromFireBase.LoaderDataListener mLoaderDataListener = this::refreshData;

    public ImageRepository(Context mContext) {
        this.mContext = mContext;
        imagesDatabase = ImagesDatabase.getInMemoryDatabase(mContext);
    }

    public ImageRepository(Context context, Album album) {
        mContext = context;
        imagesDatabase = ImagesDatabase.getInMemoryDatabase(context);
        this.album = album;
        loadAllImages();
    }


    private void loadAllImages() {
        DataLoaderFromFireBase loader = new DataLoaderFromFireBase(new WeakReference<>(mContext));
        if (album.getCount().equals("0")) {
            StorageReference albumStorageRef = FirebaseStorage.getInstance().getReference().child(album.getTitle());
            DataLoaderFromFireBase.loadImageFromFirebase(album.getId(), albumStorageRef, new WeakReference<>(mContext));
        }

        loader.setIsLoadedData(mLoaderDataListener);
        refreshData();
    }

    public void refreshData() {
        ArrayList<Image> mImages = (ArrayList<Image>) imagesDatabase.imageDAO().getImageList(album.getId());
        mutableLiveData.setValue(mImages);
    }

    public MutableLiveData<List<Image>> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<List<Image>> getFavoriteData() {
        ArrayList<Image> mImages = (ArrayList<Image>) imagesDatabase.imageDAO().getImageListFavorite();
        mutableLiveData.setValue(mImages);
        return mutableLiveData;
    }
}
