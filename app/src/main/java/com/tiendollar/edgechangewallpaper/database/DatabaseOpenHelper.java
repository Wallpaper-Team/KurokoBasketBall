package com.tiendollar.edgechangewallpaper.database;

import android.provider.BaseColumns;

public class DatabaseOpenHelper {
    public static class AlbumEntry implements BaseColumns {
        public static final String TABLE_NAME = "album";
        public static final String _ID = "_id";
        public static final String BUCKET_ID = "bucket_id";
        public static final String PATH = "absPath";
        public static final String TITLE = "title";
        public static final String COVER_PATH = "cover_absPath";
        public static final String ALBUM_ORDER = "album_order";
        public static final String COUNT = "album_count";
    }

    public static class ImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "image";
        public static final String _ID = "_id";
        public static final String ALBUM_ID = "album_id";
        public static final String PATH = "absPath";
        public static final String IMAGE_ORDER = "image_order";
        public static final String IMAGE_FAVORITED = "favorited";
    }
}

