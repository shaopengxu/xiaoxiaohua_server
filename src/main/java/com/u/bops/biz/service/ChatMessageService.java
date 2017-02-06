package com.u.bops.biz.service;

import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.redis.ChatMessageRedisDao;
import com.u.bops.biz.redis.RedisDao;

import java.util.List;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
public class ChatMessageService {

    private ChatMessageRedisDao chatMessageRedisDao;

    private WeixinUserService weixinUserService;

    public void publishMessage(ChatMessage message) {
        chatMessageRedisDao.addChatMessage(message);
        boolean updateSuccess = chatMessageRedisDao.updateUserUnreadMessage(message.getToOpenId(), message.getFromOpenId(), message.getMessageId());
        //当自己发送message，自己的unreadMessageId就会清空
        updateSuccess = chatMessageRedisDao.updateUserUnreadMessage(message.getFromOpenId(), message.getToOpenId(), -1);
        weixinUserService.sendMessage(message);

    }

    public void readMessage(String openId, String fromOpenId) {
        boolean updateSuccess = chatMessageRedisDao.updateUserUnreadMessage(openId, fromOpenId, -1);
    }

    public int getUnreadMessageSize(String openId) {
        return chatMessageRedisDao.getUnreadMessageSize(openId);
    }

    public List<ChatMessage> getChatMessages(String openId, String messageId, int size) {
        return chatMessageRedisDao.getChatMessages(openId, messageId, size);
    }


    public boolean loginUserPushUnreadMessage(String openId) {

        return true;
    }
}
