package com.example.fyp_proj;

import android.os.Parcel;
import android.os.Parcelable;

public class CartData implements Parcelable {
    String merchname, name, food, price, request, qt, status, method, statusCust, statusMerc, Id, mkey;

    public CartData(String merchname, String name, String food, String price, String request, String qt, String status,
                    String method, String statusCust, String statusMerc, String Id) {
        this.merchname = merchname;
        this.name = name;
        this.food = food;
        this.price = price;
        this.request = request;
        this.qt = qt;
        this.status = status;
        this.method = method;
        this.statusCust = statusCust;
        this.statusMerc = statusMerc;
        this.Id = Id;
    }

    public CartData() {
    }

    protected CartData(Parcel in) {
        merchname = in.readString();
        name = in.readString();
        food = in.readString();
        price = in.readString();
        request = in.readString();
        qt = in.readString();
        status = in.readString();
        method = in.readString();
        statusCust = in.readString();
        statusMerc = in.readString();
        Id = in.readString();
        mkey = in.readString();
    }

    public static final Creator<CartData> CREATOR = new Creator<CartData>() {
        @Override
        public CartData createFromParcel(Parcel in) {
            return new CartData(in);
        }

        @Override
        public CartData[] newArray(int size) {
            return new CartData[size];
        }
    };

    public String getMerchname() {
        return merchname;
    }

    public void setMerchname(String merchname) {
        this.merchname = merchname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getQt() {
        return qt;
    }

    public void setQt(String qt) {
        this.qt = qt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatusCust() {
        return statusCust;
    }

    public void setStatusCust(String statusCust) {
        this.statusCust = statusCust;
    }

    public String getStatusMerc() {
        return statusMerc;
    }

    public void setStatusMerc(String statusMerc) {
        this.statusMerc = statusMerc;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMkey() {
        return mkey;
    }

    public void setMkey(String mkey) {
        this.mkey = mkey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(merchname);
        parcel.writeString(name);
        parcel.writeString(food);
        parcel.writeString(price);
        parcel.writeString(request);
        parcel.writeString(qt);
        parcel.writeString(status);
        parcel.writeString(method);
        parcel.writeString(statusCust);
        parcel.writeString(statusMerc);
        parcel.writeString(Id);
        parcel.writeString(mkey);
    }
}
