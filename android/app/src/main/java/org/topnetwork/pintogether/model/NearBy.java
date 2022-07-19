package org.topnetwork.pintogether.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NearBy implements Parcelable {
    private String account;
    private String address;
    private String cid;
    private String createdDate;
    private String description;
    private String distance;
    private String giftId;
    private double lat;
    private double lng;
    private String name;
    private String num;
    private String ranges = "100";
    private boolean sign;
    private String total = "0";

    private String hash;
    private String receiveId;
    private String tokenId;
    private String createAccount;

    private double aglat;
    private double aglng;
    private boolean numLimit;


    public NearBy(){}

    protected NearBy(Parcel in) {
        account = in.readString();
        address = in.readString();
        cid = in.readString();
        createdDate = in.readString();
        description = in.readString();
        distance = in.readString();
        giftId = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        name = in.readString();
        num = in.readString();
        ranges = in.readString();
        sign = in.readByte() != 0;
        total = in.readString();
        hash = in.readString();
        receiveId = in.readString();
        tokenId = in.readString();
        createAccount = in.readString();
        aglat = in.readDouble();
        aglng = in.readDouble();
        numLimit = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account);
        dest.writeString(address);
        dest.writeString(cid);
        dest.writeString(createdDate);
        dest.writeString(description);
        dest.writeString(distance);
        dest.writeString(giftId);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(name);
        dest.writeString(num);
        dest.writeString(ranges);
        dest.writeByte((byte) (sign ? 1 : 0));
        dest.writeString(total);
        dest.writeString(hash);
        dest.writeString(receiveId);
        dest.writeString(tokenId);
        dest.writeString(createAccount);
        dest.writeDouble(aglat);
        dest.writeDouble(aglng);
        dest.writeByte((byte) (numLimit ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NearBy> CREATOR = new Creator<NearBy>() {
        @Override
        public NearBy createFromParcel(Parcel in) {
            return new NearBy(in);
        }

        @Override
        public NearBy[] newArray(int size) {
            return new NearBy[size];
        }
    };

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getRanges() {
        return ranges;
    }

    public void setRanges(String ranges) {
        this.ranges = ranges;
    }

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getCreateAccount() {
        return createAccount;
    }

    public void setCreateAccount(String createAccount) {
        this.createAccount = createAccount;
    }

    public double getAglat() {
        return aglat;
    }

    public void setAglat(double aglat) {
        this.aglat = aglat;
    }

    public double getAglng() {
        return aglng;
    }

    public void setAglng(double aglng) {
        this.aglng = aglng;
    }

    public static Creator<NearBy> getCREATOR() {
        return CREATOR;
    }

    public boolean isNumLimit() {
        return numLimit;
    }

    public void setNumLimit(boolean numLimit) {
        this.numLimit = numLimit;
    }
}
