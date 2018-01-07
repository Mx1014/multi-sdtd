package com.rzt.redisListener;

import redis.clients.jedis.JedisPubSub;

/**
 * Created by huyuening on 2018/1/5.
 */
public class Subscriber extends JedisPubSub {

    @Override
    // 初始化按表达式的方式订阅时候的处理
    public void onPSubscribe(String pattern, int subscribedChannels) {
        System.out.println("Subscribe-onPSubscribe>>>>>>>>>>>>>>>>>>>>>>>>"+pattern + "=" + subscribedChannels);
    }
    @Override
    // 取得按表达式的方式订阅的消息后的处理
    public void onPMessage(String pattern, String channel, String message) {
        try {
            System.out.println("===========实效=======");
            System.out.println(pattern + "=" + channel + "=" + message);
            /*if("__keyevent@0__:expired".equals(channel)){
                System.out.println(message+"----键实效了");
            }*/
            //在这里写相关的逻辑代码
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
