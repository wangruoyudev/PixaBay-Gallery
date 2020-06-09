package com.usbcameratest.jetpack_practice.pixabay;

import android.os.Parcel;
import android.os.Parcelable;

public class PixabayObject{
    int total;
    int totalHits;
    PixabayUrl[] hits;

    public PixabayObject(int total, int totalHits, PixabayUrl[] hits) {
        this.total = total;
        this.totalHits = totalHits;
        this.hits = hits;
    }
}

class PixabayUrl implements Parcelable {
    int id;
    String webformatURL;
    String largeImageURL;
    int webformatWidth;
    int webformatHeight;
    String user;
    int favorites;
    int likes;


    public PixabayUrl(int id, String webformatURL, String largeImageURL, int webformatWidth, int webformatHeight) {
        this.id = id;
        this.webformatURL = webformatURL;
        this.largeImageURL = largeImageURL;
        this.webformatWidth = webformatWidth;
        this.webformatHeight = webformatHeight;
    }


    protected PixabayUrl(Parcel in) {
        id = in.readInt();
        webformatURL = in.readString();
        largeImageURL = in.readString();
        webformatWidth = in.readInt();
        webformatHeight = in.readInt();
        user = in.readString();
        favorites = in.readInt();
        likes = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(webformatURL);
        dest.writeString(largeImageURL);
        dest.writeInt(webformatWidth);
        dest.writeInt(webformatHeight);
        dest.writeString(user);
        dest.writeInt(favorites);
        dest.writeInt(likes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PixabayUrl> CREATOR = new Creator<PixabayUrl>() {
        @Override
        public PixabayUrl createFromParcel(Parcel in) {
            return new PixabayUrl(in);
        }

        @Override
        public PixabayUrl[] newArray(int size) {
            return new PixabayUrl[size];
        }
    };
}
