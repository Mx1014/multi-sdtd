package com.rzt.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/31
 */
@Service
public class GJService extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(GJService.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 告警查询
     * @return
     */
    public WebApiResponse GJ(){
        //告警数sql
        Map<String, Object> map = null;
        try {
            String gjSql = " SELECT (SELECT count(1)" +
                    "        FROM (SELECT * FROM(SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS" +
                    "       FROM (SELECT TASK_ID,USER_ID" +
                    "             FROM MONITOR_CHECK_EJ" +
                    "             WHERE WARNING_TYPE = 10   AND trunc(CREATE_TIME)=trunc(sysdate)   ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID" +
                    "         LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                    "                                                        AND trunc(x.PLAN_START_TIME) = trunc(sysdate)) UNION ALL" +
                    "              SELECT * FROM ( SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME," +
                    "          x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS" +
                    "        FROM (SELECT TASK_ID,USER_ID" +
                    "              FROM MONITOR_CHECK_EJ" +
                    "              WHERE WARNING_TYPE = 4   AND trunc(CREATE_TIME)=trunc(sysdate)   )" +
                    "             e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID" +
                    "          LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                    "        AND trunc(x.PLAN_START_TIME) = trunc(sysdate)))) as WAS," +
                    "        (SELECT count(1)  FROM(" +
                    "    SELECT" +
                    "         e.USER_ID,u.REALNAME,u.CLASSNAME,u.DEPT, u.COMPANYNAME," +
                    "         CASE u.WORKTYPE" +
                    "         WHEN 1" +
                    "           THEN '看护'" +
                    "         WHEN 2" +
                    "           THEN '巡视'" +
                    "         WHEN 3" +
                    "           THEN '现场稽查' END AS WORKTYPE," +
                    "         e.a          AS MORE," +
                    "         u.DEPTID," +
                    "         e.CREATE_TIME," +
                    "         e.ONLINE_TIME" +
                    "       FROM (SELECT" +
                    "                        count(1) as a," +
                    "               USER_ID, MAX (CREATE_TIME) AS CREATE_TIME," +
                    "                        nvl(to_char( MAX (ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') as ONLINE_TIME" +
                    "             FROM MONITOR_CHECK_EJ" +
                    "             WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2)  AND trunc(CREATE_TIME) = trunc(sysdate)  GROUP BY USER_ID) e LEFT JOIN USERINFO u" +
                    "           ON e.USER_ID = u.ID) ch LEFT JOIN MONITOR_CHECK_EJ ce" +
                    "      ON ch.USER_ID=ce.USER_ID AND ch.CREATE_TIME=ce.CREATE_TIME  WHERE ch.ONLINE_TIME = '人员未上线') AS OFF_LINE," +
                    "  (SELECT" +
                    "     count(1)" +
                    "   FROM (SELECT TASK_ID" +
                    "         FROM MONITOR_CHECK_EJ" +
                    "         WHERE WARNING_TYPE = 1   AND trunc(CREATE_TIME) = trunc(sysdate)" +
                    "        ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID) AS CQ," +
                    "  (SELECT count(1)" +
                    "   FROM (SELECT *" +
                    "         FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON" +
                    "               FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                    "               WHERE WARNING_TYPE = 5  AND  trunc(CREATE_TIME) = trunc(sysdate))" +
                    "         UNION ALL" +
                    "         SELECT * FROM (SELECT x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '未到位' as  type,e.REASON" +
                    "                        FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                    "                        WHERE WARNING_TYPE = 3  AND  trunc(CREATE_TIME) = trunc(sysdate)))) AS XSBHG," +
                    "  (SELECT count(1) FROM" +
                    "    ( SELECT u.TASK_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1" +
                    "    AND  trunc(CREATE_TIME) = trunc(sysdate)" +
                    "    )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID) AS KHTG,10 AS JCBDW" +
                    "       FROM dual";
            map = this.execSqlSingleResult(gjSql, null);
            LOGGER.info("告警信息查询成功");
        }catch (Exception e){
            LOGGER.error("告警信息查询失败"+e.getMessage());
            return WebApiResponse.erro("告警信息查询失败"+e.getMessage());
        }

        return WebApiResponse.success(map);
    }




    /**
     * 二级页面使用  按照部门分组 返回所有部门   不传deptId
     * 三级页面使用 按照部门查询     传deptId
     * @param deptId
     * @return
     */
    public WebApiResponse AlarmList(String deptId) {
        String s = "";
        String alarm = "";
        try {

                if(null != deptId && !"".equals(deptId)){
                    alarm = " SELECT " +
                            "  NVL(A.OFFLINES,0) AS OFFLINES, " +
                            "  NVL(A.ANSWERTIME,0) AS ANSWERTIME, " +
                            "  NVL(A.OVERDUE,0) AS OVERDUE, " +
                            "  NVL(A.TEMPORARILY,0) AS TEMPORARILY, " +
                            "  NVL(A.UNQUALIFIEDPATROL,0) AS UNQUALIFIEDPATROL, " +
                            "  T.ID AS DEPTID, " +
                            "  T.DEPTNAME " +
                            "  FROM RZTSYSDEPARTMENT T LEFT JOIN (SELECT " +
                            "     nvl(sum(decode(WARNING_TYPE, 2, 1, 8, 1, 0)), 0)  AS OFFLINES, " +
                            "     nvl(sum(decode(WARNING_TYPE, 4, 1, 10, 1, 0)), 0) AS ANSWERTIME, " +
                            "     nvl(sum(decode(WARNING_TYPE, 1, 1, 0)), 0)        AS OVERDUE, " +
                            "     nvl(sum(decode(WARNING_TYPE, 7, 1, 0)), 0)        AS TEMPORARILY, " +
                            "     nvl(sum(decode(WARNING_TYPE, 5, 1, 0)), 0)        AS UNQUALIFIEDPATROL, " +
                            "     DEPTID " +
                            "   FROM MONITOR_CHECK_EJ " +
                            "   WHERE 1 = 1 AND trunc(CREATE_TIME) = trunc(sysdate) " +
                            "   GROUP BY DEPTID) A ON T.ID = A.DEPTID " +
                            "   WHERE T.DEPTSORT IS NOT NULL  AND t.ID = '"+deptId+"'  " +
                            "   ORDER BY T.DEPTSORT ";
                }else {
                    alarm = " SELECT " +
                            "  NVL(A.OFFLINES,0) AS OFFLINES, " +
                            "  NVL(A.ANSWERTIME,0) AS ANSWERTIME, " +
                            "  NVL(A.OVERDUE,0) AS OVERDUE, " +
                            "  NVL(A.TEMPORARILY,0) AS TEMPORARILY, " +
                            "  NVL(A.UNQUALIFIEDPATROL,0) AS UNQUALIFIEDPATROL, " +
                            "  T.ID AS DEPTID, " +
                            "  T.DEPTNAME " +
                            "  FROM RZTSYSDEPARTMENT T LEFT JOIN (SELECT " +
                            "     nvl(sum(decode(WARNING_TYPE, 2, 1, 8, 1, 0)), 0)  AS OFFLINES, " +
                            "     nvl(sum(decode(WARNING_TYPE, 4, 1, 10, 1, 0)), 0) AS ANSWERTIME, " +
                            "     nvl(sum(decode(WARNING_TYPE, 1, 1, 0)), 0)        AS OVERDUE, " +
                            "     nvl(sum(decode(WARNING_TYPE, 7, 1, 0)), 0)        AS TEMPORARILY, " +
                            "     nvl(sum(decode(WARNING_TYPE, 5, 1, 0)), 0)        AS UNQUALIFIEDPATROL, " +
                            "     DEPTID " +
                            "   FROM MONITOR_CHECK_EJ " +
                            "   WHERE 1 = 1 AND trunc(CREATE_TIME) = trunc(sysdate) " +
                            "   GROUP BY DEPTID) A ON T.ID = A.DEPTID " +
                            "   WHERE T.DEPTSORT IS NOT NULL " +
                            "   ORDER BY T.DEPTSORT ";
                }


                List<Map<String, Object>> alarms = this.execSql(alarm, null);
                return WebApiResponse.success(alarms);

        } catch (Exception e) {
            return WebApiResponse.erro("告警三级查询错误"+e.getMessage());
        }
    }


    /**
     * 一级页
     * @param deptId
     * @return
     */
    public WebApiResponse AlarmList1(String deptId) {
        String s = "";
        String alarm = "";
        try {

            if(null != deptId && !"".equals(deptId)){
                alarm = " SELECT " +
                        "  NVL(A.OFFLINES,0) AS OFFLINES, " +
                        "  NVL(A.ANSWERTIME,0) AS ANSWERTIME, " +
                        "  NVL(A.OVERDUE,0) AS OVERDUE, " +
                        "  NVL(A.TEMPORARILY,0) AS TEMPORARILY, " +
                        "  NVL(A.UNQUALIFIEDPATROL,0) AS UNQUALIFIEDPATROL, " +
                        "  T.ID AS DEPTID, " +
                        "  T.DEPTNAME " +
                        "  FROM RZTSYSDEPARTMENT T LEFT JOIN (SELECT " +
                        "     nvl(sum(decode(WARNING_TYPE, 2, 1, 8, 1, 0)), 0)  AS OFFLINES, " +
                        "     nvl(sum(decode(WARNING_TYPE, 4, 1, 10, 1, 0)), 0) AS ANSWERTIME, " +
                        "     nvl(sum(decode(WARNING_TYPE, 1, 1, 0)), 0)        AS OVERDUE, " +
                        "     nvl(sum(decode(WARNING_TYPE, 7, 1, 0)), 0)        AS TEMPORARILY, " +
                        "     nvl(sum(decode(WARNING_TYPE, 5, 1, 0)), 0)        AS UNQUALIFIEDPATROL, " +
                        "     DEPTID " +
                        "   FROM MONITOR_CHECK_EJ " +
                        "   WHERE 1 = 1 AND trunc(CREATE_TIME) = trunc(sysdate) " +
                        "   GROUP BY DEPTID) A ON T.ID = A.DEPTID " +
                        "   WHERE T.DEPTSORT IS NOT NULL  AND t.ID = '"+deptId+"'  " +
                        "   ORDER BY T.DEPTSORT ";
            }else {
                alarm = " SELECT " +
                        "  NVL(A.OFFLINES,0) AS OFFLINES, " +
                        "  NVL(A.ANSWERTIME,0) AS ANSWERTIME, " +
                        "  NVL(A.OVERDUE,0) AS OVERDUE, " +
                        "  NVL(A.TEMPORARILY,0) AS TEMPORARILY, " +
                        "  NVL(A.UNQUALIFIEDPATROL,0) AS UNQUALIFIEDPATROL, " +
                        "  T.ID AS DEPTID, " +
                        "  T.DEPTNAME " +
                        "  FROM RZTSYSDEPARTMENT T LEFT JOIN (SELECT " +
                        "     nvl(sum(decode(WARNING_TYPE, 2, 1, 8, 1, 0)), 0)  AS OFFLINES, " +
                        "     nvl(sum(decode(WARNING_TYPE, 4, 1, 10, 1, 0)), 0) AS ANSWERTIME, " +
                        "     nvl(sum(decode(WARNING_TYPE, 1, 1, 0)), 0)        AS OVERDUE, " +
                        "     nvl(sum(decode(WARNING_TYPE, 7, 1, 0)), 0)        AS TEMPORARILY, " +
                        "     nvl(sum(decode(WARNING_TYPE, 5, 1, 0)), 0)        AS UNQUALIFIEDPATROL, " +
                        "     DEPTID " +
                        "   FROM MONITOR_CHECK_EJ " +
                        "   WHERE 1 = 1 AND trunc(CREATE_TIME) = trunc(sysdate) " +
                        "   GROUP BY DEPTID) A ON T.ID = A.DEPTID " +
                        "   WHERE T.DEPTSORT IS NOT NULL " +
                        "   ORDER BY T.DEPTSORT ";
            }


            List<Map<String, Object>> alarms = this.execSql(alarm, null);
            int ANSWERTIME = 0;
            int TEMPORARILY = 0;
            int OFFLINES = 0;
            int UNQUALIFIEDPATROL = 0;
            int OVERDUE = 0;
            for (Map<String, Object> map : alarms) {
                ANSWERTIME += Integer.parseInt(map.get("ANSWERTIME")==null?"0" : map.get("ANSWERTIME").toString());
                TEMPORARILY += Integer.parseInt(map.get("TEMPORARILY")==null?"0" : map.get("TEMPORARILY").toString());
                OFFLINES += Integer.parseInt(map.get("OFFLINES")==null?"0" : map.get("OFFLINES").toString());
                UNQUALIFIEDPATROL += Integer.parseInt(map.get("UNQUALIFIEDPATROL")==null?"0" : map.get("UNQUALIFIEDPATROL").toString());
                OVERDUE += Integer.parseInt(map.get("OVERDUE")==null?"0" : map.get("OVERDUE").toString());

            }
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ANSWERTIME",ANSWERTIME);
            hashMap.put("TEMPORARILY",TEMPORARILY);
            hashMap.put("OFFLINES",OFFLINES);
            hashMap.put("UNQUALIFIEDPATROL",UNQUALIFIEDPATROL);
            hashMap.put("OVERDUE",OVERDUE);
            hashMap.put("ABCDEFG",0);
            return WebApiResponse.success(hashMap);

        } catch (Exception e) {
            return WebApiResponse.erro("告警三级查询错误"+e.getMessage());
        }
    }

 /*----------------------------- 三级页下半部  列表数据开始-------------------------------------------- */

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
    public WebApiResponse OfflinesList(Integer workType, Integer page, Integer size, String startTime, String endTime, String deptId, String taskType,String loginType) {
        org.springframework.data.domain.Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";

        if (!StringUtils.isEmpty(workType)) {
            listLike.add(workType);
            s1 += " AND worktypes= ?" + listLike.size();
        }
        if (null != deptId && !"".equals(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginType)){
            listLike.add(loginType);
            s1 += "  LOGINSTATUS=?" + listLike.size();
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(taskType)) {
            s += " and TASK_TYPE = " + taskType;
        }

        String sql = " SELECT DISTINCT " +
                "  ce.USER_ID AS userID, " +
                "  ce.REASON, " +
                "  ce.TASK_TYPE, " +
                "  ce.TASK_ID, " +
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
                "        e.CREATE_TIME, " +
                "        e.ONLINE_TIME " +
                "      FROM (SELECT " +
                "              count(1)                                                            AS a, " +
                "              ej.USER_ID, " +
                "              MAX(ej.CREATE_TIME)                                                 AS CREATE_TIME, " +
                "              nvl(to_char(MAX(ej.ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') AS ONLINE_TIME " +
                "            FROM MONITOR_CHECK_EJ ej " +
                "            WHERE (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2) AND USER_ID !='null'  " + s +
                "            GROUP BY USER_ID) e JOIN USERINFO u ON e.USER_ID = u.ID AND u.USERDELETE=1) ch LEFT JOIN MONITOR_CHECK_EJ ce " +
                "    ON ch.USER_ID = ce.USER_ID AND ch.CREATE_TIME = ce.CREATE_TIME  " + s1;
        try {
            return WebApiResponse.success(this.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    /**
     * 未按时开始任务
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param deptId
     * @param taskType
     * @return
     */
    public WebApiResponse answertimeList(Integer page, Integer size, String startTime, String endTime, String deptId, String taskType) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";

        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s1 += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s1 += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s1 += " AND trunc(CREATE_TIME)=trunc(sysdate) ";
        }
       if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND u.DEPTID = ?" + listLike.size();
        }
        String allSql = "";
        String xssql = "  SELECT * FROM (SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME," +
                "  x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS,e.* " +
                "FROM (SELECT TASK_ID,USER_ID,TASK_TYPE " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 4 " + s1 + " ) e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1 " +
                //  AND trunc(x.PLAN_START_TIME) = trunc(sysdate)
                // 此代码标识当前查询只查询计划开始时间在当天的 --->李成阳
                "     AND trunc(x.PLAN_START_TIME) = trunc(sysdate)    " +
                "" + s + " ) ";


        String khsql = " (SELECT x.TASK_NAME,nvl(x.PLAN_START_TIME,sysdate-2) as PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS,e.* " +
                " FROM (SELECT TASK_ID,USER_ID,TASK_TYPE " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 10 " + s1 + " ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1" +
                //  AND trunc(x.PLAN_START_TIME) = trunc(sysdate)
                // 此代码标识当前查询只查询计划开始时间在当天的 --->李成阳
                "    AND trunc(x.PLAN_START_TIME) = trunc(sysdate) " +

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
            return WebApiResponse.success(this.execSqlPage(pageable, allSql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }

    }

    /**
     * 超期
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param deptId
     * @return
     */
    public WebApiResponse overdueList(Integer page, Integer size,  String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";

        if (!StringUtils.isEmpty(deptId)) {
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
        String sql = " SELECT " +
                "  k.TASK_NAME,u.DEPT,u.CLASSNAME,u.COMPANYNAME,u.REALNAME,u.PHONE,k.PLAN_END_TIME,e.* " +
                "FROM (SELECT TASK_ID,USER_ID,TASK_TYPE  " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 1 " + s +
                "     ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID ";
        try {
            return WebApiResponse.success(this.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    /**
     * 看护脱岗
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param deptId
     * @return
     */
    public WebApiResponse khanswertimeList(Integer page, Integer size, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";

        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s1 += " AND u.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s1 += " AND u.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            //s1 += " AND trunc(u.CREATE_TIME)=trunc(sysdate) ";
            s1 += " AND trunc(t.START_TIME)=trunc(sysdate) ";
        }

        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }

        String sql="SELECT t.*,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.DEPT,u.PHONE,'1' AS TASK_TYPE,k.TASK_NAME  FROM " +
                " ( SELECT u.TASK_ID,u.USER_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u " +
                " LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1  AND t.TIME_STATUS=1 " + s1  +
                " )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID" + s;
        return WebApiResponse.success(this.execSqlPage(pageable, sql, listLike.toArray()));
    }


    public WebApiResponse unqualifiedpatrolList(Integer page, Integer size, String startTime, String endTime, String deptId, String userName) {

        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND e.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND e.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(e.CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND e.DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(userName)) {
            listLike.add(userName + "%");
            s += " AND u.REALNAME LIKE ?" + listLike.size();
        }
        //  修改增加未到位类别   增加未到位原因字段      ---> 李成阳
        String sql = "SELECT e.CREATE_TIME,x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON,e.TASK_ID,e.USER_ID,e.TASK_TYPE" +
                "      FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                "      WHERE WARNING_TYPE = 5 "+s+"";
        try {
            return WebApiResponse.success(this.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
     /*----------------------------- 三级页下半部  列表数据结束-------------------------------------------- */



}
