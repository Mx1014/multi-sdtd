package com.rzt.redisListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/**
 * Created by huyuening on 2018/1/5.
 */
@Component
public class JedisRunner implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    private Subscriber subscriber;

    private static Integer count=1;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
            count++;
            if (count==3){
                System.out.println("-----------------");
                //监听所有reids通道中的过期事件
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Jedis jedis= jedisPool.getResource();
                        try {
                            System.out.println("============");
                            jedis.psubscribe(subscriber , "__keyevent@1__:expired");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            jedis.close();
                        }
                    }
                });
                t.start();
            }

    }



}
