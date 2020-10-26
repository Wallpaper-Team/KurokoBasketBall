package com.tiendollar.edgechangewallpaper.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.database.AlbumsDatabase;
import com.tiendollar.edgechangewallpaper.database.ImagesDatabase;
import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.model.Image;
import com.tiendollar.edgechangewallpaper.service.ChangeWallPaperService;
import com.tiendollar.edgechangewallpaper.utils.support.Constants;
import com.tiendollar.edgechangewallpaper.utils.thread.LongThread;
import com.tiendollar.edgechangewallpaper.utils.support.WindowUtils;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.tiendollar.edgechangewallpaper.utils.support.Constants.PREF_EDGE_MODE_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final int OVERLAY_PERMISSION_CODE = 1001;
    private LinearLayout layoutController;
    private static int countCallback = 0;
    private ImagesDatabase imagesDatabase;
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        layoutController = findViewById(R.id.fullscreen_content_controls);

        findViewById(R.id.fab).setOnClickListener(this);
        findViewById(R.id.home_option).setOnClickListener(this);
        findViewById(R.id.lock_option).setOnClickListener(this);
        findViewById(R.id.home_lock_option).setOnClickListener(this);
        findViewById(R.id.back_option).setOnClickListener(this);
        fab.setOnClickListener(this);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_rate)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.nav_share:
                    WindowUtils.shareApp(this);
                    break;
                case R.id.nav_rate:
                    WindowUtils.rateApp(this);
                    break;
            }
            NavigationUI.onNavDestinationSelected(menuItem, navController);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        imagesDatabase = ImagesDatabase.getInMemoryDatabase(this);
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
    }

    private void downloadAllImages(List<Image> imageList) {
        ProgressDialog dialog = ProgressDialog.show(this, "Downloading", "Please wait...", true);

        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        countCallback = 0;
        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInMemoryDatabase(getApplicationContext());
        for (Image image : imageList) {
            Album album = albumsDatabase.albumDao().findById(image.getAlbumId());
            executor.execute(new LongThread(image.getPath(), album.getTitle(), new Handler(message -> {
                countCallback++;
                image.setPath(message.obj.toString());
                imagesDatabase.imageDAO().insert(image);
                if (countCallback == imageList.size()) {
                    setVisibleControlPanel(true);
                    dialog.dismiss();
                }
                return true;
            })));
        }
    }

    public void setVisibleControlPanel(boolean isShown) {
        if (isShown) {
            layoutController.setVisibility(View.VISIBLE);
        } else {
            layoutController.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {

                // Open the permission page
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == OVERLAY_PERMISSION_CODE) {
                if (Build.VERSION.SDK_INT >= 23) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    prefs.edit().putBoolean(PREF_EDGE_MODE_KEY, Settings.canDrawOverlays(this)).apply();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onClick(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        switch (view.getId()) {
            case R.id.home_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_SCREEN).apply();
                runAsForeGround();
                break;
            case R.id.lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.LOCK_SCREEN).apply();
                runAsForeGround();
                break;
            case R.id.home_lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_AND_LOCK_SCREEN).apply();
                runAsForeGround();
                break;
            case R.id.back_option:
                setVisibleControlPanel(false);
                break;
            case R.id.fab:
                checkOverlayPermission();
                List<Image> imageList = imagesDatabase.imageDAO().getImageListFavorite();
                assert imageList != null;
                int totalNeedDownload = 0;
                List<Image> imagesNeedDownload = new ArrayList<>();
                for (Image image : imageList) {
                    if (image.getPath().contains("http")) {
                        imagesNeedDownload.add(image);
                        totalNeedDownload++;
                    }
                }
                if (imageList.size() == 0) {
                    new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.favorite_empty_title))
                            .setMessage(R.string.favorite_empty_message).setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                    }).show();
                } else if (totalNeedDownload > 0) {
                    new AlertDialog.Builder(MainActivity.this).setTitle("Set Album Favorite As Wallpapers")
                            .setMessage(getString(R.string.message_download_dialog))
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> downloadAllImages(imagesNeedDownload))

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, (dialogInterface, i) -> {
                            })
                            .setIcon(R.drawable.ic_cloud_download_black_24dp)
                            .show();
                } else {
                    setVisibleControlPanel(true);
                }
                break;
        }
    }

    private void runAsForeGround() {
        Intent intent = new Intent(this, ChangeWallPaperService.class);
        ImagesDatabase imagesDatabase = ImagesDatabase.getInMemoryDatabase(this);
        List<Image> imageList = imagesDatabase.imageDAO().getImageListFavorite();
        intent.putParcelableArrayListExtra(Constants.IMAGE, (ArrayList<? extends Parcelable>) imageList);
        intent.putExtra(Constants.CURRENTITEM, 0);
        intent.setAction(Constants.ACTION_START_FOREGROUND_SERVICE);
        startService(intent);
        setVisibleControlPanel(false);
    }
}
