package com.musicapp.activities;

/**
 * Created by PalseeTrivedi on 1/20/2017.
 */

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.musicapp.R;

import com.musicapp.adapters.RvRecentlyPlayedArtist;
import com.musicapp.custom.CircularNetworkImageView;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.OfflineArtistItem;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    TextView tvRvTitle;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    RvRecentlyPlayedArtist recentlyPlayedAdapter;
    ArrayList<OfflineArtistItem> list = new ArrayList<>();
    Toolbar toolbar;
    CircularNetworkImageView circleImageView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    UserProfileActivity userProfileActivity;
    int userId;
    String deviceId;
    private static String TAG = UserProfileActivity.class.getSimpleName();
    AppBarLayout appBarLayout;
    //for bottom player
    RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;
    ImageView ivUp;
    public static ImageView ivBottomPlay;
    Button playLists;
    public String uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userProfileActivity = this;
        intialize();
        userId = PreferencesManager.getInstance(UserProfileActivity.this).getUserId();
        deviceId = PreferencesManager.getInstance(UserProfileActivity.this).getDeviceId();
        getUserProfileDetailsWebService();
        collapsingToolbarLayout.setExpandedTitleGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        collapsingToolbarLayout.setTitle(uname);
    }

    private void intialize() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvRvTitle = (TextView) findViewById(R.id.tvRvTitle);
        circleImageView = (CircularNetworkImageView) findViewById(R.id.profile_image);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        playLists = (Button) findViewById(R.id.playlists);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        collapsingToolbarLayout.setTitle("Music App");
        linearLayoutManager = new LinearLayoutManager(userProfileActivity);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // PLayLists Button Click

        playLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(userProfileActivity, PlayListActivity.class);
                startActivity(i);
                finish();
            }
        });


        /* getting list from sharedPreferance*/
        Gson gson = new Gson();
        String jsonOfflineArtist = PreferencesManager.getInstance(userProfileActivity).getRecentlyPlayedArtist();
        System.out.println("SONGSSSSSSSS" + jsonOfflineArtist);
        Type type = new TypeToken<ArrayList<OfflineArtistItem>>() {
        }.getType();
        list = gson.fromJson(jsonOfflineArtist, type);
        System.out.println();

        //reverse array to display recently played at top
        Collections.reverse(list);
        if (list.size() != 0) {
            recentlyPlayedAdapter = new RvRecentlyPlayedArtist(list, userProfileActivity);
            recyclerView.setAdapter(recentlyPlayedAdapter);
        } else {
            tvRvTitle.setVisibility(View.GONE);
        }

        bottomPlayerListner();


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
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
                                uname=c.get("userName").getAsString();
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                              progressBar.setVisibility(View.GONE);
                            collapsingToolbarLayout.setTitle(uname);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UserProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                             progressBar.setVisibility(View.GONE);
                        }
                    }) {


            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    9000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            MySingleton.getInstance(UserProfileActivity.this).getRequestQueue().add(stringRequest);
        }
    }

    private void bottomPlayerListner() {
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        seekView = (SeekBar) findViewById(R.id.seekView);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivUp = (ImageView) findViewById(R.id.ivUp);


        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, UserProfileActivity.this);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, UserProfileActivity.this);
            } else {
                bottomPlayerView.setVisibility(View.GONE);
            }
        }


        seekView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BackgroundSoundService.mPlayer.seekTo(seekView.getProgress());
                ComonHelper.timer.cancel();
                ComonHelper.updateSeekProgressTimer(seekView, UserProfileActivity.this);
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                audioPlayerActivity.updateProgressBar();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }
}
