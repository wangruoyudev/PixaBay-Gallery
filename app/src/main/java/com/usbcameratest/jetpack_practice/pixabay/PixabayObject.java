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

    public PixabayUrl(int id, String webformatURL, String largeImageURL) {
        this.id = id;
        this.webformatURL = webformatURL;
        this.largeImageURL = largeImageURL;
    }

    protected PixabayUrl(Parcel in) {
        id = in.readInt();
        webformatURL = in.readString();
        largeImageURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(webformatURL);
        dest.writeString(largeImageURL);
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
