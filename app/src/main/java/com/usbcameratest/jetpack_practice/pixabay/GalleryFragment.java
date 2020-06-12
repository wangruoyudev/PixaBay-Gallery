package com.usbcameratest.jetpack_practice.pixabay;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

import java.util.ArrayList;
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
    private RecyclerView recyclerView;

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
        binding = FragmentGalleryBinding.inflate(getLayoutInflater());
        swipeRefreshLayout = binding.gallerySwipeRefreshLayout;
        recyclerView = binding.galleryRecycleView;
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
            swipeRefreshLayout.setRefreshing(true);
            viewModel.resetquery();
        } else if (item.getItemId() == R.id.delete_item) {
            ArrayList<PixabayUrl> arrayList = new ArrayList<>();
            PixabayUrl pixabayUrl = new PixabayUrl(-1, null, null, 0, 0);
            arrayList.add(pixabayUrl);
            adapter.submitList(arrayList);
            Toast.makeText(requireActivity(), "" + arrayList.size(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()))
                .get(GalleryViewModel.class);
        if (viewModel.getLiveData().getValue() == null) {
            swipeRefreshLayout.setRefreshing(true);
        }
//        viewModel.getLiveDataStatus().setValue(GalleryRepository.DONOTHING);
        viewModel.getLiveDataFresh().setValue(false);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), new Observer<List<PixabayUrl>>() {
            @Override
            public void onChanged(List<PixabayUrl> pixabayUrls) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                adapter.submitList(pixabayUrls);
                if (viewModel.getNeedSroll()) {
                    recyclerView.scrollToPosition(0);
                    viewModel.setNeedSroll(false);
                }
            }
        });
        viewModel.getLiveDataStatus().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.d(TAG, "getLiveDataStatus-onChanged: " + integer);
                if (integer == GalleryRepository.LOADING_ERROR) {
                    Toast.makeText(requireActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                adapter.setFootviewStatus(integer);
                Log.d(TAG, "getItemCount: " + adapter.getItemCount());
                if (adapter.getItemCount() > 0) {
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                }
            }
        });
        viewModel.getLiveDataFresh().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, "onChanged: " + aBoolean);
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });

        adapter = new GalleryAdapter(requireActivity(), viewModel);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(manager);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.resetquery();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    return;
                }
                StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager)recyclerView.getLayoutManager();
                int[] arrayPosition = new int[2];
                layoutManager.findLastVisibleItemPositions(arrayPosition);
                Log.d(TAG, "onScrolled: " + arrayPosition[0]);
                if (arrayPosition[0] == adapter.getItemCount() - 1) {
                    Log.d(TAG, "onScrolled: queryImageData");
                    viewModel.queryImageData();
                }
            }
        });
    }
}
