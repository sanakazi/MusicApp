package com.musicapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    @Bind(R.id.btn_share)
    RelativeLayout btn_share;
    @Bind(R.id.rvRecentlyPlayed) RecyclerView rvRecentlyPlayed;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.nestedView)
    NestedScrollView nestedView;

    private ArrayList<HomeDetailsJson.DataList> offlineArrayList;
    private ArrayList<HomeDetailsJson.DataList> audio_itemsList=new ArrayList<>();
    private ArrayList<HomeDetailsJson.DataList> video_itemsList=new ArrayList<>();
    RvOfflineSongAdapter adapter;
    static String deviceId;
    static int userId;
    private static String TAG = LibraryFragment.class.getSimpleName();
    AppBarLayout appBarLayout;
    public String uname;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        ButterKnife.bind(this,view);
        userId = PreferencesManager.getInstance(LibraryFragment.this.getActivity()).getUserId();
        deviceId = PreferencesManager.getInstance(LibraryFragment.this.getActivity()).getDeviceId();

        if (PreferencesManager.getInstance(getActivity()).getUsername() != "") {
            uname = PreferencesManager.getInstance(getActivity()).getUsername();
            nestedView.setVisibility(View.VISIBLE);
            showRecentlyPlayedSongs();
        } else {
            getUserProfileDetailsWebService();
        }

        // getUserProfileDetailsWebService();
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
                intent5.putExtra("uname", uname);
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

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "MusicApp");
                String sAux = "\nLet me recommend you this application\n\n";
               /* sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";*/
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
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

    private void getUserProfileDetailsWebService() {

        {

            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.GET_USER_PROFILE_DATA + "UserId=" + userId + "&DeviceId=" + deviceId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.w(TAG, response);
                            try {

                                JsonParser parser = new JsonParser();
                                JsonObject o = parser.parse(response).getAsJsonObject();
                                //Log.d("Res",o.toString());

                                JsonObject c = o.getAsJsonObject().get("userDetails").getAsJsonObject();
                                //  collapsingToolbarLayout.setTitle(c.get("userName").getAsString());
                                uname = c.get("userName").getAsString();
                                PreferencesManager.getInstance(getActivity()).saveUsername(uname);
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            progressBar.setVisibility(View.GONE);
                            nestedView.setVisibility(View.VISIBLE);
                            if (getView() != null)
                                showRecentlyPlayedSongs();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);

                        }
                    }) {


            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    9000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
        }
    }


}


