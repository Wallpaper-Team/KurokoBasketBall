package com.tiendollar.edgechangewallpaper.ui.slideshow;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.model.Image;
import com.tiendollar.edgechangewallpaper.utils.support.Constants;
import com.tiendollar.edgechangewallpaper.utils.support.FileUtils;
import com.tiendollar.edgechangewallpaper.utils.callback.ItemTouchListenner;
import com.tiendollar.edgechangewallpaper.utils.callback.TouchHelperCallback;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    private FavoriteAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.viewFavorite);
        GridLayoutManager mLayoutManager = new GridLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new FavoriteAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        addItemTouchCallback(recyclerView);
        return root;
    }

    @SuppressLint("IntentReset")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fab).setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, Constants.PICK_IMAGE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        slideshowViewModel.getAllImages().observe(Objects.requireNonNull(getActivity()), images -> adapter.setImages((ArrayList<Image>) images));
    }

    private void addItemTouchCallback(RecyclerView recyclerView) {
        ItemTouchHelper.Callback callback = new TouchHelperCallback(new ItemTouchListenner() {
            @Override
            public void onMove(int oldPosition, int newPosition) {
                adapter.onMove(oldPosition, newPosition);
            }

            @Override
            public void swipe(int position, int direction) {
                adapter.swipe(position);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addImage(String path) {
        Image image = new Image(-1, path, adapter.getItemCount());
        adapter.addMoreImage(image);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.PICK_IMAGE:
                    if (data != null) {
                        if (data.getData() != null) {

                            Uri mImageUri = data.getData();

                            String imagePath = FileUtils.getPath(getActivity(), mImageUri);
                            addImage(imagePath);
                        } else {
                            if (data.getClipData() != null) {
                                ClipData mClipData = data.getClipData();
                                int i = 0;
                                for (; i < mClipData.getItemCount(); i++) {

                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    String imagePath = FileUtils.getPath(getActivity(), uri);
                                    addImage(imagePath);
                                }
                                Toast.makeText(getActivity(), "Added " + i + " images", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }
}