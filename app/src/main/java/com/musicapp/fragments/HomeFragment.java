package com.musicapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.activities.AllGenreStartActivity;
import com.musicapp.activities.LoginActivity;
import com.musicapp.activities.MainActivity;
import com.musicapp.activities.SubscriptionActivity;
import com.musicapp.adapters.HomeDataAdapter;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.PreferencesManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/21/2016.
 */
public class HomeFragment extends Fragment  {


    RecyclerView recyclerView;
    HomeDataAdapter myAdapter;
    ProgressBar progressBar;
    private static final String TAG=HomeFragment.class.getSimpleName();
    ArrayList <HomeDetailsJson.Categories> main_categories_list;
    ArrayList<HomeDetailsJson.Categories> main_categories_list_containing_elements;
    ArrayList <HomeDetailsJson.DataList> detail_categories_list;
    ArrayList <HomeDetailsJson.DataList> audio_detail_categories_list=new ArrayList<>();
    ArrayList <HomeDetailsJson.DataList> video_detail_categories_list=new ArrayList<>();
    int userId;
    String deviceId;



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
       // main_categories_list.clear();
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        userId = PreferencesManager.getInstance(getActivity()).getUserId();
        deviceId = PreferencesManager.getInstance(getActivity()).getDeviceId();

        //Get Category list from singleton, if it is empty get new list from server
      /*  main_categories_list = MySingleton.getInstance(getActivity()).getHome_categories_list();
        if (main_categories_list.isEmpty()) {
            maincategory_webservice();
        } else {
            myAdapter = new HomeDataAdapter(getActivity(), main_categories_list);
            recyclerView.setAdapter(myAdapter);
        }*/

        maincategory_webservice();
        return view;
    }

    private void maincategory_webservice()
    {
        main_categories_list_containing_elements = new ArrayList<>();
        Log.w(TAG, "url is " + Utility.home2+"UserId="+userId+"&DeviceId="+deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.home2+"UserId="+userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, "response is" + response);


                        Gson gson = new Gson();
                        HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                        if (jsonResponse.getMessage().equals("success")) {
                            main_categories_list = jsonResponse.getCategories();
                            ArrayList<Integer> valuesToRemove = new ArrayList<>();

                            for (int i = 0; i<main_categories_list.size(); i++)
                            {
                                if(main_categories_list.get(i).getDataList().isEmpty())
                                {
                                 Log.w(TAG, "Values for Child are empty " );

                                }else
                                {
                                    main_categories_list_containing_elements.add(jsonResponse.getCategories().get(i));

                                    //region for all songs

                                    if(main_categories_list.get(i).getCategoryId()==9)
                                    {
                                        detail_categories_list =  main_categories_list.get(i).getDataList();
                                        for (int j = 0; j<detail_categories_list.size(); j++)
                                        {

                                            if (detail_categories_list.get(j).getColumns().getSongTypeId() == 1) {

                                                //audio list
                                                audio_detail_categories_list.add(main_categories_list.get(i).getDataList().get(j));
                                                Log.w(TAG, "in audio list " + audio_detail_categories_list.size());
                                            } else if (detail_categories_list.get(j).getColumns().getSongTypeId() == 2) {

                                                //video list
                                                video_detail_categories_list.add(main_categories_list.get(i).getDataList().get(j));
                                                Log.w(TAG, "IN VIDEO LIST " + video_detail_categories_list.size());
                                            }
                                        }


                                    }
                                    //endregion
                                }

                            }


                            myAdapter = new HomeDataAdapter(getActivity(), main_categories_list_containing_elements,audio_detail_categories_list,video_detail_categories_list);
                         //   MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list_containing_elements);
                            recyclerView.setAdapter(myAdapter);
                        }
                        else if(jsonResponse.getMessage().equalsIgnoreCase("Please Redirect to login page"))
                        {
                            Intent i = new Intent(getActivity(), LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finish();
                        }
                        else if(jsonResponse.getMessage().equalsIgnoreCase("Not Subscribed User"))
                        {
                           /* Intent i = new Intent(getActivity(),SubscriptionActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finish();*/
                            Toast.makeText(getActivity(),"Your subscription has expired" ,Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
               //         Toast.makeText(HomeFragment.this.getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                })




                //region volleycache
        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new String(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }


        };
        //endregion


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }

  /*  @Override
    public void onCategoryClicked1(int typeId, int pos) {

        int categoryId= main_categories_list.get(pos).getCategoryId();
        Intent intent = new Intent(getActivity(),HomeItemClickListActivity.class);
        intent.putExtra(HomeItemClickListActivity.CAT_ID,categoryId);
        intent.putExtra(HomeItemClickListActivity.TYPE_ID,typeId);
        startActivity(intent);

    }*/




}

