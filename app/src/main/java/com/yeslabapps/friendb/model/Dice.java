package com.yeslabapps.friendb.model;

public class Dice {

    private String userId;
    private long diceTime;

    public Dice(){

    }

    public Dice(String userId, long diceTime) {
        this.userId = userId;
        this.diceTime = diceTime;
    }


    public String getUserId() {
        return userId;
    }

    public long getDiceTime() {
        return diceTime;
    }
}
