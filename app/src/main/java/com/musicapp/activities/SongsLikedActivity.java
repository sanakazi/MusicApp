package com.musicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.SongsLikedAdapter;
import com.musicapp.fragments.ArtistFragment;
import com.musicapp.others.Utility;

import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SongsLikedActivity extends AppCompatActivity {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.songs_liked_recyclerView) RecyclerView songs_liked_recyclerView;
    int userId;
    int categoryId;
    String deviceId;
    private static final String TAG=SongsLikedActivity.class.getSimpleName();
    ArrayList<HomeDetailsJson.Categories> HomeCategoryJsonArrayList;
    ArrayList<HomeDetailsJson.DataList> dataJsonArrayList;
    ArrayList <HomeDetailsJson.DataList> audio_detail_categories_list=new ArrayList<>();
    ArrayList <HomeDetailsJson.DataList> video_detail_categories_list=new ArrayList<>();
    HomeDetailsJson.Columns columnListJson;
    SongsLikedAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_liked);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        songs_liked_recyclerView = (RecyclerView)findViewById(R.id.songs_liked_recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(SongsLikedActivity.this);
        songs_liked_recyclerView.setLayoutManager(llm);
        userId = PreferencesManager.getInstance(SongsLikedActivity.this).getUserId();
        deviceId = PreferencesManager.getInstance(SongsLikedActivity.this).getDeviceId();

        getFavArtistService();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
      if(myAdapter.dialog!=null){
          myAdapter.dialog.dismiss();
      }
    }

    private void getFavArtistService()
    {
        HomeCategoryJsonArrayList = new ArrayList<>();
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

                            HomeCategoryJsonArrayList=jsonResponse.getCategories();
                            Log.w(TAG,"no. of categories"+ HomeCategoryJsonArrayList.size()+ " ");

                            for (int i = 0; i<HomeCategoryJsonArrayList.size(); i++)
                            {
                                if(HomeCategoryJsonArrayList.get(i).getCategoryId()==4)
                                {
                                    dataJsonArrayList = HomeCategoryJsonArrayList.get(i).getDataList();
                                    categoryId=HomeCategoryJsonArrayList.get(i).getCategoryId();
                                    Log.w(TAG,HomeCategoryJsonArrayList.get(i).getCategoryName()+ " and size is = " + dataJsonArrayList.size()+ " ");
                                    for (int j=0; j <dataJsonArrayList.size();j++)
                                    {
                                        columnListJson = dataJsonArrayList.get(j).getColumns();
                                        Log.w(TAG,"Song Name  = " + columnListJson.getTypeName());

                                        if (dataJsonArrayList.get(j).getColumns().getSongTypeId() == 1) {

                                            //audio list
                                            audio_detail_categories_list.add(HomeCategoryJsonArrayList.get(i).getDataList().get(j));
                                            Log.w(TAG, "in audio list " + audio_detail_categories_list.size());
                                        } else if (dataJsonArrayList.get(j).getColumns().getSongTypeId() == 2) {

                                            //video list
                                            video_detail_categories_list.add(HomeCategoryJsonArrayList.get(i).getDataList().get(j));
                                            Log.w(TAG, "IN VIDEO LIST " + video_detail_categories_list.size());
                                        }



                                    }

                                }

                            }

                            Log.w(TAG, dataJsonArrayList.size()+ " ");
                            setAdapter(dataJsonArrayList,categoryId);
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                     //   Toast.makeText(SongsLikedActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        MySingleton.getInstance(SongsLikedActivity.this).getRequestQueue().add(stringRequest);
    }

    private void setAdapter(ArrayList <HomeDetailsJson.DataList> dataJsonArrayList ,int categoryId)
    {
        myAdapter = new SongsLikedAdapter(this, dataJsonArrayList,categoryId,video_detail_categories_list,audio_detail_categories_list);
        songs_liked_recyclerView.setAdapter(myAdapter);
    }

  /*  @Override
    public void onSongClick(int i,int catId, int typeId, String typeName, String coverImage) {
        //play the song

    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                //  overridePendingTransition(0, R.anim.push_right);
                break;
        }
        return true;
    }
}
