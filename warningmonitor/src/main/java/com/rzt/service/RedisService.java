package com.rzt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by huyuening on 2018/1/9.
 */
@Service
public class RedisService {
    protected static Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    private static int faixTime = 20;


    @Autowired
    JedisPool jedisPool;

    public void setex(String key){
        Jedis jedis = jedisPool.getResource();
        jedis.select(1);
        try {
            jedis.setex(key, faixTime, " ");
            //System.out.println("---setex-------------------------------------------"+key+faixTime);
        } catch (Exception e) {
            LOGGER.error("redis定时失败："+e.getMessage());
           // System.out.println("redis定时失败："+e.getMessage());
        }finally {
            jedis.close();
        }
    }

    public void psetex(String key,Long time){
        Jedis jedis = jedisPool.getResource();
        jedis.select(1);
        try {
            jedis.psetex(key, time, " ");
           // System.out.println("--psetex--------------------------------------------"+key+faixTime);
        } catch (Exception e) {
            LOGGER.error("redis定时失败："+e.getMessage());
           // System.out.println("redis定时失败："+e.getMessage());
        }finally {
            jedis.close();
        }
    }

    public void delKey(String key){
        Jedis jedis = jedisPool.getResource();
        jedis.select(1);
        try {
           jedis.del(key);
        } catch (Exception e) {
            LOGGER.error("redis删除键值失败："+e.getMessage());
        }finally {
            jedis.close();
        }
    }

    public void setFaixTime(int faixTime1){
        faixTime = faixTime1;
    }

}
