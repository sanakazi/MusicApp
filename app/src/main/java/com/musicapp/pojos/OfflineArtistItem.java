package com.musicapp.pojos;

/**
 * Created by PalseeTrivedi on 1/20/2017.
 */
public class OfflineArtistItem {

    public String artistName;
    public String thumbnail;
    public int typeId;
    public int catId;
    public String typeName;

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getTypeId() {
        return typeId;
    }

    public int getCatId() {
        return catId;
    }

    public String getTypeName() {
        return typeName;
    }
}
