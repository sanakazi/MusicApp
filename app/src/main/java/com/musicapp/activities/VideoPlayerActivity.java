package com.musicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.adapters.PlayerScreenListVideoAdapter;
import com.musicapp.aes.AESHelper;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.VideoStream;
import com.musicapp.pojos.HomeDetailsJson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by PalseeTrivedi on 12/22/2016.
 */

public class VideoPlayerActivity extends Activity implements View.OnClickListener/*, SurfaceHolder.Callback*/ {
    public static ImageView ivAdd, ivDetail, ivBackward, ivPrevious, ivPlay, ivNext, ivForward, ivOption, ivDown, ivMaxMin;
    public static TextView tvSong, tvStartTime, tvDuration, tvAlbumName;
    public static SeekBar seekBarVideo;
    public static ProgressBar progressBar;
    VideoPlayerActivity videoPlayerActivity;
    View header;
    RelativeLayout frameContainer;

    //play video
    public static boolean isPotrait = true, isPlaying = true, isReleased = false, isUrlChange = false, isPlayerView = true, isDescriptionView = false, isAnotherOptionScreen = false;
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

    /*  @Bind(R.id.latest_song_name_detail)
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viddeo_player);
        videoPlayerActivity = this;
        video_itemsList.clear();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
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
                video_tempList=bundle.getParcelableArrayList("specific_categories");
                System.out.println("TYPE ID NAME" + video_itemsList.size());

                int myIndex = itemsList.get(index).getColumns().getSongId();
                System.out.println("befor FOR LOOPING ID" + myIndex);

                index = getIndexByProperty(myIndex);
                System.out.println("TYPE ID NAME final index" + index);
                VideoURL = video_itemsList.get(index).getColumns().getSongURL();
                description = video_itemsList.get(index).getColumns().getDescription();
                if (description == null || description.matches("null")) {
                    description = "";
                }


            }


        }
        initialize();


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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        frameVideo = (SurfaceView) findViewById(R.id.frameVideo);
        ivAdd = (ImageView) findViewById(R.id.ivAdd);
        ivDetail = (ImageView) findViewById(R.id.ivDetail);
        ivBackward = (ImageView) findViewById(R.id.ivBackward);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
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
        //sHolder.addCallback(this);
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        System.out.println("onSurface created");
        try {

            player = new VideoStream(VideoPlayerActivity.this);
            player.setDisplay(frameVideo, sHolder);
            player.setUpVideoFrom(VideoURL);
            player.setSeekBar((SeekBar) findViewById(R.id.seekBar)/*,
                            (TextView) findViewById(R.id.lblCurrentPosition),
                    (TextView) findViewById(R.id.lblDuration)*/);

            player.play();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

                myAdapter = new PlayerScreenListVideoAdapter(VideoPlayerActivity.this,video_itemsList, index);
                recyclerView.setAdapter(myAdapter);
                rlPlayerView.setVisibility(View.GONE);
                rlDetailView.setVisibility(View.VISIBLE);

                isPlayerView = false;
                isDescriptionView = false;
                if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);
                        player.pause();
                        isPlaying = false;
                        ivPlay.setImageResource(R.drawable.stop_video);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!isReleased) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            player.play();
                            isPlaying = true;
                            ivPlay.setImageResource(R.drawable.play_video);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("RELEASEEDDDDD");
                        progressBar.setVisibility(View.VISIBLE);
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
                            ivPlay.setImageResource(R.drawable.play_video);
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
                    finish();
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
                            isPlaying = false;
                            ivPlay.setImageResource(R.drawable.stop_video);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!isReleased) {
                            try {
                                System.out.println("IS PLAYING" + isPlaying);
                                player.play();
                                isPlaying = true;
                                ivPlay.setImageResource(R.drawable.play_video);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("RELEASEEDDDDD");
                            progressBar.setVisibility(View.VISIBLE);
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
                                ivPlay.setImageResource(R.drawable.play_video);
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
                if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);
                        player.pause();
                        isPlaying = false;
                        ivPlay.setImageResource(R.drawable.stop_video);
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
                            ivPlay.setImageResource(R.drawable.play_video);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("RELEASEEDDDDD");
                        progressBar.setVisibility(View.VISIBLE);
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
                            ivPlay.setImageResource(R.drawable.play_video);
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
                if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);
                        player.pause();
                        isPlaying = false;
                        ivPlay.setImageResource(R.drawable.stop_video);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!isReleased) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            player.play();
                            isPlaying = true;
                            ivPlay.setImageResource(R.drawable.play_video);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("RELEASEEDDDDD");
                        progressBar.setVisibility(View.VISIBLE);
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
                            ivPlay.setImageResource(R.drawable.play_video);
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
                isAnotherOptionScreen = false;
                rlDetailView.setVisibility(View.GONE);
                rlPlayerView.setVisibility(View.VISIBLE);
                isPlayerView = true;
                System.out.println("backward Detail CLICKED");
                int positionBackward = player.getCurrentPosition() - 5000;
                player.setCurrentPositionToMplayer(positionBackward);
                player.timer.cancel();
                player.updateMediaProgress();
            }
        });
        ivForwardDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAnotherOptionScreen = false;
                rlDetailView.setVisibility(View.GONE);
                rlPlayerView.setVisibility(View.VISIBLE);
                isPlayerView = true;
                System.out.println("FORWARD DetailCLICKED");
                int positionForward = player.getCurrentPosition() + 5000;
                player.setCurrentPositionToMplayer(positionForward);
                player.timer.cancel();
                player.updateMediaProgress();
            }
        });


        ivPlayDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAnotherOptionScreen = false;
                rlDetailView.setVisibility(View.GONE);
                rlPlayerView.setVisibility(View.VISIBLE);
                isPlayerView = true;
                if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);
                        player.pause();
                        isPlaying = false;
                        ivPlay.setImageResource(R.drawable.stop_video);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!isReleased) {
                        try {
                            System.out.println("IS PLAYING" + isPlaying);
                            player.play();
                            isPlaying = true;
                            ivPlay.setImageResource(R.drawable.play_video);
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("RELEASEEDDDDD");
                        progressBar.setVisibility(View.VISIBLE);
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
                            ivPlay.setImageResource(R.drawable.play_video);
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

        ivNextDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        player.release();
                    }
                }
            }
        });


        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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


                        player.release();
                    }
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
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivPlay:
                isAnotherOptionScreen = false;
                if (isPlaying) {
                    try {
                        System.out.println("IS PLAYING" + isPlaying);
                        if (player != null) {
                            player.pause();
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
                            ivPlay.setImageResource(R.drawable.play_video);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.ivForward:
                isAnotherOptionScreen = false;
                System.out.println("FORWARD CLICKED");
                int positionForward = player.getCurrentPosition() + 5000;
                player.setCurrentPositionToMplayer(positionForward);
                player.timer.cancel();
                player.updateMediaProgress();
                break;
            case R.id.ivBackward:
                isAnotherOptionScreen = false;
                System.out.println("backward CLICKED");
                int positionBackward = player.getCurrentPosition() - 5000;
                player.setCurrentPositionToMplayer(positionBackward);
                player.timer.cancel();
                player.updateMediaProgress();
                break;

            case R.id.ivPrevious:
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

                        player.release();
                    }
                }
                break;
            case R.id.ivNext:
                isAnotherOptionScreen = false;
                System.out.println("Next CLICKED");
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

                            player.release();
                        }
                    }
                }
                break;
        }

    }

/*    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("onSurface created");
        try {

            player = new VideoStream(VideoPlayerActivity.this);
            player.setDisplay(frameVideo, sHolder);
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
        }


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {


    }*/

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("ON VIDEO RESUME CALLLLEDDDD");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("ON VIDEO RESUME CALLLLEDDDD");
    }

    @Override
    public void onBackPressed() {

        if (isPlayerView) {
            finish();
        } else if (isDescriptionView) {
            isAnotherOptionScreen=true;
            isPlayerView = true;
            rlDetailView.setVisibility(View.GONE);
            rlPlayerView.setVisibility(View.VISIBLE);
            rlDescription.setVisibility(View.GONE);
        } else {
            isAnotherOptionScreen=true;
            isPlayerView = true;
            rlDetailView.setVisibility(View.GONE);
            rlPlayerView.setVisibility(View.VISIBLE);
        }
    }
}
