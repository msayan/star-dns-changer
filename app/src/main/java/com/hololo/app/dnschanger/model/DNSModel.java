package com.hololo.app.dnschanger.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class DNSModel implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("firstDNS")
    private String firstDns;
    @SerializedName("secondDNS")
    private String secondDns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstDns() {
        return firstDns;
    }

    public void setFirstDns(String firstDns) {
        this.firstDns = firstDns;
    }

    public String getSecondDns() {
        return secondDns;
    }

    public void setSecondDns(String secondDns) {
        this.secondDns = secondDns;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.firstDns);
        dest.writeString(this.secondDns);
    }

    public DNSModel() {
    }

    protected DNSModel(Parcel in) {
        this.name = in.readString();
        this.firstDns = in.readString();
        this.secondDns = in.readString();
    }

    public static final Parcelable.Creator<DNSModel> CREATOR = new Parcelable.Creator<DNSModel>() {
        @Override
        public DNSModel createFromParcel(Parcel source) {
            return new DNSModel(source);
        }

        @Override
        public DNSModel[] newArray(int size) {
            return new DNSModel[size];
        }
    };
}
