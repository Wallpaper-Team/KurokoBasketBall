package com.ducky.kurokobasketball.ui.wallpaper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ducky.kurokobasketball.database.ImageDAO;
import com.ducky.kurokobasketball.model.Image;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WallpaperViewModel extends ViewModel {

    private final ImageDAO imageDAO;

    @Inject
    public WallpaperViewModel(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    public LiveData<List<Image>> getImages(String albumName) {
        return imageDAO.getImageList(albumName);
    }
}
