package com.ducky.kurokobasketball.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ducky.kurokobasketball.model.Album;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface AlbumDAO {
    @Insert(onConflict = REPLACE)
    long insert(Album album);

    @Insert(onConflict = IGNORE)
    void insertOrReplace(Album... albums);

    @Update(onConflict = REPLACE)
    void update(Album album);

    @Query("DELETE FROM album")
    void deleteAll();

    @Query("DELETE FROM album WHERE "+ DatabaseOpenHelper.AlbumEntry._ID +" = :albumID")
    void deleteAlbumById(int albumID);

    @Query("SELECT * FROM album ORDER BY " + DatabaseOpenHelper.AlbumEntry.ALBUM_ORDER + " ASC")
    List<Album> findAll();

    @Query("SELECT * FROM album WHERE "+ DatabaseOpenHelper.AlbumEntry._ID +" = :albumID")
    Album findById(int albumID);

}