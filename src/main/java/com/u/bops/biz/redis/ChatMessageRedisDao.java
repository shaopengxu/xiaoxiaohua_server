package com.u.bops.biz.redis;

import com.u.bops.biz.domain.ChatMessage;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
@Service
public class ChatMessageRedisDao {

    @Autowired
    private RedisDao redisDao;

    private static final String CHAT = "CHAT";
    private static final String CONTENT = "CONTENT";
    private static final String DATE = "DATE";
    private static final String FROM_OPENID = "FROM_OPENID";
    private static final String TO_OPENID = "TO_OPENID";
    private static final String TYPE = "TYPE";
    private static final String SHOW_TYPE = "SHOW_TYPE";
    private static final String USER_CHAT = "USER_CHAT";

    private static final String CHAT_ID_INCR = "CHAT_ID_INCR";

    private static final String USER_UNREAD_MESSAGE_SIZE = "USER_UNREAD_MESSAGE_SIZE";


    public boolean addChatMessage(ChatMessage chatMessage) {
        // 赋值 key
        chatMessage.setMessageId(redisDao.incr(CHAT_ID_INCR));
        //hset message
        String key = CHAT + "_" + chatMessage.getMessageId();
        redisDao.hset(key, CONTENT, chatMessage.getContent());
        redisDao.hset(key, DATE, String.valueOf(chatMessage.getDate()));
        redisDao.hset(key, FROM_OPENID, chatMessage.getFromOpenId());
        redisDao.hset(key, TO_OPENID, chatMessage.getToOpenId());
        redisDao.hset(key, TYPE, chatMessage.getType());
        redisDao.hset(key, SHOW_TYPE, chatMessage.getShowType());

        //lpush fromOpenid messageid
        redisDao.rpush(USER_CHAT + "_" + chatMessage.getFromOpenId() + "_" + chatMessage.getToOpenId(), String.valueOf(chatMessage.getMessageId()));
        //lpush toOpenid messageid
        redisDao.rpush(USER_CHAT + "_" + chatMessage.getToOpenId() + "_" + chatMessage.getFromOpenId(), String.valueOf(chatMessage.getMessageId()));

        return true;
    }

    public int getUnreadMessageSize(String openId, String friendOpenId) {
        return Integer.parseInt(redisDao.hget(USER_UNREAD_MESSAGE_SIZE + "_" + openId, friendOpenId));
    }

    public Map<String, Integer> getUnreadMessageSizes(String openId) {
        Map<String, Integer> result = new HashedMap();
        Map<String, String> sizes = redisDao.hgetAll(USER_UNREAD_MESSAGE_SIZE + "_" + openId);
        for (String key : sizes.keySet()) {
            result.put(key, Integer.parseInt(sizes.get(key)));
        }
        return result;
    }


    public boolean incrUnreadMessageSize(String openId, String fromOpenId, long messageId) {
        redisDao.hincr(USER_UNREAD_MESSAGE_SIZE + "_" + openId, fromOpenId);
        return true;
    }

    public boolean readMessage(String openId, String fromOpenId) {
        redisDao.hset(USER_UNREAD_MESSAGE_SIZE + "_" + openId, fromOpenId, "0");
        return true;
    }

    public ChatMessage getChatMessage(String messageId) {
        String key = CHAT + "_" + messageId;
        ChatMessage chatMessage = new ChatMessage();
        Map<String, String> map = redisDao.hgetAll(key);
        chatMessage.setMessageId(Long.parseLong(messageId));
        chatMessage.setContent(map.get(CONTENT));
        chatMessage.setDate(Long.parseLong(map.get(DATE)));
        chatMessage.setFromOpenId(map.get(FROM_OPENID));
        chatMessage.setToOpenId(map.get(TO_OPENID));
        chatMessage.setType(map.get(TYPE));
        chatMessage.setShowType(map.get(SHOW_TYPE));
        return chatMessage;
    }

    /**
     * 获取某段聊天记录，beginMessageId 是为了防止读到删除信息设置的起点, 当size== -1 and  endMessageId == -1，返回beginMessageId到最后
     * 返回的message不包括beginMessageId 也不包括endMessageId
     * @param openId
     * @param friendOpenId
     * @param endMessageId
     * @param beginMessageId
     * @param size
     * @return
     */
    public List<ChatMessage> getChatMessages(String openId, String friendOpenId, String endMessageId, String beginMessageId, int size) {
        String key = USER_CHAT + "_" + openId + "_" + friendOpenId;
        long length = redisDao.lsize(key);
        if (length == 0) {
            return new ArrayList<>();
        }
        long toIndex = 0;
        long fromIndex = StringUtils.equals("-1", beginMessageId) ? 0 :
                redisDao.getIndexOfElementOfSortedList(key, beginMessageId);
        if (StringUtils.equals(endMessageId, "-1") && size == -1) {
            toIndex = redisDao.lsize(key);
        } else {
            if (StringUtils.equals("-1", endMessageId)) {
                toIndex = redisDao.lsize(key);
                fromIndex = toIndex - size < fromIndex ? fromIndex : toIndex - size;
            } else {
                toIndex = redisDao.getIndexOfElementOfSortedList(key, endMessageId);
                if (toIndex <= 0) {
                    return new ArrayList<>();
                }
                fromIndex = toIndex - size < fromIndex ? fromIndex : toIndex - size;
            }
        }

        List<String> messageIds = redisDao.lrange(key, fromIndex, toIndex);

        // 过滤beginMessageId、endMessageId
        if (messageIds.size() > 0 && StringUtils.equals(messageIds.get(0), beginMessageId)) {
            messageIds.remove(0);
        }
        if (messageIds.size() > 0 && StringUtils.equals(messageIds.get(messageIds.size() - 1), endMessageId)) {
            messageIds.remove(messageIds.size() - 1);
        }

        List<ChatMessage> chatMessages = new ArrayList<>();
        for (String messageId : messageIds) {
            chatMessages.add(getChatMessage(messageId));
        }
        return chatMessages;
    }

    public String getLastMessageId(String openId, String friendOpenId) {
        String key = USER_CHAT + "_" + openId + "_" + friendOpenId;
        long size = redisDao.lsize(key);
        long index = size - 1;
        if (index >= 0) {
            return redisDao.lindex(key, index);
        }
        return null;
    }

    public ChatMessage getLastMessage(String openId, String friendOpenId) {
        String lastMessageId = getLastMessageId(openId, friendOpenId);
        return getChatMessage(lastMessageId);
    }
}
