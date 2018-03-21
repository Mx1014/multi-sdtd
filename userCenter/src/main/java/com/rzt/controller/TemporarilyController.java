package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.weekTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@RequestMapping("TEMPORARILY")
public class TemporarilyController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 人员告警 列表
     *
     * @param tableType     当日 当前 本周 本月
     * @param workType      同下
     * @param page
     * @param size
     * @param currentUserId 同下
     * @param startTime     同下
     * @param endTime       同下
     * @param deptId        同下
     * @param taskType      同下
     * @return
     */
    @RequestMapping("NewTemporarilyList")
    public WebApiResponse NewOfflinesList(Integer tableType, String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        org.springframework.data.domain.Pageable pageable = new PageRequest(page, size);
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String s = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            s += " AND k.OFFWORK_TIME>= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND k.OFFWORK_TIME<= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(k.OFFWORK_TIME)=trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            s += " AND k.OFFWORK_TIME>= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND k.OFFWORK_TIME<= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND trunc(k.OFFWORK_TIME,'yyyy-mm')=trunc(sysdate,'yyyy-mm') ";
        }
        if (tableType == 0) {
            s += " AND k.CURRENT_STATUS = 0 ";
        }
        if (roletype == 1 || roletype == 2) {
            s += " AND u.DEPTID ='" + deptid + "' ";
        }
        if (!StringUtils.isEmpty(deptId)) {
            s += " AND u.DEPTID ='" + deptId + "' ";
        }
        String sql = " SELECT " +
                "  h.TASK_NAME," +
                "  u.DEPT, " +
                "  u.COMPANYNAME, " +
                "  u.CLASSNAME, " +
                "  u.USERNAME, " +
                "  u.PHONE, " +
                "  k.OFFWORK_TIME, " +
                "  u.LOGINSTATUS, " +
                "  k.CURRENT_STATUS,k.OFFWORK_FREQUENCY,k.OFFWORK_TIME_LONG  " +
                "FROM ALARM_OFFWORK k LEFT JOIN USERINFO u ON k.USER_ID = u.ID " +
                "  LEFT JOIN KH_TASK h ON h.ID = k.TASK_ID  WHERE 1=1  " + s + "   ORDER BY k.OFFWORK_TIME DESC  ";
        Page<Map<String, Object>> maps = this.service.execSqlPage(pageable, sql);
        try {
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("查询失败");
        }
    }

    /**
     * 人员离线告警 柱状图 展示
     *
     * @param tableType     当日 当前 本周 本月
     * @param workType      不知道是啥
     * @param page
     * @param size
     * @param currentUserId 人员id
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param deptId        通道单位ID
     * @param taskType      工作类型
     * @return
     */
    @GetMapping("NewTemporarilyTable")
    public WebApiResponse NewofflineAscTable(Integer tableType, String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String s = "";
        String className = "";
        String companyname = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            s += " AND k.OFFWORK_TIME>= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND k.OFFWORK_TIME<= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(k.OFFWORK_TIME)=trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            s += " AND k.OFFWORK_TIME>= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND k.OFFWORK_TIME<= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_date(k.OFFWORK_TIME,'yyyy-mm')=to_date(sysdate,'yyyy-mm') ";
        }
        if (tableType == 0) {
            s += " AND k.CURRENT_STATUS = 0 ";
        }
        if (roletype == 1 || roletype == 2) {
            className += " id = '" + deptid + "' ";
            s += " AND u.DEPTID ='" + deptid + "' ";
            companyname = " AND ORGID LIKE '%" + deptid + "%' ";
        } else if (!StringUtils.isEmpty(deptId)) {
            roletype = 1;
            s += " AND u.DEPTID ='" + deptId + "' ";
            companyname = " AND ORGID LIKE '%" + deptId + "%' ";
            className += " id = '" + deptId + "' ";
        }
        if (roletype == 0) {
            try {
                return WebApiResponse.success(deptOne(s));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        } else if (roletype == 1 || roletype == 2) {
            try {
                return WebApiResponse.success(deptTwo(s, className, companyname));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        }
        return null;
    }

    //二级通道单位查询
    private Map deptTwo(String s, String className, String companyname) {
        //查询班组
        String sql = " SELECT  nvl(a.VALUE,0) as VALUE,b.ID,DEPTNAME,nvl(a.tasknum,0) AS TASKNUM FROM ( SELECT count(k.OFFWORK_FREQUENCY) AS VALUE, u.CLASSID,count(DISTINCT k.TASK_ID) as tasknum FROM ALARM_OFFWORK k LEFT JOIN USERINFO u ON k.USER_ID = u.ID  WHERE 1 = 1 " + s + "       GROUP BY u.CLASSID) a RIGHT JOIN (SELECT ID, DEPTNAME, LASTNODE FROM RZTSYSDEPARTMENT WHERE LASTNODE = 0 START WITH " + className + " CONNECT BY PRIOR ID = DEPTPID)b ON a.CLASSID = b.ID ORDER BY VALUE desc";
        List<Map<String, Object>> maps = this.service.execSql(sql);
        //查询外协
        String companynameSql = " SELECT  nvl(a.VALUE,0) as VALUE,b.ID,COMPANYNAME,nvl(a.tasknum,0) AS TASKNUM FROM ( SELECT sum(k.OFFWORK_FREQUENCY) AS VALUE,count(DISTINCT k.TASK_ID) as tasknum, u.COMPANYID FROM ALARM_OFFWORK k LEFT JOIN USERINFO u ON k.USER_ID = u.ID WHERE 1 = 1  " + s + "  GROUP BY u.COMPANYID) a RIGHT JOIN (SELECT * FROM RZTSYSCOMPANY WHERE 1=1   " + companyname + "   )b ON a.COMPANYID = b.ID  ORDER BY VALUE desc";
        List<Map<String, Object>> maps1 = this.service.execSql(companynameSql);
        //查询人员
        String userNum = " SELECT sum(k.OFFWORK_FREQUENCY)  AS VALUE, count(DISTINCT k.TASK_ID) AS tasknum,u.ID,u.REALNAME FROM ALARM_OFFWORK k LEFT JOIN USERINFO u ON k.USER_ID = u.ID WHERE 1 = 1 " + s + "GROUP BY u.ID,u.REALNAME  ORDER BY VALUE desc";
        List<Map<String, Object>> maps2 = this.service.execSql(userNum);
        Map map = new HashMap();
        map.put("USERNUM", maps2);
        map.put("DEPT", maps);
        map.put("COMPANY", maps1);
        return map;
    }

    //一级管理员权限
    private Map deptOne(String s) {
        //查询班组
        String sql = " SELECT  nvl(a.VALUE,0) as VALUE,b.ID,DEPTNAME,nvl(a.tasknum,0) AS TASKNUM FROM ( SELECT count(k.OFFWORK_FREQUENCY) AS VALUE, u.DEPTID,count(DISTINCT k.TASK_ID) as tasknum FROM ALARM_OFFWORK k LEFT JOIN USERINFO u ON k.USER_ID = u.ID  WHERE 1 = 1 " + s + "       GROUP BY u.DEPTID) a RIGHT JOIN (SELECT * FROM RZTSYSDEPARTMENT WHERE DEPTSORT IS NOT NULL )b ON a.DEPTID = b.ID ORDER BY VALUE desc";
        List<Map<String, Object>> maps = this.service.execSql(sql);
        //查询外协
        String companynameSql = " SELECT  nvl(a.VALUE,0) as VALUE,b.ID,COMPANYNAME,nvl(a.tasknum,0) AS TASKNUM FROM ( SELECT sum(k.OFFWORK_FREQUENCY) AS VALUE,count(DISTINCT k.TASK_ID) as tasknum, u.COMPANYID FROM ALARM_OFFWORK k LEFT JOIN USERINFO u ON k.USER_ID = u.ID WHERE 1 = 1  " + s + "  GROUP BY u.COMPANYID) a RIGHT JOIN (SELECT * FROM RZTSYSCOMPANY WHERE 1=1  )b ON a.COMPANYID = b.ID  ORDER BY VALUE desc";
        List<Map<String, Object>> maps1 = this.service.execSql(companynameSql);
        //查询人员
        String userNum = " SELECT sum(k.OFFWORK_FREQUENCY)  AS VALUE, count(DISTINCT k.TASK_ID) AS tasknum,u.ID,u.REALNAME FROM ALARM_OFFWORK k LEFT JOIN USERINFO u ON k.USER_ID = u.ID WHERE 1 = 1 " + s + "GROUP BY u.ID,u.REALNAME  ORDER BY VALUE  desc";
        List<Map<String, Object>> maps2 = this.service.execSql(userNum);
        Map map = new HashMap();
        map.put("USERNUM", maps2);
        map.put("DEPT", maps);
        map.put("COMPANY", maps1);
        return map;
    }


    @RequestMapping("temporarilyList")
    public WebApiResponse answertimeList(Integer tableType, String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        if (tableType == 0) {
            return current(loginstatus, taskname, companyid, page, size, currentUserId, startTime, endTime, deptId);
        }
        return sameDay(loginstatus, taskname, companyid, page, size, currentUserId, startTime, endTime, deptId);

    }

    private WebApiResponse current(String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s1 += " AND u.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s1 += " AND u.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            //s1 += " AND trunc(u.CREATE_TIME)=trunc(sysdate) ";
            s1 += " AND trunc(t.START_TIME)=trunc(sysdate) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }

        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s += " AND k.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s += " AND u.LOGINSTATUS  = ?" + listLike.size();
        }
       /* String sql = " SELECT t.START_TIME,t.END_TIME,t.STATUS,k.TASK_NAME,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.DEPT,u.PHONE FROM " +
                "( SELECT u.TASK_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u " +
                " LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1 " + s1 +
                " )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID " + s;*/
//        String sql = "SELECT t.*,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.DEPT,u.PHONE,'1' AS TASK_TYPE,k.TASK_NAME,u.LOGINSTATUS  FROM " +
//                " ( SELECT u.TASK_ID,u.USER_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u " +
//                " LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1  AND t.TIME_STATUS=1 " + s1 +
//                " )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID" + s;
        String sql = " SELECT t.*,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.DEPT,u.PHONE,'1' AS TASK_TYPE,k.TASK_NAME,u.LOGINSTATUS  FROM\n" +
                "  ( SELECT u.TASK_ID,u.USER_ID, t.TIME_STATUS AS STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u\n" +
                "   LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID\n" +
                "  WHERE 1=1  " + s1 + "  AND t.TIME_STATUS=1  AND t.END_TIME IS NULL\n" +
                "   )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID " + s;
        return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
    }

    private WebApiResponse sameDay(String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s1 += " AND u.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s1 += " AND u.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s1 += " AND trunc(t.START_TIME)=trunc(sysdate) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }

        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s += " AND k.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s += " AND u.LOGINSTATUS  = ?" + listLike.size();
        }
        String sql = " SELECT t.*,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.DEPT,u.PHONE,'1' AS TASK_TYPE,k.TASK_NAME,u.LOGINSTATUS  FROM\n" +
                "  ( SELECT u.TASK_ID,u.USER_ID, t.TIME_STATUS AS STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u\n" +
                "   LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID\n" +
                "  WHERE 1=1  " + s1 + "  AND t.OVER_STATUS = 1 \n" +
                "   )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID " + s;
        return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
    }
}
