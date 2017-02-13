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

    /**
     * 用户聊天
     * @param message
     */
    public void publishMessage(ChatMessage message) {
        chatMessageRedisDao.addChatMessage(message);
        chatMessageRedisDao.incrUnreadMessageSize(message.getToOpenId(), message.getFromOpenId(), message.getMessageId());
        weixinUserService.sendMessage(message);
    }

    /**
     * 消息已读
     * @param openId
     * @param fromOpenId
     */
    public void readMessage(String openId, String fromOpenId) {
        chatMessageRedisDao.readMessage(openId, fromOpenId);
    }

    /**
     * 获取历史聊天记录
     * @param openId
     * @param friendOpenId
     * @param endMessageId
     * @param size
     * @return
     */
    public List<ChatMessage> getFriendChatMessages(String openId, String friendOpenId, String endMessageId, int size) {
        FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
        if (friendShip == null || friendShip.isDeleted()) {
            return new ArrayList<>();
        }
        String beginMessageId = String.valueOf(friendShip.getLasteleteMessageId());
        return chatMessageRedisDao.getChatMessages(openId, friendOpenId, endMessageId, beginMessageId, size);
    }

    /**
     * 请求最新聊天记录
     * @param openId
     * @param friendOpenId
     * @param lastMessageId
     */
    public void askForMsgPush(String openId, String friendOpenId, String lastMessageId) {
        FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
        if (friendShip == null || friendShip.isDeleted()) {
            return ;
        }
        String beginMessageId = String.valueOf(friendShip.getLasteleteMessageId());
        beginMessageId = Long.parseLong(beginMessageId) > Long.parseLong(lastMessageId) ? beginMessageId : lastMessageId;

        List<ChatMessage> chatMessages = chatMessageRedisDao.getChatMessages(openId, friendOpenId, "-1", beginMessageId, -1);
        weixinUserService.pushUnreadMessage(openId, chatMessages);
    }

    /**
     * 清空聊天记录
     * @param openId
     * @param friendOpenId
     * @return
     */
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
