package com.ducky.kurokobasketball.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.network.MyReceiver;

import static com.ducky.kurokobasketball.utils.support.Constants.APP_REQUEST_PERMISSION;

public class SplashActivity extends AppCompatActivity {
    private BroadcastReceiver MyReceiver = null;
//    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseAnalytics.getInstance(this);
        MyReceiver = new MyReceiver();
        initView();
        long SPLASH_TIME_OUT = 500;
        new Handler().postDelayed(() -> {
            if (checkSelfPermission()) {
                requestPermission();
            } else {
                init();
            }
        }, SPLASH_TIME_OUT);
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitialad_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        long SPLASH_TIME_OUT = 500;
        new Handler().postDelayed(() -> {
            if (checkSelfPermission()) {
                requestPermission();
            } else {
                init();
            }
        }, SPLASH_TIME_OUT);
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitialad_id));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showIntertitialAd() {
//        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
    }

    private void initView() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

    }


    private void init() {
        broadcastIntent();
    }

    private void showLoadingLayout() {
        findViewById(R.id.loading_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.refresh).setVisibility(View.GONE);
    }

    public void hideLoadingLayout() {
        try {
            unregisterReceiver(MyReceiver);
        } catch (Exception e) {
            Log.d("SplashActivity", "hideLoadingLayout: " + e.getMessage());
        }
        findViewById(R.id.loading_layout).setVisibility(View.GONE);
        findViewById(R.id.refresh).setVisibility(View.VISIBLE);
        findViewById(R.id.refresh).setOnClickListener(view -> {
            showLoadingLayout();
            init();

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, APP_REQUEST_PERMISSION, 1);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkSelfPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(MyReceiver);
        } catch (Exception e) {
            Log.d("SplashActivity", "onPause: " + e.getMessage());
        }
    }
}
