package com.rzt.websocket.service;

import com.rzt.websocket.serverendpoint.FirstLevelCommandServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirstLevelCommandPushService {
    @Autowired
    FirstLevelCommandServerEndpoint firstLevelCommandServerEndpoint;

    public void adminModule1() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            List list = new ArrayList();
            firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), list);
        });
    }
}
