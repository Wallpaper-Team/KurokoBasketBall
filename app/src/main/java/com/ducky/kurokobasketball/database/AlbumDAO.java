package com.ducky.kurokobasketball.database;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingSource;
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
    @Insert(onConflict = IGNORE)
    long insert(Album album);

    @Insert(onConflict = IGNORE)
    void insert(List<Album> albums);

    @Update(onConflict = REPLACE)
    void update(Album album);

    @Query("DELETE FROM album")
    void deleteAll();

    @Query("DELETE FROM album WHERE mTitle LIKE :title")
    void deleteAlbumById(String title);

    @Query("SELECT * FROM album")
    LiveData<List<Album>> findAll();

    @Query("SELECT * FROM album WHERE mTitle LIKE :albumID")
    Album findById(String albumID);

    @Query("SELECT COUNT(mTitle) FROM album")
    int countItem();
}