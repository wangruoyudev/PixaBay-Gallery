package com.usbcameratest.jetpack_practice.pixabay;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
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

import io.supercharge.shimmerlayout.ShimmerLayout;

public class GalleryAdapter extends PagedListAdapter<PixabayUrl, RecyclerView.ViewHolder> {
    private static final String TAG = "GalleryAdapter";
    private static final int FOOTER_HOLDER  = 1;
    private static final int PHOTO_HOLDER = 2;
    private int netStatus;
    private Boolean hasFooter = false;
    private GalleryViewModel viewModel;

    void setNetStatus(int netStatus) {
        Log.d(TAG, "setNetStatus: " + netStatus);
        this.netStatus = netStatus;
        if (this.netStatus == PixabayDataSource.INIT_LOAD ||
                this.netStatus == PixabayDataSource.INIT_LOAD_DONE) {
            hideFooter();
        } else {
            showFooter();
        }
    }

    void hideFooter () {
        if (hasFooter) {
            notifyItemRemoved(getItemCount() - 1);
        }
        hasFooter = false;
    }

    void showFooter () {
        if (hasFooter) {
            notifyItemChanged(getItemCount() - 1);
        } else {
            hasFooter = true;
            notifyItemInserted(getItemCount() - 1);

        }
    }

    protected GalleryAdapter(GalleryViewModel viewModel) {
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
        setNetStatus(PixabayDataSource.INIT_LOAD);
        this.viewModel = viewModel;
    }

    @Override
    public int getItemCount() {
        //第一次刚加载的时候，不显示底部的footerView
        int num =  super.getItemCount() + (hasFooter ?
                1 : 0);
//        Log.d(TAG, "getItemCount: " + num);
        return num;
//        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == getItemCount() - 1) && hasFooter) {
//            Log.d(TAG, "getItemViewType: " + position);
            return FOOTER_HOLDER;
        } else {
            return PHOTO_HOLDER;
        }
//        return PHOTO_HOLDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int viewType) {
        final RecyclerView.ViewHolder holder;
        if (viewType == PHOTO_HOLDER) {
            holder = PhotoViewHolder.getInstance(parent);
            ((PhotoViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<PixabayUrl> pixabayUrlList = getCurrentList();
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", holder.getBindingAdapterPosition());
                    assert pixabayUrlList != null;
                    bundle.putParcelableArrayList("pixaUrlList", new ArrayList<>(pixabayUrlList));
                    NavController controller = Navigation.findNavController(v);
                    controller.navigate(R.id.action_galleryFragment_to_viewPagerFragment2, bundle);

                }
            });
        } else {
//            Log.d(TAG, "onCreateViewHolder: footerHolder");
            holder = FooterViewHolder.getInstance(parent);
            ((FooterViewHolder) holder).textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (netStatus == PixabayDataSource.ERROR_LOADING) {
                        viewModel.reTry();
                    }
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == PHOTO_HOLDER) {
            ((PhotoViewHolder) holder).onBindWithPhoto(getItem(position));
        } else {
            ((FooterViewHolder) holder).onBindWithFooter(netStatus);
        }
    }


    static class PhotoViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView imageView;
        private ShimmerLayout shimmerLayout;
        private TextView userName;
        private TextView likes;
        private TextView favorites;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.gallery_card_view);
            imageView = itemView.findViewById(R.id.web_image);
            shimmerLayout = itemView.findViewById(R.id.cell_shimmer_layout);
            userName = itemView.findViewById(R.id.user_name);
            likes = itemView.findViewById(R.id.likes);
            favorites = itemView.findViewById(R.id.favorites);
        }

        static PhotoViewHolder getInstance(@NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_gallery_card, parent, false);
            final PhotoViewHolder photoViewHolder = new PhotoViewHolder(view);
            return photoViewHolder;
        }

        void onBindWithPhoto(PixabayUrl item) {
            shimmerLayout.setShimmerColor(0x55ffffff);
            shimmerLayout.setShimmerAngle(0);
            shimmerLayout.startShimmerAnimation();
            shimmerLayout.startShimmerAnimation();
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.height = item.webformatHeight;
            imageView.setLayoutParams(layoutParams);

            userName.setText(item.user);
            likes.setText(String.valueOf(item.likes));
            favorites.setText(String.valueOf(item.favorites));

            Glide.with(imageView)
                    .load(item.webformatURL)
                    .placeholder(R.drawable.photo_placeholder)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            shimmerLayout.stopShimmerAnimation();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            shimmerLayout.stopShimmerAnimation();
                            return false;
                        }
                    })
                    .into(imageView);
        }

    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ProgressBar progressBar;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.net_status);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        static FooterViewHolder getInstance(@NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.cell_footer_view, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)view.getLayoutParams();
            layoutParams.setFullSpan(true);
            view.setLayoutParams(layoutParams);
            final FooterViewHolder footerViewHolder = new FooterViewHolder(view);
            return footerViewHolder;
        }

        void onBindWithFooter(int netStatus) {
//            Log.d(TAG, "onBindWithFooter-netStatus: " + netStatus);
            if (netStatus == PixabayDataSource.NET_LOADING) {
                progressBar.setVisibility(View.VISIBLE);
                textView.setText("正在加载");
            } else if (netStatus == PixabayDataSource.DONE_LOADING) {
                progressBar.setVisibility(View.GONE);
                textView.setText("全部图片加载完毕");
            } else if (netStatus == PixabayDataSource.ERROR_LOADING) {
                progressBar.setVisibility(View.GONE);
                textView.setText("网络错误请点击重试");
            } else if (netStatus == PixabayDataSource.INIT_NOTHING) {
                progressBar.setVisibility(View.GONE);
                textView.setText("搜索不到任何相关的图片");
            }
        }
    }
}
