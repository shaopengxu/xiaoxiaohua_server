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
    private int unreadMessageSize;
    private String lastMessage;
    private long lastChatTime;

    public int getUnreadMessageSize() {
        return unreadMessageSize;
    }

    public void setUnreadMessageSize(int unreadMessageSize) {
        this.unreadMessageSize = unreadMessageSize;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(long lastChatTime) {
        this.lastChatTime = lastChatTime;
    }

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
