package com.u.bops.biz.domain;

import java.util.ArrayList;
import java.util.List;

public class UserMessage{

    private String openId;
    private List<ChatMessage> messageList = new ArrayList<ChatMessage>();

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public List<ChatMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }
}