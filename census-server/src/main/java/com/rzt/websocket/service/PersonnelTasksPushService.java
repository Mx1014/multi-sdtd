package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.PersonnelTasksServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        String xsZxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 2 AND USERDELETE = 1 ";
        /**
         * 巡视离线人员
         */
        String xsLxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 2 AND USERDELETE = 1 ";
        /**
         * 看护在线人员
         */
        String khZxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 ";
        /**
         * 看护离线人员
         */
        String khLxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 ";

        /**
         * 前台稽查在线人员
         */
        String qjcZxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 3 AND USERDELETE = 1 ";
        /**
         * 前台稽查离线人员
         */
        String qjcLxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 3 AND USERDELETE = 1 ";

        /**
         * 后台稽查在线人员
         */
        String hjcZxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 4 AND USERDELETE = 1 ";
        /**
         * 后台稽查离线人员
         */
        String hjcLxUser = " SELECT count(id) FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 4 AND USERDELETE = 1 ";



        /**
         * 正常巡视未开始
         */
        String zcXsWks = "SELECT count(1) " +
                "FROM XS_ZC_TASK " +
                "WHERE STAUTS = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 保电巡视未开始
         */
        String bdXsWks = "SELECT count(1) " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 看护未开始
         */
        String khWks = "SELECT count(1) " +
                "FROM KH_TASK " +
                "WHERE STATUS = '未开始' AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 现场稽查未开始
         */
        String xcJcWks = "SELECT count(1) " +
                "FROM CHECK_LIVE_TASK_DETAIL " +
                "WHERE STATUS = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 正常巡视进行中
         */
        String zcXsJxz = "SELECT count(1) " +
                "FROM XS_ZC_TASK " +
                "WHERE STAUTS = 1 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 保电巡视进行中
         */
        String bdXsJxz = "SELECT count(1) " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 1 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 看护进行中
         */
        String khJxz = "SELECT count(1) " +
                "FROM KH_TASK " +
                "WHERE STATUS = '进行中' AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 现场稽查进行中
         */
        String xcJcJxz = "SELECT count(1) " +
                "FROM CHECK_LIVE_TASK_DETAIL " +
                "WHERE STATUS = 1 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 正常巡视已完成
         */
        String zcXsYwc = "SELECT count(1) " +
                "FROM XS_ZC_TASK " +
                "WHERE STAUTS = 2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 保电巡视已完成
         */
        String bdXsYwc = "SELECT count(1) " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 看护已完成
         */
        String khYwc = "SELECT count(1) " +
                "FROM KH_TASK " +
                "WHERE STATUS = '已完成' AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         *现场稽查已完成
         */
        String xcJcYwc = "SELECT count(1) " +
                "FROM CHECK_LIVE_TASK_DETAIL " +
                "WHERE STATUS = 2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            String sql = "SELECT " +
                    "(" + zxUser + ") as zxUser," +
                    "(" + lxUser + ") as lxUser," +
                    "(" + xsZxUser + ") as xsZxUser," +
                    "(" + xsLxUser + ") as xsLxUser," +
                    "(" + khZxUser + ") as khZxUser," +
                    "(" + khLxUser + ") as khLxUser, " +
                    "(" + zcXsWks + ") as zcXsWks," +
                    "(" + zcXsJxz + ") as zcXsJxz," +
                    "(" + zcXsYwc + ") as zcXsYwc," +
                    "(" + khJxz + ") as khJxz," +
                    "(" + khWks + ") as khWks, " +
                    "(" + khYwc + ") as khYwc," +
                    "(" + xcJcJxz + ") as xcJcJxz," +
                    "(" + xcJcWks + ") as xcJcWks," +
                    "(" + xcJcYwc + ") as xcJcYwc " +
                    " FROM dual";
            try {
                personnelTasksServerEndpoint.sendText((Session) session.get("session"), this.execSql(sql));
            } catch (Exception e) {
                LOGGER.error("Error: The user closes the browser , Session Does Not Exist", e);
            }
        });
    }
}
