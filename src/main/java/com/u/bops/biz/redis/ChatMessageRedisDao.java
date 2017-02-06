package com.u.bops.biz.redis;

import com.u.bops.biz.domain.ChatMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
public class ChatMessageRedisDao {

    private RedisDao redisDao;

    private String fromOpenId;
    private String toOpenId;
    private String content;
    private String type;

    private static final String CHAT = "CHAT";
    private static final String CONTENT = "CONTENT";
    private static final String DATE = "DATE";
    private static final String FROM_OPENID = "FROM_OPENID";
    private static final String TO_OPENID = "TO_OPENID";
    private static final String TYPE = "TYPE";
    private static final String USER_CHAT = "USER_CHAT";

    private static final String CHAT_ID_INCR = "CHAT_ID_INCR";

    private static final String USER_UNREAD_MESSAGE_ID = "USER_UNREAD_MESSAGE_ID";
    private static final String USER_UNREAD_MESSAGE_SIZE = "USER_UNREAD_MESSAGE_SIZE";


    public boolean addChatMessage(ChatMessage chatMessage) {
        // 赋值 key
        chatMessage.setMessageId(redisDao.incr(CHAT_ID_INCR));
        //hset message
        String key = CHAT + "_" + chatMessage.getMessageId();
        redisDao.hset(key, CONTENT, chatMessage.getContent());
        redisDao.hset(key, DATE, String.valueOf(chatMessage.getDate().getTime()));
        redisDao.hset(key, FROM_OPENID, chatMessage.getFromOpenId());
        redisDao.hset(key, TO_OPENID, chatMessage.getToOpenId());
        redisDao.hset(key, TYPE, chatMessage.getType());

        //lpush fromOpenid messageid
        redisDao.lpush(USER_CHAT + "_" + chatMessage.getFromOpenId() + "_" + chatMessage.getToOpenId(), String.valueOf(chatMessage.getMessageId()));
        //lpush toOpenid messageid
        redisDao.lpush(USER_CHAT + "_" + chatMessage.getToOpenId() + "_" + chatMessage.getFromOpenId(), String.valueOf(chatMessage.getMessageId()));

        return true;
    }

    public int getUnreadMessageSize(String openId) {
        return Integer.parseInt(redisDao.hget(USER_UNREAD_MESSAGE_SIZE, openId));
    }

    public boolean updateUserUnreadMessage(String openId, String fromOpenId, long messageId) {
        redisDao.hset(USER_UNREAD_MESSAGE_ID + "_" + openId, fromOpenId, String.valueOf(messageId));
        if (messageId == -1) {
            redisDao.hset(USER_UNREAD_MESSAGE_SIZE + "_" + openId, fromOpenId, "0");
        } else {
            redisDao.hincr(USER_UNREAD_MESSAGE_SIZE + "_" + openId, fromOpenId);
        }
        return true;
    }

    public ChatMessage getChatMessage(String messageId) {
        String key = CHAT + "_" + messageId;
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(Long.parseLong(messageId));
        chatMessage.setContent(redisDao.hget(key, CONTENT));
        chatMessage.setDate(new Date(Long.parseLong(redisDao.hget(key, DATE))));
        chatMessage.setFromOpenId(redisDao.hget(key, FROM_OPENID));
        chatMessage.setToOpenId(redisDao.hget(key, TO_OPENID));
        chatMessage.setType(redisDao.hget(key, TYPE));
        return chatMessage;
    }

    public List<ChatMessage> getChatMessages(String openId, String lastMessageId, int size) {
        String key = USER_CHAT + "_" + openId;

        long toIndex = 0;
        long fromIndex = 0;
        if ("-1".equals(lastMessageId)) {
            toIndex = redisDao.lsize(key);
            fromIndex = toIndex - size < 0 ? 0 : toIndex - size;
        } else {
            toIndex = redisDao.getIndexOfElementOfSortedList(key, lastMessageId);
            if (toIndex <= 0) {
                return new ArrayList<>();
            }
            fromIndex = toIndex - size < 0 ? 0 : toIndex - size;
        }
        List<String> messageIds = redisDao.lrange(key, fromIndex, toIndex);
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (String messageId : messageIds) {
            chatMessages.add(getChatMessage(messageId));
        }
        return chatMessages;
    }

}
