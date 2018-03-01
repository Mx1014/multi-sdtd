package com.rzt.websocket.service;

import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.util.DateUtil;
import com.rzt.websocket.serverendpoint.ErJiServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.*;

@Service
public class ErJiPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    ErJiServerEndpoint erJiServerEndpoint;


    @Scheduled(fixedDelay = 30000)
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
        module2 = "select nvl(sum(decode(t.STATUS, 1, 1, 0)),0) ywc,count(1) total from TIMED_TASK t join RZTSYSUSER tt on t.USER_ID = tt.id and tt.DEPTID = '" + deptId + "' and t.CREATETIME > trunc(sysdate)";
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

    @Scheduled(fixedDelay = 30000)
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
                "  JOIN CHECK_DETAIL tt ON t.CHECK_DETAIL_ID = tt.id join RZTSYSUSER ttt on tt.QUESTION_USER_ID = ttt.id and trunc(t.CREATE_TIME) = trunc(sysdate) and DEPTID = '" + deptId + "'";
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

    @Scheduled(fixedDelay = 30000)
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
//        module4_1 = " SELECT (SELECT count(1)\n" +
//                "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
//                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
//                "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND t.END_TIME_ IS NOT NULL AND trunc(END_TIME_) = trunc(sysdate) AND y.YWORG_ID='" + deptId + "' " +
//                "  ) AS wsh,\n" +
//                "  (SELECT count(1)\n" +
//                "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
//                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
//                "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND t.END_TIME_ IS NULL  AND trunc(END_TIME_) = trunc(sysdate) AND y.YWORG_ID='" + deptId + "' " +
//                "  )    ysh\n" +
//                "FROM dual ";
        module4_1 = " SELECT\n" +
                "  (SELECT count(1)\n" +
                "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND h.NAME_ = 'YHID'\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND t.END_TIME_ IS NOT NULL AND y.ID IS NOT NULL\n" +
                "         AND y.YWORG_ID = '" + deptId + "')  AS ysh,\n" +
                "  (SELECT count(1)\n" +
                "   FROM ACT_RU_TASK t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND y.ID IS NOT NULL AND ASSIGNEE_ = 'sdid'\n" +
                "         AND y.YWORG_ID = '" + deptId + "') AS  wsh\n" +
                "FROM dual ";
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module4_1);
                message.put("module", 41);
                message.put("data", res);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedDelay = 30000)
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
//        module4_2 = " SELECT count(1) as yh\n" +
//                "FROM ACT_HI_ACTINST t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
//                "               LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
//                "WHERE  h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND trunc(END_TIME_) = trunc(sysdate) AND  y.YWORG_ID = '" + deptId + "' ";
        module4_2 = " SELECT\n" +
                "  (SELECT\n" +
                "     count(1)\n" +
                "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND  h.NAME_ = 'YHID'\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE  t.PROC_DEF_ID_ LIKE 'wtsh%'  AND ASSIGNEE_ = 'sdid'  AND t.END_TIME_ IS NOT  NULL AND y.ID IS NOT NULL\n" +
                "          AND y.YWORG_ID = '" + deptId + "') +\n" +
                "  (SELECT count(1)\n" +
                "   FROM ACT_RU_TASK t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
                "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND y.ID IS NOT NULL   AND ASSIGNEE_ = 'sdid'\n" +
                "         AND y.YWORG_ID = '" + deptId + "') AS yh\n" +
                "  FROM dual ";
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module4_2);
                HashMap map = new HashMap();
                map.put("YH", res.get("YH"));
                map.put("QX", 0);
                map.put("JC", 0);
                message.put("module", 42);
                message.put("data", map);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedDelay = 30000)
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
        /*module5_1 = " SELECT\n" +
                "  nvl(sum(decode(STATUS, 0, 1, 0)), 0) AS wcl,\n" +
                "  nvl(sum(decode(STATUS, 1, 1, 0)), 0) AS clz,\n" +
                "  nvl(sum(decode(STATUS, 2, 1, 0)), 0) AS ycl\n" +
                "FROM MONITOR_CHECK_EJ WHERE trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = '" + deptId + "' ";*/
        module5_1 = " SELECT\n" +
                "  nvl(sum(decode(STATUS, 0, 1, 0)), 0) AS wcl,\n" +
                "  nvl(sum(decode(STATUS, 1, 1, 0)), 0) AS clz,\n" +
                "  nvl(sum(decode(STATUS, 2, 1, 0)), 0) AS ycl\n" +
                "FROM MONITOR_CHECK_EJ\n" +
                "WHERE (TASK_TYPE = 1 OR TASK_TYPE = 2 OR TASK_TYPE = 3 OR TASK_TYPE = 4 OR TASK_TYPE = 5 OR TASK_TYPE = 6 OR\n" +
                "       TASK_TYPE = 7 OR TASK_TYPE = 8 OR TASK_TYPE = 10 OR TASK_TYPE = 11 OR TASK_TYPE = 12 OR TASK_TYPE = 13 OR\n" +
                "       TASK_TYPE = 14) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = '" + deptId + "'  ";
        if (allMap.containsKey(deptId)) {
            message = allMap.get(deptId);
        } else {
            try {
                Map<String, Object> res = this.execSqlSingleResult(module5_1);
                message.put("module", 51);
                message.put("data", res);
                allMap.put(deptId, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }

    @Scheduled(fixedDelay = 30000)
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
//        module5_2 = " SELECT nvl(sum(decode(TASK_TYPE, 1, 1, 0)), 0) xs,nvl(sum(decode(TASK_TYPE, 2, 1, 0)), 0) kh,nvl(sum(decode(TASK_TYPE, 3, 1, 0)), 0) jc,0 as yh,0 as tf FROM MONITOR_CHECK_EJ WHERE trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = '" + deptId + "' ";
        module5_2 = " SELECT\n" +
                "  nvl(sum(decode(TASK_TYPE, 1, 1, 2, 1, 3, 1, 4, 1, 5, 1, 0)), 0) xs,\n" +
                "  nvl(sum(decode(TASK_TYPE, 6, 1, 7, 1, 8, 1, 10, 1, 11, 1, 0)), 0) kh,\n" +
                "  nvl(sum(decode(TASK_TYPE, 12, 1, 13, 1, 14, 1, 0)), 0) jc,\n" +
                "  0 AS                                    yh,\n" +
                "  0 AS                                    tf\n" +
                "FROM MONITOR_CHECK_EJ\n" +
                "WHERE trunc(CREATE_TIME) = trunc(sysdate)\n" +
                "      AND DEPTID =  '" + deptId + "' ";
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

    @Scheduled(fixedDelay = 30000)
    public void module7() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Object deptid = session.get("DEPTID");
            Map message = new HashMap<String, Object>();
            message.put("module", 7);
            String module7 = "select count(1) total from MONITOR_CHECK_ej where CREATE_TIME >= trunc(sysdate) AND DEPTID='" + deptid + "' and status = 0";
            Map<String, Object> map1 = this.execSqlSingleResult(module7);
            message.put("data", map1);
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void module8() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }

        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Object deptid = session.get("DEPTID");
            Map message = new HashMap<String, Object>();
            message.put("module", 8);
            String module8 = "select count(1) total from WARNING_ONE_KEY l LEFT JOIN RZTSYSUSER r ON l.USER_ID = r.ID where CREATE_TIME > trunc(sysdate) AND r.DEPTID='" + deptid + "'";
            Map<String, Object> map1 = this.execSqlSingleResult(module8);
            message.put("data", map1);
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void module9() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
        if (map.size() == 0) {
            return;
        }
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            Object deptid = session.get("DEPTID");
            Map message = new HashMap<String, Object>();
            message.put("module", 9);
//            String module9 = "select nvl(sum(decode(WARNING_TYPE,5,1,0)),0) xsgj,nvl(sum(decode(WARNING_TYPE,7,1,0)),0) khgj,0 xcjc,0 yhgj  from MONITOR_CHECK_EJ WHERE DEPTID='" + deptid + "'";
//            Map<String, Object> map1 = this.execSqlSingleResult(module9);
            String module9 = "SELECT\n" +
                    "  nvl(sum(decode(tt.TASK_TYPE, 1, 1, 0)),0) xsgj,\n" +
                    "  nvl(sum(decode(tt.TASK_TYPE, 2, 1, 0)),0) khgj,\n" +
                    "  nvl(sum(decode(tt.TASK_TYPE, 3, 1, 0)),0) xcjc,\n" +
                    "  0                             yhgj\n" +
                    "FROM MONITOR_CHECK_EJ tt where CREATE_TIME >= trunc(sysdate) and STATUS = 0 and deptid = '" + deptid + "'";
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
                    "              WHERE tt.TASK_TYPE = t.TASK_TYPE AND tt.STATUS = 0 AND tt.CREATE_TIME >= trunc(sysdate) and deptid = '" + deptid + "')) t join WARNING_TYPE tt on t.WARNING_TYPE = tt.WARNING_TYPE ";
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
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    @Scheduled(fixedDelay = 30000)
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
            Object deptid = session.get("DEPTID");
            message.put("module", 10);
//            String module7 = "SELECT\n" +
//                    "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'xssh%'\n" +
//                    "    THEN 1\n" +
//                    "   ELSE 0 END)) xssh,\n" +
//                    "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'wtsh%'\n" +
//                    "    THEN 1\n" +
//                    "   ELSE 0 END)) wtsh,\n" +
//                    "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'defect%'\n" +
//                    "    THEN 1\n" +
//                    "   ELSE 0 END)) defect,\n" +
//                    "  sum((CASE WHEN PROC_DEF_ID_ LIKE 'jcsh%'\n" +
//                    "    THEN 1\n" +
//                    "   ELSE 0 END)) jcsh\n" +
//                    "FROM ACT_RU_TASK\n" +
//                    "WHERE CREATE_TIME_ > trunc(sysdate)";
//            String yh = " SELECT\n" +
//                    "  count(1) as yh\n" +
//                    "    FROM ACT_HI_ACTINST t LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND  h.NAME_ = 'YHID'\n" +
//                    "   LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
//                    "    WHERE  t.PROC_DEF_ID_ LIKE 'wtsh%'  AND t.END_TIME_ IS NOT  NULL AND y.ID IS NOT NULL AND trunc(END_TIME_)=trunc(sysdate) AND ASSIGNEE_ = 'sdid' AND YWORG_ID ='" + deptid + "'";

            String yh = " SELECT\n" +
                    "  (SELECT count(1)\n" +
                    "   FROM ACT_HI_ACTINST t LEFT JOIN ACT_HI_VARINST h ON t.PROC_INST_ID_ = h.PROC_INST_ID_ AND h.NAME_ = 'YHID'\n" +
                    "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                    "   WHERE t.PROC_DEF_ID_ LIKE 'wtsh%' AND ASSIGNEE_ = 'sdid' AND t.END_TIME_ IS NOT NULL AND y.ID IS NOT NULL\n" +
                    "         AND y.YWORG_ID = '" + deptid + "')  +\n" +
                    "  (SELECT count(1)\n" +
                    "   FROM ACT_RU_TASK t LEFT JOIN ACT_RU_VARIABLE h ON t.PROC_INST_ID_ = h.PROC_INST_ID_\n" +
                    "     LEFT JOIN XS_SB_YH y ON y.ID = h.TEXT_\n" +
                    "   WHERE h.NAME_ = 'YHID' AND t.PROC_DEF_ID_ LIKE 'wtsh%' AND y.ID IS NOT NULL AND ASSIGNEE_ = 'sdid'\n" +
                    "         AND y.YWORG_ID = '" + deptid + "') AS yh\n" +
                    "FROM dual ";


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
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void module11() throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
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
        Date ercreatetime = DateUtil.parseDate(towHour.get("CREATETIME").toString());
        if (ercreatetime.getTime() >= DateUtil.getScheduleTime(timeConfig.get("START_TIME").toString())) {
            returnMap.put("xcsjyj", DateUtil.addDate(ercreatetime, Double.parseDouble(timeConfig.get("DAY_ZQ").toString())));
            returnMap.put("yjjg", timeConfig.get("DAY_ZQ") + "小时/次");
        } else if (ercreatetime.getTime() <= DateUtil.getScheduleTime(timeConfig.get("END_TIME").toString())) {
            returnMap.put("xcsjyj", DateUtil.addDate(ercreatetime, Double.parseDouble(timeConfig.get("NIGHT_ZQ").toString())));
            returnMap.put("yjjg", timeConfig.get("NIGHT_ZQ") + "小时/次");
        }
        message.put("data", returnMap);
        Set<Map.Entry<String, HashMap>> entries = map.entrySet();
        for (Map.Entry<String, HashMap> entry : entries) {
            String sessionId = entry.getKey();
            HashMap session = map.get(sessionId);
            erJiServerEndpoint.sendText((Session) session.get("session"), message);
        }
    }

    public void module11(String sessionId) throws Exception {
        Map<String, HashMap> map = erJiServerEndpoint.sendMsg();
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
        Date ercreatetime = DateUtil.parseDate(towHour.get("CREATETIME").toString());
        if (ercreatetime.getTime() >= DateUtil.getScheduleTime(timeConfig.get("START_TIME").toString())) {
            returnMap.put("xcsjyj", DateUtil.addDate(ercreatetime, Double.parseDouble(timeConfig.get("DAY_ZQ").toString())));
            returnMap.put("yjjg", timeConfig.get("DAY_ZQ") + "小时/次");
        } else if (ercreatetime.getTime() <= DateUtil.getScheduleTime(timeConfig.get("END_TIME").toString())) {
            returnMap.put("xcsjyj", DateUtil.addDate(ercreatetime, Double.parseDouble(timeConfig.get("NIGHT_ZQ").toString())));
            returnMap.put("yjjg", timeConfig.get("NIGHT_ZQ") + "小时/次");
        }
        message.put("data", returnMap);
        erJiServerEndpoint.sendText((Session) session.get("session"), message);
    }
}
