package com.tiendollar.edgechangewallpaper.utils.support;

import android.Manifest;
import android.content.Intent;


public class Constants {

    public static final String APP_OWNER_FOLDER_NAME = "EdgeChangeWPP";

    public static final String ALBUMS = "Albums";

    public static final String ALBUM = "Album";

    public static final String IMAGE = "Image";

    public static final String[] APP_REQUEST_PERMISSION = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String CURRENTITEM = "cur_item";

    public static final String PREF_CUSTOM_FREQUENCY_KEY = "pref_frequency_custom";
    public static final String PREF_ALLOW_SHAKE_SWITCH_KEY = "allow_shake";
    public static final String PREF_AUTO_CHANGE_FREQUENCY_KEY = "autochange_frequency";
    public static final String PREF_EDGE_MODE_KEY = "edge_mode";
    public static final String PREF_AUTO_CHANGE_MODE_KEY = "autochange_mode";
    public static final String PREF_AUTO_CHANGE_SWITCH_KEY = "autochange_switch";
    public static final String PREF_SHARE_APP_KEY = "pref_share_app";
    public static final String PREF_RATE_APP_KEY = "pref_rate_app";


    /*For forground service*/
    public static final int FOREGROUND_ID = 8080;

    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_SERVICE";

    public static final String ACTION_START_IN_LOOP_FOREGROUND_SERVICE = "ACTION_START_IN_LOOP_SERVICE";

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_SERVICE";

    public static final String ACTION_NEXT = "next";

    public static final String ACTION_PREVIOUS = "previous";

    public static final String CHANEL_ID = "com.tiendollar.edgechangewallpaper";

    public static final String ACTION_APPWIDGET_UPDATE = "com.tiendollar.ACTION_APPWIDGET_UPDATE";

    public static final String HOME_SCREEN = "Home screen";

    public static final String LOCK_SCREEN = "Lock screen";

    public static final String HOME_AND_LOCK_SCREEN = "Home and lock screens";

    public static final int PICK_IMAGE = 7812;

    public static final String SET_WALLPAPER_OPTION = "SET_WALLPAPER_OPTION";

    public static final String ALBUM_NAME = "albums3";
}
