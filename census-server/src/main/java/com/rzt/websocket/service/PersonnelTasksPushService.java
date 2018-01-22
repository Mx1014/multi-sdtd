package com.rzt.websocket.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.PersonnelTasksServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
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
        //遍历Map取出通道单位id用于数据库查询权限
        sendMsg.forEach((sessionId, session) -> {
            HashOperations hashOperations = redisTemplate.opsForHash();
            String s1 = "";
            String s2 = "";
            String s3 = "";
            String s4 = "";
            String s5 = "";
            String s6 = "";
            String s7 = "";
            String s8 = "";
            String s9 = "";
            String s10 = "";
            String s11 = "";
            String s12 = "";
            String s13 = "";
            String s14 = "";
            String s15 = "";
            String s16 = "";
            String s17 = "";
            String s18 = "";
            String s19 = "";
            String s20 = "";
            String s21 = "";
            String s22 = "";
            String s23 = "";
            String s24 = "";
            String userId = String.valueOf(session.get("userId"));
            Object userInformation = hashOperations.get("UserInformation", userId);
            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(userInformation));
            Integer roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
            List list = new ArrayList();
            if (roletype == 1 || roletype == 2) {
                String DEPTID = jsonObject.get("DEPTID").toString();
                list.add(DEPTID);
                s1 += " AND r.DEPTID = ?" + list.size();
                list.add(DEPTID);
                s2 += " AND r.DEPTID = ?" + list.size();
                list.add(DEPTID);
                s3 += " AND u.DEPTID = ?" + list.size();
                list.add(DEPTID);
                s4 += " AND u.DEPTID = ?" + list.size();
                list.add(DEPTID);
                s5 += " AND u.DEPTID = ?" + list.size();
                list.add(DEPTID);
                s6 += " AND u.DEPTID = ?" + list.size();
                list.add(DEPTID);
                s7 += " AND DEPTID=?" + list.size();
                list.add(DEPTID);
                s8 += " AND DEPTID=?" + list.size();
                list.add(DEPTID);
                s9 += " AND TD_ORG = ?" + list.size();
                list.add(DEPTID);
                s10 += " AND TD_ORG = ?" + list.size();
                list.add(DEPTID);
                s11 += " AND d.ID=?" + list.size();
                list.add(DEPTID);
                s12 += " AND u.DEPTID=?" + list.size();
                list.add(DEPTID);
                s13 += " AND TD_ORG=?" + list.size();
                list.add(DEPTID);
                s14 += " AND TD_ORG=?" + list.size();
                list.add(DEPTID);
                s15 += " AND r.DEPTID = ?" + list.size();
                list.add(DEPTID);
                s16 += " AND u.DEPTID=?" + list.size();
                list.add(DEPTID);
                s17 += " AND TD_ORG = ?" + list.size();
                list.add(DEPTID);
                s18 += " AND TD_ORG = ?" + list.size();
                list.add(DEPTID);
                s19 += " AND u.ID=?" + list.size();
                list.add(DEPTID);
                s20 += " AND u.ID=?" + list.size();
                list.add(DEPTID);
                s21 += " AND YWORG_ID =?" + list.size();
                list.add(DEPTID);
                s22 += " AND YWORG_ID =?" + list.size();
                list.add(DEPTID);
                s23 += " AND YWORG_ID =?" + list.size();
                list.add(DEPTID);
                s24 += " AND YWORG_ID =?" + list.size();
            }
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
                    "      WHERE LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate " + s1 +
                    "      GROUP BY z.CM_USER_ID) ";
            /**
             * 巡视离线人员
             */
            String xsLxUser = " SELECT count(1) FROM (SELECT z.CM_USER_ID " +
                    "  FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                    "  WHERE LOGINSTATUS = 0 AND USERDELETE = 1  AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " + s2 +
                    "  GROUP BY z.CM_USER_ID) ";
            /**
             * 看护在线人员
             */
            String khZxUser = " SELECT count(1)FROM (SELECT count(u.ID) " +
                    " FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                    " WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " + s3 +
                    " GROUP BY k.USER_ID) ";
            /**
             * 看护离线人员
             */
            String khLxUser = " SELECT count(1)FROM (SELECT count(u.ID) " +
                    " FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                    " WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " + s4 +
                    " GROUP BY k.USER_ID) ";

            /**
             * 前台稽查在线人员
             */
            String qjcZxUser = " SELECT count(1) FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND sysdate BETWEEN PLAN_START_TIME AND PLAN_END_TIME " + s5 + " GROUP BY k.USER_ID) ";
            /**
             * 前台稽查离线人员
             */
            String qjcLxUser = " SELECT count(1) FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND sysdate BETWEEN PLAN_START_TIME AND PLAN_END_TIME " + s6 + " GROUP BY k.USER_ID) ";

            /**
             * 后台稽查在线人员
             */
            String hjcZxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 " + s7;
            /**
             * 后台稽查离线人员
             */
            String hjcLxUser = " SELECT count(id)  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 " + s8;


            /**
             * 正常巡视未开始
             */
            String zcXsWks = "SELECT count(1) " +
                    " FROM XS_ZC_TASK " +
                    " WHERE STAUTS = 0 AND PLAN_END_TIME >= trunc( SYSDATE ) AND PLAN_START_TIME <= trunc(sysdate+1) " + s9;
            /**
             * 保电巡视未开始
             */
            String bdXsWks = " SELECT count(1) " +
                    " FROM XS_TXBD_TASK " +
                    " WHERE STAUTS = 0 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1)" + s10;
            /**
             * 看护未开始
             */
            String khWks = " SELECT count(1) " +
                    " FROM KH_TASK k LEFT JOIN RZTSYSDEPARTMENT d ON k.TDYW_ORG = d.DEPTNAME " +
                    " WHERE STATUS = 0 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1) " + s11;
            /**
             * 现场稽查未开始
             */
            String xcJcWks = " SELECT count(1) " +
                    " FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    " WHERE STATUS = 0 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1) " + s12;
            /**
             * 正常巡视进行中
             */
            String zcXsJxz = " SELECT count(1) " +
                    " FROM XS_ZC_TASK " +
                    " WHERE STAUTS = 1 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1)" + s13;
            /**
             * 保电巡视进行中
             */
            String bdXsJxz = " SELECT count(1) " +
                    " FROM XS_TXBD_TASK " +
                    " WHERE STAUTS = 1 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1)" + s14;
            /**
             * 看护进行中
             */
            String khJxz = " SELECT count(1) " +
                    " FROM KH_TASK k LEFT JOIN RZTSYSUSER r ON k.USER_ID = r.ID " +
                    " WHERE STATUS = 1 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1) " + s15;
            /**
             * 现场稽查进行中
             */
            String xcJcJxz = " SELECT count(1) " +
                    " FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                     " WHERE STATUS = 1 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1) " + s16;
            /**
             * 正常巡视已完成
             */
            String zcXsYwc = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE STAUTS = 2 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1) " + s17;
            /**
             * 保电巡视已完成
             */
            String bdXsYwc = " SELECT count(1)  " +
                    " FROM XS_TXBD_TASK " +
                    " WHERE STAUTS = 2 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1) " + s18;
            /**
             * 看护已完成
             */
            String khYwc = " SELECT count(1) " +
                    " FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    " WHERE STATUS = 2 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1) " + s19;
            /**
             *现场稽查已完成
             */
            String xcJcYwc = " SELECT count(1) " +
                    " FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID  " +
                    " WHERE STATUS = 2 AND PLAN_END_TIME >= trunc(SYSDATE) AND PLAN_START_TIME <= trunc(sysdate + 1)" + s20;
            /**
             * 今日治理隐患
             */
            String handlesql = " SELECT COUNT(*)  FROM KH_YH_HISTORY WHERE trunc(YHXQ_TIME) =trunc(sysdate)" + s21;
            /**
             * 今日新增隐患
             */
            String addedsql = " SELECT COUNT(*)  FROM KH_YH_HISTORY WHERE trunc(create_time) =trunc(sysdate)" + s22;
            /**
             * 今日调整隐患
             */
            String updateSql = " SELECT COUNT(*)  FROM KH_YH_HISTORY WHERE trunc(update_time) =trunc(sysdate)" + s23;
            /**
             * 隐患总数
             */
            String allSql = " select count(*)  from kh_yh_history where yhzt=0" + s24;
            String sql = "SELECT " +
//                    "(" + zxUser + ") as zxUser," +
//                    "(" + lxUser + ") as lxUser," +
                    "(" + xsZxUser + ") as xsZxUser," +
                    "(" + xsLxUser + ") as xsLxUser," +
                    "(" + khZxUser + ") as khZxUser," +
                    "(" + khLxUser + ") as khLxUser, " +
                    "(" + qjcZxUser + ") as qjcZxUser," +
                    "(" + qjcLxUser + ") as qjcLxUser," +
                    "(" + hjcZxUser + ") as hjcZxUser," +
                    "(" + hjcLxUser + ") as hjcLxUser, " +
                    "(" + zcXsWks + ")+(" + bdXsWks + ") as XsWks," +
                    "(" + khWks + ") as khWks, " +
                    "(" + xcJcWks + ") as xcJcWks," +
                    "(" + zcXsJxz + ")+(" + bdXsJxz + ") as XsJxz," +
                    "(" + khJxz + ") as khJxz," +
                    "(" + xcJcJxz + ") as xcJcJxz," +
                    "(" + zcXsYwc + ")+(" + bdXsYwc + ") as XsYwc," +
                    "(" + khYwc + ") as khYwc," +
                    "(" + xcJcYwc + ") as xcJcYwc," +
                    "(" + handlesql + ") as handlesql," +
                    "(" + addedsql + ") as addedsql," +
                    "(" + updateSql + ") as updateSql," +
                    "(" + allSql + ") as ALLSQL " +
                    " FROM dual";
            personnelTasksServerEndpoint.sendText((Session) session.get("session"), this.execSql(sql, list.toArray()));
        });
    }
}
