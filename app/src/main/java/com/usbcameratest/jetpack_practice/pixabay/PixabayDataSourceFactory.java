package com.usbcameratest.jetpack_practice.pixabay;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

public class PixabayDataSourceFactory extends DataSource.Factory<Integer, PixabayUrl> {

    private Context context;
    public PixabayDataSourceFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public DataSource<Integer, PixabayUrl> create() {
        return new PixabayDataSource(context);
    }
}
