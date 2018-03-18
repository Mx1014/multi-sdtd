package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.weekTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    @RequestMapping("NewOfflinesList")
    public WebApiResponse NewOfflinesList(Integer tableType, Integer workType, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {
        org.springframework.data.domain.Pageable pageable = new PageRequest(page, size);
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String s = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            s += " AND r.CREATE_TIME>= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND r.CREATE_TIME<= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(r.CREATE_TIME)=trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            s += " AND r.CREATE_TIME>= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND r.CREATE_TIME<= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_char(r.CREATE_TIME, 'yyyy-mm') = to_char(sysdate, 'yyyy-mm') ";
        }
        if (tableType == 0) {
            s += " AND u.LOGINSTATUS = 0 ";
        }
        if (roletype == 1 || roletype == 2) {
            s += " AND u.DEPTID ='" + deptid + "' ";
        }
        if (!StringUtils.isEmpty(deptId)) {
            s += " AND u.DEPTID ='" + deptId + "' ";
        }
        if (!StringUtils.isEmpty(taskType)) {
            s += " u.WORKTYPE = '" + taskType + "' ";
        }
        String sql = "   SELECT " +
                "   OFFLINE_TIME_LONG, " +
                "   u.REALNAME, " +
                "   u.DEPT, " +
                "   u.COMPANYNAME, " +
                "   u.WORKTYPE, " +
                "   r.OFFLINE_END_TIME, " +
                "   r.OFFLINE_FREQUENCY " +
                " FROM ALARM_OFFLINE r LEFT JOIN USERINFO u ON u.ID = r.USER_ID  where 1=1  " + s + " ORDER BY r.CREATE_TIME ";
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
    @GetMapping("NewofflineAscTable")
    public WebApiResponse NewofflineAscTable(Integer tableType, Integer workType, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, Integer taskType) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        String s = "";
        String userNumTime = "";
        String className = "";
        String companyname = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            s += " AND a.CREATE_TIME>= to_date('" + startTime + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND a.CREATE_TIME<= to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ";
            userNumTime = " AND PLAN_START_TIME >=  to_date('" + startTime + "','yyyy-mm-dd') AND PLAN_END_TIME <= to_date('" + endTime + "','yyyy-mm-dd') ";
        } else if (tableType == 0 || tableType == 1) {
            s += " AND trunc(a.CREATE_TIME)=trunc(sysdate) ";
            userNumTime = " AND PLAN_START_TIME <= trunc(sysdate + 1) AND PLAN_END_TIME >= trunc(sysdate) ";
        } else if (tableType == 2) {
            Map map = weekTime.weekTime();
            Object mon = map.get("Mon");
            Object sun = map.get("Sun");
            s += " AND a.CREATE_TIME>= to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') ";
            s += " AND a.CREATE_TIME<= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
            userNumTime = " AND PLAN_START_TIME >=  to_date('" + mon + "','yyyy-mm-dd hh24:mi:ss') AND PLAN_END_TIME <= to_date('" + sun + "','yyyy-mm-dd hh24:mi:ss') ";
        } else if (tableType == 3) {
            s += " AND to_char(A.CREATE_TIME, 'yyyy-mm') = to_char(sysdate, 'yyyy-mm')  ";
            userNumTime = " AND to_char(PLAN_START_TIME,'yyyy-mm-dd hh24:mi:ss') >= to_char(sysdate,'yyyy-mm') AND to_char(PLAN_END_TIME,'yyyy-mm-dd hh24:mi:ss') <= to_char(sysdate,'yyyy-mm') ";
        }
        if (tableType == 0) {
            s += " AND r.LOGINSTATUS = 0 ";
        }
        if (roletype == 1 || roletype == 2) {
            className += " id = '" + deptid + "' ";
            s += " AND r.DEPTID ='" + deptid + "' ";
            userNumTime += " AND U.DEPTID='" + deptid + "' ";
            companyname = " AND ORGID LIKE '%" + deptid + "%' ";
        } else if (!StringUtils.isEmpty(deptId)) {
            roletype = 1;
            s += " AND r.DEPTID ='" + deptId + "' ";
            userNumTime += " AND U.DEPTID='" + deptId + "' ";
            companyname = " AND ORGID LIKE '%" + deptId + "%' ";
            className += " id = '" + deptId + "' ";
        }
        if (!StringUtils.isEmpty(taskType)) {
            s += " r.WORKTYPE = '" + taskType + "' ";
        }
        if (roletype == 0) {
            try {
                return WebApiResponse.success(deptOne(taskType, s, userNumTime));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        } else if (roletype == 1 || roletype == 2) {
            try {
                return WebApiResponse.success(deptTwo(taskType, s, userNumTime, className, companyname));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("查询失败");
            }
        }
        return null;
    }

    //二级通道单位查询
    private Map deptTwo(Integer taskType, String s, String userNumTime, String className, String companyname) {
        //查询班组
        String sql = " SELECT nvl(b.aaaaa, 0) AS VALUE,DEPTNAME,a.ID FROM (SELECT ID, DEPTNAME FROM (SELECT ID, DEPTNAME, LASTNODE FROM RZTSYSDEPARTMENT START WITH " + className + " CONNECT BY PRIOR id = DEPTPID)WHERE LASTNODE = 0) a " +
                " LEFT JOIN  " +
                " (SELECT count(1) AS aaaaa, COMPANYID FROM ALARM_OFFLINE a LEFT JOIN USERINFO r ON a.USER_ID = r.ID WHERE 1 = 1 " + s + " GROUP BY r.COMPANYID) b ON a.ID = b.COMPANYID  ORDER BY nvl(b.aaaaa, 0) DESC";
        List<Map<String, Object>> khUserNumClass = null;
        List<Map<String, Object>> xsUserNumClass = null;
        if (StringUtils.isEmpty(taskType)) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.CLASSNAME\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.CLASSNAME ";
            khUserNumClass = this.service.execSql(sqlKH);
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.CLASSNAME\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.CLASSNAME ";
            xsUserNumClass = this.service.execSql(sqlxs);
        } else if (taskType == 1) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.CLASSNAME\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.CLASSNAME ";
            khUserNumClass = this.service.execSql(sqlKH);
        } else if (taskType == 2) {
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.CLASSNAME\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.CLASSNAME ";
            xsUserNumClass = this.service.execSql(sqlxs);
        }
        Map KHNUMCLASS = new HashMap();
        Map XSNUMCLASS = new HashMap();
        if (khUserNumClass != null) {
            for (Map khUser : khUserNumClass) {
                KHNUMCLASS.put(khUser.get("CLASSNAME"), khUser.get("KHUSERNUM"));
            }
        }
        if (xsUserNumClass != null) {
            for (Map xsUser : xsUserNumClass) {
                XSNUMCLASS.put(xsUser.get("CLASSNAME"), xsUser.get("XSUSERNUM"));
            }
        }
        //返回集合
        List<Map<String, Object>> maps = this.service.execSql(sql);
        for (Map mappp : maps) {
            Object id = mappp.get("ID");
            mappp.put("KHNUM", KHNUMCLASS.get(id) == null ? 0 : KHNUMCLASS.get(id));
            mappp.put("XSNUM", XSNUMCLASS.get(id) == null ? 0 : XSNUMCLASS.get(id));
        }
        //查询班组查询外协
        String companynameSql = " SELECT  nvl(b.aaaaa, 0) AS  VALUE, a.COMPANYNAME, a.ID FROM ( " +
                "  SELECT * FROM RZTSYSCOMPANY WHERE 1=1 " + companyname + " ) a LEFT JOIN (SELECT " +
                "  count(1) AS aaaaa, r.COMPANYID FROM ALARM_OFFLINE a LEFT JOIN USERINFO r ON a.USER_ID = r.ID " +
                "  WHERE 1 = 1 " + s +
                "  GROUP BY r.COMPANYID) b ON a.ID = b.COMPANYID  ORDER BY nvl(b.aaaaa, 0) DESC";
        List<Map<String, Object>> khUserNumCOMPANYID = null;
        List<Map<String, Object>> xsUserNumCOMPANYID = null;
        if (StringUtils.isEmpty(taskType)) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.COMPANYID\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.COMPANYID ";
            khUserNumCOMPANYID = this.service.execSql(sqlKH);
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.COMPANYID\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.COMPANYID ";
            xsUserNumCOMPANYID = this.service.execSql(sqlxs);
        } else if (taskType == 1) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.COMPANYID\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.COMPANYID ";
            khUserNumCOMPANYID = this.service.execSql(sqlKH);
        } else if (taskType == 2) {
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.COMPANYID\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.COMPANYID ";
            xsUserNumCOMPANYID = this.service.execSql(sqlxs);
        }
        Map KHNUMCOMPANYID = new HashMap();
        Map XSNUMCOMPANYID = new HashMap();
        if (khUserNumCOMPANYID != null) {
            for (Map khUserNumcompanyid : khUserNumCOMPANYID) {
                KHNUMCOMPANYID.put(khUserNumcompanyid.get("COMPANYID"), khUserNumcompanyid.get("KHUSERNUM"));
            }
        }
        if (xsUserNumCOMPANYID != null) {
            for (Map xsusernumcompanyid : xsUserNumCOMPANYID) {
                XSNUMCOMPANYID.put(xsusernumcompanyid.get("COMPANYID"), xsusernumcompanyid.get("XSUSERNUM"));
            }
        }
        List<Map<String, Object>> mapsRZTSYSCOMPANY = this.service.execSql(companynameSql);
        for (Map mapRZTSYSCOMPANY : mapsRZTSYSCOMPANY) {
            Object id = mapRZTSYSCOMPANY.get("ID");
            mapRZTSYSCOMPANY.put("KHNUM", KHNUMCOMPANYID.get(id) == null ? 0 : KHNUMCOMPANYID.get(id));
            mapRZTSYSCOMPANY.put("XSNUM", XSNUMCOMPANYID.get(id) == null ? 0 : XSNUMCOMPANYID.get(id));
        }
        //人员
        String userNum = " SELECT " +
                "  sum(a.OFFLINE_FREQUENCY) as userNum, " +
                "  r.ID,r.REALNAME " +
                "FROM ALARM_OFFLINE a LEFT JOIN USERINFO r ON a.USER_ID = r.ID " +
                " WHERE 1 = 1 " + s +
                " GROUP BY r.ID,r.REALNAME ORDER BY userNum DESC ";
        List<Map<String, Object>> userNums = this.service.execSql(userNum);
        Map map = new HashMap();
        map.put("USERNUM", userNums);
        map.put("DEPT", maps);
        map.put("COMPANY", mapsRZTSYSCOMPANY);
        return map;
    }

    //一级管理员权限
    private Map deptOne(Integer taskType, String s, String userNumTime) {
        //通道单位
        String sql = " SELECT  nvl(b.aaaaa, 0) AS VALUE,  DEPTNAME,a.ID  FROM (SELECT * " +
                "      FROM RZTSYSDEPARTMENT  WHERE DEPTSORT IS NOT NULL  ORDER BY DEPTSORT) a LEFT JOIN (SELECT " +
                "      count(1) AS aaaaa, " +
                "      DEPTID " +
                "      FROM ALARM_OFFLINE a LEFT JOIN USERINFO r ON a.USER_ID = r.ID " +
                "      WHERE 1=1 " + s +
                "      GROUP BY r.DEPTID) b ON a.ID = b.DEPTID ORDER BY nvl(b.aaaaa, 0) DESC";
        List<Map<String, Object>> khUserNum = null;
        List<Map<String, Object>> xsUserNum = null;
        if (StringUtils.isEmpty(taskType)) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.DEPTID\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.DEPTID ";
            khUserNum = this.service.execSql(sqlKH);
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.DEPTID\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.DEPTID ";
            xsUserNum = this.service.execSql(sqlxs);
        } else if (taskType == 1) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.DEPTID\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.DEPTID ";
            khUserNum = this.service.execSql(sqlKH);
        } else if (taskType == 2) {
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.DEPTID\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.DEPTID ";
            xsUserNum = this.service.execSql(sqlxs);
        }
        Map KHNUM = new HashMap();
        Map XSNUM = new HashMap();
        if (khUserNum != null) {
            for (Map khUser : khUserNum) {
                KHNUM.put(khUser.get("DEPTID"), khUser.get("KHUSERNUM"));
            }
        }
        if (xsUserNum != null) {
            for (Map xsUser : xsUserNum) {
                XSNUM.put(xsUser.get("DEPTID"), xsUser.get("XSUSERNUM"));
            }
        }
        List<Map<String, Object>> maps = this.service.execSql(sql);
        for (Map mappp : maps) {
            Object id = mappp.get("ID");
            mappp.put("KHNUM", KHNUM.get(id) == null ? 0 : KHNUM.get(id));
            mappp.put("XSNUM", XSNUM.get(id) == null ? 0 : XSNUM.get(id));
        }
        //外协单位
        String RZTSYSCOMPANY = " SELECT " +
                "  nvl(b.aaaaa, 0) AS VALUE," +
                "  a.COMPANYNAME, " +
                "  a.ID " +
                "FROM (SELECT * " +
                "      FROM RZTSYSCOMPANY) a LEFT JOIN (SELECT " +
                "      count(1) AS aaaaa, " +
                "      r.COMPANYID " +
                "      FROM ALARM_OFFLINE a LEFT JOIN USERINFO r ON a.USER_ID = r.ID " +
                "      WHERE 1 = 1  " + s +
                "      GROUP BY r.COMPANYID) b ON a.ID = b.COMPANYID ORDER BY nvl(b.aaaaa, 0) DESC";
        List<Map<String, Object>> khUserNumCOMPANYID = null;
        List<Map<String, Object>> xsUserNumCOMPANYID = null;
        if (StringUtils.isEmpty(taskType)) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.COMPANYID\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.COMPANYID ";
            khUserNumCOMPANYID = this.service.execSql(sqlKH);
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.COMPANYID\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.COMPANYID ";
            xsUserNumCOMPANYID = this.service.execSql(sqlxs);
        } else if (taskType == 1) {
            String sqlKH = " SELECT count(DISTINCT USER_ID) as khusernum,u.COMPANYID\n" +
                    "      FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID\n" +
                    "      WHERE  1=1 " + userNumTime +
                    "      GROUP BY u.COMPANYID ";
            khUserNumCOMPANYID = this.service.execSql(sqlKH);
        } else if (taskType == 2) {
            String sqlxs = " SELECT count(DISTINCT u.ID) as xsusernum,u.COMPANYID\n" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID\n" +
                    "   WHERE 1=1 " + userNumTime +
                    "   GROUP BY u.COMPANYID ";
            xsUserNumCOMPANYID = this.service.execSql(sqlxs);
        }
        Map KHNUMCOMPANYID = new HashMap();
        Map XSNUMCOMPANYID = new HashMap();
        if (khUserNumCOMPANYID != null) {
            for (Map khUserNumcompanyid : khUserNumCOMPANYID) {
                KHNUMCOMPANYID.put(khUserNumcompanyid.get("COMPANYID"), khUserNumcompanyid.get("KHUSERNUM"));
            }
        }
        if (xsUserNumCOMPANYID != null) {
            for (Map xsusernumcompanyid : xsUserNumCOMPANYID) {
                XSNUMCOMPANYID.put(xsusernumcompanyid.get("COMPANYID"), xsusernumcompanyid.get("XSUSERNUM"));
            }
        }
        List<Map<String, Object>> mapsRZTSYSCOMPANY = this.service.execSql(RZTSYSCOMPANY);
        for (Map mapRZTSYSCOMPANY : mapsRZTSYSCOMPANY) {
            Object id = mapRZTSYSCOMPANY.get("ID");
            mapRZTSYSCOMPANY.put("KHNUM", KHNUMCOMPANYID.get(id) == null ? 0 : KHNUMCOMPANYID.get(id));
            mapRZTSYSCOMPANY.put("XSNUM", XSNUMCOMPANYID.get(id) == null ? 0 : XSNUMCOMPANYID.get(id));
        }
        //人员
        String userNum = " SELECT " +
                "  sum(a.OFFLINE_FREQUENCY) as userNum, " +
                "  r.ID,r.REALNAME " +
                "FROM ALARM_OFFLINE a LEFT JOIN USERINFO r ON a.USER_ID = r.ID " +
                "   WHERE 1 = 1 " + s +
                "  GROUP BY r.ID,r.REALNAME ORDER BY userNum DESC ";
        List<Map<String, Object>> userNums = this.service.execSql(userNum);
        Map map = new HashMap();
        map.put("USERNUM", userNums);
        map.put("DEPT", maps);
        map.put("COMPANY", mapsRZTSYSCOMPANY);
        return map;
    }

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
