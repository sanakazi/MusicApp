package com.musicapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicapp.R;
import com.musicapp.activities.ArtistActivity;
import com.musicapp.activities.GenreActivity;
import com.musicapp.activities.PlayListActivity;
import com.musicapp.activities.SettingsActivity;
import com.musicapp.activities.SongsLikedActivity;
import com.musicapp.activities.UserProfileActivity;
import com.musicapp.adapters.RvOfflineSongAdapter;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by SanaKazi on 1/16/2017.
 */
public class LibraryFragment  extends Fragment {
    @Bind(R.id.btn_settings) RelativeLayout btn_settings;
    @Bind(R.id.btn_playlist) RelativeLayout btn_playlist;
    @Bind(R.id.btn_artists) RelativeLayout btn_artists;
    @Bind(R.id.btn_genre) RelativeLayout btn_genre;
    @Bind(R.id.btn_profile) RelativeLayout btn_profile;
    @Bind(R.id.btn_songs) RelativeLayout btn_songs;
    @Bind(R.id.rvRecentlyPlayed) RecyclerView rvRecentlyPlayed;

    private ArrayList<HomeDetailsJson.DataList> offlineArrayList;
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList=new ArrayList<>();
    private ArrayList<HomeDetailsJson.DataList> video_itemsList=new ArrayList<>();
    RvOfflineSongAdapter adapter;
    static String deviceId;
    static int userId;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        ButterKnife.bind(this,view);
        userId = PreferencesManager.getInstance(LibraryFragment.this.getActivity()).getUserId();
        deviceId = PreferencesManager.getInstance(LibraryFragment.this.getActivity()).getDeviceId();
        showRecentlyPlayedSongs();

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
       if (adapter.dialog!=null){
           adapter.dialog.dismiss();
       }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent1);
            }
        });
        btn_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2= new Intent(getActivity(), PlayListActivity.class);
                startActivity(intent2);
            }
        });
        btn_artists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(getActivity(), ArtistActivity.class);
                startActivity(intent3);
            }
        });

        btn_genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(getActivity(), GenreActivity.class);
                startActivity(intent4);
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent5 = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent5);
            }
        });

        btn_songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6=new Intent(getActivity(), SongsLikedActivity.class);
                startActivity(intent6);
            }
        });
    }


    private void showRecentlyPlayedSongs(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(LibraryFragment.this.getActivity().getApplicationContext());
        rvRecentlyPlayed.setLayoutManager(mLayoutManager);
        /* getting list from sharedPreferance*/
        Gson gson = new Gson();
        String jsonOfflineSong = PreferencesManager.getInstance(LibraryFragment.this.getActivity()).getOfflineSong();
        System.out.println("SONGSSSSSSSS"+jsonOfflineSong);
       Type type = new TypeToken<ArrayList<HomeDetailsJson.DataList>>() {}.getType();
        offlineArrayList=gson.fromJson(jsonOfflineSong, type);

        //reverse array to display recently played at top
        Collections.reverse(offlineArrayList);

        for (int i=0;i<offlineArrayList.size();i++){
            if (offlineArrayList.get(i).getColumns().getSongTypeId() == 1) {
                //audio list
                audio_itemsList.add(offlineArrayList.get(i));
            } else if (offlineArrayList.get(i).getColumns().getSongTypeId() == 2) {
                //video list
                video_itemsList.add(offlineArrayList.get(i));

            }
        }
        adapter = new RvOfflineSongAdapter(offlineArrayList,audio_itemsList,video_itemsList, LibraryFragment.this.getActivity());
        rvRecentlyPlayed.setAdapter(adapter);
    }




}


