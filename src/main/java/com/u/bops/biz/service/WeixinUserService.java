package com.u.bops.biz.service;

import com.u.bops.biz.dal.mapper.WeixinUserMapper;
import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.vo.Result;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */

@Service
public class WeixinUserService {

    @Autowired
    private WeixinUserMapper weixinUserMapper;

    public  Map<String, Channel> userOpenIdChannelMap = new ConcurrentHashMap<>();

    public  Map<String, WeixinUser> weixinUserMap = new ConcurrentHashMap<>();

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

    public boolean sendMessage(ChatMessage chatMessage) {
        Channel channel = userOpenIdChannelMap.get(chatMessage.getToOpenId());
        if (channel != null && channel.isActive()) {
            Result<ChatMessage> message = Result.success(chatMessage, Result.TYPE_PUBLISH_MESSAGE);
            TextWebSocketFrame frame = new TextWebSocketFrame(message.toString());
            channel.writeAndFlush(frame);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeUsers() {
        //TODO 定时清理不在线的user
        return true;
    }

    public boolean checkUserLogin(String openId, String password) {
        return true;
    }

    public void createWeixinUser(WeixinUser weixinUser) {
        //TODO 增加用户
    }
}
