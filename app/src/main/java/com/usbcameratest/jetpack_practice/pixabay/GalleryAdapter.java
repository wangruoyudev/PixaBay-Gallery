package com.usbcameratest.jetpack_practice.pixabay;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.usbcameratest.jetpack_practice.R;

import java.util.zip.Inflater;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class GalleryAdapter extends ListAdapter<PixabayUrl, GalleryAdapter.MyViewHolder> {
    private static final String TAG = "GalleryAdapter";

    protected GalleryAdapter() {
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
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cell_gallery_card, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(view);
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PixabayUrl pixabayUrl = (PixabayUrl) myViewHolder.itemView.getTag(R.id.pixabayLargeUrl);
                Log.d(TAG, "onClick-pixabayLargeUrl: " + pixabayUrl.largeImageURL);
                NavController controller = Navigation.findNavController(v);
                Bundle bundle = new Bundle();
                bundle.putParcelable("pixaUrlObject", pixabayUrl);
                controller.navigate(R.id.action_galleryFragment_to_photoFragment, bundle);

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
        holder.shimmerLayout.startShimmerAnimation();
        holder.itemView.setTag(R.id.pixabayLargeUrl, pixabayUrl);
        Glide.with(holder.imageView)
                .load(pixabayUrl.webformatURL)
                .placeholder(R.drawable.photo)
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

    static class MyViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private ImageView imageView;
        private ShimmerLayout shimmerLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.gallery_card_view);
            imageView = itemView.findViewById(R.id.web_image);
            shimmerLayout = itemView.findViewById(R.id.cell_shimmer_layout);
        }
    }
}
