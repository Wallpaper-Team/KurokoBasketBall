package com.ducky.kurokobasketball.ui.favorite;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.common.widget.GridSpace;
import com.ducky.kurokobasketball.databinding.ActivityFavoriteBinding;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.service.ChangeWallPaperService;
import com.ducky.kurokobasketball.utils.support.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteActivity extends AppCompatActivity {
    @Inject
    FavoriteAdapter adapter;
    private FavoriteViewModel viewModel;
    private ActivityFavoriteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setTitle(getString(R.string.favorite_activity));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView() {
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        binding.viewFavorite.setLayoutManager(mLayoutManager);
        binding.viewFavorite.addItemDecoration(new GridSpace(2, 20, false));

        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        adapter.setImages((ArrayList<Image>) viewModel.getImageList());
        binding.viewFavorite.setAdapter(adapter);
        binding.btnSetWallpaper.setOnClickListener(v -> {
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(Constants.SET_WALLPAPER_OPTION, Constants.HOME_AND_LOCK_SCREEN).apply();
            Intent intent = new Intent(this, ChangeWallPaperService.class);
            intent.putExtra(Constants.CURRENTITEM, 0);
            intent.setAction(Constants.ACTION_START_FOREGROUND_SERVICE);
            startService(intent);
        });
    }
}
