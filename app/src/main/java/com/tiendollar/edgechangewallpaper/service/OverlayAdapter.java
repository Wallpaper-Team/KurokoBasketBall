package com.tiendollar.edgechangewallpaper.service;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.common.widget.RectangleImageView;
import com.tiendollar.edgechangewallpaper.model.Image;

import java.util.List;

public class OverlayAdapter extends RecyclerView.Adapter<OverlayAdapter.MyHolder> {

    private List<Image> images;
    private ItemClickListener listener;

    void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    OverlayAdapter(List<Image> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_overlay, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final Context context = holder.imageView.getContext();
        Glide.with(context)
                .setDefaultRequestOptions(new RequestOptions()
                        .centerCrop())
                .load(images.get(position).getPath())
                .placeholder(R.drawable.album_empty)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(view -> {
            if(listener!=null){
                listener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(images!=null){
            return images.size();
        }else{
            return 0;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {
        RectangleImageView imageView;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbnail);
        }
    }

    public interface ItemClickListener{
        void onClick(int currentItem);
    }
}
