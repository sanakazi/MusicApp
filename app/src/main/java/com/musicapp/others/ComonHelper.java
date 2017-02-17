package com.musicapp.others;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.musicapp.R;
import com.musicapp.activities.AudioPlayerActivity;
import com.musicapp.activities.MainActivity;
import com.musicapp.pojos.HomeDetailsJson;
import com.musicapp.pojos.OfflineArtistItem;
import com.musicapp.service.BackgroundSoundService;
import com.musicapp.singleton.PreferencesManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PalseeTrivedi on 12/21/2016.
 */

public class ComonHelper {
    public static Timer timer;
    Context mContext;
    private static ArrayList<HomeDetailsJson.DataList> offlineSongArrayList;
    private static ArrayList<OfflineArtistItem> offlineArtistArrayList;
    static boolean duplicateArtistName;
    static boolean duplicationIndex;
    static int duplicateArtistIndex;
    static int duplicateSongIndex;

    public ComonHelper() {
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //SCREEN HEIGHT and WIDTH
    public ComonHelper(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressWarnings("deprecation")
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public int getScreenHeight() {
        int columnHeight;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnHeight = point.y;
        return columnHeight;
    }


    //For Bottom audio player
    public void bottomPlayerListner(SeekBar seekView, ImageView ivPlay, ImageView ivUp, final Context context) {

        seekView.setProgress(BackgroundSoundService.mPlayer.getCurrentPosition());
        seekView.setMax(BackgroundSoundService.mPlayer.getDuration());
        updateSeekProgressTimer(seekView, context);


        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AudioPlayerActivity.isPlaying) {
                    BackgroundSoundService.mPlayer.pause();
                    MainActivity.ivBottomPlay.setImageResource(R.drawable.pause_orange);
                    AudioPlayerActivity.isPlaying = false;
                    AudioPlayerActivity.isPause = true;
                } else {
                    BackgroundSoundService.mPlayer.start();
                    MainActivity.ivBottomPlay.setImageResource(R.drawable.play_orange);
                    AudioPlayerActivity.isPlaying = true;
                    AudioPlayerActivity.isPause = false;
                }


            }
        });
        ivUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AudioPlayerActivity.class);
                ((Activity) context).startActivity(i);
            }
        });
    }


    public static void updateSeekProgressTimer(final SeekBar seekBar, final Context context) {
        timer = new Timer("progress Updater");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        seekBar.setProgress(BackgroundSoundService.mPlayer.getCurrentPosition());
                    }
                });
            }
        }, 0, 1000);
    }


    public static void storeOfflineSongLisner(Context context, int position, ArrayList<HomeDetailsJson.DataList> itemsList) {

         /*getting offline stored array*/
        Gson gsonSong = new Gson();
        String jsonOfflineSong = PreferencesManager.getInstance(context).getOfflineSong();
        System.out.println("DUPLICATE INDEX" + jsonOfflineSong);
        Type type = new TypeToken<ArrayList<HomeDetailsJson.DataList>>() {
        }.getType();
        offlineSongArrayList = gsonSong.fromJson(jsonOfflineSong, type);
        int myId = itemsList.get(position).getColumns().getSongId();
        System.out.println("DUPLICATE INDEX" + myId);
        if (offlineSongArrayList.size() != 0) {
            duplicationIndex = isCheckDuplication(myId);
        } else {
            duplicationIndex = false;
        }
        System.out.println("DUPLICATE INDEX" + duplicationIndex);

        if (duplicationIndex) {
            offlineSongArrayList.remove(duplicateSongIndex);
        }
        if (offlineSongArrayList.size() >= 11) {
           /* if size equals to 10 then replace the zero position with latest song i.e. making
              size of recently played array of 10 max only
             */
            offlineSongArrayList.remove(10);
            HomeDetailsJson.DataList oldItem = offlineSongArrayList.get(0);
            oldItem.setColumns(itemsList.get(position).getColumns());
            offlineSongArrayList.add(oldItem);

            //saving the array again to shared preferance after updating
            Gson gsonStore = new Gson();
            String jsonOfflineSongStore = gsonStore.toJson(offlineSongArrayList);
            PreferencesManager.getInstance(context).saveOfflineSong(jsonOfflineSongStore);

        } else {
                         /*If size of recently played song is not reach to 10 yet then it will keep adding*/
            Gson gsonNew = new Gson();
            String item = gsonNew.toJson(itemsList.get(position));
            HomeDetailsJson.DataList newObj = gsonNew.fromJson(item, HomeDetailsJson.DataList.class);
            offlineSongArrayList.add(newObj);

            //saving the array again to shared preferance after updating
            Gson gsonStore = new Gson();
            String jsonOfflineSongStore = gsonStore.toJson(offlineSongArrayList);
            PreferencesManager.getInstance(context).saveOfflineSong(jsonOfflineSongStore);
        }


    }


    public static void storeOfflineStoreArtist(Context context, int position, ArrayList<HomeDetailsJson.DataList> itemsList, int categoryID) {
               /*getting offline stored array*/
        Gson gsonArtist = new Gson();
        String jsonOfflineArtist = PreferencesManager.getInstance(context).getRecentlyPlayedArtist();
        Type type = new TypeToken<ArrayList<OfflineArtistItem>>() {
        }.getType();

        System.out.println("DUPLICATE INDEXArtist" + jsonOfflineArtist);

        offlineArtistArrayList = gsonArtist.fromJson(jsonOfflineArtist, type);
        String mtArtist = itemsList.get(position).getColumns().getTypeName();
        System.out.println("DUPLICATE INDEXArtist" + mtArtist);
        duplicateArtistName = isCheckDuplicatArtist(mtArtist);

        System.out.println("DUPLICATE INDEXArtist" + duplicateArtistName);


        if (duplicateArtistName) {
            offlineArtistArrayList.remove(duplicateArtistIndex);

        }
        if (offlineArtistArrayList.size() >= 11) {
         /* if size equals to 10 then replace the zero position with latest song i.e. making
              size of recently played array of 10 max only
             */
            offlineArtistArrayList.remove(10);
            OfflineArtistItem oldItem = offlineArtistArrayList.get(0);
            oldItem.setArtistName(itemsList.get(position).getColumns().getTypeName());
            oldItem.setCatId(categoryID);
            oldItem.setThumbnail(itemsList.get(position).getColumns().getThumbnailImage());
            oldItem.setTypeId(itemsList.get(position).getColumns().getTypeId());
            oldItem.setTypeName(itemsList.get(position).getColumns().getTypeName());
            offlineArtistArrayList.add(oldItem);
            //saving the array again to shared preferance after updating
            Gson gsonStore = new Gson();
            String jsonOfflineArtistStore = gsonStore.toJson(offlineArtistArrayList);
            PreferencesManager.getInstance(context).saveRecentlyPlayedArtist(jsonOfflineArtistStore);

        } else {
                          /*If size of recently played song is not reach to 10 yet then it will keep adding*/
            Gson gsonNew = new Gson();
            OfflineArtistItem storeObj = new OfflineArtistItem();
            storeObj.setCatId(categoryID);
            storeObj.setThumbnail(itemsList.get(position).getColumns().getThumbnailImage());
            storeObj.setArtistName(itemsList.get(position).getColumns().getTypeName());
            storeObj.setTypeId(itemsList.get(position).getColumns().getTypeId());
            storeObj.setTypeName(itemsList.get(position).getColumns().getTypeName());
            // String item = gsonNew.toJson(itemsList.get(i));
            // OfflineArtistItem newObj = gsonNew.fromJson(item, OfflineArtistItem.class);
            offlineArtistArrayList.add(storeObj);
            //saving the array again to shared preferance after updating
            Gson gsonStore = new Gson();
            String jsonOfflineSongStore = gsonStore.toJson(offlineArtistArrayList);
            PreferencesManager.getInstance(context).saveRecentlyPlayedArtist(jsonOfflineSongStore);
        }


    }


    private static boolean isCheckDuplication(int myId) {

        for (int i = 0; i < offlineSongArrayList.size(); i++) {
            int id = offlineSongArrayList.get(i).getColumns().getSongId();
            System.out.println("DUPLICATE INDEX" + id);
            if (id == myId) {
                duplicateSongIndex = i;
                return true;
            }
        }
        return false;
    }

    private static boolean isCheckDuplicatArtist(String myArtist) {

        for (int i = 0; i < offlineArtistArrayList.size(); i++) {
            String artist = offlineArtistArrayList.get(i).getArtistName();
            if (artist.matches(myArtist)) {
                duplicateArtistIndex = i;
                return true;
            }
        }
        return false;
    }

}
