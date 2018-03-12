package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("ANSWERTIME")
public class AnswertimeController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("answertimeList")
    public WebApiResponse answertimeList(Integer tableType, String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {
        if (tableType == 0) {
            return current(loginstatus, taskname, companyid, page, size, currentUserId, startTime, endTime, deptId, taskType);
        }
        return sameDay(loginstatus, taskname, companyid, page, size, currentUserId, startTime, endTime, deptId, taskType);
    }

    private WebApiResponse current(String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND x.PLAN_START_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND x.PLAN_START_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(x.PLAN_START_TIME) = trunc(sysdate) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND u.DEPTID = ?" + listLike.size();
        } else if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND u.DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s += " AND x.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s += " AND u.LOGINSTATUS = ?" + listLike.size();
        }
        String allSql = "";
        String xssql = "  SELECT * FROM (SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME," +
                "  x.REAL_START_TIME,u.REALNAME,u.LOGINSTATUS,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS,e.* " +
                "FROM (SELECT TASK_ID,USER_ID,TASK_TYPE " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 4  AND STATUS = 0 AND TASK_STATUS = 0  " + s1 + " ) e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1 " +
                "" + s + " ) ";


        String khsql = " (SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.LOGINSTATUS,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS,e.* " +
                " FROM (SELECT TASK_ID,USER_ID,TASK_TYPE " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 10  AND STATUS = 0 AND TASK_STATUS = 0  " + s1 + " ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                //  AND trunc(x.PLAN_START_TIME) = trunc(sysdate)
                // 此代码标识当前查询只查询计划开始时间在当天的 --->李成阳
//                "    AND trunc(x.PLAN_START_TIME) = trunc(sysdate) " +

                " " + s + " ) "/* + "ORDER BY  PLAN_START_TIME DESC "*/;
        allSql = xssql + " UNION ALL " + khsql;
        if (!StringUtils.isEmpty(taskType)) {
            switch (taskType) {
                case "1":
                    allSql = xssql;
                    break;
                case "2":
                    allSql = khsql;
                    break;
                default:
                    allSql = xssql + " UNION ALL " + khsql;
            }
        }
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, allSql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    private WebApiResponse sameDay(String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND x.PLAN_START_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND x.PLAN_START_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(x.PLAN_START_TIME) = trunc(sysdate) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND u.DEPTID = ?" + listLike.size();
        } else if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND u.DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s += " AND x.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s += " AND u.LOGINSTATUS = ?" + listLike.size();
        }
        String allSql = "";
        String xssql = "  SELECT * FROM (SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME," +
                "  x.REAL_START_TIME,u.REALNAME,u.LOGINSTATUS,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS,e.* " +
                "FROM (SELECT TASK_ID,USER_ID,TASK_TYPE " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 4  " + s1 + " ) e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1 " +
                "" + s + " ) ";


        String khsql = " (SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.LOGINSTATUS,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS,e.* " +
                " FROM (SELECT TASK_ID,USER_ID,TASK_TYPE " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 10  " + s1 + " ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                //  AND trunc(x.PLAN_START_TIME) = trunc(sysdate)
                // 此代码标识当前查询只查询计划开始时间在当天的 --->李成阳
//                "    AND trunc(x.PLAN_START_TIME) = trunc(sysdate) " +

                " " + s + " ) "/* + "ORDER BY  PLAN_START_TIME DESC "*/;
        allSql = xssql + " UNION ALL " + khsql;
        if (!StringUtils.isEmpty(taskType)) {
            switch (taskType) {
                case "1":
                    allSql = xssql;
                    break;
                case "2":
                    allSql = khsql;
                    break;
                default:
                    allSql = xssql + " UNION ALL " + khsql;
            }
        }
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, allSql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
