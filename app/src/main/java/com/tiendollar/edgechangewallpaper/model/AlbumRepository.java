package com.tiendollar.edgechangewallpaper.model;

import android.content.Context;
import android.os.Environment;

import androidx.lifecycle.MutableLiveData;

import com.tiendollar.edgechangewallpaper.database.AlbumsDatabase;
import com.tiendollar.edgechangewallpaper.database.ImagesDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AlbumRepository {
    private static final String TAG = "AlbumRepository";
    private ArrayList<Album> mAlbums;
    private MutableLiveData<List<Album>> mutableLiveData = new MutableLiveData<>();
    private AlbumsDatabase mAlbumsDatabase;
    private ImagesDatabase mImagesDatabase;


    public AlbumRepository(Context context) {
        mAlbumsDatabase = AlbumsDatabase.getInMemoryDatabase(context);
        mImagesDatabase = ImagesDatabase.getInMemoryDatabase(context);
        mAlbums = loadAllImages();
        getDefaultValueAlbums();
        mutableLiveData.setValue(mAlbums);
    }

    private void getDefaultValueAlbums() {
        for (Album album : mAlbums) {
            album.setCount(mAlbumsDatabase.albumDao().findById(album.getId()).getCount());
            Image image = mImagesDatabase.imageDAO().getImageByOrderInAlbum(album.getId());
            File file = new File(album.getCoverPath());
            if (!file.exists()) {
                if(image!=null){
                    album.setCoverPath(image.getPath());
                }else{
                    album.setCoverPath("");
                }
                mAlbumsDatabase.albumDao().update(album);
            }
        }
    }

    private ArrayList<Album> loadAllImages() {
        ArrayList<Album> albums = (ArrayList<Album>) mAlbumsDatabase.albumDao().findAll();
        return albums;
    }

    public MutableLiveData<List<Album>> getMutableLiveData() {
        return mutableLiveData;
    }

    public void updateData() {
        getDefaultValueAlbums();
    }
}