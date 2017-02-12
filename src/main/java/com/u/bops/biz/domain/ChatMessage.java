package com.u.bops.biz.domain;

import java.util.Date;

public class ChatMessage {
    //messageId（保证递增 用redis的自增序列）, date, from, to, content, type
    private long messageId;
    private long date;
    private String fromOpenId;
    private String toOpenId;
    private String content;
    private String type;
    private String fromUserDelete;
    private String toUserDelete;

    public String getFromUserDelete() {
        return fromUserDelete;
    }

    public void setFromUserDelete(String fromUserDelete) {
        this.fromUserDelete = fromUserDelete;
    }

    public String getToUserDelete() {
        return toUserDelete;
    }

    public void setToUserDelete(String toUserDelete) {
        this.toUserDelete = toUserDelete;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getFromOpenId() {
        return fromOpenId;
    }

    public void setFromOpenId(String fromOpenId) {
        this.fromOpenId = fromOpenId;
    }

    public String getToOpenId() {
        return toOpenId;
    }

    public void setToOpenId(String toOpenId) {
        this.toOpenId = toOpenId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}