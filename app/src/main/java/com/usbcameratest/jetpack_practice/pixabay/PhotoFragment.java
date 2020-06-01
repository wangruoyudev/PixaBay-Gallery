package com.usbcameratest.jetpack_practice.pixabay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.usbcameratest.jetpack_practice.R;
import com.usbcameratest.jetpack_practice.databinding.FragmentPhotoBinding;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentPhotoBinding binding;
    private int position;
    private PhotoViewModel viewModel;
    private TextView pageTextView;
    public PhotoFragment(int position) {
        // Required empty public constructor
        this.position = position;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String param1, String param2) {
//        PhotoFragment fragment = new PhotoFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
        return null;
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
        binding = FragmentPhotoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Bundle bundle = getArguments();
//        PixabayUrl pixabayUrl = bundle.getParcelable("pixaUrlObject");
        viewModel = new ViewModelProvider(requireActivity(),
                new ViewModelProvider.NewInstanceFactory()).get(PhotoViewModel.class);

        PixabayUrl pixabayUrl = viewModel.getLiveData().getValue().get(position);
        Log.d(TAG, "pixabayUrl-bundle: " + pixabayUrl.largeImageURL);
        final ShimmerLayout shimmerLayout = binding.photoShimmerLayout;
        shimmerLayout.setShimmerColor(0x55ffffff);
        shimmerLayout.setShimmerAngle(0);
        shimmerLayout.startShimmerAnimation();
        Glide.with(this)
                .load(pixabayUrl.largeImageURL)
                .placeholder(R.drawable.photo)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: ", e);
                        shimmerLayout.stopShimmerAnimation();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        shimmerLayout.stopShimmerAnimation();
                        return false;
                    }
                })
                .into(binding.photoLarge);
    }

    @Override
    public void onResume() {
        super.onResume();
        pageTextView = requireActivity().findViewById(R.id.page_num);
        pageTextView.setText((position+1) + "/" + viewModel.getLiveData().getValue().size());
    }
}
