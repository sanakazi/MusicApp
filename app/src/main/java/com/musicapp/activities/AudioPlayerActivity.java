package com.musicapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.musicapp.R;
import com.musicapp.aes.AESHelper;
import com.musicapp.encrypt.CryptLib;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Controls;
import com.musicapp.others.PlayerConstants;
import com.musicapp.others.SeekArc;
import com.musicapp.others.Utility;
import com.musicapp.pojos.AudioItem;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.MySingleton;
import com.musicapp.singleton.PreferencesManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.musicapp.activities.MainActivity.ivBottomPlay;

/**
 * Created by PalseeTrivedi on 12/22/2016.
 */
public class AudioPlayerActivity extends Activity {
    public static ImageView ivDown, ivOption, ivAdd, ivDetail, ivShuffle, ivPrevious, ivPlay, ivNext, ivRepeate, ivAlbum;
    public static TextView tvAlbumName, tvSong, tvStartTime, tvDuration;
    // Handler mHandler= new Handler();
    public static SeekArc seekBar;
    AudioPlayerActivity audioPlayerActivity;
    public static String AudioURL;
    public static boolean isRepeat = false, isShuffle = false, isFirstClick = true, isPlaying = false, isPause = false, temp = false, isUrlChanged = false, previoussong, nextsong;
    public static Timer timer;
    Intent playbackServiceIntent;
    ArrayList<HomeDetailsJson.DataList> detail_categories_list;
    public static ArrayList<HomeDetailsJson.DataList> audio_itemsList = new ArrayList<>();
    public static ArrayList<HomeDetailsJson.DataList> audio_tempList = new ArrayList<HomeDetailsJson.DataList>();
    public static int index;
    public static final String ALBUM_NAME = "album_name";
    public static String albumName, from, songName, imageUrl, description;

    //for encryptionDecription
    CryptLib crypt;

    //deepLinking
    String deepLinkSongId;
    public static boolean isDeeplink = true;
    public static ProgressBar progressBar, progressBarPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        System.out.println("On audioplayer create called");
        BackgroundSoundService.isProcessChanging = false;
        audioPlayerActivity = this;
        System.out.println("COMIN NOTIF" + ComonHelper.notifFlag);
        if (ComonHelper.notifFlag) {
            if (ComonHelper.from.matches("search")) {

                description = ComonHelper.description;
                imageUrl = ComonHelper.imageUrl;
                songName = ComonHelper.songName;
                from = ComonHelper.from;
            } else {
                from = ComonHelper.from;
                index = ComonHelper.position;
                albumName = ComonHelper.albumName;
                detail_categories_list = new ArrayList<>();
                detail_categories_list.clear();
                detail_categories_list.addAll(ComonHelper.itemsList);
                audio_itemsList.clear();
                audio_itemsList.addAll(ComonHelper.audio_itemsList);
                int myIndex = detail_categories_list.get(index).getColumns().getSongId();
                index = getIndexByProperty(myIndex);
                PlayerConstants.SONG_NUMBER = index;//changes by amol
                description = audio_itemsList.get(index).getColumns().getDescription();
                System.out.println("bottom player" + description);
                imageUrl = audio_itemsList.get(index).getColumns().getThumbnailImage();
                System.out.println("IMAGE URRLLL" + imageUrl);
                if (description == null || description.matches("null")) {
                    description = "";
                }
            }
            ComonHelper.notifFlag = false;
        } else {
            Bundle b = this.getIntent().getExtras();
            if (b != null) {

                isDeeplink = b.getBoolean("isDeeplink", true);
                System.out.println("IS DEEP LINKING" + isDeeplink);
                if (!isDeeplink) {
                    from = b.getString("from");
                    if (from.matches("search")) {
                        AudioURL = b.getString("songUrl");
                    /*try {
                        AudioURL = AESHelper.decrypt("musicapp2017", AudioURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                        songName = b.getString("songName");
                        imageUrl = b.getString("imageUrl");
                        ComonHelper.imageUrl = imageUrl;
                        System.out.println("IMAGE URRLLL" + imageUrl);
                        description = b.getString("description");
                        audio_itemsList = new ArrayList<HomeDetailsJson.DataList>();
                        detail_categories_list = new ArrayList<>();
                    } else if (from.matches("bottomPlayer")) {

                        if (ComonHelper.from.matches("search")) {

                            description = ComonHelper.description;
                            imageUrl = ComonHelper.imageUrl;
                            ComonHelper.imageUrl = imageUrl;
                            songName = ComonHelper.songName;
                            from = ComonHelper.from;
                        } else {
                            index = b.getInt("index");
                            System.out.println("INDEXXXX OF BOTTOM UP" + index);
                            albumName = b.getString(ALBUM_NAME);
                            detail_categories_list = b.getParcelableArrayList("categories");
                            audio_itemsList = b.getParcelableArrayList("specific_categories");

                            //<-------------changes by palsee 16thOct2017 commented code--------------------->
                            // int myIndex = detail_categories_list.get(index).getColumns().getSongId();
                            //  index = getIndexByProperty(myIndex);
                            //<-------------changes by palsee done 16thOct2017 commented code--------------------->

                            PlayerConstants.SONG_NUMBER = index;//changes by amol
                            description = audio_itemsList.get(index).getColumns().getDescription();
                            System.out.println("bottom player" + description);
                            imageUrl = audio_itemsList.get(index).getColumns().getThumbnailImage();
                            ComonHelper.imageUrl = imageUrl;
                            System.out.println("IMAGE URRLLL" + imageUrl);
                            if (description == null || description.matches("null")) {
                                description = "";
                            }
                        }

                    } else {
                        index = b.getInt("index");
                        albumName = b.getString(ALBUM_NAME);
                        detail_categories_list = b.getParcelableArrayList("categories");
                        audio_itemsList = b.getParcelableArrayList("specific_categories");
                        audio_tempList = b.getParcelableArrayList("specific_categories");
                        int myIndex = detail_categories_list.get(index).getColumns().getSongId();
                        index = getIndexByProperty(myIndex);
                        PlayerConstants.SONG_NUMBER = index;//changes by amol
                        description = b.getString("des");
                        System.out.println("Description in audio" + audio_itemsList.get(0).getColumns().getDescription());
                        imageUrl = audio_itemsList.get(index).getColumns().getThumbnailImage();
                        System.out.println("IMAGE URRLLL" + imageUrl);
                        if (description == null || description.matches("null")) {
                            description = "";
                        }
                        AudioURL = audio_itemsList.get(index).getColumns().getSongURL();
                        System.out.println("INDEXXX" + AudioURL);
                  /*  try {
                        AudioURL = AESHelper.decrypt("musicapp2017", AudioURL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                    }
                    initialize();
                    setupViewAction();
                    if (!AudioURL.matches("")) {
                        playbackServiceIntent = new Intent(this, BackgroundSoundService.class);
                        updateProgressBar();
                    } else {
                        Toast.makeText(audioPlayerActivity, "Can not play, song url is empty", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Uri uri = this.getIntent().getData();
                    if (uri != null) {
                        String scheme = uri.getScheme();
                        String host = uri.getHost();
                        deepLinkSongId = uri.getQuery();
                        System.out.println("Deeplink song url" + deepLinkSongId);

                        Log.d("Datafromurl", scheme + "://" + host + "" + deepLinkSongId);
                    }
                    boolean isLoggedIn = PreferencesManager.getInstance(audioPlayerActivity).getIsLoggedIn();
                    if (isLoggedIn) {
                        getDataFromDeepLink();
                    } else {
                        Intent i = new Intent(audioPlayerActivity, AppIntroActivityNew.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        AudioPlayerActivity.this.finish();
                    }
                }
            }

        }
    }

    private int getIndexByProperty(int yourIndex) {
        for (int i = 0; i < audio_itemsList.size(); i++) {
            int index = audio_itemsList.get(i).getColumns().getSongId();

            if (index == yourIndex) {

                return i;
            }
        }
        return -1;// not there is list
    }


    @Override
    protected void onStart() {
        super.onStart();
       /* if (!isDeeplink) {
            isFirstClick = true;
            startService(playbackServiceIntent);
        }*/
    }


    private void initialize() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarPlayer = (ProgressBar) findViewById(R.id.progressBarPlayer);

        seekBar = (SeekArc) findViewById(R.id.seekBar);
        seekBar.setRotation(180);
        seekBar.setStartAngle(30);
        seekBar.setSweepAngle(300);
        seekBar.setTouchInSide(true);
        ivDown = (ImageView) findViewById(R.id.ivDown);
        ivOption = (ImageView) findViewById(R.id.ivOption);
        if (from.matches("search") && !from.matches("bottomPlayer")) {
            ivOption.setVisibility(View.GONE);
        }
        ivAlbum = (ImageView) findViewById(R.id.ivAlbum);

        if (from.matches("search")) {
            if (!imageUrl.matches("")) {
                Picasso.with(audioPlayerActivity).load(imageUrl).into(ivAlbum);
            }
        } else {
            String url = audio_itemsList.get(index).getColumns().getThumbnailImage();
            if (!url.matches("") && url != null) {
                Picasso.with(audioPlayerActivity).load(url).into(ivAlbum);
            }

        }

        ivAdd = (ImageView) findViewById(R.id.ivAdd);
        ivDetail = (ImageView) findViewById(R.id.ivDetail);
        ivShuffle = (ImageView) findViewById(R.id.ivShuffle);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivPlay.setVisibility(View.GONE);

        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivRepeate = (ImageView) findViewById(R.id.ivRepeate);

        tvAlbumName = (TextView) findViewById(R.id.tvAlbumName);
        tvSong = (TextView) findViewById(R.id.tvSong);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
    }

    @Override
    protected void onResume() {
        super.onResume();


        System.out.println("On audioplayer resume called" + ComonHelper.pauseFlag);

        if (ComonHelper.pauseFlag) {
            System.out.println("ON RESUME CALLEDDDDDD" + BackgroundSoundService.mPlayer.getCurrentPosition());
            ComonHelper.pauseFlag = false;
            initialize();
            setupViewAction();
            seekBar.setMax(BackgroundSoundService.mPlayer.getDuration());
            seekBar.setProgress(BackgroundSoundService.mPlayer.getCurrentPosition());
            if (isPause) {
                progressBarPlayer.setVisibility(View.GONE);
                ivPlay.setVisibility(View.VISIBLE);
                ivPlay.setImageResource(R.drawable.stop_audio);
            } else {
                progressBarPlayer.setVisibility(View.GONE);
                ivPlay.setVisibility(View.VISIBLE);
                ivPlay.setImageResource(R.drawable.play_audio);
            }

            if (isRepeat) {
                ivRepeate.setImageResource(R.drawable.repeate_enable);
            } else {
                ivRepeate.setImageResource(R.drawable.repeat_disable);
            }

            if (isShuffle) {
                ivShuffle.setImageResource(R.drawable.cancel_enable);
            } else {
                ivShuffle.setImageResource(R.drawable.cancel_desable);

            }

        }

        System.out.println("ON RESUME CALLEDDDDDD" + from);
        if (!from.matches("bottomPlayer")) {
            System.out.println("ON RESUME CALLEDDDDDD no bottom" + temp);
            if (temp == true) {
                System.out.println("ON RESUME CALLEDDDDDD no bottom" + temp + " " + from);
                if (!from.matches("home") && !from.matches("search")) {
                    temp = false;
                    progressBarPlayer.setVisibility(View.GONE);
                    ivPlay.setVisibility(View.VISIBLE);
                    ivPlay.setImageResource(R.drawable.stop_audio);
                    BackgroundSoundService.mPlayer.pause();

                } else {

                    temp = true;
                    playbackServiceIntent = new Intent(this, BackgroundSoundService.class);
                    startService(playbackServiceIntent);
                    updateProgressBar();

                }

            } else {
                temp = true;
                playbackServiceIntent = new Intent(this, BackgroundSoundService.class);
                startService(playbackServiceIntent);
                updateProgressBar();
            }
        }

        seekBar.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                System.out.println("Seek bar onStartTrackingTouch");
                BackgroundSoundService.isProcessChanging = true;
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity.timer = null;
                System.out.println("background seek onStartTrackingTouch" + BackgroundSoundService.isProcessChanging);
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                System.out.println("Seek bar onStartTrackingTouch");
                try {
                    System.out.println("background seek onStopTrackingTouch");
                    BackgroundSoundService.isProcessChanging = true;
                    if (AudioPlayerActivity.timer != null) {
                        AudioPlayerActivity.timer.cancel();
                        AudioPlayerActivity.timer = null;
                    }
                    BackgroundSoundService.mPlayer.seekTo(seekArc.getProgress());
                    AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                    audioPlayerActivity.updateProgressBar();
                    BackgroundSoundService.isProcessChanging = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        /*if (isPause = false) {
            isFirstClick = true;
            startService(playbackServiceIntent);
        }*/
    }

    @Override
    protected void onPause() {
        System.out.println("on Pause called");
        super.onPause();
        if (isPause == true) {
            temp = true;
            if (BackgroundSoundService.mPlayer != null) {
                BackgroundSoundService.mPlayer.pause();
            }
        } else {
            temp = false;
            if (BackgroundSoundService.mPlayer != null) {
                BackgroundSoundService.mPlayer.start();
            }
        }

    }

    private void setupViewAction() {

        if (!from.matches("search")) {
            tvAlbumName.setText(albumName.toString());
            tvSong.setText(audio_itemsList.get(index).getColumns().getSongName());
            ComonHelper.songName = audio_itemsList.get(index).getColumns().getSongName();
            ComonHelper.imageUrl = imageUrl;

        } else {
            // tvAlbumName.setVisibility(View.GONE);
            tvAlbumName.setText(albumName.toString());
            tvSong.setText(songName);
            if (ComonHelper.tvSongName != null) {
                ComonHelper.songName = songName;
                ComonHelper.imageUrl = audio_itemsList.get(index).getColumns().getThumbnailImage();
            }
        }

        ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("is deep linking" + isDeeplink);
                if (!isDeeplink) {
                    if (from.matches("search")) {
                        ComonHelper.from = "search";
                        ComonHelper.songName = songName;
                        ComonHelper.description = description;
                        ComonHelper.imageUrl = imageUrl;
                        ComonHelper.pauseFlag = true;
                        if (ComonHelper.ivPlay != null) {
                            if (isPlaying) {

                                ComonHelper.ivPlay.setImageResource(R.drawable.play_orange);

                            } else {
                                ComonHelper.ivPlay.setImageResource(R.drawable.pause_orange);
                            }
                        }
                    } else {
                        System.out.println("DOWN TIME INDEXXXX" + index);
                        ComonHelper.position = index;
                        ComonHelper.from = from;
                        ComonHelper.audio_itemsList.clear();
                        ComonHelper.audio_itemsList.addAll(audio_itemsList);
                        ComonHelper.itemsList.clear();
                        ComonHelper.itemsList.addAll(detail_categories_list);
                        ComonHelper.pauseFlag = true;
                        ComonHelper.description = audio_itemsList.get(index).getColumns().getDescription();
                        ComonHelper.albumName = albumName;
                        ComonHelper.songName = audio_itemsList.get(index).getColumns().getSongName();
                        if (ComonHelper.ivPlay != null) {
                            if (isPlaying) {

                                ComonHelper.ivPlay.setImageResource(R.drawable.play_orange);

                            } else {
                                ComonHelper.ivPlay.setImageResource(R.drawable.pause_orange);
                            }
                        }
                    }
                    overridePendingTransition(0, R.anim.push_down);
                    finish();
                } else {
                    Intent i = new Intent(audioPlayerActivity, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    AudioPlayerActivity.this.finish();
                }
            }
        });
        ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AudioPlayerActivity.this, PlayerScreenListActivity.class);
                intent.putExtra("index", index);
                intent.putExtra(PlayerScreenListActivity.ALBUM_NAME, albumName);
                Bundle b = new Bundle();
                b.putParcelableArrayList("categories", audio_itemsList);
                intent.putExtras(b);
                startActivity(intent);
                overridePendingTransition(0, R.anim.push_left);

            }
        });

        ivAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent i = new Intent(audioPlayerActivity, AudioDetailScreen.class);

                if (from.matches("search")) {
                    bundle.putString("songName", songName);
                    bundle.putString("albumName", "");
                    bundle.putString("imageUrl", imageUrl);
                    bundle.putString("des", description);
                } else {
                    System.out.println("DESCRIPTION ON DETAIL" + index + " " + audio_itemsList.get(index).getColumns().getDescription());
                    bundle.putString("des", audio_itemsList.get(index).getColumns().getDescription());
                    bundle.putString("songName", audio_itemsList.get(index).getColumns().getSongName());
                    bundle.putString("albumName", albumName);
                    bundle.putString("imageUrl", audio_itemsList.get(index).getColumns().getThumbnailImage());
                }
                i.putExtras(bundle);
                startActivity(i);

            }
        });
        ivShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffle) {
                    isShuffle = false;
                    isRepeat = true;
                    ivShuffle.setImageResource(R.drawable.cancel_desable);
                } else {
                    isShuffle = true;
                    isRepeat = false;
                    ivShuffle.setImageResource(R.drawable.cancel_enable);
                }

            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*startService(playbackServiceIntent);
               updateProgressBar();*/
                System.out.println("IS FIRSSTTT CLICK" + isFirstClick);
              /*  if (isFirstClick) {
                    System.out.println("IS FIRSSTTT CLICK" + isFirstClick);
                    startService(playbackServiceIntent);
                    updateProgressBar();

                } else {*/
                System.out.println("IS FIRSSTTT CLICK" + isFirstClick);
                if (isPlaying) {
                    Controls.pauseControl(getApplicationContext());

                    BackgroundSoundService.mPlayer.pause();
                    if (ComonHelper.ivPlay != null) {
                        ComonHelper.ivPlay.setImageResource(R.drawable.pause_orange);
                    }
                    isPlaying = false;
                    isPause = true;
                    temp = true;
                    ivPlay.setImageResource(R.drawable.stop_audio);


                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                        if (ComonHelper.timer != null) {
                            ComonHelper.timer.cancel();
                            ComonHelper.timer = null;
                        }
                    }

                } else {
                    Controls.playControl(getApplicationContext());
                    BackgroundSoundService.mPlayer.start();
                    if (ComonHelper.ivPlay != null) {
                        ComonHelper.ivPlay.setImageResource(R.drawable.play_orange);
                    }
                    isPlaying = true;
                    isPause = false;
                    temp = false;
                    ivPlay.setImageResource(R.drawable.play_audio);
                    updateProgressBar();
                }
               /* }*/

            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUrlChanged = true;
                if (audio_itemsList.size() != 0) {
                    if (index != 0) {


                        // changes by amol
                        PlayerConstants.SONG_NUMBER--;
                        PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                        previoussong = true;
                        // done by amol



                        playSong(index - 1);
                        // index = index - 1;
                    }

                    //changes by amol
                    else {
                        PlayerConstants.SONG_NUMBER = audio_itemsList.size() - 1;
                        PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                    }//done by amol
                }/*else {
                    // play last song
                    playSong(audioList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }*/
            }
        });
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUrlChanged = true;
                // check if next song is there or not
                if (audio_itemsList.size() != 0) {
                    if (index != audio_itemsList.size() - 1) {

                        // changes by amol

                        PlayerConstants.SONG_NUMBER++;
                        PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                        nextsong = true;
                        //done by amol

                        playSong(index + 1);
                        //index = index + 1;
                    } //changes by amol
                    else {
                        PlayerConstants.SONG_NUMBER = 0;
                        PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                    }//done by amol
                }/*else {
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }*/
            }
        });

        ivRepeate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeat) {
                    isRepeat = false;
                    ivRepeate.setImageResource(R.drawable.repeat_disable);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    // make shuffle to false
                    isShuffle = false;
                    ivShuffle.setImageResource(R.drawable.cancel_desable);
                    ivRepeate.setImageResource(R.drawable.repeate_enable);
                }
            }
        });
    }

    public void updateProgressBar() {

        timer = new Timer("progress Updater");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (timer != null) {
                            if (BackgroundSoundService.mPlayer != null) {
                                if (isPlaying) {
                                    System.out.println("background seek timer" + BackgroundSoundService.isProcessChanging);
                                    if (!BackgroundSoundService.isProcessChanging) {
                                        System.out.println("TIMER RUNNING OF PLAYER ACTIVITY");
                                        try {
                                            seekBar.setMax(BackgroundSoundService.mPlayer.getDuration());
                                            seekBar.setProgress(BackgroundSoundService.mPlayer.getCurrentPosition());
                                            tvStartTime.setText(getDurationInSeconds(BackgroundSoundService.mPlayer.getCurrentPosition()));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }, 0, 1000);

         /* mHandler.postDelayed(mUpdateTimeTask, 1000);*/
    }

    public static String getDurationInSeconds(int sec) {
        sec = sec / 1000;
        int hours = sec / 3600;
        int minutes = (sec / 60) - (hours * 60);
        int seconds = sec - (hours * 3600) - (minutes * 60);
        String formatted = String.format("%d:%02d:%02d", hours, minutes, seconds);

        return formatted;
    }

    @Override
    public void onDestroy() {
        System.out.print("DESTROY Calllll");
        super.onDestroy();
      /*  if (isPlaying) {
            timer.cancel();
            BackgroundSoundService.mPlayer.release();
            Intent intent = new Intent(getApplicationContext(), BackgroundSoundService.class);
            stopService(intent);
        }*/


    }

    public void getDataFromDeepLink() {
        hideKeyboard();
        progressBar.setVisibility(View.VISIBLE);
        int userId = PreferencesManager.getInstance(audioPlayerActivity).getUserId();
        String deviceId = PreferencesManager.getInstance(audioPlayerActivity).getDeviceId();
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
                                        AudioURL = jDetailObj.getString("SongURL");
                                        /*try {
                                            AudioURL = AESHelper.decrypt("musicapp2017", AudioURL);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }*/
                                        songName = jDetailObj.getString("SongName");
                                        imageUrl = jDetailObj.getString("ThumbnailImage");
                                        description = jDetailObj.getString("Description");
                                        audio_itemsList = new ArrayList<HomeDetailsJson.DataList>();
                                    }
                                }
                                System.out.println("Deeplink song url" + AudioURL);

                                System.out.println("FINAL ULR" + AudioURL);
                                initialize();
                                setupViewAction();
                                if (!AudioURL.matches("")) {
                                    playbackServiceIntent = new Intent(audioPlayerActivity, BackgroundSoundService.class);
                                    updateProgressBar();
                                } else {
                                    Toast.makeText(audioPlayerActivity, "Can not play, song url is empty", Toast.LENGTH_LONG).show();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                System.out.println("Volley submit error" + error);
                if (null != error.networkResponse) {

                }
            }
        });

        MySingleton.getInstance(audioPlayerActivity).getRequestQueue().add(request);

    }

    private void hideKeyboard() {
        // Check if no   has focus:
        View v = audioPlayerActivity.getCurrentFocus();
        if (v != null) {
            InputMethodManager inputManager = (InputMethodManager) audioPlayerActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void playSong(int songIndex) {
        // Play song
        PlayerConstants.SONG_PAUSED = false;
        try {
            progressBarPlayer.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.GONE);

            if (PlayerScreenListActivity.progressBarPlayer != null) {
                PlayerScreenListActivity.progressBarPlayer.setVisibility(View.VISIBLE);
            }
            if (PlayerScreenListActivity.ivPlay != null) {
                PlayerScreenListActivity.ivPlay.setVisibility(View.GONE);
            }
            BackgroundSoundService.mPlayer.reset();
            AudioURL = audio_itemsList.get(songIndex).getColumns().getSongURL();
            index = songIndex;
            ComonHelper.position = songIndex;
            PlayerConstants.SONG_NUMBER = songIndex; // changes by amol
            ComonHelper.songName = audio_itemsList.get(songIndex).getColumns().getSongName();
            ComonHelper.description = audio_itemsList.get(songIndex).getColumns().getDescription();
            ComonHelper.imageUrl = audio_itemsList.get(songIndex).getColumns().getThumbnailImage();
            ComonHelper.albumName = albumName;
            try {
                crypt = new CryptLib();

                String iv = "musicapp2017aurum";
                String key = CryptLib.SHA256(iv, 32);

                AudioURL = crypt.decrypt(
                        TextUtils.htmlEncode(AudioURL), key, iv);

                Log.e("Encrypted format : ", "Encrypted format : "
                        + AudioURL);

            } catch (Exception e1) {
                e1.printStackTrace();
            }


            BackgroundSoundService.mPlayer.setDataSource(AudioURL);
            BackgroundSoundService.mPlayer.prepareAsync();
            // BackgroundSoundService.mPlayer.start();
            tvSong.setText(audio_itemsList.get(songIndex).getColumns().getSongName());
            songName = audio_itemsList.get(songIndex).getColumns().getSongName();
            ComonHelper.songName = audio_itemsList.get(songIndex).getColumns().getSongName();

            // tvAlbumName.setText("");
          /* if (audio_itemsList.get(songIndex).getColumns().getSongName().matches(albumName.toString())){
                tvAlbumName.setText("");
            }*/
            String url = audio_itemsList.get(songIndex).getColumns().getThumbnailImage();
            if (!url.matches("") && url != null) {
                Picasso.with(audioPlayerActivity).load(url).into(ivAlbum);
            }
            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        if (!isDeeplink) {
            if (from.matches("search")) {
                ComonHelper.from = "search";
                ComonHelper.songName = songName;
                ComonHelper.description = description;
                ComonHelper.imageUrl = imageUrl;
                ComonHelper.pauseFlag = true;
                if (ComonHelper.ivPlay != null) {
                    if (isPlaying) {

                        ComonHelper.ivPlay.setImageResource(R.drawable.play_orange);

                    } else {
                        ComonHelper.ivPlay.setImageResource(R.drawable.pause_orange);
                    }
                }
            } else {
                ComonHelper.position = index;
                ComonHelper.from = from;
                ComonHelper.audio_itemsList.clear();
                ComonHelper.audio_itemsList.addAll(audio_itemsList);
                ComonHelper.itemsList.clear();
                ComonHelper.itemsList.addAll(detail_categories_list);
                ComonHelper.pauseFlag = true;
                ComonHelper.description = audio_itemsList.get(index).getColumns().getDescription();
                ComonHelper.albumName = albumName;
                ComonHelper.songName = audio_itemsList.get(index).getColumns().getSongName();
                if (ComonHelper.ivPlay != null) {
                    if (isPlaying) {

                        ComonHelper.ivPlay.setImageResource(R.drawable.play_orange);

                    } else {
                        ComonHelper.ivPlay.setImageResource(R.drawable.pause_orange);
                    }
                }
            }
            overridePendingTransition(0, R.anim.push_down);
            finish();
        } else {
            Intent i = new Intent(audioPlayerActivity, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            AudioPlayerActivity.this.finish();
        }


    }

    public static void changeButton() {
        if (PlayerConstants.SONG_PAUSED) {
            ivPlay.setImageResource(R.drawable.stop_audio);

        } else {
            ivPlay.setImageResource(R.drawable.play_audio);

        }
    }


}
