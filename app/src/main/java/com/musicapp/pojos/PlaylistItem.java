package com.musicapp.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PalseeTrivedi on 1/12/2017.
 */
public class PlaylistItem {
    public String playlistName;
    public int playlistId;
    public String songCount;
    public  String imageUrl;
    public  boolean isChecked;


    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public void setSongCount(String songCount) {
        this.songCount = songCount;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getPlaylistName() {
        return playlistName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public String getSongCount() {
        return songCount;
    }

}
