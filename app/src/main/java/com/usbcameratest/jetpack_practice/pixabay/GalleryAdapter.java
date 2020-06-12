package com.usbcameratest.jetpack_practice.pixabay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.usbcameratest.jetpack_practice.R;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class GalleryAdapter extends ListAdapter<PixabayUrl, GalleryAdapter.MyViewHolder> {
    private static final String TAG = "GalleryAdapter";
    private Activity activity;
    private GalleryViewModel viewModel;
    private static final int NORMAL_VIEW_TYPE = 0;
    private static final int FOOTER_VIEW_TYPE = 1;
    private int footviewStatus;
    private Boolean isClickable = false;

    protected GalleryAdapter(Activity activity, GalleryViewModel viewModel) {
        super(new DiffUtil.ItemCallback<PixabayUrl>() {
            @Override
            public boolean areItemsTheSame(@NonNull PixabayUrl oldItem, @NonNull PixabayUrl newItem) {
                return (oldItem.id == newItem.id);
            }

            @Override
            public boolean areContentsTheSame(@NonNull PixabayUrl oldItem, @NonNull PixabayUrl newItem) {
                return (oldItem.webformatURL.equals(newItem.webformatURL) &&
                        oldItem.largeImageURL.equals(newItem.largeImageURL));
            }
        });
        this.activity = activity;
        this.viewModel = viewModel;
    }

    public void setFootviewStatus(int footviewStatus) {
        this.footviewStatus = footviewStatus;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view;
        final MyViewHolder myViewHolder;
        if (viewType == NORMAL_VIEW_TYPE) {
            Log.d(TAG, "onCreateViewHolder-NORMAL_VIEW_TYPE: ");
            view = layoutInflater.inflate(R.layout.cell_gallery_card, parent, false);
            myViewHolder = new MyViewHolder(view);
            myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PixabayUrl pixabayUrl = (PixabayUrl) myViewHolder.itemView.getTag(R.id.pixabayLargeUrl);
//                Log.d(TAG, "onClick-pixabayLargeUrl: " + pixabayUrl.largeImageURL);
                    List<PixabayUrl> pixabayUrlList = getCurrentList();
                    Bundle bundle = new Bundle();
                    bundle.putInt("page_num", getItemCount());
                    bundle.putInt("position", (int) myViewHolder.itemView.getTag(R.id.pixabayPosition));
                    bundle.putParcelable("pixaUrlObject", pixabayUrl);
                    bundle.putParcelableArrayList("pixaUrlList", new ArrayList<>(pixabayUrlList));
//                    Intent intent = new Intent(activity, PhotoViewActivity.class);
//                    intent.putExtra("adapter_object", bundle);
//                    activity.startActivity(intent);
//
                    NavController controller = Navigation.findNavController(v);
                    controller.navigate(R.id.action_galleryFragment_to_viewPagerFragment2, bundle);

                }
            });
        } else {
            Log.d(TAG, "onCreateViewHolder: footer_view");
            view = layoutInflater.inflate(R.layout.footer_view, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            myViewHolder = new MyViewHolder(view);
            TextView textView = myViewHolder.itemView.findViewById(R.id.status_text);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClickable) {
                        Log.d(TAG, "onClick: text");
                        viewModel.queryImageData();
                        viewModel.getLiveDataFresh().setValue(true);
                    }
                }
            });
        }
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            TextView textView = holder.itemView.findViewById(R.id.status_text);
            ProgressBar progressBar = holder.itemView.findViewById(R.id.progressBar);
            Log.d(TAG, "onBindViewHolder: " + footviewStatus);
            if (textView != null) {
                Log.d(TAG, "onBindViewHolder111: " + footviewStatus);
                if (footviewStatus == GalleryRepository.NORMAL_LOADING) {
                    isClickable = false;
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setText("正在加载中...");
                } else if (footviewStatus == GalleryRepository.LOADING_DONE) {
                    isClickable = false;
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setText("没有更多的数据了");
                } else if (footviewStatus == GalleryRepository.LOADING_ERROR) {
                    isClickable = true;
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setText("网络错误，点击重试或下拉刷新");
                }
            }
            return;
        }
        Log.d(TAG, "onBindViewHolder-Normal: ");
        PixabayUrl pixabayUrl = getItem(position);
        if (pixabayUrl.id == -1) {
            holder.itemView.setVisibility(View.INVISIBLE);
            return;
        }
//        List<PixabayUrl> pixabayUrlList =  getCurrentList();
        holder.itemView.setTag(R.id.pixabayLargeUrl, pixabayUrl);
        holder.itemView.setTag(R.id.pixabayPosition, position);

        holder.shimmerLayout.setShimmerColor(0x55ffffff);
        holder.shimmerLayout.setShimmerAngle(0);
        holder.shimmerLayout.startShimmerAnimation();
        holder.shimmerLayout.startShimmerAnimation();

        ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
        layoutParams.height = getItem(position).webformatHeight;
        holder.imageView.setLayoutParams(layoutParams);

        holder.userName.setText(pixabayUrl.user);
        holder.likes.setText(String.valueOf(pixabayUrl.likes));
        holder.favorites.setText(String.valueOf(pixabayUrl.favorites));

        Glide.with(holder.imageView)
                .load(pixabayUrl.webformatURL)
                .placeholder(R.drawable.photo_placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
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

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_VIEW_TYPE;
        }
        return NORMAL_VIEW_TYPE;
//        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (super.getItemCount() == 0) {
            return super.getItemCount();
        }
        return super.getItemCount() + 1;
//        return super.getItemCount();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView imageView;
        private ShimmerLayout shimmerLayout;
        private TextView userName;
        private TextView likes;
        private TextView favorites;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.gallery_card_view);
            imageView = itemView.findViewById(R.id.web_image);
            shimmerLayout = itemView.findViewById(R.id.cell_shimmer_layout);
            userName = itemView.findViewById(R.id.user_name);
            likes = itemView.findViewById(R.id.likes);
            favorites = itemView.findViewById(R.id.favorites);
        }
    }
}
