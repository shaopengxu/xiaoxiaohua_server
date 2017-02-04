package com.u.bops.websockets;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User: jinsong
 */
public class ChannelService {

    private static final Logger logger = LoggerFactory.getLogger(ChannelService.class);

    private Cache<String, Channel> channelCache = CacheBuilder
            .newBuilder().maximumSize(1000000).expireAfterAccess(1, TimeUnit.HOURS).build();

    public boolean associated(Channel c) {
        String defaultName = getChannelAttachName(c);
        if (StringUtils.isBlank(defaultName)) {
            return false;
        }
        Channel cacheChannel = channelCache.getIfPresent(defaultName);
        if (cacheChannel != c) {
            return false;
        }
        return true;
    }

    public Channel getChannel(String defaultName) {
        return channelCache.getIfPresent(defaultName);
    }

    public String getChannelAttachName(Channel c) {
        return c.attr(WebSocketServerHandler.DEFAULT_NAME_KEY).get();
    }

    public void associate(Channel c, String defaultName) {
        // 切换帐号的处理
        String oldDefaultName = c.attr(WebSocketServerHandler.DEFAULT_NAME_KEY).getAndSet(defaultName);
        if (oldDefaultName != null) {
            channelCache.invalidate(oldDefaultName);
        }
        channelCache.put(defaultName, c);
    }

    public void remove(String defaultName, Channel c) {
        logger.info("default_name removed :" + defaultName);
        Channel channelInCache = channelCache.getIfPresent(defaultName);
        if (channelInCache == c) {
            channelCache.invalidate(defaultName);
            // c.attr(WebSocketServerHandler.CLIENT_KEY).remove();
        }
    }

    public <T> void sendMessageToAll(Result<T> msg) {
        Map<String, Channel> map = channelCache.asMap();
        logger.info("sending data to all:" + msg);
        for (Channel c : map.values()) {
            if (c.isActive()) {
                TextWebSocketFrame frame = new TextWebSocketFrame(msg.toString());
                c.writeAndFlush(frame);
            }
        }
    }

    public <T> void sendMessage(String defaultName, String msg) {
        TextWebSocketFrame frame = new TextWebSocketFrame(msg);
        Channel c = channelCache.getIfPresent(defaultName);
        if (c != null) {
            c.writeAndFlush(frame);
        }
    }
}
