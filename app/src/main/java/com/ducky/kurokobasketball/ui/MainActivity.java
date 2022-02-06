package com.ducky.kurokobasketball.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.databinding.ActivityMainBinding;
import com.ducky.kurokobasketball.service.ChangeWallPaperService;
import com.ducky.kurokobasketball.ui.favorite.FavoriteActivity;
import com.ducky.kurokobasketball.utils.support.Constants;
import com.ducky.kurokobasketball.utils.support.WindowUtils;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ducky.kurokobasketball.utils.support.Constants.PREF_EDGE_MODE_KEY;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final int OVERLAY_PERMISSION_CODE = 1001;
    private LinearLayout layoutController;

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        layoutController = findViewById(R.id.fullscreen_content_controls);

        binding.appBarMain.fab.setOnClickListener(this);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_rate)
                .setDrawerLayout(binding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setNavigationItemSelectedListener(menuItem -> {
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
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onNavigateUp() {
        return super.onNavigateUp();
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
                break;
            case R.id.lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.LOCK_SCREEN).apply();
                break;
            case R.id.home_lock_option:
                WindowUtils.setProgressDialog(this);
                prefs.edit().putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_AND_LOCK_SCREEN).apply();
                break;
            case R.id.back_option:
                setVisibleControlPanel(false);
                break;
            case R.id.fab:
                checkOverlayPermission();
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
        }
    }
}
