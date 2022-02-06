package com.ducky.kurokobasketball.ui.album;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ducky.kurokobasketball.database.ImageDAO;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.model.State;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AlbumViewModel extends ViewModel {

    private final ImageDAO imageDAO;
    private final DatabaseReference mDatabase;

    @Inject
    public AlbumViewModel(ImageDAO imageDAO) {
        this.imageDAO = imageDAO;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public List<Image> getImageList(String albumName) {
        fetchData(albumName);
        return imageDAO.getImageList(albumName);
    }

    public void fetchData(String albumName) {
        Image image = imageDAO.findLatestImageByID(albumName);
        String loadKey = "";
        if (image != null) {
            loadKey = image.getId().split("_")[0];
        }
        mDatabase.child("Images/KurokoBasketball/" + albumName)
                .orderByKey()
                .startAfter(loadKey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Image> images = new ArrayList<>();
                        for (DataSnapshot child : task.getResult().getChildren()) {
                            Image img = new Image();
                            img.setId(child.getKey() + "_" + albumName);
                            img.setAlbumId(albumName);
                            img.setDownloadState(State.NORMAL);
                            img.setFullSize(child.child("fullSize").getValue(String.class));
                            img.setMediumSize(child.child("mediumSize").getValue(String.class));
                            images.add(img);
                        }
                        imageDAO.insert(images);
                    }
                });
    }
}
