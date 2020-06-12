package com.usbcameratest.jetpack_practice.pixabay;

import android.content.Context;
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
    private String keyWord;
    private RequestQueue queue;
    private static String[] imageTypeList = new String[]{"car", "cat", "dog", "China", "sea", "mountain", "sightseeing"};
    private static String url = "https://pixabay.com/api/?key=16774440-88977b747759b26cf6f62c30f&";

    public PixabayDataSource(Context context) {
        this.context = context;
        queue = VolleryObject.getInstance(context);
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, PixabayUrl> callback) {
        Random random = new Random();
        keyWord = imageTypeList[random.nextInt(imageTypeList.length)];
        String queryUrl = url + "q=" + keyWord + "&image_type=photo" + "&per_page=50" + "&page=1";
        Log.d(TAG, "queryPixaImageList-queryUrl: " + queryUrl);
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
                        callback.onResult(list, null, 2);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        String queryUrl = url + "q=" + keyWord + "&image_type=photo" + "&per_page=50" + "&page=" + params.key;
        Log.d(TAG, "queryPixaImageList-queryUrl: " + queryUrl);
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
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                }
        );
        queue.add(stringRequest);
    }
}
