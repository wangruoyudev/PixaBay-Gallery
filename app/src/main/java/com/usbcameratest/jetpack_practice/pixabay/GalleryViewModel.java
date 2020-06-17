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
import java.util.Objects;

public class GalleryViewModel extends AndroidViewModel {

    private static final String TAG = "GalleryViewModel";

    private LiveData<PagedList<PixabayUrl>> pagedListLiveData;
    private MutableLiveData<Integer> netLiveData;
    private PixabayDataSourceFactory pixabayDataSourceFactory;
    private String searchKeyWord = "";


    public GalleryViewModel(@NonNull Application application) {
        super(application);
        pixabayDataSourceFactory = new PixabayDataSourceFactory(application, this);
        pagedListLiveData = new LivePagedListBuilder<>(pixabayDataSourceFactory, 1)
                .build();
    }

    public void setSearchKeyWord(String searchKeyWord) {
        Log.d(TAG, "setSearchKeyWord: " + searchKeyWord);
        this.searchKeyWord = searchKeyWord;
    }

    public String getSearchKeyWord() {
        return searchKeyWord;
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
        Objects.requireNonNull(pagedListLiveData.getValue()).getDataSource().invalidate();
    }
    void searchQuery (String keyWord) {
        setSearchKeyWord(keyWord);
        Objects.requireNonNull(pagedListLiveData.getValue()).getDataSource().invalidate();
    }
}
