package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.util.DateUtil;
import com.rzt.websocket.serverendpoint.TwoLevelCommandServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TwoLevelCommandPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    TwoLevelCommandServerEndpoint twoLevelCommandServerEndpoint;

    @Scheduled(fixedRate = 30000)
    public void adminModule1() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            List list = new ArrayList();
            twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), list);
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule2() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            List list = new ArrayList();
            twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), list);
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule4() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            String sql = " select  " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='施工隐患' AND YWORG_ID='" + deptid + "' and yhzt=0) sg, " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='建筑隐患' AND YWORG_ID='" + deptid + "' and yhzt=0) jz, " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='异物隐患' AND YWORG_ID='" + deptid + "' and yhzt=0) yw, " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='树木隐患' AND YWORG_ID='" + deptid + "' and yhzt=0) sm from dual  ";
            try {
                Map<Object, Object> map1 = new HashMap<>();
                Map<String, Object> map = this.execSqlSingleResult(sql);
                map1.put("data", map);
                map1.put("adminModule", 4);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule3() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            String sql = "SELECT " +
                    "  (SELECT count(h.id) " +
                    "   FROM KH_YH_HISTORY h " +
                    "   WHERE trunc(CREATE_TIME) = trunc(sysdate) AND YWORG_ID='" + deptid + "') xzyh, " +
                    "  (SELECT count(*) " +
                    "   FROM KH_YH_HISTORY h " +
                    "   WHERE (yhjb1 = '施工隐患' OR yhjb1 = '建筑隐患' OR yhjb1 = '异物隐患' OR " +
                    "         yhjb1 = '树木隐患') AND UPDATE_TIME IS NOT NULL AND   YHXQ_TIME IS NULL AND trunc(UPDATE_TIME) = trunc(sysdate) and YWORG_ID='" + deptid + "' " +
                    "  )                                           tzyh, " +
                    "  (SELECT count(*) " +
                    "   FROM KH_YH_HISTORY h " +
                    "   WHERE (yhjb1 = '施工隐患' OR yhjb1 = '建筑隐患' OR yhjb1 = '异物隐患' OR " +
                    "         yhjb1 = '树木隐患') AND YHXQ_TIME IS NOT NULL AND trunc(YHXQ_TIME) = trunc(sysdate) and YWORG_ID='" + deptid + "'" +
                    "  )                                           zlyh " +
                    "FROM dual";

            try {
                HashMap<Object, Object> map1 = new HashMap<>();
                Map<String, Object> map = this.execSqlSingleResult(sql);
                map1.put("data", map);
                map1.put("adminModule", 3);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule5() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            String xsZxUser = " SELECT count(1) SM " +
                    "FROM (SELECT z.CM_USER_ID " +
                    "      FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                    "      WHERE LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME <= sysdate AND z.PLAN_END_TIME >= trunc(sysdate) and r.deptid='" + deptid + "'" +
                    "      GROUP BY z.CM_USER_ID) ";
            /**
             * 巡视离线人员
             */
            String xsLxUser = " SELECT count(1) SM  FROM (SELECT z.CM_USER_ID " +
                    "  FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                    "  WHERE LOGINSTATUS = 0 AND USERDELETE = 1  AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and r.deptid='" + deptid + "'" +
                    "  GROUP BY z.CM_USER_ID) ";
            /**
             * 看护在线人员
             */
            String khZxUser = " SELECT count(1) SM FROM (SELECT count(u.ID) " +
                    "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                    "WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and u.deptid='" + deptid + "'" +
                    "GROUP BY k.USER_ID) ";
            /**
             * 看护离线人员
             */
            String khLxUser = " SELECT count(1) SM FROM (SELECT count(u.ID) " +
                    "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                    "WHERE LOGINSTATUS = 0 AND WORKTYPE     = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and u.deptid='" + deptid + "'" +
                    "GROUP BY k.USER_ID) ";

            /**
             * 前台稽查在线人员
             */
            String qjcZxUser = " SELECT count(1) SM FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 and k.check_type=2 and to_date('" + timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time and u.deptid='" + deptid + "' GROUP BY k.USER_ID) ";
            /**
             * 前台稽查离线人员
             */
            String qjcLxUser = " SELECT count(1) SM FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 and k.check_type=2 and to_date('" + timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time and u.deptid='" + deptid + "' GROUP BY k.USER_ID) ";

            /*  *//**
             * 后台稽查在线人员
             *//*
            String hjcZxUser = " SELECT count(id) SM FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 and deptid='" + deptid + "' ";
            *//**
             * 后台稽查离线人员
             *//*
            String hjcLxUser = " SELECT count(id) SM  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 and deptid='" + deptid + "'";*/
            int a = 0;
            int b = 0;
            try {
                String user = "SELECT * FROM WORKING_TIMED WHERE DEPT_ID='" + deptid + "'";
                Map<String, Object> map = this.execSqlSingleResult(user);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String format = formatter.format(new Date());
                String s = format + " 00:00:00";
                String userId = "";
                String start = map.get("START_TIME").toString();
                String end = map.get("END_TIME").toString();
                Date nowDate = DateUtil.getNowDate();
                if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(end)).getTime()) {
                    userId = map.get("DAY_USER").toString();
                } else {
                    userId = map.get("NIGHT_USER").toString();
                }
                String[] split = userId.split(",");
                for (int i = 0; i < split.length; i++) {
                    String sql = "SELECT LOGINSTATUS status FROM RZTSYSUSER where id=? and USERDELETE=1";
                    List<Map<String, Object>> status = this.execSql(sql, split[i]);
                    if (status.size() > 0) {
                        if (status.get(0).get("STATUS").toString().equals("0")) {
                            a++;
                        } else {
                            b++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Map<Object, Object> returnMap = new HashMap<>();
                Map<Object, Object> iocMap = new HashMap<>();
                Map<String, Object> xsZxUserMap = this.execSqlSingleResult(xsZxUser);
                Map<String, Object> xsLxUserMap = this.execSqlSingleResult(xsLxUser);
                Map<String, Object> khZxUserMap = this.execSqlSingleResult(khZxUser);
                Map<String, Object> khLxUserMap = this.execSqlSingleResult(khLxUser);
                Map<String, Object> qjcZxUserMap = this.execSqlSingleResult(qjcZxUser);
                Map<String, Object> qjcLxUserMap = this.execSqlSingleResult(qjcLxUser);
//                Map<String, Object> hjcZxUserMap = this.execSqlSingleResult(hjcZxUser);
//                Map<String, Object> hjcLxUserMap = this.execSqlSingleResult(hjcLxUser);
                iocMap.put("XSZX", xsZxUserMap.get("SM").toString());
                iocMap.put("XSLX", xsLxUserMap.get("SM").toString());
                iocMap.put("KHZX", khZxUserMap.get("SM").toString());
                iocMap.put("KHLX", khLxUserMap.get("SM").toString());
                iocMap.put("QJCZX", qjcZxUserMap.get("SM").toString());
                iocMap.put("QJCLX", qjcLxUserMap.get("SM").toString());
                iocMap.put("HJCZX", a);
                iocMap.put("HJCLX", b);
                returnMap.put("data", iocMap);
                returnMap.put("adminModule", 5);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), returnMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule8() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            /**
             * 离线
             */
//            String offline = "SELECT count(1) as OFFLINES FROM MONITOR_CHECK_EJ  WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
            String offline = " SELECT count(count(1)) AS OFFLINES " +
                    "                    FROM MONITOR_CHECK_EJ  " +
                    "                    WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2 OR WARNING_TYPE = 13)  AND STATUS = 0   AND trunc(CREATE_TIME) = trunc(sysdate) AND USER_ID != 'null' AND TASK_STATUS=0  AND USER_LOGIN_TYPE = 0 AND DEPTID= '" + deptid + "' GROUP BY USER_ID ";
            /**
             *未按时开始任务
             */
            String answertime = "SELECT count(1) as ANSWERTIME  FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 4 OR WARNING_TYPE = 10)  AND STATUS = 0  AND TASK_STATUS = 0  AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
            /**
             * 超期任务
             */
            String overdue = " SELECT count(1) as OVERDUE FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 1  AND trunc(CREATE_TIME) = trunc(sysdate) AND STATUS = 0  AND TASK_STATUS = 0  AND DEPTID= '" + deptid + "'";
            /**
             * 看护人员脱岗
             */
//            String temporarily = " SELECT count(1) AS TEMPORARILY FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 7  AND STATUS = 0  AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
            String temporarily = " SELECT count(DISTINCT ej.TASK_ID) AS TEMPORARILY FROM MONITOR_CHECK_EJ ej\n" +
                    "  LEFT JOIN WARNING_OFF_POST_USER_TIME t ON ej.USER_ID=t.FK_USER_ID\n" +
                    "  AND ej.TASK_ID=t.FK_TASK_ID\n" +
                    " WHERE ej.WARNING_TYPE = 7 AND trunc(t.START_TIME) = trunc(sysdate) AND t.TIME_STATUS=1 AND t.END_TIME IS NULL  AND DEPTID= '" + deptid + "'";
            /**
             * 巡视不合格
             */
            String unqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 5 OR WARNING_TYPE = 3 ) AND STATUS = 0  AND TASK_STATUS = 0  AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
//            String unqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol FROM UNQUALIFIEDPATROLTABLE WHERE DEPTID = '" + deptid + "' ";
            try {
                Map map1 = new HashMap();
                Map map = new HashMap();
                Map<String, Object> offlineMap = this.execSqlSingleResult(offline);
                Map<String, Object> answertimeMap = this.execSqlSingleResult(answertime);
                Map<String, Object> overdueMap = this.execSqlSingleResult(overdue);
                Map<String, Object> temporarilyMap = this.execSqlSingleResult(temporarily);
                Map<String, Object> unqualifiedpatrolMap = this.execSqlSingleResult(unqualifiedpatrol);
                map.put("OFFLINEMAP", offlineMap.get("OFFLINES"));
                map.put("ANSWERTIMEMAP", answertimeMap.get("ANSWERTIME"));
                map.put("OVERDUEMAP", overdueMap.get("OVERDUE"));
                map.put("TEMPORARILYMAP", temporarilyMap.get("TEMPORARILY"));
                map.put("UNQUALIFIEDPATROLMAP", unqualifiedpatrolMap.get("UNQUALIFIEDPATROL"));
                map1.put("data", map);
                map1.put("adminModule", 8);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule20() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            /**
             * 离线
             */
//            String offline = "SELECT count(1) as OFFLINES FROM MONITOR_CHECK_EJ  WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
            String offline = " SELECT count(1) AS OFFLINES\n" +
                    "FROM MONITOR_CHECK_EJ\n" +
                    "WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2 OR WARNING_TYPE = 13) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID ='" + deptid + "' ";
            /**
             *未按时开始任务
             */
            String answertime = "SELECT count(1) as ANSWERTIME  FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 4 OR WARNING_TYPE = 10) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
            /**
             * 超期任务
             */
            String overdue = " SELECT count(1) as OVERDUE FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 1  AND trunc(CREATE_TIME) = trunc(sysdate)  AND DEPTID= '" + deptid + "'";
            /**
             * 看护人员脱岗
             */
//            String temporarily = " SELECT count(1) AS TEMPORARILY FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 7  AND STATUS = 0  AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
            String temporarily = " SELECT count(1) AS TEMPORARILY\n" +
                    "FROM WARNING_OFF_POST_USER u LEFT JOIN RZTSYSUSER r ON r.ID = u.USER_ID\n" +
                    "  LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID = t.FK_USER_ID AND u.TASK_ID = t.FK_TASK_ID\n" +
                    "WHERE 1 = 1 AND trunc(t.START_TIME) = trunc(sysdate) AND t.OVER_STATUS = 1 AND r.DEPTID ='" + deptid + "' ";
            /**
             * 巡视不合格
             */
            String unqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 5 OR WARNING_TYPE = 3 ) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID= '" + deptid + "'";
//            String unqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol FROM UNQUALIFIEDPATROLTABLE WHERE DEPTID = '" + deptid + "' ";
            try {
                Map map1 = new HashMap();
                Map map = new HashMap();
                Map<String, Object> offlineMap = this.execSqlSingleResult(offline);
                Map<String, Object> answertimeMap = this.execSqlSingleResult(answertime);
                Map<String, Object> overdueMap = this.execSqlSingleResult(overdue);
                Map<String, Object> temporarilyMap = this.execSqlSingleResult(temporarily);
                Map<String, Object> unqualifiedpatrolMap = this.execSqlSingleResult(unqualifiedpatrol);
                map.put("OFFLINEMAP", offlineMap.get("OFFLINES"));
                map.put("ANSWERTIMEMAP", answertimeMap.get("ANSWERTIME"));
                map.put("OVERDUEMAP", overdueMap.get("OVERDUE"));
                map.put("TEMPORARILYMAP", temporarilyMap.get("TEMPORARILY"));
                map.put("UNQUALIFIEDPATROLMAP", unqualifiedpatrolMap.get("UNQUALIFIEDPATROL"));
                map1.put("data", map);
                map1.put("adminModule", 20);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule6() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            Object dept = session.get("DEPT");
            /**
             * 正常巡视未开始
             */
            String zcXsWks = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE STAUTS = 0 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)  AND TD_ORG='" + deptid + "'";
            /**
             * 保电巡视未开始
             */
            String bdXsWks = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 0 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) AND TD_ORG='" + deptid + "'";
            /**
             * 看护未开始
             */
            String khWks = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 0 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) AND YWORG_ID = '" + deptid + "'";
            /**
             * 现场稽查未开始
             */
            String xcJcWks = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK  t left join rztsysuser u on u.id= t.user_id" +
                    " WHERE STATUS = 0 and check_type=2 and to_date('" + timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time AND u.DEPTID='" + deptid + "'";
            /**
             * 正常巡视进行中
             */
            String zcXsJxz = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    " WHERE STAUTS = 1 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)AND TD_ORG='" + deptid + "'";
            /**
             * 保电巡视进行中
             */
            String bdXsJxz = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 1 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) AND TD_ORG='" + deptid + "'";
            /**
             * 看护进行中
             */
            String khJxz = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 1 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) AND YWORG_ID = '" + deptid + "'";
            /**
             * 现场稽查进行中
             */
            String xcJcJxz = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK t left join rztsysuser u on u.id= t.user_id" +
                    " WHERE STATUS = 1 and check_type=2 and to_date('" + timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time AND u.DEPTID='" + deptid + "'";
            /**
             * 正常巡视已完成
             */
            String zcXsYwc = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE STAUTS = 2 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) AND TD_ORG='" + deptid + "'";
            /**
             * 保电巡视已完成
             */
            String bdXsYwc = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 2 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) AND TD_ORG='" + deptid + "'";
            /**
             * 看护已完成
             */
            String khYwc = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    " WHERE STATUS = 2 AND PLAN_START_TIME<= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) AND YWORG_ID = '" + deptid + "'";
            /**
             *现场稽查已完成
             */
            String xcJcYwc = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK  t left join rztsysuser u on u.id= t.user_id" +
                    " WHERE STATUS = 2 and check_type=2 and to_date('" + timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + timeUtil(1) + "','yyyy-MM-dd HH24:mi') <plan_end_time AND u.DEPTID='" + deptid + "'";
            /*String sql1 = "select * from(SELECT CREATETIME FROM TIMED_TASK where THREEDAY=1 ORDER BY CREATETIME DESC ) where ROWNUM=1";
            List<Map<String, Object>> maps = this.execSql(sql1);
            Date createtime = DateUtil.parseDate(maps.get(0).get("CREATETIME").toString());
            Date nextTime = DateUtil.addDate(createtime, 72);*/
            /**
             *后台稽查未完成
             */
            /*String htJcWks = "SELECT count(*) " +
                    "FROM TIMED_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "WHERE  k.CREATETIME > (SELECT max(CREATETIME)-10/24/60 FROM TIMED_TASK) AND k.THREEDAY = 0 AND k.STATUS = 0 AND u.DEPTID = '" + deptid + "'";
            *//**
             *后台稽查进行中
             *//*
            String htJcYks = "SELECT count(*) FROM TIMED_TASK where STATUS=1 and THREEDAY=1 and CREATETIME>( SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60) FROM TIMED_TASK  WHERE THREEDAY = 1 )";
            *//**
             *后台稽查已完成
             *//*
            String htJcYwc = "SELECT count(*) " +
                    "FROM TIMED_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    " WHERE  k.CREATETIME > (SELECT max(CREATETIME)-10/24/60 FROM TIMED_TASK) AND k.THREEDAY = 0 AND k.STATUS = 1 AND u.DEPTID = '" + deptid + "'";*/
            String htJcWks = "SELECT count(*) FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) and (TASKS>COMPLETE) and dept_id='" + deptid + "'";
            /**
             *后台稽查进行中
             */
//            String htJcYks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
            String htJcYks = "SELECT count(DISTINCT (DEPT_ID)) FROM TIMED_TASK_RECORD ";
            /**
             *后台稽查已完成
             */
            String htJcYwc = "SELECT count(*) FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) and (TASKS=COMPLETE) and dept_id='" + deptid + "'";
            String sql = "SELECT " +
                    "(" + zcXsWks + ")+(" + bdXsWks + ") as XsWks," +
                    "(" + zcXsJxz + ")+(" + bdXsJxz + ") as XsJxz," +
                    "(" + zcXsYwc + ")+(" + bdXsYwc + ") as XsYwc," +
                    "(" + khJxz + ") as khJxz," +
                    "(" + khWks + ") as khWks, " +
                    "(" + khYwc + ") as khYwc," +
                    "(" + xcJcJxz + ") as xcJcJxz," +
                    "(" + xcJcWks + ") as xcJcWks," +
                    "(" + xcJcYwc + ") as xcJcYwc, " +
                    "(" + htJcWks + ") as htJcWks, " +
                    "(1) as htJcYks, " +
                    "(" + htJcYwc + ") as htJcYwc " +
                    "  FROM dual";
            List<Map<String, Object>> list = this.execSql(sql);
            Map map = new HashMap();
            map.put("data", list);
            map.put("adminModule", 6);
            twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule6_1() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            /**
             * 正常巡视未开始
             */
            String zcXsWks = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and td_org='" + deptid + "'";
            /**
             * 保电巡视未开始
             */
            String bdXsWks = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and td_org='" + deptid + "'";
            /**
             * 看护未开始
             */
            String khWks = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and yworg_id='" + deptid + "'";
            /**
             * 现场稽查未开始
             */
            String xcJcWks = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK  t left join rztsysuser u on u.id= t.user_id" +
                    " WHERE STATUS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and u.deptid='" + deptid + "'";
            /**
             * 正常巡视进行中
             */
            String zcXsJxz = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)  and td_org='" + deptid + "'";
            /**
             * 保电巡视进行中
             */
            String bdXsJxz = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and td_org='" + deptid + "'";
            /**
             * 看护进行中
             */
            String khJxz = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and yworg_id='" + deptid + "'";
            /**
             * 现场稽查进行中
             */
            String xcJcJxz = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK  t left join rztsysuser u on u.id= t.user_id" +
                    " WHERE STATUS = 1  AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and u.deptid='" + deptid + "'";
            /**
             * 正常巡视已完成
             */
            String zcXsYwc = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and td_org='" + deptid + "'";
            /**
             * 保电巡视已完成
             */
            String bdXsYwc = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and td_org='" + deptid + "'";
            /**
             * 看护已完成
             */
            String khYwc = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and yworg_id='" + deptid + "'";
            /**
             *现场稽查已完成
             */
            String xcJcYwc = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK  t left join rztsysuser u on u.id= t.user_id" +
                    " WHERE STATUS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) and u.deptid='" + deptid + "'";

           /* String sql1 = "select * from(SELECT CREATETIME FROM TIMED_TASK where THREEDAY=1 ORDER BY CREATETIME DESC ) where ROWNUM=1";
            List<Map<String, Object>> maps = this.execSql(sql1);
            Date createtime = DateUtil.parseDate(maps.get(0).get("CREATETIME").toString());
            Date nextTime = DateUtil.addDate(createtime, 72);*/
            /**
             *后台稽查未完成
             */
            String htJcWks = "SELECT count(*) FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) and (TASKS>COMPLETE) and dept_id='" + deptid + "'";
            /**
             *后台稽查进行中
             */
//            String htJcYks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
            String htJcYks = "SELECT count(DISTINCT (DEPT_ID)) FROM TIMED_TASK_RECORD ";
            /**
             *后台稽查已完成
             */
            String htJcYwc = "SELECT count(*) FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) and (TASKS=COMPLETE) and dept_id='" + deptid + "'";
            String sql = "SELECT " +
                    "(" + zcXsWks + ")+(" + bdXsWks + ") as XsWks," +
                    "(" + zcXsJxz + ")+(" + bdXsJxz + ") as XsJxz," +
                    "(" + zcXsYwc + ")+(" + bdXsYwc + ") as XsYwc," +
                    "(" + khJxz + ") as khJxz," +
                    "(" + khWks + ") as khWks, " +
                    "(" + khYwc + ") as khYwc," +
                    "(" + xcJcJxz + ") as xcJcJxz," +
                    "(" + xcJcWks + ") as xcJcWks," +
                    "(" + xcJcYwc + ") as xcJcYwc, " +
                    "(" + htJcWks + ") as htJcWks, " +
                    "(" + 1 + ") as htJcYks, " +
                    "(" + htJcYwc + ") as htJcYwc " +
                    "  FROM dual";
            List<Map<String, Object>> list = this.execSql(sql);
            Map map = new HashMap();
            map.put("data", list);
            map.put("adminModule", "6_1");
            twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
        });
    }

    @Scheduled(fixedRate = 30000)
    public void adminModule7() {
        Map<String, HashMap> sendMsg = twoLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            if (Integer.valueOf(session.get("mapType").toString()) == 2) {
                String xskh = null;
                String xcjc = null;
                //0 当天 1 当前
                if (Integer.valueOf(session.get("tableType").toString()) == 0) {
                    xskh = " PLAN_START_TIME <= trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) ";
                    xcjc = " to_date('" + timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time ";
                } else if (Integer.valueOf(session.get("tableType").toString()) == 1) {
                    xskh = " PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) ";
                    xcjc = " PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) ";
                }
                //现场稽查进行中
                String xcJcJxz = "SELECT count(1) AS xsjcjxz," +
                        "  DEPTID as TD_ORG " +
                        "FROM CHECK_LIVE_TASK t left join rztsysuser u on u.id= t.user_id" +
                        " WHERE STATUS = 1 and check_type=2 and " + xcjc + " AND u.DEPTID='" + deptid + "'  GROUP BY u.DEPTID ";
                //现场稽查未开始
                String xcJcWks = "SELECT count(1) AS xsjcwks, DEPTID as TD_ORG  " +
                        "FROM CHECK_LIVE_TASK  t left join rztsysuser u on u.id= t.user_id" +
                        " WHERE STATUS = 0 and check_type=2 and " + xcjc + " AND u.DEPTID='" + deptid + "'  GROUP BY u.DEPTID ";
                //现场稽查已完成
                String xcJcYwc = "SELECT  count(1) AS xsjcywc, DEPTID as TD_ORG   " +
                        "FROM CHECK_LIVE_TASK  t left join rztsysuser u on u.id= t.user_id" +
                        " WHERE STATUS = 2 and check_type=2 and " + xcjc + " AND u.DEPTID='" + deptid + "'   GROUP BY u.DEPTID ";
                //后台稽查未开始
                String htjcWks = " SELECT count(1) as htjcwks,DEPT_ID as TD_ORG FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) and (TASKS>COMPLETE) GROUP BY DEPT_ID ";
                //后台稽查进行中
                String htjcJxz = " SELECT 1 as htjcjxz,'" + deptid + "' as TD_ORG FROM dual ";
                //后台稽查已完成
                String htjcYwc = " SELECT count(1) as htjcywc,DEPT_ID as TD_ORG FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) and (TASKS=COMPLETE) GROUP BY DEPT_ID ";

                String wks = " SELECT nvl(xswks,0) + nvl(khwks,0) + nvl(xsjcwks,0) +nvl(htjcwks,0) AS wks, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE " + xskh + " AND STAUTS = 0 AND  is_delete = 0 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE " + xskh + " AND STATUS = 0 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG  LEFT JOIN (" + xcJcWks + ")c  ON a.TD_ORG = c.TD_ORG LEFT JOIN (" + htjcWks + ") d on a.TD_ORG=d.TD_ORG   WHERE b.TD_ORG = '" + deptid + "'";
                String jxz = "SELECT nvl(xswks,0) + nvl(khwks,0) + nvl(xsjcjxz,0) +nvl(htjcjxz,0) AS jxz, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE " + xskh + " AND STAUTS = 1 AND  is_delete = 0 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE " + xskh + " AND STATUS = 1 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG LEFT JOIN (" + xcJcJxz + ")c  ON a.TD_ORG = c.TD_ORG LEFT JOIN (" + htjcJxz + ") d on a.TD_ORG=d.TD_ORG  WHERE b.TD_ORG = '" + deptid + "'";
                String ywc = " SELECT nvl(xswks,0) + nvl(khwks,0) + nvl(xsjcywc,0) + nvl(htjcywc,0) AS ywc, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE " + xskh + " AND STAUTS = 2 AND  is_delete = 0 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE " + xskh + " AND STATUS = 2 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG  LEFT JOIN (" + xcJcYwc + ")c  ON a.TD_ORG = c.TD_ORG LEFT JOIN (" + htjcYwc + ") d on a.TD_ORG=d.TD_ORG WHERE b.TD_ORG = '" + deptid + "'";
//                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL AND ID = '" + deptid + "' ORDER BY t.DEPTSORT ";
                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  ORDER BY t.DEPTSORT ";
                List<Map<String, Object>> deptname = this.execSql(deptnameSql);
                List<Map<String, Object>> list = null;
                List<Map<String, Object>> list1 = null;
                List<Map<String, Object>> list2 = null;
                if (Integer.valueOf(session.get("type").toString()) == 0) {
                    list = this.execSql(wks);
                } else if (Integer.valueOf(session.get("type").toString()) == 9) {
                    list = this.execSql(wks);
                }
                if (Integer.valueOf(session.get("type").toString()) == 1) {
                    list1 = this.execSql(jxz);
                } else if (Integer.valueOf(session.get("type").toString()) == 9) {
                    list1 = this.execSql(jxz);
                }
                if (Integer.valueOf(session.get("type").toString()) == 2) {
                    list2 = this.execSql(ywc);
                } else if (Integer.valueOf(session.get("type").toString()) == 9) {
                    list2 = this.execSql(ywc);
                }
                Map wks1 = new HashMap();
                Map jxz1 = new HashMap();
                Map ywc2 = new HashMap();
                if (!StringUtils.isEmpty(list)) {
                    for (Map<String, Object> singleXs : list) {
                        wks1.put(singleXs.get("TD_ORG"), singleXs.get("WKS"));
                    }
                }
                if (!StringUtils.isEmpty(list1)) {
                    for (Map<String, Object> singleKh : list1) {
                        jxz1.put(singleKh.get("TD_ORG"), singleKh.get("JXZ"));
                    }
                }
                if (!StringUtils.isEmpty(list2)) {
                    for (Map<String, Object> singleKh : list2) {
                        ywc2.put(singleKh.get("TD_ORG"), singleKh.get("YWC"));
                    }
                }
                if (Integer.valueOf(session.get("type").toString()) == 9) {
                    for (Map<String, Object> dept : deptname) {
                        boolean wkss = wks1.containsKey(dept.get("ID"));
                        boolean jxzs = jxz1.containsKey(dept.get("ID"));
                        boolean ywcs = ywc2.containsKey(dept.get("ID"));
                        if (wkss) {
                            dept.put("wks", wks1.get(dept.get("ID")));
                        } else {
                            dept.put("wks", 0);
                        }
                        if (jxzs) {
                            dept.put("jxz", jxz1.get(dept.get("ID")));
                        } else {
                            dept.put("jxz", 0);
                        }
                        if (ywcs) {
                            dept.put("ywc", ywc2.get(dept.get("ID")));
                        } else {
                            dept.put("ywc", 0);
                        }
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 0) {
                    for (Map<String, Object> dept : deptname) {
                        boolean wkss = wks1.containsKey(dept.get("ID"));
                        if (wkss) {
                            dept.put("wks", wks1.get(dept.get("ID")));
                        } else {
                            dept.put("wks", 0);
                        }
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                    for (Map<String, Object> dept : deptname) {
                        boolean jxz1s = jxz1.containsKey(dept.get("ID"));
                        if (jxz1s) {
                            dept.put("jxz", jxz1.get(dept.get("ID")));
                        } else {
                            dept.put("jxz", 0);
                        }
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 2) {
                    for (Map<String, Object> dept : deptname) {
                        boolean ywc2s = ywc2.containsKey(dept.get("ID"));
                        if (ywc2s) {
                            dept.put("ywc", ywc2.get(dept.get("ID")));
                        } else {
                            dept.put("ywc", 0);
                        }
                    }
                }
                Map map = new HashMap();
                map.put("data", deptname);
                map.put("adminModule", 7);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
            } else if (Integer.valueOf(session.get("mapType").toString()) == 0) {
                String sql = "  ";
                if (Integer.valueOf(session.get("type").toString()) == 0) {
                    sql = " SELECT\n" +
                            "  deptname,d.id,\n" +
                            "(SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname)                                           all_count\n" +
                            "FROM RZTSYSDEPARTMENT d\n" +
                            "WHERE d.id = '" + deptid + "'\n" +
                            "ORDER BY d.DEPTSORT ";
                } else if (Integer.valueOf(session.get("type").toString()) == 9) {
                    sql = " SELECT\n" +
                            "  deptname,d.id,\n" +
                            "  (SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname)                                           all_count,\n" +
                            "  (SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname AND trunc(h.CREATE_TIME) = trunc(sysdate)) new_count\n" +
                            "FROM RZTSYSDEPARTMENT d\n" +
                            "WHERE d.id = '" + deptid + "'\n" +
                            "ORDER BY d.DEPTSORT ";
                } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                    sql = " SELECT\n" +
                            "  deptname,d.id,\n" +
                            "  (SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname AND trunc(h.CREATE_TIME) = trunc(sysdate)) new_count\n" +
                            "FROM RZTSYSDEPARTMENT d\n" +
                            "WHERE d.id = '" + deptid + "'\n" +
                            "ORDER BY d.DEPTSORT ";
                }
                String SQL = " SELECT DEPTNAME,ID,0 as all_count,0 as new_count FROM RZTSYSDEPARTMENT WHERE DEPTSORT IS NOT NULL  ORDER BY  DEPTSORT ";
                List<Map<String, Object>> maps = this.execSql(SQL);
                List<Map<String, Object>> list = this.execSql(sql);
                for (int i = 0; i < maps.size(); i++) {
                    if (maps.get(i).get("ID").equals(list.get(0).get("ID"))) {
                        maps.get(i).put("ID", list.get(0).get("ID"));
                        maps.get(i).put("DEPTNAME", list.get(0).get("DEPTNAME"));
                        maps.get(i).put("ALL_COUNT", list.get(0).get("ALL_COUNT"));
                        maps.get(i).put("NEW_COUNT", list.get(0).get("NEW_COUNT"));
                    }
                }
                Map map = new HashMap();
                map.put("data", maps);
                map.put("adminModule", 7);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
            } else if (Integer.valueOf(session.get("mapType").toString()) == 1) {
                String khzx = " SELECT rr.ID,count(a.ID) as khzx FROM (SELECT u.ID,u.DEPTID FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME<= sysdate AND PLAN_END_TIME >= sysdate GROUP BY u.ID,u.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL AND rr.ID = '" + deptid + "' GROUP BY rr.ID ";
                String khlx = " SELECT rr.ID,count(a.ID) as khlx FROM (SELECT u.ID,u.DEPTID FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME<= sysdate AND PLAN_END_TIME >= sysdate GROUP BY u.ID,u.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL AND rr.ID = '" + deptid + "' GROUP BY rr.ID ";
                String xszx = " SELECT rr.ID,count(a.ID) as xszx FROM (SELECT r.ID,r.DEPTID FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID WHERE LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME<= sysdate AND z.PLAN_END_TIME >= sysdate GROUP BY r.ID,r.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL AND rr.ID = '" + deptid + "' GROUP BY rr.ID ";
                String xslx = " SELECT rr.ID,count(a.ID) as xslx FROM (SELECT r.ID,r.DEPTID FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID WHERE LOGINSTATUS = 0 AND USERDELETE = 1 AND z.PLAN_START_TIME<= sysdate AND z.PLAN_END_TIME >= sysdate GROUP BY r.ID,r.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL AND rr.ID = '" + deptid + "' GROUP BY rr.ID ";
//                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL AND ID='" + deptid + "' ORDER BY t.DEPTSORT ";
                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  ORDER BY t.DEPTSORT ";
                List<Map<String, Object>> deptname = this.execSql(deptnameSql);
                List<Map<String, Object>> list1 = null;
                List<Map<String, Object>> list3 = null;
                List<Map<String, Object>> list2 = null;
                List<Map<String, Object>> list4 = null;
                if (Integer.valueOf(session.get("type").toString()) == 0) {
                    list2 = this.execSql(khlx);
                    list4 = this.execSql(xslx);
                } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                    list1 = this.execSql(khzx);
                    list3 = this.execSql(xszx);
                } else if (Integer.valueOf(session.get("type").toString()) == 9) {
                    list2 = this.execSql(khlx);
                    list4 = this.execSql(xslx);
                    list1 = this.execSql(khzx);
                    list3 = this.execSql(xszx);
                }
                Map map1 = new HashMap();
                Map map2 = new HashMap();
                Map map3 = new HashMap();
                Map map4 = new HashMap();
                if (!StringUtils.isEmpty(list1)) {
                    for (Map<String, Object> singleXs : list1) {
                        map1.put(singleXs.get("ID"), singleXs.get("KHZX"));
                    }
                }
                if (!StringUtils.isEmpty(list2)) {
                    for (Map<String, Object> singleXs : list2) {
                        map2.put(singleXs.get("ID"), singleXs.get("KHLX"));
                    }
                }
                if (!StringUtils.isEmpty(list3)) {
                    for (Map<String, Object> singleXs : list3) {
                        map3.put(singleXs.get("ID"), singleXs.get("XSZX"));
                    }
                }
                if (!StringUtils.isEmpty(list4)) {
                    for (Map<String, Object> singleXs : list4) {
                        map4.put(singleXs.get("ID"), singleXs.get("XSLX"));
                    }
                }
                if (Integer.valueOf(session.get("type").toString()) == 9) {
                    for (Map<String, Object> dept : deptname) {
                        Integer khzx1 = 0;
                        Integer xszx1 = 0;
                        Integer khlx1 = 0;
                        Integer xslx1 = 0;
                        if (map1.containsKey(dept.get("ID"))) {
                            khzx1 = Integer.valueOf(map1.get(dept.get("ID")).toString());
                        }
                        if (map3.containsKey(dept.get("ID"))) {
                            xszx1 = Integer.valueOf(map3.get(dept.get("ID")).toString());
                        }
                        dept.put("zx", khzx1 + xszx1);
                        if (map2.containsKey(dept.get("ID"))) {
                            khlx1 = Integer.valueOf(map2.get(dept.get("ID")).toString());
                        }
                        if (map4.containsKey(dept.get("ID"))) {
                            xslx1 = Integer.valueOf(map4.get(dept.get("ID")).toString());
                        }
                        dept.put("lx", khlx1 + xslx1);
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 0) {
                    for (Map<String, Object> dept : deptname) {
                        Integer khlx1 = 0;
                        Integer xslx1 = 0;
                        if (map2.containsKey(dept.get("ID"))) {
                            khlx1 = Integer.valueOf(map2.get(dept.get("ID")).toString());
                        }
                        if (map4.containsKey(dept.get("ID"))) {
                            xslx1 = Integer.valueOf(map4.get(dept.get("ID")).toString());
                        }
                        dept.put("lx", khlx1 + xslx1);
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                    for (Map<String, Object> dept : deptname) {
                        Integer khzx1 = 0;
                        Integer xszx1 = 0;
                        if (map1.containsKey(dept.get("ID"))) {
                            khzx1 = Integer.valueOf(map1.get(dept.get("ID")).toString());
                        }
                        if (map3.containsKey(dept.get("ID"))) {
                            xszx1 = Integer.valueOf(map3.get(dept.get("ID")).toString());
                        }
                        dept.put("zx", khzx1 + xszx1);
                    }
                }
                Map map = new HashMap();
                map.put("data", deptname);
                map.put("adminModule", 7);
                twoLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
            }
        });
    }

    public String timeUtil(int i) {
        String date = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Date m = c.getTime();
        String mon = df.format(m);
        if (i == 1) {
            date = mon + " 00:00";
        } else {
            date = mon + " 23:59";
        }
        //  task.setPlanEndTime(df.format(new Date()) + " 23:59");
        return date;
    }
}
