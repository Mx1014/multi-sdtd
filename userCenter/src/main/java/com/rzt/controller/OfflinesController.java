package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.weekTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("OFFLINES")
public class OfflinesController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 离线人员告警
     *
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param deptId
     * @return
     */
    @RequestMapping("OfflinesList")
    public WebApiResponse OfflinesList(Integer tableType, Integer workType, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType, String loginType) {
        if (tableType == 0) {
            return current(workType, page, size, currentUserId, startTime, endTime, deptId, taskType, loginType);
        }
        return sameDay(tableType, workType, page, size, currentUserId, startTime, endTime, deptId, taskType, loginType);
    }

    private WebApiResponse current(Integer workType, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType, String loginType) {
        org.springframework.data.domain.Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        String s2 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(workType)) {
            listLike.add(workType);
            s1 += " AND worktypes= ?" + listLike.size();
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginType)) {
            listLike.add(loginType);
            s2 += " AND LOGINSTATUS=?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND CREATE_TIME-90/(60*24)  >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND CREATE_TIME-90/(60*24)  <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(taskType)) {
            listLike.add(taskType);
            s += " and TASK_TYPE = ?" + listLike.size();
        }
//        String sql = "SELECT DISTINCT   " +
//                "  ce.USER_ID AS userID,   " +
//                "  ce.REASON, " +
//                "  ce.TASK_TYPE, " +
//                "  ce.TASK_ID, " +
//                "  ch.*   " +
//                "FROM (SELECT   " +
//                "        e.USER_ID,   " +
//                "        u.REALNAME,   " +
//                "        u.CLASSNAME,   " +
//                "        u.DEPT,   " +
//                "        u.COMPANYNAME,   " +
//                "        CASE u.WORKTYPE   " +
//                "        WHEN 1   " +
//                "          THEN '看护'   " +
//                "        WHEN 2   " +
//                "          THEN '巡视'   " +
//                "        WHEN 3   " +
//                "          THEN '现场稽查' END AS WORKTYPE,   " +
//                "        e.a               AS MORE,   " +
//                "        u.DEPTID,   " +
//                "        e.CREATE_TIME,   " +
//                "        e.ONLINE_TIME  " +
//                "      FROM (SELECT   " +
//                "              count(1)    AS a,   " +
//                "              ej.USER_ID,   " +
//                "              MAX(ej.CREATE_TIME)       AS CREATE_TIME,   " +
//                "              nvl(to_char(MAX(ej.ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') AS ONLINE_TIME   " +
//                "            FROM MONITOR_CHECK_EJ ej   " +
//                "            WHERE (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2) " + s +
//                "            GROUP BY USER_ID) e LEFT JOIN USERINFO u ON e.USER_ID = u.ID) ch LEFT JOIN MONITOR_CHECK_EJ ce   " +
//                "    ON ch.USER_ID = ce.USER_ID AND ch.CREATE_TIME = ce.CREATE_TIME";
        String sql = " SELECT DISTINCT " +
                "  ce.USER_ID AS userID, " +
                "  ce.REASON, " +
                "  ce.TASK_TYPE, " +
                "  ce.TASK_ID, " +
                " ce.TASK_NAME, " +
                " nvl(to_char(ce.ONLINE_TIME, 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') AS ONLINE_TIME , " +
                "  ch.*, " +
                "  CASE ch.WORKTYPEs " +
                "        WHEN 1 " +
                "          THEN '看护' " +
                "        WHEN 2 " +
                "          THEN '巡视' " +
                "        WHEN 3 " +
                "          THEN '现场稽查' END AS WORKTYPE " +
                "FROM (SELECT " +
                "        e.USER_ID, " +
                "        u.REALNAME, " +
                "        u.CLASSNAME, " +
                "        u.DEPT, " +
                "        u.LOGINSTATUS, " +
                "        u.COMPANYNAME, " +
                "         u.WORKTYPE AS WORKTYPEs, " +
                "        e.a               AS MORE, " +
                "        u.DEPTID, " +
                "        e.CREATE_TIME  -90/(60*24) AS CREATE_TIME," +
                "      e.timeLong  " +
                "      FROM (SELECT " +
                "              count(1)                                                            AS a, " +
                "              ej.USER_ID, " +
                "              MAX(ej.CREATE_TIME)                                                 AS CREATE_TIME, " +
                "           sum(ROUND(TO_NUMBER(nvl(ONLINE_TIME,sysdate) - (CREATE_TIME-90/(60*24))) * 24 * 60 * 60)) timeLong  " +
                "            FROM MONITOR_CHECK_EJ ej " +
                "            WHERE (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2 OR WARNING_TYPE = 13 )  AND STATUS = 0  AND USER_ID !='null' AND TASK_STATUS=0 AND USER_LOGIN_TYPE = 0  " + s +
                "            GROUP BY USER_ID) e JOIN USERINFO u ON e.USER_ID = u.ID AND u.USERDELETE=1 " + s2 + " ) ch LEFT JOIN MONITOR_CHECK_EJ ce " +
                "    ON ch.USER_ID = ce.USER_ID AND ch.CREATE_TIME = (ce.CREATE_TIME -90/(60*24))  " + s1;
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    private WebApiResponse sameDay(Integer tableType, Integer workType, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType, String loginType) {
        org.springframework.data.domain.Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        String s2 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(workType)) {
            listLike.add(workType);
            s1 += " AND worktypes= ?" + listLike.size();
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginType)) {
            listLike.add(loginType);
            s2 += " AND LOGINSTATUS=?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND CREATE_TIME-90/(60*24)  >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND CREATE_TIME-90/(60*24)  <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            listLike.add(mon);
            s += " AND CREATE_TIME-90/(60*24)  >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(sun);
            s += " AND CREATE_TIME-90/(60*24)  <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_char(CREATE_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
        }
        if (!StringUtils.isEmpty(taskType)) {
            listLike.add(taskType);
            s += " and TASK_TYPE = ?" + listLike.size();
        }
        String sql = " SELECT DISTINCT " +
                "  ce.USER_ID AS userID, " +
                "  ce.REASON, " +
                "  ce.TASK_TYPE, " +
                "  ce.TASK_ID, " +
                " ce.TASK_NAME, " +
                " nvl(to_char(ce.ONLINE_TIME, 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') AS ONLINE_TIME , " +
                "  ch.*, " +
                "  CASE ch.WORKTYPEs " +
                "        WHEN 1 " +
                "          THEN '看护' " +
                "        WHEN 2 " +
                "          THEN '巡视' " +
                "        WHEN 3 " +
                "          THEN '现场稽查' END AS WORKTYPE " +
                "FROM (SELECT " +
                "        e.USER_ID, " +
                "        u.REALNAME, " +
                "        u.CLASSNAME, " +
                "        u.DEPT, " +
                "        u.LOGINSTATUS, " +
                "        u.COMPANYNAME, " +
                "         u.WORKTYPE AS WORKTYPEs, " +
                "        e.a               AS MORE, " +
                "        u.DEPTID, " +
                "        e.CREATE_TIME  -90/(60*24) AS CREATE_TIME," +
                "      e.timeLong  " +
                "      FROM (SELECT " +
                "              count(1)                                                            AS a, " +
                "              ej.USER_ID, " +
                "              MAX(ej.CREATE_TIME)                                                 AS CREATE_TIME, " +
                "           sum(ROUND(TO_NUMBER(nvl(ONLINE_TIME,sysdate) - (CREATE_TIME-90/(60*24))) * 24 * 60 * 60)) timeLong  " +

                "            FROM MONITOR_CHECK_EJ ej " +
                "            WHERE (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2 OR WARNING_TYPE = 13 )  " + s +
                "            GROUP BY USER_ID) e JOIN USERINFO u ON e.USER_ID = u.ID AND u.USERDELETE=1 " + s2 + " ) ch LEFT JOIN MONITOR_CHECK_EJ ce " +
                "    ON ch.USER_ID = ce.USER_ID AND ch.CREATE_TIME = (ce.CREATE_TIME -90/(60*24))  " + s1;
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    @GetMapping("offlineAscTable")
    public WebApiResponse offlineAscTable(Integer tableType, Integer workType, String currentUserId, String startTime, String endTime, String deptId, String taskType, String loginType) {
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginType)) {
            listLike.add(loginType);
            s2 += " AND LOGINSTATUS=?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (tableType == 0) {
            s += " AND STATUS = 0  AND USER_ID !='null' AND TASK_STATUS=0 AND USER_LOGIN_TYPE = 0 ";
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND CREATE_TIME-90/(60*24)  >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND CREATE_TIME-90/(60*24)  <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            listLike.add(mon);
            s += " AND CREATE_TIME-90/(60*24)  >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(sun);
            s += " AND CREATE_TIME-90/(60*24)  <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_char(CREATE_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
        }
        if (!StringUtils.isEmpty(taskType)) {
            listLike.add(taskType);
            s += " and TASK_TYPE = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            roletype = 1;
            s3 = "  ID =  '" + deptId + "'";
            s4 = " and ORGID LIKE '%" + deptId + "%'";
        } else if (roletype == 1 || roletype == 2) {
            s3 = "  ID =  '" + deptid + "'";
            s4 = " and ORGID LIKE '%" + deptid + "%'";
        }
        if (roletype == 0) {
            String OFFlinedept = " SELECT\n" +
                    "  r.ID,\n" +
                    "  r.DEPTNAME AS NAME,\n" +
                    "  nvl(a.aa, 0) AS VALUE\n" +
                    "FROM (\n" +
                    "       SELECT\n" +
                    "         count(1) AS aa,\n" +
                    "         sum(e.timeLong),\n" +
                    "         u.DEPTID\n" +
                    "       FROM (SELECT\n" +
                    "               count(1)            AS                                                                           a,\n" +
                    "               ej.USER_ID,\n" +
                    "               MAX(\n" +
                    "                   ej.CREATE_TIME) AS                                                                           CREATE_TIME,\n" +
                    "               sum(ROUND(TO_NUMBER(nvl(ONLINE_TIME, sysdate) - (CREATE_TIME - 90 / (60 * 24))) * 24 * 60 * 60)) timeLong\n" +
                    "             FROM MONITOR_CHECK_EJ ej\n" +
                    "             WHERE\n" +
                    "               (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2 OR WARNING_TYPE = 13)  " + s +
                    "             GROUP BY USER_ID) e\n" +
                    "         JOIN RZTSYSUSER u ON e.USER_ID = u.ID AND u.USERDELETE = 1 " + s2 +
                    "       GROUP BY u.DEPTID\n" +
                    "     ) a RIGHT JOIN (SELECT *\n" +
                    "                     FROM RZTSYSDEPARTMENT\n" +
                    "                     WHERE DEPTSORT IS NOT NULL\n" +
                    "                     ORDER BY DEPTSORT) r ON a.DEPTID = r.ID ";
            String OFFlinecom = " SELECT\n" +
                    "  r.ID,\n" +
                    "  r.COMPANYNAME AS NAME ,\n" +
                    "  nvl(a.aa, 0) AS VALUE\n" +
                    "FROM (\n" +
                    "       SELECT\n" +
                    "         count(1) AS aa,\n" +
                    "         sum(e.timeLong),\n" +
                    "         u.COMPANYID\n" +
                    "       FROM (SELECT\n" +
                    "               count(1)            AS                                                                           a,\n" +
                    "               ej.USER_ID,\n" +
                    "               MAX(\n" +
                    "                   ej.CREATE_TIME) AS                                                                           CREATE_TIME,\n" +
                    "               sum(ROUND(TO_NUMBER(nvl(ONLINE_TIME, sysdate) - (CREATE_TIME - 90 / (60 * 24))) * 24 * 60 * 60)) timeLong\n" +
                    "             FROM MONITOR_CHECK_EJ ej\n" +
                    "             WHERE\n" +
                    "               (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2 OR WARNING_TYPE = 13) " + s +
                    "             GROUP BY USER_ID) e\n" +
                    "         JOIN RZTSYSUSER u ON e.USER_ID = u.ID AND u.USERDELETE = 1 " + s2 +
                    "       GROUP BY u.COMPANYID\n" +
                    "     ) a RIGHT JOIN (SELECT\n" +
                    "                       ID,\n" +
                    "                       COMPANYNAME\n" +
                    "                     FROM RZTSYSCOMPANY  ) r ON a.COMPANYID = r.ID " + s1;
            try {
                Map map = new HashMap();
                List<Map<String, Object>> maps = this.service.execSql(OFFlinedept, listLike.toArray());
                List<Map<String, Object>> maps1 = this.service.execSql(OFFlinecom, listLike.toArray());
                map.put("OVERDUE", maps);
                map.put("OVERDUECOMPANY", maps1);
                return WebApiResponse.success(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (roletype == 2 || roletype == 1) {
            String OFFlinedept = " SELECT\n" +
                    "  r.ID,\n" +
                    "  r.DEPTNAME AS NAME,\n" +
                    "  nvl(a.aa, 0) AS VALUE\n" +
                    "FROM (\n" +
                    "       SELECT\n" +
                    "         count(1) AS aa,\n" +
                    "         sum(e.timeLong),\n" +
                    "         u.CLASSNAME\n" +
                    "       FROM (SELECT\n" +
                    "               count(1)            AS                                                                           a,\n" +
                    "               ej.USER_ID,\n" +
                    "               MAX(\n" +
                    "                   ej.CREATE_TIME) AS                                                                           CREATE_TIME,\n" +
                    "               sum(ROUND(TO_NUMBER(nvl(ONLINE_TIME, sysdate) - (CREATE_TIME - 90 / (60 * 24))) * 24 * 60 * 60)) timeLong\n" +
                    "             FROM MONITOR_CHECK_EJ ej\n" +
                    "             WHERE\n" +
                    "               (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2 OR WARNING_TYPE = 13) " + s +
                    "             GROUP BY USER_ID) e\n" +
                    "         JOIN RZTSYSUSER u ON e.USER_ID = u.ID AND u.USERDELETE = 1 " + s2 +
                    "       GROUP BY u.CLASSNAME\n" +
                    "     ) a RIGHT JOIN (SELECT\n" +
                    "  ID,\n" +
                    "  DEPTNAME\n" +
                    "FROM (SELECT\n" +
                    "        ID,\n" +
                    "        DEPTNAME,\n" +
                    "        LASTNODE\n" +
                    "      FROM RZTSYSDEPARTMENT\n" +
                    "      START WITH " + s3 + " CONNECT BY PRIOR id=\n" +
                    "                               DEPTPID)\n" +
                    "WHERE LASTNODE = 0) r ON a.CLASSNAME = r.ID " + s1;
            String OFFlinecom = " SELECT\n" +
                    "  r.ID,\n" +
                    "  r.COMPANYNAME AS NAME,\n" +
                    "  nvl(a.aa, 0) AS VALUE\n" +
                    "FROM (\n" +
                    "       SELECT\n" +
                    "         count(1) AS aa,\n" +
                    "         sum(e.timeLong),\n" +
                    "         u.COMPANYID\n" +
                    "       FROM (SELECT\n" +
                    "               count(1)            AS                                                                           a,\n" +
                    "               ej.USER_ID,\n" +
                    "               MAX(\n" +
                    "                   ej.CREATE_TIME) AS                                                                           CREATE_TIME,\n" +
                    "               sum(ROUND(TO_NUMBER(nvl(ONLINE_TIME, sysdate) - (CREATE_TIME - 90 / (60 * 24))) * 24 * 60 * 60)) timeLong\n" +
                    "             FROM MONITOR_CHECK_EJ ej\n" +
                    "             WHERE\n" +
                    "               (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2 OR WARNING_TYPE = 13) " + s +
                    "             GROUP BY USER_ID) e\n" +
                    "         JOIN RZTSYSUSER u ON e.USER_ID = u.ID AND u.USERDELETE = 1 " + s2 +
                    "       GROUP BY u.COMPANYID\n" +
                    "     ) a RIGHT JOIN (SELECT\n" +
                    "                       ID,\n" +
                    "                       COMPANYNAME\n" +
                    "                     FROM RZTSYSCOMPANY where 1=1 " + s4 + " ) r ON a.COMPANYID = r.ID " + s1;
            try {
                Map map = new HashMap();
                List<Map<String, Object>> maps = this.service.execSql(OFFlinedept, listLike.toArray());
                List<Map<String, Object>> maps1 = this.service.execSql(OFFlinecom, listLike.toArray());
                map.put("OVERDUE", maps);
                map.put("OVERDUECOMPANY", maps1);
                return WebApiResponse.success(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
