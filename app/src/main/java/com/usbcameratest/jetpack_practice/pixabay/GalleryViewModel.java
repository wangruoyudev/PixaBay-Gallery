package com.usbcameratest.jetpack_practice.pixabay;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private static final String TAG = "GalleryViewModel";

    private LiveData<PagedList<PixabayUrl>> pagedListLiveData;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        PixabayDataSourceFactory pixabayDataSourceFactory = new PixabayDataSourceFactory(application);
        pagedListLiveData = new LivePagedListBuilder<>(pixabayDataSourceFactory, 1)
                .build();
    }

    LiveData<PagedList<PixabayUrl>> getPagedListLiveData() {
        return pagedListLiveData;
    }

    void resetQuery () {
        pagedListLiveData.getValue().getDataSource().invalidate();
    }
}
