package com.rzt.redisListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by huyuening on 2018/1/5.
 */
public class Subscriber extends JedisPubSub {
    protected static Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);
   /* @Autowired
    private Monitorcheckejservice monitorcheckej;

    @Autowired
    JedisPool jedisPool;

    @Override
    // 初始化按表达式的方式订阅时候的处理
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("开始监听："+pattern + "=" + subscribedChannels);
    }
    @Override
    // 取得按表达式的方式订阅的消息后的处理
    public void onPMessage(String pattern, String channel, String message) {
        try {
            System.out.println(pattern + "=" + channel + "=" + message);
            //以+号分隔
            String[] messages = message.split("\\+");
            if("TWO".equals(messages[0])){  //表示告警任务生成，插入到二级单位表中
                try{
                    monitorcheckej.saveCheckEj(messages);

                }catch (Exception e){
                    LOGGER.error("插入数据失败："+e.getMessage());
                    System.out.println("插入数据失败："+e.getMessage());
                }
            }else if("ONE".equals(messages[0])){  //表示告警任务过期 插入到一级单位表中，并更新二级状态

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

}
