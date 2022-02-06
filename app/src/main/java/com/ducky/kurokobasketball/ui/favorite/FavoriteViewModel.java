package com.ducky.kurokobasketball.ui.favorite;

import androidx.lifecycle.ViewModel;

import com.ducky.kurokobasketball.database.ImageDAO;
import com.ducky.kurokobasketball.model.Image;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteViewModel extends ViewModel {

    private final ImageDAO imageDAO;

    @Inject
    public FavoriteViewModel(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
    }

    public List<Image> getImageList() {
        return imageDAO.getImageListFavorite();
    }
}
