package com.u.bops.biz.service;

import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.redis.FriendShipRedisDao;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
public class FriendShipService {

    private FriendShipRedisDao friendShipRedisDao;

    private WeixinUserService weixinUserService;

    /**
     * 添加好友
     * @param openId
     * @param friendOpenId
     * @param nickName
     * @param image
     * @return
     */
    public boolean addFriend(String openId, String friendOpenId, String nickName, String image) {
        FriendShip friendShipA = new FriendShip();
        friendShipA.setOpenId(openId);
        friendShipA.setFriendOpenId(friendOpenId);
        friendShipA.setFriendNickName(nickName);
        friendShipA.setFriendImage(image);
        friendShipRedisDao.addFriendShip(friendShipA);
        WeixinUser user = weixinUserService.getWeixinUser(openId);
        FriendShip friendShipB = new FriendShip();
        friendShipA.setOpenId(friendOpenId);
        friendShipA.setFriendOpenId(openId);
        friendShipA.setFriendNickName(user.getNickName());
        friendShipA.setFriendImage(user.getAvatarUrl());
        friendShipRedisDao.addFriendShip(friendShipB);
        return true;
    }
}
