package com.rzt.websocket.serverendpoint;

import com.alibaba.fastjson.JSONObject;
import com.rzt.websocket.service.ErJiPushService;
import com.rzt.websocket.service.YiJiPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/serverendpoint/ErJiServerEndpoint/{userId}")
public class ErJiServerEndpoint {
    static RedisTemplate<String, Object> redisTemplate;
    private static ErJiPushService erJiPushService;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        ErJiServerEndpoint.redisTemplate = redisTemplate;
    }

    @Resource
    public void setWuDPushService(ErJiPushService erJiPushService) {
        ErJiServerEndpoint.erJiPushService = erJiPushService;
    }

    /**
     * WebSocket服务器端通过一个线程安全的队列来保持所有客户端的Session
     */
    private static Map<String, HashMap> livingSessions = new ConcurrentHashMap<String, HashMap>();

    /**
     * 建立连接
     *
     * @param userId
     * @param session
     */
    @OnOpen
    public void openSession(@PathParam("userId") String userId, Session session) throws Exception {

        //放人员
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Object userInformation = hashOperations.get("UserInformation", userId);
        HashMap jsonObject = JSONObject.parseObject(userInformation.toString(), HashMap.class);
        jsonObject.put("session", session);
        String sessionId = session.getId();
        livingSessions.put(sessionId, jsonObject);
        //tui - tui - tui
        erJiPushService.module2Method(new HashMap<>(), jsonObject);
        erJiPushService.module3Method(new HashMap<>(), jsonObject);
        erJiPushService.module4_1Method(new HashMap<>(), jsonObject);
        erJiPushService.module4_2Method(new HashMap<>(), jsonObject);
        erJiPushService.module5_1Method(new HashMap<>(), jsonObject);
        erJiPushService.module5_2Method(new HashMap<>(), jsonObject);
        erJiPushService.module7(sessionId);
        erJiPushService.module8(sessionId);
        erJiPushService.module9(sessionId);
        erJiPushService.module10(sessionId);

    }


    public Map<String, HashMap> sendMsg() {
        return livingSessions;
    }

    /**
     * 关闭连接
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();
        livingSessions.remove(sessionId);
    }

    /**
     * 连接发生错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 推送消息
     *
     * @param session
     * @param message
     */
    public void sendText(Session session, Object message) {
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        try {
            String s = JSONObject.toJSONString(message);
            //解决这个异常 java.lang.IllegalStateException: The remote endpoint was in state [TEXT_PARTIAL_WRITING] which is an invalid state for called method
            synchronized (basic) {
                basic.sendText(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
