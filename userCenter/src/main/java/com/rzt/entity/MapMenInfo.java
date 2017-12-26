package com.rzt.entity;

import javax.persistence.Column;

public class MapMenInfo {
    private String userid;
    private String userName;
    private Integer gzlx;
    private float longitude;
    private float latitude;
    private boolean onLine;
    public MapMenInfo(String userid, String userName, Integer gzlx, float longitude, float latitude,boolean onLine) {
        this.userid = userid;
        this.userName = userName;
        this.gzlx = gzlx;
        this.longitude = longitude;
        this.latitude = latitude;
        this.onLine = onLine;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getGzlx() {
        return gzlx;
    }

    public void setGzlx(Integer gzlx) {
        this.gzlx = gzlx;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public boolean isOnLine() {
        return onLine;
    }

    public void setOnLine(boolean onLine) {
        this.onLine = onLine;
    }
}
