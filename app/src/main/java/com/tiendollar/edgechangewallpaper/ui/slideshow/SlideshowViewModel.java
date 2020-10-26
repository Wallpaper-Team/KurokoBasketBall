package com.tiendollar.edgechangewallpaper.ui.slideshow;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.model.Image;
import com.tiendollar.edgechangewallpaper.model.ImageRepository;

import java.util.List;

public class SlideshowViewModel extends AndroidViewModel {

    private ImageRepository imageRepository;

    public SlideshowViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application.getApplicationContext());
    }

    LiveData<List<Image>> getAllImages() {
        return imageRepository.getFavoriteData();
    }

}