package com.rzt.redisServer;


import com.rzt.activiti.service.impl.DefectServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;


@Component
public class Subscriber extends JedisPubSub {
    protected static Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);
    @Autowired
    private DefectServiceImpl defectService;


    @Override
    // 初始化按表达式的方式订阅时候的处理
    public void onPSubscribe(String pattern, int subscribedChannels) {
        LOGGER.info("redis事件监听   defect 流程    开始监听："+pattern + "=" + subscribedChannels);
    }
    @Override
    // 取得按表达式的方式订阅的消息后的处理   message就是回调时的键
    public void onPMessage(String pattern, String channel, String message) {
        try {

            //   拿到当前流程id   此时当前流程id 以放置超过24小时   直接取消该流程
            //System.out.println("message       "+ message);
            String[] split = message.split(",");
            if("defect".equals(split[2])){
                Map<String, Object> map = new HashMap<>();
                map.put("flag","0");
                map.put("qxId",split[1]);
                //进入公司本部通报步骤   启动公司本部节点监听
                defectService.complete(split[0],map);
            }
        }catch (Exception e){
            LOGGER.error("redis事件监听   defect 流程失败     "+e.getMessage());
        }
    }
}
