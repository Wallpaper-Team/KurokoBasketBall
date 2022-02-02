package com.ducky.kurokobasketball.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.ducky.kurokobasketball.R;
import com.ducky.kurokobasketball.databinding.ItemAlbumsBinding;
import com.ducky.kurokobasketball.model.Album;
import com.ducky.kurokobasketball.ui.album.AlbumActivity;
import com.ducky.kurokobasketball.utils.support.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.AlBumHolder> {
    private List<Album> albumsFiltered = new ArrayList<>();
    private Context mContext;

    @Inject
    public HomeAdapter(@ActivityContext Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public AlBumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlbumsBinding itemAlbumsBinding = ItemAlbumsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new AlBumHolder(itemAlbumsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlBumHolder holder, int position) {
        final Album currentAlbum = albumsFiltered.get(position);
        holder.albumsBinding.setAlbum(currentAlbum);
        holder.albumsBinding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AlbumActivity.class);
            intent.putExtra(Constants.ALBUM, currentAlbum.getTitle());
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return albumsFiltered == null ? 0 : albumsFiltered.size();
    }


    void setAlbums(List<Album> mAlbums) {
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
