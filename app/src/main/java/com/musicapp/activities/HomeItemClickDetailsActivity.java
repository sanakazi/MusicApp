package com.musicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.HomeItemClickDetailsAdapter;

import com.musicapp.custom.HeaderView;

import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeItemClickDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,HomeItemClickDetailsAdapter.LikeUnlikeSongListener {

    @Bind(R.id.toolbar_header_view) protected HeaderView toolbarHeaderView;
    @Bind(R.id.float_header_view) protected HeaderView floatHeaderView;
    @Bind(R.id.toolbar) protected Toolbar toolbar;
    @Bind(R.id.appbar) protected AppBarLayout appBarLayout;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    @Bind(R.id.backdrop) NetworkImageView backdrop;
    @Bind(R.id.img) NetworkImageView img;

    private boolean isHideToolbarView = false;
    private static final String TAG=HomeItemClickDetailsActivity.class.getSimpleName();
  //  private static final String url = "http://192.168.168.5:9000/WebServices/AppServices.svc/GetSongs?categoryId=";
  private static final String url = "http://192.168.168.5:9000/WebServices/AppServices.svc/GetSongs_V2?categoryId=";
    public static final String CAT_ID= "categoryId";
    public static final String TYPE_ID= "typeId";
    public static final String TYPE_NAME= "type_name";
    public static final String IMAGE_URL = "image_url";
    ArrayList <HomeDetailsJson.Categories> main_categories_list;
    ArrayList <HomeDetailsJson.DataList> detail_categories_list;
    ArrayList <HomeDetailsJson.DataList> audio_detail_categories_list=new ArrayList<>();
    ArrayList <HomeDetailsJson.DataList> video_detail_categories_list=new ArrayList<>();
    RecyclerView recyclerView;
    HomeItemClickDetailsAdapter myAdapter;
    int cat_id,type_id;
    String type_name;
    private ImageLoader mImageLoader;
    String  songCount=" 0 Song";
    int userId;
    String deviceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_item_click_details);
        userId = PreferencesManager.getInstance(HomeItemClickDetailsActivity.this).getUserId();
        deviceId = PreferencesManager.getInstance(HomeItemClickDetailsActivity.this).getDeviceId();
        ButterKnife.bind(this);
        Intent intent = getIntent();
        cat_id=intent.getIntExtra(CAT_ID,cat_id);
        type_id=intent.getIntExtra(TYPE_ID,type_id);
        type_name= intent.getStringExtra(TYPE_NAME);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


      //  backdrop.setDefaultImageResId(R.drawable.home_item3);
        mImageLoader = MySingleton.getInstance(this).getImageLoader();
        backdrop.setImageUrl(intent.getStringExtra(IMAGE_URL), mImageLoader);
     //   img.setImageResource(R.drawable.shape_new1);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        initUi();
        maincategory_webservice();

    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);

        toolbarHeaderView.bindTo(type_name, songCount);
        floatHeaderView.bindTo(type_name, songCount);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
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


    private void maincategory_webservice()
    {
        Log.w(TAG,"Url is " + Utility.home_details_url2+cat_id+"&typeId="+type_id+"&UserId="+userId+"&DeviceId="+deviceId);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Utility.home_details_url2+cat_id+"&typeId="+type_id+"&UserId="+userId+"&DeviceId="+deviceId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                        if (jsonResponse.getMessage().equals("success")) {
                            main_categories_list = jsonResponse.getCategories();
                            Log.w(TAG,"total size " + main_categories_list.size());


         // region separating audio video list
                            for (int i = 0; i<main_categories_list.size(); i++)
                            {
                                if(main_categories_list.get(i).getDataList().isEmpty())
                                {
                                            // empty
                                }
                                else
                                {
                                    detail_categories_list =  main_categories_list.get(i).getDataList();

                                    for (int j = 0; j<detail_categories_list.size(); j++)
                                    {
                                       if(detail_categories_list.size()<=1)
                                       {songCount = detail_categories_list.size()+" Song";}
                                        else
                                       {songCount = detail_categories_list.size()+" Songs";}
                                        toolbarHeaderView.bindTo(type_name, songCount);
                                        floatHeaderView.bindTo(type_name, songCount);

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
                                    Log.w(TAG,"audio list size " + audio_detail_categories_list.size());
                                    Log.w(TAG,"video list size "+ video_detail_categories_list.size());


                                    myAdapter = new HomeItemClickDetailsAdapter(HomeItemClickDetailsActivity.this, detail_categories_list,audio_detail_categories_list,video_detail_categories_list,type_name,"landing",0);
                                    // MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list);
                                    recyclerView.setAdapter(myAdapter);
                                }

                            }
   //endregion


                        }
                        progressBar.setVisibility(View.GONE);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(HomeItemClickDetailsActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }){


        };

        MySingleton.getInstance(HomeItemClickDetailsActivity.this).getRequestQueue().add(stringRequest);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onSongLikeUnlike(int playlistId, String from, String songName, String albumName, String thumbnail, int songId, String like) {

        Bundle bundle = new Bundle();
        bundle.putInt("playlistId", playlistId);
        bundle.putString("from", from);
        bundle.putString("songName",songName);
        bundle.putString("albumName", albumName);
        bundle.putString("thumbnail", thumbnail);
        bundle.putInt("songId", songId);
        bundle.putString("Like",like);
        Intent i = new Intent(HomeItemClickDetailsActivity.this, SongTakeoverActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2)
        {Log.w(TAG,"result is 2");}
        else if(requestCode==1)
        {
            Log.w(TAG,"request is 1");
            maincategory_webservice();
        }

    }
}
