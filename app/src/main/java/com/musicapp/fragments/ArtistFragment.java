package com.musicapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.ArtistsAdapter;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by sanakazi on 1/17/2017.
 */
public class ArtistFragment extends Fragment {

    int userId;
    String deviceId;
    private static final String TAG=ArtistFragment.class.getSimpleName();
    ProgressBar progressBar;
    RecyclerView artists_recyclerView;
    ArtistsAdapter myAdapter;
    int categoryId;

    
    ArrayList<HomeDetailsJson.Categories> favDataJsonArrayList;
    ArrayList<HomeDetailsJson.DataList> dataJsonArrayList;
    HomeDetailsJson.Columns columnListJson;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        ButterKnife.bind(this, view);
        artists_recyclerView = (RecyclerView)view.findViewById(R.id.artists_container);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        artists_recyclerView.setLayoutManager(llm);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        userId = PreferencesManager.getInstance(getActivity()).getUserId();
        deviceId = PreferencesManager.getInstance(getActivity()).getDeviceId();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAllArtistService();
        /*Bundle bundle = getArguments();
        dataJsonArrayList =bundle.getParcelableArrayList("categories");
        categoryId=bundle.getInt("categoryId",0);
        setAdapter(dataJsonArrayList,categoryId);*/
    }



    private void getAllArtistService()
    {
        favDataJsonArrayList = new ArrayList<>();
        dataJsonArrayList= new ArrayList<>();
       // columnListJson = new ArrayList<>();
        Log.w(TAG, Utility.GET_FAV_DATA+"UserId="+ userId+"&DeviceId="+deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.GET_FAV_DATA+"UserId="+ userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        Gson gson = new Gson();
                        HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                        if (jsonResponse.getMessage().equalsIgnoreCase("success")) {

                            favDataJsonArrayList=jsonResponse.getCategories();
                            Log.w(TAG,"no. of categories"+ favDataJsonArrayList.size()+ " ");

                            for (int i = 0; i<favDataJsonArrayList.size(); i++)
                            {
                                if(favDataJsonArrayList.get(i).getCategoryId()==2)
                                {
                                    dataJsonArrayList = favDataJsonArrayList.get(i).getDataList();
                                    categoryId=favDataJsonArrayList.get(i).getCategoryId();
                                    Log.w(TAG,favDataJsonArrayList.get(i).getCategoryName()+ " and size is = " + dataJsonArrayList.size()+ " ");
                                    for (int j=0; j <dataJsonArrayList.size();j++)
                                    {
                                       columnListJson = dataJsonArrayList.get(j).getColumns();
                                        Log.w(TAG,"Song Name  = " + columnListJson.getTypeName());
                                    }

                                }

                            }
                            //  MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list_containing_elements);
                            Log.w(TAG, dataJsonArrayList.size()+ " ");
                          setAdapter(dataJsonArrayList,categoryId);
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }){


        };

        MySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }

    private void setAdapter(ArrayList <HomeDetailsJson.DataList> dataJsonArrayList , int categoryId)
    {
        myAdapter = new ArtistsAdapter(getActivity(), dataJsonArrayList,categoryId);
        artists_recyclerView.setAdapter(myAdapter);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
