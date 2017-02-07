package com.u.bops.biz.service;

import com.u.bops.biz.dal.mapper.FriendShipMapper;
import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.redis.ChatMessageRedisDao;
import com.u.bops.biz.redis.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRedisDao chatMessageRedisDao;

    @Autowired
    private FriendShipMapper friendShipMapper ;

    @Autowired
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

    public List<ChatMessage> getFriendChatMessages(String openId, String friendOpenId, String endMessageId, int size) {
        FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
        if (friendShip == null || friendShip.isDelete()) {
            return new ArrayList<>();
        }
        String beginMessageId = String.valueOf(friendShip.getLasteleteMessageId());
        return chatMessageRedisDao.getChatMessages(openId, friendOpenId, endMessageId, beginMessageId, size);
    }

    public boolean pushUnreadMessage(String openId) {

        return true;
    }

    public void askForMsgPush(String openId) {

    }

    public void deleteChatMessages(String openId, String friendOpenId) {

    }
}
