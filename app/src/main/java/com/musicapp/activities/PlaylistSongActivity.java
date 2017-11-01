package com.musicapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.musicapp.R;
import com.musicapp.adapters.HomeItemClickDetailsAdapter;
import com.musicapp.adapters.RvPlaylistSongAdapter;
import com.musicapp.custom.HeaderView;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.PlaylistItem;
import com.musicapp.pojos.PlaylistSongItem;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PalseeTrivedi on 1/12/2017.
 */
public class PlaylistSongActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener,HomeItemClickDetailsAdapter.LikeUnlikeSongListener {
    PlaylistSongActivity playlistSongActivity;
    RecyclerView rvPlayListSongs;
    LinearLayout lnrtvFind, lnrFindSong;
    RelativeLayout rlMainContent,rlNosongs;
    CoordinatorLayout main_content;
    ProgressBar progressBar;
    HomeItemClickDetailsAdapter adapter;
    ArrayList <HomeDetailsJson.Categories> main_categories_list;
    ArrayList <HomeDetailsJson.DataList> detail_categories_list;
    ArrayList <HomeDetailsJson.DataList> audio_detail_categories_list=new ArrayList<>();
    ArrayList <HomeDetailsJson.DataList> video_detail_categories_list=new ArrayList<>();
    String playlistName, playlistImageUrl;
    int userId,playlistId;
    String deviceId;

    //for collapsable view
    HeaderView toolbar_header_view,float_header_view;
    AppBarLayout appbar;
    NetworkImageView backdrop;
    private boolean isHideToolbarView = false;
    TextView tvName,last_seen;
    ImageView img_play;
    String songName,coverImage;
    private ImageLoader mImageLoader;

    private static final String TAG = PlaylistSongActivity.class.getSimpleName();

    //for bottom player
    ImageView ivUp, ivBottomPlay;
    public static RelativeLayout bottomPlayerView;
    SeekBar seekView;
    TextView tvPlayName;


    //for create list popup
    EditText edtPopupName;
    TextView tvCreate, tvCancel;
    PopupWindow popupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Toolbar toolbar2 = (Toolbar) findViewById(R.id.create_toolbar);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playlistSongActivity = this;

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
          playlistId=bundle.getInt("playlistId");
            playlistName=bundle.getString("playlistName");
            playlistImageUrl = bundle.getString("playlistImage");
        }
        getSupportActionBar().setTitle(playlistName);
        System.out.println("DETAIL ON LIST"+playlistId+" "+playlistName);

        userId = PreferencesManager.getInstance(playlistSongActivity).getUserId();
        deviceId = PreferencesManager.getInstance(playlistSongActivity).getDeviceId();
        initialize();
        setupViewAction();
        bottomPlayerListner();

        if (ComonHelper.checkConnection(playlistSongActivity)) {
            getData();

        } else {
            Toast.makeText(playlistSongActivity, getResources().getString(R.string.error_no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void initialize() {
        rvPlayListSongs = (RecyclerView) findViewById(R.id.rvPlayListSongs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(playlistSongActivity);
        rvPlayListSongs.setLayoutManager(mLayoutManager);
        lnrtvFind = (LinearLayout) findViewById(R.id.lnrtvFind);
        lnrFindSong = (LinearLayout) findViewById(R.id.lnrFindSong);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        rlNosongs=(RelativeLayout) findViewById(R.id.rlNosongs);
      // rlMainContent=(RelativeLayout) findViewById(R.id.rlMainContent);
        main_content=(CoordinatorLayout) findViewById(R.id.main_content);
        toolbar_header_view=(HeaderView) findViewById(R.id.toolbar_header_view);
        float_header_view=(HeaderView) findViewById(R.id.float_header_view);
        appbar=(AppBarLayout) findViewById(R.id.appbar);
        appbar.addOnOffsetChangedListener(this);
        mImageLoader = MySingleton.getInstance(this).getImageLoader();
        backdrop=(NetworkImageView) findViewById(R.id.backdrop);
        last_seen=(TextView) findViewById(R.id.last_seen);
        last_seen.setVisibility(View.GONE);
        img_play=(ImageView) findViewById(R.id.img_play);
        img_play.setImageResource(R.drawable.detail);
        tvName=(TextView) findViewById(R.id.name);
        tvName.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, 30);
        params.gravity=Gravity.CENTER_VERTICAL;
        img_play.setLayoutParams(params);
        toolbar_header_view.bindTo(playlistName,"");
        float_header_view.bindTo(playlistName,"");
    }


    private void setupViewAction() {
        lnrFindSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("from","playlist");
                bundle.putInt("playlistId",playlistId);
                bundle.putString("playlistName",playlistName);
                Intent i=new Intent(playlistSongActivity,SearchActivity.class);
                i.putExtras(bundle);
                startActivity(i);


               // openPopupCreateList(v);


          /*      Intent intent=new Intent(playlistSongActivity,SearchActivity.class);
                intent.putExtra("from","playlist");
                intent.putExtra("playlistId",playlistId);
                intent.putExtra("playlistName",playlistName);
                startActivity(intent);*/

            }
        });

        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putInt("playlistId",playlistId);
                bundle.putString("playlistName",playlistName);
                bundle.putString("thumbnail", playlistImageUrl);
                Intent i=new Intent(playlistSongActivity,PlaylistTakeOverActivity.class);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

    }


    private void getData() {
        hideKeyboard();
       progressBar.setVisibility(View.VISIBLE);
       final String url=Utility.getsongPlaylist+"UserId="+userId+"&DeviceId="+deviceId+"&PlaylistId="+playlistId;
        System.out.println("PLAYLIST SONGS LIST" + url);
      //  String url="http://boxofficecapsule.com:91/WebServices/UserServices.svc/GetSongsFromPlaylist?UserId=72&DeviceId=b7e3838559093ff3&PlaylistId=14";
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {
                        Log.w(TAG,"url is " + url.toString());
                        Log.w(TAG, "Response is " + response.toString());
                        System.out.println("getlist" + response.toString());

                            Gson gson = new Gson();
                            HomeDetailsJson jsonResponse = gson.fromJson(response, HomeDetailsJson.class);
                            int id = jsonResponse.getId();
                            if (id == 1) {

                                main_categories_list = jsonResponse.getCategories();
                                // region separating audio video list
                                for (int i = 0; i<main_categories_list.size(); i++)
                                {
                                    if(main_categories_list.get(i).getDataList().isEmpty())
                                    {

                                            rlNosongs.setVisibility(View.VISIBLE);
                                        appbar.setVisibility(View.GONE);
                                        float_header_view.setVisibility(View.GONE);
                                        rvPlayListSongs.setVisibility(View.GONE);
                                         // rlMainContent.setVisibility(View.GONE);



                                        // empty
                                    }
                                    else
                                    {
                                        detail_categories_list =  main_categories_list.get(i).getDataList();
                                        System.out.println("DETAIL SIZE"+detail_categories_list.size());

                                        for (int j = 0; j<detail_categories_list.size(); j++)
                                        {
                                            songName=detail_categories_list.get(0).getColumns().getSongName();
                                            coverImage = detail_categories_list.get(0).getColumns().getThumbnailImage();
                                            if (playlistImageUrl != null || !playlistImageUrl.matches("")) {
                                                backdrop.setImageUrl(playlistImageUrl, mImageLoader);
                                            } else {
                                                if (coverImage != null || !coverImage.matches("")) {
                                                    backdrop.setImageUrl(coverImage, mImageLoader);
                                                    playlistImageUrl = coverImage;
                                                }
                                            }


                                            if (detail_categories_list.get(j).getColumns().getSongTypeId() == 1) {

                                                //audio list
                                                audio_detail_categories_list.add(main_categories_list.get(i).getDataList().get(j));
                                            } else if (detail_categories_list.get(j).getColumns().getSongTypeId() == 2) {

                                                //video list
                                                video_detail_categories_list.add(main_categories_list.get(i).getDataList().get(j));
                                            }
                                        }
                                        if (detail_categories_list.size() != 0) {
                                            rlNosongs.setVisibility(View.GONE);
                                            appbar.setVisibility(View.VISIBLE);
                                            float_header_view.setVisibility(View.VISIBLE);
                                            rvPlayListSongs.setVisibility(View.VISIBLE);
                                         //  rlMainContent.setVisibility(View.VISIBLE);

                                        }else {
                                            rlNosongs.setVisibility(View.VISIBLE);
                                            appbar.setVisibility(View.GONE);
                                            float_header_view.setVisibility(View.GONE);
                                            rvPlayListSongs.setVisibility(View.GONE);
                                         // rlMainContent.setVisibility(View.GONE);

                                        }

                                        intializeAdapter();
                                    }
                                }


                            } else {
                                Toast.makeText(playlistSongActivity, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                            }


                        progressBar.setVisibility(View.GONE);

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

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(request);
    }


    private void intializeAdapter() {
        adapter = new HomeItemClickDetailsAdapter(playlistSongActivity, detail_categories_list,audio_detail_categories_list,video_detail_categories_list,playlistName,"playlist",playlistId);
        // MySingleton.getInstance(getActivity()).setHome_categories_list(main_categories_list);
        rvPlayListSongs.setAdapter(adapter);
    }


    private void bottomPlayerListner() {
        //for bottom view
        bottomPlayerView = (RelativeLayout) findViewById(R.id.bottomPlayerView);
        seekView = (SeekBar) findViewById(R.id.seekView);
        ivUp = (ImageView) findViewById(R.id.ivUp);
        tvPlayName = (TextView) findViewById(R.id.tvPlayName);
        ivBottomPlay = (ImageView) findViewById(R.id.ivBottomPlay);
        if (AudioPlayerActivity.isPlaying) {
            bottomPlayerView.setVisibility(View.VISIBLE);
            ComonHelper comonHelper = new ComonHelper();
            comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, playlistSongActivity);
        } else {
            if (AudioPlayerActivity.isPause) {
                bottomPlayerView.setVisibility(View.VISIBLE);
                ivBottomPlay.setImageResource(R.drawable.pause_orange);
                ComonHelper comonHelper = new ComonHelper();
                comonHelper.bottomPlayerListner(seekView, ivBottomPlay, ivUp, tvPlayName, playlistSongActivity);
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
                    ComonHelper.updateSeekProgressTimer(seekBar, PlaylistSongActivity.this);
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

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AudioPlayerActivity.isPlaying) {
            ComonHelper.timer.cancel();
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbar_header_view.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbar_header_view.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public void onSongLikeUnlike(int playlistId, String from, String songName, String albumName, String thumbnail, int songId, String like, int songTypeId) {
        System.out.println("THUMB NAIL IN PLAY SONG" + thumbnail);
        Bundle bundle = new Bundle();
        bundle.putInt("playlistId", playlistId);
        bundle.putString("from", from);
        bundle.putString("songName",songName);
        bundle.putString("albumName", albumName);
        bundle.putString("thumbnail", thumbnail);
        bundle.putInt("songId", songId);
        bundle.putString("Like",like);
        bundle.putInt("type", songTypeId);
        Intent i = new Intent(PlaylistSongActivity.this, SongTakeoverActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            getData();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        bottomPlayerListner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomPlayerListner();
    }
}
