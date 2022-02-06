package com.ducky.kurokobasketball.ui.wallpaper;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.ducky.kurokobasketball.common.DownloadCallback;
import com.ducky.kurokobasketball.common.Utils;
import com.ducky.kurokobasketball.database.ImageDAO;
import com.ducky.kurokobasketball.databinding.ActivityWallpapersBinding;
import com.ducky.kurokobasketball.model.State;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.common.ZoomOutPageTransformer;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.ui.cropper.CropperActivity;
import com.ducky.kurokobasketball.utils.support.Constants;
import com.ducky.kurokobasketball.utils.support.WindowUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

@AndroidEntryPoint
public class WallpapersActivity extends AppCompatActivity implements View.OnClickListener, DownloadCallback {

    private static final int TYPE_DOWNLOAD = 1;
    private static final int TYPE_CROP = 2;
    private static final int TYPE_SET_AS_WALLPAPER = 3;
    private WallpaperViewModel viewModel;
    private WallpaperAdapter adapter;

    private boolean isFabClicked = false;
    private String currentImage;
    private Image image;
    private boolean isShowingOptionBar = false;
    private SharedPreferences prefs;

    private ActivityWallpapersBinding binding;
    @Inject
    ImageDAO imageDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWallpapersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupWindow();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        currentImage = bundle.getString(Constants.CURRENTITEM);
        viewModel = new ViewModelProvider(this).get(WallpaperViewModel.class);
        image = imageDAO.findImageByID(currentImage);
        assert image != null;
        binding.setImage(image);
        List<Image> images = viewModel.getImages(image.getAlbumId());
        adapter = new WallpaperAdapter(getSupportFragmentManager(), new WeakReference<>(this));
        adapter.setImageList(images);
        binding.pager.setAdapter(adapter);
        binding.pager.setPageTransformer(false, new ZoomOutPageTransformer());
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getId().equalsIgnoreCase(image.getId())) {
                binding.pager.setCurrentItem(i, false);
                break;
            }
        }
        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                image = adapter.getCurrentItem(binding.pager.getCurrentItem());
                binding.setImage(image);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.crop.setOnClickListener(this);
        binding.setAsWpp.setOnClickListener(this);
        binding.share.setOnClickListener(this);
        binding.btnGroup.homeOption.setOnClickListener(this);
        binding.btnGroup.lockOption.setOnClickListener(this);
        binding.btnGroup.homeLockOption.setOnClickListener(this);
        binding.btnGroup.backOption.setOnClickListener(this);
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
            binding.crop.setVisibility(View.GONE);
            binding.share.setVisibility(View.GONE);
            binding.setAsWpp.setVisibility(View.GONE);
        } else {
            binding.crop.setVisibility(View.VISIBLE);
            binding.share.setVisibility(View.VISIBLE);
            binding.setAsWpp.setVisibility(View.VISIBLE);
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
        switch (view.getId()) {
            case R.id.crop:
                if (TextUtils.isEmpty(image.getPath())) {
                    type = TYPE_CROP;
                    downloadImage();
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
                if (TextUtils.isEmpty(image.getPath())) {
                    type = TYPE_SET_AS_WALLPAPER;
                    downloadImage();
                } else {
                    showOptionBar();
                }
                setClickEventOnFab();
                break;
            case R.id.home_option:
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_SCREEN).apply();
                WindowUtils.setWallPaperFitScreen(this, image.getPath());
                hideOptionBar();
                break;
            case R.id.lock_option:
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.LOCK_SCREEN).apply();
                WindowUtils.setWallPaperFitScreen(this, image.getPath());
                hideOptionBar();
                break;
            case R.id.home_lock_option:
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_AND_LOCK_SCREEN).apply();
                WindowUtils.setWallPaperFitScreen(this, image.getPath());
                hideOptionBar();
                break;
            case R.id.back_option:
                hideOptionBar();
                break;
        }
    }

    private int type;

    public void downloadImage() {
        image.setDownloadState(State.DOWNLOADING);
        Utils.downloadThroughManager(image.getAlbumId(), image.getFullSize(), this, this);
    }

    public void setClickEventOnFab() {
        setAnimation(binding.crop, -200f, -200);
        setAnimation(binding.setAsWpp, 0f, -300);
        setAnimation(binding.share, 200f, -200);
        isFabClicked = !isFabClicked;
    }

    private void startCropperActivity(String s) {
        Intent intent = new Intent(this, CropperActivity.class);
        intent.putExtra(CropperActivity.KEY_PATH_WPP, s);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void showOptionBar() {
        binding.btnGroup.getRoot().setVisibility(View.VISIBLE);
        isShowingOptionBar = true;
    }

    private void hideOptionBar() {
        binding.btnGroup.getRoot().setVisibility(View.GONE);
        isShowingOptionBar = false;
    }

    @Override
    public void onBackPressed() {
        if (isShowingOptionBar) {
            hideOptionBar();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void success(String path) {
        image.setPath(path);
        image.setDownloadState(State.DOWNLOADED);
        imageDAO.update(image);
        switch (type) {
            case TYPE_CROP:
                startCropperActivity(path);
                break;
            case TYPE_SET_AS_WALLPAPER:
                showOptionBar();
                break;
        }
        type = -1;
    }

    @Override
    public void failure() {
        image.setDownloadState(State.NORMAL);
        Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
    }
}
