package com.u.bops.biz.domain;


import java.util.List;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
public class FriendShip {

    private String openId;
    private String friendOpenId;
    private boolean deleted;
    private String friendNickName;
    private String friendImage;
    private List<ChatMessage> unreadChatMessages;

    public List<ChatMessage> getUnreadChatMessages() {
        return unreadChatMessages;
    }

    public void setUnreadChatMessages(List<ChatMessage> unreadChatMessages) {
        this.unreadChatMessages = unreadChatMessages;
    }

    /**
     * 最近一次清空聊天记录的messageId
     */
    private long lasteleteMessageId = -1;

    public long getLasteleteMessageId() {
        return lasteleteMessageId;
    }

    public void setLasteleteMessageId(long lasteleteMessageId) {
        this.lasteleteMessageId = lasteleteMessageId;
    }

    public String getFriendOpenId() {
        return friendOpenId;
    }

    public void setFriendOpenId(String friendOpenId) {
        this.friendOpenId = friendOpenId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
