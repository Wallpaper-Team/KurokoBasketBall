package com.tiendollar.edgechangewallpaper.ui.album;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.model.Image;
import com.tiendollar.edgechangewallpaper.model.ImageRepository;

import java.util.List;

public class AlbumViewModel extends AndroidViewModel {
    private ImageRepository imageRepository;
    private Context context;

    public AlbumViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public void setAlbum(Album album) {
        imageRepository = new ImageRepository(context, album);
    }

    LiveData<List<Image>> getAllImages() {
        return imageRepository.getMutableLiveData();
    }

    void refreshData(){
        imageRepository.refreshData();
    }




}
