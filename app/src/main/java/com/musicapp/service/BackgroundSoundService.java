package com.musicapp.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.MainActivity;
import com.musicapp.activities.PlayListActivity;
import com.musicapp.activities.PlayerScreenListActivity;
import com.musicapp.activities.VideoPlayerActivity;
import com.musicapp.encrypt.CryptLib;
import com.musicapp.others.ComonHelper;
import com.musicapp.others.Controls;
import com.musicapp.others.PlayerConstants;
import com.musicapp.others.SeekArc;
import com.musicapp.receiver.NotificationBroadcast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.musicapp.activities.AudioPlayerActivity.audio_itemsList;

/**
 * Created by PalseeTrivedi on 12/27/2016.
 */
public class BackgroundSoundService extends Service implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = null;
    public static MediaPlayer mPlayer;
    public static boolean isProcessChanging = false, isBuffering = false;
    CryptLib crypt;

    private MediaSessionCompat mediaSession;
    //for notification player
    public static int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_PREVIOUS = "com.musicapp.previous";
    public static final String NOTIFY_DELETE = "com.musicapp.delete";
    public static final String NOTIFY_PAUSE = "com.musicapp.pause";
    public static final String NOTIFY_PLAY = "com.musicapp.play";
    public static final String NOTIFY_NEXT = "com.musicapp.next";
    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;
    public static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;
    public static NotificationManager notificationManager;
    Notification.Builder myNotifications;
    BroadcastReceiver broadcastReceiver;
    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    public static Notification notification;

    Timer notificationViewTimer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiver = new NotificationBroadcast();
        System.out.println("bgService os repeate false");
        try {

            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            //for notification player
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
            currentVersionSupportBigNotification = Controls.currentVersionSupportBigNotification();
            currentVersionSupportLockScreenControls = Controls.currentVersionSupportLockScreenControls();
            //notification player done
            try {
                crypt = new CryptLib();

                String iv = "musicapp2017aurum";
                String key = CryptLib.SHA256(iv, 32);

                AudioPlayerActivity.AudioURL = crypt.decrypt(
                        TextUtils.htmlEncode(AudioPlayerActivity.AudioURL), key, iv);

                Log.e("Encrypted format : ", "Encrypted format : "
                        + AudioPlayerActivity.AudioURL);

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            mPlayer.setDataSource(AudioPlayerActivity.AudioURL);
            mPlayer.prepareAsync();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int onStartCommand(Intent intent, final int flags, int startId) {
        System.out.println("bgService os repeate false on start");

        //for notification player
        if (currentVersionSupportLockScreenControls) {
            RegisterRemoteClient();
        }
        newNotification();
        PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                String message = (String) msg.obj;
                if (mPlayer == null)
                    return false;
                if (message.equalsIgnoreCase("Play")) {
                    PlayerConstants.SONG_PAUSED = false;
                    if (currentVersionSupportLockScreenControls) {
                        remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                    }
                    AudioPlayerActivity.isPlaying = true;
                    mPlayer.start();
                } else if (message.equalsIgnoreCase("Pause")) {

                    PlayerConstants.SONG_PAUSED = true;
                    AudioPlayerActivity.isPlaying = false;
                    if (currentVersionSupportLockScreenControls) {
                        remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                    }
                    mPlayer.pause();
                }
                newNotification();
                try {
                    AudioPlayerActivity.changeButton();
                    MainActivity.changeBottomUI();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("TAG", "TAG Pressed: " + message);
                return false;
            }
        });
// changes by amol

        PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                audio_itemsList.get(PlayerConstants.SONG_NUMBER).getColumns().getSongName();
                newNotification();
                try {

                   /* MainActivity.changeUI();
                    AudioPlayerActivity.changeUI();*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
//done by amol
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                AudioPlayerActivity.isUrlChanged = false;
                System.out.println("FIRST CLICK " + AudioPlayerActivity.isFirstClick);
                AudioPlayerActivity.isFirstClick = false;
                AudioPlayerActivity.isPlaying = true;
                PlayerConstants.SONG_PAUSED = false;
                AudioPlayerActivity.ivPlay.setImageResource(R.drawable.play_audio);
                System.out.println("FIRST CLICK FALSe");
                System.out.println("LATEST SONG NAME" + AudioPlayerActivity.songName);
                newNotification();
                if (!mPlayer.isPlaying()) {

                    //for notification player
                    if (currentVersionSupportLockScreenControls) {
                        Data();
                        remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                    }
                    //notification player done

                    System.out.println("FIRST CLICK is playing false");
                    mPlayer.start();
                    AudioPlayerActivity.progressBarPlayer.setVisibility(View.GONE);
                    if (PlayerScreenListActivity.progressBarPlayer != null) {
                        PlayerScreenListActivity.progressBarPlayer.setVisibility(View.GONE);
                        PlayerScreenListActivity.ivPlay.setVisibility(View.VISIBLE);
                        PlayerScreenListActivity.ivPlay.setImageResource(R.drawable.play_audio);
                    }
                    AudioPlayerActivity.ivPlay.setVisibility(View.VISIBLE);
                    AudioPlayerActivity.seekBar.setProgress(mPlayer.getCurrentPosition());
                    AudioPlayerActivity.seekBar.setMax(mPlayer.getDuration());
                    AudioPlayerActivity.tvStartTime.setText(AudioPlayerActivity.getDurationInSeconds(mPlayer.getCurrentPosition()));
                    AudioPlayerActivity.tvDuration.setText(AudioPlayerActivity.getDurationInSeconds(mPlayer.getDuration()));
                }

            }
        });

        mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        AudioPlayerActivity.progressBarPlayer.setVisibility(View.VISIBLE);
                        AudioPlayerActivity.ivPlay.setVisibility(View.GONE);
                        if (PlayerScreenListActivity.progressBarPlayer != null) {
                            PlayerScreenListActivity.progressBarPlayer.setVisibility(View.VISIBLE);

                        }
                        if (PlayerScreenListActivity.ivPlay != null) {
                            PlayerScreenListActivity.ivPlay.setVisibility(View.GONE);
                        }

                        isBuffering = true;
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        AudioPlayerActivity.progressBarPlayer.setVisibility(View.GONE);
                        if (PlayerScreenListActivity.progressBarPlayer != null) {
                            PlayerScreenListActivity.progressBarPlayer.setVisibility(View.GONE);
                        }
                        AudioPlayerActivity.ivPlay.setVisibility(View.VISIBLE);
                        if (PlayerScreenListActivity.ivPlay != null) {
                            PlayerScreenListActivity.ivPlay.setVisibility(View.VISIBLE);
                            PlayerScreenListActivity.ivPlay.setImageResource(R.drawable.play_audio);
                        }
                        isBuffering = false;
                        break;
                }
                return false;
            }
        });

        AudioPlayerActivity.seekBar.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                System.out.println("background seek onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

                isProcessChanging = true;
                AudioPlayerActivity.timer.cancel();
                AudioPlayerActivity.timer = null;
                System.out.println("background seek onStartTrackingTouch" + isProcessChanging);
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

                try {
                    System.out.println("background seek onStopTrackingTouch");
                    isProcessChanging = true;
                    if (AudioPlayerActivity.timer != null) {
                        AudioPlayerActivity.timer.cancel();
                        AudioPlayerActivity.timer = null;
                    }
                    mPlayer.seekTo(seekArc.getProgress());
                    AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                    audioPlayerActivity.updateProgressBar();
                    isProcessChanging = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                System.out.println("IN COMPLETE LISTNER" + AudioPlayerActivity.isRepeat + " " + AudioPlayerActivity.isShuffle + " " + AudioPlayerActivity.isUrlChanged);
                if (AudioPlayerActivity.isRepeat) {
                    System.out.println("IN COMPLETE LISTNER" + AudioPlayerActivity.isRepeat);

                    AudioPlayerActivity.isPlaying = false;
                    AudioPlayerActivity.isPause = false;
                    AudioPlayerActivity.seekBar.setProgress(0);
                    AudioPlayerActivity.tvStartTime.setText("00:00:00");
                    AudioPlayerActivity.tvDuration.setText("00:00:00");

                    AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                    audioPlayerActivity.playSong(AudioPlayerActivity.index);


                } else if (AudioPlayerActivity.isShuffle) {
                    System.out.println("IN COMPLETE LISTNER" + AudioPlayerActivity.isShuffle);
                    Random rand = new Random();
                    int index = rand.nextInt((audio_itemsList.size() - 1) - 0 + 1) + 0;
                    System.out.println("SHUFFLE INDEX" + index);
                    PlayerScreenListActivity.index = index;
                    AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                    audioPlayerActivity.playSong(index);
                } else {
                    System.out.println("IN COMPLETE LISTNER" + "Else");
                    if (!AudioPlayerActivity.isUrlChanged) {
                        AudioPlayerActivity.isPlaying = false;
                        AudioPlayerActivity.isPause = false;

                        AudioPlayerActivity.seekBar.setProgress(0);
                        AudioPlayerActivity.tvStartTime.setText("00:00:00");
                        AudioPlayerActivity.tvDuration.setText("00:00:00");

                        if (audio_itemsList.size() != 0) {
                            if (AudioPlayerActivity.index != audio_itemsList.size() - 1) {
                                int index = AudioPlayerActivity.index + 1;
                                AudioPlayerActivity.index = index;
                                PlayerScreenListActivity.index = index;
                                AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                                audioPlayerActivity.playSong(AudioPlayerActivity.index);

                            }
                        }
                    }
                }
            }
        });

        return START_STICKY;
    }

    private void Data() {

        String img = audio_itemsList.get(PlayerConstants.SONG_NUMBER).getColumns().getThumbnailImage();
        if (remoteControlClient == null)
            return;
        RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, ComonHelper.songName);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, ComonHelper.songName);
        URL newurl;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            newurl = new URL(img);
            Bitmap mDummyAlbumArt = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());

            if (mDummyAlbumArt == null) {
                mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.default_album_art);
            } else {
                // Default image is to set here
                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, mDummyAlbumArt);
                if (currentVersionSupportLockScreenControls) {
                    notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, mDummyAlbumArt);
                }
            }
            metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
            metadataEditor.apply();
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //for notification player

    public void newNotification() {

        // changes by amol

        String songName = audio_itemsList.get(PlayerConstants.SONG_NUMBER).getColumns().getSongName();
        String img = audio_itemsList.get(PlayerConstants.SONG_NUMBER).getColumns().getThumbnailImage();

        //done by amol

        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);


        // title of notifications
        ComonHelper.notifFlag = true;
        Intent intent = new Intent(getApplicationContext(), AudioPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent1 = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, 0);

       /* notification = new Notification.Builder(getApplicationContext()).setAutoCancel(true).setContentTitle("MusicApp").setContentIntent(intent1).setSmallIcon(R.mipmap.ic_launcher).build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;*/


        System.out.println("SERVICE IMAGE URL" + ComonHelper.imageUrl);


        if (Build.VERSION.SDK_INT < 16) {
            notification = new Notification.Builder(this)
                    .setContentTitle("MusicApp")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(intent1)
                    .setAutoCancel(true).getNotification();
            notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("MusicApp")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(intent1)
                    .setAutoCancel(true).build();
            notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if (currentVersionSupportBigNotification) {
            notification.bigContentView = expandedView;
        }
        // BITMAP IMAGES

        // change Imageview in Big notification

        if (currentVersionSupportBigNotification) {
            //notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
            System.out.println("IMAGE URRRLLLLLL" + ComonHelper.imageUrl);
            //  notification.contentView.setImageViewUri(R.id.imageViewAlbumArt, Uri.parse(ComonHelper.imageUrl));
        }


        // changes by amol

        URL newurl;
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            newurl = new URL(img);
            Bitmap bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            if (bitmap != null) {
                notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, bitmap);
                if (currentVersionSupportBigNotification) {
                    notification.bigContentView.setImageViewBitmap(R.id.imageViewAlbumArt, bitmap);
                }
            } else {
                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                if (currentVersionSupportBigNotification) {
                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }//done by amol


        if (PlayerConstants.SONG_PAUSED) {
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        } else {
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }

        }
        System.out.println("TEXT OF NOTIF" + ComonHelper.songName);


        // changes by amol

        notification.contentView.setTextViewText(R.id.textSongName, songName);
        // change Imageview in small notification
//        notification.contentView.setImageViewResource(R.id.imageViewAlbumArt,R.drawable.default_album_art);
        if (currentVersionSupportBigNotification) {
            System.out.println("TEXT OF NOTIF" + ComonHelper.songName);
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
            // notification.bigContentView.setTextViewText(R.id.textAlbumName, "MusicApp");
        }//done by amol









       /* notification.contentView.setTextViewText(R.id.textSongName, ComonHelper.songName);
        // change Imageview in small notification
        //notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
        System.out.println("IMAGE URRRLLLLLL"+ComonHelper.imageUrl);
      //  notification.contentView.setImageViewUri(R.id.imageViewAlbumArt, Uri.parse(ComonHelper.imageUrl));

        //  notification.contentView.setTextViewText(R.id.textAlbumName, "MusicApp");
        if (currentVersionSupportBigNotification) {
            System.out.println("TEXT OF NOTIF" + ComonHelper.songName);
            notification.bigContentView.setTextViewText(R.id.textSongName, ComonHelper.songName);
            // notification.bigContentView.setTextViewText(R.id.textAlbumName, "MusicApp");
        }
*/
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);

    }

    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent previus = new Intent(NOTIFY_PREVIOUS);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);
        PendingIntent pPrevius = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevius);
        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if (remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        } catch (Exception ex) {

        }
    }

//notification player done

    public void onStart(Intent intent, int startId) {
        // TO DO


    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {
        notificationManager.cancelAll();
        stopForeground(true);

    }

    public void onPause() {
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onDestroy() {
        AudioPlayerActivity.isPause = false;
        AudioPlayerActivity.isPlaying = false;
        isProcessChanging = true;
        try {
            AudioPlayerActivity.timer.cancel();
            AudioPlayerActivity.timer = null;
            System.out.println("destroyed called");
            ComonHelper.timer.cancel();
            ComonHelper.timer = null;
            mPlayer.release();
            mPlayer = null;
            stopForeground(false);
            stopSelf();
            notificationManager.cancel(NOTIFICATION_ID);

            audioManager.unregisterRemoteControlClient(remoteControlClient);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        mPlayer.release();
        mPlayer = null;
        stopForeground(false);
        stopSelf();
        notificationManager.cancel(NOTIFICATION_ID);
        audioManager.unregisterRemoteControlClient(remoteControlClient);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopForeground(false);
        mPlayer.release();
        stopSelf();
        notificationManager.cancel(NOTIFICATION_ID);
        audioManager.unregisterRemoteControlClient(remoteControlClient);
        System.exit(0);
    }


    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    public Bitmap getBitmapFromURL(String strURL) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            System.out.println("BITMMMAAP ERROR" + e.toString());
            e.printStackTrace();
            return null;
        }
    }


}
