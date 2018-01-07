package com.rzt.redisListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * Created by huyuening on 2018/1/5.
 */
@Component
public class JedisRunner implements CommandLineRunner {

    @Autowired
    JedisPool jedisPool;

    @Override
    public void run(String... strings) throws Exception {
        Jedis jedis= jedisPool.getResource();
        try {
            //监听所有reids通道中的过期事件
            jedis.psubscribe(new Subscriber(), "__keyevent@5__:expired");
        } catch (Exception e) {
            jedis.close();
            e.printStackTrace();
        }finally {
            jedis.close();
        }
    }

}
