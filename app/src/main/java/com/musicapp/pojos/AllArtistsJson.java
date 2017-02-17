package com.musicapp.pojos;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 1/13/2017.
 */
public class AllArtistsJson {

    ArrayList<ArtistsList> artistList;
    int id;
    String message;

    public ArrayList<ArtistsList> getArtistList() {
        return artistList;
    }

    public void setArtistList(ArrayList<ArtistsList> artistList) {
        this.artistList = artistList;
    }

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

    public class ArtistsList{
        int artistId;
        String coverImage,artistName,thumbnailImage,isFav;

        public String getIsFav() {
            return isFav;
        }

        public void setIsFav(String isFav) {
            this.isFav = isFav;
        }

        public int getArtistId() {
            return artistId;
        }

        public void setArtistId(int artistId) {
            this.artistId = artistId;
        }

        public String getCoverImage() {
            return coverImage;
        }

        public void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }

        public String getArtistName() {
            return artistName;
        }

        public void setArtistName(String artistName) {
            this.artistName = artistName;
        }

        public String getThumbnailImage() {
            return thumbnailImage;
        }

        public void setThumbnailImage(String thumbnailImage) {
            this.thumbnailImage = thumbnailImage;
        }
    }
}
