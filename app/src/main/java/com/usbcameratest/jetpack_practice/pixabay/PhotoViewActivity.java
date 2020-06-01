package com.usbcameratest.jetpack_practice.pixabay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.usbcameratest.jetpack_practice.databinding.ActivityPhotoViewBinding;

import java.util.ArrayList;

public class PhotoViewActivity extends AppCompatActivity {
    private ActivityPhotoViewBinding binding;
    private int page_num;
    private int position;
    private ArrayList<PixabayUrl> pixabayUrlArrayList;
    private ViewPager2 viewPager2;
    private FragmentStateAdapter adapter;
    private PhotoViewModel viewModel;
    private TextView pageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("adapter_object");
        if (bundle != null) {
            viewModel = new ViewModelProvider(this,
                    new ViewModelProvider.NewInstanceFactory()).get(PhotoViewModel.class);
            page_num = bundle.getInt("page_num");
            position = bundle.getInt("position");
            pixabayUrlArrayList = bundle.getParcelableArrayList("pixaUrlList");
            viewModel.getLiveData().setValue(pixabayUrlArrayList);
            viewPager2 = binding.viewPager2;
            adapter = new ScreenSlidePagerAdapter(this);
            viewPager2.setAdapter(adapter);
            viewPager2.setCurrentItem(position, false);
        }


    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {


        ScreenSlidePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new PhotoFragment(position);
        }

        @Override
        public int getItemCount() {
            return page_num;
        }
    }
}
