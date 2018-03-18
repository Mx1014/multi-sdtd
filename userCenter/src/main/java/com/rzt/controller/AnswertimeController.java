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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
* @Method 告警中 未按时开始
* @Description         
* @param 
* @return 
* @date 2018/3/15 16:21
* @author nwz
*/
@RestController
@RequestMapping("ANSWERTIME")
public class AnswertimeController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    /***
    * @Method answertimeListNew 未按时开始任务列表
    * @Description         
    * @param [tableType, loginstatus, taskname, companyid, page, size, currentUserId, startTime, endTime, deptId, taskType]
    * @return com.rzt.util.WebApiResponse
    * @date 2018/3/16 11:45
    * @author nwz
    */
    @RequestMapping("answertimeListNew")
    public WebApiResponse answertimeListNew(Integer tableType, Integer loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {
        StringBuffer conditionSql = new StringBuffer("where 1 = 1");
        ArrayList<Object> paramList = new ArrayList<>();
        //没事闲着干嘛 拼个sql耍耍
        String s = answerTimeParams(tableType, loginstatus, companyid, currentUserId, startTime, endTime, deptId, conditionSql, paramList,taskname);

        conditionSql.append(" order by ALARM_TIME desc ");
        s += conditionSql.toString();
        Pageable pageable = new PageRequest(page, size);
        try {
            Page<Map<String, Object>> maps = this.service.execSqlPage(pageable, s, paramList.toArray());
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro(e.getMessage());
        }

    }

    private String answerTimeParams(Integer tableType, Integer loginstatus, String companyid, String currentUserId, String startTime, String endTime, String deptId, StringBuffer conditionSql, ArrayList<Object> paramList, String taskname) {
        //1.巡视sql
        String xsSql = "SELECT\n" +
                "  tt.TASK_NAME,\n" +
                "  t.ALARM_TIME,\n" +
                "  t.CHECK_STATUS,\n" +
                "  tt.PLAN_START_TIME,\n" +
                "  tt.REAL_START_TIME,\n" +
                "  u.DEPTID,\n" +
                "  u.COMPANYID,\n" +
                "  u.CLASSID,\n" +
                "  u.REALNAME,\n" +
                "  u.loginstatus,\n" +
                "  u.DEPT,\n" +
                "  u.COMPANYNAME,2 task_type,\n" +
                "  u.CLASSNAME,u.PHONE,ROUND(TO_NUMBER(nvl(tt.REAL_START_TIME, sysdate) - tt.PLAN_START_TIME) * 24 * 60 * 60 * 1000) timeLong\n" +
                "FROM ALARM_NOT_ON_TIME_TASK t\n" +
                "  JOIN xs_zc_task tt ON t.TASK_TYPE = 2 AND t.TASK_ID = tt.id\n" +
                "  join USERINFO u on u.id = t.USER_ID";
        //2.看护sql
        String khSql = "SELECT\n" +
                "  tt.TASK_NAME,\n" +
                "  t.ALARM_TIME,\n" +
                "  t.CHECK_STATUS,\n" +
                "  tt.PLAN_START_TIME,\n" +
                "  tt.REAL_START_TIME,\n" +
                "  u.DEPTID,\n" +
                "  u.COMPANYID,\n" +
                "  u.CLASSID,\n" +
                "  u.REALNAME,\n" +
                "  u.loginstatus,\n" +
                "  u.DEPT,\n" +
                "  u.COMPANYNAME,1 task_type,\n" +
                "  u.CLASSNAME,u.PHONE,ROUND(TO_NUMBER(nvl(tt.REAL_START_TIME, sysdate) - tt.PLAN_START_TIME) * 24 * 60 * 60 * 1000) timeLong\n" +
                "FROM ALARM_NOT_ON_TIME_TASK t\n" +
                "  JOIN KH_TASK tt ON t.TASK_TYPE = 1 AND t.TASK_ID = tt.id\n" +
                "  join USERINFO u on u.id = t.USER_ID";

        /*数据权限的筛选*/
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (roletype == 1 || roletype == 2) {
            paramList.add(deptid);
            conditionSql.append(" AND e.DEPTID = ?" + paramList.size());
        }


        /*1.时间维度上筛选*/
        if(StringUtils.isEmpty(startTime)) {
            //如果用户没有选择开始时间和结束时间 此时走tableType的判断
            if(tableType == 0) {
                //当前任务 当天&&未处理
                conditionSql.append(" and e.ALARM_TIME >= trunc(sysdate) and e.CHECK_STATUS = 0 ");
            } else if(tableType == 1) {
                //当天
                conditionSql.append(" and e.ALARM_TIME >= trunc(sysdate) ");
            }  else if (tableType == 2) {
                Map map = weekTime.weekTime();
                Object mon = map.get("Mon");
                Object sun = map.get("Sun");
                paramList.add(mon);
                conditionSql.append(" AND e.ALARM_TIME >= to_date(?" + paramList.size() + ",'yyyy-mm-dd hh24:mi:ss') ");
                paramList.add(sun);
                conditionSql.append(" AND e.ALARM_TIME <= to_date(?" + paramList.size() + ",'yyyy-mm-dd hh24:mi:ss') " );
            } else if (tableType == 3) {
                conditionSql.append(" AND to_char(e.ALARM_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ");
            }
        } else {
            //根据用户选择的开始时间和结束时间对结果进行筛选
            paramList.add(startTime);
            conditionSql.append(" AND e.ALARM_TIME >= to_date(?" + paramList.size() + ",'yyyy-mm-dd hh24:mi:ss') ");
            paramList.add(endTime);
            conditionSql.append(" AND e.ALARM_TIME <= to_date(?" + paramList.size() + ",'yyyy-mm-dd hh24:mi:ss') ");
        }
        /*2.在线状态查询*/
        if(!StringUtils.isEmpty(loginstatus)) {
            paramList.add(loginstatus);
            conditionSql.append(" AND e.LOGINSTATUS  = ?" + paramList.size());
        }
        /*3.通道单位查询*/
        if (!StringUtils.isEmpty(deptId)) {
            paramList.add(deptId);
            conditionSql.append(" AND e.DEPTID = ?" + paramList.size());
        }
        /*4.外协单位查询*/
        if (!StringUtils.isEmpty(companyid)) {
            paramList.add(companyid);
            conditionSql.append(" AND e.COMPANYID = ?" + paramList.size());
        }
        /*5.任务名称查询*/
        if (!StringUtils.isEmpty(taskname)) {
            paramList.add("%" + taskname + "%");
            conditionSql.append(" AND e.task_name like ?" + paramList.size());
        }

        return "select * from (" + xsSql + " union all " + khSql + ") e ";
    }

    @RequestMapping("answertimeTable2")
    public WebApiResponse leftTable2(Integer tableType, Integer loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {
        StringBuffer conditionSql = new StringBuffer("where 1 = 1");
        ArrayList<Object> paramList = new ArrayList<>();
        String s = answerTimeParams(tableType, loginstatus, companyid, currentUserId, startTime, endTime, deptId, conditionSql, paramList, taskname);
        s += conditionSql.toString();
        String param1 = "";
        String param2 = "";
        /*数据权限的筛选*/
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");

        if(!StringUtils.isEmpty(deptId)) {
            paramList.add(deptId);
            param1 = "lastnode = 0\n" +
                    "              START WITH id = ?"+ paramList.size() +" CONNECT BY PRIOR id = DEPTPID";
            param2 = "classid";
        } else if( roletype == 1 || roletype == 2) {
            paramList.add(deptid);
            param1 = "lastnode = 0\n" +
                    "              START WITH id = ?"+ paramList.size() +" CONNECT BY PRIOR id = DEPTPID";
            param2 = "classid";
        } else if(roletype == 0) {
            param1 = " deptsort IS NOT NULL";
            param2 = "deptid";
        }
        String sql = "SELECT\n" +
                "  ttt.DEPTNAME,\n" +
                "  ttt.ID,\n" +
                "  sum(decode(t.TASK_TYPE, 1, 1, 0))                                                                kh,\n" +
                "  sum(decode(t.TASK_TYPE, 2, 1, 0))                                                                xs,\n" +
                "  nvl(avg(ROUND(TO_NUMBER(nvl(t.REAL_START_TIME, sysdate) - PLAN_START_TIME) * 24 * 60 * 60 * 1000)), 0) avg,\n" +
                "  nvl(sum(ROUND(TO_NUMBER(nvl(REAL_START_TIME, sysdate) - PLAN_START_TIME) * 24 * 60 * 60 * 1000)), 0)    sum\n" +
                "FROM (" + s + ") t\n" +
                "  RIGHT JOIN (SELECT\n" +
                "                ID,\n" +
                "                DEPTNAME,\n" +
                "                LASTNODE\n" +
                "              FROM RZTSYSDEPARTMENT\n" +
                "              WHERE " + param1 + ") ttt\n" +
                "    ON ttt.id = t." + param2 + "\n" +
                "GROUP BY ttt.id, ttt.DEPTNAME order by sum DESC ";
        try {
            List<Map<String, Object>> maps = this.service.execSql(sql, paramList.toArray());
            List<Map<String, Object>> maps2 = wxChouYiXia(tableType, loginstatus, companyid, currentUserId, startTime, endTime, deptId,taskname);
            HashMap<String, Object> res = new HashMap<>();
            res.put("dept",maps);
            res.put("company",maps2);
            return WebApiResponse.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

        @RequestMapping("answertimeTable")
    public WebApiResponse leftTable(Integer tableType, Integer loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {

        StringBuffer conditionSql = new StringBuffer("where 1 = 1");
        ArrayList<Object> paramList = new ArrayList<>();

        /*数据权限的筛选*/
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (roletype == 1 || roletype == 2) {
            paramList.add(deptid);
            conditionSql.append(" AND e.DEPTID = ?");
        }
        String param1 = "";
        String param2 = "";
        if(!StringUtils.isEmpty(deptId) || roletype == 1 || roletype == 2) {
            param1 = "lastnode = 0\n" +
                    "              START WITH id = ? CONNECT BY PRIOR id = DEPTPID";
            paramList.add(deptid);
            param2 = "CLASS_ID";
        }
        if(roletype == 0) {
            param1 = " deptsort IS NOT NULL";
            param2 = "TD_ORG";
        }
        String sql = "SELECT\n" +
                "  ttt.DEPTNAME,\n" +
                "  ttt.ID,\n" +
                "  sum(decode(t.TASK_TYPE, 1, 1, 0))                                                                kh,\n" +
                "  sum(decode(t.TASK_TYPE, 2, 1, 0))                                                                xs,\n" +
                "  nvl(avg(ROUND(TO_NUMBER(nvl(tt.REAL_START_TIME, sysdate) - PLAN_START_TIME) * 24 * 60 * 60 * 1000)), 0) avg,\n" +
                "  nvl(sum(ROUND(TO_NUMBER(nvl(REAL_START_TIME, sysdate) - PLAN_START_TIME) * 24 * 60 * 60 * 1000)), 0)    sum\n" +
                "FROM ALARM_NOT_ON_TIME_TASK t\n" +
                "  JOIN (SELECT\n" +
                "          id,\n" +
                "          TD_ORG,\n" +
                "          WX_ORG,\n" +
                "          CLASS_ID,\n" +
                "          REAL_START_TIME,\n" +
                "          PLAN_START_TIME\n" +
                "        FROM XS_ZC_TASK\n" +
                "        UNION ALL (SELECT\n" +
                "                     kh.id,\n" +
                "                     u.DEPTID,\n" +
                "                     u.COMPANYID,\n" +
                "                     u.CLASSNAME,\n" +
                "                     kh.REAL_START_TIME,\n" +
                "                     kh.PLAN_START_TIME\n" +
                "                   FROM kh_task kh\n" +
                "                     JOIN RZTSYSUSER u ON kh.USER_ID = u.ID)) tt ON t.TASK_ID = tt.id\n" +
                "  RIGHT JOIN (SELECT\n" +
                "                ID,\n" +
                "                DEPTNAME,\n" +
                "                LASTNODE\n" +
                "              FROM RZTSYSDEPARTMENT\n" +
                "              WHERE " + param1 + ") ttt\n" +
                "    ON ttt.id = tt." + param2 + "\n" +
                "GROUP BY ttt.id, ttt.DEPTNAME order by sum DESC ";
        try {
            List<Map<String, Object>> maps = this.service.execSql(sql, paramList.toArray());
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }


    @RequestMapping("answertimeTableWx")
    public WebApiResponse answertimeTableWx(Integer tableType, Integer loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {


        try {
            List<Map<String, Object>> maps = wxChouYiXia(tableType, loginstatus, companyid, currentUserId, startTime, endTime, deptId,taskname);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    private List<Map<String, Object>> wxChouYiXia(Integer tableType, Integer loginstatus, String companyid, String currentUserId, String startTime, String endTime, String deptId,String taskname) {
        StringBuffer conditionSql = new StringBuffer("where 1 = 1");
        ArrayList<Object> paramList = new ArrayList<>();
        String s = answerTimeParams(tableType, loginstatus, companyid, currentUserId, startTime, endTime, deptId, conditionSql, paramList, taskname);
        s += conditionSql.toString();
        /*数据权限的筛选*/
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String param1 = "";
        String param2 = "";
        if(!StringUtils.isEmpty(deptId)) {
            paramList.add("%" + deptId + "%");
            param1 = "and ORGID like ?" + paramList.size();
            param2 = "companyid";
        } else if(roletype == 1 || roletype == 2) {
            paramList.add("%" + deptId + "%");
            param1 = "and ORGID like ?" + paramList.size();
            param2 = "companyid";
        } else if(roletype == 0) {
            param2 = "companyid";
        }
        String sql = "SELECT\n" +
                "  ttt.DEPTNAME,\n" +
                "  ttt.ID,\n" +
                "  sum(decode(t.TASK_TYPE, 1, 1, 0))                                                                kh,\n" +
                "  sum(decode(t.TASK_TYPE, 2, 1, 0))                                                                xs,\n" +
                "  nvl(avg(ROUND(TO_NUMBER(nvl(t.REAL_START_TIME, sysdate) - PLAN_START_TIME) * 24 * 60 * 60 * 1000)), 0) avg,\n" +
                "  nvl(sum(ROUND(TO_NUMBER(nvl(REAL_START_TIME, sysdate) - PLAN_START_TIME) * 24 * 60 * 60 * 1000)), 0)    sum\n" +
                "FROM ("+ s + ") t \n" +
                "  RIGHT JOIN (SELECT\n" +
                "                ID,\n" +
                "                COMPANYNAME deptname\n" +
                "              FROM RZTSYSCOMPANY\n" +
                "              WHERE 1 = 1 " + param1 + ") ttt\n" +
                "    ON ttt.id = t." + param2 + "\n" +
                "GROUP BY ttt.id, ttt.DEPTNAME order by sum DESC ";
        return this.service.execSql(sql, paramList.toArray());
    }


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
