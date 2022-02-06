package com.ducky.kurokobasketball;

import android.app.Application;

import com.ducky.kurokobasketball.utils.ads.AppOpenManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class AppApplication extends Application {

    private static AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> {
        });

        appOpenManager = new AppOpenManager(this);
    }
}
