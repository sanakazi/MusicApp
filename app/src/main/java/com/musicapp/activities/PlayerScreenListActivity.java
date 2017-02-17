package com.musicapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.adapters.PlayerScreenListAdapter;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlayerScreenListActivity extends AppCompatActivity {

    @Bind(R.id.latest_song_name)
    TextView latest_song_name;
    @Bind(R.id.latest_song_img)
    ImageView latest_song_img;
    @Bind(R.id.album_name)
    TextView album_name;
    @Bind(R.id.slide_down)
    ImageView slide_down;
    RecyclerView recyclerView;
    PlayerScreenListAdapter myAdapter;
    ArrayList<HomeDetailsJson.DataList> audio_itemsList;
public static TextView tvLabelNext;
    ImageView ivShuffle, ivPrevious, ivPlay, ivNext, ivRepeate;

    private static final String TAG = PlayerScreenListActivity.class.getSimpleName();
    public static final String ALBUM_NAME = "album_name";
    String albumName;
    public static int index;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_screen_list);
        ButterKnife.bind(this);
        Bundle b = this.getIntent().getExtras();
        audio_itemsList = b.getParcelableArrayList("categories");
        index = getIntent().getIntExtra("index", 0);
        albumName = getIntent().getStringExtra(ALBUM_NAME);
        ivShuffle = (ImageView) findViewById(R.id.ivShuffle);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        tvLabelNext=(TextView) findViewById(R.id.tvLabelNext);
        if (AudioPlayerActivity.isPlaying){
            ivPlay.setImageResource(R.drawable.play_audio);
        }else {
            ivPlay.setImageResource(R.drawable.stop_audio);
        }
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivRepeate = (ImageView) findViewById(R.id.ivRepeate);

        recyclerView = (RecyclerView) findViewById(R.id.playerlist_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        events();

        updateViewData();
    }

    private void events() {
        album_name.setText(albumName.toString());
        latest_song_name.setText(audio_itemsList.get(index).getColumns().getSongName());
        String thumbnailImagePath = audio_itemsList.get(index).getColumns().getThumbnailImage();
        if (!thumbnailImagePath.matches("") && thumbnailImagePath != null) {
            Picasso.with(PlayerScreenListActivity.this).load(thumbnailImagePath).into(latest_song_img);
        }

        myAdapter = new PlayerScreenListAdapter(this, audio_itemsList,index);
        recyclerView.setAdapter(myAdapter);
        slide_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, R.anim.push_down);
            }
        });

        ivShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AudioPlayerActivity.isShuffle) {
                    AudioPlayerActivity.isShuffle = false;
                    AudioPlayerActivity.isRepeat = true;
                    ivShuffle.setImageResource(R.drawable.cancel_desable);
                } else {
                    AudioPlayerActivity.isRepeat = false;
                    AudioPlayerActivity.isShuffle = true;
                    ivShuffle.setImageResource(R.drawable.cancel_enable);
                }

            }
        });
        ivRepeate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AudioPlayerActivity.isRepeat) {
                    AudioPlayerActivity.isRepeat = false;
                    AudioPlayerActivity.isShuffle = true;
                    ivRepeate.setImageResource(R.drawable.repeat_disable);
                } else {
                    AudioPlayerActivity.isShuffle = false;
                    AudioPlayerActivity.isRepeat = true;
                    ivRepeate.setImageResource(R.drawable.repeate_enable);
                }

            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audio_itemsList.size() != 0) {
                    if (index != 0) {
                        AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                        audioPlayerActivity.playSong(index - 1);
                        index = index - 1;
                        AudioPlayerActivity.index=index-1;
                    }
                }
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audio_itemsList.size() != 0) {
                    if (index != audio_itemsList.size() - 1) {
                        AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                        audioPlayerActivity.playSong(index + 1);
                        index = index + 1;
                        AudioPlayerActivity.index=index+1;
                    }
                }
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AudioPlayerActivity.isPlaying) {
                    BackgroundSoundService.mPlayer.pause();
                    AudioPlayerActivity.isPlaying = false;
                    AudioPlayerActivity.isPause = true;
                    ivPlay.setImageResource(R.drawable.stop_audio);
                } else {
                    BackgroundSoundService.mPlayer.start();
                    AudioPlayerActivity.isPlaying = true;
                    AudioPlayerActivity.isPause = false;
                    ivPlay.setImageResource(R.drawable.play_audio);
                }
            }
        });
    }

    public void updateViewData() {


        timer = new Timer("progress Updater");

        timer.scheduleAtFixedRate(new

                                          TimerTask() {
                                              @Override
                                              public void run() {
                                                  runOnUiThread(new Runnable() {
                                                      public void run() {
                                                          System.out.println("TIMER RUNNING OF PLAYER ACTIVITY");
                                                          if (BackgroundSoundService.mPlayer != null) {
                                                              if (audio_itemsList.size()!=0) {
                                                                  String thumbnailImagePath = audio_itemsList.get(index).getColumns().getThumbnailImage();
                                                                  if (!thumbnailImagePath.matches("") && thumbnailImagePath != null) {
                                                                      Picasso.with(PlayerScreenListActivity.this).load(thumbnailImagePath).into(latest_song_img);
                                                                  }
                                                                  latest_song_name.setText(audio_itemsList.get(index).getColumns().getSongName());
                                                              }else {
                                                                  timer.cancel();
                                                              }
                                                          }
                                                      }
                                                  });
                                              }
                                          }

                , 0, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null) {
            timer.cancel();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (timer!=null) {
            timer.cancel();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (timer!=null) {
            timer.cancel();
        }
    }
}
