package com.u.bops.websockets;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.*;
import com.u.bops.biz.domain.*;
import com.u.bops.util.Pair;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;

@Service
public class HandlerEntry {

    private static final Logger logger = LoggerFactory
            .getLogger(HandlerEntry.class);
    private static final long VALID_TIME_RANGE_IN_SECONDS = TimeUnit.HOURS
            .toSeconds(2L);

    private Map<String, WebsocketHandler> websocketHandlerMap = new ConcurrentHashMap<String, WebsocketHandler>();

    private boolean dev = false;

    public Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    public Map<String, Set<String>> projectClientMap = new ConcurrentHashMap<>();



    public BlockingQueue<Pair<Result, Channel>> channelBlockingQueue = new LinkedBlockingQueue<>();


    @Autowired
    private ThreadPoolTaskExecutor executor;

    private HandlerEntry() {

    }

    private long startTime = System.currentTimeMillis();

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void push() {
        while (true) {
            try {
                Pair<Result, Channel> pair = channelBlockingQueue.take();
                Result result = pair.getL();
                Channel c = pair.getR();
                if (System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(5)) {
                    logger.info("channel blocking queue, size : " + channelBlockingQueue.size());
                    startTime = System.currentTimeMillis();
                }
                if (result.getCode() != Message.NO_REPLY) {
                    c.writeAndFlush(new TextWebSocketFrame(result.toString()));
                }
            } catch (InterruptedException e) {
                logger.error("e", e);
            }
        }
    }


    public void execute(final Channel c, final String request) {
        //logger.info(request.substring(0,Math.min(request.length(), 30)));
        executor.execute(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                Result result = new Result();
                String messageType = null;
                try {

                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonObject = jsonParser.parse(request).getAsJsonObject();
                    JsonElement je = jsonObject.get(MessageKeys.MESSAGE_TYPE);
                    messageType = je != null ? je.getAsString() : null;
                    if (StringUtils.isBlank(messageType)) {
                        result.setCode(Message.API_NOT_EXIST);
                        channelBlockingQueue.put(new Pair(result, c));
                        return;
                    }
                    result.setMsgType(messageType);
                    if (jsonObject.has(MessageKeys.SEQ)) {
                        String sequence = jsonObject.get(MessageKeys.SEQ).getAsString();
                        if (StringUtils.isNotBlank(sequence)) {
                            result.setSeq(sequence);
                        }
                    }

                    result.setCode(Message.NO_REPLY);
                } catch (Exception e) {
                    result.setCode(Message.EXCEPTION_ERROR);
                }
                long costTime = System.currentTimeMillis() - startTime;
                logger.info("msgType : " + messageType + ", cost time : " + costTime);
            }
        });
    }

    private void sendAskClientInfo(Channel c) {
        Result result = new Result();
        result.setMsgType("askclientinfo");
        TextWebSocketFrame frame = new TextWebSocketFrame(result.toString());
        c.writeAndFlush(frame);
        return;
    }



    public BlockingQueue<Pair<Map, Channel>> channelMsgBlockingQueue = new LinkedBlockingQueue<>();

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void pushMsg(){
        while (true) {
            try {
                Pair<Map, Channel> pair = channelMsgBlockingQueue.take();
                Map result = pair.getL();
                Channel c = pair.getR();
                if(System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(5)){
                    logger.info("channel blocking queue, size : " + channelBlockingQueue.size());
                    startTime = System.currentTimeMillis();
                }
                c.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(result)));
                Thread.sleep(50);
            } catch (Exception e) {
                logger.error("e", e);
            }
        }
    }

    public BlockingQueue<Pair<ResultInfo, Channel>> channelResultInfoBlockingQueue = new LinkedBlockingQueue<>();

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void pushResultInfo(){
        while (true) {
            try {
                Pair<ResultInfo, Channel> pair = channelResultInfoBlockingQueue.take();
                ResultInfo result = pair.getL();
                Channel c = pair.getR();
                if(System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(5)){
                    logger.info("channel blocking queue, size : " + channelBlockingQueue.size());
                    startTime = System.currentTimeMillis();
                }
                c.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(result)));
                Thread.sleep(50);
            } catch (Exception e) {
                logger.error("e", e);
            }
        }
    }

    public void sendMsg(String puuid, String msgType) {
        Set<String> set = projectClientMap.get(puuid);
        if (set == null) {
            return;
        }
        Map<String, Object> m = new HashMap<>();
        m.put("msgType", msgType);

        String s = new Gson().toJson(m);
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext(); ) {
            String clientid = iterator.next();

            Channel c = channelMap.get(clientid);
            if (c != null && c.isActive()) {
                try {
                    channelMsgBlockingQueue.put(new Pair<Map, Channel>(m, c));
                } catch (InterruptedException e) {
                    logger.error("e", e);
                }
            }
        }
    }


    public static void main(String[] args) {
        JsonParser jsonParser = new JsonParser();
        jsonParser.parse("abc");

    }

    public int getProjectClientCount(String uuid) {
        Set<String> set = projectClientMap.get(uuid);
        if (set == null) {
            return 0;
        }
        return set.size();
    }

}
