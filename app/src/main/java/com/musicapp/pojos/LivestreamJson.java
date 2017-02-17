package com.musicapp.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by SanaKazi on 2/15/2017.
 */
public class LivestreamJson {
     int Id,UserId;
     String DeviceId,Message;
    ArrayList<ConcertClass> Concert;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public ArrayList<ConcertClass> getConcert() {
        return Concert;
    }

    public void setConcert(ArrayList<ConcertClass> concert) {
        Concert = concert;
    }

    public static class ConcertClass implements Parcelable{
        int CityId;
        String CityName;
        ArrayList <DataListClass> dataList;

        protected ConcertClass(Parcel in) {
            CityId = in.readInt();
            CityName = in.readString();
            dataList = in.createTypedArrayList(DataListClass.CREATOR);
        }

        public static final Creator<ConcertClass> CREATOR = new Creator<ConcertClass>() {
            @Override
            public ConcertClass createFromParcel(Parcel in) {
                return new ConcertClass(in);
            }

            @Override
            public ConcertClass[] newArray(int size) {
                return new ConcertClass[size];
            }
        };

        public int getCityId() {
            return CityId;
        }

        public void setCityId(int cityId) {
            CityId = cityId;
        }

        public String getCityName() {
            return CityName;
        }

        public void setCityName(String cityName) {
            CityName = cityName;
        }

        public ArrayList<DataListClass> getDataList() {
            return dataList;
        }

        public void setDataList(ArrayList<DataListClass> dataList) {
            this.dataList = dataList;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(CityId);
            dest.writeString(CityName);
            dest.writeTypedList(dataList);
        }
    }

    public static class DataListClass implements Parcelable{
        int ConcertId;
        String ConcertDate,ConcertLink,ConcertTime,ConcertTitle,CoverImage,Description,DurationHrs,DurationMins,Location,ThumbnailImage;


        protected DataListClass(Parcel in) {
            ConcertId = in.readInt();
            ConcertDate = in.readString();
            ConcertLink = in.readString();
            ConcertTime = in.readString();
            ConcertTitle = in.readString();
            CoverImage = in.readString();
            Description = in.readString();
            DurationHrs = in.readString();
            DurationMins = in.readString();
            Location = in.readString();
            ThumbnailImage = in.readString();
        }

        public static final Creator<DataListClass> CREATOR = new Creator<DataListClass>() {
            @Override
            public DataListClass createFromParcel(Parcel in) {
                return new DataListClass(in);
            }

            @Override
            public DataListClass[] newArray(int size) {
                return new DataListClass[size];
            }
        };

        public int getConcertId() {
            return ConcertId;
        }

        public void setConcertId(int concertId) {
            ConcertId = concertId;
        }

        public String getConcertDate() {
            return ConcertDate;
        }

        public void setConcertDate(String concertDate) {
            ConcertDate = concertDate;
        }

        public String getConcertLink() {
            return ConcertLink;
        }

        public void setConcertLink(String concertLink) {
            ConcertLink = concertLink;
        }

        public String getConcertTime() {
            return ConcertTime;
        }

        public void setConcertTime(String concertTime) {
            ConcertTime = concertTime;
        }

        public String getConcertTitle() {
            return ConcertTitle;
        }

        public void setConcertTitle(String concertTitle) {
            ConcertTitle = concertTitle;
        }

        public String getCoverImage() {
            return CoverImage;
        }

        public void setCoverImage(String coverImage) {
            CoverImage = coverImage;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String description) {
            Description = description;
        }

        public String getDurationHrs() {
            return DurationHrs;
        }

        public void setDurationHrs(String durationHrs) {
            DurationHrs = durationHrs;
        }

        public String getDurationMins() {
            return DurationMins;
        }

        public void setDurationMins(String durationMins) {
            DurationMins = durationMins;
        }

        public String getLocation() {
            return Location;
        }

        public void setLocation(String location) {
            Location = location;
        }

        public String getThumbnailImage() {
            return ThumbnailImage;
        }

        public void setThumbnailImage(String thumbnailImage) {
            ThumbnailImage = thumbnailImage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ConcertId);
            dest.writeString(ConcertDate);
            dest.writeString(ConcertLink);
            dest.writeString(ConcertTime);
            dest.writeString(ConcertTitle);
            dest.writeString(CoverImage);
            dest.writeString(Description);
            dest.writeString(DurationHrs);
            dest.writeString(DurationMins);
            dest.writeString(Location);
            dest.writeString(ThumbnailImage);
        }
    }
}
