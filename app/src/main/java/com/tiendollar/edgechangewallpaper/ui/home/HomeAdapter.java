package com.tiendollar.edgechangewallpaper.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.databinding.ItemAlbumsBinding;
import com.tiendollar.edgechangewallpaper.model.Album;
import com.tiendollar.edgechangewallpaper.ui.album.AlbumActivity;
import com.tiendollar.edgechangewallpaper.utils.support.Constants;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.AlBumHolder> {
    private ArrayList<Album> albumsFiltered = new ArrayList<>();
    private Context mContext;

    HomeAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AlBumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlbumsBinding itemAlbumsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_albums, parent, false);
        return new AlBumHolder(itemAlbumsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlBumHolder holder, int position) {
        final Album currentAlbum = albumsFiltered.get(position);
        holder.albumsBinding.setAlbum(currentAlbum);
        holder.albumsBinding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AlbumActivity.class);
            intent.putExtra(Constants.ALBUM, currentAlbum);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        if (albumsFiltered != null) {
            return albumsFiltered.size();
        } else {
            return 0;
        }
    }


    void setAlbums(ArrayList<Album> mAlbums) {
        albumsFiltered = mAlbums;
        notifyDataSetChanged();
    }

    class AlBumHolder extends RecyclerView.ViewHolder {
        private ItemAlbumsBinding albumsBinding;

        AlBumHolder(@NonNull ItemAlbumsBinding itemAlbumsBinding) {
            super(itemAlbumsBinding.getRoot());
            this.albumsBinding = itemAlbumsBinding;
        }
    }
}
