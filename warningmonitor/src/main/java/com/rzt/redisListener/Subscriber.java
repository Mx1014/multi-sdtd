package com.rzt.redisListener;

import com.rzt.service.Monitorcheckejservice;
import com.rzt.service.Monitorcheckyjservice;
import com.rzt.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

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

    @Override
    // 初始化按表达式的方式订阅时候的处理
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("开始监听："+pattern + "=" + subscribedChannels);
    }
    @Override
    // 取得按表达式的方式订阅的消息后的处理
    public void onPMessage(String pattern, String channel, String message) {
        try {
            //System.out.println(pattern + "========================" + channel + "=" + message);
            //以+号分隔
            /*new Thread(new Runnable() {
                @Override
                public void run() {

                }
            }).start();*/
            String[] messages = message.split("\\+");
            if(messages.length<7){
                LOGGER.error("redis信息录入不全");
               // throw new RuntimeException("redis信息录入不全");
            }
            if("TWO".equals(messages[0])){  //表示告警任务生成，插入到二级单位表中
                try{

                    //未按时接任务,要先判断该任务有没有按规定时间开始，所以单独判断
                   /* if("4".equals(messages[3])||"10".equals(messages[3])){ //未按时接任务
                        monitorcheckej.addXSWAS(messages);
                    }else{

                    */
                     monitorcheckej.saveCheckEj(messages);
                     String key = "ONE+"+messages[1]+"+"+messages[2]+"+"+messages[3]+"+"+messages[4]+"+"+messages[5]+"+"+messages[6];
                     redisService.setex(key);
                    //}
                }catch (Exception e){
                    LOGGER.error("插入数据失败："+e.getMessage());
                   // System.out.println("插入数据失败："+e.getMessage());
                }
            }else if("ONE".equals(messages[0])){  //表示告警任务过期 插入到一级单位表中
                /*if("8".equals(messages[3])||"2".equals(messages[3])){
                    //获取到结束时间，将redis中的值设置为结束时间值
                    if(new Date().getTime()<Long.valueOf(messages[8])){
                        return;
                    }
                    String key = "ONE+"+messages[1]+"+"+messages[2]+"+"+messages[3]+"+"+messages[4]+"+"+messages[5]+"+"+messages[6]+"+"+messages[7];
                    redisService.psetex(key,Long.valueOf(messages[8])-new Date().getTime());
                }*/

                monitorcheckyj.saveCheckYj(messages);
            }
        }catch (Exception e){
            //System.out.println(e.getMessage());
        }
    }


}
