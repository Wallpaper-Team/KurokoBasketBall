package com.ducky.kurokobasketball.ui.wallpaper;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.databinding.FragmentScreenSlidePageBinding;
import com.ducky.kurokobasketball.model.Image;

import java.util.Objects;

public class ScreenSlidePageFragment extends Fragment {

    private Image image;
    private boolean isShown = false;

    ScreenSlidePageFragment(Image image) {
        this.image = image;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentScreenSlidePageBinding binding = FragmentScreenSlidePageBinding.inflate(
                LayoutInflater.from(container.getContext()), container, false);
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

    }
}