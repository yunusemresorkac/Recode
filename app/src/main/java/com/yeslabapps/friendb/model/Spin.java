package com.yeslabapps.friendb.model;

public class Spin {

    private String userId;
    private long normalSpinTime;
    private long luckySpinTime;

    public Spin(){

    }

    public Spin(String userId, long normalSpinTime, long luckySpinTime) {
        this.userId = userId;
        this.normalSpinTime = normalSpinTime;
        this.luckySpinTime = luckySpinTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getNormalSpinTime() {
        return normalSpinTime;
    }

    public void setNormalSpinTime(long normalSpinTime) {
        this.normalSpinTime = normalSpinTime;
    }

    public long getLuckySpinTime() {
        return luckySpinTime;
    }

    public void setLuckySpinTime(long luckySpinTime) {
        this.luckySpinTime = luckySpinTime;
    }
}
