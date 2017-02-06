package com.u.bops.biz.domain;


/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
public class FriendShip {

    private String openId;
    private String friendOpenId;
    private boolean delete;
    private String friendNickName;
    private String friendImage;

    public String getFriendOpenId() {
        return friendOpenId;
    }

    public void setFriendOpenId(String friendOpenId) {
        this.friendOpenId = friendOpenId;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public String getFriendImage() {
        return friendImage;
    }

    public void setFriendImage(String friendImage) {
        this.friendImage = friendImage;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

}
