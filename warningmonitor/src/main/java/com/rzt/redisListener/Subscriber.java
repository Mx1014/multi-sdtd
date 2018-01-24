package com.rzt.redisListener;

import com.rzt.eureka.StaffLine;
import com.rzt.service.Monitorcheckejservice;
import com.rzt.service.Monitorcheckyjservice;
import com.rzt.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huyuening on 2018/1/5.
 */
@Component
public class Subscriber extends JedisPubSub {
    protected static Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);

    @Autowired
    private Monitorcheckejservice monitorcheckej;
    @Autowired
    private Monitorcheckyjservice monitorcheckyj;

    @Autowired
    private RedisService redisService;

    @Autowired
    private StaffLine staffLine;



    @Override
    // 初始化按表达式的方式订阅时候的处理
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("开始监听："+pattern + "=" + subscribedChannels);
    }
    @Override
    // 取得按表达式的方式订阅的消息后的处理
    public void onPMessage(String pattern, String channel, String message) {
        try {

            String[] messages = message.split("\\+");
            if(messages.length<7){
                LOGGER.error("redis信息录入不全");
               // throw new RuntimeException("redis信息录入不全");
            }
            if("TWO".equals(messages[0])){  //表示告警任务生成，插入到二级单位表中
                try{
                    monitorcheckej.saveCheckEj(messages);
                    String key = "ONE+"+messages[1]+"+"+messages[2]+"+"+messages[3]+"+"+messages[4]+"+"+messages[5]+"+"+messages[6];
                    redisService.setex(key);
                }catch (Exception e){
                    LOGGER.error("插入数据失败："+e.getMessage());
                }
            }else if("ONE".equals(messages[0])){  //表示告警任务过期 插入到一级单位表中

                monitorcheckyj.saveCheckYj(messages);

            }

        }catch (Exception e){
            //System.out.println(e.getMessage());
        }

    }


}
