package com.krypto.movietalk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores and returns individual information for a particular movie
 */
public class Movie implements Parcelable {

    private String mTitle, mOverview, mVote, mUrl, mRelease, mId;

    public Movie(String title, String overvie, String vote, String url, String release, String id) {

        mTitle = title;
        mOverview = overvie;
        mVote = vote;
        mUrl = url;
        mRelease = release;
        mId = id;
    }

    public Movie(Parcel in) {
        mTitle = in.readString();
        mOverview = in.readString();
        mVote = in.readString();
        mUrl = in.readString();
        mRelease = in.readString();
        mId = in.readString();
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }


    public String getOverview() {
        return mOverview;
    }


    public String getVote() {
        return mVote;
    }


    public String getUrl() {
        return mUrl;
    }


    public String getRelease() {
        return mRelease;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeString(mVote);
        dest.writeString(mUrl);
        dest.writeString(mRelease);
        dest.writeString(mId);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };
}
