package com.usbcameratest.jetpack_practice.pixabay;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.usbcameratest.jetpack_practice.R;
import com.usbcameratest.jetpack_practice.databinding.FragmentGalleryBinding;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentGalleryBinding binding;
    private GalleryViewModel viewModel;
    private GalleryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public GalleryFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView: ");
        binding = FragmentGalleryBinding.inflate(getLayoutInflater());
        swipeRefreshLayout = binding.gallerySwipeRefreshLayout;
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fresh_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fresh_item) {
//            swipeRefreshLayout.setRefreshing(true);
//            viewModel.queryImageData();
            viewModel.resetQuery();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: ");
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(GalleryViewModel.class);
//        if (viewModel.getPagedListLiveData().getValue() == null) {
//            Log.d(TAG, "onActivityCreated: getValue null");
//            swipeRefreshLayout.setRefreshing(true);
//        }
        viewModel.getPagedListLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<PixabayUrl>>() {
            @Override
            public void onChanged(PagedList<PixabayUrl> pixabayUrls) {
                Log.d(TAG, "onChanged111: " + pixabayUrls);
//                if (swipeRefreshLayout.isRefreshing()) {
//                    swipeRefreshLayout.setRefreshing(false);
//                }
                adapter.submitList(pixabayUrls);
            }
        });
        viewModel.getNetLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "onChanged: " + integer);
                adapter.setNetStatus(integer);
                if (integer != PixabayDataSource.INIT_LOAD) {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(true);
                }
                if (integer.equals(PixabayDataSource.ERROR_LOADING)) {
                    Toast.makeText(requireActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                } else if (integer.equals(PixabayDataSource.DONE_LOADING)) {
                    Toast.makeText(requireActivity(), "已经全部加载完毕", Toast.LENGTH_SHORT).show();
                } else if (integer.equals(PixabayDataSource.NET_LOADING)) {
                    Toast.makeText(requireActivity(), "正在加载", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RecyclerView recyclerView = binding.galleryRecycleView;
        adapter = new GalleryAdapter(viewModel);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(manager);
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.resetQuery();
                viewModel.getNetLiveData().setValue(PixabayDataSource.INIT_LOAD);
            }
        });
//        viewModel.queryImageData();
    }
}
