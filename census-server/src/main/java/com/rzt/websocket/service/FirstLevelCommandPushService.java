package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.FirstLevelCommandServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirstLevelCommandPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    FirstLevelCommandServerEndpoint firstLevelCommandServerEndpoint;

    public void adminModule1() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            List list = new ArrayList();
            firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), list);
        });
    }

    public void adminModule2() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {

            firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), "1");
        });
    }
}
