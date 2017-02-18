package com.u.bops.biz.redis;

import com.u.bops.biz.domain.FriendShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
@Service
public class FriendShipRedisDao {

    @Autowired
    private RedisDao redisDao;

    public static final String FRIENDSHIP = "FRIENDSHIP";

    public boolean addFriendShip(String openId, String friendOpenId) {
        String key = FRIENDSHIP + "_" + openId;
        List<String> friends = redisDao.lrange(key, 0, -1);
        if (!friends.contains(friendOpenId)) {
            redisDao.rpush(key, friendOpenId);
        }
        key = FRIENDSHIP + "_" + friendOpenId;
        friends = redisDao.lrange(key, 0, -1);
        if (!friends.contains(openId)) {
            redisDao.rpush(key, openId);
        }
        return true;
    }

    public boolean existsFriendShip(String openId, String friendOpenId) {
        String key = FRIENDSHIP + "_" + openId;
        List<String> friends = redisDao.lrange(key, 0, -1);
        return friends.contains(friendOpenId);
    }

    public List<String> getFriendOpenIds(String openId) {
        String key = FRIENDSHIP + "_" + openId;
        return redisDao.lrange(key, 0, -1);
    }

    public boolean removeFriendShip(String openId, String friendOpenId) {
        String key = FRIENDSHIP + "_" + openId;
        return redisDao.lrem(key, 1, friendOpenId);
    }

    /**
     * 返回随机头像的本地存储地址
     * @return
     */
    public List<String> getRandomImages(){
        //TODO
        return new ArrayList<>();
    }
}
