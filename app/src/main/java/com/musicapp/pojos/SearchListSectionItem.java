package com.musicapp.pojos;

/**
 * Created by PalseeTrivedi on 12/22/2016.
 */

public class SearchListSectionItem {
    public String SongTypeId;
    public String songUrl;
    public String ThumbnailImage;
    public String id;
    public String TypeName;
    public String CoverImage;
    public String categoryId;
    public String Description;
    public String Like;

    public void setSongTypeId(String songTypeId) {
        SongTypeId = songTypeId;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCoverImage(String coverImage) {
        CoverImage = coverImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        ThumbnailImage = thumbnailImage;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public void setLike(String like) {
        Like = like;
    }

    public String getSongTypeId() {
        return SongTypeId;
    }

    public String getThumbnailImage() {
        return ThumbnailImage;
    }

    public String getId() {
        return id;
    }

    public String getTypeName() {
        return TypeName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public String getCoverImage() {
        return CoverImage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getDescription() {
        return Description;
    }

    public String getLike() {
        return Like;
    }
}

