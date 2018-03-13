package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.util.DateUtil;
import com.rzt.websocket.serverendpoint.YiJiServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class YiJiPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    YiJiServerEndpoint wuDServerEndpoint;

    @Scheduled(fixedRate = 30000)
    public void module1() {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        map.forEach((sessionId, session) -> {
            module1Method(allMap, session);
        });
    }


    public void module1Method(Map<String, Map> allMap, HashMap session) {
        Integer roletype = Integer.parseInt(session.get("ROLETYPE").toString());
        String deptId = session.get("DEPTID").toString();
        Map message = new HashMap<String, Object>();
        List<Map<String, Object>> resList = new ArrayList<>();
        String sql1 = "SELECT count(1) AS sum,yj.DEPTID FROM MONITOR_CHECK_YJ yj LEFT JOIN RZTSYSUSER u ON yj.USER_ID=u.ID " +
                " WHERE trunc(yj.CREATE_TIME)=trunc(sysdate) AND u.USERDELETE=1 " +
                "    AND yj.STATUS =0  AND yj.USER_ID IS NOT NULL GROUP BY yj.DEPTID";
        //查询处理中
        String sql2 = "SELECT count(1) AS sum,DEPTID FROM MONITOR_CHECK_YJ WHERE trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =1  AND USER_ID IS NOT NULL GROUP BY DEPTID  ";
        //查询已处理
        String sql3 = "SELECT count(1) AS sum,DEPTID FROM MONITOR_CHECK_YJ WHERE trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =2  AND USER_ID IS NOT NULL GROUP BY DEPTID  ";
        List<Map<String, Object>> maps = execSql(sql1);
        List<Map<String, Object>> maps1 = execSql(sql2);
        List<Map<String, Object>> maps2 = execSql(sql3);
        String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
        List<Map<String, Object>> dept = execSql(deptnameSql);
        int i = 0;
        for (Map<String, Object> map : dept) {
            Map<String, Object> sumMap = new HashMap<String, Object>();
            String id = (String) map.get("ID");
            String deptName = (String) map.get("DEPTNAME");
            if (deptName.contains("本部")) {
                deptName = "本部";
            } else {
                deptName = deptName.substring(0, deptName.length() - 2);
            }
            sumMap.put("ID", id);
            sumMap.put("DEPTNAME", deptName);
            sumMap.put("WCL", 0);
            sumMap.put("CLZ", 0);
            sumMap.put("YCL", 0);
            sumMap.put("DEPTSORT", i++);
            //添加未处理
            for (Map<String, Object> m1 : maps) {
                if (id.equals(m1.get("DEPTID").toString())) {
                    sumMap.put("WCL", m1.get("SUM"));
                }
            }
            //添加处理中
            for (Map<String, Object> m1 : maps1) {
                if (id.equals(m1.get("DEPTID").toString())) {
                    sumMap.put("CLZ", m1.get("SUM"));
                }
            }
            //添加已处理
            for (Map<String, Object> m1 : maps2) {
                if (id.equals(m1.get("DEPTID").toString())) {
                    sumMap.put("YCL", m1.get("SUM"));
                }
            }
            resList.add(sumMap);
        }
       /* if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {*/
        message.put("module", 1);
        message.put("data", resList);
        // allMap.put(deptId, message);
         /*   } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module2() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module2Method(allMap, session);
        });

    }

    public void module2Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap<String, Object>();
        Integer roletype = Integer.parseInt(session.get("ROLETYPE").toString());
        String deptId;
        String module2;
        if (1 == 1 /**roletype == 0*/) {
            deptId = "admin";
            module2 = "select sum(decode(STATUS,1,1,0)) ywc,count(1) total from TIMED_TASK where CREATETIME > trunc(sysdate)";
        } else {
            deptId = session.get("DEPTID").toString();
            module2 = "select sum(decode(t.STATUS,1,1,0)) ywc,count(1) total from TIMED_TASK t join RZTSYSUSER tt on t.USER_ID = tt.id and tt.DEPTID = " + deptId + " and t.CREATETIME > trunc(sysdate)";
        }
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module2);
                message.put("data", res);
                message.put("module", 2);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module3() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module3Method(allMap, session);
        });

    }

    public void module3Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap();
//        Integer roletype = Integer.parseInt(session.get("ROLETYPE").toString());
        String deptId;
        String module3;
        if (1 == 1 /**roletype == 0*/) {
            deptId = "admin";
            module3 = "SELECT\n" +
                    "  nvl(sum(decode(QUESTION_TYPE, 1, 1, 0)),0) a,\n" +
                    "  nvl(sum(decode(QUESTION_TYPE, 2, 1, 0)),0) b,\n" +
                    "  nvl(sum(decode(QUESTION_TYPE, 3, 1, 0)),0) c,\n" +
                    "  nvl(sum(decode(QUESTION_TYPE, 4, 1, 0)),0) d\n" +
                    "FROM CHECK_RESULT\n" +
                    "WHERE trunc(create_time) = trunc(sysdate)";
        } /*else {
            deptId = session.get("DEPTID").toString();
            module3 = "SELECT\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 1, 1, 0)),0) a,\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 2, 1, 0)),0) b,\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 3, 1, 0)),0) c,\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 4, 1, 0)),0) d\n" +
                    "FROM CHECK_RESULT t\n" +
                    "  JOIN CHECK_DETAIL tt ON t.CHECK_DETAIL_ID = tt.id join RZTSYSUSER ttt on tt.QUESTION_USER_ID = ttt.id and DEPTID = ?";
        }*/
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module3);
                message.put("module", 3);
                message.put("data", res);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module4() {
        Map<String, Map> allMap = new HashMap<String, Map>();

        //1.未按时开始 4 10
        //2.巡视不到位 3
        //3.巡视超速   5
        //4.超期 1
        //5.看护脱岗 7
        //6.停留过久 6
        //0 未处理 1 处理中 2 已处理
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        map.forEach((sessionId, session) -> {
            module4Method(allMap, session);
        });
    }

    public void module4Method(Map<String, Map> allMap, HashMap session) {
        AtomicReference<String> statusSql = new AtomicReference<>("");
        Map message = new HashMap<String, Object>();
        List list = new ArrayList();
        Integer roletype = Integer.parseInt(session.get("ROLETYPE").toString());
        String deptId = session.get("DEPTID").toString();
        String xSql = " and deptid = " + deptId;
        if (1 == 1 /**roletype == 0*/) {
            deptId = "admin";
            xSql = "";
        }

        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                for (int warningType = 0; warningType <= 5; warningType++) {
                    switch (warningType) {
                        case 0:
                            statusSql.set("(WARNING_TYPE = 4 or WARNING_TYPE = 10)");
                            break;
                        case 1:
                            statusSql.set("WARNING_TYPE = 3");
                            break;
                        case 2:
                            statusSql.set("WARNING_TYPE = 5");
                            break;
                        case 3:
                            statusSql.set("WARNING_TYPE = 1");
                            break;
                        case 4:
                            statusSql.set("WARNING_TYPE = 7");
                            break;
                        case 5:
                            statusSql.set("WARNING_TYPE = 6");
                            break;
                    }
                    StringBuffer module_4 = new StringBuffer("select nvl(sum(decode(STATUS,0,1,0)),0) wcl,nvl(sum(decode(STATUS,1,1,0)),0) clz,nvl(sum(decode(STATUS,2,1,0)),0) ycl,count(1) total from MONITOR_CHECK_EJ where " + statusSql + " and CREATE_TIME > trunc(sysdate) ");
                    module_4.append(xSql);
                    Map<String, Object> res = this.execSqlSingleResult(module_4.toString());
                    list.add(res);
                }
                message.put("module", 4);
                message.put("data", list);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }


    public void module5(String sessionId) {
        try {
            Map<String, HashMap> map1 = wuDServerEndpoint.sendMsg();
            HashMap session = map1.get(sessionId);
            Object deptid = session.get("DEPTID");
            Map message = new HashMap<String, Object>();
            message.put("module", 5);
            Map module5Data = new HashMap();
            int a = 0;
            int b = 0;
            int c = 0;
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
                String allUser = map.get("DAY_USER").toString() + "," + map.get("NIGHT_USER").toString();
                c = allUser.split(",").length;
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
             *后台稽查未完成
             */
            String htJcWks = "SELECT count(1) SM FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 0";
            /**
             *后台稽查进行中
             */
            String htJcYks = "SELECT count(1) SM FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
            /**
             *后台稽查已完成
             */
            String htJcYwc = "SELECT count(1) SM FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
            Map<String, Object> htJcWksMap = this.execSqlSingleResult(htJcWks);
            Map<String, Object> htJcYksMap = this.execSqlSingleResult(htJcYks);
            Map<String, Object> htJcYwcMap = this.execSqlSingleResult(htJcYwc);
            module5Data.put("zx", b);
            module5Data.put("lx", a);
            module5Data.put("all", c);
            module5Data.put("htjcwks", htJcWksMap.get("SM"));
            module5Data.put("htJcYks", htJcYksMap.get("SM"));
            module5Data.put("htJcYwc", htJcYwcMap.get("SM"));
//        module5Data.put("wsx", 0);
            message.put("data", module5Data);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 30000)
    public void module5() {

        Map<String, HashMap> sendMsg = wuDServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            try {
                Object deptid = session.get("DEPTID");
                Map message = new HashMap<String, Object>();
                message.put("module", 5);
                Map module5Data = new HashMap();
                int a = 0;
                int b = 0;
                int c = 0;
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
                    String allUser = map.get("DAY_USER").toString() + "," + map.get("NIGHT_USER").toString();
                    c = allUser.split(",").length;
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
                 *后台稽查未完成
                 */
                String htJcWks = "SELECT count(1) SM FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 0";
                /**
                 *后台稽查进行中
                 */
                String htJcYks = "SELECT count(1) SM FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
                /**
                 *后台稽查已完成
                 */
                String htJcYwc = "SELECT count(1) SM FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
                Map<String, Object> htJcWksMap = this.execSqlSingleResult(htJcWks);
                Map<String, Object> htJcYksMap = this.execSqlSingleResult(htJcYks);
                Map<String, Object> htJcYwcMap = this.execSqlSingleResult(htJcYwc);
                module5Data.put("zx", b);
                module5Data.put("lx", a);
                module5Data.put("all", c);
                module5Data.put("htjcwks", htJcWksMap.get("SM"));
                module5Data.put("htJcYks", htJcYksMap.get("SM"));
                module5Data.put("htJcYwc", htJcYwcMap.get("SM"));
//        module5Data.put("wsx", 0);
                message.put("data", module5Data);
                wuDServerEndpoint.sendText((Session) session.get("session"), message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void module6(String sessionId) {
        try {
            Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
            HashMap session = map.get(sessionId);
            Map message = new HashMap<String, Object>();
            message.put("module", 6);
            /**
             * 前台稽查在线人员
             */
            String qjcZxUser = " SELECT count(1) SM FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1  and k.status in (1,2) and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
            /**
             * 前台稽查离线人员
             */
            String qjcLxUser = " SELECT count(1) SM FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1  and k.status in (1,2) and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
            /**
             * 当天有任务的稽查人员
             */
            String allUser = " SELECT count(1) SM FROM (SELECT " +
                    "    count(1) " +
                    "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                    "  WHERE  u.USERDELETE = 1  and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
            /**
             * 现场稽查未开始
             */
            String xcJcWks = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 0  and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time ";
            /**
             * 现场稽查进行中
             */
            String xcJcJxz = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 1  and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time";
            /**
             *现场稽查已完成
             */
            String xcJcYwc = "SELECT count(1)  " +
                    "FROM CHECK_LIVE_TASK " +
                    "WHERE STATUS = 2  and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') <plan_end_time";
//                module6Data.put("wsx", 0);
            String sql = "SELECT " +
                    "(" + qjcZxUser + ") as zx," +
                    "(" + qjcLxUser + ") as lx," +
                    "(" + allUser + ") as alluser," +
                    "(" + xcJcJxz + ") as xcJcJxz," +
                    "(" + xcJcWks + ") as xcJcWks," +
                    "(" + xcJcYwc + ") as xcJcYwc " +
                    "  FROM dual";
            Map<String, Object> maps = this.execSqlSingleResult(sql);
            message.put("data", maps);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 30000)
    public void module6() {
        Map<String, HashMap> sendMsg = wuDServerEndpoint.sendMsg();
        sendMsg.forEach((sessionId, session) -> {
            try {
                Map message = new HashMap<String, Object>();
                message.put("module", 6);
                /**
                 * 前台稽查在线人员
                 */
                String qjcZxUser = " SELECT count(1) SM FROM (SELECT " +
                        "    count(1) " +
                        "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                        "  WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1  and k.status in (1,2) and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
                /**
                 * 前台稽查离线人员
                 */
                String qjcLxUser = " SELECT count(1) SM FROM (SELECT " +
                        "    count(1) " +
                        "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                        "  WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1  and k.status in (1,2) and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
                /**
                 * 当天有任务的稽查人员
                 */
                String allUser = " SELECT count(1) SM FROM (SELECT " +
                        "    count(1) " +
                        "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                        "  WHERE  u.USERDELETE = 1  and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
                /**
                 * 现场稽查未开始
                 */
                String xcJcWks = "SELECT count(1)  " +
                        "FROM CHECK_LIVE_TASK " +
                        "WHERE STATUS = 0 and check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time ";
                /**
                 * 现场稽查进行中
                 */
                String xcJcJxz = "SELECT count(1)  " +
                        "FROM CHECK_LIVE_TASK " +
                        "WHERE STATUS = 1 and check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time";
                /**
                 *现场稽查已完成
                 */
                String xcJcYwc = "SELECT count(1)  " +
                        "FROM CHECK_LIVE_TASK " +
                        "WHERE STATUS = 2 and check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') <plan_end_time";
//                module6Data.put("wsx", 0);
                String sql = "SELECT " +
                        "(" + qjcZxUser + ") as zx," +
                        "(" + qjcLxUser + ") as lx," +
                        "(" + allUser + ") as alluser," +
                        "(" + xcJcJxz + ") as xcJcJxz," +
                        "(" + xcJcWks + ") as xcJcWks," +
                        "(" + xcJcYwc + ") as xcJcYwc " +
                        "  FROM dual";
                Map<String, Object> maps = this.execSqlSingleResult(sql);
                message.put("data", maps);
                wuDServerEndpoint.sendText((Session) session.get("session"), message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void module7(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 7);
        String module7 = "select count(1) total from MONITOR_CHECK_YJ where CREATE_TIME > trunc(sysdate) and status = 0";
        Map<String, Object> map1 = this.execSqlSingleResult(module7);
        message.put("data", map1);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module7() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Map message = new HashMap<String, Object>();
            message.put("module", 7);
            String module7 = "select count(1) total from MONITOR_CHECK_YJ where CREATE_TIME > trunc(sysdate) and status = 0";
            Map<String, Object> map1 = this.execSqlSingleResult(module7);
            message.put("data", map1);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module8(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 8);
        String module8 = "select count(1) total from WARNING_ONE_KEY where CREATE_TIME > trunc(sysdate)";
        Map<String, Object> map1 = this.execSqlSingleResult(module8);
        message.put("data", map1);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module8() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Map message = new HashMap<String, Object>();
        message.put("module", 8);
        String module8 = "select count(1) total from WARNING_ONE_KEY where CREATE_TIME > trunc(sysdate)";
        Map<String, Object> map1 = this.execSqlSingleResult(module8);
        message.put("data", map1);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module9(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 9);
        // 巡视超速  看护脱岗
        //巡视告警
        String xsgj = "SELECT  nvl(sum(decode(j.TASK_TYPE, 1, 1, 0)),0) xsgj FROM MONITOR_CHECK_YJ j\n" +
                "   LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID\n" +
                "   LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID\n" +
                "where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1  AND xs.IS_DELETE=0 AND j.CREATE_TIME >= trunc(sysdate)";
        //看护告警
        String khgj = "SELECT  nvl(sum(decode(j.TASK_TYPE, 2, 1, 0)),0) khgj  FROM MONITOR_CHECK_YJ j\n" +
                "    LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID\n" +
                "   LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1  AND j.CREATE_TIME >= trunc(sysdate)\n";
        //稽查告警
        String jcgj = "SELECT   nvl(sum(decode(j.TASK_TYPE, 3, 1, 0)),0) xcjc FROM MONITOR_CHECK_YJ j " +
                "     LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID " +
                "     LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1  AND j.CREATE_TIME >= trunc(sysdate)";
        String module9 = "SELECT " +
                "(" + xsgj + ") as xsgj, " +
                "(" + khgj + ") as khgj," +
                "(" + jcgj + ") as xcjc" +
                "  FROM dual";
        /*String module9 = "SELECT\n" +
                "  nvl(sum(decode(tt.TASK_TYPE, 1, 1, 0)),0) xsgj,\n" +
                "  nvl(sum(decode(tt.TASK_TYPE, 2, 1, 0)),0) khgj,\n" +
                "  nvl(sum(decode(tt.TASK_TYPE, 3, 1, 0)),0) xcjc,\n" +
                "  0                             yhgj\n" +
                "FROM MONITOR_CHECK_YJ tt where CREATE_TIME >= trunc(sysdate) and STATUS = 0";*/
        Map<String, Object> map1 = this.execSqlSingleResult(module9);
        String module9Detail = "SELECT t.*,tt.DESCRIPTION from (SELECT\n" +
                "  t.WARNING_TYPE,\n" +
                "  t.REASON,\n" +
                "  t.TASK_NAME,\n" +
                "  t.USER_ID,\n" +
                "  t.TASK_TYPE\n" +
                "FROM MONITOR_CHECK_EJ t\n" +
                "WHERE t.id = (SELECT max(tt.ID)\n" +
                "              FROM MONITOR_CHECK_EJ tt\n" +
                "              WHERE tt.TASK_TYPE = t.TASK_TYPE AND tt.STATUS = 0 AND tt.CREATE_TIME >= trunc(sysdate))) t join WARNING_TYPE tt on t.WARNING_TYPE = tt.WARNING_TYPE ";
        List<Map<String, Object>> detail = this.execSql(module9Detail);
        for (Map<String, Object> obj1 : detail) {
            try {
                Integer task_type = Integer.parseInt(obj1.get("TASK_TYPE").toString());
                Object description = obj1.get("DESCRIPTION");
                if (task_type == 1) {
                    map1.put("xsgjDetail", description);
                } else if (task_type == 2) {
                    map1.put("khgjDetail", description);
                } else if (task_type == 3) {
                    map1.put("xcjcDetail", description);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }
        }

        message.put("data", map1);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module9() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Map message = new HashMap<String, Object>();
        message.put("module", 9);
        String xsgj = "SELECT  nvl(sum(decode(j.TASK_TYPE, 1, 1, 0)),0) xsgj FROM MONITOR_CHECK_YJ j\n" +
                "   LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID\n" +
                "   LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID\n" +
                "where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1  AND xs.IS_DELETE=0 AND j.CREATE_TIME >= trunc(sysdate)";
        //看护告警
        String khgj = "SELECT  nvl(sum(decode(j.TASK_TYPE, 2, 1, 0)),0) khgj  FROM MONITOR_CHECK_YJ j\n" +
                "    LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID\n" +
                "   LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1  AND j.CREATE_TIME >= trunc(sysdate)\n";
        //稽查告警
        String jcgj = "SELECT   nvl(sum(decode(j.TASK_TYPE, 3, 1, 0)),0) xcjc FROM MONITOR_CHECK_YJ j\n" +
                "     LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID\n" +
                "     LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1  AND j.CREATE_TIME >= trunc(sysdate)\n";
        String module9 = "SELECT " +
                "(" + xsgj + ") as xsgj, " +
                "(" + khgj + ") as khgj," +
                "(" + jcgj + ") as xcjc" +
                "  FROM dual";
        Map<String, Object> map1 = this.execSqlSingleResult(module9);
        map1.put("xsgjDetail", "暂无");
        map1.put("khgjDetail", "暂无");
        map1.put("xcjcDetail", "暂无");
        String module9Detail = "SELECT t.*,tt.DESCRIPTION from (SELECT\n" +
                "  t.WARNING_TYPE,\n" +
                "  t.REASON,\n" +
                "  t.TASK_NAME,\n" +
                "  t.USER_ID,\n" +
                "  t.TASK_TYPE\n" +
                "FROM MONITOR_CHECK_EJ t\n" +
                "WHERE t.id = (SELECT max(tt.ID)\n" +
                "              FROM MONITOR_CHECK_EJ tt\n" +
                "              WHERE tt.TASK_TYPE = t.TASK_TYPE AND tt.STATUS = 0 AND tt.CREATE_TIME >= trunc(sysdate))) t join WARNING_TYPE tt on t.WARNING_TYPE = tt.WARNING_TYPE ";
        List<Map<String, Object>> detail = this.execSql(module9Detail);
        for (Map<String, Object> obj1 : detail) {
            try {
                Integer task_type = Integer.parseInt(obj1.get("TASK_TYPE").toString());
                Object description = obj1.get("DESCRIPTION");
                if (task_type == 1) {
                    map1.put("xsgjDetail", description);
                } else if (task_type == 2) {
                    map1.put("khgjDetail", description);
                } else if (task_type == 3) {
                    map1.put("xcjcDetail", description);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                continue;
            }
        }
        message.put("data", map1);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }


    public void module10(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Object deptid = session.get("DEPTID");
        Map message = new HashMap<String, Object>();
        message.put("module", 10);
        /*String module7 = "SELECT\n" +
                "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'xssh%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)),0) xssh,\n" +
                "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'wtsh%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)),0) wtsh,\n" +
                "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'defect%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)),0) defect,\n" +
                "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'jcsh%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)),0) jcsh\n" +
                "FROM ACT_RU_TASK\n" +
                "WHERE CREATE_TIME_ > trunc(sysdate)";*/
//        String yh = " SELECT\n" +
//                "  count(1) as yh\n" +
//                "    FROM ACT_HI_ACTINST t LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND  h.NAME_ = 'YHID'\n" +
//                "   LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
//                "    WHERE  t.PROC_DEF_ID_ LIKE 'wtsh%'  AND t.END_TIME_ IS NOT  NULL AND y.ID IS NOT NULL AND trunc(END_TIME_)=trunc(sysdate) AND ASSIGNEE_ = 'sdid' AND YWORG_ID ='" + deptid + "'";
        String yh = " SELECT\n" +
                "  (SELECT\n" +
                "     count(1)\n" +
                "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND  h.NAME_ = 'YHID'\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE  t.PROC_DEF_ID_ LIKE 'wtsh%'  AND ASSIGNEE_ = 'jkid'  AND t.END_TIME_ IS NOT  NULL AND y.ID IS NOT NULL\n" +
                "          ) +\n" +
                "  (SELECT count(1)\n" +
                "   FROM ACT_RU_TASK t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND y.ID IS NOT NULL   AND ASSIGNEE_ = 'jkid'\n" +
                "         ) AS task\n" +
                "  FROM dual ";
        String xs = " SELECT count(1) as xs\n" +
                "FROM ACT_HI_ACTINST t\n" +
                "  LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND h.NAME_ = 'XSID'\n" +
                "  LEFT JOIN XS_ZC_CYCLE_RECORD x ON x.XS_ZC_CYCLE_ID = h.TEXT_ LEFT JOIN RZTSYSUSER r ON x.CM_USER_ID = r.ID\n" +
                "WHERE t.PROC_DEF_ID_ LIKE 'xssh%' AND t.ASSIGNEE_ = 'sdid' AND t.END_TIME_ IS NOT NULL AND r.DEPTID = '" + deptid + "' \n" +
                "ORDER BY t.END_TIME_ DESC ";
        Map<String, Object> stringObjectMap = this.execSqlSingleResult(yh);
        Map<String, Object> stringObjectMap1 = this.execSqlSingleResult(xs);
        stringObjectMap.putAll(stringObjectMap1);
        stringObjectMap.put("qx", 0);
        stringObjectMap.put("jc", 0);
        message.put("data", stringObjectMap);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module10() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Map message = new HashMap<String, Object>();
            message.put("module", 10);
            String module7 = "SELECT\n" +
                    "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'xssh%'\n" +
                    "    THEN 1\n" +
                    "   ELSE 0 END)),0) xssh,\n" +
                    "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'wtsh%'\n" +
                    "    THEN 1\n" +
                    "   ELSE 0 END)),0) wtsh,\n" +
                    "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'defect%'\n" +
                    "    THEN 1\n" +
                    "   ELSE 0 END)),0) defect,\n" +
                    "  nvl(sum((CASE WHEN PROC_DEF_ID_ LIKE 'jcsh%'\n" +
                    "    THEN 1\n" +
                    "   ELSE 0 END)),0) jcsh\n" +
                    "FROM ACT_RU_TASK\n" +
                    "WHERE CREATE_TIME_ > trunc(sysdate)";
            Map<String, Object> map1 = this.execSqlSingleResult(module7);
            message.put("data", map1);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    @Scheduled(fixedRate = 30000)
    public void module11() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Map message = new HashMap<String, Object>();
        message.put("module", 11);
        String module11 = "select * from TIMED_CONFIG where id LIKE 'TIME_CONFIG'";
        Map<String, Object> timeConfig = this.execSqlSingleResult(module11);
        String newTime = "SELECT THREEDAY,CREATETIME FROM (SELECT * FROM TIMED_TASK where THREEDAY=? ORDER BY CREATETIME DESC ) WHERE rownum =1 ";
        Map<String, Object> towHour = this.execSqlSingleResult(newTime, 0);
        Map<String, Object> threeDay = this.execSqlSingleResult(newTime, 1);
        Map<Object, Object> returnMap = new HashMap<>();
        returnMap.put("dqsj", DateUtil.getWebsiteDatetime());
        Date yjcreatetime = DateUtil.parseDate(threeDay.get("CREATETIME").toString());
        returnMap.put("xcsjyj", DateUtil.addDate(yjcreatetime, 72));
        returnMap.put("yjjg", "72小时/次");
        message.put("data", returnMap);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module11(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 11);
        String module11 = "select * from TIMED_CONFIG where id LIKE 'TIME_CONFIG'";
        Map<String, Object> timeConfig = this.execSqlSingleResult(module11);
        String newTime = "SELECT THREEDAY,CREATETIME FROM (SELECT * FROM TIMED_TASK where THREEDAY=? ORDER BY CREATETIME DESC ) WHERE rownum =1 ";
        Map<String, Object> towHour = this.execSqlSingleResult(newTime, 0);
        Map<String, Object> threeDay = this.execSqlSingleResult(newTime, 1);
        Map<Object, Object> returnMap = new HashMap<>();
        returnMap.put("dqsj", DateUtil.getWebsiteDatetime());
        Date yjcreatetime = DateUtil.parseDate(threeDay.get("CREATETIME").toString());
//        Date ercreatetime = DateUtil.parseDate(towHour.get("CREATETIME").toString());
        returnMap.put("xcsjyj", DateUtil.addDate(yjcreatetime, 72));
        returnMap.put("yjjg", "72小时/次");
        /*if (ercreatetime.getTime() >= DateUtil.getScheduleTime(timeConfig.get("START_TIME").toString())) {
            returnMap.put("xcsjej", DateUtil.addDate(ercreatetime, Double.parseDouble(timeConfig.get("DAY_ZQ").toString())));
            returnMap.put("ejjg", timeConfig.get("DAY_ZQ") + "小时/次");
        } else if (ercreatetime.getTime() <= DateUtil.getScheduleTime(timeConfig.get("END_TIME").toString())) {
            returnMap.put("xcsjej", DateUtil.addDate(ercreatetime, Double.parseDouble(timeConfig.get("NIGHT_ZQ").toString())));
            returnMap.put("ejjg", timeConfig.get("NIGHT_ZQ") + "小时/次");
        }*/
        message.put("data", returnMap);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    public static void main(String[] args) {
        String a = "SELECT\n" +
                "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'xssh%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)) xssh,\n" +
                "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'wtsh%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)) wtsh,\n" +
                "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'defect%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)) defect,\n" +
                "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'jcsh%'\n" +
                "    THEN 1\n" +
                "   ELSE 0 END)) jcsh\n" +
                "FROM ACT_RU_TASK\n" +
                "WHERE CREATE_TIME_ > trunc(sysdate)";
        System.out.println(a);
    }
}
