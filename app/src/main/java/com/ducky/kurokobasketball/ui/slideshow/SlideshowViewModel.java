package com.ducky.kurokobasketball.ui.slideshow;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.model.ImageRepository;

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