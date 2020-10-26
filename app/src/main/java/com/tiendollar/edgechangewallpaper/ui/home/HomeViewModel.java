package com.tiendollar.edgechangewallpaper.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.model.AlbumRepository;

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