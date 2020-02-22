package com.youseforex.support_and_restincance;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonRequestQueue {
    private static SingletonRequestQueue mInstance;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private String url;

    private SingletonRequestQueue(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        url = getUrl();
    }

    public static synchronized SingletonRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SingletonRequestQueue(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }
    public String getUrl(){
        return "http://youseforex.com/";
    }
}

