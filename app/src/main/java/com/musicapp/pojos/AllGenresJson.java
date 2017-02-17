package com.musicapp.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 1/12/2017.
 */
public class AllGenresJson {
    int id;
    String message;
    ArrayList<GenreList> genreList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<GenreList> getGenreList() {
        return genreList;
    }

    public void setGenreList(ArrayList<GenreList> genreList) {
        this.genreList = genreList;
    }

    public static class GenreList implements Parcelable{
        int genreId;
        String coverImage;
        String genreName;
        String thumbnailImage;

        protected GenreList(Parcel in) {
            genreId = in.readInt();
            coverImage = in.readString();
            genreName = in.readString();
            thumbnailImage = in.readString();
            isFav = in.readString();
        }

        public static final Creator<GenreList> CREATOR = new Creator<GenreList>() {
            @Override
            public GenreList createFromParcel(Parcel in) {
                return new GenreList(in);
            }

            @Override
            public GenreList[] newArray(int size) {
                return new GenreList[size];
            }
        };

        public String getIsFav() {
            return isFav;
        }

        public void setIsFav(String isFav) {
            this.isFav = isFav;
        }

        String isFav;

        public int getGenreId() {
            return genreId;
        }

        public void setGenreId(int genreId) {
            this.genreId = genreId;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }

        public String getGenreName() {
            return genreName;
        }

        public void setGenreName(String genreName) {
            this.genreName = genreName;
        }

        public String getThumbnailImage() {
            return thumbnailImage;
        }

        public void setThumbnailImage(String thumbnailImage) {
            this.thumbnailImage = thumbnailImage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(genreId);
            dest.writeString(coverImage);
            dest.writeString(genreName);
            dest.writeString(thumbnailImage);
            dest.writeString(isFav);
        }
    }

}

