package com.ducky.kurokobasketball.ui.album;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducky.kurokobasketball.BuildConfig;
import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.common.widget.GridSpace;
import com.ducky.kurokobasketball.database.AlbumDAO;
import com.ducky.kurokobasketball.databinding.ActivityAlbumBinding;
import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.utils.support.Constants;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlbumActivity extends AppCompatActivity {
    public static final String AD_UNIT_ID = BuildConfig.DEBUG ?
            "ca-app-pub-3940256099942544/1033173712" : "ca-app-pub-2089873880879307/7897718464";
    @Inject
    AlbumAdapter adapter;
    @Inject
    AlbumDAO albumDAO;
    private AlbumViewModel viewModel;
    private Album album;
    private ActivityAlbumBinding binding;
    private InterstitialAd mInterstitialAd;

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

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, AD_UNIT_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
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
        final List<Image>[] images = new List[]{viewModel.getImageList(album.getTitle())};
        binding.emptyViews.setVisibility(!images[0].isEmpty() ? View.GONE : View.VISIBLE);
        adapter.setImages((ArrayList<Image>) images[0]);
        binding.contentAlbum.viewWpps.setAdapter(adapter);
        binding.contentAlbum.swipeRefresh.setOnRefreshListener(() -> {
            images[0] = viewModel.getImageList(album.getTitle());
            adapter.setImages((ArrayList<Image>) images[0]);
            binding.emptyViews.setVisibility(!images[0].isEmpty() ? View.GONE : View.VISIBLE);
            binding.contentAlbum.swipeRefresh.setRefreshing(false);
        });
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(AlbumActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
        super.onBackPressed();
    }
}
