package com.yeslabapps.friendb.model;

public class Referral {

    private String inviterId;
    private String receiverId;

    public Referral(){

    }

    public Referral(String inviterId, String receiverId) {
        this.inviterId = inviterId;
        this.receiverId = receiverId;
    }

    public String getInviterId() {
        return inviterId;
    }

    public void setInviterId(String inviterId) {
        this.inviterId = inviterId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
