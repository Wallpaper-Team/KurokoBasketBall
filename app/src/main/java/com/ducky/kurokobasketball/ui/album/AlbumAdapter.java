package com.ducky.kurokobasketball.ui.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.common.DownloadCallback;
import com.ducky.kurokobasketball.common.Utils;
import com.ducky.kurokobasketball.database.ImageDAO;
import com.ducky.kurokobasketball.databinding.ItemWallpaperBinding;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.model.State;
import com.ducky.kurokobasketball.ui.wallpaper.WallpapersActivity;
import com.ducky.kurokobasketball.utils.support.Constants;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ImageHolder> {
    private ArrayList<Image> mImages;
    private Context mContext;
    private final ImageDAO imageDAO;

    @Inject
    AlbumAdapter(@ActivityContext Context context, ImageDAO imageDAO) {
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
        if (!TextUtils.isEmpty(currentImage.getPath())) {
            currentImage.setDownloadState(State.DOWNLOADED);
        }
        holder.imagesBinding.setImage(currentImage);
        holder.imagesBinding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WallpapersActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.CURRENTITEM, currentImage.getId());
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mContext.startActivity(intent);
        });
    }

    void setImages(ArrayList<Image> mImages) {
        this.mImages = mImages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mImages == null ? 0 : mImages.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder implements DownloadCallback {
        ItemWallpaperBinding imagesBinding;

        ImageHolder(@NonNull ItemWallpaperBinding itemImagesBinding) {
            super(itemImagesBinding.getRoot());
            imagesBinding = itemImagesBinding;
        }

        @Override
        public void success(String path) {
            Image image = imagesBinding.getImage();
            image.setPath(path);
            imageDAO.update(image);
        }

        @Override
        public void failure() {
            imagesBinding.getImage().setDownloadState(State.NORMAL);
        }
    }
}
