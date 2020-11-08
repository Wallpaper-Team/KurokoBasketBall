package com.ducky.kurokobasketball.ui.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.database.ImagesDatabase;
import com.ducky.kurokobasketball.databinding.ItemWallpaperBinding;
import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.model.Image;
import com.ducky.kurokobasketball.ui.wallpaper.WallpapersActivity;
import com.ducky.kurokobasketball.utils.support.Constants;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ImageHolder> {
    private ArrayList<Image> mImages;
    private Context mContext;
    private Album curAlbum;

    AlbumAdapter(Context context, Album album) {
        mContext = context;
        curAlbum = album;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWallpaperBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_wallpaper, parent, false);
        return new ImageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, final int position) {
        final Image currentImage = mImages.get(position);
        holder.imagesBinding.setImage(currentImage);
        holder.imagesBinding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(mContext, WallpapersActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.CURRENTITEM, position);
                bundle.putParcelable(Constants.ALBUM, curAlbum);
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
        });
        CheckBox checkBox = holder.imagesBinding.getRoot().findViewById(R.id.favorite);
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            mImages.get(position).setChecked(b);
            ImagesDatabase imagesDatabase = ImagesDatabase.getInMemoryDatabase(mContext);
            imagesDatabase.imageDAO().insert(mImages.get(position));
        });
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
        ItemWallpaperBinding imagesBinding;

        ImageHolder(@NonNull ItemWallpaperBinding itemImagesBinding) {
            super(itemImagesBinding.getRoot());
            imagesBinding = itemImagesBinding;
        }
    }
}
