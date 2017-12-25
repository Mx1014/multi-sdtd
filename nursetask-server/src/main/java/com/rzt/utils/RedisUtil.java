package com.rzt.utils;

import org.aspectj.lang.annotation.Before;
import redis.clients.jedis.Jedis;

/**
 * Created by admin on 2017/12/25.
 */
public class RedisUtil {
    private static Jedis jedis;

    public static Jedis getConnection() {
        //连接redis服务器，192.168.0.100:6379
        jedis = new Jedis("192.168.0.100", 6379);
        //权限认证
        jedis.auth("admin");
        return jedis;
    }
}
