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
            s1 = "  AND u.DEPTID =  ?" + listLike.size();
            s3 = "  ID =  '" + deptId + "'";
            s4 = "  AND ORGID LIKE '%" + deptId + "%'";
        } else if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s1 = "  AND u.DEPTID =  ?" + listLike.size();
            s3 = " ID =  '" + deptid + "'";
            s4 = "  AND ORGID LIKE '%" + deptid + "%'";
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
            String overdue = " SELECT nvl(a.OVERDUEDEPT,0) AS VALUE,r.DEPTNAME as NAME,r.ID\n" +
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
            String OVERDUECOMPANY = " SELECT b.ID,b.COMPANYNAME as NAME,nvl(a.OVERDUECOMPANY,0) as VALUE\n" +
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
                    "  nvl(a.OVERDUEDEPT, 0) AS VALUE,\n" +
                    "  r.DEPTNAME AS NAME,\n" +
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
                    "  b.COMPANYNAME AS NAME,\n" +
                    "  nvl(a.OVERDUECOMPANY, 0) AS VALUE\n" +
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

    @RequestMapping("overdueList1")
    public WebApiResponse overdueList1(Integer tableType, String taskname, String loginstatus, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId){
        Pageable pageable = new PageRequest(page, size);
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");

        String s = "";
        if (!StringUtils.isEmpty(tableType)) {
            if (tableType == 0) {
                s += " AND o.CHECK_STATUS=0 ";
            }
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                //超期的告警时间就是创建时间
                s += " AND o.ALARM_TIME >= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
                s += " AND o.ALARM_TIME <= to_date('" +endTime+ "','yyyy-mm-dd hh24:mi:ss') ";
            } else if (tableType == 0 || tableType == 1) {
                s += " AND trunc(o.ALARM_TIME) = trunc(sysdate) ";
            } else if (tableType == 2) {
                Map map = weekTime.weekTime();
                Object mon = map.get("Mon");
                Object sun = map.get("Sun");
                s += " AND o.ALARM_TIME >= to_date('" + mon+ "','yyyy-mm-dd hh24:mi:ss') ";
                s += " AND o.ALARM_TIME <= to_date('" + sun+ "','yyyy-mm-dd hh24:mi:ss') ";
            } else if (tableType == 3) {
                s += " AND to_char(o.ALARM_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
            }
        }
        if(!StringUtils.isEmpty(deptId)){
            //条件查询通道单位
            s+=" AND  u.DEPTID='"+deptId+"'";
        }
        if(!StringUtils.isEmpty(companyid)){
            s+=" AND u.COMPANYID='"+companyid+"'";
        }
        if(!StringUtils.isEmpty(taskname)){
            s+=" AND xs.TASK_NAME LIKE '%"+taskname+"%'";
        }
        if(!StringUtils.isEmpty(loginstatus)){
            s+=" AND u.LOGINSTATUS="+loginstatus;
        }
        String sql="";
        if(roletype==0){
            sql = "SELECT o.ALARM_TIME,o.TASK_ID,o.USER_ID,o.CHECK_STATUS,xs.TASK_NAME,xs.PLAN_END_TIME,xs.PLAN_START_TIME,u.*" +
                    " FROM ALARM_OVERDUE o LEFT JOIN XS_ZC_TASK xs ON o.TASK_ID=xs.ID LEFT JOIN USERINFO u\n" +
                    "  ON o.USER_ID=u.ID WHERE 1=1 "+s+" ORDER BY ALARM_TIME DESC ";
        }else if(roletype==1 || roletype==2){
            sql = "SELECT o.ALARM_TIME,o.TASK_ID,o.USER_ID,o.CHECK_STATUS,xs.TASK_NAME,xs.PLAN_END_TIME,xs.PLAN_START_TIME,u.*" +
                    " FROM ALARM_OVERDUE o LEFT JOIN XS_ZC_TASK xs ON o.TASK_ID=xs.ID LEFT JOIN USERINFO u\n" +
                    "  ON o.USER_ID=u.ID WHERE u.DEPTID='"+deptid+"' "+s+" ORDER BY ALARM_TIME DESC ";
        }

        try {
            if(!"".equals(sql)){
                Page<Map<String, Object>> maps = this.service.execSqlPage(pageable, sql);
                return WebApiResponse.success(maps);
            }else{
                return WebApiResponse.success("该用户无此权限");
            }
        }catch (Exception e){
            return WebApiResponse.erro("查询失败："+e.getMessage());
        }
    }

    @RequestMapping("overdueAscTable1")
    public WebApiResponse overdueDeptTable1(Integer tableType, String taskname, String loginstatus, String companyid, String currentUserId, String startTime, String endTime, String deptId){
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");

        String s = "";
        String s1 = "";
        String s2 = "";
        String s3 = "";
        String s4 = ""; //查询总任务数条件
        if (!StringUtils.isEmpty(tableType)) {
            if (tableType == 0) {
                s += " AND o.CHECK_STATUS=0 ";
            }
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                //超期的告警时间就是创建时间
                s += " AND o.ALARM_TIME >= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
                s += " AND o.ALARM_TIME <= to_date('" +endTime+ "','yyyy-mm-dd hh24:mi:ss') ";
                //超期是以结束任务算的，所以查询所有在这个时间段内并且是整体前一天结束的任务
                s4 += " AND PLAN_END_TIME >= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss')-1 ";
                s4 += " AND PLAN_END_TIME <= to_date('" +endTime+ "','yyyy-mm-dd hh24:mi:ss')-1 ";
            } else if (tableType == 0 || tableType == 1) {
                s += " AND trunc(o.ALARM_TIME) = trunc(sysdate) ";

                s4 +="  AND trunc(PLAN_END_TIME)=trunc(sysdate) ";
            } else if (tableType == 2) {
                Map map = weekTime.weekTime();
                Object mon = map.get("Mon");
                Object sun = map.get("Sun");
                s += " AND o.ALARM_TIME >= to_date('" + mon+ "','yyyy-mm-dd hh24:mi:ss') ";
                s += " AND o.ALARM_TIME <= to_date('" + sun+ "','yyyy-mm-dd hh24:mi:ss') ";

                //超期是以结束任务算的，所以查询所有在这个时间段内并且是整体前一天结束的任务
                s4 += " AND PLAN_END_TIME >= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss')-1 ";
                s4 += " AND PLAN_END_TIME <= to_date('" +sun+ "','yyyy-mm-dd hh24:mi:ss')-1 ";
            } else if (tableType == 3) {
                s += " AND to_char(o.ALARM_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";

                s4+=" AND (trunc(PLAN_END_TIME) BETWEEN to_date('"+weekTime.getFirstDayOfMonth()+"','yyyy-MM-dd')-1  AND to_date('"+weekTime.getLastDayOfMonth()+"','yyyy-MM-dd')-1 )";
            }
        }
        if(!StringUtils.isEmpty(deptId)){
            if(roletype==0){
                roletype=1;
                //条件查询通道单位
                s+=" AND  u.DEPTID='"+deptId+"'";
                s2+=deptId;

                s4+=" AND TD_ORG='"+deptId+"'";
            }else if(roletype==1 || roletype==2){
                s+=" AND  u.DEPTID='"+deptid+"'";
                s2+=deptid;

                s4+=" AND TD_ORG='"+deptid+"'";
            }
            s1+=" AND ORGID LIKE '%"+deptId+"%' ";
        }
        if(!StringUtils.isEmpty(companyid)){
            s+=" AND u.COMPANYID='"+companyid+"'";
            s3+=" AND ID='"+companyid+"' ";

            s4+=" AND WX_ORG='"+companyid+"'";
        }
        if(!StringUtils.isEmpty(taskname)){
            s+=" AND xs.TASK_NAME LIKE '%"+taskname+"%'";
        }
        if(!StringUtils.isEmpty(loginstatus)){
            s+=" AND u.LOGINSTATUS="+loginstatus;
        }
        if (roletype == 0) {
            //左下角查询通道单位
            String overdue = "SELECT nvl(a.OVERDUEDEPT,0) AS VALUE,r.DEPTNAME as NAME,r.ID FROM\n" +
                    "  (SELECT count(1) AS OVERDUEDEPT,u.DEPTID FROM ALARM_OVERDUE o LEFT JOIN XS_ZC_TASK xs ON o.TASK_ID=xs.ID\n" +
                    "LEFT JOIN USERINFO u ON o.USER_ID=u.ID WHERE 1=1 "+s+" GROUP BY u.DEPTID) a\n" +
                    "RIGHT JOIN (SELECT ID,DEPTNAME\n" +
                    " FROM RZTSYSDEPARTMENT\n" +
                    " WHERE DEPTSORT IS NOT NULL ORDER BY DEPTSORT) R ON a.DEPTID = r.ID ORDER BY VALUE DESC";
            //左下角查询外协单位
            String OVERDUECOMPANY = " SELECT b.ID,b.COMPANYNAME as NAME,nvl(a.OVERDUECOMPANY,0) as VALUE\n" +
                    " FROM (SELECT count(1) AS OVERDUECOMPANY,u.COMPANYID FROM ALARM_OVERDUE o LEFT JOIN XS_ZC_TASK xs ON o.TASK_ID=xs.ID\n" +
                    "LEFT JOIN USERINFO u ON o.USER_ID=u.ID WHERE 1=1 "+s+"  GROUP BY u.COMPANYID) a RIGHT JOIN \n" +
                    "  (SELECT ID, COMPANYNAME FROM RZTSYSCOMPANY WHERE 1=1 "+s1+") b ON a.COMPANYID = b.ID ORDER BY VALUE DESC\n";

            //查询通道单位对应的所有任务
            String overdueTotal = "SELECT nvl(a.OVERDUEDEPT,0) AS VALUE,r.DEPTNAME as NAME,r.ID FROM\n" +
                    "   (SELECT count(1) AS OVERDUEDEPT,u.DEPTID FROM XS_ZC_TASK xs\n" +
                    "     LEFT JOIN USERINFO u ON xs.CM_USER_ID=u.ID WHERE 1=1 "+s4+" GROUP BY u.DEPTID) a\n" +
                    " RIGHT JOIN (SELECT ID,DEPTNAME\n" +
                    "  FROM RZTSYSDEPARTMENT\n" +
                    "  WHERE DEPTSORT IS NOT NULL ORDER BY DEPTSORT) R ON a.DEPTID = r.ID ORDER BY VALUE DESC";
            String OVERDUECOMPANYTotal = "  SELECT b.ID,b.COMPANYNAME as NAME,nvl(a.OVERDUECOMPANY,0) as VALUE\n" +
                    "  FROM (SELECT count(1) AS OVERDUECOMPANY,u.COMPANYID FROM XS_ZC_TASK xs\n" +
                    "     LEFT JOIN USERINFO u ON xs.CM_USER_ID=u.ID WHERE 1=1 "+s4+" GROUP BY u.COMPANYID) a RIGHT JOIN\n" +
                    "   (SELECT ID, COMPANYNAME FROM RZTSYSCOMPANY WHERE 1=1 "+s1+") b ON a.COMPANYID = b.ID ORDER BY VALUE DESC";

            String user = "SELECT uu.*,us.REALNAME FROM USERINFO us RIGHT JOIN\n" +
                    "(SELECT count(u.ID) AS VALUE,u.ID FROM ALARM_OVERDUE o LEFT JOIN USERINFO u ON o.USER_ID=u.ID WHERE 1=1 "+s+" GROUP BY u.ID\n" +
                    "ORDER BY VALUE DESC) uu ON uu.ID=us.ID";
            try {
                Map map = new HashMap();
                //查询通道单位总数及超期总数
                List<Map<String, Object>> totalTaskMap = this.service.execSql(overdueTotal);//查询通道单位所有任务
                Map totalTask = new HashMap();
                if (totalTaskMap != null) {
                    for (Map mapp : totalTaskMap) {
                        totalTask.put(mapp.get("ID"), mapp.get("VALUE"));
                    }
                }
                //返回集合
                List<Map<String, Object>> maps = this.service.execSql(overdue);//查询通道单位所有超期任务
                for (Map mappp : maps) {
                    Object id = mappp.get("ID");
                    mappp.put("TOTALTASK", mappp.get(id) == null ? 0 : mappp.get(id));
                }

                //查询外协单位总数及超期总数
                List<Map<String, Object>> maps3 = this.service.execSql(OVERDUECOMPANYTotal);//查询外协所有任务
                Map companyTotalTask = new HashMap();
                if (maps3 != null) {
                    for (Map mapp : maps3) {
                        companyTotalTask.put(mapp.get("ID"), mapp.get("VALUE"));
                    }
                }
                //返回集合
                List<Map<String, Object>> maps1 = this.service.execSql(OVERDUECOMPANY);//查询外协所有超期告警
                for (Map mappp : maps1) {
                    Object id = mappp.get("ID");
                    mappp.put("TOTALTASK", mappp.get(id) == null ? 0 : mappp.get(id));
                }
                List<Map<String, Object>> maps2 = this.service.execSql(user);
                map.put("OVERDUE", maps);
                map.put("OVERDUECOMPANY", maps1);
                map.put("USER",maps2);
                return WebApiResponse.success(map);
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        }
        if (roletype == 1 || roletype == 2) {
            String overdue = "SELECT nvl(a.OVERDUECOMPANY, 0) AS VALUE,\n" +
                    "  r.DEPTNAME AS NAME,\n" +
                    "  r.ID FROM (\n" +
                    "SELECT count(1) AS OVERDUECOMPANY,u.CLASSID FROM ALARM_OVERDUE o LEFT JOIN XS_ZC_TASK xs ON o.TASK_ID=xs.ID\n" +
                    "LEFT JOIN USERINFO u ON o.USER_ID=u.ID WHERE 1=1 "+s+" GROUP BY u.CLASSID\n" +
                    ") a RIGHT JOIN (SELECT ID,DEPTNAME FROM (SELECT  ID,  DEPTNAME,  LASTNODE FROM RZTSYSDEPARTMENT\n" +
                    "    START WITH ID='"+s2+"' CONNECT BY PRIOR ID =DEPTPID)\n" +
                    "     WHERE LASTNODE = 0) r ON a.CLASSID = r.ID ORDER BY VALUE DESC";
            String OVERDUECOMPANY = " SELECT b.ID, b.COMPANYNAME AS NAME, nvl(a.OVERDUECOMPANY, 0) AS VALUE\n" +
                    " FROM ( SELECT count(1) AS OVERDUECOMPANY, u.COMPANYID\n" +
                    "   FROM ALARM_OVERDUE o LEFT JOIN XS_ZC_TASK xs ON o.TASK_ID = xs.ID\n" +
                    "     LEFT JOIN USERINFO u ON o.USER_ID = u.ID\n" +
                    "   WHERE 1 = 1 "+s+"\n" +
                    "   GROUP BY u.COMPANYID ) a RIGHT JOIN(\n" +
                    "SELECT ID, COMPANYNAME FROM RZTSYSCOMPANY\n" +
                    "  WHERE 1=1 "+s3+")  b ON a.COMPANYID = b.ID ORDER BY VALUE DESC";

            //查询通道单位对应的所有任务
            String overdueTotal = "SELECT nvl(a.OVERDUECOMPANY, 0) AS VALUE,\n" +
                    "  r.DEPTNAME AS NAME,\n" +
                    "  r.ID FROM (\n" +
                    "SELECT count(1) AS OVERDUECOMPANY,u.CLASSID FROM XS_ZC_TASK xs\n" +
                    "     LEFT JOIN USERINFO u ON xs.CM_USER_ID=u.ID WHERE 1=1 "+s4+" GROUP BY u.CLASSID\n" +
                    ") a RIGHT JOIN (SELECT ID,DEPTNAME FROM (SELECT  ID,  DEPTNAME,  LASTNODE FROM RZTSYSDEPARTMENT\n" +
                    "    START WITH ID='"+s2+"' CONNECT BY PRIOR ID =DEPTPID)\n" +
                    "     WHERE LASTNODE = 0) r ON a.CLASSID = r.ID ORDER BY VALUE DESC";
            String OVERDUECOMPANYTotal = "  SELECT b.ID, b.COMPANYNAME AS NAME, nvl(a.OVERDUECOMPANY, 0) AS VALUE\n" +
                    "  FROM ( SELECT count(1) AS OVERDUECOMPANY,u.COMPANYID FROM XS_ZC_TASK xs\n" +
                    "     LEFT JOIN USERINFO u ON xs.CM_USER_ID=u.ID WHERE 1=1 "+s4+" GROUP BY u.COMPANYID ) a RIGHT JOIN(\n" +
                    " SELECT ID, COMPANYNAME FROM RZTSYSCOMPANY\n" +
                    "   WHERE 1=1 "+s3+")  b ON a.COMPANYID = b.ID ORDER BY VALUE DESC";
            //查询左下角人员
            String user = "SELECT uu.*,us.REALNAME FROM USERINFO us RIGHT JOIN\n" +
                    "(SELECT count(u.ID) AS VALUE,u.ID FROM ALARM_OVERDUE o LEFT JOIN USERINFO u ON o.USER_ID=u.ID WHERE 1=1 "+s+" GROUP BY u.ID\n" +
                    "ORDER BY VALUE DESC) uu ON uu.ID=us.ID";
            try {
                Map map = new HashMap();
                //查询通道单位总数及超期总数
                List<Map<String, Object>> totalTaskMap = this.service.execSql(overdueTotal);// 查询通道单位所有任务
                Map totalTask = new HashMap();
                if (totalTaskMap != null) {
                    for (Map mapp : totalTaskMap) {
                        totalTask.put(mapp.get("ID"), mapp.get("VALUE"));
                    }
                }
                //返回集合
                List<Map<String, Object>> maps = this.service.execSql(overdue);//查询通道单位所有超期任务
                for (Map mappp : maps) {
                    Object id = mappp.get("ID");
                    mappp.put("TOTALTASK", mappp.get(id) == null ? 0 : mappp.get(id));
                }

                //查询外协单位总数及超期总数
                List<Map<String, Object>> maps3 = this.service.execSql(OVERDUECOMPANYTotal);//查询外协所有任务
                Map companyTotalTask = new HashMap();
                if (maps3 != null) {
                    for (Map mapp : maps3) {
                        companyTotalTask.put(mapp.get("ID"), mapp.get("VALUE"));
                    }
                }
                //返回集合
                List<Map<String, Object>> maps1 = this.service.execSql(OVERDUECOMPANY);//查询外协所有超期告警
                for (Map mappp : maps1) {
                    Object id = mappp.get("ID");
                    mappp.put("TOTALTASK", mappp.get(id) == null ? 0 : mappp.get(id));
                }
                List<Map<String, Object>> maps2 = this.service.execSql(user);
                map.put("OVERDUE", maps);
                map.put("OVERDUECOMPANY", maps1);
                map.put("USER",maps2);
                return WebApiResponse.success(map);
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        }
        return null;
    }

}
