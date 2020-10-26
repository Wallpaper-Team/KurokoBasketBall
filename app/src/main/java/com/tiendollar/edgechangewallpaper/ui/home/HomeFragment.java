package com.tiendollar.edgechangewallpaper.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tiendollar.edgechangewallpaper.R;
import com.tiendollar.edgechangewallpaper.model.Album;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private HomeAdapter homeAdapter;
    private Observer<List<Album>> mObserver = new Observer<List<Album>>() {
        @Override
        public void onChanged(List<Album> albums) {
            homeAdapter.setAlbums((ArrayList<Album>) albums);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        initView(root);
        return root;
    }

    private void initView(View root) {
        RecyclerView rvMain = root.findViewById(R.id.viewAlbums);

        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvMain.setHasFixedSize(true);
        rvMain.setLayoutManager(mLayoutManager);

        homeAdapter = new HomeAdapter(getActivity());
        rvMain.setAdapter(homeAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.getAllAlbums().observe(getActivity(), mObserver);
        homeViewModel.updateData();
        homeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        homeViewModel.getAllAlbums().removeObserver(mObserver);
    }
}