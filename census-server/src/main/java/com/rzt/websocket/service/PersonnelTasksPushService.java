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
//    @Scheduled(fixedRate = 3000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = personnelTasksServerEndpoint.sendMsg();
        /**
         * zxUser 在线人员
         */
//        String zxUser = "SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND USERDELETE = 1  AND USERTYPE=0 ";
        /**
         * lxUser 离线人员
         */
//        String lxUser = "SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND USERDELETE = 1  AND USERTYPE=0 ";
        /**
         * 巡视在线人员
         */
        String xsZxUser = " SELECT count(1) " +
                "FROM (SELECT z.CM_USER_ID " +
                "      FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                "      WHERE LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate " +
                "      GROUP BY z.CM_USER_ID) ";
        /**
         * 巡视离线人员
         */
        String xsLxUser = " SELECT count(1) FROM (SELECT z.CM_USER_ID " +
                "  FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                "  WHERE LOGINSTATUS = 0 AND USERDELETE = 1  AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " +
                "  GROUP BY z.CM_USER_ID) ";
        /**
         * 看护在线人员
         */
        String khZxUser = " SELECT count(1)FROM (SELECT count(u.ID) " +
                "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                "WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " +
                "GROUP BY k.USER_ID) ";
        /**
         * 看护离线人员
         */
        String khLxUser = " SELECT count(1)FROM (SELECT count(u.ID) " +
                "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                "WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " +
                "GROUP BY k.USER_ID) ";

        /**
         * 前台稽查在线人员
         */
        String qjcZxUser = " SELECT count(1) FROM (SELECT " +
                "    count(1) " +
                "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                "  WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND sysdate BETWEEN PLAN_START_TIME AND PLAN_END_TIME GROUP BY k.USER_ID) ";
        /**
         * 前台稽查离线人员
         */
        String qjcLxUser = " SELECT count(1) FROM (SELECT " +
                "    count(1) " +
                "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                "  WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND sysdate BETWEEN PLAN_START_TIME AND PLAN_END_TIME GROUP BY k.USER_ID) ";

        /**
         * 后台稽查在线人员
         */
        String hjcZxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 ";
        /**
         * 后台稽查离线人员
         */
        String hjcLxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 ";


        /**
         * 正常巡视未开始
         */
        String zcXsWks = "SELECT count(1)  " +
                "FROM XS_ZC_TASK " +
                "WHERE STAUTS = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 保电巡视未开始
         */
        String bdXsWks = "SELECT count(1)  " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 看护未开始
         */
        String khWks = "SELECT count(1)  " +
                "FROM KH_TASK " +
                "WHERE STATUS = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 现场稽查未开始
         */
        String xcJcWks = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK " +
                "WHERE STATUS = 0 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 正常巡视进行中
         */
        String zcXsJxz = "SELECT count(1)  " +
                "FROM XS_ZC_TASK " +
                "WHERE STAUTS = 1 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 保电巡视进行中
         */
        String bdXsJxz = "SELECT count(1)  " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 1 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 看护进行中
         */
        String khJxz = "SELECT count(1)  " +
                "FROM KH_TASK " +
                "WHERE STATUS = 1 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 现场稽查进行中
         */
        String xcJcJxz = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK " +
                "WHERE STATUS = 1 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 正常巡视已完成
         */
        String zcXsYwc = "SELECT count(1)  " +
                "FROM XS_ZC_TASK " +
                "WHERE STAUTS = 2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 保电巡视已完成
         */
        String bdXsYwc = "SELECT count(1)  " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 看护已完成
         */
        String khYwc = "SELECT count(1)  " +
                "FROM KH_TASK " +
                "WHERE STATUS = 2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         *现场稽查已完成
         */
        String xcJcYwc = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK " +
                "WHERE STATUS = 2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 今日治理隐患
         */
        String handlesql = "SELECT COUNT(*)  FROM KH_YH_HISTORY WHERE trunc(YHXQ_TIME) =trunc(sysdate)";
        /**
         * 今日新增隐患
         */
        String addedsql = "SELECT COUNT(*)  FROM KH_YH_HISTORY WHERE trunc(create_time) =trunc(sysdate)";
        /**
         * 今日调整隐患
         */
        String updateSql = "SELECT COUNT(*)  FROM KH_YH_HISTORY WHERE trunc(update_time) =trunc(sysdate)";
        /**
         * 隐患总数
         */
        String allSql = "select count(*)  from kh_yh_history where yhzt=0";
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            String sql = "SELECT " +
//                    "(" + zxUser + ") as zxUser," +
//                    "(" + lxUser + ") as lxUser," +
                    "(" + xsZxUser + ") as xsZxUser," +
                    "(" + xsLxUser + ") as xsLxUser," +
                    "(" + khZxUser + ") as khZxUser," +
                    "(" + khLxUser + ") as khLxUser, " +
                    "(" + zcXsWks + ")+(" + bdXsWks + ") as XsWks," +
                    "(" + zcXsJxz + ")+(" + bdXsJxz + ") as XsJxz," +
                    "(" + zcXsYwc + ")+(" + bdXsYwc + ") as XsYwc," +
                    "(" + khJxz + ") as khJxz," +
                    "(" + khWks + ") as khWks, " +
                    "(" + khYwc + ") as khYwc," +
                    "(" + xcJcJxz + ") as xcJcJxz," +
                    "(" + xcJcWks + ") as xcJcWks," +
                    "(" + xcJcYwc + ") as xcJcYwc," +
                    "(" + qjcZxUser + ") as qjcZxUser," +
                    "(" + qjcLxUser + ") as qjcLxUser," +
                    "(" + hjcZxUser + ") as hjcZxUser," +
                    "(" + hjcLxUser + ") as hjcLxUser, " +
                    "(" + handlesql + ") as handlesql," +
                    "(" + addedsql + ") as addedsql," +
                    "(" + updateSql + ") as updateSql," +
                    "(" + allSql + ") as ALLSQL " +
                    " FROM dual";
            personnelTasksServerEndpoint.sendText((Session) session.get("session"), this.execSql(sql));
        });
    }
}
