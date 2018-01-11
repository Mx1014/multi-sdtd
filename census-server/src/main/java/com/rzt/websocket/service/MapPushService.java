package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.MapServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MapPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    MapServerEndpoint mapserverendpoint;

    @Scheduled(fixedRate = 3000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = mapserverendpoint.sendMsg();
        String sql = " SELECT deptname,(select count(h.id) from KH_YH_HISTORY h where h.TDYW_ORG=d.deptname) all_count,(select count(h.id) from KH_YH_HISTORY h where h.TDYW_ORG=d.deptname and trunc(h.CREATE_TIME)=trunc(sysdate)) new_count FROM RZTSYSDEPARTMENT d WHERE d.DEPTPID='402881e6603a69b801603a6ab1d70000'  ORDER BY  d.DEPTSORT ";
        List<Map<String, Object>> list = this.execSql(sql);
        sendMsg.forEach((sessionId, session) -> {
            mapserverendpoint.sendText((Session) session.get("session"), list);
        });
    }
}
