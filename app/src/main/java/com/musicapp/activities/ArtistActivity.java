package com.musicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.musicapp.R;
import com.musicapp.adapters.ArtistsAdapter;
import com.musicapp.fragments.ArtistFragment;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArtistActivity extends AppCompatActivity implements ArtistsAdapter.ArtistClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.btn_manage_artists)
    Button btn_manage_artists;
    int userId;
    int categoryId;
    String deviceId;
    private static final String TAG = ArtistActivity.class.getSimpleName();

    ArrayList<HomeDetailsJson.Categories> favDataJsonArrayList;
    ArrayList<HomeDetailsJson.DataList> dataJsonArrayList;
    HomeDetailsJson.Columns columnListJson;

    //for bottom view
    RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;
    ImageView ivUp;
    public static ImageView ivBottomPlay;
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        userId = PreferencesManager.getInstance(ArtistActivity.this).getUserId();
        deviceId = PreferencesManager.getInstance(ArtistActivity.this).getDeviceId();
       // getFavArtistService();
        events();

        ArtistFragment artistFragment = new ArtistFragment();
        addFragment(artistFragment);
       // checkSubscription();

    }

    private void events() {
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        seekView = (SeekBar) findViewById(R.id.seekView);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivUp = (ImageView) findViewById(R.id.ivUp);

        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, ArtistActivity.this);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, ArtistActivity.this);
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
                ComonHelper.updateSeekProgressTimer(seekView, ArtistActivity.this);
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                audioPlayerActivity.updateProgressBar();
            }
        });


        btn_manage_artists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArtistActivity.this, AllArtistsActivity.class);
                Bundle bund = new Bundle();
                bund.putInt("from", 1);
                intent.putExtras(bund);
                startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            ArtistFragment artistFragment = new ArtistFragment();
            addFragment(artistFragment);
        }
    }

    public void addFragment(Fragment fragment) {
     /*   Bundle bund = new Bundle();
        bund.putParcelableArrayList("categories", dataJsonArrayList);
        bund.putInt("categoryId", categoryId);
        fragment.setArguments(bund);*/

        getSupportFragmentManager().beginTransaction().add(R.id.artists_container, fragment).commit();
    }

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


    @Override
    public void onArtistClick(ArrayList<HomeDetailsJson.DataList>itemList,int catId, int typeIdValue, String typeName, String coverImage, int position) {

        if (catId == 2) {
            ComonHelper.storeOfflineStoreArtist(ArtistActivity.this,position,itemList,catId);
        }

        Intent intent = new Intent(ArtistActivity.this, HomeItemClickDetailsActivity.class);
        intent.putExtra(HomeItemClickListActivity.CAT_ID, catId);
        intent.putExtra(HomeItemClickListActivity.TYPE_ID, typeIdValue);
        intent.putExtra(HomeItemClickListActivity.TYPE_NAME, typeName);
        intent.putExtra(HomeItemClickListActivity.IMAGE_URL, coverImage);
        startActivity(intent);
    }

  /*  private void getFavArtistService() {
        favDataJsonArrayList = new ArrayList<>();
        dataJsonArrayList = new ArrayList<>();
        // columnListJson = new ArrayList<>();
        Log.w(TAG, Utility.GET_FAV_DATA + "UserId=" + userId + "&DeviceId=" + deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.GET_FAV_DATA + "UserId=" + userId + "&DeviceId=" + deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        Gson gson = new Gson();
                        HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                        if (jsonResponse.getMessage().equalsIgnoreCase("success")) {

                            favDataJsonArrayList = jsonResponse.getCategories();
                            Log.w(TAG, "no. of categories" + favDataJsonArrayList.size() + " ");

                            for (int i = 0; i < favDataJsonArrayList.size(); i++) {
                                if (favDataJsonArrayList.get(i).getCategoryId() == 2) {
                                    dataJsonArrayList = favDataJsonArrayList.get(i).getDataList();
                                    categoryId = favDataJsonArrayList.get(i).getCategoryId();
                                    Log.w(TAG, favDataJsonArrayList.get(i).getCategoryName() + " and size is = " + dataJsonArrayList.size() + " ");
                                    for (int j = 0; j < dataJsonArrayList.size(); j++) {
                                        columnListJson = dataJsonArrayList.get(j).getColumns();
                                        Log.w(TAG, "Song Name  = " + columnListJson.getTypeName());
                                    }

                                }

                            }

                            Log.w(TAG, dataJsonArrayList.size() + " ");
                            ArtistFragment artistFragment = new ArtistFragment();
                            addFragment(artistFragment);
                            //setAdapter(dataJsonArrayList,categoryId);
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(ArtistActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });

        MySingleton.getInstance(ArtistActivity.this).getRequestQueue().add(stringRequest);
    }*/


    public void checkSubscription() {

        progressBar.setVisibility(View.VISIBLE);

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("UserId", PreferencesManager.getInstance(ArtistActivity.this).getUserId());
            jsonParams.put("DeviceId", PreferencesManager.getInstance(ArtistActivity.this).getDeviceId());

        }
        catch (JSONException e) {e.printStackTrace();}

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Utility.CHECK_SUBSCRIPTION_URL, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)

                    {
                        progressBar.setVisibility(View.GONE);
                        try {
                            message= response.getString("message");
                            Log.w(TAG ,message.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                          progressBar.setVisibility(View.GONE);

                        System.out.println("Volley submit error" + error);
                        if (null != error.networkResponse) {
                            System.out.println("Volley submit error" + error);
                        }
                    }
                });



        MySingleton.getInstance(ArtistActivity.this).getRequestQueue().add(request);
        if(message.equalsIgnoreCase("Sucess"))
        {
           // getFavArtistService();
        }
        Log.w(TAG ,message);

    }

    @Override
    public void onBackPressed() {
        if (AudioPlayerActivity.isPlaying) {
            if (ComonHelper.timer != null) {
                ComonHelper.timer.cancel();
            }

            if (AudioPlayerActivity.timer != null) {
                AudioPlayerActivity.timer.cancel();
            }

        }
        super.onBackPressed();
    }
}
