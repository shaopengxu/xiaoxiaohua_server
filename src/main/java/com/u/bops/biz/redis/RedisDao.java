package com.u.bops.biz.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * Created by Shaopeng.Xu on 2017-02-06.
 */
@Service
public class RedisDao {

    @Autowired
    private JedisPool jedisPool;

    public boolean lpush(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lpush(key, value) > 0;
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public long incr(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incr(key);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public boolean hset(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        } finally {
            jedisPool.returnResource(jedis);
        }
        return true;
    }

    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrange(key, start, end);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public long hincr(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hincrBy(key, field, 1);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hget(key, field);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

    public long lsize(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.llen(key);
        } finally {
            jedisPool.returnResource(jedis);
        }
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
        try {
            return jedis.lindex(key, index);
        } finally {
            jedisPool.returnResource(jedis);
        }
    }

}
