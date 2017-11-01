package com.musicapp.others;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.aes.AESHelper;
import com.musicapp.encrypt.CryptLib;
import com.squareup.picasso.Picasso;

/**
 * Created by PalseeTrivedi on 12/27/2016.
 */
public class VideoStream implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, OnSeekBarChangeListener,
        OnSeekCompleteListener, MediaPlayer.OnBufferingUpdateListener {


    private int STATUS = 0;
    private final int STATUS_STOPED = 1;
    private final int STATUS_PLAYING = 2;
    private final int STATUS_PAUSED = 3;

    private Context ctx;
    private WakeLock wakeLock;
    public MediaPlayer mPlayer;
    private SeekBar seekBar = null;
    private SurfaceView surfaceView;
    //  private TextView lblCurrentPosition = null;
    // private TextView lblDuration = null;
    public static Timer timer = null;
    /*boolean isCompleted=false;*/

    //for encrypted text
    CryptLib crypt;

    public VideoStream(Context ctx) {
        this.ctx = ctx;

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);

        mPlayer.setOnBufferingUpdateListener(this);

        PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyMediaPlayer");
    }

    /**
     * Sets up the surface dimensions to display
     * the video on it.
     */
    public void setUpVideoDimensions() {


        ComonHelper comonHelper = new ComonHelper(ctx);
        int height = comonHelper.getScreenHeight();
        int width = comonHelper.getScreenWidth();
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        if (VideoPlayerActivity.isPotrait) {

            lp.height = height / 3;
        } else {
            lp.height = height;
        }



      /*  // Get the dimensions of the video
        int videoWidth = mPlayer.getVideoWidth();
        int videoHeight = mPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen

        ComonHelper comonHelper=new ComonHelper(ctx);
        int screenWidth = comonHelper.getScreenWidth();
        int screenHeight = comonHelper.getScreenHeight();
      *//*  int screenWidth = ((Activity) ctx).getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = ((Activity) ctx).getWindowManager().getDefaultDisplay().getHeight();*//*
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) (((float) screenWidth / videoProportion));
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }*/

        // Commit the layout parameters
        surfaceView.setLayoutParams(lp);
        VideoPlayerActivity.progressBar.setVisibility(View.GONE);
        VideoPlayerActivity.ivPlay.setClickable(true);
    }

    /**
     * Pause the video playback.
     */
    public void pause() {
        if (VideoPlayerActivity.isPlaying) {
            if (mPlayer != null) {
                mPlayer.pause();
            }
            STATUS = STATUS_PAUSED;

            wakeLockRelease();
        }
    }

    /**
     * Start the video playback.
     *
     * @throws IOException
     * @throws IllegalStateException
     */
    public void play() throws IllegalStateException, IOException {
        System.out.println("PLAYINGGGG" + STATUS + " " + STATUS_PLAYING + " " + STATUS_PAUSED + " " + STATUS_STOPED + " " + VideoPlayerActivity.index);

        if (STATUS != STATUS_PLAYING) {
            wakeLockAcquire();
            System.out.println("PLAYINGGGG" + STATUS);
            if (STATUS == STATUS_PAUSED) {
                if (mPlayer != null) {
                    System.out.println("PLAYINGGGG" + STATUS);
                    mPlayer.start();
                    VideoPlayerActivity.isPlaying = true;
                } else {
                    System.out.println("PLAYINGGGG" + STATUS);
                    if (mPlayer != null) {
                        mPlayer.prepareAsync();
                        mPlayer.setScreenOnWhilePlaying(true);
                        mPlayer.setOnPreparedListener(this);


                    }

                }
            } else {

                if (mPlayer != null) {
                    System.out.println("PLAYINGGGG IF ELSE" + STATUS);
                    mPlayer.prepareAsync();
                    mPlayer.setScreenOnWhilePlaying(true);
                    mPlayer.setOnPreparedListener(this);
                    System.out.println("PLAYINGGGG IF ELSE" + STATUS);
                    // mPlayer.start();
                    System.out.println("PLAYINGGGG IF ELSE" + STATUS);

                }
            }

            STATUS = STATUS_PLAYING;
        }

        updateMediaProgress();
    }

    /**
     * Sets up the video source.
     *
     * @param source - The video address
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws IOException
     */
    public void setUpVideoFrom(String source) throws IllegalArgumentException, IllegalStateException, IOException {
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            crypt = new CryptLib();

            String iv = "musicapp2017aurum";
            String key = CryptLib.SHA256(iv, 32);

            source = crypt.decrypt(
                    TextUtils.htmlEncode(source), key, iv);

            Log.e("Encrypted format : ", "Encrypted format : "
                    + source);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        System.out.println("PLAYING VIDEO URL" + source);
        Uri uri = Uri.parse(source);
        mPlayer.setDataSource(ctx, uri);


//		else {
//			Uri uri = Uri.parse(source);
//			mPlayer.setDataSource(ctx, uri);
//		}

    }

    /**
     * Release the video object.
     * This will stops the playback and release the memory used.
     */
    public void release() {
        System.out.println("RELEASED CALLED");
        if (mPlayer != null) {
            System.out.println("RELEASED CALLED IF");
            mPlayer.seekTo(0);
            mPlayer.release();
            mPlayer = null;
            STATUS = 0;
            if (!VideoPlayerActivity.isAnotherOptionScreen) {
                System.out.println("RELEASED CALLED IF");
                reset();
            }
        }


    }

    /**
     * Reset the seekbar.
     */
    private void reset() {

        if (VideoPlayerActivity.detroyedTime != 0) {
          /*  try {
                VideoPlayerActivity.progressBar.setVisibility(View.VISIBLE);

                mPlayer = new MediaPlayer();
                VideoPlayerActivity.isFirstCall=true;
                mPlayer.setOnCompletionListener(this);
                setUpVideoFrom(VideoPlayerActivity.VideoURL);
                PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
                wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyMediaPlayer");
                setSeekBar(VideoPlayerActivity.seekBarVideo);
                setDisplay(VideoPlayerActivity.frameVideo, VideoPlayerActivity.sHolder);
                play();
                VideoPlayerActivity.isPlaying = true;
                if (VideoPlayerActivity.isPotrait) {
                    VideoPlayerActivity.ivPlay.setImageResource(R.drawable.play_video_vert);
                } else {
                    VideoPlayerActivity.ivPlay.setImageResource(R.drawable.play_video);

                }
                VideoPlayerActivity.isUrlChange = false;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        } else {


            System.out.println("RESET CALLED");
            System.out.println("IS CHANGED TRUEE RESET CALLED");
            if (seekBar != null) {
                seekBar.setProgress(0);
                if (timer != null) {
                    timer.cancel();
                }
                VideoPlayerActivity.isReleased = true;
                VideoPlayerActivity.isPlaying = false;
                if (VideoPlayerActivity.isPotrait) {
                    VideoPlayerActivity.ivPlay.setImageResource(R.drawable.pause_video_vert);
                } else {
                    VideoPlayerActivity.ivPlay.setImageResource(R.drawable.stop_video);
                }
                VideoPlayerActivity.tvStartTime.setText("00:00:00");
                VideoPlayerActivity.tvDuration.setText("00:00:00");
            }
            if (VideoPlayerActivity.isUrlChange) {
                System.out.println("IS CHANGED TRUEE");
                try {
                    VideoPlayerActivity.progressBar.setVisibility(View.VISIBLE);
                    VideoPlayerActivity.ivPlay.setClickable(false);

                    mPlayer = new MediaPlayer();
                    mPlayer.setOnCompletionListener(this);
                    setUpVideoFrom(VideoPlayerActivity.VideoURL);
                    mPlayer.setOnPreparedListener(this);
                    PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
                    wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyMediaPlayer");
                    setSeekBar(VideoPlayerActivity.seekBarVideo);
                    setDisplay(VideoPlayerActivity.frameVideo, VideoPlayerActivity.sHolder);
                    //onPrepared(mPlayer);
                    play();
                    VideoPlayerActivity.isPlaying = true;
                    if (VideoPlayerActivity.isPotrait) {
                        VideoPlayerActivity.ivPlay.setImageResource(R.drawable.play_video_vert);
                    } else {
                        VideoPlayerActivity.ivPlay.setImageResource(R.drawable.play_video);

                    }
                    VideoPlayerActivity.isUrlChange = false;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {


                try {
                    System.out.println("IS CHANGED FALSE CLICKED");

                    VideoPlayerActivity.progressBar.setVisibility(View.VISIBLE);
                    VideoPlayerActivity.ivPlay.setClickable(false);
                    int index = VideoPlayerActivity.index + 1;
                    VideoPlayerActivity.index = index;
                    if (VideoPlayerActivity.video_itemsList.size() != 0) {
                        if (index != VideoPlayerActivity.video_itemsList.size()) {
                            if (index < VideoPlayerActivity.video_itemsList.size()) {
                                VideoPlayerActivity.VideoURL = VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongURL();
                                VideoPlayerActivity.tvSong.setText(VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongName());
                                VideoPlayerActivity.latest_song_name_detail.setText(VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongName());

                                VideoPlayerActivity.tvDesSongName.setText(VideoPlayerActivity.video_itemsList.get(index).getColumns().getSongName());
                                String url = VideoPlayerActivity.video_itemsList.get(index).getColumns().getThumbnailImage();
                                if (!url.matches("") && url != null) {
                                    Picasso.with(ctx).load(url).into(VideoPlayerActivity.latest_song_img_detail);
                                    Picasso.with(ctx).load(url).into(VideoPlayerActivity.ivDesSongImage);
                                }


                                System.out.println("ELSE COMPLETE RESET" + VideoPlayerActivity.VideoURL + " " + index);
                                mPlayer = new MediaPlayer();
                                mPlayer.setOnCompletionListener(this);
                                setUpVideoFrom(VideoPlayerActivity.VideoURL);

                                mPlayer.setOnPreparedListener(this);
                                PowerManager powerManager = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
                                wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyMediaPlayer");
                                setSeekBar(VideoPlayerActivity.seekBarVideo);
                                setDisplay(VideoPlayerActivity.frameVideo, VideoPlayerActivity.sHolder);
                                //onPrepared(mPlayer);
                                play();
                                VideoPlayerActivity.isPlaying = true;
                                if (VideoPlayerActivity.isPotrait) {
                                    VideoPlayerActivity.ivPlay.setImageResource(R.drawable.play_video_vert);
                                } else {
                                    VideoPlayerActivity.ivPlay.setImageResource(R.drawable.play_video);

                                }
                                VideoPlayerActivity.isUrlChange = false;
                            }

                        }
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

    /**
     * Set up the surface to display the video on it.
     *
     * @param holder - The surface to display the video.
     */
    public void setDisplay(SurfaceView surfaceView, SurfaceHolder holder) {
        this.surfaceView = surfaceView;
        mPlayer.setDisplay(holder);
        System.out.println("SEtting surface");
    }

    /**
     * Set up a listener to execute when the video is ready to playback.
     *
     * @param listener - The Listener.
     */
    public void setOnPrepared(MediaPlayer.OnPreparedListener listener) {
        mPlayer.setOnPreparedListener(listener);
    }

    /**
     * Sets up a seekbar and two labels to display the video progress.
     *
     * @param seekBar // * @param lblCurrentPosition
     *                //* @param lblDuration
     */
    public void setSeekBar(SeekBar seekBar/*, TextView lblCurrentPosition, TextView lblDuration*/) {


        this.seekBar = seekBar;
        // this.lblCurrentPosition = lblCurrentPosition;
        //  this.lblDuration = lblDuration;
        System.out.println("SETTING SEEK BAr");
        seekBar.setOnSeekBarChangeListener(this);
        if (VideoPlayerActivity.detroyedTime != 0) {
            seekBar.setProgress(VideoPlayerActivity.detroyedTime);
            VideoPlayerActivity.tvStartTime.setText(getDurationInSeconds(VideoPlayerActivity.detroyedTime));
        } else {

            seekBar.setProgress(0);
            VideoPlayerActivity.tvStartTime.setText("00:00:00");
            VideoPlayerActivity.tvDuration.setText("00:00:00");
        }
    }

    /**
     * Get a string with the video's duration.
     * The format of the string is hh:mm:ss
     *
     * @param sec - The seconds to convert.
     * @return A string formated.
     */
    private String getDurationInSeconds(int sec) {
        sec = sec / 1000;
        int hours = sec / 3600;
        int minutes = (sec / 60) - (hours * 60);
        int seconds = sec - (hours * 3600) - (minutes * 60);
        String formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return formatted;
    }

    /**
     * Set the current position of the video in the seekbar
     *
     * @param progress - The seconds to seek the bar
     */
    private void setCurrentPosition(int progress) {
        System.out.println("THE PROCESS" + getDurationInSeconds(progress));
        VideoPlayerActivity.tvStartTime.setText(getDurationInSeconds(progress));
    }

    public int getCurrentPosition() {
        int pos = 0;
        if (mPlayer != null) {
            pos = mPlayer.getCurrentPosition();
        }
        return pos;
    }

    public void setCurrentPositionToMplayer(int position) {
        mPlayer.seekTo(position);
    }

    /**
     * Acquire wakelock the screen.
     */
    private void wakeLockAcquire() {
        wakeLock.acquire();
    }

    /**
     * Release the wakelock.
     */
    private void wakeLockRelease() {
        if (wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Throwable th) {
                th.printStackTrace();
                // ignoring this exception, probably wakeLock was already released
            }
        } else {
            // should never happen during normal workflow
            Log.e("", "Wakelock reference is null");
        }
    }

    /**
     * Update the seekbar while the video is playing.
     */
    public void updateMediaProgress() {
        timer = new Timer("progress Updater");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    public void run() {
                        if (timer != null) {
                            try {
                                if (mPlayer != null) {
                                    if (VideoPlayerActivity.isPlaying) {
                                        if (seekBar != null) {
                                            seekBar.setProgress(mPlayer.getCurrentPosition());
                                            setCurrentPosition(mPlayer.getCurrentPosition());
                                            VideoPlayerActivity.tvStartTime.setText(getDurationInSeconds(mPlayer.getCurrentPosition()));
                                        }
                                    }

                                }
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (!VideoPlayerActivity.tvStartTime.getText().toString().matches("00:00:00")) {
            if (!VideoPlayerActivity.isAnotherOptionScreen) {
                if (!VideoPlayerActivity.isFirstCall) {
                    System.out.println("Complete listener called");
                    release();
                }
                VideoPlayerActivity.isFirstCall = false;
            }

        }


     /*   if (!VideoPlayerActivity.isUrlChange) {

            VideoPlayerActivity.tvDuration.setText("00:00:00");
            VideoPlayerActivity.tvStartTime.setText("00:00:00");


            if (VideoPlayerActivity.videoList.size() != 0) {
                if (VideoPlayerActivity.index != VideoPlayerActivity.videoList.size() - 1) {
                    if (timer != null) {
                        timer.cancel();
                    }

                    System.out.println("INDEXXXXXX" + VideoPlayerActivity.index);
                    int index = VideoPlayerActivity.index + 1;

                    if (index == VideoPlayerActivity.index + 2) {
                        index = index - 2;
                    }else {
                        VideoPlayerActivity.index=index;
                    }


                   *//* System.out.println("INDEXXXXXX" + index);
                    VideoPlayerActivity.index = index;
                    System.out.println("INDEXXXXXX" + VideoPlayerActivity.index);*//*

                    try {

                        VideoPlayerActivity.player = new VideoStream(ctx);
                        VideoPlayerActivity.player.setUpVideoFrom(VideoPlayerActivity.videoList.get(index).getVideoUrl());
                        VideoPlayerActivity.index=index;
                        VideoPlayerActivity.player.setSeekBar(seekBar);
                        VideoPlayerActivity.player.setDisplay(VideoPlayerActivity.frameVideo, VideoPlayerActivity.sHolder);
                        VideoPlayerActivity.player.onPrepared(mPlayer);
                        VideoPlayerActivity.player.play();
                        VideoPlayerActivity.isPlaying = true;
                        VideoPlayerActivity.ivPlay.setImageResource(R.drawable.play_video);
                        System.out.println("INDEXXXXXX" + index);

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    stop();
                }
            } else {

                stop();
            }

        }*/


    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        // VideoPlayerActivity.frameVideo.setBackgroundColor(Color.TRANSPARENT);
        if (VideoPlayerActivity.detroyedTime != 0) {
            mp.seekTo(VideoPlayerActivity.detroyedTime);
        }
        mp.start();
        VideoPlayerActivity.isPlaying = true;
        if (seekBar != null) {
            mp.setOnSeekCompleteListener(this);
            int duration = (int) mp.getDuration();
            seekBar.setMax(duration);
            VideoPlayerActivity.tvDuration.setText(getDurationInSeconds(duration));


            System.out.println("PREPAREDDDD");


        }
        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        VideoPlayerActivity.progressBar.setVisibility(View.VISIBLE);
                        VideoPlayerActivity.ivPlay.setClickable(false);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        VideoPlayerActivity.progressBar.setVisibility(View.GONE);
                        VideoPlayerActivity.ivPlay.setClickable(true);
                        break;
                }
                return false;
            }
        });
        setUpVideoDimensions();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setCurrentPosition(progress);
        // seekBar.setSecondaryProgress(mPlayer.getCurrentPosition()+5);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            if (mPlayer != null) {
                mPlayer.seekTo(seekBar.getProgress());
            }
            System.out.println("ON TRACKING" + mPlayer.isPlaying());
            if (!VideoPlayerActivity.isPlaying) {
                System.out.println("ON TRACKING" + VideoPlayerActivity.detroyedTime);
                VideoPlayerActivity.detroyedTime = mPlayer.getCurrentPosition();
                System.out.println("ON TRACKING" + VideoPlayerActivity.detroyedTime);
            }
            updateMediaProgress();
        } catch (Exception e) {
            System.out.println("ON TRACKING" + e.toString());
            e.printStackTrace();
        }

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        System.out.println("UPDATION CALLED" + percent);
        // seekBar.setSecondaryProgress(mPlayer.getCurrentPosition()+5);
        double ratio = percent / 100.0;
        int bufferingLevel = (int) (mp.getDuration() * ratio);

        seekBar.setSecondaryProgress(bufferingLevel);
    }
}