package com.musicapp.activities;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.musicapp.R;
import com.musicapp.pojos.LivestreamJson;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LiveStreamVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    @Bind(R.id.live_stream_title) TextView live_stream_title;
    @Bind(R.id.artistname) TextView artistname;
    @Bind(R.id.downs) ImageView downs;
    @Bind(R.id.tvDetail) ImageView tvDetail;
    @Bind(R.id.video_desctiption) RelativeLayout video_desctiption;
    @Bind(R.id.cancel) Button cancel;
    @Bind(R.id.tv_desc_SongName) TextView tv_desc_SongName;
    @Bind(R.id.tv_desc_Detail) TextView tv_desc_Detail;
    @Bind(R.id.total_time) TextView desc_total_time;
    @Bind(R.id.description) TextView description;

    /*Changes By Amol*/
    @Bind(R.id.surface_view)
    SurfaceView surfaceView;
    @Bind(R.id.maximizes)
    ImageView maximizes;
    @Bind(R.id.starttime)
    TextView startTime;
    @Bind(R.id.endtime)
    TextView endTime;
    @Bind(R.id.media)
    ImageView playPause;
    @Bind(R.id.seekbar)
    SeekBar seekBar;
    @Bind(R.id.seeklayout)
    RelativeLayout seeklayout;
    @Bind(R.id.linear)
    LinearLayout linear;
    @Bind(R.id.frame)
    RelativeLayout frame;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.headers)
    View headers;
    public static boolean mode = true;
    MediaPlayer mediaPlayer;
    public static int pos1;
    public static int pos;
    Thread thread;
    Timer timer;
    boolean stop = true;
    SurfaceHolder surfaceHolder;
    public static int maxtouchland;
    public static boolean running;
    public static int latestProgress;
    public static int orgprogress;
    public static int currentpos;
    public static final String url = "http://www.quirksmode.org/html5/videos/big_buck_bunny.mp4";
    RelativeLayout.LayoutParams layoutParams;
    public static boolean isPlaying;
    //
    private ArrayList<LivestreamJson.DataListClass> itemsList;
    public static final String LIVE_ARRAY = "LIVE_ARRAY";
    int position;

    private static final String TAG= LiveStreamVideoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_stream_video);
        ButterKnife.bind(this);
        Intent intent= getIntent();
        itemsList = intent.getParcelableArrayListExtra(LIVE_ARRAY);
        position=intent.getIntExtra("position",0);
        initialize();
    }

    private void initialize() {
        video_desctiption.setVisibility(View.GONE);
        Log.w(TAG,itemsList.get(position).getConcertTitle());
        live_stream_title.setText(itemsList.get(position).getConcertTitle());
        artistname.setText(itemsList.get(position).getConcertTitle());
        tvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_desctiption.setVisibility(View.VISIBLE);
                tv_desc_SongName.setText(itemsList.get(position).getConcertTitle().toString());
                tv_desc_Detail.setText(itemsList.get(position).getConcertDate().toString());
                description.setText(itemsList.get(position).getDescription().toString());
            }
        });
        downs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_desctiption.setVisibility(View.GONE);

            }
        });
        // changes By Amol

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(LiveStreamVideoActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        maximizes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == true) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mode = false;
                    maxtouchland = 1;
                } else {

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mode = true;
                }

            }
        });


       /* playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    if (stop == true)
                    {
                        mediaPlayer.pause();
                        pos1=mediaPlayer.getCurrentPosition();
                        playPause.setImageResource(R.drawable.pause_video_vert);
                        stop = false;
                    }
                    else
                    {
                        mediaPlayer.start();
                        seekbarplayvideo();
                        updateTime();
                        running=true;
                        playPause.setImageResource(R.drawable.play_video_vert);
                        stop = true;
                    }

                }

        });*/


    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        mediaPlayer.start();

        // if we want to play video again then use below method

        // mp.setLooping(true);

        String secondsString = "";
        String finalTime = "";

        isPlaying = true;

        playPause.setClickable(true);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stop == true) {
                    mp.pause();
                    pos1 = mp.getCurrentPosition();
                    playPause.setImageResource(R.drawable.pause_video_vert);
                    stop = false;
                } else {
                    mp.start();
                    running = true;
                    seekbarplayvideo();
                    updateTime();
                    playPause.setImageResource(R.drawable.play_video_vert);
                    stop = true;
                }

            }

        });


        int hours;

        hours = (int) (mediaPlayer.getDuration() / (1000 * 60 * 60));

        if (hours > 0) {
            finalTime = hours + ":";
        }
        int minutes = (int) (mediaPlayer.getDuration() % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((mediaPlayer.getDuration() % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        endTime.setText(finalTime + minutes + ":" + secondsString);


        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        progressBar.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playPause.setImageResource(R.drawable.pause_video_vert);
                Toast.makeText(LiveStreamVideoActivity.this, "Video Completed", Toast.LENGTH_SHORT).show();
            }
        });


        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (percent < mp.getDuration()) {
                    seekBar.setSecondaryProgress(percent);
                    seekBar.setSecondaryProgress(percent / 100);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if (fromUser) {
                    if (mediaPlayer != null) {
                        if (seekBar.getProgress() < latestProgress) {
                            mediaPlayer.seekTo(progress);
                        }
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                orgprogress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                System.out.println("THE PROCESS TIMUING" + orgprogress + " " + seekBar.getProgress());

                if (orgprogress > seekBar.getProgress()) {
                    latestProgress = orgprogress;
                    currentpos = seekBar.getProgress();
                    mediaPlayer.seekTo(seekBar.getProgress());
                    System.out.println("THE Latest Process" + latestProgress + " " + seekBar.getProgress());

                } else {
                    seekBar.setProgress(orgprogress);
                }


            }
        });

        progressBar.setVisibility(View.GONE);

        updateTime();

        seekbarplayvideo();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(surfaceHolder);
        try {
            mediaPlayer.setDataSource(url);
            Log.d("Links", itemsList.get(position).getConcertLink());
            mediaPlayer.prepareAsync();
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnPreparedListener(LiveStreamVideoActivity.this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            pos = mediaPlayer.getCurrentPosition();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void seekbarplayvideo() {
        seekBar.setMax(mediaPlayer.getDuration());

        if (running == true) {
            mediaPlayer.seekTo(pos1);
        }

        if (pos != 0) {
            mediaPlayer.seekTo(pos);
            mediaPlayer.start();
        }


        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!Thread.interrupted()) {
                    try {

                        while (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());

                           /* try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void updateTime() {

        final String[] finalTime = {""};
        final String[] secondsString = {""};
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            startTime.post(new Runnable() {
                                @Override
                                public void run() {

                                    int hours;

                                    hours = (int) (mediaPlayer.getCurrentPosition() / (1000 * 60 * 60));

                                    if (hours > 0) {
                                        finalTime[0] = hours + ":";
                                    }
                                    int minutes = (int) (mediaPlayer.getCurrentPosition() % (1000 * 60 * 60)) / (1000 * 60);
                                    int seconds = (int) ((mediaPlayer.getCurrentPosition() % (1000 * 60 * 60)) % (1000 * 60) / 1000);

                                    if (seconds < 10) {
                                        secondsString[0] = "0" + seconds;
                                    } else {
                                        secondsString[0] = "" + seconds;
                                    }

                                    startTime.setText(finalTime[0] + minutes + ":" + secondsString[0] + "");
                                }
                            });
                        } else {
                            timer.cancel();
                            timer.purge();
                        }
                    }
                });

            }
        }, 0, 1000);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutParams = (RelativeLayout.LayoutParams) surfaceView.getLayoutParams();
            RelativeLayout.LayoutParams params;
            params = new RelativeLayout.LayoutParams(0, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            surfaceView.setLayoutParams(params);
            RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, frame.getId());
            paramsBottom.setMargins(10, 20, 10, 10);
            seeklayout.setLayoutParams(paramsBottom);
            headers.setVisibility(View.GONE);
//            bgimg.setVisibility(View.GONE);
            seeklayout.setVisibility(View.VISIBLE);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            surfaceView.setLayoutParams(layoutParams);
            headers.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsBottom.addRule(RelativeLayout.BELOW, linear.getId());
            paramsBottom.setMargins(10, 20, 10, 0);
            seeklayout.setLayoutParams(paramsBottom);
            seeklayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        if (mediaPlayer != null) {
            pos = mediaPlayer.getCurrentPosition();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



}
