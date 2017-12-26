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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @Scheduled(fixedRate = 3000)
    public void sendMsgs() {
        Map<String, HashMap> sendMsg = personnelTasksServerEndpoint.sendMsg();
        /**
         * zxUser 在线人员
         */
        String zxUser = "SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND USERDELETE = 1";
        /**
         * lxUser 离线人员
         */
        String lxUser = "SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND USERDELETE = 1";
        /**
         * 巡视在线人员
         */
        String xsZxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 2 AND USERDELETE = 1 ";
        /**
         * 巡视离线人员
         */
        String xsLxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 2 AND USERDELETE = 1 ";
        /**
         * 看护在线人员
         */
        String khZxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 ";
        /**
         * 看护离线人员
         */
        String khLxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 ";

        /**
         * 前台稽查在线人员
         */
        String qjcZxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 3 AND USERDELETE = 1 ";
        /**
         * 前台稽查离线人员
         */
        String qjcLxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 3 AND USERDELETE = 1 ";

        /**
         * 后台稽查在线人员
         */
        String hjcZxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 4 AND USERDELETE = 1 ";
        /**
         * 后台稽查离线人员
         */
        String hjcLxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 4 AND USERDELETE = 1 ";


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
                "WHERE STATUS = '未开始' AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 现场稽查未开始
         */
        String xcJcWks = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK_DETAIL " +
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
                "WHERE STATUS = '进行中' AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         * 现场稽查进行中
         */
        String xcJcJxz = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK_DETAIL " +
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
                "WHERE STATUS = '已完成' AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        /**
         *现场稽查已完成
         */
        String xcJcYwc = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK_DETAIL " +
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
                    "(" + zxUser + ") as zxUser," +
                    "(" + lxUser + ") as lxUser," +
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
//            try {
                /*List<Map<String, Object>> list = new ArrayList();
                Map map = new HashMap();
                Map<String, Object> stringObjectMap = execSqlSingleResult(zxUser);
                map.put("ZXUSER", stringObjectMap.get("COUNT"));
                Map<String, Object> stringObjectMap1 = execSqlSingleResult(lxUser);
                map.put("LXUSER", stringObjectMap1.get("COUNT"));
                Map<String, Object> stringObjectMap2 = execSqlSingleResult(xsZxUser);
                map.put("XSZXUSER", stringObjectMap2.get("COUNT"));
                Map<String, Object> stringObjectMap3 = execSqlSingleResult(xsLxUser);
                map.put("XSLXUSER", stringObjectMap3.get("COUNT"));
                Map<String, Object> stringObjectMap4 = execSqlSingleResult(khZxUser);
                map.put("KHZXUSER", stringObjectMap4.get("COUNT"));
                Map<String, Object> stringObjectMap5 = execSqlSingleResult(khLxUser);
                map.put("KHLXUSER", stringObjectMap5.get("COUNT"));
                Map<String, Object> stringObjectMap6 = execSqlSingleResult(zcXsWks);
                map.put("ZCXSWKS", stringObjectMap6.get("COUNT"));
                Map<String, Object> stringObjectMap7 = execSqlSingleResult(bdXsWks);
                map.put("XSWKS", Integer.valueOf(String.valueOf(stringObjectMap6)) + Integer.valueOf(String.valueOf(stringObjectMap7)));
                Map<String, Object> stringObjectMap8 = execSqlSingleResult(zcXsJxz);
                Map<String, Object> stringObjectMap9 = execSqlSingleResult(bdXsJxz);
                map.put("XSJXZ", Integer.valueOf(String.valueOf(stringObjectMap8)) + Integer.valueOf(String.valueOf(stringObjectMap9)));
                Map<String, Object> stringObjectMap10 = execSqlSingleResult(zcXsYwc);
                Map<String, Object> stringObjectMap11 = execSqlSingleResult(bdXsYwc);
                map.put("XSYWC", Integer.valueOf(String.valueOf(stringObjectMap10)) + Integer.valueOf(String.valueOf(stringObjectMap11)));
                Map<String, Object> stringObjectMap12 = execSqlSingleResult(khJxz);
                map.put("KHJXZ", stringObjectMap12);
                Map<String, Object> stringObjectMap13 = execSqlSingleResult(khWks);
                map.put("KHWKS", stringObjectMap13);
                Map<String, Object> stringObjectMap14 = execSqlSingleResult(khYwc);
                map.put("KHYWC", stringObjectMap14);
                Map<String, Object> stringObjectMap15 = execSqlSingleResult(xcJcJxz);
                map.put("XCJCJXZ", stringObjectMap15);
                Map<String, Object> stringObjectMap16 = execSqlSingleResult(xcJcWks);
                map.put("XCJCWKS", stringObjectMap16);
                Map<String, Object> stringObjectMap17 = execSqlSingleResult(xcJcYwc);
                map.put("XCJCYWC", stringObjectMap17);
                Map<String, Object> stringObjectMap18 = execSqlSingleResult(qjcZxUser);
                map.put("QJCZXUSER", stringObjectMap18);
                Map<String, Object> stringObjectMap19 = execSqlSingleResult(qjcLxUser);
                map.put("QJCLXUSER", stringObjectMap19);
                Map<String, Object> stringObjectMap20 = execSqlSingleResult(hjcZxUser);
                map.put("HJCZXUSER", stringObjectMap20);
                Map<String, Object> stringObjectMap21 = execSqlSingleResult(hjcLxUser);
                map.put("HJCLXUSER", stringObjectMap21);
                Map<String, Object> stringObjectMap22 = execSqlSingleResult(handlesql);
                map.put("HANDLESQL", stringObjectMap22);
                Map<String, Object> stringObjectMap23 = execSqlSingleResult(updateSql);
                map.put("UPDATESQL", stringObjectMap23);
                Map<String, Object> stringObjectMap24 = execSqlSingleResult(addedsql);
                map.put("ADDEDSQL", stringObjectMap24);
                Map<String, Object> stringObjectMap25 = execSqlSingleResult(allSql);
                map.put("ALLSQL", stringObjectMap25);*/

//            } catch (Exception e) {
//                LOGGER.error("Error: The user closes the browser , Session Does Not Exist", e);
//            }
        });
    }
}
