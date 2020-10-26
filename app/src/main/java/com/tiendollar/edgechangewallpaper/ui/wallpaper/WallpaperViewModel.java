package com.tiendollar.edgechangewallpaper.ui.wallpaper;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.model.Image;
import com.tiendollar.edgechangewallpaper.model.ImageRepository;

import java.util.List;

public class WallpaperViewModel extends AndroidViewModel {

    private ImageRepository imageRepository;
    private Context context;


    public WallpaperViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public void setAlbum(Album album){
        if(context!=null){
            imageRepository = new ImageRepository(context, album);
        }
    }

    LiveData<List<Image>> getAllWallpaper(){
        return imageRepository.getMutableLiveData();
    }

}
