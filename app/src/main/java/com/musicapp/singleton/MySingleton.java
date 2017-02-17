package com.musicapp.singleton;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.musicapp.pojos.AllGenresJson;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.utils.LruBitmapCache;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/26/2016.
 */
public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mContext;
    ArrayList <HomeDetailsJson.Categories> home_categories_list;



    private MySingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,new LruBitmapCache(LruBitmapCache.getCacheSize(mContext)));
        home_categories_list = new ArrayList<>();


   /*     mNearByGymsList= new ArrayList<>();
    */
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());

        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {

        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public ArrayList<HomeDetailsJson.Categories> getHome_categories_list() {
        return home_categories_list;
    }

    public void setHome_categories_list(ArrayList<HomeDetailsJson.Categories> home_categories_list) {
        this.home_categories_list = home_categories_list;
    }



}
