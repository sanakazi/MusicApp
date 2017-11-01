package com.musicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.HomeItemClickListDataAdapter;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import java.util.ArrayList;

public class HomeItemClickListActivity extends AppCompatActivity {

  //  private static final String url = "http://192.168.168.5:9000/WebServices/AppServices.svc/GetSongsByCategoryId?categoryId=2&typeId=3";
 // private static final String url = "http://192.168.168.5:9000/WebServices/AppServices.svc/GetSongsByCategoryId?categoryId=";
  private static final String url = "http://boxofficecapsule.com:91/WebServices/AppServices.svc/GetSongsByCategoryId_V2?categoryId=";
    public static final String CAT_ID= "categoryId";
    public static final String TYPE_ID= "typeId";
    public static final String TYPE_NAME= "type_name";
    public static final String IMAGE_URL = "image_url";

    private static final String TAG=HomeItemClickListActivity.class.getSimpleName();
    ArrayList <HomeDetailsJson.Categories> main_categories_list;
    ArrayList <HomeDetailsJson.Categories> main_categories_list_containing_elements;
    ArrayList <HomeDetailsJson.DataList> detail_categories_list;
    ArrayList <HomeDetailsJson.DataList> audio_detail_categories_list=new ArrayList<>();
    ArrayList <HomeDetailsJson.DataList> video_detail_categories_list=new ArrayList<>();

    RecyclerView recyclerView;
   // ImageView backdrop;
    HomeItemClickListDataAdapter myAdapter;
    ProgressBar progressBar;
    int cat_id,type_id;
    int cat_id_forAllSongs, type_id_forAllSongs,subCategoryId;
    String type_name;
    String coverImage;
    private ImageLoader mImageLoader;
    int userId;
    String deviceId;

    //bottom player
    public static RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;
    ImageView ivUp;
    public static ImageView ivBottomPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_item_click_list);
        userId = PreferencesManager.getInstance(HomeItemClickListActivity.this).getUserId();
        deviceId = PreferencesManager.getInstance(HomeItemClickListActivity.this).getDeviceId();

        Intent intent = getIntent();
        cat_id=intent.getIntExtra(CAT_ID,cat_id);
        type_id=intent.getIntExtra(TYPE_ID,type_id);
        cat_id_forAllSongs=cat_id; type_id_forAllSongs=type_id;
        type_name = intent.getStringExtra(TYPE_NAME);
        coverImage=intent.getStringExtra(IMAGE_URL);

        recyclerView = (RecyclerView)findViewById(R.id.container);
        recyclerView.setHasFixedSize(true);
     //   backdrop = (NetworkImageView)findViewById(R.id.backdrop);
     //   mImageLoader = MySingleton.getInstance(this).getImageLoader();
      //  backdrop.setImageUrl(intent.getStringExtra(IMAGE_URL), mImageLoader);
        Log.w(TAG,"image url is "+coverImage);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
      /*  myAdapter = new HomeItemClickListDataAdapter(this, arrayList1);
        recyclerView.setAdapter(myAdapter);*/

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(type_name);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        loadBackdrop(coverImage);
        if (ComonHelper.checkConnection(HomeItemClickListActivity.this)) {
            maincategory_webservice();
        } else {
            Toast.makeText(HomeItemClickListActivity.this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }


        bottomPlayerEvent();


    }

    public void bottomPlayerEvent() {
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        seekView = (SeekBar) findViewById(R.id.seekView);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivUp = (ImageView) findViewById(R.id.ivUp);


        System.out.println("PLAYINGGG" + AudioPlayerActivity.isPlaying);
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, HomeItemClickListActivity.this);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, HomeItemClickListActivity.this);
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
                try {
                    if (ComonHelper.timer != null) {
                        ComonHelper.timer.cancel();
                        ComonHelper.timer = null;
                    }
                    BackgroundSoundService.mPlayer.seekTo(seekBar.getProgress());
                    ComonHelper.updateSeekProgressTimer(seekBar, HomeItemClickListActivity.this);
                    if (AudioPlayerActivity.timer != null) {
                        AudioPlayerActivity.timer.cancel();
                        AudioPlayerActivity.timer = null;
                    }
                    AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                    audioPlayerActivity.updateProgressBar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void maincategory_webservice()
    {
        main_categories_list_containing_elements = new ArrayList<>();

        Log.w(TAG, "Url is " + Utility.homelist_url2 + cat_id + "&typeId=" + type_id + "&UserId=" + userId + "&DeviceId=" + deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.homelist_url2+cat_id+"&typeId="+type_id+"&UserId="+userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.w(TAG, response);
                        Gson gson = new Gson();
                        HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                        if (jsonResponse.getMessage().equals("success")) {
                            main_categories_list = jsonResponse.getCategories();

                            for (int i = 0; i<main_categories_list.size(); i++)
                            {
                                if(main_categories_list.get(i).getDataList().isEmpty())
                                {
                                    Log.w(TAG, "Values for Child are empty " );

                                }
                                else
                                {
                                    main_categories_list_containing_elements.add(jsonResponse.getCategories().get(i));
                                    subCategoryId=main_categories_list.get(i).getCategoryId();

                                    //region for all songs

                                    if(main_categories_list.get(i).getCategoryId()==4)
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

                            myAdapter = new HomeItemClickListDataAdapter(HomeItemClickListActivity.this, main_categories_list_containing_elements, audio_detail_categories_list, video_detail_categories_list, cat_id_forAllSongs, type_id_forAllSongs, subCategoryId, type_name);
                           // MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list);
                            recyclerView.setAdapter(myAdapter);
                        } else if (jsonResponse.getMessage().equalsIgnoreCase("Please Redirect to login page")) {
                            PreferencesManager.getInstance(HomeItemClickListActivity.this).clearUserPreferences();
                            Intent i = new Intent(HomeItemClickListActivity.this, AppIntroActivityNew.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                            ;
                        }


                      progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeItemClickListActivity.this, getResources().getString(R.string.error_msg), Toast.LENGTH_LONG).show();
                       progressBar.setVisibility(View.GONE);
                    }
                }){


        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                9000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(HomeItemClickListActivity.this).getRequestQueue().add(stringRequest);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    private void loadBackdrop(String coverImage) {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(coverImage).centerCrop().into(imageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // overridePendingTransition(0, R.anim.push_right);
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
    protected void onRestart() {
        super.onRestart();
        bottomPlayerEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomPlayerEvent();
    }
}
