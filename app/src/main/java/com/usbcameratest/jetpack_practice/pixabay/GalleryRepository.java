package com.usbcameratest.jetpack_practice.pixabay;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GalleryRepository {
    static final int NORMAL_LOADING = 0;
    static final int LOADING_DONE = 1;
    static final int LOADING_ERROR = 2;
    private static final String TAG = "GalleryRepository";
    private RequestQueue queue;
    private String keyWord;
    private int perPage = 200;
    private int totalPage = 1;
    private int currentPage = 1;
    private Boolean needToScrollToTop = true;
    private Boolean isLoading = false;
    private Boolean isNewQuery = true;

    private GalleryViewModel viewModel;
    private static String[] imageTypeList = new String[]{"car", "cat", "dog", "pasta", "sea", "mountain", "sightseeing"};
    private static String url = "https://pixabay.com/api/?key=16774440-88977b747759b26cf6f62c30f&";

    public GalleryRepository(GalleryViewModel viewModel, Context context) {
        queue = VolleryObject.getInstance(context);
        this.viewModel = viewModel;
    }

    void resetQueryPixaImage() {
        currentPage = 1;
        totalPage = 1;
        needToScrollToTop = true;
        isLoading = false;
        isNewQuery = true;
        Random random = new Random();
        keyWord = imageTypeList[random.nextInt(imageTypeList.length)];
        Log.d(TAG, "resetQueryPixaImage: " + keyWord);
        viewModel.getLiveDataStatus().setValue(NORMAL_LOADING);
        queryPixaImageList();
    }

    void queryPixaImageList () {
        Log.d(TAG, "queryPixaImageList: " + isLoading);
        if (isLoading) {
            return;
        }
        isLoading = true;
        Log.d(TAG, "queryPixaImageList: " + currentPage + totalPage);
        if (currentPage > totalPage) {
            viewModel.getLiveDataStatus().setValue(LOADING_DONE);
            return;
        }
        String queryUrl = url + "q=" + keyWord + "&image_type=photo" + "&per_page=" + perPage + "&page=" + currentPage;
        Log.d(TAG, "queryPixaImageList-queryUrl: " + queryUrl);
        StringRequest stringRequest = new StringRequest(
            Request.Method.GET,
            queryUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Gson gson = new Gson();
                    PixabayObject pixabayObject = gson.fromJson(response, PixabayObject.class);
//                    totalPage = pixabayObject.totalHits;
                    totalPage = (pixabayObject.totalHits % perPage) != 0 ?  (pixabayObject.totalHits / perPage + 1) :
                            (pixabayObject.totalHits / perPage);
                    List<PixabayUrl> list = Arrays.asList(pixabayObject.hits);
                    if (isNewQuery) {
//                        viewModel.getLiveData().getValue().clear();
                        viewModel.getLiveData().setValue(list);
                        isNewQuery = false;
                    } else {
                        ArrayList<PixabayUrl> arrayList = new ArrayList<>();
                        arrayList.addAll(viewModel.getLiveData().getValue());
                        arrayList.addAll(list);
                        viewModel.getLiveData().setValue(arrayList);
                    }
                    currentPage++;
                    isLoading = false;
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "onErrorResponse: ", error);
//                    ArrayList<PixabayUrl> arrayList = new ArrayList<>();
//                    viewModel.getLiveData().setValue(null);
                    isLoading = false;
                    viewModel.getLiveDataStatus().setValue(LOADING_ERROR);
                }
            }
        );
        queue.add(stringRequest);
    }

    public Boolean getNeedToScrollToTop() {
        return needToScrollToTop;
    }

    public void setNeedToScrollToTop(Boolean needToScrollToTop) {
        this.needToScrollToTop = needToScrollToTop;
    }
}
