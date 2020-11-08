package com.ducky.kurokobasketball.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ducky.kurokobasketball.model.Image;

@Database(entities = {Image.class}, version = 2, exportSchema = false)
public abstract class ImagesDatabase extends RoomDatabase {
    private static ImagesDatabase INSTANCE;

    public abstract ImageDAO imageDAO();

    public static ImagesDatabase getInMemoryDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ImagesDatabase.class, "image.db")
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