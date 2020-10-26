package com.tiendollar.edgechangewallpaper.ui.cropper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.tiendollar.edgechangewallpaper.BuildConfig;
import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.utils.support.Constants;
import com.tiendollar.edgechangewallpaper.utils.support.WindowUtils;

import java.io.File;
import java.io.FileOutputStream;

import static com.edmodo.cropper.CropImageView.GUIDELINES_ON_TOUCH;

public class CropperActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_PATH_WPP = "key_path_wpp";
    private CropImageView cropImageView;
    private TextView tv11, tv34, tv43, tv169;
    private ImageView cropfree, back, done;

    private String imagePath;
    private boolean isShowingOptionBar = false;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        setupWindow();
        imagePath = getIntent().getStringExtra(KEY_PATH_WPP);
        initView();
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

    private void initView() {
        cropImageView = findViewById(R.id.CropImageView);
        cropImageView.setGuidelines(GUIDELINES_ON_TOUCH);

        tv11 = findViewById(R.id.size11);
        tv34 = findViewById(R.id.size34);
        tv43 = findViewById(R.id.size43);
        tv169 = findViewById(R.id.size916);
        cropfree = findViewById(R.id.free_crop);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);

        tv11.setOnClickListener(this);
        tv34.setOnClickListener(this);
        tv43.setOnClickListener(this);
        tv169.setOnClickListener(this);
        cropfree.setOnClickListener(this);
        back.setOnClickListener(this);
        done.setOnClickListener(this);
        findViewById(R.id.home_option).setOnClickListener(this);
        findViewById(R.id.lock_option).setOnClickListener(this);
        findViewById(R.id.home_lock_option).setOnClickListener(this);
        findViewById(R.id.back_option).setOnClickListener(this);

        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            cropImageView.setImageBitmap(myBitmap);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.size11:
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(10, 16);
                tv11.setTextColor(getResources().getColor(R.color.color_orange));
                cropfree.setImageResource(R.drawable.ic_crop_din_black_24dp);
                break;
            case R.id.size34:
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(3, 4);
                setTextColorBlack();
                tv34.setTextColor(getResources().getColor(R.color.color_orange));
                cropfree.setImageResource(R.drawable.ic_crop_din_black_24dp);
                break;
            case R.id.size43:
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(9, 16);
                setTextColorBlack();
                tv43.setTextColor(getResources().getColor(R.color.color_orange));
                cropfree.setImageResource(R.drawable.ic_crop_din_black_24dp);
                break;
            case R.id.size916:
                cropImageView.setFixedAspectRatio(true);
                cropImageView.setAspectRatio(9, 18);
                setTextColorBlack();
                tv169.setTextColor(getResources().getColor(R.color.color_orange));
                cropfree.setImageResource(R.drawable.ic_crop_din_black_24dp);
                break;
            case R.id.free_crop:
                cropfree.setImageResource(R.drawable.ic_crop_free_black_24dp);
                cropImageView.setFixedAspectRatio(false);
                setTextColorBlack();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.done:
                showOptionBar();
                break;
            case R.id.home_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_SCREEN).apply();
                Bitmap croppedImage = cropImageView.getCroppedImage();
                setWPP(croppedImage);
                break;
            case R.id.lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.LOCK_SCREEN).apply();
                croppedImage = cropImageView.getCroppedImage();
                setWPP(croppedImage);
                break;
            case R.id.home_lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_AND_LOCK_SCREEN).apply();
                croppedImage = cropImageView.getCroppedImage();
                setWPP(croppedImage);
                break;
            case R.id.back_option:
                hideOptionBar();
                break;
        }
    }

    private void setWPP(Bitmap croppedImage) {
        File croppedPicture = createPictureFile("cropped.png");
        assert croppedPicture != null;
        String mDir = croppedPicture.getParent();
        String mFileName = croppedPicture.getName();

        File file = createFile(mDir, mFileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            croppedImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            WindowUtils.setWallPaperFitScreen(this, file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createFile(String dir, String name) {
        File file = new File(dir, name);
        File dirFile = file.getParentFile();
        assert dirFile != null;
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return file;
    }

    private File createPictureFile(String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File picture = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + BuildConfig.APPLICATION_ID,
                fileName);

        File dirFile = picture.getParentFile();
        if (!(dirFile != null && dirFile.exists())) {
            dirFile.mkdirs();
        }

        return picture;
    }

    private void setTextColorBlack() {
        tv11.setTextColor(getResources().getColor(android.R.color.black));
        tv34.setTextColor(getResources().getColor(android.R.color.black));
        tv43.setTextColor(getResources().getColor(android.R.color.black));
        tv169.setTextColor(getResources().getColor(android.R.color.black));
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
