package com.musicapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.activities.AppIntroActivityNew;
import com.musicapp.adapters.HomeDataAdapter;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.others.Utility;
import com.musicapp.singleton.PreferencesManager;
import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/21/2016.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    RecyclerView recyclerView;
    HomeDataAdapter myAdapter;
    ProgressBar progressBar;
    private static final String TAG = HomeFragment.class.getSimpleName();
    ArrayList<HomeDetailsJson.Categories> main_categories_list = new ArrayList<>();
    ArrayList<HomeDetailsJson.Categories> main_categories_list_containing_elements = new ArrayList<>();
    ArrayList<HomeDetailsJson.DataList> detail_categories_list = new ArrayList<>();
    ArrayList<HomeDetailsJson.DataList> audio_detail_categories_list = new ArrayList<>();
    ArrayList<HomeDetailsJson.DataList> video_detail_categories_list = new ArrayList<>();
    int userId;
    String deviceId;
    SwipeRefreshLayout refreshLayout;//changes by amol
    public boolean refreshmode = false;//changes by amol
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);    //changes by amol
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        refreshLayout.setOnRefreshListener(this);    //changes by amol
        refreshLayout.setColorSchemeResources(R.color.header_bg_start, R.color.header_bg_end, R.color.home_icons_yellow); //changes by amol
        userId = PreferencesManager.getInstance(getActivity()).getUserId();
        deviceId = PreferencesManager.getInstance(getActivity()).getDeviceId();

        maincategory_webservice();

        return view;
    }

    private void maincategory_webservice() {
        refreshLayout.setRefreshing(true); //changes by amol
        main_categories_list_containing_elements = new ArrayList<>();
        Log.w(TAG, "url is " + Utility.home2 + "UserId=" + userId + "&DeviceId=" + deviceId);
        if (refreshmode) //changes by amol
        {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.home2 + "UserId=" + userId + "&DeviceId=" + deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        main_categories_list.clear();
                        main_categories_list_containing_elements.clear();
                        detail_categories_list.clear();
                        audio_detail_categories_list.clear();
                        video_detail_categories_list.clear();


                        Gson gson = new Gson();
                        HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                        System.out.println(response);
                        if (jsonResponse.getMessage().equals("success")) {
                            main_categories_list = jsonResponse.getCategories();
                            ArrayList<Integer> valuesToRemove = new ArrayList<>();

                            for (int i = 0; i < main_categories_list.size(); i++) {
                                if (main_categories_list.get(i).getDataList().isEmpty()) {


                                } else {
                                    main_categories_list_containing_elements.add(jsonResponse.getCategories().get(i));

                                    //region for all songs

                                    /*if (main_categories_list.get(i).getCategoryId() == 9) {*/
                                    detail_categories_list = main_categories_list.get(i).getDataList();
                                    for (int j = 0; j < detail_categories_list.size(); j++) {

                                        if (detail_categories_list.get(j).getColumns().getSongTypeId() == 1) {

                                            //audio list
                                            audio_detail_categories_list.add(main_categories_list.get(i).getDataList().get(j));

                                        } else if (detail_categories_list.get(j).getColumns().getSongTypeId() == 2) {

                                            //video list
                                            video_detail_categories_list.add(main_categories_list.get(i).getDataList().get(j));

                                        }
                                    }


                                   /* }*/
                                    //endregion
                                }

                            }


                            myAdapter = new HomeDataAdapter(getActivity(), main_categories_list_containing_elements, audio_detail_categories_list, video_detail_categories_list);
                            //   MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list_containing_elements);
                            recyclerView.setAdapter(myAdapter);


                        } else if (jsonResponse.getMessage().equalsIgnoreCase("Please Redirect to login page")) {
                            PreferencesManager.getInstance(getActivity()).clearUserPreferences();
                            Intent i = new Intent(getActivity(), AppIntroActivityNew.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finish();
                        } else if (jsonResponse.getMessage().equalsIgnoreCase("Not Subscribed User")) {
                           /* Intent i = new Intent(getActivity(),SubscriptionActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            getActivity().finish();*/
                            Toast.makeText(getActivity(), "Your subscription has expired", Toast.LENGTH_SHORT).show();
                        }
                        //         myAdapter.notifyDataSetChanged();//changes by amol

                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false); //changes by amol
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //         Toast.makeText(HomeFragment.this.getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        refreshLayout.setRefreshing(false); //changes by amol

                    }
                });


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }

    //changes by amol
    @Override
    public void onRefresh() {
        refreshmode = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                maincategory_webservice();
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
       /* myAdapter = new HomeDataAdapter(getActivity(), main_categories_list_containing_elements, audio_detail_categories_list, video_detail_categories_list);
        myAdapter.notifyDataSetChanged();*/
    }
}

