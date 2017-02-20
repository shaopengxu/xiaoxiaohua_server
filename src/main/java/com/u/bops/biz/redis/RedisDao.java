package com.u.bops.biz.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

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
            jedis.close();
        }
    }

    public boolean rpush(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.rpush(key, value) > 0;
        } finally {
            jedis.close();
        }
    }

    public boolean lrem(String key, int count, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrem(key, count, value) > 0;
        } finally {
            jedis.close();
        }
    }

    public long incr(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incr(key);
        } finally {
            jedis.close();
        }
    }

    public boolean hset(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        } finally {
            jedis.close();
        }
        return true;
    }

    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrange(key, start, end);
        } finally {
            jedis.close();
        }
    }


    public long hincr(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hincrBy(key, field, 1);
        } finally {
            jedis.close();
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hget(key, field);
        } finally {
            jedis.close();
        }
    }

    public Map<String, String> hgetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hgetAll(key);
        } finally {
            jedis.close();
        }
    }

    public long lsize(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.llen(key);
        } finally {
            jedis.close();
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

    public String lindex(String key, long index) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lindex(key, index);
        } finally {
            jedis.close();
        }
    }

    public void del(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    public void hdel(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hdel(key, field);
        } finally {
            jedis.close();
        }
    }



}
