package com.u.bops.biz.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
public class RedisDao {

    private JedisPool jedisPool;

    public boolean lpush(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        return jedis.lpush(key, value) > 0;
    }

    public long incr(String key) {
        Jedis jedis = jedisPool.getResource();
        return jedis.incr(key);
    }

    public boolean hset(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.hset(key, field, value);
        return true;
    }

    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        return jedis.lrange(key, start, end);
    }

    public long hincr(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        return jedis.hincrBy(key, field, 1);
    }

    public String hget(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        return jedis.hget(key, field);
    }

    public long lsize(String key) {
        Jedis jedis = jedisPool.getResource();
        return jedis.llen(key);
    }

    /**
     * 获取元素为数字的排序的列表的元素的位置
     *
     * @param key
     * @param target
     * @return
     */
    public long getIndexOfElementOfSortedList(String key, String target) {
        long size = lsize(key);
        int interval = 5;
        long index = size;
        while (true) {
            index = index - interval;
            if (index < 0) {
                index = 0;
            }
            String value = lindex(key, index);
            long com = Long.parseLong(value) - Long.parseLong(target);
            if (com == 0) {
                return index;
            } else if (com < 0) {
                List<String> list = lrange(key, index, index + interval);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equals(target)) {
                        return i + index;
                    }
                }
            } else {
                if (index == 0) {
                    return -1;
                }
                interval = interval * 2;
            }
        }
    }

    private String lindex(String key, long index) {
        Jedis jedis = jedisPool.getResource();
        return jedis.lindex(key, index);
    }

}
