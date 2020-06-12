package com.usbcameratest.jetpack_practice.pixabay;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class GalleryViewModel extends AndroidViewModel {
    private static final String TAG = "GalleryViewModel";
    private MutableLiveData<Boolean> liveDataFresh;
    private MutableLiveData<Integer> liveDataStatus;
    private MutableLiveData<List<PixabayUrl>> liveData;
    private GalleryRepository repository;
    public GalleryViewModel(@NonNull Application application) {
        super(application);
        repository = new GalleryRepository(this, application.getApplicationContext());
    }

    MutableLiveData<List<PixabayUrl>> getLiveData() {
        if (liveData == null) {
            Log.d(TAG, "getLiveData: new");
            liveData = new MutableLiveData<List<PixabayUrl>>();
            resetquery();
        }
        return liveData;
    }

    public MutableLiveData<Integer> getLiveDataStatus() {
        if (liveDataStatus == null) {
            liveDataStatus = new MutableLiveData<Integer>();
        }
        return liveDataStatus;
    }

    public MutableLiveData<Boolean> getLiveDataFresh() {
        if (liveDataFresh == null) {
            liveDataFresh = new MutableLiveData<>();
        }
        return liveDataFresh;
    }

    public void queryImageData () {
        repository.queryPixaImageList();
    }

    public void resetquery () {
        repository.resetQueryPixaImage();
    }

    void setNeedSroll (Boolean bool) {
        repository.setNeedToScrollToTop(bool);
    }
    Boolean getNeedSroll () {
        return repository.getNeedToScrollToTop();
    }
}
