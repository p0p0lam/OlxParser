package com.popolam.olxparser.model.lun;

/**
 * Project: olxparser
 * Created by p0p0lam on 24.11.2014.
 */
public class Ad {
    private long provId;
    private String address;
    private String link;
    private String details;
    private String price;
    private String params;
    private String area;
    private String roomNo;

    public long getProvId() {
        return provId;
    }

    public void setProvId(long provId) {
        this.provId = provId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    @Override
    public String toString() {
        return "Ad{" +
                "provId=" + provId +
                ", price='" + price + '\'' +
                ", address='" + address + '\'' +
                ", link='" + link + '\'' +
                ", details='" + details + '\'' +
                ", params='" + params + '\'' +
                ", area='" + area + '\'' +
                ", roomNo='" + roomNo + '\'' +
                '}';
    }
}
