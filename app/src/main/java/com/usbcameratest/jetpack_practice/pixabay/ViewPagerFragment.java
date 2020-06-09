package com.usbcameratest.jetpack_practice.pixabay;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.print.PageRange;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.usbcameratest.jetpack_practice.R;
import com.usbcameratest.jetpack_practice.databinding.FragmentViewPagerBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {
    private static final String TAG = "ViewPagerFragment";
    private FragmentViewPagerBinding binding;
    private TextView textView;
    private ViewPager2 viewPager2;
    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentViewPagerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_view_pager, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            textView = binding.serialNumView;
            final int page_num = bundle.getInt("page_num");
            final int position = bundle.getInt("position");
            ArrayList<PixabayUrl> pixabayUrlArrayList = bundle.getParcelableArrayList("pixaUrlList");
            viewPager2 = binding.viewPager2View;
            ViewPagerListAdapter adapter = new ViewPagerListAdapter();
            adapter.submitList(pixabayUrlArrayList);
            viewPager2.setAdapter(adapter);
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    String pageNum = getString(R.string.page_num, position + 1, page_num);
                    textView.setText(pageNum);

                }
            });
            viewPager2.setCurrentItem(position, false);
            viewPager2.setPageTransformer(new ZoomOutPageTransformer());
//            adapter.notifyDataSetChanged();
            binding.savePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        savePhoto();
                    }
                }
            });
        }
    }

    void savePhoto () {
        RecyclerView.ViewHolder viewHolder= ((RecyclerView) viewPager2.getChildAt(0))
                .findViewHolderForAdapterPosition(viewPager2.getCurrentItem());
        ViewPagerListAdapter.MyViewHolder myViewHolder = (ViewPagerListAdapter.MyViewHolder) viewHolder;
        Bitmap bitmap = ((BitmapDrawable)myViewHolder.imageView.getDrawable()).getBitmap();
//        MediaStore.Images.Media.insertImage()
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "image.png");
        Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = getActivity().getContentResolver();
        Uri insertUri = resolver.insert(external, values);
        OutputStream os = null;
        if (insertUri != null) {
            try {
                os = resolver.openOutputStream(insertUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, os);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(getContext(), myViewHolder.shimmerLayout.getId() + "", Toast.LENGTH_SHORT).show();
    }

    private class ViewPagerListAdapter extends ListAdapter<PixabayUrl, ViewPagerListAdapter.MyViewHolder> {


        protected ViewPagerListAdapter() {
            super(new DiffUtil.ItemCallback<PixabayUrl>() {
                @Override
                public boolean areItemsTheSame(@NonNull PixabayUrl oldItem, @NonNull PixabayUrl newItem) {
                    return (oldItem.id == newItem.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull PixabayUrl oldItem, @NonNull PixabayUrl newItem) {
                    return (oldItem.largeImageURL.equals(newItem.largeImageURL) &&
                            oldItem.webformatURL.equals(newItem.webformatURL));
                }
            });
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.cell_viewpager2, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavController controller = Navigation.findNavController(v);
                    controller.navigateUp();
                }
            });
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            PixabayUrl pixabayUrl = getItem(position);
            holder.shimmerLayout.setShimmerColor(0x55ffffff);
            holder.shimmerLayout.setShimmerAngle(0);
            holder.shimmerLayout.startShimmerAnimation();
            Glide.with(holder.imageView)
                    .load(pixabayUrl.largeImageURL)
                    .placeholder(R.drawable.photo_placeholder)
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.d(TAG, "onLoadFailed: ", e);
                            holder.shimmerLayout.stopShimmerAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.shimmerLayout.stopShimmerAnimation();
                            return false;
                        }
                    })
                    .into(holder.imageView);
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            private ShimmerLayout shimmerLayout;
            private ImageView imageView;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                shimmerLayout = itemView.findViewById(R.id.shimmer_view);
                imageView = itemView.findViewById(R.id.photo_view);
            }
        }
    }
}
