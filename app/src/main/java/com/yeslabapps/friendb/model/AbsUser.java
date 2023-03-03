package com.yeslabapps.friendb.model;

import java.util.Objects;

public abstract class AbsUser {

    private String username;
    private String email;
    private long lastSeen;
    private long registerDate;
    private int gold;
    private String deviceId;
    private int diamond;
    private int accountType;
    private String userId;
    private int referralStatus;





    public AbsUser(){

    }


    public AbsUser(String username, String email, long lastSeen, long registerDate, int gold, String deviceId, int diamond,
                   int accountType, String userId,int referralStatus) {

        this.username = username;
        this.email = email;
        this.lastSeen = lastSeen;
        this.registerDate = registerDate;
        this.gold = gold;
        this.deviceId = deviceId;
        this.diamond = diamond;
        this.accountType = accountType;
        this.userId = userId;
        this.referralStatus = referralStatus;
    }

    public int getReferralStatus() {
        return referralStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(long registerDate) {
        this.registerDate = registerDate;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbsUser absUser = (AbsUser) o;
        return lastSeen == absUser.lastSeen && registerDate == absUser.registerDate && gold == absUser.gold && Double.compare(absUser.diamond, diamond) == 0 && accountType == absUser.accountType && Objects.equals(username, absUser.username) && Objects.equals(email, absUser.email)
                &&  Objects.equals(deviceId, absUser.deviceId) && Objects.equals(userId, absUser.userId);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = username != null ? username.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (int) (lastSeen ^ (lastSeen >>> 32));
        result = 31 * result + (int) (registerDate ^ (registerDate >>> 32));
        result = 31 * result + gold;
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        temp = Double.doubleToLongBits(diamond);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + accountType;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AbsUser{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", lastSeen=" + lastSeen +
                ", registerDate=" + registerDate +
                ", point=" + gold +
                ", deviceId='" + deviceId + '\'' +
                ", dollar=" + diamond +
                ", accountType=" + accountType +
                ", userId='" + userId + '\'' +
                '}';
    }
}
