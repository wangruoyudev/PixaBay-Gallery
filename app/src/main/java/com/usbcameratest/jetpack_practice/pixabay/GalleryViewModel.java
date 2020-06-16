package com.usbcameratest.jetpack_practice.pixabay;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {

    private static final String TAG = "GalleryViewModel";

    private LiveData<PagedList<PixabayUrl>> pagedListLiveData;
    private MutableLiveData<Integer> netLiveData;
    private PixabayDataSourceFactory pixabayDataSourceFactory;


    public GalleryViewModel(@NonNull Application application) {
        super(application);
        pixabayDataSourceFactory = new PixabayDataSourceFactory(application, this);
        pagedListLiveData = new LivePagedListBuilder<>(pixabayDataSourceFactory, 1)
                .build();
    }

    LiveData<PagedList<PixabayUrl>> getPagedListLiveData() {
        return pagedListLiveData;
    }

    public MutableLiveData<Integer> getNetLiveData() {
        if (netLiveData == null) {
            netLiveData = new MutableLiveData<>();
        }
        return netLiveData;
    }

    void reTry () {
        pixabayDataSourceFactory.getPixabayDataSource()
                .getValue()
                .reTry();
    }

    void resetQuery () {
        pagedListLiveData.getValue().getDataSource().invalidate();
    }
}
