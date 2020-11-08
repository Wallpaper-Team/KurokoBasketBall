package com.ducky.kurokobasketball.ui.slideshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.database.ImagesDatabase;
import com.ducky.kurokobasketball.databinding.ItemFavoriteBinding;
import com.ducky.kurokobasketball.model.Image;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ImageHolder> {
    private ArrayList<Image> mImages;
    private Context mContext;
    private ImagesDatabase imagesDatabase;

    FavoriteAdapter(Context mContext) {
        this.mContext = mContext;
        imagesDatabase = ImagesDatabase.getInMemoryDatabase(mContext);
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
        return mImages.size();
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
                    if (mImages.get(position).getAlbumId() != -1) {
                        mImages.get(position).setChecked(false);
                        imagesDatabase.imageDAO().insert(mImages.get(position));
                    }
                    mImages.remove(position);
                    notifyItemRemoved(position);
                })

                .setNegativeButton(android.R.string.no, (dialogInterface, i) -> notifyItemChanged(position))
                .setIcon(android.R.drawable.ic_delete)
                .show();

    }

    void onMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                int tmp = mImages.get(i).getImageOrder();
                mImages.get(i).setImageOrder(mImages.get(i + 1).getImageOrder());
                mImages.get(i + 1).setImageOrder(tmp);
                Collections.swap(mImages, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                int tmp = mImages.get(i).getImageOrder();
                mImages.get(i).setImageOrder(mImages.get(i - 1).getImageOrder());
                mImages.get(i - 1).setImageOrder(tmp);
                Collections.swap(mImages, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    void addMoreImage(Image image) {
        image.setChecked(true);
        long id = imagesDatabase.imageDAO().insert(image);
        image.setId((int) id);
        mImages.add(image);
    }
}
