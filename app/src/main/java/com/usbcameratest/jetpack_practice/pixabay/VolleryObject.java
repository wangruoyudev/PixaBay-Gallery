package com.usbcameratest.jetpack_practice.pixabay;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class VolleryObject {
    private static RequestQueue queue;

    static synchronized RequestQueue getInstance(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context);
        }
        return queue;
    }
}
