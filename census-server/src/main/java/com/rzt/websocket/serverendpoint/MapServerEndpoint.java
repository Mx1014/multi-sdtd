package com.rzt.websocket.serverendpoint;

import com.alibaba.fastjson.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/serverendpoint/Map/{type}")
public class MapServerEndpoint {
    /**
     * WebSocket服务器端通过一个线程安全的队列来保持所有客户端的Session
     */
    private static Map<String, HashMap> livingSessions = new ConcurrentHashMap<String, HashMap>();

    /**
     * 建立连接
     *
     * @param usernamee
     * @param orgid
     * @param session
     */
    @OnOpen
    public void openSession(@PathParam("type") String type, @PathParam("username") String usernamee, @PathParam("orgid") String orgid, Session session) {

        HashMap h = new HashMap();
        String sessionId = session.getId();
        h.put("session", session);
        h.put("type", type);
//        h.put("username", usernamee);
//        h.put("orgid", orgid);
        livingSessions.put(sessionId, h);
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
    public void sendText(Session session, List<Map<String, Object>> message) {
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        try {
            String s = JSONObject.toJSONString(message);
            basic.sendText(s);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
