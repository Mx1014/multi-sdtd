package com.rzt.websocket.serverendpoint;

import com.alibaba.fastjson.JSONObject;
import com.rzt.websocket.service.FirstLevelCommandPushService;
import com.rzt.websocket.service.TwoLevelCommandPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/serverendpoint/twolevelcommand/{currentUserId}/{mapType}/{type}/{tableType}")
public class TwoLevelCommandServerEndpoint {
    static RedisTemplate<String, Object> redisTemplate;
    private static TwoLevelCommandPushService twoLevelCommandPushService;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        TwoLevelCommandServerEndpoint.redisTemplate = redisTemplate;
    }

    @Resource
    public void setWuDPushService(TwoLevelCommandPushService wuDPushService) {
        TwoLevelCommandServerEndpoint.twoLevelCommandPushService = wuDPushService;
    }

    /**
     * WebSocket服务器端通过一个线程安全的队列来保持所有客户端的Session
     */
    private static Map<String, HashMap> livingSessions = new ConcurrentHashMap<String, HashMap>();

    /**
     * 建立连接
     *
     * @param session
     */
    @OnOpen
    public void openSession(@PathParam("currentUserId") String currentUserId, @PathParam("mapType") String mapType, @PathParam("type") String type, @PathParam("tableType") String tableType, Session session) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        HashMap<String, Object> h = new HashMap();
        String sessionId = session.getId();
        h.put("session", session);
        h.put("DEPTID", jsonObject.get("DEPTID"));
        h.put("DEPT", jsonObject.get("DEPT"));
        h.put("mapType", mapType);
        h.put("type", type);
        h.put("tableType", tableType);
        livingSessions.put(sessionId, h);
        twoLevelCommandPushService.adminModule1();
        twoLevelCommandPushService.adminModule2();
        twoLevelCommandPushService.adminModule3();
        twoLevelCommandPushService.adminModule4();
        twoLevelCommandPushService.adminModule5();
        twoLevelCommandPushService.adminModule6();
        twoLevelCommandPushService.adminModule7();
        twoLevelCommandPushService.adminModule8();
        twoLevelCommandPushService.adminModule20();
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
        System.out.println("发生错误");
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
            synchronized (basic) {
                basic.sendText(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
