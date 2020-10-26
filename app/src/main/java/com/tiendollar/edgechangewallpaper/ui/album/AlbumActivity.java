package com.tiendollar.edgechangewallpaper.ui.album;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;

import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.model.Image;
import com.tiendollar.edgechangewallpaper.utils.support.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlbumActivity extends AppCompatActivity {
    private AlbumAdapter adapter;
    private AlbumViewModel viewModel;
    private Album album;
    private Observer<List<Image>> mObserver = new Observer<List<Image>>() {
        @Override
        public void onChanged(List<Image> images) {
            adapter.setImages((ArrayList<Image>) images);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        album = getIntent().getParcelableExtra(Constants.ALBUM);
        assert album != null;
        setTitle(album.getTitle());

        initView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getAllImages().observe(this, mObserver);
        viewModel.refreshData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.getAllImages().removeObserver(mObserver);
    }

    private void initView() {
        ImageView header = findViewById(R.id.header);
        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(new RequestOptions()
                        .fitCenter())
                .load(album.getCoverPath())
                .into(header);

        RecyclerView recyclerView = findViewById(R.id.viewWpps);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        viewModel = ViewModelProviders.of(this).get(AlbumViewModel.class);
        viewModel.setAlbum(album);
        adapter = new AlbumAdapter(this, album);
        recyclerView.setAdapter(adapter);
    }
}
