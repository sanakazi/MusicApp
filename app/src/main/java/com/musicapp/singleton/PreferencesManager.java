package com.musicapp.singleton;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SanaKazi on 1/4/2017.
 */
public class PreferencesManager {
    public static final String PREF_NAME = "myPrefs";
    public static final String PREFKEY_USER_ID = "user_id";
    public static final String PREFKEY_DEVICE_ID = "device_id";
    public static final String PREFKEY_IS_SOCIAL_LOGIN = "IsSocialLogin";
    private static final String FIRST_RUN = "FIRST_RUN";
    private static final String PLAY_LIST_SIZE = "playlist_size";
    private static final String IS_OFFLINE_SONG_ARRAY_CREATED = "is_song_array_created";
    private static final String RECENT_PLAYED_SONG_ARRAY = "recently_played_songs";
    private static final String RECENT_PLAYED_ARTIST_ARRAY = "recently_played_artsit";
    private static final String IS_OFFLINE_ARTIST_ARRAY_CREATED = "is_artist_array_created";
    private SharedPreferences sPref;
    private static PreferencesManager preferencesManager;

    private PreferencesManager(Context context) {
        sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PreferencesManager getInstance(Context context) {
        if (preferencesManager == null) {
            preferencesManager = new PreferencesManager(context);
        }
        return preferencesManager;
    }


    public int getUserId() {
        return (sPref.getInt(PREFKEY_USER_ID, 0));
    }

    public void saveUserId(int id) {
        sPref.edit().putInt(PREFKEY_USER_ID, id).commit();
    }


    public boolean getIsOfflineSongCreated() {
        return (sPref.getBoolean(IS_OFFLINE_SONG_ARRAY_CREATED, false));
    }

    public void saveIsOfflineSongCreated(boolean isCreated) {
        sPref.edit().putBoolean(IS_OFFLINE_SONG_ARRAY_CREATED, isCreated).commit();
    }

    public String getOfflineSong() {
        return (sPref.getString(RECENT_PLAYED_SONG_ARRAY, ""));
    }

    public void saveOfflineSong(String songs) {
        sPref.edit().putString(RECENT_PLAYED_SONG_ARRAY, songs).commit();
    }

    public String getRecentlyPlayedArtist() {
        return (sPref.getString(RECENT_PLAYED_ARTIST_ARRAY, ""));
    }

    public void saveRecentlyPlayedArtist(String artist) {
        sPref.edit().putString(RECENT_PLAYED_ARTIST_ARRAY, artist).commit();
    }

    public boolean getIsOfflineArtistCreated() {
        return (sPref.getBoolean(IS_OFFLINE_ARTIST_ARRAY_CREATED, false));
    }

    public void saveIsOfflineArtistCreated(boolean isArtsitCreated){
        sPref.edit().putBoolean(IS_OFFLINE_ARTIST_ARRAY_CREATED,isArtsitCreated).commit();

    }


    public int getPlayListSize() {
        return (sPref.getInt(PLAY_LIST_SIZE, 0));
    }

    public void savePlaylistSize(int size) {
        sPref.edit().putInt(PLAY_LIST_SIZE, size).commit();
    }


    public String getDeviceId() {
        return (sPref.getString(PREFKEY_DEVICE_ID, ""));
    }

    public void saveDeviceId(String id) {
        sPref.edit().putString(PREFKEY_DEVICE_ID, id).commit();
    }

    public boolean isLoggedIn() {
        return (sPref.getInt(PREFKEY_USER_ID, 0) != 0);
    }

    public boolean isFirstRun() {
        return (sPref.getBoolean(FIRST_RUN, true));
    }

    public void saveIsFirstRun(boolean firstrun) {
        sPref.edit().putBoolean(FIRST_RUN, firstrun).commit();
    }

    public void saveIsSocial(int isSocial) {
        sPref.edit().putInt(PREFKEY_IS_SOCIAL_LOGIN, isSocial).commit();
    }


    public int isSocialLogin() {
        return (sPref.getInt(PREFKEY_IS_SOCIAL_LOGIN, 0));
    }


    public void clearUserPreferences() {

        SharedPreferences.Editor edit = sPref.edit();
        edit.putInt(PREFKEY_USER_ID, 0);
        edit.putInt(PREFKEY_IS_SOCIAL_LOGIN, 0);
        edit.putBoolean(IS_OFFLINE_ARTIST_ARRAY_CREATED,false);
        edit.putBoolean(IS_OFFLINE_SONG_ARRAY_CREATED,false);
        edit.putString(RECENT_PLAYED_ARTIST_ARRAY,"");
        edit.putString(RECENT_PLAYED_SONG_ARRAY,"");
        edit.commit();
    }

}
