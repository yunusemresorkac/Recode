package com.yeslabapps.friendb.model;

public class User extends AbsUser {

    public User(){

    }

    public User(String username, String email, long lastSeen, long registerDate,  int gold, String deviceId, int diamond,
                int accountType,  String userId,int referralStatus) {

        super(username, email, lastSeen, registerDate, gold, deviceId, diamond, accountType, userId,referralStatus);
    }




}
