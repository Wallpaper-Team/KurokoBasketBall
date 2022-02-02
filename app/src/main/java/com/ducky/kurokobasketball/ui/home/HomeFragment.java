package com.ducky.kurokobasketball.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ducky.kurokobasketball.common.widget.GridSpace;
import com.ducky.kurokobasketball.databinding.FragmentHomeBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    @Inject
    HomeAdapter homeAdapter;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        Log.d("duc.dv1", "initView: ");
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getAllAlbums().observe(getActivity(), albums -> {
            homeAdapter.setAlbums(albums);
            binding.emptyView.emptyLayout.setVisibility(albums.isEmpty() ? View.VISIBLE : View.GONE);
        });
        binding.swipeRefresh.setOnRefreshListener(() -> {
            homeViewModel.refresh();
            binding.swipeRefresh.setRefreshing(false);
        });
        binding.viewAlbums.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.viewAlbums.setAdapter(homeAdapter);
        binding.viewAlbums.addItemDecoration(new GridSpace(1, 20, false));
    }
}