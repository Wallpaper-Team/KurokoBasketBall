package com.ducky.kurokobasketball.ui.wallpaper;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.common.ZoomOutPageTransformer;
import com.ducky.kurokobasketball.database.AlbumsDatabase;
import com.ducky.kurokobasketball.database.ImagesDatabase;
import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.ui.cropper.CropperActivity;
import com.ducky.kurokobasketball.utils.support.Constants;
import com.ducky.kurokobasketball.utils.thread.LongThread;
import com.ducky.kurokobasketball.utils.support.WindowUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

public class WallpapersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TYPE_DOWNLOAD = 1;
    private static final int TYPE_CROP = 2;
    private static final int TYPE_SET_AS_WALLPAPER = 3;
    private WallpaperViewModel viewModel;
    private WallpaperAdapter adapter;
    private FloatingActionButton cropBtn;
    private FloatingActionButton setAsWPPBtn;
    private FloatingActionButton shareBtn;
    private FloatingActionButton downloadBtn;
    private ProgressDialog dialog;

    private boolean isFabClicked = false;
    private int curWPP = 0;
    private Image image;
    private ViewPager viewPager;
    private boolean isShowingOptionBar = false;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);
        setupWindow();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        Album album = bundle.getParcelable(Constants.ALBUM);
        curWPP = bundle.getInt(Constants.CURRENTITEM);
        assert album != null;
        viewModel = ViewModelProviders.of(this).get(WallpaperViewModel.class);
        viewModel.setAlbum(album);
        adapter = new WallpaperAdapter(getSupportFragmentManager());
        initView();
    }

    private void initView() {
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewModel.getAllWallpaper().observe(this, images -> {
            adapter.setImageList(images);
            if (images.size() > 0 && curWPP < images.size()) {
                viewPager.setCurrentItem(curWPP);
            }
        });
        cropBtn = findViewById(R.id.crop);
        downloadBtn = findViewById(R.id.download);
        setAsWPPBtn = findViewById(R.id.set_as_wpp);
        shareBtn = findViewById(R.id.share);
        FloatingActionButton fab = findViewById(R.id.fab);

        cropBtn.setOnClickListener(this);
        downloadBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        setAsWPPBtn.setOnClickListener(this);
        fab.setOnClickListener(this);


        findViewById(R.id.home_option).setOnClickListener(this);
        findViewById(R.id.lock_option).setOnClickListener(this);
        findViewById(R.id.home_lock_option).setOnClickListener(this);
        findViewById(R.id.back_option).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }

    private void setAnimation(View view, float valueX, float valueY) {
        if (isFabClicked) {
            valueX = 0f;
            valueY = 0f;
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, valueX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, valueY);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY);
        animator.setDuration(500);
        animator.start();
    }

    private void setupWindow() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }

    @Override
    public void onClick(View view) {
        image = adapter.getCurrentItem(viewPager.getCurrentItem());
        switch (view.getId()) {
            case R.id.crop:
                if (!image.getPath().contains(Environment.getExternalStorageDirectory().getPath())) {
                    dialog = ProgressDialog.show(this, "Downloading", "Please wait...", true);
                    downloadImage(TYPE_CROP);
                } else {
                    startCropperActivity(image.getPath());
                }
                setClickEventOnFab();
                break;
            case R.id.share:
                WindowUtils.shareApp(this);
                setClickEventOnFab();
                break;
            case R.id.set_as_wpp:
                if (!image.getPath().contains(Environment.getExternalStorageDirectory().getPath())) {
                    dialog = ProgressDialog.show(this, "Downloading", "Please wait...", true);
                    downloadImage(TYPE_SET_AS_WALLPAPER);
                }else{
                    showOptionBar();
                }
                setClickEventOnFab();
                break;
            case R.id.download:
                if (image != null) {
                    if (!image.getPath().contains(Environment.getExternalStorageDirectory().getPath())) {
                        dialog = ProgressDialog.show(this, "Downloading", "Please wait...", true);
                        downloadImage(TYPE_DOWNLOAD);
                    } else {
                        Toast.makeText(this, "Wallpaper was downloaded at " + image.getPath(), Toast.LENGTH_SHORT).show();
                    }
                }
                setClickEventOnFab();
                break;
            case R.id.fab:
                setClickEventOnFab();
                break;
            case R.id.home_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_SCREEN).apply();
                WindowUtils.setWallPaperFitScreen(this, image.getPath());
                break;
            case R.id.lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.LOCK_SCREEN).apply();
                WindowUtils.setWallPaperFitScreen(this, image.getPath());

                break;
            case R.id.home_lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_AND_LOCK_SCREEN).apply();
                WindowUtils.setWallPaperFitScreen(this, image.getPath());
                break;
            case R.id.back_option:
                hideOptionBar();
                break;
        }
    }

    private void setClickEventOnFab() {
        setAnimation(cropBtn, -250f, -200);
        setAnimation(downloadBtn, -100f, -300);
        setAnimation(setAsWPPBtn, 100f, -300);
        setAnimation(shareBtn, 250f, -200);
        isFabClicked = !isFabClicked;
    }

    private void downloadImage(int type) {
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        AlbumsDatabase albumsDatabase = AlbumsDatabase.getInMemoryDatabase(this);
        Album album = albumsDatabase.albumDao().findById(image.getAlbumId());
        executor.execute(new LongThread(image.getPath(), album.getTitle(), new Handler(message -> {
            if (dialog != null) {
                dialog.dismiss();
            }
            image.setPath((String) message.obj);
            ImagesDatabase imagesDatabase = ImagesDatabase.getInMemoryDatabase(this);
            imagesDatabase.imageDAO().insert(image);
            switch (type) {
                case TYPE_DOWNLOAD:
                    Toast.makeText(this, "Downloaded this wallpaper... You can find it at:" + image.getPath(), Toast.LENGTH_SHORT).show();
                    break;
                case TYPE_CROP:
                    startCropperActivity(message.obj.toString());
                    break;
                case TYPE_SET_AS_WALLPAPER:
                    showOptionBar();
                    break;
            }
            return true;
        })));
    }

    private void startCropperActivity(String s) {
        Intent intent = new Intent(this, CropperActivity.class);
        intent.putExtra(CropperActivity.KEY_PATH_WPP, s);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void showOptionBar() {
        findViewById(R.id.fullscreen_content_controls).setVisibility(View.VISIBLE);
        isShowingOptionBar = true;
    }

    private void hideOptionBar() {
        findViewById(R.id.fullscreen_content_controls).setVisibility(View.GONE);
        isShowingOptionBar = false;
    }

    @Override
    public void onBackPressed() {
        if (isShowingOptionBar) {
            hideOptionBar();
        }else{
            super.onBackPressed();
        }
    }
}
