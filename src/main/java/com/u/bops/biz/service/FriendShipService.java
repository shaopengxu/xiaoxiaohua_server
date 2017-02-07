package com.u.bops.biz.service;

import com.u.bops.biz.dal.mapper.FriendShipMapper;
import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.redis.FriendShipRedisDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        FriendShip friendShipA = new FriendShip();
        friendShipA.setOpenId(openId);
        friendShipA.setFriendOpenId(friendOpenId);
        friendShipA.setFriendNickName(nickName);
        friendShipA.setFriendImage(image);
        friendShipMapper.insert(friendShipA);
        WeixinUser user = weixinUserService.getWeixinUser(openId);
        FriendShip friendShipB = new FriendShip();
        friendShipA.setOpenId(friendOpenId);
        friendShipA.setFriendOpenId(openId);
        friendShipA.setFriendNickName(user.getNickName());
        friendShipA.setFriendImage(user.getAvatarUrl());
        friendShipMapper.insert(friendShipB);
        friendShipRedisDao.addFriendShip(openId, friendOpenId);
        return true;
    }

    public List<FriendShip> getFriends(String openId) {
        List<String> friendOpenIds = friendShipRedisDao.getFriendOpenIds(openId);
        List<FriendShip> friendShips = new ArrayList<>();
        for (String friendOpenId : friendOpenIds) {
            friendShips.add(friendShipMapper.getFriendShip(openId, friendOpenId));
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
}
