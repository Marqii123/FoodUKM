package com.example.fyp_proj.Merchant.ui.AddMenu;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MenuData implements Parcelable {
    private String merchname, menuname, price, desc, img, mKey;

    public MenuData(String merchname, String menuname, String price, String desc, String img) {
        this.merchname = merchname;
        this.menuname = menuname;
        this.price = price;
        this.desc = desc;
        this.img = img;
    }

    public MenuData() {
    }

    protected MenuData(Parcel in) {
        merchname = in.readString();
        menuname = in.readString();
        price = in.readString();
        desc = in.readString();
        img = in.readString();
        mKey = in.readString();
    }

    public static final Creator<MenuData> CREATOR = new Creator<MenuData>() {
        @Override
        public MenuData createFromParcel(Parcel in) {
            return new MenuData(in);
        }

        @Override
        public MenuData[] newArray(int size) {
            return new MenuData[size];
        }
    };

    public String getMerchname() {
        return merchname;
    }

    public void setMerchname(String merchname) {
        this.merchname = merchname;
    }

    public String getMenuname() {
        return menuname;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(merchname);
        parcel.writeString(menuname);
        parcel.writeString(price);
        parcel.writeString(desc);
        parcel.writeString(img);
        parcel.writeString(mKey);
    }
}
