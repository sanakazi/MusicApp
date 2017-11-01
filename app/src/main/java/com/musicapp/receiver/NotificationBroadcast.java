package com.musicapp.receiver;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.musicapp.R;
import com.musicapp.activities.AboutActivity;
import com.musicapp.activities.ArtistActivity;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.BrowseItemListActivity;
import com.musicapp.activities.ChangePasswordActivity;
import com.musicapp.activities.GenreActivity;
import com.musicapp.activities.HomeItemClickDetailsActivity;
import com.musicapp.activities.HomeItemClickListActivity;
import com.musicapp.activities.MainActivity;
import com.musicapp.activities.PlayListActivity;
import com.musicapp.activities.PlaylistSongActivity;
import com.musicapp.activities.SearchActivity;
import com.musicapp.activities.UserProfileActivity;
import com.musicapp.others.Controls;
import com.musicapp.others.PlayerConstants;
import com.musicapp.service.BackgroundSoundService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.musicapp.service.BackgroundSoundService.currentVersionSupportBigNotification;
import static com.musicapp.service.BackgroundSoundService.notification;

/**
 * Created by AmolGursali on 3/8/2017.
 */

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            // Below switch is for Lock Screen Notification Handling

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    Toast.makeText(context, "KEYCODE HEADSETHOOK", Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
//                    Toast.makeText(context, "KEYCODE MEDIA_PLAY_PAUSE", Toast.LENGTH_SHORT).show();
//                    BackgroundSoundService.mPlayer.pause();

                    // if & else for changing operations like play/pause

                    if (!PlayerConstants.SONG_PAUSED) {
                        Controls.pauseControl(context);
                    } else {
                        Controls.playControl(context);
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    // Play Song on Lock Screen Notification
//                    BackgroundSoundService.mPlayer.start();
                    Toast.makeText(context, "KEYCODE PLAY", Toast.LENGTH_SHORT).show();

                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    // Pause Song on Lock Screen Notification
//                    BackgroundSoundService.mPlayer.pause();
                    Toast.makeText(context, "KEYCODE PAUSE", Toast.LENGTH_SHORT).show();

                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    // STOP Song on Lock Screen Notification
//                    BackgroundSoundService.mPlayer.stop();
                    Toast.makeText(context, "KEYCODE STOP", Toast.LENGTH_SHORT).show();

                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    AudioPlayerActivity.isUrlChanged = true;
                    if (AudioPlayerActivity.audio_itemsList.size() != 0) {
                        if (AudioPlayerActivity.index != AudioPlayerActivity.audio_itemsList.size() - 1) {
                            AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                            audioPlayerActivity.playSong(AudioPlayerActivity.index + 1);
                        }
                        PlayerConstants.SONG_PAUSED = false;
                    }

                    // Lock screen Notification Next Button Listener
                    // Toast.makeText(context, "Lock screen Notification Next Button Listener", Toast.LENGTH_SHORT).show();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:

                    Controls.previousControl(context);
                    AudioPlayerActivity.isUrlChanged = true;
                    if (AudioPlayerActivity.audio_itemsList.size() != 0) {
                        System.out.println("IF AUDIO LIST SIZE NOT ZERO" + AudioPlayerActivity.index + " " + AudioPlayerActivity.audio_itemsList.size());
                        if (AudioPlayerActivity.index != 0) {
                            AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                            audioPlayerActivity.playSong(AudioPlayerActivity.index - 1);
                        }
                        PlayerConstants.SONG_PAUSED = false;
                    }

                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    // Lock screen Notification Previous Button Listener
                    // Toast.makeText(context, "Lock screen Notification Previous Button Listener", Toast.LENGTH_SHORT).show();

                    break;
            }
        } else {
            // Notifications icons
            if (intent.getAction().equals(BackgroundSoundService.NOTIFY_PLAY)) {
                // Change UI when clicked on play button
                Controls.playControl(context);
            } else if (intent.getAction().equals(BackgroundSoundService.NOTIFY_PAUSE)) {
                // Change UI when clicked on pause button
                Controls.pauseControl(context);
            } else if (intent.getAction().equals(BackgroundSoundService.NOTIFY_NEXT)) {
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());//change by amol

                // Perform Operations on click of Next Button
                // Controls .nextControl(context);
                AudioPlayerActivity.isUrlChanged = true;
                if (AudioPlayerActivity.audio_itemsList.size() != 0) {
                    if (AudioPlayerActivity.index != AudioPlayerActivity.audio_itemsList.size() - 1) {
                        AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                        audioPlayerActivity.playSong(AudioPlayerActivity.index + 1);
                    }
                    PlayerConstants.SONG_PAUSED = false;
                }

                //  Toast.makeText(context, "Next Button", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(BackgroundSoundService.NOTIFY_DELETE)) {
//<------------------------------changes by palsee start 16Oct2017--------------------------------------->
                try {
                    MainActivity.bottomPlayerView.setVisibility(View.GONE);
                    AboutActivity.bottomPlayerView.setVisibility(View.GONE);
                    ArtistActivity.bottomPlayerView.setVisibility(View.GONE);
                    BrowseItemListActivity.bottomPlayerView.setVisibility(View.GONE);
                    ChangePasswordActivity.bottomPlayerView.setVisibility(View.GONE);
                    GenreActivity.bottomPlayerView.setVisibility(View.GONE);
                    HomeItemClickDetailsActivity.bottomPlayerView.setVisibility(View.GONE);
                    HomeItemClickListActivity.bottomPlayerView.setVisibility(View.GONE);
                    PlayListActivity.bottomPlayerView.setVisibility(View.GONE);
                    PlaylistSongActivity.bottomPlayerView.setVisibility(View.GONE);
                    SearchActivity.bottomPlayerView.setVisibility(View.GONE);
                    UserProfileActivity.bottomPlayerView.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//<------------------------------changes by palsee finish 16Oct2017--------------------------------------->


                Intent i = new Intent(context, BackgroundSoundService.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.stopService(i);
                BackgroundSoundService.mPlayer.release();
                try {
                    ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
                    PackageManager pm = context.getPackageManager();
                    PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
                    String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
                    System.out.println("FORE GROUNDDDD" + foregroundTaskPackageName + " " + foregroundTaskAppName);
                    if (foregroundTaskAppName.matches("Dcom.musicapp")) {
                        Intent in = new Intent(context, MainActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(in);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                // System.exit(0);
                // Lock Screen Notifications are visible
            } else if (intent.getAction().equals(BackgroundSoundService.NOTIFY_PREVIOUS)) {
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());//change by amol
//               Controls.previousControl(context);
                // Perform Operations on click of Previous Button
                Controls.previousControl(context);
                AudioPlayerActivity.isUrlChanged = true;

                if (AudioPlayerActivity.audio_itemsList.size() != 0) {
                    System.out.println("IF AUDIO LIST SIZE NOT ZERO");
                    if (AudioPlayerActivity.index != 0) {
                        System.out.println("IF AUDIO LIST SIZE INNER IF");

                        AudioPlayerActivity audioPlayerActivity = new AudioPlayerActivity();
                        audioPlayerActivity.playSong(AudioPlayerActivity.index - 1);
                    }
                    PlayerConstants.SONG_PAUSED = false;
                }

                // Toast.makeText(context, "Previous Button", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
