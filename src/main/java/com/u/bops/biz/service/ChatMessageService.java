package com.u.bops.biz.service;

import com.u.bops.biz.dal.mapper.FriendShipMapper;
import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.redis.ChatMessageRedisDao;
import com.u.bops.biz.redis.FriendShipRedisDao;
import org.apache.commons.lang.StringUtils;
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

    public List<ChatMessage> getFriendChatMessages(String openId, String friendOpenId, String endMessageId, int size) {
        FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
        if (friendShip == null || friendShip.isDeleted()) {
            return new ArrayList<>();
        }
        String beginMessageId = String.valueOf(friendShip.getLasteleteMessageId());
        return chatMessageRedisDao.getChatMessages(openId, friendOpenId, endMessageId, beginMessageId, size);
    }

    public void askForMsgPush(String openId, String friendOpenId, String lastMessageId) {
        FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
        if (friendShip == null || friendShip.isDeleted()) {
            return ;
        }
        String beginMessageId = String.valueOf(friendShip.getLasteleteMessageId());
        beginMessageId = StringUtils.equals(beginMessageId, "-1") ? lastMessageId :
                (Long.parseLong(beginMessageId) > Long.parseLong(lastMessageId) ? beginMessageId : lastMessageId);

        List<ChatMessage> chatMessages = chatMessageRedisDao.getChatMessages(openId, friendOpenId, "-1", beginMessageId, -1);
        weixinUserService.pushUnreadMessage(openId, chatMessages);
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
