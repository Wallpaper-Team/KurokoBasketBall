package com.ducky.kurokobasketball.ui.wallpaper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ducky.kurokobasketball.model.Image;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

public class WallpaperAdapter extends FragmentStatePagerAdapter {

    private List<Image> imageList;

    public WallpaperAdapter(@NonNull FragmentManager fm) {
        super(fm);
        imageList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new ScreenSlidePageFragment(imageList.get(position));
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    void setImageList(List<Image> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    Image getCurrentItem(int currentItem) {
        return imageList.get(currentItem);
    }
}
