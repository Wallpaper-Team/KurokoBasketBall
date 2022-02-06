package com.ducky.kurokobasketball.ui.album;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.ImageView;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.common.widget.GridSpace;
import com.ducky.kurokobasketball.database.AlbumDAO;
import com.ducky.kurokobasketball.databinding.ActivityAlbumBinding;
import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.utils.support.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlbumActivity extends AppCompatActivity {
    @Inject
    AlbumAdapter adapter;
    @Inject
    AlbumDAO albumDAO;
    private AlbumViewModel viewModel;
    private Album album;
    private ActivityAlbumBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String title = getIntent().getStringExtra(Constants.ALBUM);
        album = albumDAO.findById(title);
        assert album != null;
        setTitle(title);

        initView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initView() {
        ImageView header = findViewById(R.id.header);
        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(new RequestOptions()
                        .fitCenter())
                .load(album.getCoverPath())
                .into(header);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        binding.contentAlbum.viewWpps.setLayoutManager(mLayoutManager);
        binding.contentAlbum.viewWpps.addItemDecoration(new GridSpace(2, 20, false));

        viewModel = new ViewModelProvider(this).get(AlbumViewModel.class);
        List<Image> images = viewModel.getImageList(album.getTitle());
        adapter.setImages((ArrayList<Image>) images);
        binding.contentAlbum.viewWpps.setAdapter(adapter);
    }
}
