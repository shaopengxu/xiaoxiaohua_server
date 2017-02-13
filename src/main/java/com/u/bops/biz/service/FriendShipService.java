package com.u.bops.biz.service;

import com.u.bops.biz.dal.mapper.FriendShipMapper;
import com.u.bops.biz.domain.ChatMessage;
import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.redis.ChatMessageRedisDao;
import com.u.bops.biz.redis.FriendShipRedisDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */

@Service
public class FriendShipService {

    @Autowired
    private FriendShipRedisDao friendShipRedisDao;

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private FriendShipMapper friendShipMapper;

    @Autowired
    private ChatMessageRedisDao chatMessageRedisDao;

    /**
     * 添加好友
     * @param openId
     * @param friendOpenId
     * @param nickName
     * @param image
     * @return
     */
    public boolean addFriend(String openId, String friendOpenId, String nickName, String image) {
        //TODO 考虑之前加为好友，中间删了，后来又加为好友

        FriendShip friendShipA = getFriendShip(openId, friendOpenId);
        if (friendShipA == null) {
            friendShipA = new FriendShip();
            friendShipA.setOpenId(openId);
            friendShipA.setFriendOpenId(friendOpenId);
            friendShipA.setFriendNickName(nickName);
            friendShipA.setFriendImage(image);
            friendShipMapper.insert(friendShipA);
        }

        FriendShip friendShipB = getFriendShip(friendOpenId, openId);
        if (friendShipB == null) {
            WeixinUser user = weixinUserService.getWeixinUser(openId);
            friendShipB = new FriendShip();
            friendShipB.setOpenId(friendOpenId);
            friendShipB.setFriendOpenId(openId);
            friendShipB.setFriendNickName(user.getNickName());
            friendShipB.setFriendImage(user.getAvatarUrl());
            friendShipMapper.insert(friendShipB);
        }

        friendShipRedisDao.addFriendShip(openId, friendOpenId);
        return true;
    }

    private FriendShip getFriendShip(String openId, String friendOpenId) {
        return friendShipMapper.getFriendShip(openId, friendOpenId);
    }

    /**
     * 获取好友信息，包括unreadMessageSize、 最近聊天时间、最后一句对话
     * @param openId
     * @return
     */
    public List<FriendShip> getFriends(String openId) {
        List<String> friendOpenIds = friendShipRedisDao.getFriendOpenIds(openId);
        List<FriendShip> friendShips = new ArrayList<>();
        Map<String, Integer> unreadMessageSizes = chatMessageRedisDao.getUnreadMessageSizes(openId);
        for (String friendOpenId : friendOpenIds) {
            //TODO 批量获取
            FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);

            friendShip.setUnreadMessageSize(unreadMessageSizes.get(friendOpenId) == null ? 0 : unreadMessageSizes.get(friendOpenId));
            ChatMessage chatMessage = chatMessageRedisDao.getLastMessage(openId, friendOpenId);
            if (chatMessage != null) {
                friendShip.setLastChatTime(chatMessage.getDate());
                friendShip.setLastMessage(chatMessage.getContent());
            } else {
                friendShip.setLastChatTime(0);
                friendShip.setLastMessage("");
            }
            friendShips.add(friendShip);
        }
        return friendShips;
    }

    public void removeFriendShip(String openId, String friendOpenId) {
        if (existsFriendShip(openId, friendOpenId)) {
            friendShipRedisDao.removeFriendShip(openId, friendOpenId);
            friendShipMapper.setDelete(openId, friendOpenId);
        }
    }

    public boolean existsFriendShip(String openId, String friendOpenId) {
        return friendShipRedisDao.existsFriendShip(openId, friendOpenId);
    }

    public void updateFriendMessage(String openId, String friendOpenId, String nickName, String image) {
        FriendShip friendShip = friendShipMapper.getFriendShip(openId, friendOpenId);
        if (friendShip == null) {
            return ;
        }
        if (StringUtils.isNotBlank(nickName)) {
            friendShip.setFriendNickName(nickName);
        }
        if (StringUtils.isNotBlank(image)) {
            friendShip.setFriendImage(image);
        }
        friendShipMapper.updateById(friendShip);
    }

    public int getFriendSize(String openId) {
        return friendShipRedisDao.getFriendOpenIds(openId).size();
    }
}
