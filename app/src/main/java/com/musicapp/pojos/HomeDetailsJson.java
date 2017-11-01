package com.musicapp.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 12/30/2016.
 */
public class HomeDetailsJson {


    //region class HomeCategoryJson

    int id,userId;
    String message;
    ArrayList <Categories> categories;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Categories> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Categories> categories) {
        this.categories = categories;
    }

    //endregion


    public class Categories
    {
        //region class Categories
        int CategoryId;

        public int getCategoryId() {
            return CategoryId;
        }

        public String getCategoryName() {
            return CategoryName;
        }

        public ArrayList<DataList> getDataList() {
            return dataList;
        }

        String CategoryName;
        ArrayList<DataList> dataList;

        public void setCategoryId(int categoryId) {
            CategoryId = categoryId;
        }

        public void setCategoryName(String categoryName) {
            CategoryName = categoryName;
        }

        public void setDataList(ArrayList<DataList> dataList) {
            this.dataList = dataList;
        }

        //endregion
    }

    public  static class DataList implements Parcelable{
        //region class DataList
        Columns columns;

        public DataList() {

        }
        protected DataList(Parcel in) {
            columns = in.readParcelable(Columns.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(columns, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DataList> CREATOR = new Creator<DataList>() {
            @Override
            public DataList createFromParcel(Parcel in) {
                return new DataList(in);
            }

            @Override
            public DataList[] newArray(int size) {
                return new DataList[size];
            }
        };

        public Columns getColumns() {
            return columns;
        }

        public void setColumns(Columns columns) {
            this.columns = columns;
        }

        //endregion
    }

    public static class Columns implements Parcelable{

        int MoodId,SongCount,SongId,SongTypeId,TypeId;
        String Like,CoverImage,Description,Origin,Producer,ReleaseDate,SongName,SongURL,ThumbnailImage,TypeName,SecondaryArtistId,ArtistId,GenreId;

        public Columns() {

        }
        public static final Creator<Columns> CREATOR = new Creator<Columns>() {
            @Override
            public Columns createFromParcel(Parcel in) {
                return new Columns(in);
            }

            @Override
            public Columns[] newArray(int size) {
                return new Columns[size];
            }
        };

        public String getArtistId() {
            return ArtistId;
        }

        public void setArtistId(String artistId) {
            ArtistId = artistId;
        }

        public String getGenreId() {
            return GenreId;
        }



        public void setGenreId(String genreId) {
            GenreId = genreId;
        }
        public String getLike() {
            return Like;
        }
        public void setLike(String like) {
            Like = like;
        }

        public int getMoodId() {
            return MoodId;
        }

        public void setMoodId(int moodId) {
            MoodId = moodId;
        }

        public int getSongCount() {
            return SongCount;
        }

        public void setSongCount(int songCount) {
            SongCount = songCount;
        }

        public int getSongId() {
            return SongId;
        }

        public void setSongId(int songId) {
            SongId = songId;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getOrigin() {
            return Origin;
        }

        public void setOrigin(String origin) {
            Origin = origin;
        }

        public String getProducer() {
            return Producer;
        }

        public void setProducer(String producer) {
            Producer = producer;
        }

        public String getReleaseDate() {
            return ReleaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            ReleaseDate = releaseDate;
        }

        public String getSongName() {
            return SongName;
        }

        public void setSongName(String songName) {
            SongName = songName;
        }

        public String getSongURL() {
            return SongURL;
        }

        public void setSongURL(String songURL) {
            SongURL = songURL;
        }

        public String getSecondaryArtistId() {
            return SecondaryArtistId;
        }

        public void setSecondaryArtistId(String secondaryArtistId) {
            SecondaryArtistId = secondaryArtistId;
        }

        public String getCoverImage() {
            return CoverImage;
        }

        public void setCoverImage(String coverImage) {
            CoverImage = coverImage;
        }

        public int getSongTypeId() {
            return SongTypeId;
        }

        public void setSongTypeId(int songTypeId) {
            SongTypeId = songTypeId;
        }

        public int getTypeId() {
            return TypeId;
        }

        public void setTypeId(int typeId) {
            TypeId = typeId;
        }

        public String getThumbnailImage() {
            return ThumbnailImage;
        }

        public void setThumbnailImage(String thumbnailImage) {
            ThumbnailImage = thumbnailImage;
        }

        public String getTypeName() {
            return TypeName;
        }

        public void setTypeName(String typeName) {
            TypeName = typeName;
        }

        @Override
        public int describeContents() {
            return 0;
        }


        protected Columns(Parcel in) {

            MoodId = in.readInt();
            SongCount = in.readInt();
            SongId = in.readInt();
            SongTypeId = in.readInt();
            TypeId = in.readInt();
            Like = in.readString();
            CoverImage = in.readString();
            Description = in.readString();
            Origin = in.readString();
            Producer = in.readString();
            ReleaseDate = in.readString();
            SongName = in.readString();
            SongURL = in.readString();
            ThumbnailImage = in.readString();
            TypeName = in.readString();
            SecondaryArtistId = in.readString();
            ArtistId = in.readString();
            GenreId = in.readString();



           /* ArtistId = in.readString();
            GenreId = in.readString();
            MoodId = in.readInt();
            SongCount = in.readInt();
            SongId = in.readInt();
            SongTypeId = in.readInt();
            TypeId = in.readInt();
            CoverImage = in.readString();
            Like=in.readString();
            Description = in.readString();
            Origin = in.readString();
            Producer = in.readString();
            ReleaseDate = in.readString();
            SongName = in.readString();
            SongURL = in.readString();
            ThumbnailImage = in.readString();
            TypeName = in.readString();
            SecondaryArtistId = in.readString();*/
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {


            dest.writeInt(MoodId);
            dest.writeInt(SongCount);
            dest.writeInt(SongId);
            dest.writeInt(SongTypeId);
            dest.writeInt(TypeId);
            dest.writeString(Like);
            dest.writeString(CoverImage);
            dest.writeString(Description);
            dest.writeString(Origin);
            dest.writeString(Producer);
            dest.writeString(ReleaseDate);
            dest.writeString(SongName);
            dest.writeString(SongURL);
            dest.writeString(ThumbnailImage);
            dest.writeString(TypeName);
            dest.writeString(SecondaryArtistId);
            dest.writeString(ArtistId);
            dest.writeString(GenreId);

          /*  dest.writeString(ArtistId);
            dest.writeString(GenreId);
            dest.writeInt(MoodId);
            dest.writeInt(SongCount);
            dest.writeInt(SongId);
            dest.writeInt(SongTypeId);
            dest.writeInt(TypeId);
            dest.writeString(CoverImage);
            dest.writeString(Description);
            dest.writeString(Like);
            dest.writeString(Origin);
            dest.writeString(Producer);
            dest.writeString(ReleaseDate);
            dest.writeString(SongName);
            dest.writeString(SongURL);
            dest.writeString(ThumbnailImage);
            dest.writeString(TypeName);
            dest.writeString(SecondaryArtistId);*/
        }
    }

}
