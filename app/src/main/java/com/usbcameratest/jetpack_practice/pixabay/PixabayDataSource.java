package com.usbcameratest.jetpack_practice.pixabay;

import android.content.Context;
import android.hardware.camera2.CaptureResult;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PixabayDataSource extends PageKeyedDataSource<Integer, PixabayUrl> {
    private static final String TAG = "PixabayDataSource";
    private Context context;
    private GalleryViewModel viewModel;
    private String keyWord;
    private int per_page = 50;
    private RequestQueue queue;
    private static String[] imageTypeList = new String[]{"car", "cat", "dog", "China", "sea", "mountain", "sightseeing"};
    private static String url = "https://pixabay.com/api/?key=16774440-88977b747759b26cf6f62c30f&";
    static final int NET_LOADING = 0;
    static final int DONE_LOADING = 1;
    static final int ERROR_LOADING = 2;
    static final int INIT_LOAD = 3;
    static final int INIT_LOAD_DONE = 4;

    private LoadInitialParams<Integer> roolBackparams;
    private LoadInitialCallback<Integer, PixabayUrl> roolBackCallback;

    private LoadParams<Integer> roolBackAfterparams;
    private LoadCallback<Integer, PixabayUrl> roolBackAfterCallback;

    PixabayDataSource(Context context, GalleryViewModel viewModel) {
        this.context = context;
        queue = VolleryObject.getInstance(context);
        this.viewModel = viewModel;
    }
    
    void reTry () {
        Log.d(TAG, "reTry: 11111");
        if (roolBackparams != null || roolBackCallback != null) {
            loadInitial(roolBackparams, roolBackCallback);
        } else {
            loadAfter(roolBackAfterparams, roolBackAfterCallback);
        }
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, PixabayUrl> callback) {
        Random random = new Random();
        keyWord = imageTypeList[random.nextInt(imageTypeList.length)];
        String queryUrl = url + "q=" + keyWord + "&image_type=photo" + "&per_page=" + per_page + "&page=1";
        Log.d(TAG, "queryPixaImageList-queryUrl: " + queryUrl);
        viewModel.getNetLiveData().postValue(INIT_LOAD);
        roolBackparams = null;
        roolBackCallback = null;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                queryUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        viewModel.getNetLiveData().postValue(INIT_LOAD_DONE);
                        Log.d(TAG, "onResponse: " + response);
                        Gson gson = new Gson();
                        PixabayObject pixabayObject = gson.fromJson(response, PixabayObject.class);
                        List<PixabayUrl> list = Arrays.asList(pixabayObject.hits);
                        callback.onResult(list, null, 2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        roolBackparams = params;
                        roolBackCallback = callback;
                        viewModel.getNetLiveData().postValue(ERROR_LOADING);
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                }
        );
        queue.add(stringRequest);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, PixabayUrl> callback) {

    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, PixabayUrl> callback) {
        String queryUrl = url + "q=" + keyWord + "&image_type=photo" + "&per_page=" + per_page + "&page=" + params.key;
        Log.d(TAG, "queryPixaImageList-queryUrl: " + queryUrl);
        viewModel.getNetLiveData().postValue(NET_LOADING);
        roolBackAfterparams = null;
        roolBackAfterCallback = null;
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                queryUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        Gson gson = new Gson();
                        PixabayObject pixabayObject = gson.fromJson(response, PixabayObject.class);
                        List<PixabayUrl> list = Arrays.asList(pixabayObject.hits);
                        callback.onResult(list, params.key + 1);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        roolBackAfterparams = params;
                        roolBackAfterCallback = callback;
                        if (error != null && error.toString().equals("com.android.volley.ClientError")) {
                            viewModel.getNetLiveData().postValue(DONE_LOADING);
                        } else {
                            viewModel.getNetLiveData().postValue(ERROR_LOADING);
                        }
                        Log.e(TAG, "onErrorResponse: ", error);
                        Log.d(TAG, "onErrorResponse111: " + error.toString());
                    }
                }
        );
        queue.add(stringRequest);
    }
}
