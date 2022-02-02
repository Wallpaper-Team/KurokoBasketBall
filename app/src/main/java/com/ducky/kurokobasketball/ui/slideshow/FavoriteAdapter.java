package com.ducky.kurokobasketball.ui.slideshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.databinding.ItemFavoriteBinding;
import com.ducky.kurokobasketball.model.Image;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ImageHolder> {
    private ArrayList<Image> mImages;
    private Context mContext;

    FavoriteAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_favorite, parent, false);
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
        return mImages == null? 0: mImages.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        ItemFavoriteBinding imagesBinding;

        ImageHolder(@NonNull ItemFavoriteBinding itemImagesBinding) {
            super(itemImagesBinding.getRoot());
            imagesBinding = itemImagesBinding;
        }
    }

    void swipe(final int position) {
        new AlertDialog.Builder(mContext).setTitle("Delete Wallpaper").setMessage("Do you want to delete this wallpaper?").
                setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    /*if (mImages.get(position).getAlbumId() != -1) {
                        mImages.get(position).setChecked(false);
//                        imagesDatabase.imageDAO().insert(mImages.get(position));
                    }*/
                    mImages.remove(position);
                    notifyItemRemoved(position);
                })

                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> notifyItemChanged(position))
                .setIcon(android.R.drawable.ic_delete)
                .show();

    }

    void addMoreImage(Image image) {
        /*long id = imagesDatabase.imageDAO().insert(image);
        image.setId((int) id);*/
        mImages.add(image);
    }
}
