package com.ducky.kurokobasketball.ui.favorite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ducky.kurokobasketball.common.DownloadCallback;
import com.ducky.kurokobasketball.database.ImageDAO;
import com.ducky.kurokobasketball.databinding.ItemWallpaperBinding;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.model.State;
import com.ducky.kurokobasketball.ui.wallpaper.WallpapersActivity;
import com.ducky.kurokobasketball.utils.support.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ImageHolder> {
    private ArrayList<Image> mImages;
    private Context mContext;
    private final ImageDAO imageDAO;

    @Inject
    FavoriteAdapter(@ActivityContext Context context, ImageDAO imageDAO) {
        mContext = context;
        this.imageDAO = imageDAO;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWallpaperBinding binding = ItemWallpaperBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ImageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, final int position) {
        final Image currentImage = mImages.get(position);
        holder.imagesBinding.setImage(currentImage);
    }

    void setImages(ArrayList<Image> mImages) {
        this.mImages = mImages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mImages == null ? 0 : mImages.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ItemWallpaperBinding imagesBinding;

        ImageHolder(@NonNull ItemWallpaperBinding itemImagesBinding) {
            super(itemImagesBinding.getRoot());
            imagesBinding = itemImagesBinding;
        }
    }
}
