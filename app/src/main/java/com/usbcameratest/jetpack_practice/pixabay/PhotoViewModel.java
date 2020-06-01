package com.usbcameratest.jetpack_practice.pixabay;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewModel extends ViewModel {

    private MutableLiveData<ArrayList<PixabayUrl>> liveData;

    public MutableLiveData<ArrayList<PixabayUrl>> getLiveData() {
        if (liveData == null) {
            liveData = new MutableLiveData<ArrayList<PixabayUrl>>();
        }
        return liveData;
    }

}
