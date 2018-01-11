package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.historyServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoryPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    historyServerEndpoint historyServerEndpoint;

    @Scheduled(fixedRate = 3000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = historyServerEndpoint.sendMsg();
        String sql = " select  " +
                "(select count(h.id) from KH_YH_HISTORY h where yhjb1='施工隐患') sg, " +
                "(select count(h.id) from KH_YH_HISTORY h where yhjb1='建筑隐患') jz, " +
                "(select count(h.id) from KH_YH_HISTORY h where yhjb1='异物隐患') yw, " +
                "(select count(h.id) from KH_YH_HISTORY h where yhjb1='树木隐患') sm from dual  ";
//        String sql1 = "select  " +
//                "(select count(h.id) from KH_YH_HISTORY h where trunc(h.create_time)=trunc(sysdate)) sbyh, " +
//                "(select count(h.id) from KH_YH_HISTORY h where YHZT=1) yxc, " +
//                "(select count(h.id) from KH_YH_HISTORY h) zs from dual ";
        String sql1 = "SELECT " +
                "  (SELECT count(h.id) " +
                "   FROM KH_YH_HISTORY h " +
                "   WHERE trunc(CREATE_TIME) = trunc(sysdate)) sbyh, " +
                "  (SELECT count(*) " +
                "   FROM KH_YH_HISTORY h " +
                "   WHERE (yhjb1 = '施工隐患' OR yhjb1 = '建筑隐患' OR yhjb1 = '异物隐患' OR " +
                "         yhjb1 = '树木隐患') AND UPDATE_TIME IS NOT NULL AND   YHXQ_TIME IS NULL AND trunc(UPDATE_TIME) = trunc(sysdate) " +
                "  )                                           tzyh, " +
                "  (SELECT count(*) " +
                "   FROM KH_YH_HISTORY h " +
                "   WHERE (yhjb1 = '施工隐患' OR yhjb1 = '建筑隐患' OR yhjb1 = '异物隐患' OR " +
                "         yhjb1 = '树木隐患') AND YHXQ_TIME IS NOT NULL AND trunc(YHXQ_TIME) = trunc(sysdate) " +
                "  )                                           zlyh " +
                "FROM dual";
        sendMsg.forEach((sessionId, session) -> {
            Map map = new HashMap();
            map.put("zhu", this.execSql(sql));
            map.put("bing", this.execSql(sql1));
            historyServerEndpoint.sendText((Session) session.get("session"), map);
        });
    }

}
