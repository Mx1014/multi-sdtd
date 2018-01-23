package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.YiJiServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class YiJiPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    YiJiServerEndpoint wuDServerEndpoint;

    @Scheduled(fixedRate = 30000)
    public void module1() {
        Map<String,Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        map.forEach((sessionId, session) -> {
            module1Method(allMap, session);
        });
    }



    public void module1Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap<String,Object>();
        Integer roletype = Integer.parseInt(session.get("ROLETYPE").toString());
        String deptId = session.get("DEPTID").toString();
        String module1 = "SELECT\n" +
                "  ttt.DEPTNAME,\n" +
                "  sum(decode(STATUS, 0, 1, 0)) wcl,\n" +
                "  sum(decode(STATUS, 1, 1, 0)) clz,\n" +
                "  sum(decode(STATUS, 2, 1, 0)) ycl,\n" +
                "  count(1)                     total,ttt.deptsort\n" +
                "FROM (SELECT\n" +
                "        STATUS,\n" +
                "        DEPTID,\n" +
                "        USER_ID\n" +
                "      FROM MONITOR_CHECK_EJ\n" +
                "      WHERE CREATE_TIME > trunc(sysdate) and DEPTID = " + deptId + ") t\n" +
                "  JOIN rztsysuser tt ON t.USER_ID = tt.id join RZTSYSDEPARTMENT ttt on ttt.id = tt.CLASSNAME group by ttt.DEPTNAME,ttt.deptsort order by ttt.deptsort";
        if(roletype == 0) {
            deptId = "admin";
            module1 = "SELECT\n" +
                    "  tt.DEPTNAME,\n" +
                    "  sum(decode(STATUS, 0, 1, 0)) wcl,\n" +
                    "  sum(decode(STATUS, 1, 1, 0)) clz,\n" +
                    "  sum(decode(STATUS, 2, 1, 0)) ycl,\n" +
                    "  count(1)                     total,tt.deptsort\n" +
                    "FROM (SELECT\n" +
                    "        STATUS,\n" +
                    "        DEPTID\n" +
                    "      FROM MONITOR_CHECK_EJ\n" +
                    "      WHERE CREATE_TIME > trunc(sysdate)) t\n" +
                    "  JOIN RZTSYSDEPARTMENT tt ON t.DEPTID = tt.id group by tt.DEPTNAME,tt.deptsort order by tt.deptsort";
        }
        if(allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                List<Map<String, Object>> resList = this.execSql(module1);
                message.put("module",1);
                message.put("data",resList);
                allMap.put(deptId,message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module2() throws Exception {
        Map<String,Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module2Method(allMap, session);
        });

    }

    public void module2Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap<String,Object>();
        Integer roletype = Integer.parseInt(session.get("ROLETYPE").toString());
        String deptId;
        String module2;
        if(roletype == 0) {
            deptId = "admin";
            module2 = "select sum(decode(STATUS,1,1,0)) ywc,count(1) total from TIMED_TASK where CREATETIME > trunc(sysdate)";
        } else {
            deptId = session.get("DEPTID").toString();
            module2 = "select sum(decode(t.STATUS,1,1,0)) ywc,count(1) total from TIMED_TASK t join RZTSYSUSER tt on t.USER_ID = tt.id and tt.DEPTID = " + deptId + " and t.CREATETIME > trunc(sysdate)";
        }
        if(allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module2);
                message.put("data",res);
                message.put("module",2);
                allMap.put(deptId,message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module3() throws Exception {
        Map<String,Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module3Method(allMap, session);
        });

    }

    public void module3Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap();
        Integer roletype = Integer.parseInt(session.get("ROLETYPE").toString());
        String deptId;
        String module3;
        if(roletype == 0) {
            deptId = "admin";
            module3 = "SELECT sum(decode(QUESTION_TYPE,1,1,0)) a,sum(decode(QUESTION_TYPE,2,1,0)) b,sum(decode(QUESTION_TYPE,3,1,0)) c,sum(decode(QUESTION_TYPE,4,1,0)) d from CHECK_RESULT";
        } else {
            deptId = session.get("DEPTID").toString();
            module3 = "SELECT\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 1, 1, 0)),0) a,\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 2, 1, 0)),0) b,\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 3, 1, 0)),0) c,\n" +
                    "  nvl(sum(decode(t.QUESTION_TYPE, 4, 1, 0)),0) d\n" +
                    "FROM CHECK_RESULT t\n" +
                    "  JOIN CHECK_DETAIL tt ON t.CHECK_DETAIL_ID = tt.id join RZTSYSUSER ttt on tt.QUESTION_USER_ID = ttt.id and DEPTID = ?";
        }
        if(allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module3);
                message.put("module",3);
                message.put("data",res);
                allMap.put(deptId,message);
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
        if (roletype == 0) {
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
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String,Object>();
        message.put("module",5);
        Map module5Data = new HashMap();
        module5Data.put("zx",0);
        module5Data.put("lx",0);
        module5Data.put("wsx",0);
        message.put("data",module5Data);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module5() {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if(map.size() == 0) {
            return;
        }
        Map message = new HashMap<String,Object>();
        message.put("module",5);
        Map module5Data = new HashMap();
        module5Data.put("zx",0);
        module5Data.put("lx",0);
        module5Data.put("wsx",0);
        message.put("data",module5Data);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String,HashMap> entry:entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module6(String sessionId) {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String,Object>();
        message.put("module",6);
        Map module6Data = new HashMap();
        module6Data.put("zx",0);
        module6Data.put("lx",0);
        module6Data.put("wsx",0);
        message.put("data",module6Data);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module6() {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if(map.size() == 0) {
            return;
        }
        Map message = new HashMap<String, Object>();
        message.put("module", 6);
        Map module6Data = new HashMap();
        module6Data.put("zx", 0);
        module6Data.put("lx", 0);
        module6Data.put("wsx", 0);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String,HashMap> entry:entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            message.put("data", module6Data);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module7(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String,Object>();
        message.put("module",7);
        String module7 = "select count(1) total from MONITOR_CHECK_YJ where CREATE_TIME > trunc(sysdate)";
        Map<String, Object> map1 = this.execSqlSingleResult(module7);
        message.put("data",map1);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }
    @Scheduled(fixedRate = 30000)
    public void module7() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if(map.size() == 0) {
            return;
        }
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry: entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Map message = new HashMap<String,Object>();
            message.put("module",7);
            String module7 = "select count(1) total from MONITOR_CHECK_YJ where CREATE_TIME > trunc(sysdate)";
            Map<String, Object> map1 = this.execSqlSingleResult(module7);
            message.put("data",map1);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module8(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String,Object>();
        message.put("module",8);
        String module8 = "select count(1) total from WARNING_ONE_KEY where CREATE_TIME > trunc(sysdate)";
        Map<String, Object> map1 = this.execSqlSingleResult(module8);
        message.put("data",map1);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module8() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if(map.size() == 0) {
            return;
        }
        Map message = new HashMap<String,Object>();
        message.put("module",8);
        String module8 = "select count(1) total from WARNING_ONE_KEY where CREATE_TIME > trunc(sysdate)";
        Map<String, Object> map1 = this.execSqlSingleResult(module8);
        message.put("data",map1);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry:entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module9(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String,Object>();
        message.put("module",9);
        String module9 = "select sum(decode(WARNING_TYPE,5,1,0)) xsgj,sum(decode(WARNING_TYPE,7,1,0)) khgj,0 xcjc,0 yhgj  from MONITOR_CHECK_EJ";
        Map<String, Object> map1 = this.execSqlSingleResult(module9);
        message.put("data",map1);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module9() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if(map.size() == 0) {
            return;
        }
        Map message = new HashMap<String,Object>();
        message.put("module",9);
        String module9 = "select sum(decode(WARNING_TYPE,5,1,0)) xsgj,sum(decode(WARNING_TYPE,7,1,0)) khgj,0 xcjc,0 yhgj  from MONITOR_CHECK_EJ";
        Map<String, Object> map1 = this.execSqlSingleResult(module9);
        message.put("data",map1);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry:entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }


    public void module10(String sessionId) throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String,Object>();
        message.put("module",10);
        String module7 = "SELECT\n" +
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
        Map<String, Object> map1 = this.execSqlSingleResult(module7);
        message.put("data",map1);
        wuDServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module10() throws Exception {
        Map<String, HashMap> map = wuDServerEndpoint.sendMsg();
        if(map.size() == 0) {
            return;
        }
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry:entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Map message = new HashMap<String,Object>();
            message.put("module",10);
            String module7 = "SELECT\n" +
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
            Map<String, Object> map1 = this.execSqlSingleResult(module7);
            message.put("data",map1);
            wuDServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    /*public void module22() throws Exception {
        String timed = "select START_TIME,END_TIME,DAY_ZQ,NIGHT_ZQ from TIMED_CONFIG where id = 'TIME_CONFIG'";
        Map<String, Object> timedMap = this.execSqlSingleResult(timed);
        int start_time = Integer.parseInt(timedMap.get("START_TIME").toString());
        int end_time = Integer.parseInt(timedMap.get("END_TIME").toString());
        int day_zq = Integer.parseInt(timedMap.get("DAY_ZQ").toString());
        int night_zq = Integer.parseInt(timedMap.get("NIGHT_ZQ").toString());
        Date date = new Date();
        Date start = DateUtils.setHours(date, start_time);
        Calendar calendar = Calendar.getInstance();
        Date time = calendar.getTime();
//        if(date.before(start)) {
//            while(true) {
//                int hour = start_time;
//                if(date.after(DateUtils.setHours(date,hour -= night_zq))) {
//
//                }
//            }
//        } else if(time > time1) {
//            while() {
//
//            }
//        }
//        for(int i = start_time;i<=night_zq;i+=day_zq ) {
//            Date date1 = DateUtils.setHours(date, i);
//            if() {
//
//            }
//        }
        String s = timedMap.get("START_TIME").toString();


    }*/
}
