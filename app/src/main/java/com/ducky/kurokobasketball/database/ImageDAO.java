package com.ducky.kurokobasketball.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.ducky.kurokobasketball.model.Image;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ImageDAO {
    @Insert(onConflict = REPLACE)
    long insert(Image image);

    @Insert(onConflict = IGNORE)
    void insert(List<Image> images);

    @Update(onConflict = REPLACE)
    void update(Image image);

    @Query("DELETE FROM image")
    void deleteAll();

    @Query("DELETE FROM image WHERE albumId LIKE :albumID")
    void deleteImageByAlbumId(String albumID);

    @Query("SELECT * FROM image WHERE albumId LIKE :album")
    List<Image> getImageList(String album);

    @Query("SELECT * FROM image WHERE albumId LIKE :albumName ORDER BY id DESC LIMIT 1")
    Image findLatestImageByID(String albumName);

    @Query("SELECT * FROM image WHERE id LIKE :id")
    Image findImageByID(String id);

    @Query("DELETE FROM image WHERE id LIKE :id")
    void deleteImageById(String id);

    @Query("SELECT * FROM image WHERE path IS NOT NULL")
    List<Image> getImageListFavorite();
}
