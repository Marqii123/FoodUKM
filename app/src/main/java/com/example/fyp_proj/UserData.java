package com.example.fyp_proj;

import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable {
    String name,email, pic,uid;

    public UserData(String name, String email, String pic,String uid) {
        this.name = name;
        this.email = email;
        this.pic = pic;
        this.uid = uid;
    }

    public UserData() {
    }

    protected UserData(Parcel in) {
        name = in.readString();
        email = in.readString();
        pic = in.readString();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(pic);
        dest.writeString(uid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
