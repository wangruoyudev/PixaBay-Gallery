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
//    private static final String TAG = "GalleryRepository";
//    private RequestQueue queue;
//    private GalleryViewModel viewModel;
//    private static String[] imageTypeList = new String[]{"car", "cat", "dog", "China", "sea", "mountain", "sightseeing"};
//    private static String url = "https://pixabay.com/api/?key=16774440-88977b747759b26cf6f62c30f&";
//
//    public GalleryRepository(GalleryViewModel viewModel, Context context) {
//        queue = VolleryObject.getInstance(context);
//        this.viewModel = viewModel;
//    }
//
//    void queryPixaImageList () {
//        Random random = new Random();
//        String queryUrl = url + "q=" + imageTypeList[random.nextInt(7)] + "&image_type=photo" + "&per_page=100";
//        Log.d(TAG, "queryPixaImageList-queryUrl: " + queryUrl);
//        StringRequest stringRequest = new StringRequest(
//            Request.Method.GET,
//            queryUrl,
//            new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d(TAG, "onResponse: " + response);
//                    Gson gson = new Gson();
//                    PixabayObject pixabayObject = gson.fromJson(response, PixabayObject.class);
//                    List<PixabayUrl> list = Arrays.asList(pixabayObject.hits);
//                    viewModel.getLiveData().setValue(list);
//                }
//            },
//            new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.e(TAG, "onErrorResponse: ", error);
//                    viewModel.getLiveData().setValue(null);
//                }
//            }
//        );
//        queue.add(stringRequest);
//    }
}
