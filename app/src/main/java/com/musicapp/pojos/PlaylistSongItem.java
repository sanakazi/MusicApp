package com.musicapp.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PalseeTrivedi on 1/12/2017.
 */
public class PlaylistSongItem implements Parcelable{

    public String ArtistId;
    public String CoverImage;
    public String Description;
    public String GenreId;
    public String MoodId;
    public String Origin;
    public String Producer;
    public String ReleaseDate;
    public String SecondaryArtistId;
    public int SongId;
    public String SongName;
    public int SongTypeId;
    public String SongURL;
    public String ThumbnailImage;

    protected PlaylistSongItem(Parcel in) {
        ArtistId = in.readString();
        CoverImage = in.readString();
        Description = in.readString();
        GenreId = in.readString();
        MoodId = in.readString();
        Origin = in.readString();
        Producer = in.readString();
        ReleaseDate = in.readString();
        SecondaryArtistId = in.readString();
        SongId = in.readInt();
        SongName = in.readString();
        SongTypeId = in.readInt();
        SongURL = in.readString();
        ThumbnailImage = in.readString();
    }
    public PlaylistSongItem(){}
    public static final Creator<PlaylistSongItem> CREATOR = new Creator<PlaylistSongItem>() {
        @Override
        public PlaylistSongItem createFromParcel(Parcel in) {
            return new PlaylistSongItem(in);
        }

        @Override
        public PlaylistSongItem[] newArray(int size) {
            return new PlaylistSongItem[size];
        }
    };

    public void setArtistId(String artistId) {
        ArtistId = artistId;
    }

    public void setCoverImage(String coverImage) {
        CoverImage = coverImage;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setGenreId(String genreId) {
        GenreId = genreId;
    }

    public void setMoodId(String moodId) {
        MoodId = moodId;
    }

    public void setOrigin(String origin) {
        Origin = origin;
    }

    public void setProducer(String producer) {
        Producer = producer;
    }

    public void setReleaseDate(String releaseDate) {
        ReleaseDate = releaseDate;
    }

    public void setSecondaryArtistId(String secondaryArtistId) {
        SecondaryArtistId = secondaryArtistId;
    }

    public void setSongId(int songId) {
        SongId = songId;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public void setSongTypeId(int songTypeId) {
        SongTypeId = songTypeId;
    }

    public void setSongURL(String songURL) {
        SongURL = songURL;
    }

    public void setThumbnailImage(String thumbnailImage) {
        ThumbnailImage = thumbnailImage;
    }

    public String getArtistId() {
        return ArtistId;
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public String getDescription() {
        return Description;
    }

    public String getGenreId() {
        return GenreId;
    }

    public String getMoodId() {
        return MoodId;
    }

    public String getOrigin() {
        return Origin;
    }

    public String getProducer() {
        return Producer;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public String getSecondaryArtistId() {
        return SecondaryArtistId;
    }

    public int getSongId() {
        return SongId;
    }

    public String getSongName() {
        return SongName;
    }

    public int getSongTypeId() {
        return SongTypeId;
    }

    public String getSongURL() {
        return SongURL;
    }

    public String getThumbnailImage() {
        return ThumbnailImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ArtistId);
        dest.writeString(CoverImage);
        dest.writeString(Description);
        dest.writeString(GenreId);
        dest.writeString(MoodId);
        dest.writeString(Origin);
        dest.writeString(Producer);
        dest.writeString(ReleaseDate);
        dest.writeString(SecondaryArtistId);
        dest.writeInt(SongId);
        dest.writeString(SongName);
        dest.writeInt(SongTypeId);
        dest.writeString(SongURL);
        dest.writeString(ThumbnailImage);
    }
}
