package com.musicapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.musicapp.R;
import com.musicapp.adapters.PlayerScreenListVideoAdapter;
import com.musicapp.aes.AESHelper;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Utility;
import com.musicapp.others.VideoStream;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by PalseeTrivedi on 12/22/2016.
 */

public class VideoPlayerActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {
    public static ImageView ivAdd, ivDetail, ivBackward, ivPrevious, ivPlay, ivNext, ivForward, ivOption, ivDown, ivMaxMin;
    public static TextView tvSong, tvStartTime, tvDuration, tvAlbumName;
    public static SeekBar seekBarVideo;
    public static ProgressBar progressBar;
    VideoPlayerActivity videoPlayerActivity;
    View header;
    RelativeLayout frameContainer;

    //play video
    public static boolean isPotrait = true, isPlaying = true, isReleased = false, isUrlChange = false, isPlayerView = true, isDescriptionView = false, isAnotherOptionScreen = false, isFirstCall = false, isPlayToolbarEnable = true;
    public static String VideoURL;
    public static int index = 0;
    public static VideoStream player;
    public static SurfaceView frameVideo;
    public static SurfaceHolder sHolder;
    // public static ArrayList<VideoStreamzItem> videoList = new ArrayList<VideoStreamzItem>();
    public static ArrayList<HomeDetailsJson.DataList> itemsList;
    public static ArrayList<HomeDetailsJson.DataList> video_itemsList = new ArrayList<HomeDetailsJson.DataList>();
    public static ArrayList<HomeDetailsJson.DataList> video_tempList = new ArrayList<HomeDetailsJson.DataList>();
    public static final String ALBUM_NAME = "album_name";
    String from, albumName, songName, imageUrl, description;
    public static RelativeLayout rlPlayerView, rlBottomControls;

    //for detail screen view
    /*@Bind(R.id.latest_song_name_detail)
      TextView latest_song_name_detail;*/

    ImageView slide_downDetail;
    public static TextView tvLabelNext, latest_song_name_detail, album_nameDetail;
    public static ImageView ivBackwardDetail, ivPreviousDetail, ivPlayDetail, ivNextDetail, ivForwardDetail,
            latest_song_img_detail;
    RecyclerView recyclerView;
    PlayerScreenListVideoAdapter myAdapter;
    public static RelativeLayout rlDetailView;

    //for description view
    public static RelativeLayout rlDescription;
    public static ImageView ivDesSongImage;
    public static TextView tvDesSongName, tvDesAlbumName;
    LinearLayout lnrCancel;
    WebView webView;
    public static int detroyedTime = 0;


    //deepLinking
    String deepLinkSongId;
    public boolean isDeeplink = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viddeo_player);
        videoPlayerActivity = this;
        video_itemsList.clear();
        Bundle bundle = getIntent().getExtras();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (bundle != null) {
            isDeeplink = bundle.getBoolean("isDeeplink", true);
            System.out.println("DEPPLLLOIINNKK" + isDeeplink);
            if (!isDeeplink) {
                from = bundle.getString("from");

                if (from.matches("search")) {
                    VideoURL = bundle.getString("songUrl");
                    songName = bundle.getString("songName");
                    imageUrl = bundle.getString("imageUrl");
                    description = bundle.getString("description");
                    video_itemsList = new ArrayList<HomeDetailsJson.DataList>();
                } else {
                    index = bundle.getInt("index");
                    Log.w("Song", "index is  " + index);

                    System.out.println("TYPE ID NAME" + index);
                    albumName = bundle.getString("album_name");
                    System.out.println("TYPE ID NAME" + albumName);

                    itemsList = bundle.getParcelableArrayList("categories");
                    Log.w("Song", "itemlist size is  " + itemsList.size());
                    System.out.println("TYPE ID NAME" + itemsList.size());

                    video_itemsList = bundle.getParcelableArrayList("specific_categories");
                    video_tempList = bundle.getParcelableArrayList("specific_categories");
                    System.out.println("TYPE ID NAME" + video_itemsList.size());

                    int myIndex = itemsList.get(index).getColumns().getSongId();
                    System.out.println("befor FOR LOOPING ID" + myIndex);

                    index = getIndexByProperty(myIndex);
                    System.out.println("TYPE ID NAME final index" + index);
                    VideoURL = video_itemsList.get(index).getColumns().getSongURL();

                    description = bundle.getString("des");
                    System.out.println("TYPE description" + description);
                    if (description == null || description.matches("null")) {
                        description = "";
                    }
                }
                System.out.println("FINAL ULR" + VideoURL);
                initialize();
            } else {
                Uri uri = this.getIntent().getData();
                if (uri != null) {
                    String scheme = uri.getScheme();
                    String host = uri.getHost();
                    deepLinkSongId = uri.getQuery();
                    String[] words = deepLinkSongId.split("=");
                    deepLinkSongId = words[1];

                    System.out.println("Deeplink song url" + deepLinkSongId);
                    Log.d("Datafromurl", scheme + "://" + host + "" + deepLinkSongId);
                }
                boolean isLoggedInUSER = PreferencesManager.getInstance(videoPlayerActivity).getIsLoggedIn();
                System.out.println("IS LOGGED IN" + isLoggedInUSER);
                if (isLoggedInUSER) {

                    getDataFromDeepLink();
                } else {
                    System.out.println("IS LOGGED IN" + isLoggedInUSER);
                    Intent i = new Intent(videoPlayerActivity, AppIntroActivityNew.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    VideoPlayerActivity.this.finish();
                }
            }

        }


    }

    private int getIndexByProperty(int yourIndex) {
        for (int i = 0; i < video_itemsList.size(); i++) {
            int index = video_itemsList.get(i).getColumns().getSongId();
            System.out.println("FOR LOOPING ID" + index);
            System.out.println("FOR LOOPING ID" + itemsList.get(0).getColumns().getSongId());


            if (index == yourIndex) {

                return i;
            }
        }
        return -1;// not there is list
    }

    private void initialize() {
        header = (View) findViewById(R.id.header);
        frameContainer = (RelativeLayout) findViewById(R.id.frameContainer);
        progressBar.setVisibility(View.VISIBLE);
        frameVideo = (SurfaceView) findViewById(R.id.frameVideo);
        ivAdd = (ImageView) findViewById(R.id.ivAdd);
        ivDetail = (ImageView) findViewById(R.id.ivDetail);
        ivBackward = (ImageView) findViewById(R.id.ivBackward);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivPlay.setClickable(false);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivForward = (ImageView) findViewById(R.id.ivForward);
        ivMaxMin = (ImageView) findViewById(R.id.ivMaxMin);
        ivOption = (ImageView) findViewById(R.id.ivOption);
        ivDown = (ImageView) findViewById(R.id.ivDown);
        tvSong = (TextView) findViewById(R.id.tvSong);
        tvAlbumName = (TextView) findViewById(R.id.tvAlbumName);
        if (!from.matches("search")) {
            tvAlbumName.setText(albumName.toString());
            tvSong.setText(video_itemsList.get(index).getColumns().getSongName());
        } else {
            tvAlbumName.setVisibility(View.GONE);
            tvSong.setText(songName);
            ivOption.setVisibility(View.GONE);
        }
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        seekBarVideo = (SeekBar) findViewById(R.id.seekBar);
        sHolder = frameVideo.getHolder();
        sHolder.addCallback(this);
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
      /*try {

            player = new VideoStream(VideoPlayerActivity.this);
            player.setDisplay(frameVideo, sHolder);
            System.out.println("onSurface created"+VideoURL);
            player.setUpVideoFrom(VideoURL);
            player.setSeekBar((SeekBar) findViewById(R.id.seekBar)*//*,
                            (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*//*);

            player.play();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        ivPlay.setOnClickListener(this);
        ivForward.setOnClickListener(this);
        ivBackward.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        rlPlayerView = (RelativeLayout) findViewById(R.id.rlPlayerView);
        rlBottomControls = (RelativeLayout) findViewById(R.id.rlBottomControls);
        RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsBottom.addRule(RelativeLayout.BELOW, frameContainer.getId());
        rlBottomControls.setLayoutParams(paramsBottom);

        //detail screen view initialization
        rlDetailView = (RelativeLayout) findViewById(R.id.rlDetailView);
        slide_downDetail = (ImageView) findViewById(R.id.slide_downDetail);
        ivBackwardDetail = (ImageView) findViewById(R.id.ivBackwardDetail);
        ivPreviousDetail = (ImageView) findViewById(R.id.ivPreviousDetail);
        ivPlayDetail = (ImageView) findViewById(R.id.ivPlayDetail);
        ivNextDetail = (ImageView) findViewById(R.id.ivNextDetail);
        ivForwardDetail = (ImageView) findViewById(R.id.ivForwardDetail);
        latest_song_img_detail = (ImageView) findViewById(R.id.latest_song_img_detail);
        tvLabelNext = (TextView) findViewById(R.id.tvLabelNext);
        album_nameDetail = (TextView) findViewById(R.id.album_nameDetail);
        if (!from.matches("search")) {
            album_nameDetail.setText(albumName.toString());
        }
        latest_song_name_detail = (TextView) findViewById(R.id.latest_song_name_detail);
        if (video_itemsList.size() != 0) {
            Picasso.with(videoPlayerActivity).load(video_itemsList.get(index).getColumns().getThumbnailImage()).into(latest_song_img_detail);
            latest_song_name_detail.setText(video_itemsList.get(index).getColumns().getSongName());
        }

        recyclerView = (RecyclerView) findViewById(R.id.playerlist_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);


        //for description view
        rlDescription = (RelativeLayout) findViewById(R.id.rlDescription);
        ivDesSongImage = (ImageView) findViewById(R.id.ivDesSongImage);
        tvDesSongName = (TextView) findViewById(R.id.tvDesSongName);
        tvDesAlbumName = (TextView) findViewById(R.id.tvDesAlbumName);
        lnrCancel = (LinearLayout) findViewById(R.id.lnrCancel);
        webView = (WebView) findViewById(R.id.webView);
        webView.setBackgroundColor(Color.parseColor("#1c1c1c"));
        if (description.matches("")) {
            webView.setVisibility(View.GONE);
        }
        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", description, mimeType, encoding, "");

        if (!from.matches("search")) {
            tvDesAlbumName.setText(albumName.toString());
            tvDesSongName.setText(video_itemsList.get(index).getColumns().getSongName());
            String imageUrl = video_itemsList.get(index).getColumns().getThumbnailImage();
            if (imageUrl != null && !imageUrl.matches("")) {
                Picasso.with(videoPlayerActivity).load(video_itemsList.get(index).getColumns().getThumbnailImage()).into(ivDesSongImage);
            }
        } else {
            // tvAlbumName.setVisibility(View.GONE);
            tvDesSongName.setText(songName);

            if (!imageUrl.matches("")) {
                Picasso.with(videoPlayerActivity).load(imageUrl).into(ivDesSongImage);
            }


        }
        ivMaxMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAnotherOptionScreen = false;
                if (isPotrait) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    rlBottomControls.setVisibility(View.VISIBLE);
                    if (isPlaying) {
                        ivPlay.setImageResource(R.drawable.play_video);
                    } else {
                        ivPlay.setImageResource(R.drawable.stop_video);
                    }
                    ivDetail.setVisibility(View.GONE);
                    ivNext.setImageResource(R.drawable.next_video);
                    ivPrevious.setImageResource(R.drawable.previous_video);
                    ivForward.setImageResource(R.drawable.forward_video);
                    ivBackward.setImageResource(R.drawable.backward_video);

                    header.setVisibility(View.GONE);
                    ivMaxMin.setImageResource(R.drawable.minimize);
                    isPotrait = false;
                    player.setUpVideoDimensions();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    frameContainer.setLayoutParams(params);
                    RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, rlPlayerView.getId());
                    rlBottomControls.setLayoutParams(paramsBottom);


                    //this code will return access to automatic sensor again for orientation
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                        }
                    }, 2000);

                    //sensor access done

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    rlBottomControls.setVisibility(View.VISIBLE);


                    if (isPlaying) {
                        ivPlay.setImageResource(R.drawable.play_video_vert);
                    } else {
                        ivPlay.setImageResource(R.drawable.pause_video_vert);
                    }

                    ivNext.setImageResource(R.drawable.next_vert_video);
                    ivPrevious.setImageResource(R.drawable.previous_vert_video);
                    ivForward.setImageResource(R.drawable.foward_vert_video);
                    ivBackward.setImageResource(R.drawable.backward_vert_video);

                    ivDetail.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    ivMaxMin.setImageResource(R.drawable.maximize);
                    isPotrait = true;
                    player.setUpVideoDimensions();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, header.getId());
                    params.setMargins(10, 30, 10, 0);
                    frameContainer.setLayoutParams(params);

                    RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsBottom.addRule(RelativeLayout.BELOW, frameContainer.getId());
                    rlBottomControls.setLayoutParams(paramsBottom);

                    //this code will return access to automatic sensor again for orientation
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                        }
                    }, 2000);

                    //sensor access done
                }
            }
        });

        frameVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPotrait) {

                } else {
                    if (isPlayToolbarEnable) {
                        rlBottomControls.setVisibility(View.INVISIBLE);
                        isPlayToolbarEnable = false;
                    } else {
                        rlBottomControls.setVisibility(View.VISIBLE);
                        isPlayToolbarEnable = true;
                    }
                }
            }
        });


        ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAnotherOptionScreen = true;
                System.out.println("VIDEO LIST SIZE" + video_itemsList.size());
                //showing detail view of screen
                if (video_itemsList.size() != 0) {
                    latest_song_name_detail.setText(video_itemsList.get(index).getColumns().getSongName());
                }

                myAdapter = new PlayerScreenListVideoAdapter(VideoPlayerActivity.this, video_itemsList, index);
                recyclerView.setAdapter(myAdapter);
                rlPlayerView.setVisibility(View.GONE);
                rlDetailView.setVisibility(View.VISIBLE);

                isPlayerView = false;
                isDescriptionView = false;
                if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);
                        player.pause();
                        player.timer.cancel();
                        player.timer = null;
                        isPlaying = false;
                        if (isPotrait) {
                            ivPlay.setImageResource(R.drawable.pause_video_vert);
                        } else {
                            ivPlay.setImageResource(R.drawable.stop_video);
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!isReleased) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            player.play();
                            isPlaying = true;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.play_video_vert);
                            } else {
                                ivPlay.setImageResource(R.drawable.play_video);
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("RELEASEEDDDDD");
                        progressBar.setVisibility(View.VISIBLE);
                        ivPlay.setClickable(false);
                        player = null;
                        try {

                            player = new VideoStream(VideoPlayerActivity.this);
                            player.setUpVideoFrom(VideoURL);
                            player.setSeekBar((SeekBar) findViewById(R.id.seekBar)/*,
                    (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*/);
                            player.setDisplay(frameVideo, sHolder);
                            player.play();
                            isPlaying = true;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.play_video_vert);
                            } else {
                                ivPlay.setImageResource(R.drawable.play_video);
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }


                }


            }
        });
        ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAnotherOptionScreen = true;
                if (isPlayerView) {
                    if (isDeeplink) {
                        try {
                            if (player != null) {
                                if (player.mPlayer != null) {
                                    player.mPlayer.release();
                                    player.mPlayer = null;
                                    player = null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(videoPlayerActivity, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        VideoPlayerActivity.this.finish();
                    } else {
                        try {
                            if (player != null) {
                                if (player.mPlayer != null) {
                                    player.mPlayer.release();
                                    player.mPlayer = null;
                                    player = null;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        VideoPlayerActivity.this.finish();
                    }
                } else {
                    isPlayerView = true;
                    rlDetailView.setVisibility(View.GONE);
                    rlPlayerView.setVisibility(View.VISIBLE);
                    isPlayerView = true;
                    isDescriptionView = false;
                    if (isPlaying) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            player.pause();
                            player.timer.cancel();
                            player.timer = null;
                            isPlaying = false;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.pause_video_vert);
                            } else {
                                ivPlay.setImageResource(R.drawable.stop_video);
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!isReleased) {
                            try {
                                System.out.println("IS PLAYING" + isPlaying);
                                player.play();
                                isPlaying = true;
                                if (isPotrait) {
                                    ivPlay.setImageResource(R.drawable.play_video_vert);
                                } else {
                                    ivPlay.setImageResource(R.drawable.play_video);
                                }
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("RELEASEEDDDDD");
                            progressBar.setVisibility(View.VISIBLE);
                            ivPlay.setClickable(false);
                            player = null;
                            try {

                                player = new VideoStream(VideoPlayerActivity.this);
                                player.setUpVideoFrom(VideoURL);
                                player.setSeekBar((SeekBar) findViewById(R.id.seekBar)/*,
                    (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*/);
                                player.setDisplay(frameVideo, sHolder);
                                player.play();
                                isPlaying = true;
                                if (isPotrait) {
                                    ivPlay.setImageResource(R.drawable.play_video_vert);
                                } else {
                                    ivPlay.setImageResource(R.drawable.play_video);
                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }


                    }
                }
            }
        });

        ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAnotherOptionScreen = true;
                rlPlayerView.setVisibility(View.GONE);
                rlDetailView.setVisibility(View.GONE);
                rlDescription.setVisibility(View.VISIBLE);

                isPlayerView = false;
                isDescriptionView = true;
                description = video_itemsList.get(index).getColumns().getDescription();
                if (description.matches("")) {
                    webView.setVisibility(View.GONE);
                }
                final String mimeType = "text/html";
                final String encoding = "UTF-8";
                webView.getSettings().setJavaScriptEnabled(true);
                webView.loadDataWithBaseURL("", description, mimeType, encoding, "");
                if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);
                        if (player != null) {
                            player.pause();
                            player.timer.cancel();
                            player.timer = null;
                        }
                        isPlaying = false;
                        if (isPotrait) {
                            ivPlay.setImageResource(R.drawable.pause_video_vert);
                        } else {
                            ivPlay.setImageResource(R.drawable.stop_video);
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!isReleased) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            if (player != null) {
                                player.play();
                            }
                            isPlaying = true;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.play_video_vert);
                            } else {
                                ivPlay.setImageResource(R.drawable.play_video);

                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("RELEASEEDDDDD");
                        progressBar.setVisibility(View.VISIBLE);
                        ivPlay.setClickable(false);
                        player = null;
                        try {

                            player = new VideoStream(VideoPlayerActivity.this);
                            player.setUpVideoFrom(VideoURL);
                            player.setSeekBar((SeekBar) findViewById(R.id.seekBar)/*,
                    (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*/);
                            player.setDisplay(frameVideo, sHolder);
                            player.play();
                            isPlaying = true;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.play_video_vert);
                            } else {
                                ivPlay.setImageResource(R.drawable.play_video);
                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }


                }
            }
        });

        lnrCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAnotherOptionScreen = true;

                rlPlayerView.setVisibility(View.VISIBLE);
                rlDetailView.setVisibility(View.GONE);
                rlDescription.setVisibility(View.GONE);

                isPlayerView = true;
                isDescriptionView = false;
 /*               if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);

                        player.pause();
                        isPlaying = false;
                        if (isPotrait) {
                            ivPlay.setImageResource(R.drawable.pause_video_vert);
                        }else {
                            ivPlay.setImageResource(R.drawable.stop_video);
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!isReleased) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            player.play();
                            isPlaying = true;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.pause_video_vert);
                            }else {
                                ivPlay.setImageResource(R.drawable.stop_video);
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("RELEASEEDDDDD");
                        progressBar.setVisibility(View.VISIBLE);
                        ivPlay.setClickable(false);
                        player = null;
                        try {

                            player = new VideoStream(VideoPlayerActivity.this);
                            player.setUpVideoFrom(VideoURL);
                            player.setSeekBar((SeekBar) findViewById(R.id.seekBar)*//*,
                    (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*//*);
                            player.setDisplay(frameVideo, sHolder);
                            player.play();
                            isPlaying = true;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.play_video_vert);
                            }else {
                                ivPlay.setImageResource(R.drawable.play_video);

                            }
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }


                }*/


            }
        });
        slide_downDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAnotherOptionScreen = true;
                isPlayerView = true;
                rlDetailView.setVisibility(View.GONE);
                rlPlayerView.setVisibility(View.VISIBLE);


            }
        });

        ivBackwardDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    rlDetailView.setVisibility(View.GONE);
                    rlPlayerView.setVisibility(View.VISIBLE);
                    isPlayerView = true;
                    System.out.println("backward Detail CLICKED");
                    int positionBackward = player.getCurrentPosition() - 5000;
                    player.setCurrentPositionToMplayer(positionBackward);
                    player.timer.cancel();
                    player.updateMediaProgress();
                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();

                }


            }
        });
        ivForwardDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    rlDetailView.setVisibility(View.GONE);
                    rlPlayerView.setVisibility(View.VISIBLE);
                    isPlayerView = true;
                    System.out.println("FORWARD DetailCLICKED");
                    int positionForward = player.getCurrentPosition() + 5000;
                    player.setCurrentPositionToMplayer(positionForward);
                    player.timer.cancel();
                    player.updateMediaProgress();
                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();

                }

            }
        });


        ivPlayDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    rlDetailView.setVisibility(View.GONE);
                    rlPlayerView.setVisibility(View.VISIBLE);
                    isPlayerView = true;
                    if (isPlaying) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            player.pause();
                            player.timer.cancel();
                            player.timer = null;
                            isPlaying = false;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.pause_video_vert);
                            } else {
                                ivPlay.setImageResource(R.drawable.stop_video);

                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!isReleased) {
                            try {
                                System.out.println("IS PLAYING" + isPlaying);
                                player.play();
                                isPlaying = true;
                                if (isPotrait) {
                                    ivPlay.setImageResource(R.drawable.play_video_vert);
                                } else {
                                    ivPlay.setImageResource(R.drawable.play_video);

                                }
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("RELEASEEDDDDD");
                            progressBar.setVisibility(View.VISIBLE);
                            ivPlay.setClickable(false);
                            player = null;
                            try {

                                player = new VideoStream(VideoPlayerActivity.this);
                                player.setUpVideoFrom(VideoURL);
                                player.setSeekBar((SeekBar) findViewById(R.id.seekBar)/*,
                    (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*/);
                                player.setDisplay(frameVideo, sHolder);
                                player.play();
                                isPlaying = true;
                                if (isPotrait) {
                                    ivPlay.setImageResource(R.drawable.play_video_vert);
                                } else {
                                    ivPlay.setImageResource(R.drawable.play_video);

                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }


                    }

                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();

                }

            }

        });

        ivNextDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    rlDetailView.setVisibility(View.GONE);
                    rlPlayerView.setVisibility(View.VISIBLE);
                    isPlayerView = true;
                    System.out.println("Next DETAIL CLICKED");
                    if (video_itemsList.size() != 0) {
                        System.out.println("NEXTTTT when size not zero");
                        if (index != video_itemsList.size() - 1) {

                            isUrlChange = true;
                            index = index + 1;
                            VideoURL = video_itemsList.get(index).getColumns().getSongURL();
                            Picasso.with(videoPlayerActivity).load(video_itemsList.get(index).getColumns().getThumbnailImage());
                            tvSong.setText(video_itemsList.get(index).getColumns().getSongName());
                            latest_song_name_detail.setText(video_itemsList.get(index).getColumns().getSongName());
                            tvDesSongName.setText(video_itemsList.get(index).getColumns().getSongName());
                            tvDesAlbumName.setText(albumName);
                            String url = video_itemsList.get(index).getColumns().getThumbnailImage();
                            if (!url.matches("") && url != null) {
                                Picasso.with(videoPlayerActivity).load(url).into(latest_song_img_detail);
                                Picasso.with(videoPlayerActivity).load(url).into(ivDesSongImage);
                            }
                            myAdapter = new PlayerScreenListVideoAdapter(VideoPlayerActivity.this, video_itemsList, index);
                            recyclerView.setAdapter(myAdapter);
                            player.release();
                        }

                    }
                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();

                }

            }

        });


        ivPreviousDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    rlDetailView.setVisibility(View.GONE);
                    rlPlayerView.setVisibility(View.VISIBLE);
                    isPlayerView = true;
                    if (video_itemsList.size() != 0) {
                        if (index != 0) {
                            System.out.println("PREVIOUS DETAIL CLICKED");
                            isUrlChange = true;
                            index = index - 1;
                            VideoURL = video_itemsList.get(index).getColumns().getSongURL();
                            Picasso.with(videoPlayerActivity).load(video_itemsList.get(index).getColumns().getThumbnailImage());
                            latest_song_name_detail.setText(video_itemsList.get(index).getColumns().getSongName());
                            tvSong.setText(video_itemsList.get(index).getColumns().getSongName());
                            tvDesSongName.setText(video_itemsList.get(index).getColumns().getSongName());
                            tvDesAlbumName.setText(albumName);
                            String url = video_itemsList.get(index).getColumns().getThumbnailImage();
                            if (!url.matches("") && url != null) {
                                Picasso.with(videoPlayerActivity).load(url).into(latest_song_img_detail);
                                Picasso.with(videoPlayerActivity).load(url).into(ivDesSongImage);
                            }

                            myAdapter = new PlayerScreenListVideoAdapter(VideoPlayerActivity.this, video_itemsList, index);
                            recyclerView.setAdapter(myAdapter);
                            player.release();
                        }
                    }
                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isUrlChange = false;
        try {
            if (player != null) {
                player.mPlayer.release();
                player.mPlayer = null;
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            System.out.println("CALLING ON PAUSE");
            if (player != null) {
                player.pause();
                player.timer.cancel();
                player.timer = null;
                player = null;
                detroyedTime = 0;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivPlay:
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    if (isPlaying) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            if (player != null) {
                                player.pause();
                                player.timer.cancel();
                                player.timer = null;
                            }
                            isPlaying = false;
                            if (isPotrait) {
                                ivPlay.setImageResource(R.drawable.pause_video_vert);
                            } else {
                                ivPlay.setImageResource(R.drawable.stop_video);
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!isReleased) {
                            try {
                                System.out.println("IS PLAYING" + isPlaying);
                                if (player != null) {
                                    player.play();
                                }
                                isPlaying = true;
                                if (isPotrait) {
                                    ivPlay.setImageResource(R.drawable.play_video_vert);
                                } else {
                                    ivPlay.setImageResource(R.drawable.play_video);
                                }
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("RELEASEEDDDDD");
                            progressBar.setVisibility(View.VISIBLE);
                            ivPlay.setClickable(false);
                            player = null;
                            try {

                                player = new VideoStream(this);
                                player.setUpVideoFrom(VideoURL);
                                player.setSeekBar((SeekBar) findViewById(R.id.seekBar)/*,
                    (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*/);
                                player.setDisplay(frameVideo, sHolder);
                                player.play();
                                isPlaying = true;
                                if (isPotrait) {
                                    ivPlay.setImageResource(R.drawable.play_video_vert);
                                } else {
                                    ivPlay.setImageResource(R.drawable.play_video);

                                }
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();

                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.ivForward:
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    try {
                        isAnotherOptionScreen = false;
                        System.out.println("FORWARD CLICKED");
                        int positionForward = player.getCurrentPosition() + 5000;
                        player.setCurrentPositionToMplayer(positionForward);
                        if (player.timer != null) {
                            player.timer.cancel();
                        }
                        player.updateMediaProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.ivBackward:
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    try {
                        isAnotherOptionScreen = false;
                        System.out.println("backward CLICKED");
                        int positionBackward = player.getCurrentPosition() - 5000;
                        player.setCurrentPositionToMplayer(positionBackward);
                        if (player.timer != null) {
                            player.timer.cancel();
                        }
                        player.updateMediaProgress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ivPrevious:
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    if (video_itemsList.size() != 0) {
                        if (index != 0) {
                            System.out.println("PREVIOUS CLICKED");
                            isUrlChange = true;
                            index = index - 1;
                            VideoURL = video_itemsList.get(index).getColumns().getSongURL();
                            tvSong.setText(video_itemsList.get(index).getColumns().getSongName());
                            latest_song_name_detail.setText(video_itemsList.get(index).getColumns().getSongName());
                            tvDesSongName.setText(video_itemsList.get(index).getColumns().getSongName());
                            tvDesAlbumName.setText(albumName);
                            String url = video_itemsList.get(index).getColumns().getThumbnailImage();
                            if (!url.matches("") && url != null) {
                                Picasso.with(videoPlayerActivity).load(url).into(latest_song_img_detail);
                                Picasso.with(videoPlayerActivity).load(url).into(ivDesSongImage);
                            }
                            if (player != null) {
                                player.release();
                            }
                        }
                    }
                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ivNext:
                System.out.println("NEXT CLICKED" + tvStartTime.getText().toString());
                if (!tvStartTime.getText().toString().matches("00:00:00")) {
                    isAnotherOptionScreen = false;
                    System.out.println("Next CLICKED main");
                    if (video_itemsList.size() != 0) {
                        System.out.println("NEXTTTT when size not zero");
                        if (index != video_itemsList.size() - 1) {
                            if (video_itemsList.size() != 1) {

                                isUrlChange = true;
                                index = index + 1;
                                VideoURL = video_itemsList.get(index).getColumns().getSongURL();
                                tvSong.setText(video_itemsList.get(index).getColumns().getSongName());
                                latest_song_name_detail.setText(video_itemsList.get(index).getColumns().getSongName());
                                tvDesSongName.setText(video_itemsList.get(index).getColumns().getSongName());
                                tvDesAlbumName.setText(albumName);
                                String url = video_itemsList.get(index).getColumns().getThumbnailImage();
                                if (!url.matches("") && url != null) {
                                    Picasso.with(videoPlayerActivity).load(url).into(latest_song_img_detail);
                                    Picasso.with(videoPlayerActivity).load(url).into(ivDesSongImage);
                                }
                                if (player != null) {
                                    player.release();
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(videoPlayerActivity, "Please wait we are preparing", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("onSurface created");
        isFirstCall = true;
        try {
            player = new VideoStream(VideoPlayerActivity.this);
            player.setDisplay(frameVideo, sHolder);
            player.setUpVideoFrom(VideoURL);
            player.setSeekBar((SeekBar) findViewById(R.id.seekBar)/*,
                            (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*/);

            player.play();
            progressBar.setVisibility(View.VISIBLE);
            ivPlay.setClickable(false);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            detroyedTime = player.getCurrentPosition();
            isPlaying = false;
            player.release();
            player = null;
            System.out.println("DEST TIMING" + detroyedTime);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        rlBottomControls.setVisibility(View.VISIBLE);
        isPotrait = true;
        isPlaying = true;
        if (isPlaying) {
            ivPlay.setImageResource(R.drawable.play_video_vert);
        } else {
            ivPlay.setImageResource(R.drawable.pause_video_vert);
        }

        ivNext.setImageResource(R.drawable.next_vert_video);
        ivPrevious.setImageResource(R.drawable.previous_vert_video);
        ivForward.setImageResource(R.drawable.foward_vert_video);
        ivBackward.setImageResource(R.drawable.backward_vert_video);


        header.setVisibility(View.VISIBLE);
        ivMaxMin.setImageResource(R.drawable.maximize);
        isPotrait = true;
        if (player != null) {
            player.setUpVideoDimensions();
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, header.getId());
        params.setMargins(10, 30, 10, 0);
        frameContainer.setLayoutParams(params);

        RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsBottom.addRule(RelativeLayout.BELOW, frameContainer.getId());
        rlBottomControls.setLayoutParams(paramsBottom);


        if (player != null) {
            System.out.println("DEST TIMING" + detroyedTime);
            progressBar.setVisibility(View.VISIBLE);
            ivPlay.setClickable(false);
        }
        System.out.println("ON VIDEO RESUME CALLLLEDDDD");

        //this code will return access to automatic sensor again for orientation
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

            }
        }, 2000);

        //sensor access done
    }

    public void getDataFromDeepLink() {
        System.out.println("IS LOGGED IN" + "true");
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        ivPlay.setClickable(false);
        int userId = PreferencesManager.getInstance(videoPlayerActivity).getUserId();
        String deviceId = PreferencesManager.getInstance(videoPlayerActivity).getDeviceId();
        String url = Utility.baseUrl + "GetSongById?UserId=" + userId + "&DeviceId=" + deviceId + "&SongId=" + deepLinkSongId;

        Log.w("VideoPlayerActivity", "Search url is" + url);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)

                    {

                        System.out.println("RESPONNNSEEE" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String id = jsonObject.getString("id");
                            if (id.matches("1")) {
                                from = "search";

                                JSONArray jArray = jsonObject.getJSONArray("categories");
                                for (int i = 0; i < jArray.length(); i++) {
                                    JSONObject jObj = jArray.getJSONObject(i);
                                    JSONArray jArrayDetail = jObj.getJSONArray("dataList");
                                    for (int j = 0; j < jArrayDetail.length(); j++) {
                                        JSONObject jsonObj = jArrayDetail.getJSONObject(j);
                                        JSONObject jDetailObj = jsonObj.getJSONObject("columns");
                                        VideoURL = jDetailObj.getString("SongURL");
                                        songName = jDetailObj.getString("SongName");
                                        imageUrl = jDetailObj.getString("ThumbnailImage");
                                        description = jDetailObj.getString("Description");
                                        video_itemsList = new ArrayList<HomeDetailsJson.DataList>();
                                    }
                                }
                                System.out.println("Deeplink song url" + VideoURL);
                                initialize();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);
                        ivPlay.setClickable(true);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                ivPlay.setClickable(true);
                System.out.println("Volley submit error" + error);
                if (null != error.networkResponse) {
                    System.out.println("Volley submit error" + error);
                }
            }
        });

        MySingleton.getInstance(videoPlayerActivity).getRequestQueue().add(request);

    }

    private void hideKeyboard() {
        // Check if no   has focus:
        View v = videoPlayerActivity.getCurrentFocus();
        if (v != null) {
            InputMethodManager inputManager = (InputMethodManager) videoPlayerActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {

        if (isPlayerView) {
            if (isDeeplink) {
                try {
                    if (player != null) {
                        if (player.mPlayer != null) {
                            player.mPlayer.release();
                            player.mPlayer = null;
                            player = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Intent i = new Intent(videoPlayerActivity, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                VideoPlayerActivity.this.finish();
            } else {
                try {
                    if (player != null) {
                        if (player.mPlayer != null) {
                            player.mPlayer.release();
                            player.mPlayer = null;
                            player = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                VideoPlayerActivity.this.finish();
            }
        } else if (isDescriptionView) {
            isAnotherOptionScreen = true;
            isPlayerView = true;
            rlDetailView.setVisibility(View.GONE);
            rlPlayerView.setVisibility(View.VISIBLE);
            rlDescription.setVisibility(View.GONE);
        } else {
            isAnotherOptionScreen = true;
            isPlayerView = true;
            rlDetailView.setVisibility(View.GONE);
            rlPlayerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen

        System.out.println("CONFIGGGUUUEEE" + newConfig.orientation);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


            //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            rlBottomControls.setVisibility(View.VISIBLE);
            if (isPlaying) {
                ivPlay.setImageResource(R.drawable.play_video);
            } else {
                ivPlay.setImageResource(R.drawable.stop_video);
            }
            ivDetail.setVisibility(View.GONE);
            ivNext.setImageResource(R.drawable.next_video);
            ivPrevious.setImageResource(R.drawable.previous_video);
            ivForward.setImageResource(R.drawable.forward_video);
            ivBackward.setImageResource(R.drawable.backward_video);

            header.setVisibility(View.GONE);
            ivMaxMin.setImageResource(R.drawable.minimize);
            isPotrait = false;
            player.setUpVideoDimensions();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            frameContainer.setLayoutParams(params);
            RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, rlPlayerView.getId());
            rlBottomControls.setLayoutParams(paramsBottom);
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            //   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            rlBottomControls.setVisibility(View.VISIBLE);


            if (isPlaying) {
                ivPlay.setImageResource(R.drawable.play_video_vert);
            } else {
                ivPlay.setImageResource(R.drawable.pause_video_vert);
            }

            ivNext.setImageResource(R.drawable.next_vert_video);
            ivPrevious.setImageResource(R.drawable.previous_vert_video);
            ivForward.setImageResource(R.drawable.foward_vert_video);
            ivBackward.setImageResource(R.drawable.backward_vert_video);

            ivDetail.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            ivMaxMin.setImageResource(R.drawable.maximize);
            isPotrait = true;
            player.setUpVideoDimensions();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, header.getId());
            params.setMargins(10, 30, 10, 0);
            frameContainer.setLayoutParams(params);

            RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsBottom.addRule(RelativeLayout.BELOW, frameContainer.getId());
            rlBottomControls.setLayoutParams(paramsBottom);


            // Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

}
