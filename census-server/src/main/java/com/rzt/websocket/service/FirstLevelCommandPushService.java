package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.util.DateUtil;
import com.rzt.websocket.serverendpoint.FirstLevelCommandServerEndpoint;
import org.hibernate.sql.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FirstLevelCommandPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    FirstLevelCommandServerEndpoint firstLevelCommandServerEndpoint;

    @Scheduled(fixedDelay = 3000)
    public void adminModule1() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            List list = new ArrayList();
            firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), list);
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule2() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            List list = new ArrayList();
            firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), list);
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule4() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            String sql = " select  " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='施工隐患' and yhzt=0) sg, " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='建筑隐患' and yhzt=0) jz, " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='异物隐患' and yhzt=0) yw, " +
                    "(select count(h.id) from KH_YH_HISTORY h where yhjb1='树木隐患' and yhzt=0) sm from dual  ";
            try {
                Map<Object, Object> map1 = new HashMap<>();
                Map<String, Object> map = this.execSqlSingleResult(sql);
                map1.put("data", map);
                map1.put("adminModule", 4);
                firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule3() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            String sql = "SELECT " +
                    "  (SELECT count(h.id) " +
                    "   FROM KH_YH_HISTORY h " +
                    "   WHERE trunc(CREATE_TIME) = trunc(sysdate)) xzyh, " +
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

            try {
                HashMap<Object, Object> map1 = new HashMap<>();
                Map<String, Object> map = this.execSqlSingleResult(sql);
                map1.put("data", map);
                map1.put("adminModule", 3);
                firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule5() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            String xsZxUser = " SELECT count(1) SM " +
                    "FROM (SELECT z.CM_USER_ID " +
                    "      FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                    "      WHERE  z.is_delete = 0 and LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate " +
                    "      GROUP BY z.CM_USER_ID) ";
            /**
             * 巡视离线人员
             */
            String xsLxUser = " SELECT count(1) SM  FROM (SELECT z.CM_USER_ID " +
                    "  FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                    "  WHERE z.is_delete = 0 and LOGINSTATUS = 0 AND USERDELETE = 1  AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) " +
                    "  GROUP BY z.CM_USER_ID) ";
            /**
             * 看护在线人员
             */
            String khZxUser = " SELECT count(1) SM FROM (SELECT count(u.ID) " +
                    "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                    "WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) " +
                    "GROUP BY k.USER_ID) ";
            /**
             * 看护离线人员
             */
            String khLxUser = " SELECT count(1) SM FROM (SELECT count(u.ID) " +
                    "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                    "WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) " +
                    "GROUP BY k.USER_ID) ";

            /**
             * 前台稽查在线人员
             */
            String qjcZxUser = " SELECT count(1) SM FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 and k.check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
            /**
             * 前台稽查离线人员
             */
            String qjcLxUser = " SELECT count(1) SM FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 and k.check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
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
                    String sql = "SELECT LOGINSTATUS status FROM RZTSYSUSER where id=?";
                    Map<String, Object> status = this.execSqlSingleResult(sql, split[i]);
                    if (status.get("STATUS").toString().equals("0")) {
                        a++;
                    } else {
                        b++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            /**
             * 后台稽查在线人员
             */
            //   String hjcZxUser = " SELECT count(id) SM FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 ";
            /**
             * 后台稽查离线人员
             */
            //  String hjcLxUser = " SELECT count(id) SM  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 ";
            try {
                Map<Object, Object> returnMap = new HashMap<>();
                Map<Object, Object> iocMap = new HashMap<>();
                Map<String, Object> xsZxUserMap = this.execSqlSingleResult(xsZxUser);
                Map<String, Object> xsLxUserMap = this.execSqlSingleResult(xsLxUser);
                Map<String, Object> khZxUserMap = this.execSqlSingleResult(khZxUser);
                Map<String, Object> khLxUserMap = this.execSqlSingleResult(khLxUser);
                Map<String, Object> qjcZxUserMap = this.execSqlSingleResult(qjcZxUser);
                Map<String, Object> qjcLxUserMap = this.execSqlSingleResult(qjcLxUser);
                // Map<String, Object> hjcZxUserMap = this.execSqlSingleResult(hjcZxUser);
                // Map<String, Object> hjcLxUserMap = this.execSqlSingleResult(hjcLxUser);
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
                firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), returnMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule8() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            /**
             * 离线
             */
            String offline = "SELECT count(1) as OFFLINES FROM MONITOR_CHECK_EJ  WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2) AND trunc(CREATE_TIME) = trunc(sysdate) AND USER_ID !='null' ";

            /**
             *未按时开始任务
             */
            String answertime = "SELECT sum(ANSWERTIME) ANSWERTIME FROM (SELECT count(1) AS ANSWERTIME FROM MONITOR_CHECK_EJ e   LEFT JOIN KH_TASK t ON e.TASK_ID = t.ID WHERE (e.WARNING_TYPE = 4 OR e.WARNING_TYPE = 10) AND trunc(e.CREATE_TIME) = trunc(sysdate) AND     trunc(t.PLAN_START_TIME) = trunc(sysdate) UNION ALL SELECT count(1) AS ANSWERTIME FROM MONITOR_CHECK_EJ e  LEFT JOIN XS_ZC_TASK t ON e.TASK_ID = t.ID WHERE (e.WARNING_TYPE = 4 OR e.WARNING_TYPE = 10) AND trunc(e.CREATE_TIME) = trunc(sysdate) AND trunc(t.PLAN_START_TIME) = trunc(sysdate))";

            /**
             * 超期任务
             */
            String overdue = " SELECT count(1) as OVERDUE FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 1  AND trunc(CREATE_TIME) = trunc(sysdate) ";
            /**
             * 看护人员脱岗
             */
            String temporarily = " SELECT count(1) AS TEMPORARILY FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 7 AND trunc(CREATE_TIME) = trunc(sysdate) ";
            /**
             * 巡视不合格
             */
            String unqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 5 AND trunc(CREATE_TIME) = trunc(sysdate) ";
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
                firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule6() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            /**
             * 正常巡视未开始
             */
            String zcXsWks = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 0 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 保电巡视未开始
             */
            String bdXsWks = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 0 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 看护未开始
             */
            String khWks = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 0 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 现场稽查未开始
             */
            String xcJcWks = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 0 and check_type=2 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time ";
            /**
             * 正常巡视进行中
             */
            String zcXsJxz = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 1 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 保电巡视进行中
             */
            String bdXsJxz = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 1 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 看护进行中
             */
            String khJxz = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 1 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 现场稽查进行中
             */
            String xcJcJxz = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 1 and check_type=2 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time";
            /**
             * 正常巡视已完成
             */
            String zcXsYwc = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 2 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 保电巡视已完成
             */
            String bdXsYwc = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 2 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 看护已完成
             */
            String khYwc = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 2 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             *现场稽查已完成
             */
            String xcJcYwc = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 2 and check_type=2 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') <plan_end_time";

           /* String sql1 = "select * from(SELECT CREATETIME FROM TIMED_TASK where THREEDAY=1 ORDER BY CREATETIME DESC ) where ROWNUM=1";
            List<Map<String, Object>> maps = this.execSql(sql1);
            Date createtime = DateUtil.parseDate(maps.get(0).get("CREATETIME").toString());
            Date nextTime = DateUtil.addDate(createtime, 72);*/
            /**
             *后台稽查未完成
             */
            String htJcWks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 0";
            /**
             *后台稽查进行中
             */
            String htJcYks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
            /**
             *后台稽查已完成
             */
            String htJcYwc = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
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
                    // "(" + htJcYks + ") as htJcYks, " +
                    "(" + htJcYwc + ") as htJcYwc " +
                    "  FROM dual";
            List<Map<String, Object>> list = this.execSql(sql);
            Map map = new HashMap();
            map.put("data", list);
            map.put("adminModule", 6);
            firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule6_1() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            Object deptid = session.get("DEPTID");
            /**
             * 正常巡视未开始
             */
            String zcXsWks = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 保电巡视未开始
             */
            String bdXsWks = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 看护未开始
             */
            String khWks = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 现场稽查未开始
             */
            String xcJcWks = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 0 and check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time and plan_start_time <= sysdate";
            /**
             * 正常巡视进行中
             */
            String zcXsJxz = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 1 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 保电巡视进行中
             */
            String bdXsJxz = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 1 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 看护进行中
             */
            String khJxz = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 1 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 现场稽查进行中
             */
            String xcJcJxz = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 1 and check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time";
            /**
             * 正常巡视已完成
             */
            String zcXsYwc = "SELECT count(1)  " +
                    "FROM XS_ZC_TASK " +
                    "WHERE is_delete = 0 and STAUTS = 2 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 保电巡视已完成
             */
            String bdXsYwc = "SELECT count(1)  " +
                    "FROM XS_TXBD_TASK " +
                    "WHERE STAUTS = 2 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             * 看护已完成
             */
            String khYwc = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 2 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate)";
            /**
             *现场稽查已完成
             */
            String xcJcYwc = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 2 and check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') <plan_end_time";

           /* String sql1 = "select * from(SELECT CREATETIME FROM TIMED_TASK where THREEDAY=1 ORDER BY CREATETIME DESC ) where ROWNUM=1";
            List<Map<String, Object>> maps = this.execSql(sql1);
            Date createtime = DateUtil.parseDate(maps.get(0).get("CREATETIME").toString());
            Date nextTime = DateUtil.addDate(createtime, 72);*/
            /**
             *后台稽查未完成
             */
            String htJcWks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 0";
            /**
             *后台稽查进行中
             */
            String htJcYks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
            /**
             *后台稽查已完成
             */
            String htJcYwc = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
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
                    // "(" + htJcYks + ") as htJcYks, " +
                    "(" + htJcYwc + ") as htJcYwc " +
                    "  FROM dual";
            List<Map<String, Object>> list = this.execSql(sql);
            Map map = new HashMap();
            map.put("data", list);
            map.put("adminModule", "6_1");
            firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
        });
    }

    @Scheduled(fixedDelay = 3000)
    public void adminModule7() {
        Map<String, HashMap> sendMsg = firstLevelCommandServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            if (Integer.valueOf(session.get("mapType").toString()) == 2) {
                String wks = " SELECT nvl(xswks,0) + nvl(khwks,0) AS wks, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG";
                String jxz = "SELECT nvl(xswks,0) + nvl(khwks,0) AS wks, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 1 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 1 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG ";
                String ywc = " SELECT nvl(xswks,0) + nvl(khwks,0) AS wks, a.TD_ORG FROM (SELECT rr.ID AS TD_ORG, xswks FROM (SELECT count(1) AS xswks, TD_ORG FROM XS_ZC_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2 GROUP BY TD_ORG) cae RIGHT JOIN RZTSYSDEPARTMENT rr ON cae.TD_ORG = rr.ID WHERE rr.DEPTSORT IS NOT NULL ORDER BY rr.DEPTSORT) a LEFT JOIN (SELECT khwks,  ppp.ID as TD_ORG FROM (SELECT count(1)   AS khwks, k.TDYW_ORG AS TD_ORG FROM KH_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2 GROUP BY TDYW_ORG) bb RIGHT JOIN RZTSYSDEPARTMENT ppp ON bb.TD_ORG = ppp.DEPTNAME WHERE ppp.DEPTSORT IS NOT NULL) b ON a.TD_ORG = b.TD_ORG";
                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
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
                        jxz1.put(singleKh.get("TD_ORG"), singleKh.get("WKS"));
                    }
                }
                if (!StringUtils.isEmpty(list2)) {
                    for (Map<String, Object> singleKh : list2) {
                        ywc2.put(singleKh.get("TD_ORG"), singleKh.get("WKS"));
                    }
                }
                if (Integer.valueOf(session.get("type").toString()) == 9) {
                    for (Map<String, Object> dept : deptname) {
                        dept.put("wks", wks1.get(dept.get("ID")));
                        dept.put("jxz", jxz1.get(dept.get("ID")));
                        dept.put("ywc", ywc2.get(dept.get("ID")));
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 0) {
                    for (Map<String, Object> dept : deptname) {
                        dept.put("wks", wks1.get(dept.get("ID")));
                    }

                } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                    for (Map<String, Object> dept : deptname) {
                        dept.put("jxz", jxz1.get(dept.get("ID")));
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 2) {
                    for (Map<String, Object> dept : deptname) {
                        dept.put("ywc", ywc2.get(dept.get("ID")));
                    }
                }
                Map map = new HashMap();
                map.put("data", deptname);
                map.put("adminModule", 7);
                firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
            } else if (Integer.valueOf(session.get("mapType").toString()) == 0) {
                String sql = "  ";
                if (Integer.valueOf(session.get("type").toString()) == 0) {
                    sql = " SELECT\n" +
                            "  deptname,\n" +
                            "(SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname)                                           all_count\n" +
                            "FROM RZTSYSDEPARTMENT d\n" +
                            "WHERE d.DEPTPID = '402881e6603a69b801603a6ab1d70000'\n" +
                            "ORDER BY d.DEPTSORT ";
                } else if (Integer.valueOf(session.get("type").toString()) == 9) {
                    sql = " SELECT\n" +
                            "  deptname,\n" +
                            "  (SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname)                                           all_count,\n" +
                            "  (SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname AND trunc(h.CREATE_TIME) = trunc(sysdate)) new_count\n" +
                            "FROM RZTSYSDEPARTMENT d\n" +
                            "WHERE d.DEPTPID = '402881e6603a69b801603a6ab1d70000'\n" +
                            "ORDER BY d.DEPTSORT ";
                } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                    sql = " SELECT\n" +
                            "  deptname,\n" +
                            "  (SELECT count(h.id)\n" +
                            "   FROM KH_YH_HISTORY h\n" +
                            "   WHERE h.TDYW_ORG = d.deptname AND trunc(h.CREATE_TIME) = trunc(sysdate)) new_count\n" +
                            "FROM RZTSYSDEPARTMENT d\n" +
                            "WHERE d.DEPTPID = '402881e6603a69b801603a6ab1d70000'\n" +
                            "ORDER BY d.DEPTSORT ";
                }
                List<Map<String, Object>> list = this.execSql(sql);
                Map map = new HashMap();
                map.put("data", list);
                map.put("adminModule", 7);
                firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
            } else if (Integer.valueOf(session.get("mapType").toString()) == 1) {
                String khzx = " SELECT rr.ID,count(a.ID) as khzx FROM (SELECT u.ID,u.DEPTID FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate GROUP BY u.ID,u.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL GROUP BY rr.ID ";
                String khlx = " SELECT rr.ID,count(a.ID) as khlx FROM (SELECT u.ID,u.DEPTID FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate GROUP BY u.ID,u.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL GROUP BY rr.ID ";
                String xszx = " SELECT rr.ID,count(a.ID) as xszx FROM (SELECT r.ID,r.DEPTID FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID WHERE LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate GROUP BY r.ID,r.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL GROUP BY rr.ID ";
                String xslx = " SELECT rr.ID,count(a.ID) as xslx FROM (SELECT r.ID,r.DEPTID FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID WHERE LOGINSTATUS = 0 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate GROUP BY r.ID,r.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT rr ON a.DEPTID = rr.ID WHERE rr.DEPTSORT IS NOT NULL GROUP BY rr.ID\n ";
                String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
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
                        Integer khzx1 = Integer.valueOf(map1.get(dept.get("ID")).toString());
                        Integer xszx1 = Integer.valueOf(map3.get(dept.get("ID")).toString());
                        dept.put("zx", khzx1 + xszx1);
                        Integer khlx1 = Integer.valueOf(map2.get(dept.get("ID")).toString());
                        Integer xslx1 = Integer.valueOf(map4.get(dept.get("ID")).toString());
                        dept.put("lx", khlx1 + xslx1);
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 0) {
                    for (Map<String, Object> dept : deptname) {
                        Integer khlx1 = Integer.valueOf(map2.get(dept.get("ID")).toString());
                        Integer xslx1 = Integer.valueOf(map4.get(dept.get("ID")).toString());
                        dept.put("lx", khlx1 + xslx1);
                    }
                } else if (Integer.valueOf(session.get("type").toString()) == 1) {
                    for (Map<String, Object> dept : deptname) {
                        Integer khzx1 = Integer.valueOf(map1.get(dept.get("ID")).toString());
                        Integer xszx1 = Integer.valueOf(map3.get(dept.get("ID")).toString());
                        dept.put("zx", khzx1 + xszx1);
                    }
                }
                Map map = new HashMap();
                map.put("data", deptname);
                map.put("adminModule", 7);
                firstLevelCommandServerEndpoint.sendText((Session) session.get("session"), map);
            }
        });
    }

}
