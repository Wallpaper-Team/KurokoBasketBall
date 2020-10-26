package com.tiendollar.edgechangewallpaper.ui.wallpaper;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.database.ImagesDatabase;
import com.tiendollar.edgechangewallpaper.databinding.FragmentScreenSlidePageBinding;
import com.tiendollar.edgechangewallpaper.model.Image;

import java.util.Objects;

public class ScreenSlidePageFragment extends Fragment {

    private Image image;
    private boolean isShown = false;
    private ImagesDatabase imagesDatabase;

    ScreenSlidePageFragment(Image image) {
        this.image = image;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentScreenSlidePageBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen_slide_page, container, false);
        binding.setImage(image);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView wpp = view.findViewById(R.id.wallpaper);
        wpp.setOnClickListener(view1 -> {
            View decorView = Objects.requireNonNull(getActivity()).getWindow().getDecorView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isShown) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                    isShown = false;
                } else {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LOW_PROFILE
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    isShown = true;
                }
            }
        });

        imagesDatabase = ImagesDatabase.getInMemoryDatabase(getActivity());
        CheckBox checkBox = view.findViewById(R.id.favorite);
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            image.setChecked(b);
            imagesDatabase.imageDAO().insert(image);
        });
    }
}