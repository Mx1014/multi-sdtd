package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.PersonnelTasksServerEndpoint;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.websocket
 * @Author: liuze
 * @date: 2017-12-5 16:26
 * 任务人员统计service
 */
@Service
public class PersonnelTasksPushService extends CurdService<websocket, websocketRepository> {

    private static Logger LOGGER = LoggerFactory.getLogger(AlarmSituationPushService.class);

    @Autowired
    PersonnelTasksServerEndpoint personnelTasksServerEndpoint;

    /**
     * 定时查询数据推送消息
     * The WebSocket session [0] has been closed
     * and no method (apart from close()) may be called on a closed session
     */
    @Scheduled(fixedRate = 1000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = personnelTasksServerEndpoint.sendMsg();
        /**
         * zxUser 在线人员
         */
        String zxUser = "SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND USERDELETE = 1";
        /**
         * lxUser 离线人员
         */
        String lxUser = "SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND USERDELETE = 1";
        /**
         * 巡视在线人员
         */
        String xsZxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 ";
        /**
         * 巡视离线人员
         */
        String xsLxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 ";
        /**
         * 看护在线人员
         */
        String khZxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 0 AND USERDELETE = 1 ";
        /**
         * 看护离线人员
         */
        String khLxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 0 AND USERDELETE = 1 ";
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            String sql = "SELECT " +
                    "(" + zxUser + ") as zxUser," +
                    "(" + lxUser + ") as lxUser," +
                    "(" + xsZxUser + ") as xsZxUser," +
                    "(" + xsLxUser + ") as xsLxUser," +
                    "(" + khZxUser + ") as khZxUser," +
                    "(" + khLxUser + ") as khLxUser " +
                    " FROM dual";
            try {
                personnelTasksServerEndpoint.sendText((Session) session.get("session"), this.execSql(sql));
            } catch (Exception e) {
                LOGGER.error("Error: The user closes the browser , Session Does Not Exist", e);
            }
        });
    }
}
