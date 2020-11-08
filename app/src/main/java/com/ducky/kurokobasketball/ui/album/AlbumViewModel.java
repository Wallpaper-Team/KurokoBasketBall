package com.ducky.kurokobasketball.ui.album;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.model.ImageRepository;

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
