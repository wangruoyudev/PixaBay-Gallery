package com.usbcameratest.jetpack_practice.pixabay;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class PixabayDataSourceFactory extends DataSource.Factory<Integer, PixabayUrl> {
    private GalleryViewModel viewModel;
    private Context context;
    private MutableLiveData<PixabayDataSource> pixabayDataSource;
    public PixabayDataSourceFactory(Context context, GalleryViewModel viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public DataSource<Integer, PixabayUrl> create() {
        PixabayDataSource pixabayDataSource =  new PixabayDataSource(context, viewModel);
        getPixabayDataSource().postValue(pixabayDataSource);
        return pixabayDataSource;
    }

    public MutableLiveData<PixabayDataSource> getPixabayDataSource() {
        if (pixabayDataSource == null) {
            pixabayDataSource = new MutableLiveData<>();
        }
        return pixabayDataSource;
    }
}
