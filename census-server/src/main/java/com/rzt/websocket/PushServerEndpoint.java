package com.rzt.websocket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sdtd2-task com.rzt.websocket
 * User: liuze
 * Date: 2017-11-29
 * Time: 15:11
 */
@ServerEndpoint("/chat-room/{username}/{orgid}")
public class PushServerEndpoint {
    //WebSocket服务器端通过一个线程安全的队列来保持所有客户端的Session
    private static Map<String, HashMap> livingSessions = new ConcurrentHashMap<String, HashMap>();

    //建立连接
    @OnOpen
    public void openSession(@PathParam("username") String usernamee, @PathParam("orgid") String orgid, Session session) {

        HashMap h = new HashMap();
        String sessionId = session.getId();
        h.put("session", session);
        h.put("username", usernamee);
        h.put("orgid", orgid);
        livingSessions.put(sessionId, h);
    }

    public Map<String, HashMap> sendMsg() {
        return livingSessions;
    }

    //关闭连接
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();
        livingSessions.remove(sessionId);
    }

    //连接发生错误
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    //推送消息
    public void sendText(Session session, String message) {
        RemoteEndpoint.Basic basic = session.getBasicRemote();
        try {
            basic.sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
