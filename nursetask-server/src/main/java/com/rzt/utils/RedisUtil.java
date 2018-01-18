package com.rzt.utils;

import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by admin on 2017/12/25.
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static Jedis jedis;

    public static Jedis getConnection() {
        //连接redis服务器，192.168.0.100:6379
        jedis = new Jedis("192.168.0.100", 6379);
        //权限认证
        jedis.auth("admin");
        return jedis;
    }

    public  void removeSomeKey(Long id) {
        String s = "TWO+" + id + "+2+10*";
        RedisConnection connection = null;
        try {
            connection = redisTemplate.getConnectionFactory().getConnection();
            connection.select(1);
            Set<byte[]> keys = connection.keys(s.getBytes());
            byte[][] ts = keys.toArray(new byte[][]{});
            if (ts.length > 0) {
                connection.del(ts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

}
