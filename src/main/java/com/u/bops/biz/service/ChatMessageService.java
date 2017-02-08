package com.u.bops.biz.service;

import com.u.bops.biz.dal.mapper.FriendShipMapper;
import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.redis.ChatMessageRedisDao;
import com.u.bops.biz.redis.FriendShipRedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private FriendShipRedisDao friendShipRedisDao;

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
        if (friendShip == null || friendShip.isDeleted()) {
            return new ArrayList<>();
        }
        String beginMessageId = String.valueOf(friendShip.getLasteleteMessageId());
        return chatMessageRedisDao.getChatMessages(openId, friendOpenId, endMessageId, beginMessageId, size);
    }

    public boolean pushUnreadMessage(String openId) {

        List<String> friendOpenIds = friendShipRedisDao.getFriendOpenIds(openId);
        List<FriendShip> friendShips = new ArrayList<>();
        for (String friendOpenId : friendOpenIds) {
            List<ChatMessage> chatMessages = chatMessageRedisDao.getUnreadChatMessages(openId, friendOpenId);
            if (chatMessages.size() > 0) {
                FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
                friendShip.setUnreadChatMessages(chatMessages);
            }
        }
        Map<String, List<ChatMessage>> unreadChatMessages = chatMessageRedisDao.getUnreadChatMessages(openId);
        weixinUserService.pushUnreadMessage(openId, unreadChatMessages);
        return true;
    }

    public void askForMsgPush(String openId) {
        pushUnreadMessage(openId);
    }

    public boolean deleteChatMessages(String openId, String friendOpenId) {

        FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
        if (friendShip == null) {
            return false;
        }
        String messageId = chatMessageRedisDao.getLastMessageId(openId, friendOpenId);
        if (messageId == null) {
            return false;
        }
        friendShip.setLasteleteMessageId(Long.parseLong(messageId));
        friendShipMapper.updateById(friendShip);

        return true;
    }
}
