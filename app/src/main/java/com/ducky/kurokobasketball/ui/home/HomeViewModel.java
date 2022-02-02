package com.ducky.kurokobasketball.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.ducky.kurokobasketball.database.AlbumDAO;
import com.ducky.kurokobasketball.model.Album;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final AlbumDAO albumDAO;
    private final DatabaseReference mDatabase;

    @Inject
    public HomeViewModel(AlbumDAO albumDAO) {
        this.albumDAO = albumDAO;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (albumDAO.countItem() == 0) {
            refresh();
        }
    }

    public LiveData<List<Album>> getAllAlbums() {
        return albumDAO.findAll();
    }

    public void refresh() {
        Log.d("duc.dv1", "refresh: ");
        mDatabase.child("Topics/KurokoBasketball/Albums").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Album> albums = new ArrayList<>();
                for (DataSnapshot child : task.getResult().getChildren()) {
                    Log.d("duc.dv1", "refresh: " + child);
                    Album album = new Album();
                    album.setTitle(child.getKey());
                    album.setCoverPath(child.child("imageUrl").getValue(String.class));
                    albums.add(album);
                }
                albumDAO.insert(albums);
            }
        });
    }
}