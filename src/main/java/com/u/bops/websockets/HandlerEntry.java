package com.u.bops.websockets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.service.ChatMessageService;
import com.u.bops.biz.service.WeixinUserService;
import com.u.bops.biz.vo.Result;
import com.u.bops.util.Pair;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class HandlerEntry {

    private static final Logger logger = LoggerFactory
            .getLogger(HandlerEntry.class);

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private ChatMessageService chatMessageService;

    public BlockingQueue<Pair<Result, Channel>> channelMsgBlockingQueue = new LinkedBlockingQueue<>();

    public static final String ATTRIBUTEKEY_OPENID = "ATTRIBUTEKEY_OPENID";
    public static final String FIELD_OPENID = "openId";
    public static final String FIELD_PASSWORD = "password";

    public static AttributeKey<String> OPENID_KEY = AttributeKey
            .valueOf(ATTRIBUTEKEY_OPENID);

    @Autowired
    private ThreadPoolTaskExecutor executor;

    private HandlerEntry() {

    }

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void pushMsg() {
        while (true) {
            try {
                Pair<Result, Channel> pair = channelMsgBlockingQueue.take();
                Result result = pair.getL();
                Channel c = pair.getR();

                if (result.getCode() != Message.NO_REPLY) {
                    c.writeAndFlush(new TextWebSocketFrame(result.toString()));
                }
            } catch (InterruptedException e) {
                logger.error("e", e);
            }
        }
    }


    public void execute(final Channel channel, final String request) {
        // logger.info(request.substring(0,Math.min(request.length(), 30)));
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Result result = new Result();
                String messageType = null;
                try {

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(request).getAsJsonObject();
                    JsonElement je = jsonObject.get(Result.TYPE);
                    messageType = je != null ? je.getAsString() : null;
                    if (StringUtils.isBlank(messageType)) {
                        result.setCode(Message.API_NOT_EXIST);
                        channelMsgBlockingQueue.put(new Pair(result, channel));
                        return;
                    }
                    result.setType(messageType);
                    if (StringUtils.equals(Result.TYPE_LOGIN, messageType)) {
                        //LOGIN
                        String openId = channel.attr(OPENID_KEY).get();
                        if (!StringUtils.isBlank(openId)) {

                            String dataOpenId = jsonObject.get(FIELD_OPENID).getAsString();
                            if (StringUtils.equals(openId, dataOpenId)) {
                                logger.error("用户重复登录, openId = " + openId);
                            }else{
                                logger.error("用户登录异常，用不同openId登录, openId = " + openId + " ,another openId = " + dataOpenId);
                                channel.attr(OPENID_KEY).remove();
                                channel.close();
                            }
                        } else {
                            openId = jsonObject.get(FIELD_OPENID).getAsString();
                            String password = jsonObject.get(FIELD_PASSWORD).getAsString();
                            boolean loginSuccess = weixinUserService.checkUserLogin(openId, password);
                            if (loginSuccess) {
                                channel.attr(OPENID_KEY).set(openId);
                                weixinUserService.userOpenIdChannelMap.put(openId, channel);
                            }
                        }
                    } else if (StringUtils.equals(messageType, Result.TYPE_PUSH_MESSAGE)) {
                        String openId = channel.attr(OPENID_KEY).get();
                        if (openId == null) {
                            result.setCode(Message.NOT_LOGIN);
                            channelMsgBlockingQueue.put(new Pair(result, channel));
                            return;
                        }

                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage = getChatMessageFromJson(jsonObject.get("data").getAsJsonObject());
                        chatMessage.setDate(new Date());
                        chatMessageService.publishMessage(chatMessage);
                    }
                    if (jsonObject.has(Result.SEQ)) {
                        String sequence = jsonObject.get(Result.SEQ).getAsString();
                        if (StringUtils.isNotBlank(sequence)) {
                            result.setSeq(sequence);
                        }
                    }

                } catch (Exception e) {
                    logger.error("error", e);
                    result.setCode(Message.EXCEPTION_ERROR);
                    try {
                        channelMsgBlockingQueue.put(new Pair(result, channel));
                    } catch (InterruptedException e1) {
                        logger.error("error", e);
                    }
                }
            }
        });
    }

    private ChatMessage getChatMessageFromJson(JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject.toString(), ChatMessage.class);
    }


    public static void main(String[] args) {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parse("abc");

    }

}
