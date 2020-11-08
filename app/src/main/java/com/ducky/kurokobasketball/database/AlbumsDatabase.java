package com.ducky.kurokobasketball.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ducky.kurokobasketball.model.Album;

@Database(entities = {Album.class}, version = 2, exportSchema = false)
public abstract class AlbumsDatabase extends RoomDatabase {
    private static AlbumsDatabase INSTANCE;

    public abstract AlbumDAO albumDao();

    public static AlbumsDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AlbumsDatabase.class, "album.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}