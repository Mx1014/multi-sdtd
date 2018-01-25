package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.ErJiServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.websocket.Session;
import java.util.*;

public class ErJiPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    ErJiServerEndpoint erJiServerEndpoint;


    @Scheduled(fixedRate = 30000)
    public void module2() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module2Method(allMap, session);
        });

    }

    public void module2Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap<String, Object>();
        String deptId;
        String module2;
        deptId = session.get("DEPTID").toString();
        module2 = "select sum(decode(t.STATUS,1,1,0)) ywc,count(1) total from TIMED_TASK t join RZTSYSUSER tt on t.USER_ID = tt.id and tt.DEPTID = '" + deptId + "' and t.CREATETIME > trunc(sysdate)";
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
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module3() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module3Method(allMap, session);
        });

    }

    public void module3Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap();
        String deptId;
        String module3;
        deptId = session.get("DEPTID").toString();
        module3 = "SELECT\n" +
                "  nvl(sum(decode(t.QUESTION_TYPE, 1, 1, 0)),0) a,\n" +
                "  nvl(sum(decode(t.QUESTION_TYPE, 2, 1, 0)),0) b,\n" +
                "  nvl(sum(decode(t.QUESTION_TYPE, 3, 1, 0)),0) c,\n" +
                "  nvl(sum(decode(t.QUESTION_TYPE, 4, 1, 0)),0) d\n" +
                "FROM CHECK_RESULT t\n" +
                "  JOIN CHECK_DETAIL tt ON t.CHECK_DETAIL_ID = tt.id join RZTSYSUSER ttt on tt.QUESTION_USER_ID = ttt.id and DEPTID = '" + deptId + "'";
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
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module4_1() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module4_1Method(allMap, session);
        });

    }

    public void module4_1Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap();
        String deptId;
        String module4_1;
        deptId = session.get("DEPTID").toString();
        module4_1 = " SELECT (SELECT count(1)\n" +
                "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND t.END_TIME_ IS NOT NULL AND y.YWORG_ID='" + deptId + "' " +
                "  ) AS wsh,\n" +
                "  (SELECT count(1)\n" +
                "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND t.END_TIME_ IS NULL AND y.YWORG_ID='" + deptId + "' " +
                "  )    ysh\n" +
                "FROM dual ";
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module4_1);
                message.put("module", 4_1);
                message.put("data", res);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module4_2() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module4_2Method(allMap, session);
        });

    }

    public void module4_2Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap();
        String deptId;
        String module4_2;
        deptId = session.get("DEPTID").toString();
        module4_2 = " SELECT count(1)\n" +
                "FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
                "               LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "WHERE  h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND y.YWORG_ID = '" + deptId + "' ";
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module4_2);
                message.put("module", 4_2);
                message.put("data", res);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module5_1() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module5_1Method(allMap, session);
        });

    }

    public void module5_1Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap();
        String deptId;
        String module5_1;
        deptId = session.get("DEPTID").toString();
        module5_1 = " SELECT\n" +
                "  nvl(sum(decode(STATUS, 0, 1, 0)), 0) AS wcl,\n" +
                "  nvl(sum(decode(STATUS, 1, 1, 0)), 0) AS clz,\n" +
                "  nvl(sum(decode(STATUS, 2, 1, 0)), 0) AS ycl\n" +
                "FROM MONITOR_CHECK_EJ WHERE trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = '" + deptId + "' ";
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module5_1);
                message.put("module", 5_1);
                message.put("data", res);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module5_2() throws Exception {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        map.forEach((String sessionId, HashMap session) -> {
            module5_2Method(allMap, session);
        });

    }

    public void module5_2Method(Map<String, Map> allMap, HashMap session) {
        Map message = new HashMap();
        String deptId;
        String module5_2;
        deptId = session.get("DEPTID").toString();
        module5_2 = " SELECT nvl(sum(decode(TASK_TYPE, 1, 1, 0)), 0) xs,nvl(sum(decode(TASK_TYPE, 2, 1, 0)), 0) kh,nvl(sum(decode(TASK_TYPE, 3, 1, 0)), 0) jc FROM MONITOR_CHECK_EJ WHERE trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = '" + deptId + "' ";
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module5_2);
                message.put("module", 5_2);
                message.put("data", res);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    public void module7(String sessionId) throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 7);
        String module7 = "select count(1) total from MONITOR_CHECK_YJ where CREATE_TIME > trunc(sysdate)";
        Map<String, Object> map1 = this.execSqlSingleResult(module7);
        message.put("data", map1);
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module7() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Map message = new HashMap<String, Object>();
            message.put("module", 7);
            String module7 = "select count(1) total from MONITOR_CHECK_YJ where CREATE_TIME > trunc(sysdate)";
            Map<String, Object> map1 = this.execSqlSingleResult(module7);
            message.put("data", map1);
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module8(String sessionId) throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 8);
        String module8 = "select count(1) total from WARNING_ONE_KEY where CREATE_TIME > trunc(sysdate)";
        Map<String, Object> map1 = this.execSqlSingleResult(module8);
        message.put("data", map1);
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module8() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
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
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module9(String sessionId) throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 9);
        String module9 = "select sum(decode(WARNING_TYPE,5,1,0)) xsgj,sum(decode(WARNING_TYPE,7,1,0)) khgj,0 xcjc,0 yhgj  from MONITOR_CHECK_EJ";
        Map<String, Object> map1 = this.execSqlSingleResult(module9);
        message.put("data", map1);
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module9() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Map message = new HashMap<String, Object>();
        message.put("module", 9);
        String module9 = "select sum(decode(WARNING_TYPE,5,1,0)) xsgj,sum(decode(WARNING_TYPE,7,1,0)) khgj,0 xcjc,0 yhgj  from MONITOR_CHECK_EJ";
        Map<String, Object> map1 = this.execSqlSingleResult(module9);
        message.put("data", map1);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }


    public void module10(String sessionId) throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        HashMap session = map.get(sessionId);
        Map message = new HashMap<String, Object>();
        message.put("module", 10);
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
        message.put("data", map1);
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedRate = 30000)
    public void module10() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
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
            message.put("data", map1);
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }
}
