package com.ducky.kurokobasketball.database;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class AppDatabaseModule {

    @Singleton
    @Provides
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return AppDatabase.getInMemoryDatabase(context);
    }

    @Singleton
    @Provides
    public ImageDAO provideImageDao(AppDatabase database) {
        return database.imageDAO();
    }

    @Singleton
    @Provides
    public AlbumDAO provideAlbumDao(AppDatabase database) {
        return database.albumDao();
    }

}
