package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.weekTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("OVERDUE")
public class OverdueController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("overdueList")
    public WebApiResponse overdueList(Integer tableType, String taskname, String loginstatus, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        if (tableType == 0) {
            return current(taskname, loginstatus, companyid, page, size, currentUserId, startTime, endTime, deptId);
        }
        return sameDay(tableType, taskname, loginstatus, companyid, page, size, currentUserId, startTime, endTime, deptId);

    }

    private WebApiResponse current(String taskname, String loginstatus, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID = ?" + listLike.size();
        } else if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s1 += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s1 += " AND k.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s1 += " AND u.LOGINSTATUS = ?" + listLike.size();
        }
        String sql = " SELECT " +
                "  k.TASK_NAME,u.DEPT,u.CLASSNAME,u.LOGINSTATUS,u.COMPANYNAME,u.REALNAME,u.PHONE,k.PLAN_END_TIME,e.* " +
                "FROM (SELECT TASK_ID,USER_ID,TASK_TYPE  " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 1  AND STATUS = 0  AND TASK_STATUS = 0  " + s +
                "     ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID where 1 = 1 " + s1;
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    private WebApiResponse sameDay(Integer tableType, String taskname, String loginstatus, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID = ?" + listLike.size();
        } else if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(tableType)) {
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                listLike.add(startTime);
                s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
                listLike.add(endTime);
                s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            } else if (tableType == 1) {
                s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
            } else if (tableType == 2) {
                Map map = weekTime.weekTime();
                Object mon = map.get("Mon");
                Object sun = map.get("Sun");
                listLike.add(mon);
                s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
                listLike.add(sun);
                s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            } else if (tableType == 3) {
                s += " AND to_char(CREATE_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
            }
        }
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s1 += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s1 += " AND k.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s1 += " AND u.LOGINSTATUS = ?" + listLike.size();
        }
        String sql = " SELECT " +
                "  k.TASK_NAME,u.DEPT,u.CLASSNAME,u.LOGINSTATUS,u.COMPANYNAME,u.REALNAME,u.PHONE,k.PLAN_END_TIME,e.* " +
                "FROM (SELECT TASK_ID,USER_ID,TASK_TYPE  " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 1   " + s +
                "     ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID where 1 = 1 " + s1;
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    @GetMapping("overdueAscTable")
    public WebApiResponse overdueDeptTable(String taskname, String loginstatus, String companyid, String currentUserId, String startTime, String endTime, String deptId, Integer tableType) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String s = "";
        String s1 = "";
        String s3 = "";
        String s4 = "";
        List listLike = new ArrayList();
        //当前 0 当天 1
        if (!StringUtils.isEmpty(tableType)) {
            if (tableType == 0) {
                s += " AND STATUS = 0  AND TASK_STATUS = 0 ";
            }
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                listLike.add(startTime);
                s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
                listLike.add(endTime);
                s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            } else if (tableType == 0 || tableType == 1) {
                s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
            } else if (tableType == 2) {
                Map map = weekTime.weekTime();
                Object mon = map.get("Mon");
                Object sun = map.get("Sun");
                listLike.add(mon);
                s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
                listLike.add(sun);
                s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            } else if (tableType == 3) {
                s += " AND to_char(CREATE_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
            }
        }
        if (!StringUtils.isEmpty(deptId)) {
            roletype = 1;
            listLike.add(deptId);
            s1 = " u.DEPTID =  ?" + listLike.size();
            listLike.add(deptId);
            s3 = " ID =  ?" + listLike.size();
        } else if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s1 = " u.DEPTID =  ?" + listLike.size();
            listLike.add(deptid);
            s3 = " ID =  ?" + listLike.size();
            listLike.add("%" + deptid + "%");
            s4 = " ORGID LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s1 += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s1 += " AND k.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s1 += " AND u.LOGINSTATUS = ?" + listLike.size();
        }
        if (roletype == 0) {
            String overdue = " SELECT nvl(a.OVERDUEDEPT,0) AS OVERDUEDEPT,r.DEPTNAME,r.ID\n" +
                    "FROM (\n" +
                    "       SELECT\n" +
                    "         count(1) AS OVERDUEDEPT,\n" +
                    "         u.DEPTID\n" +
                    "       FROM (SELECT\n" +
                    "               TASK_ID,\n" +
                    "               USER_ID,\n" +
                    "               TASK_TYPE\n" +
                    "             FROM MONITOR_CHECK_EJ\n" +
                    "             WHERE WARNING_TYPE = 1 " + s +
                    "            ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID\n" +
                    "         LEFT JOIN RZTSYSUSER u ON k.CM_USER_ID = u.ID\n" +
                    "       WHERE 1 = 1\n " + s1 +
                    "       GROUP BY u.DEPTID) A RIGHT JOIN (SELECT ID,DEPTNAME " +
                    "                                        FROM RZTSYSDEPARTMENT\n" +
                    "                                        WHERE DEPTSORT IS NOT NULL ORDER BY DEPTSORT) R ON a.DEPTID = r.ID ";
            String OVERDUECOMPANY = " SELECT b.ID,b.COMPANYNAME,nvl(a.OVERDUECOMPANY,0) as OVERDUECOMPANY\n" +
                    "FROM (SELECT\n" +
                    "        count(1) AS OVERDUECOMPANY,\n" +
                    "        u.COMPANYID\n" +
                    "      FROM (SELECT\n" +
                    "              TASK_ID,\n" +
                    "              USER_ID,\n" +
                    "              TASK_TYPE\n" +
                    "            FROM MONITOR_CHECK_EJ\n" +
                    "            WHERE WARNING_TYPE = 1 " + s +
                    "           ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID\n" +
                    "        LEFT JOIN RZTSYSUSER u ON k.CM_USER_ID = u.ID\n" +
                    "      WHERE 1 = 1 " + s1 +
                    "      GROUP BY u.COMPANYID) a RIGHT JOIN (SELECT\n" +
                    "                                            ID,\n" +
                    "                                            COMPANYNAME\n" +
                    "                                          FROM RZTSYSCOMPANY ) b ON a.COMPANYID = b.ID ";
            try {
                Map map = new HashMap();
                List<Map<String, Object>> maps = this.service.execSql(overdue, listLike.toArray());
                List<Map<String, Object>> maps1 = this.service.execSql(OVERDUECOMPANY, listLike.toArray());
                map.put("OVERDUE", maps);
                map.put("OVERDUECOMPANY", maps1);
                return WebApiResponse.success(map);
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        }
        if (roletype == 1 || roletype == 2) {
            String overdue = " SELECT\n" +
                    "  nvl(a.OVERDUEDEPT, 0) AS OVERDUEDEPT,\n" +
                    "  r.DEPTNAME,\n" +
                    "  r.ID\n" +
                    "FROM (\n" +
                    "       SELECT\n" +
                    "         count(1) AS OVERDUEDEPT,\n" +
                    "         u.CLASSNAME\n" +
                    "       FROM (SELECT\n" +
                    "               TASK_ID,\n" +
                    "               USER_ID,\n" +
                    "               TASK_TYPE\n" +
                    "             FROM MONITOR_CHECK_EJ\n" +
                    "             WHERE WARNING_TYPE = 1 " + s +
                    "            ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID\n" +
                    "         LEFT JOIN RZTSYSUSER u ON k.CM_USER_ID = u.ID\n" +
                    "       WHERE 1 = 1 " + s1 +
                    "       GROUP BY u.CLASSNAME) A RIGHT JOIN (SELECT\n" +
                    "                                          ID,\n" +
                    "                                          DEPTNAME\n" +
                    "                                        FROM (SELECT\n" +
                    "                                                ID,\n" +
                    "                                                DEPTNAME,\n" +
                    "                                                LASTNODE\n" +
                    "                                              FROM RZTSYSDEPARTMENT\n" +
                    "                                              START WITH " + s3 + " CONNECT BY PRIOR ID =\n" +
                    "                                                                                                            DEPTPID)\n" +
                    "                                        WHERE LASTNODE = 0) R ON a.CLASSNAME = r.ID\n ";
            String OVERDUECOMPANY = " SELECT\n" +
                    "  b.ID,\n" +
                    "  b.COMPANYNAME,\n" +
                    "  nvl(a.OVERDUECOMPANY, 0) AS OVERDUECOMPANY\n" +
                    "FROM (SELECT\n" +
                    "        count(1) AS OVERDUECOMPANY,\n" +
                    "        u.COMPANYID\n" +
                    "      FROM (SELECT\n" +
                    "              TASK_ID,\n" +
                    "              USER_ID,\n" +
                    "              TASK_TYPE\n" +
                    "            FROM MONITOR_CHECK_EJ\n" +
                    "            WHERE WARNING_TYPE = 1 " + s +
                    "           ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID\n" +
                    "        LEFT JOIN RZTSYSUSER u ON k.CM_USER_ID = u.ID\n" +
                    "      WHERE 1 = 1 " + s1 +
                    "      GROUP BY u.COMPANYID) a RIGHT JOIN (SELECT\n" +
                    "                                            ID,\n" +
                    "                                            COMPANYNAME\n" +
                    "                                          FROM RZTSYSCOMPANY\n" +
                    "                                          WHERE 1=1 " + s4 + " ) b ON a.COMPANYID = b.ID ";
            try {
                Map map = new HashMap();
                List<Map<String, Object>> maps = this.service.execSql(overdue, listLike.toArray());
                List<Map<String, Object>> maps1 = this.service.execSql(OVERDUECOMPANY, listLike.toArray());
                map.put("OVERDUE", maps);
                map.put("OVERDUECOMPANY", maps1);
                return WebApiResponse.success(map);
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        }
        return null;
    }
}
