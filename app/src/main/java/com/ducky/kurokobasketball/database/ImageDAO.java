package com.ducky.kurokobasketball.database;

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
    void insertOrReplace(Image... image);

    @Update(onConflict = REPLACE)
    void update(Image image);

    @Query("DELETE FROM image")
    void deleteAll();

    @Query("DELETE FROM image WHERE "+ DatabaseOpenHelper.ImageEntry.ALBUM_ID +" = :albumID")
    void deleteImageByAlbumId(int albumID);

    @Query("SELECT * FROM image WHERE " + DatabaseOpenHelper.ImageEntry.ALBUM_ID + " = :albumID ORDER BY " + DatabaseOpenHelper.ImageEntry.IMAGE_ORDER + " ASC LIMIT 1")
    Image getImageByOrderInAlbum(int albumID);

    @Query("SELECT * FROM image WHERE " + DatabaseOpenHelper.ImageEntry.ALBUM_ID + " = :albumID ORDER BY " + DatabaseOpenHelper.ImageEntry.IMAGE_ORDER + " ASC LIMIT :numItem")
    List<Image> getImageList(int albumID, int numItem);

    @Query("SELECT * FROM image WHERE " + DatabaseOpenHelper.ImageEntry.ALBUM_ID + " = :albumID ORDER BY " + DatabaseOpenHelper.ImageEntry.IMAGE_ORDER + " ASC")
    List<Image> getImageList(int albumID);

    @Query("DELETE FROM image WHERE "+ DatabaseOpenHelper.ImageEntry._ID +" = :id")
    void deleteImageById(int id);

    @Query("SELECT * FROM image WHERE "+ DatabaseOpenHelper.ImageEntry.IMAGE_FAVORITED +" = 1 ORDER BY " + DatabaseOpenHelper.ImageEntry.IMAGE_ORDER + " ASC")
    List<Image> getImageListFavorite();
}
