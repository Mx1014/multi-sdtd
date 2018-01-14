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
public class JedisRunner implements ApplicationListener<ContextRefreshedEvent>/* implements CommandLineRunner*/ {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    private Subscriber subscriber;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //监听所有reids通道中的过期事件
        //subscribe();
        System.out.println("------------------");
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
//    @Async
    private void subscribe() {
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
    }

    /*@Override
    public void run(String... strings) throws Exception {
        Jedis jedis= jedisPool.getResource();
        try {
            //监听所有reids通道中的过期事件
            jedis.psubscribe(subscriber, "__keyevent@1__:expired");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
    }*/

}
