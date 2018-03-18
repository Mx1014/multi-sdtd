package com.rzt.controller;

import com.alibaba.fastjson.JSONArray;
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
@RequestMapping("UNQUALIFIEDPATROL")
public class UnqualifiedpatrolController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("NewUnqualifiedpatrolList")
    public WebApiResponse NewUnqualifiedpatrolList(Integer tableType, Integer status, String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String userName) {
        Pageable pageable = new PageRequest(page, size);
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String s = "";
        if (tableType == 0) {
            s += " AND AUDIT_STATUS = 0 ";
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            s += " AND ul.CREATE_TIME >= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND ul.CREATE_TIME <= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(ul.CREATE_TIME) = trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            s += " AND ul.CREATE_TIME >= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND ul.CREATE_TIME <= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_char(ul.CREATE_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
        }
        if (!StringUtils.isEmpty(deptId)) {
            s += " AND u.DEPTID= '" + deptId + "' ";
        }
        if (roletype == 1 || roletype == 2) {
            s += " AND u.DEPTID= '" + deptid + "' ";
        }
        if (!StringUtils.isEmpty(companyid)) {
            s += " AND u.COMPANYID= '" + companyid + "' ";
        }
        if (!StringUtils.isEmpty(taskname)) {
            s += " AND x.TASK_NAME LIKE '%" + taskname.trim() + "%' ";
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            s += " AND u.LOGINSTATUS=" + loginstatus + " ";
        }
        if (!StringUtils.isEmpty(status)) {
            s += " AND AUDIT_STATUS=" + status + " ";
        }
        String sql = " SELECT ul.CREATE_TIME,ul.ALARM_TYPE,x.TASK_NAME,u.DEPTID,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE,ul.IS_DW_TOUR,ul.UNQUALIFIED_REASONS,u.LOGINSTATUS,x.ID AS TASK_ID,u.ID AS USER_ID " +
                "  FROM ALARM_UNQUALIFIEDPATROL ul LEFT JOIN XS_ZC_TASK x ON ul.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON u.ID = ul.USER_ID WHERE 1=1 " + s + " ORDER BY ul.CREATE_TIME ";
        try {
            Page<Map<String, Object>> maps = this.service.execSqlPage(pageable, sql);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("查询失败");
        }
    }

    @GetMapping("NewUnqualifiedpatrolZhu")
    public WebApiResponse NewUnqualifiedpatrolZhu(Integer tableType, Integer status, String loginstatus, String taskname, String companyid, String currentUserId, String startTime, String endTime, String deptId, String userName) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String s = "";
        String tourDept = "";
        String classDeptName = "";
        String companyLikeDept = "";
        if (tableType == 0) {
            s += " AND AUDIT_STATUS = 0 ";
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            s += " AND ul.CREATE_TIME >= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND ul.CREATE_TIME <= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ";
            tourDept += " AND PLAN_START_TIME <= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') AND PLAN_END_TIME >= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(ul.CREATE_TIME) = trunc(sysdate) ";
            tourDept += " AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= sysdate ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            s += " AND ul.CREATE_TIME >= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND ul.CREATE_TIME <= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
            tourDept += " AND PLAN_START_TIME <= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') AND PLAN_END_TIME >= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_char(ul.CREATE_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
            tourDept += " AND PLAN_START_TIME <= to_date(to_char(sysdate,'yyyy-mm'),'yyyy-mm') AND PLAN_END_TIME >= to_date(to_char(sysdate,'yyyy-mm'),'yyyy-mm') ";
        }
        if (!StringUtils.isEmpty(deptId)) {
            roletype = 1;
            s += " AND u.DEPTID= '" + deptId + "' ";
            classDeptName = " ID = '" + deptId + "' ";
            tourDept += " AND t.TD_ORG = '" + deptId + "' ";
            companyLikeDept = " AND ORGID LIKE '%" + deptId + "%' ";
        } else if (roletype == 1 || roletype == 2) {
            s += " AND u.DEPTID= '" + deptid + "' ";
            classDeptName = " ID = '" + deptid + "' ";
            tourDept += " AND t.TD_ORG = '" + deptid + "' ";
            companyLikeDept = " AND  ORGID  LIKE '%" + deptId + "%' ";
        }
        if (!StringUtils.isEmpty(companyid)) {
            s += " AND u.COMPANYID= '" + companyid + "' ";

        }
        if (!StringUtils.isEmpty(taskname)) {
            s += " AND x.TASK_NAME LIKE '%" + taskname.trim() + "%' ";
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            s += " AND u.LOGINSTATUS=" + loginstatus + " ";
        }
        if (!StringUtils.isEmpty(status)) {
            s += " AND AUDIT_STATUS=" + status + " ";
        }
        if (roletype == 0) {
            try {
                return WebApiResponse.success(UnqualifiedpatrolOne(s, tourDept));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        } else if (roletype == 1 || roletype == 2) {
            try {
                return WebApiResponse.success(UnqualifiedpatrolTwo(s, tourDept, classDeptName, companyLikeDept));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        }
        return null;
    }

    private Map UnqualifiedpatrolTwo(String s, String tourDept, String classDeptName, String companyLikeDept) {
        //通道单位
        String SQL = " SELECT nvl(a.aaaaa, 0) AS VALUE, d.ID, d.DEPTNAME " +
                "FROM (SELECT count(1) AS aaaaa, u.CLASSID FROM ALARM_UNQUALIFIEDPATROL ul " +
                " LEFT JOIN USERINFO u ON u.ID = ul.USER_ID  LEFT JOIN XS_ZC_TASK x ON ul.TASK_ID = x.ID  WHERE 1 = 1  " + s + "   GROUP BY u.CLASSID) a RIGHT JOIN (SELECT ID, DEPTNAME FROM (SELECT ID, DEPTNAME, LASTNODE " +
                " FROM RZTSYSDEPARTMENT START WITH " + classDeptName + " CONNECT BY PRIOR id = DEPTPID)WHERE LASTNODE = 0) d ON a.CLASSID = d.ID ORDER BY nvl(a.aaaaa, 0) DESC";
        String sqlClassAll = " SELECT sum(t.PLAN_XS_NUM * tt.TOTAL) towerNum, " +
                "  t.CLASS_ID FROM XS_ZC_TASK t JOIN (SELECT count(1) total, " +
                "  XS_ZC_CYCLE_ID FROM XS_ZC_CYCLE_LINE_TOWER GROUP BY XS_ZC_CYCLE_ID) tt " +
                "  ON t.XS_ZC_CYCLE_ID = tt.XS_ZC_CYCLE_ID " + tourDept + " GROUP BY t.CLASS_ID ";
        List<Map<String, Object>> sqlClassAllList = this.service.execSql(sqlClassAll);
        Map tourCompanyAllMap = new HashMap();
        if (sqlClassAllList != null) {
            for (Map sqlClassAllMap : sqlClassAllList) {
                tourCompanyAllMap.put(sqlClassAllMap.get("CLASS_ID"), sqlClassAllMap.get("TOWERNUM"));
            }
        }
        //返回集合
        List<Map<String, Object>> sqlClassList = this.service.execSql(SQL);
        for (Map mappp : sqlClassList) {
            Object id = mappp.get("ID");
            mappp.put("TOWERNUM", tourCompanyAllMap.get(id) == null ? 0 : tourCompanyAllMap.get(id));
        }
        //外协
        String sqlCompany = " SELECT nvl(a.aaaaa, 0) AS VALUE, d.ID, d.COMPANYNAME " +
                "FROM (SELECT count(1) AS aaaaa, u.COMPANYID FROM ALARM_UNQUALIFIEDPATROL ul " +
                "LEFT JOIN USERINFO u ON u.ID = ul.USER_ID  LEFT JOIN XS_ZC_TASK x ON ul.TASK_ID = x.ID  WHERE 1 = 1   " + s + "  GROUP BY u.COMPANYID) a RIGHT JOIN (SELECT * FROM RZTSYSCOMPANY WHERE 1=1 " + companyLikeDept + ") d " +
                "ON a.COMPANYID = d.ID ORDER BY nvl(a.aaaaa, 0) DESC";
        String sqlCompanyAll = " SELECT sum(t.PLAN_XS_NUM * tt.TOTAL) towerNum, t.WX_ORG " +
                "FROM XS_ZC_TASK t JOIN (SELECT count(1) total, XS_ZC_CYCLE_ID FROM XS_ZC_CYCLE_LINE_TOWER GROUP BY XS_ZC_CYCLE_ID) tt " +
                "ON t.XS_ZC_CYCLE_ID = tt.XS_ZC_CYCLE_ID  " + tourDept + "  GROUP BY t.WX_ORG ";
        List<Map<String, Object>> sqlCompanyAllList = this.service.execSql(sqlCompanyAll);
        Map CompanyAllMap = new HashMap();
        if (sqlCompanyAllList != null) {
            for (Map sqlCompanyAllMap : sqlCompanyAllList) {
                tourCompanyAllMap.put(sqlCompanyAllMap.get("WX_ORG"), sqlCompanyAllMap.get("TOWERNUM"));
            }
        }
        List<Map<String, Object>> sqlCompanyList = this.service.execSql(sqlCompany);
        for (Map mappp : sqlCompanyList) {
            Object id = mappp.get("ID");
            mappp.put("TOWERNUM", tourCompanyAllMap.get(id) == null ? 0 : tourCompanyAllMap.get(id));
        }
        //人员
        String userNum = " SELECT " +
                "  sum(ul.IS_DW_TOUR) AS VALUE, " +
                "  u.ID, " +
                "  u.REALNAME " +
                " FROM ALARM_UNQUALIFIEDPATROL ul " +
                "  LEFT JOIN USERINFO u ON u.ID = ul.USER_ID  LEFT JOIN XS_ZC_TASK x ON ul.TASK_ID = x.ID " +
                " WHERE 1 = 1 " + s +
                " GROUP BY u.ID, u.REALNAME " +
                " ORDER BY sum(ul.IS_DW_TOUR) DESC";
        List<Map<String, Object>> userNums = this.service.execSql(userNum);
        Map map = new HashMap();
        map.put("USERNUM", userNums);
        map.put("DEPT", sqlClassList);
        map.put("COMPANY", sqlCompanyList);
        return map;
    }

    private Map UnqualifiedpatrolOne(String s, String tourDept) {
        //一级单位
        String deptSql = " SELECT nvl(a.aaaaa, 0) AS VALUE, d.ID, d.DEPTNAME " +
                " FROM ( SELECT " +
                "         count(1) AS aaaaa, u.DEPTID " +
                "       FROM ALARM_UNQUALIFIEDPATROL ul " +
                "         LEFT JOIN USERINFO u ON u.ID = ul.USER_ID LEFT JOIN XS_ZC_TASK x ON ul.TASK_ID = x.ID " +
                "       WHERE 1 = 1 " + s +
                "       GROUP BY u.DEPTID) a RIGHT JOIN RZTSYSDEPARTMENT d ON a.DEPTID = d.ID " +
                " WHERE d.DEPTSORT IS NOT NULL " +
                " ORDER BY nvl(a.aaaaa, 0) DESC  ";
        String tourAll = " SELECT " +
                "  sum(t.PLAN_XS_NUM * tt.TOTAL) towerNum, " +
                "  t.TD_ORG " +
                "FROM XS_ZC_TASK t " +
                "  JOIN (SELECT " +
                "          count(1) total, " +
                "          XS_ZC_CYCLE_ID " +
                "        FROM XS_ZC_CYCLE_LINE_TOWER " +
                "        GROUP BY XS_ZC_CYCLE_ID) tt " +
                "    ON t.XS_ZC_CYCLE_ID = tt.XS_ZC_CYCLE_ID " + tourDept +
                "GROUP BY t.TD_ORG ";
        List<Map<String, Object>> tourAllList = this.service.execSql(tourAll);
        Map tourAllMap = new HashMap();
        if (tourAllList != null) {
            for (Map khUser : tourAllList) {
                tourAllMap.put(khUser.get("TD_ORG"), khUser.get("TOWERNUM"));
            }
        }
        //返回集合
        List<Map<String, Object>> deptSqlList = this.service.execSql(deptSql);
        for (Map mappp : deptSqlList) {
            Object id = mappp.get("ID");
            mappp.put("TOWERNUM", tourAllMap.get(id) == null ? 0 : tourAllMap.get(id));
        }
        //外协
        String sqlCompany = " SELECT nvl(a.aaaaa,0) AS VALUE,c.COMPANYNAME,c.ID FROM ( " +
                "  SELECT count(1) AS aaaaa, u.COMPANYID " +
                "  FROM ALARM_UNQUALIFIEDPATROL ul LEFT JOIN USERINFO u ON u.ID = ul.USER_ID  LEFT JOIN XS_ZC_TASK x ON ul.TASK_ID = x.ID " +
                "  WHERE 1 = 1 " + s + "  GROUP BY u.COMPANYID ) a RIGHT JOIN RZTSYSCOMPANY c ON a.COMPANYID = c.ID  ORDER BY  nvl(a.aaaaa, 0) DESC ";
        String sqlCompanyAll = " SELECT sum(t.PLAN_XS_NUM * tt.TOTAL) towerNum, " +
                "  t.WX_ORG FROM XS_ZC_TASK t JOIN (SELECT count(1) total, " +
                "  XS_ZC_CYCLE_ID FROM XS_ZC_CYCLE_LINE_TOWER GROUP BY XS_ZC_CYCLE_ID) tt " +
                "  ON t.XS_ZC_CYCLE_ID = tt.XS_ZC_CYCLE_ID " + tourDept + " GROUP BY t.WX_ORG ";
        List<Map<String, Object>> sqlCompanyAllList = this.service.execSql(sqlCompanyAll);
        Map tourCompanyAllMap = new HashMap();
        if (tourAllList != null) {
            for (Map khUser : sqlCompanyAllList) {
                tourCompanyAllMap.put(khUser.get("WX_ORG"), khUser.get("TOWERNUM"));
            }
        }
        //返回集合
        List<Map<String, Object>> sqlCompanyList = this.service.execSql(sqlCompany);
        for (Map mappp : sqlCompanyList) {
            Object id = mappp.get("ID");
            mappp.put("TOWERNUM", tourCompanyAllMap.get(id) == null ? 0 : tourCompanyAllMap.get(id));
        }
        //人员
        String userNum = " SELECT " +
                "  sum(ul.IS_DW_TOUR) AS VALUE, " +
                "  u.ID, " +
                "  u.REALNAME " +
                " FROM ALARM_UNQUALIFIEDPATROL ul " +
                "  LEFT JOIN USERINFO u ON u.ID = ul.USER_ID LEFT JOIN XS_ZC_TASK x ON ul.TASK_ID = x.ID " +
                " WHERE 1 = 1 " + s +
                " GROUP BY u.ID, u.REALNAME " +
                " ORDER BY sum(ul.IS_DW_TOUR) DESC";
        List<Map<String, Object>> userNums = this.service.execSql(userNum);
        Map map = new HashMap();
        map.put("USERNUM", userNums);
        map.put("DEPT", deptSqlList);
        map.put("COMPANY", sqlCompanyList);
        return map;
    }

    @RequestMapping("unqualifiedpatrolList")
    public WebApiResponse unqualifiedpatrolList(Integer tableType, Integer status, String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String userName) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND e.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND e.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(e.CREATE_TIME) = trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            listLike.add(mon);
            s += " AND e.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(sun);
            s += " AND e.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_char(e.CREATE_TIME,'yyyy-mm') = to_char(sysdate,'yyyy-mm') ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND e.DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND e.DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(userName)) {
            listLike.add(userName.trim() + "%");
            s += " AND u.REALNAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname.trim() + "%");
            s += " AND x.TASK_NAME LIKE ?" + listLike.size();
        }
        if (tableType == 0) {
            if (!StringUtils.isEmpty(status)) {
                listLike.add(status);
                s += " AND TASK_STATUS = 0 AND STATUS = ?" + listLike.size();
            } else {
                listLike.add(0);
                s += " AND TASK_STATUS = 0  AND STATUS = ?" + listLike.size();
            }
        } else {
            if (!StringUtils.isEmpty(status)) {
                listLike.add(status);
                s += "  AND STATUS = ?" + listLike.size();
            }
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s += " AND u.LOGINSTATUS  = ?" + listLike.size();
        }
        String sql = " SELECT\n" +
                "  a.*,\n" +
                "  w.WARNING_NAME AS type,\n" +
                "  de.count\n" +
                "FROM (\n" +
                "       SELECT\n" +
                "         e.CREATE_TIME,\n" +
                "         x.TASK_NAME,\n" +
                "         u.DEPT,\n" +
                "         u.COMPANYNAME,\n" +
                "         u.CLASSNAME,\n" +
                "         u.REALNAME,\n" +
                "         u.PHONE,\n" +
                "         WARNING_TYPE,\n" +
                "         e.REASON,\n" +
                "         e.TASK_ID,\n" +
                "         e.USER_ID,\n" +
                "         e.TASK_TYPE,\n" +
                "         u.LOGINSTATUS\n" +
                "       FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID\n" +
                "         LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID\n" +
                "       WHERE (WARNING_TYPE = 5 OR WARNING_TYPE = 3)    " + s +
                "             ) a LEFT JOIN WARNING_TYPE w\n" +
                "    ON a.WARNING_TYPE = w.WARNING_TYPE\n" +
                "  LEFT JOIN (SELECT\n" +
                "               count(1) AS count,\n" +
                "               e.XS_ZC_TASK_ID\n" +
                "             FROM XS_ZC_TASK_EXEC_DETAIL d\n" +
                "               LEFT JOIN XS_ZC_TASK_EXEC e ON d.XS_ZC_TASK_EXEC_ID = e.ID\n" +
                "             WHERE IS_DW = 1 AND e.XS_ZC_TASK_ID IN (\n" +
                "               SELECT\n" +
                "         x.ID\n" +
                "       FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID\n" +
                "         LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID\n" +
                "       WHERE (WARNING_TYPE = 5 OR WARNING_TYPE = 3)   " + s +
                "             )\n" +
                "             GROUP BY e.XS_ZC_TASK_ID) de ON a.TASK_ID = de.XS_ZC_TASK_ID  ";
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    @GetMapping("/csInfo")
    public WebApiResponse csInfo(Long taskId) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            String sql = "select YCDATA,CREATE_TIME from XS_ZC_EXCEPTION WHERE TASK_ID=?1";
            List<Map<String, Object>> maps = this.service.execSql(sql, taskId);
            maps.forEach(map -> {
                Object ycdata = map.get("YCDATA");
                JSONArray objects = JSONObject.parseArray(ycdata.toString());
                for (int i = 0; i < objects.size(); i++) {
                    Map<String, Object> m = (Map<String, Object>) objects.get(i);
                    result.add(m);
                }
            });
            return WebApiResponse.success(result);
        } catch (Exception e) {
            return WebApiResponse.erro("erro" + e.getMessage());
        }
    }

    @GetMapping("/csPicture")
    public WebApiResponse csPicture(Long taskId) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {

            String sql = "select YCDATA from XS_ZC_EXCEPTION WHERE TASK_ID=?1";
            List<Map<String, Object>> maps = this.service.execSql(sql, taskId);
            StringBuffer ids = new StringBuffer();
            for (int j = 0; j < maps.size(); j++) {
                Map<String, Object> map = maps.get(j);
                Object ycdata = map.get("YCDATA");
                JSONArray objects = JSONObject.parseArray(ycdata.toString());
                for (int i = 0; i < objects.size(); i++) {
                    Map<String, Object> m = (Map<String, Object>) objects.get(i);
                    String id = (String) m.get("ID");
                    if (i != objects.size() - 1) {
                        ids.append(id + ", ");
                    } else {
                        ids.append(id);
                    }
                }
                if (j != maps.size() - 1) {
                    ids.append(",");
                }
            }
            String sql1 = "select FILE_PATH,PROCESS_NAME AS OPERATE_NAME ,CREATE_TIME from PICTURE_TOUR where PROCESS_ID in (" + ids.toString() + ")";
            List<Map<String, Object>> maps1 = this.service.execSql(sql1);
            result.addAll(maps1);
            return WebApiResponse.success(result);
        } catch (Exception e) {
            return WebApiResponse.erro("erro" + e.getMessage());
        }
    }
}
