package com.tiendollar.edgechangewallpaper.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tiendollar.edgechangewallpaper.ui.MainActivity;
import com.tiendollar.edgechangewallpaper.ui.SplashActivity;
import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.model.AlbumJson;
import com.tiendollar.edgechangewallpaper.model.Image;
import com.tiendollar.edgechangewallpaper.utils.support.Constants;
import com.tiendollar.edgechangewallpaper.utils.support.FileUtils;
import com.tiendollar.edgechangewallpaper.utils.thread.LongThread;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DataLoaderFromFireBase {
    private static final String TAG = "DataLoaderFromFireBase";

    private static final String KEY_VERSION_DB = Constants.ALBUM_NAME + "/version";
    private static final String KEY_ALBUM_DB = Constants.ALBUM_NAME + "/AlbumJson";
    private static final String KEY_PRE_VERSION = "version_db";

    private DatabaseReference mDatabase;
    private WeakReference<Context> mContext;
    private ArrayList<AlbumJson> albumJsons;
    private AlbumsDatabase albumsDatabase;
    private ImagesDatabase imagesDatabase;
    private Long currentVersion;
    private SharedPreferences preferences;


    private static boolean isCheckedVersion = false;
    private static boolean isLoadedData = false;
    private static int countCallback = 0;
    private static int countImage = 0;

    private static LoaderDataListener mListener;

    public void setIsLoadedData(LoaderDataListener listener) {
        mListener = listener;
    }


    public DataLoaderFromFireBase(WeakReference<Context> context) {
        isCheckedVersion = false;
        isLoadedData = false;
        countCallback = 0;
        countImage = 0;
        mContext = context;
        albumsDatabase = AlbumsDatabase.getInMemoryDatabase(context.get());
        imagesDatabase = ImagesDatabase.getInMemoryDatabase(context.get());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        preferences = PreferenceManager.getDefaultSharedPreferences(context.get());
    }

    public void loadDefaultAlbumsIfNecessary() {
        final boolean[] isNecessary = {false};
        final File albums = new File(FileUtils.getAppOwnDirectory() + File.separator + Constants.ALBUMS);

        mDatabase.child(KEY_VERSION_DB).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (isCheckedVersion) {
                    return;
                }
                isCheckedVersion = true;
                currentVersion = dataSnapshot.getValue(Long.class);
                long oldVersion = preferences.getLong(KEY_PRE_VERSION, 0);
                Log.d(TAG, "onDataChange: version " + oldVersion + " to " + currentVersion);
                if (!albums.exists() || oldVersion != currentVersion) {
                    isNecessary[0] = true;
                }
                if (isNecessary[0]) {
                    albumsDatabase.albumDao().deleteAll();
                    imagesDatabase.imageDAO().deleteAll();
                    loadNewDB();
                } else {
                    new Handler().postDelayed(() -> startMainActivity(mContext.get()), 2500);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void loadNewDB() {
        mDatabase.child(KEY_ALBUM_DB).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (isLoadedData) {
                    return;
                }
                isLoadedData = true;
                albumJsons = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    AlbumJson item = data.getValue(AlbumJson.class);
                    albumJsons.add(item);
                }
                loadAlbumFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void loadAlbumFromFirebase() {
        FirebaseStorage rootRef = FirebaseStorage.getInstance();
        final StorageReference storageReference = rootRef.getReference();
        for (int i = 0; i < albumJsons.size(); i++) {

            final Album ab = new Album(-1, "", albumJsons.get(i).getName(), "", i, "0");

            int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    NUMBER_OF_CORES * 2,
                    NUMBER_OF_CORES * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>()
            );
            //download preview for header
            downloadHeader(executor, storageReference, i, ab);

        }
    }

    private void downloadHeader(ThreadPoolExecutor executor, StorageReference storageReference, final int finalI, final Album ab) {
        StorageReference referenceHeader = storageReference.child(albumJsons.get(finalI).getName()).child("preview.jpg");
        referenceHeader.getDownloadUrl().addOnSuccessListener(uri -> {

            String imageUrl = uri.toString();
            executor.execute(new LongThread(imageUrl, albumJsons.get(finalI).getName(), new Handler(message -> {
                countCallback++;
                Log.d(TAG, "handleMessage: " + message.obj);
                ab.setCoverPath((String) message.obj);
                albumsDatabase.albumDao().insert(ab);
                if (countCallback == albumJsons.size()) {
                    Log.d(TAG, "onSuccess");
                    preferences.edit().putLong(KEY_PRE_VERSION, currentVersion).apply();
                    startMainActivity(mContext.get());
                }
                return true;
            })));
        }).addOnFailureListener(e -> {
            Log.d(TAG, "onFailure: " + e.getMessage());
            ((SplashActivity) mContext.get()).hideLoadingLayout();
            Toast.makeText(mContext.get(), "Download failed... Please try again!", Toast.LENGTH_LONG).show();
        });
    }

    private void startMainActivity(final Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof SplashActivity) {
//            ((SplashActivity) context).showIntertitialAd();
            ((Activity) context).finish();
        }
    }

    public interface LoaderDataListener {
        void loadDone();
    }

    public static void loadImageFromFirebase(final int albumId, StorageReference albumRef, WeakReference<Context> contextWeakReference) {
        countImage = 0;
        final ImagesDatabase imagesDatabase = ImagesDatabase.getInMemoryDatabase(contextWeakReference.get());
        final AlbumsDatabase albumsDatabase = AlbumsDatabase.getInMemoryDatabase(contextWeakReference.get());
        imagesDatabase.imageDAO().deleteImageByAlbumId(albumId);
        albumRef.listAll().addOnSuccessListener(listResult -> {
            for (int i = 0; i < listResult.getItems().size(); i++) {
                final int finalI = i;
                if (!listResult.getItems().get(i).getName().contains("preview")) {
                    listResult.getItems().get(i).getDownloadUrl().addOnSuccessListener(uri -> {
                        countImage++;
                        Image image1 = new Image(albumId, uri.toString(), finalI);
                        imagesDatabase.imageDAO().insert(image1);
                        if (countImage == listResult.getItems().size()) {
                            Album album = albumsDatabase.albumDao().findById(albumId);
                            album.setCount(listResult.getItems().size() + "");
                            albumsDatabase.albumDao().update(album);
                            if (mListener != null) {
                                mListener.loadDone();
                            }
                        }
                    }).addOnFailureListener(e -> {
                        countImage++;
                        Log.d(TAG, "onFailure: " + listResult.getItems().get(finalI).getName());
                    });
                } else {
                    countImage++;
                }
            }
        });
    }
}
