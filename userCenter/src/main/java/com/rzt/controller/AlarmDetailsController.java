package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ALARMDETAILS")
public class AlarmDetailsController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("AlarmList")
    public WebApiResponse AlarmList(String currentUserId, String startTime, String endTime, String DEPTID) {
        List listLike = new ArrayList();
        String s = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        if (!StringUtils.isEmpty(DEPTID)) {
            roletype = 1;
        }
        if (roletype == 0) {
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                listLike.add(startTime);
                s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
                listLike.add(endTime);
                s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            } else {
                s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
            }
            if (!StringUtils.isEmpty(DEPTID)) {
                listLike.add(DEPTID);
                s += " AND DEPTID= ?" + listLike.size();
            }
//            String alarm = " SELECT " +
//                    "  NVL(A.OFFLINES,0) AS OFFLINES, " +
//                    "  NVL(A.ANSWERTIME,0) AS ANSWERTIME, " +
//                    "  NVL(A.OVERDUE,0) AS OVERDUE, " +
//                    "  NVL(A.TEMPORARILY,0) AS TEMPORARILY, " +
//                    "  NVL(A.UNQUALIFIEDPATROL,0) AS UNQUALIFIEDPATROL, " +
//                    "  T.ID AS DEPTID, " +
//                    "  T.DEPTNAME " +
//                    "FROM RZTSYSDEPARTMENT T LEFT JOIN (SELECT " +
//                    "                                     nvl(sum(decode(WARNING_TYPE, 2, 1, 8, 1,13,1,0)), 0)  AS OFFLINES, " +
//                    "                                     nvl(sum(decode(WARNING_TYPE, 4, 1, 10, 1, 0)), 0) AS ANSWERTIME, " +
//                    "                                     nvl(sum(decode(WARNING_TYPE, 1, 1, 0)), 0)        AS OVERDUE, " +
//                    "                                     nvl(sum(decode(WARNING_TYPE, 7, 1, 0)), 0)        AS TEMPORARILY, " +
//                    "                                     nvl(sum(decode(WARNING_TYPE, 5, 1, 0)), 0)        AS UNQUALIFIEDPATROL, " +
//                    "                                     DEPTID " +
//                    "                                   FROM MONITOR_CHECK_EJ " +
//                    "                                   WHERE 1 = 1 AND TASK_STATUS = 0 " + s +
//                    "                                   GROUP BY DEPTID) A ON T.ID = A.DEPTID " +
//                    "WHERE T.DEPTSORT IS NOT NULL " +
//                    "ORDER BY T.DEPTSORT ";
            String alarm = "SELECT\n" +
                    "  NVL(A.OFFLINES, 0)          AS OFFLINES,\n" +
                    "  NVL(A.ANSWERTIME, 0)        AS ANSWERTIME,\n" +
                    "  NVL(A.OVERDUE, 0)           AS OVERDUE,\n" +
                    "  NVL(A.TEMPORARILY, 0)       AS TEMPORARILY,\n" +
                    "  NVL(A.UNQUALIFIEDPATROL, 0) AS UNQUALIFIEDPATROL,\n" +
                    "  T.ID                        AS DEPTID,\n" +
                    "  T.DEPTNAME\n" +
                    "FROM RZTSYSDEPARTMENT T LEFT JOIN (SELECT\n" +
                    "                                     cc.UNQUALIFIEDPATROL,\n" +
                    "                                     CC.TEMPORARILY,\n" +
                    "                                     CC.OVERDUE,\n" +
                    "                                     CC.ANSWERTIME,\n" +
                    "                                     CC.OFFLINES,\n" +
                    "                                     CC.DEPTID\n" +
                    "                                   FROM (SELECT *\n" +
                    "                                         FROM (SELECT\n" +
                    "                                                 nvl(sum(decode(WARNING_TYPE, 4, 1, 10, 1, 0)), 0) AS ANSWERTIME,\n" +
                    "                                                 nvl(sum(decode(WARNING_TYPE, 1, 1, 0)), 0)        AS OVERDUE,\n" +
                    "                                                 nvl(sum(decode(WARNING_TYPE, 7, 1, 0)), 0)        AS TEMPORARILY,\n" +
                    "                                                 nvl(sum(decode(WARNING_TYPE, 5, 1, 0)), 0)        AS UNQUALIFIEDPATROL,\n" +
                    "                                                 DEPTID\n" +
                    "                                               FROM MONITOR_CHECK_EJ\n" +
                    "                                               WHERE 1 = 1 AND TASK_STATUS = 0 AND trunc(CREATE_TIME) = trunc(sysdate)\n" +
                    "                                               GROUP BY DEPTID) aa LEFT JOIN (SELECT\n" +
                    "  sum(ddd.ccc) AS OFFLINES,\n" +
                    "  ddd.DEPTID   AS DEPP\n" +
                    "FROM (\n" +
                    "       SELECT\n" +
                    "         nvl(sum(decode(WARNING_TYPE, 2,\n" +
                    "                        1, 8,\n" +
                    "                        1, 13, 1, 0)),\n" +
                    "             0) AS ccc,\n" +
                    "         DEPTID\n" +
                    "       FROM (SELECT DISTINCT\n" +
                    "               USER_ID,\n" +
                    "               WARNING_TYPE,\n" +
                    "               DEPTID\n" +
                    "             FROM MONITOR_CHECK_EJ\n" +
                    "       WHERE trunc(CREATE_TIME) =\n" +
                    "             trunc(sysdate)  AND TASK_STATUS = 0 )\n" +
                    "\n" +
                    "       GROUP BY DEPTID) ddd\n" +
                    "GROUP BY DEPTID) bb\n" +
                    "                                             ON aa.DEPTID = bb.DEPP) cc) A ON T.ID = A.DEPTID\n" +
                    "WHERE T.DEPTSORT IS NOT NULL\n" +
                    "ORDER BY T.DEPTSORT";
            List<Map<String, Object>> alarms = this.service.execSql(alarm, listLike.toArray());
            return WebApiResponse.success(alarms);
        } else if (roletype == 1 || roletype == 2) {
            Map<String, Integer> map1 = new HashMap();
            Map<String, Integer> map2 = new HashMap();
            Map<String, Integer> map3 = new HashMap();
            Map<String, Integer> map4 = new HashMap();
            Map<String, Integer> map5 = new HashMap();
            Map<String, Integer> map6 = new HashMap();
            Object deptid = jsonObject.get("DEPTID");
            if (!StringUtils.isEmpty(DEPTID)) {
                deptid = DEPTID;
            }
            listLike.add(deptid);
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                listLike.add(startTime);
                s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
                listLike.add(endTime);
                s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            } else {
                s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
            }
            /**
             * 离线人员
             */
            String offlines = " SELECT count(1) AS OFFLINES," +
                    "  u.CLASSNAME as CLASS_ID FROM (SELECT USER_ID FROM MONITOR_CHECK_EJ  WHERE DEPTID = ?1 AND (WARNING_TYPE = 8 OR WARNING_TYPE = 2 OR WARNING_TYPE = 13 )  AND TASK_STATUS = 0 " +
                    "  " + s + " GROUP BY USER_ID ) e LEFT JOIN RZTSYSUSER u ON e.USER_ID = u.ID GROUP BY u.CLASSNAME ";
            /**
             *未按时开始任务
             */
            String xsAnswertime = " SELECT count(1) AS XSANSWERTIME,k.CLASS_ID as CLASS_ID  FROM (SELECT TASK_ID FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 4 )  AND DEPTID = ?1 AND TASK_TYPE = 1 " + s + " ) e LEFT JOIN XS_ZC_TASK k ON k.ID = e.TASK_ID GROUP BY k.CLASS_ID ";
            String khAnswertime = " SELECT count(1) AS KHANSWERTIME,r.CLASSNAME as CLASS_ID FROM (SELECT TASK_ID,USER_ID FROM MONITOR_CHECK_EJ WHERE ( WARNING_TYPE = 10)  AND DEPTID = ?1 AND TASK_TYPE = 2 " + s + " ) e LEFT JOIN KH_TASK k ON k.ID = e.TASK_ID LEFT JOIN RZTSYSUSER r ON e.USER_ID = r.ID GROUP BY r.CLASSNAME ";
            /**
             * 超期任务
             */
            String xsOverdue = " SELECT count(1) as OVERDUE ,k.CLASS_ID as CLASS_ID FROM (SELECT * FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 1  AND DEPTID = ?1 " + s + " ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID GROUP BY k.CLASS_ID ";
            /**
             * 看护人员脱岗
             */
            String khTemporarily = " SELECT count(1) as TEMPORARILY,u.CLASSNAME as CLASS_ID FROM (SELECT DEPTID,TASK_ID FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 7  AND DEPTID = ?1  " + s + " ) e LEFT JOIN KH_TASK k ON k.ID = e.TASK_ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID GROUP BY CLASSNAME ";
            /**
             * 巡视不合格
             */
            String xsUnqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol,x.CLASS_ID as CLASS_ID FROM (SELECT TASK_ID FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 5  AND DEPTID = ?1  " + s + " ) e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID GROUP BY x.CLASS_ID ";
            String className = " SELECT ID,DEPTNAME FROM (SELECT ID,DEPTNAME,LASTNODE FROM RZTSYSDEPARTMENT START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0 ";
            List<Map<String, Object>> list = this.service.execSql(offlines, listLike.toArray());
            List<Map<String, Object>> list1 = this.service.execSql(xsAnswertime, listLike.toArray());
            List<Map<String, Object>> list2 = this.service.execSql(khAnswertime, listLike.toArray());
            List<Map<String, Object>> list3 = this.service.execSql(xsOverdue, listLike.toArray());
            List<Map<String, Object>> list4 = this.service.execSql(khTemporarily, listLike.toArray());
            List<Map<String, Object>> list5 = this.service.execSql(xsUnqualifiedpatrol, listLike.toArray());
            List<Map<String, Object>> list6 = this.service.execSql(className, deptid);
            for (int i = 0; i < list.size(); i++) {
                map1.put(list.get(i).get("CLASS_ID").toString(), Integer.parseInt(list.get(i).get("OFFLINES").toString()));
            }
            for (int i = 0; i < list1.size(); i++) {
                map2.put(list1.get(i).get("CLASS_ID").toString(), Integer.parseInt(list1.get(i).get("XSANSWERTIME").toString()));
            }
            for (int i = 0; i < list2.size(); i++) {
                map3.put(list2.get(i).get("CLASS_ID").toString(), Integer.parseInt(list2.get(i).get("KHANSWERTIME").toString()));
            }
            for (int i = 0; i < list3.size(); i++) {
                map4.put(list3.get(i).get("CLASS_ID").toString(), Integer.parseInt(list3.get(i).get("OVERDUE").toString()));
            }
            for (int i = 0; i < list4.size(); i++) {
                map5.put(list4.get(i).get("CLASS_ID").toString(), Integer.parseInt(list4.get(i).get("TEMPORARILY").toString()));
            }
            for (int i = 0; i < list5.size(); i++) {
                map6.put(list5.get(i).get("CLASS_ID").toString(), Integer.parseInt(list5.get(i).get("UNQUALIFIEDPATROL").toString()));
            }
            for (Map map : list6) {
                Object id = map.get("ID");

                Integer OFFLINESs = 0;
                Integer OFFLINES = map1.get(id);
                if (!StringUtils.isEmpty(OFFLINES)) {
                    OFFLINESs = OFFLINES;
                }
                Integer XSANSWERTIMEs = 0;
                Integer XSANSWERTIME = map1.get(id);
                if (!StringUtils.isEmpty(XSANSWERTIME)) {
                    XSANSWERTIMEs = XSANSWERTIME;
                }
                Integer KHANSWERTIMEs = 0;
                Integer KHANSWERTIME = map1.get(id);
                if (!StringUtils.isEmpty(KHANSWERTIME)) {
                    KHANSWERTIMEs = KHANSWERTIME;
                }
                Integer OVERDUEs = 0;
                Integer OVERDUE = map1.get(id);
                if (!StringUtils.isEmpty(OVERDUE)) {
                    OVERDUEs = OVERDUE;
                }
                Integer TEMPORARILYs = 0;
                Integer TEMPORARILY = map1.get(id);
                if (!StringUtils.isEmpty(TEMPORARILY)) {
                    TEMPORARILYs = TEMPORARILY;
                }
                Integer UNQUALIFIEDPATROLs = 0;
                Integer UNQUALIFIEDPATROL = map1.get(id);
                if (!StringUtils.isEmpty(UNQUALIFIEDPATROL)) {
                    UNQUALIFIEDPATROLs = OFFLINES;
                }
                map.put("OFFLINES", OFFLINESs);
                map.put("ANSWERTIME", XSANSWERTIMEs + KHANSWERTIMEs);
                map.put("OVERDUE", OVERDUEs);
                map.put("TEMPORARILY", TEMPORARILYs);
                map.put("UNQUALIFIEDPATROL", UNQUALIFIEDPATROLs);
            }
            return WebApiResponse.success(list6);
        }
        return WebApiResponse.success(null);
    }

    @RequestMapping("AlarmZhu")
    public WebApiResponse AlarmZhu(String currentUserId, String startTime, String endTime, String deptId) {
        String s = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        List list = new ArrayList();
        if (roletype == 1 || roletype == 2) {
            Object deptid = jsonObject.get("DEPTID");
            list.add(deptid);
            s += " AND DEPTID=?" + list.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            list.add(deptId);
            s += " AND DEPTID=?" + list.size();
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            list.add(startTime);
            s += " AND CREATE_TIME >= to_date(?" + list.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            list.add(endTime);
            s += " AND CREATE_TIME <= to_date(?" + list.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        try {
            String offlinesS = " SELECT\n" +
                    "  nvl(sum(decode(TASK_TYPE, 1, 1, 0)), 0) xsOFFLINES,\n" +
                    "  sum(decode(TASK_TYPE, 2, 1, 0))         khOFFLINES,\n" +
                    "  sum(decode(TASK_TYPE, 3, 1, 0))         xcjcOFFLINES\n" +
                    "FROM (SELECT DISTINCT USER_ID,TASK_TYPE FROM MONITOR_CHECK_EJ  WHERE  (WARNING_TYPE = 8 OR WARNING_TYPE = 2 OR WARNING_TYPE = 13) AND TASK_STATUS = 0  " + s + ") ";
            String answertimeS = " SELECT nvl(sum(decode(TASK_TYPE, 1, 1, 0)),0) XSANSWERTIME,sum(decode(TASK_TYPE, 2, 1, 0)) KHANSWERTIME FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 4 OR WARNING_TYPE = 10)  " + s;
            String overdueS = " SELECT nvl(sum(decode(TASK_TYPE, 1, 1, 0)),0) OVERDUE FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 1  " + s;
            String temporarilyS = " SELECT nvl(sum(decode(TASK_TYPE, 2, 1, 0)),0) TEMPORARILY FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 7  " + s;
            String unqualifiedpatrolS = " SELECT nvl(sum(decode(TASK_TYPE, 1, 1, 0)),0) UNQUALIFIEDPATROL FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 5  " + s;
            List<Map<String, Object>> offlines = this.service.execSql(offlinesS, list.toArray());
            List<Map<String, Object>> answertime = this.service.execSql(answertimeS, list.toArray());
            List<Map<String, Object>> overdue = this.service.execSql(overdueS, list.toArray());
            List<Map<String, Object>> temporarily = this.service.execSql(temporarilyS, list.toArray());
            List<Map<String, Object>> unqualifiedpatrol = this.service.execSql(unqualifiedpatrolS, list.toArray());
            List list1 = new ArrayList();
            list1.add(offlines);
            list1.add(answertime);
            list1.add(overdue);
            list1.add(temporarily);
            list1.add(unqualifiedpatrol);
            return WebApiResponse.success(list1);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
