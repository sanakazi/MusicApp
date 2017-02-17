package com.musicapp.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.MainActivity;
import com.musicapp.activities.PlayerScreenListActivity;
import com.musicapp.others.SeekArc;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PalseeTrivedi on 12/27/2016.
 */
public class BackgroundSoundService extends Service {

    private static final String TAG = null;
    public static MediaPlayer mPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("bgService os repeate false");
        try {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(AudioPlayerActivity.AudioURL);
            mPlayer.prepare();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("bgService os repeate false on start");

        if (AudioPlayerActivity.isFirstClick) {
            AudioPlayerActivity.isFirstClick = false;
            AudioPlayerActivity.isPlaying = true;
            AudioPlayerActivity.ivPlay.setImageResource(R.drawable.play_audio);
            System.out.println("FIRST CLICK FALSe");
            if (!mPlayer.isPlaying()) {
                mPlayer.start();
                AudioPlayerActivity.seekBar.setProgress(mPlayer.getCurrentPosition());
                AudioPlayerActivity.seekBar.setMax(mPlayer.getDuration());
                AudioPlayerActivity.tvStartTime.setText(AudioPlayerActivity.getDurationInSeconds(mPlayer.getCurrentPosition()));
                AudioPlayerActivity.tvDuration.setText(AudioPlayerActivity.getDurationInSeconds(mPlayer.getDuration()));
            }

        }

        AudioPlayerActivity.seekBar.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                mPlayer.seekTo(AudioPlayerActivity.seekBar.getProgress());
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                audioPlayerActivity.updateProgressBar();
            }
        });


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                AudioPlayerActivity.isPlaying = false;
                AudioPlayerActivity.isPause = false;
                AudioPlayerActivity.seekBar.setProgress(0);
                AudioPlayerActivity.tvStartTime.setText("00:00:00");
                AudioPlayerActivity.tvDuration.setText("00:00:00");

                if (AudioPlayerActivity.audio_itemsList.size()!=0){
                 if (AudioPlayerActivity.index!=AudioPlayerActivity.audio_itemsList.size()-1){
                     int index=AudioPlayerActivity.index+1;
                     AudioPlayerActivity.index=index;
                     PlayerScreenListActivity.index=index;
                     AudioPlayerActivity audioPlayerActivity=new AudioPlayerActivity();
                     audioPlayerActivity.playSong(AudioPlayerActivity.index);

                 }
                }


            }
        });

        return START_NOT_STICKY;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO


    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {

    }

    public void onPause() {

    }

    @Override
    public void onDestroy() {
        /*if(AudioPlayerActivity.isPlaying){
            mPlayer.stop();
        }*/
        mPlayer.release();
    }

    @Override
    public void onLowMemory() {

    }

}
