package com.musicapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.musicapp.R;
import com.musicapp.aes.AESHelper;
import com.musicapp.others.SeekArc;
import com.musicapp.pojos.AudioItem;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.service.BackgroundSoundService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
    public static boolean isRepeat = false, isShuffle = true, isFirstClick = true, isPlaying = false, isPause = false;
    public static Timer timer;
    Intent playbackServiceIntent;
    ArrayList<HomeDetailsJson.DataList> detail_categories_list;
    public static ArrayList<HomeDetailsJson.DataList> audio_itemsList;
    //  public static ArrayList<AudioItem> audioList = new ArrayList<AudioItem>();
    public static int index;
    public static final String ALBUM_NAME = "album_name";
    String albumName, from, songName, imageUrl, description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        audioPlayerActivity = this;
        Bundle b = this.getIntent().getExtras();

        if (b != null) {
            from = b.getString("from");
            if (from.matches("search")) {
                AudioURL = b.getString("songUrl");
                try {
                    AudioURL= AESHelper.decrypt("musicapp2017",AudioURL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                songName = b.getString("songName");
                imageUrl = b.getString("imageUrl");
                description = b.getString("description");
                audio_itemsList = new ArrayList<HomeDetailsJson.DataList>();
            } else {
                index = getIntent().getIntExtra("index", 0);
                albumName = getIntent().getStringExtra(ALBUM_NAME);
                detail_categories_list = b.getParcelableArrayList("categories");
                audio_itemsList = b.getParcelableArrayList("specific_categories");
                int myIndex = detail_categories_list.get(index).getColumns().getSongId();
                index = getIndexByProperty(myIndex);
                description = audio_itemsList.get(index).getColumns().getDescription();
                if (description == null || description.matches("null")) {
                    description = "";
                }
                AudioURL = audio_itemsList.get(index).getColumns().getSongURL();
                try {
                    AudioURL= AESHelper.decrypt("musicapp2017",AudioURL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        initialize();
        setupViewAction();
        if (!AudioURL.matches("")) {
            playbackServiceIntent = new Intent(this, BackgroundSoundService.class);
            updateProgressBar();
        } else {
            Toast.makeText(audioPlayerActivity, "Can not play, song url is empty", Toast.LENGTH_LONG).show();
        }
    }

    private int getIndexByProperty(int yourIndex) {
        for (int i = 0; i < audio_itemsList.size(); i++) {
            int index = audio_itemsList.get(i).getColumns().getSongId();

            if (index==yourIndex) {

                return i;
            }
        }
        return -1;// not there is list
    }


    @Override
    protected void onStart() {
        super.onStart();
        isFirstClick = true;
        startService(playbackServiceIntent);
    }


    private void initialize() {
        seekBar = (SeekArc) findViewById(R.id.seekBar);
        seekBar.setRotation(180);
        seekBar.setStartAngle(30);
        seekBar.setSweepAngle(300);
        seekBar.setTouchInSide(true);
        ivDown = (ImageView) findViewById(R.id.ivDown);
        ivOption = (ImageView) findViewById(R.id.ivOption);
        if (from.matches("search")) {
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
        if (isPlaying) {
            seekBar.setProgress(BackgroundSoundService.mPlayer.getCurrentPosition());
            seekBar.setMax(BackgroundSoundService.mPlayer.getDuration());
            ivPlay.setImageResource(R.drawable.play_audio);
            if (isRepeat) {
                ivRepeate.setImageResource(R.drawable.repeate_enable);
            } else {
                ivRepeate.setImageResource(R.drawable.repeat_disable);
            }
        }
        System.out.println("ON RESUME CALLEDDDDDD");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isPause = false) {
            isFirstClick = true;
            startService(playbackServiceIntent);
        }
    }

    private void setupViewAction() {

        if (!from.matches("search")) {
            tvAlbumName.setText(albumName.toString());
            tvSong.setText(audio_itemsList.get(index).getColumns().getSongName());
        } else {
            tvAlbumName.setVisibility(View.GONE);
            tvSong.setText(songName);
        }

        ivDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, R.anim.push_down);
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
                bundle.putString("des",description);
                if (from.matches("search")) {
                    bundle.putString("songName", songName);
                    bundle.putString("albumName", "");
                    bundle.putString("imageUrl",imageUrl);
                }else {
                    bundle.putString("songName", audio_itemsList.get(index).getColumns().getDescription());
                    bundle.putString("albumName", albumName);
                    bundle.putString("imageUrl",audio_itemsList.get(index).getColumns().getThumbnailImage());
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
                if (isFirstClick) {
                    System.out.println("IS FIRSSTTT CLICK" + isFirstClick);
                    startService(playbackServiceIntent);
                    updateProgressBar();

                } else {
                    System.out.println("IS FIRSSTTT CLICK" + isFirstClick);
                    if (isPlaying) {
                        BackgroundSoundService.mPlayer.pause();
                        isPlaying = false;
                        isPause = true;
                        ivPlay.setImageResource(R.drawable.stop_audio);
                    } else {
                        BackgroundSoundService.mPlayer.start();
                        isPlaying = true;
                        isPause = false;
                        ivPlay.setImageResource(R.drawable.play_audio);
                    }
                }

            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audio_itemsList.size() != 0) {
                    if (index != 0) {
                        playSong(index - 1);
                        index = index - 1;
                    }
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
                // check if next song is there or not
                if (audio_itemsList.size() != 0) {
                    if (index != audio_itemsList.size() - 1) {
                        playSong(index + 1);
                        index = index + 1;
                    }
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
                    isShuffle = true;
                    ivRepeate.setImageResource(R.drawable.repeat_disable);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    // make shuffle to false
                    isShuffle = false;
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
                        System.out.println("TIMER RUNNING OF PLAYER ACTIVITY");
                        if (BackgroundSoundService.mPlayer != null) {
                            seekBar.setProgress(BackgroundSoundService.mPlayer.getCurrentPosition());
                            tvStartTime.setText(getDurationInSeconds(BackgroundSoundService.mPlayer.getCurrentPosition()));
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

   /* private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            seekBar.setProgress(BackgroundSoundService.mPlayer.getCurrentPosition());
          *//*  mHandler.postDelayed(this, 1000);*//*
        }
    };*/

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

    public void playSong(int songIndex) {
        // Play song
        try {
            BackgroundSoundService.mPlayer.reset();
            BackgroundSoundService.mPlayer.setDataSource(audio_itemsList.get(songIndex).getColumns().getSongURL());
            BackgroundSoundService.mPlayer.prepare();
            BackgroundSoundService.mPlayer.start();
            tvSong.setText(audio_itemsList.get(songIndex).getColumns().getSongName());
            String url = audio_itemsList.get(index).getColumns().getThumbnailImage();
            if (!url.matches("") && url != null) {
                Picasso.with(audioPlayerActivity).load(url).into(ivAlbum);
            }
          /*  // Displaying Song title
            String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);*/

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


}
