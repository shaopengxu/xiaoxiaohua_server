package com.u.bops.biz.service;

import com.u.bops.biz.dal.mapper.WeixinUserMapper;
import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.vo.Result;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */

@Service
public class WeixinUserService {

    @Autowired
    private WeixinUserMapper weixinUserMapper;

    public Map<String, Channel> userOpenIdChannelMap = new ConcurrentHashMap<>();

    public Map<String, WeixinUser> weixinUserMap = new ConcurrentHashMap<>();

    public WeixinUser getWeixinUser(String openId) {
        WeixinUser weixinUser = weixinUserMap.get(openId);
        if (weixinUser == null) {
            weixinUser = weixinUserMapper.getWeixinUser(openId);
            if (weixinUser != null) {
                weixinUserMap.put(openId, weixinUser);
            }
        }
        return weixinUser;
    }

    /**
     * 发送聊天
     *
     * @param chatMessage
     * @return
     */
    public boolean sendMessage(ChatMessage chatMessage) {
        Channel channel = userOpenIdChannelMap.get(chatMessage.getToOpenId());
        if (channel != null && channel.isActive()) {
            Result<ChatMessage> message = Result.success(chatMessage, Result.TYPE_PUSH_MESSAGE);
            TextWebSocketFrame frame = new TextWebSocketFrame(message.toString());
            channel.writeAndFlush(frame);
        }

        //自己也发送一条消息
        channel = userOpenIdChannelMap.get(chatMessage.getFromOpenId());
        if (channel != null && channel.isActive()) {
            Result<ChatMessage> message = Result.success(chatMessage, Result.TYPE_PUSH_MESSAGE);
            TextWebSocketFrame frame = new TextWebSocketFrame(message.toString());
            channel.writeAndFlush(frame);
        }
        return true;
    }

    /**
     * 每隔一段时间清理用户
     *
     * @return
     */
    @Scheduled(initialDelay = 1000000, fixedDelay = 1000000)
    public void removeUsers() {
        for (Iterator<String> iterator = userOpenIdChannelMap.keySet().iterator(); iterator.hasNext(); ) {
            String openId = iterator.next();
            if (!userOpenIdChannelMap.get(openId).isActive()) {
                iterator.remove();
                weixinUserMap.get(openId).setOverdue(true);
                weixinUserMap.remove(openId);
            }
        }
    }

    /**
     * 检查用户登录密码是否正确
     *
     * @param openId
     * @param password
     * @return
     */
    public boolean checkUserLogin(String openId, String password) {
        WeixinUser weixinUser = getWeixinUser(openId);
        return StringUtils.equals(weixinUser.getPassword(), password);
    }

    public boolean createWeixinUser(WeixinUser weixinUser) {
        WeixinUser dbWeixinUser = getWeixinUser(weixinUser.getOpenId());
        if (dbWeixinUser != null) {
            return false;
        }
        return weixinUserMapper.insert(weixinUser) > 0;
    }

    /**
     * 可能不需要指定friendOpenID,方法待定
     * @param openId
     * @param unreadMessages
     */
    public void pushUnreadMessage(String openId, Map<String, List<ChatMessage>> unreadMessages) {

        Channel channel = userOpenIdChannelMap.get(openId);
        if (channel != null && channel.isActive()) {
            Result<Map<String, List<ChatMessage>>> message = Result.success(unreadMessages, Result.TYPE_PUSH_UNREAD_MESSAGE);
            TextWebSocketFrame frame = new TextWebSocketFrame(message.toString());
            channel.writeAndFlush(frame);
        }
    }

    public void pushUnreadMessage(String openId, List<ChatMessage> chatMessages) {

        Channel channel = userOpenIdChannelMap.get(openId);
        if (channel != null && channel.isActive()) {
            Result<List<ChatMessage>> message = Result.success(chatMessages, Result.TYPE_PUSH_UNREAD_MESSAGE);
            TextWebSocketFrame frame = new TextWebSocketFrame(message.toString());
            channel.writeAndFlush(frame);
        }
    }
}
