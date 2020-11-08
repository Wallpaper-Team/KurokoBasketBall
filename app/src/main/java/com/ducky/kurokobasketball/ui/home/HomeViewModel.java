package com.ducky.kurokobasketball.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.model.AlbumRepository;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private AlbumRepository albumRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        albumRepository = new AlbumRepository(application.getBaseContext());
    }

    void updateData() {
        albumRepository.updateData();
    }

    LiveData<List<Album>> getAllAlbums() {
        return albumRepository.getMutableLiveData();
    }
}