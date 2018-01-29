package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.ListDataServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.*;
import java.util.Map;

@Service
public class ListDataPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    ListDataServerEndpoint listDataServerEndpoint;

    public void listData() {
        Map<String, HashMap> stringHashMapMap = listDataServerEndpoint.sendMsg();
//        stringHashMapMap.forEach((sessionId, session) -> {
//            /**
//             * 离线
//             */
//            String offline = "SELECT count(1) as OFFLINES FROM MONITOR_CHECK_EJ  WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2) AND trunc(CREATE_TIME) = trunc(sysdate)";
//            /**
//             *未按时开始任务
//             */
//            String answertime = "SELECT count(1) as ANSWERTIME  FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 4 OR WARNING_TYPE = 10) AND trunc(CREATE_TIME) = trunc(sysdate)";
//            /**
//             * 超期任务
//             */
//            String overdue = " SELECT count(1) as OVERDUE FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 1  AND trunc(CREATE_TIME) = trunc(sysdate) ";
//            /**
//             * 看护人员脱岗
//             */
//            String temporarily = " SELECT count(1) AS TEMPORARILY FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 7 AND trunc(CREATE_TIME) = trunc(sysdate) ";
//            /**
//             * 巡视不合格
//             */
//            String unqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 5 AND trunc(CREATE_TIME) = trunc(sysdate) ";
//            try {
//                List list = new ArrayList();
//                Map map = new HashMap();
//                Map<String, Object> offlineMap = this.execSqlSingleResult(offline);
//                Map<String, Object> answertimeMap = this.execSqlSingleResult(answertime);
//                Map<String, Object> overdueMap = this.execSqlSingleResult(overdue);
//                Map<String, Object> temporarilyMap = this.execSqlSingleResult(temporarily);
//                Map<String, Object> unqualifiedpatrolMap = this.execSqlSingleResult(unqualifiedpatrol);
//                map.put("OFFLINEMAP", offlineMap.get("OFFLINES"));
//                map.put("ANSWERTIMEMAP", answertimeMap.get("ANSWERTIME"));
//                map.put("OVERDUEMAP", overdueMap.get("OVERDUE"));
//                map.put("TEMPORARILYMAP", temporarilyMap.get("TEMPORARILY"));
//                map.put("UNQUALIFIEDPATROLMAP", unqualifiedpatrolMap.get("UNQUALIFIEDPATROL"));
//                list.add(map);
//                listDataServerEndpoint.sendText((Session) session.get("session"), list);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
    }
}
