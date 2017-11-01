package com.musicapp.activities;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.BrowseItemListDataAdapter;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

    public class BrowseItemListActivity extends AppCompatActivity {

        public static final String CAT_ID= "categoryId";
        public static final String IMAGE_URL = "image_url";
        public static final String CATEGORY_NAME = "category_name";


        private static final String TAG=BrowseItemListActivity.class.getSimpleName();
        ArrayList <HomeDetailsJson.Categories> main_categories_list;
        ArrayList <HomeDetailsJson.Categories> main_categories_list_containing_elements;


        RecyclerView recyclerView;
        // ImageView backdrop;
        BrowseItemListDataAdapter myAdapter;
        ProgressBar progressBar;
        int cat_id;
        int subCategoryId;
        String category_name;
        String coverImage;
        private ImageLoader mImageLoader;
        int userId;
        String deviceId;

        public static RelativeLayout bottomPlayerView;
        SeekBar seekView;
        TextView tvPlayName;
        ImageView ivUp;
        public static ImageView ivBottomPlay;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_browse_item_list);
            userId = PreferencesManager.getInstance(BrowseItemListActivity.this).getUserId();
            deviceId = PreferencesManager.getInstance(BrowseItemListActivity.this).getDeviceId();

            Intent intent = getIntent();
            cat_id=intent.getIntExtra(CAT_ID,0);
            category_name = intent.getStringExtra(CATEGORY_NAME);
            coverImage=intent.getStringExtra(IMAGE_URL);

            recyclerView = (RecyclerView)findViewById(R.id.container);
            recyclerView.setHasFixedSize(true);
            //   backdrop = (NetworkImageView)findViewById(R.id.backdrop);
            //   mImageLoader = MySingleton.getInstance(this).getImageLoader();
            //  backdrop.setImageUrl(intent.getStringExtra(IMAGE_URL), mImageLoader);
            Log.w(TAG,"image url is "+coverImage);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(llm);
      /*  myAdapter = new BrowseItemListDataAdapter(this, arrayList1);
        recyclerView.setAdapter(myAdapter);*/

            Log.w(TAG,"CAT ID is"+cat_id);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

           /* CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(category_name);*/
            setTitle(category_name);
            progressBar = (ProgressBar)findViewById(R.id.progressBar);

            // loadBackdrop(coverImage);

            if (ComonHelper.checkConnection(BrowseItemListActivity.this)) {
                maincategory_webservice();
            } else {
                Toast.makeText(BrowseItemListActivity.this, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
            }
            bottomPlayerEvents();

        }


        public void bottomPlayerEvents() {
            bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
            ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
            seekView = (SeekBar) findViewById(R.id.seekView);
            tvPlayName = (TextView) findViewById(R.id.tvPlayName);
            ivUp = (ImageView) findViewById(R.id.ivUp);


            System.out.println("PLAYINGGG" + AudioPlayerActivity.isPlaying);
            if (AudioPlayerActivity.isPlaying) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, BrowseItemListActivity.this);
            } else {
                if (AudioPlayerActivity.isPause) {
                    bottomPlayerView.setVisibility(View.VISIBLE);
                    ivBottomPlay.setImageResource(R.drawable.pause_orange);
                    ComonHelper comonHelper = new ComonHelper();
                    comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, BrowseItemListActivity.this);
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
                        ComonHelper.updateSeekProgressTimer(seekBar, BrowseItemListActivity.this);
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

            Log.w(TAG,"Url is " + Utility.BROWSE_ITEM_CLICK+"categoryId="+cat_id+"&UserId="+userId+"&DeviceId="+deviceId);
            progressBar.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.BROWSE_ITEM_CLICK+"categoryId="+cat_id+"&UserId="+userId+"&DeviceId="+deviceId,
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
                                    }

                                }


                                myAdapter = new BrowseItemListDataAdapter(BrowseItemListActivity.this, main_categories_list_containing_elements,cat_id);
                                // MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list);
                                recyclerView.setAdapter(myAdapter);
                            } else if (jsonResponse.getMessage().equalsIgnoreCase("Please Redirect to login page")) {
                                PreferencesManager.getInstance(BrowseItemListActivity.this).clearUserPreferences();
                                Intent i = new Intent(BrowseItemListActivity.this, AppIntroActivityNew.class);
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
                            Toast.makeText(BrowseItemListActivity.this, getResources().getString(R.string.error_msg), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }){


            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    9000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MySingleton.getInstance(BrowseItemListActivity.this).getRequestQueue().add(stringRequest);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            try {
                return super.dispatchTouchEvent(ev);
            } catch (Exception e) {
                return false;
            }
        }

      /*  private void loadBackdrop(String coverImage) {
            final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
            Glide.with(this).load(coverImage).centerCrop().into(imageView);
        }*/

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
            bottomPlayerEvents();
        }

        @Override
        protected void onResume() {
            super.onResume();
            bottomPlayerEvents();
        }
    }
