package com.rzt.websocket.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.warningMonitorServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WarningMonitorPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    warningMonitorServerEndpoint warningEndpoint;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public void sendMsgs(Long id) {
        Map<String, HashMap> sendMsg = warningEndpoint.sendMsg();

        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        sendMsg.forEach((sessionId, session) -> {
            Object userId = session.get("userId");

            try {
                String sql = "SELECT * FROM WARNING_ONE_KEY WHERE ID=?  ";
                List<Map<String, Object>> maps = this.execSql(sql, id);
                if(maps.size()>0){
                    Map<String, Object> map = maps.get(0);
                    Object userInformation = hash.get("UserInformation", map.get("USER_ID"));
                    JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
                    if(jsonObject!=null){
                        map.put("DEPT", jsonObject.get("DEPT"));
                        map.put("COMPANYNAME", jsonObject.get("COMPANYNAME"));
                        map.put("REALNAME", jsonObject.get("REALNAME"));
                        map.put("PHONE", jsonObject.get("PHONE"));
                    }

                }

                warningEndpoint.sendText((Session) session.get("session"), maps);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

}
