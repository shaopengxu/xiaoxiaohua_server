package com.u.bops.biz.service;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shpng on 2015/9/13.
 */
@Service
public class ChannelMessageService {

    public static Map<Long, Channel> channelMap = new HashMap<>();
    public static Map<Long, Set<String>> projectClientMap = new ConcurrentHashMap<>();



}

