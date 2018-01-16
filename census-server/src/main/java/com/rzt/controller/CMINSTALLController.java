/**
 * 文件名：CMINSTALLController
 * 版本信息：
 * 日期：2017/12/11 15:58:59
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.CMINSTALL;
import com.rzt.service.CMINSTALLService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类名称：CMINSTALLController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/11 15:58:59
 * 修改人：张虎成
 * 修改时间：2017/12/11 15:58:59
 * 修改备注：
 */
@RestController
@RequestMapping("INSTALL")
@Api(value = "字典表")
public class CMINSTALLController extends
        CurdController<CMINSTALL, CMINSTALLService> {
    /**
     * 查询字典所有数据
     *
     * @return
     */
    @RequestMapping("cminstallQuery")
    @ApiOperation(value = "查询字典表数据", notes = "查询字典表数据")
    public WebApiResponse cminstallQuery() {
        try {
            return WebApiResponse.success(this.service.cminstallQuery());
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据请求失败");
        }
    }

    /**
     * 修改字典表数据
     *
     * @param id  字典表ID
     * @param key 数值
     * @return
     */
    @RequestMapping("cminstallUpdate")
    @ApiOperation(value = "修改字典表数据", notes = "修改字典表数据")
    public Object cminstallUpdate(Long id, int key) {
        try {
            int one = 1;
            int zero = 0;
            int cminstallUpdate = this.service.cminstallUpdate(key, id);
            if (cminstallUpdate == one) {
                return WebApiResponse.success("修改成功");
            } else if (cminstallUpdate == zero) {
                return WebApiResponse.erro("保存失败");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("错误");
        }
    }

    private static ConcurrentHashMap<String, Integer> offLine_map = new ConcurrentHashMap<String, Integer>();
    private static ConcurrentHashMap<String, Integer> onLine_map = new ConcurrentHashMap<String, Integer>();
    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();

    @GetMapping("deptname")
    public WebApiResponse deptname() {
        String sql = " SELECT ID,DEPTNAME FROM RZTSYSDEPARTMENT WHERE DEPTSORT IS NOT NULL ";
        try {
            return WebApiResponse.success(this.service.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @PostMapping("getCount")
    public Map<String, Integer> lineSize(String ID, Integer LOGINSTATUS, Integer WORKTYPE) {
        if (!StringUtils.isEmpty(LOGINSTATUS) && !StringUtils.isEmpty(WORKTYPE)) {
            //离线状态(0表示不在线  1表示在线)
            if (LOGINSTATUS == 0) {
                offLine_map.put(ID + "_" + WORKTYPE, 0);
            } else if (LOGINSTATUS == 1) {
                onLine_map.put(ID + "_" + WORKTYPE, 1);
            }
            map.put("offLine", offLine_map.size());
            map.put("onLine", onLine_map.size());
        }
        return map;
    }

    /**
     * 告警信息展示
     */
    @GetMapping("tourAlarm")
    public WebApiResponse tourAlarm() {
        String sql = "SELECT *  " +
                "FROM (SELECT  " +
                "        x.PLAN_START_TIME,  " +
                "        x.TASK_NAME,  " +
                "        t.DEPTNAME,  " +
                "        '未按时间按时任务' AS ZT,  " +
                "        u.REALNAME,  " +
                "        mm.COMPANYNAME  " +
                "      FROM xs_txbd_task x LEFT JOIN RZTSYSDEPARTMENT t ON x.TD_ORG = t.ID  " +
                "        LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID  " +
                "        LEFT JOIN RZTSYSCOMPANY mm ON x.CLASS_ID = mm.ID  " +
                "      WHERE trunc(x.plan_start_time) = trunc(sysdate) AND x.plan_start_time < nvl(x.real_start_time, sysdate)  " +
                "      UNION ALL  " +
                "      SELECT  " +
                "        x.PLAN_START_TIME,  " +
                "        x.TASK_NAME,  " +
                "        t.DEPTNAME,  " +
                "        '未按时间按时任务' AS ZT,  " +
                "        u.REALNAME,  " +
                "        mm.COMPANYNAME  " +
                "      FROM xs_zc_task x LEFT JOIN RZTSYSDEPARTMENT t ON x.TD_ORG = t.ID  " +
                "        LEFT JOIN RZTSYSUSER u ON x.CM_USER_ID = u.ID  " +
                "        LEFT JOIN RZTSYSCOMPANY mm ON x.CLASS_ID = mm.ID  " +
                "      WHERE trunc(x.plan_start_time) = trunc(sysdate) AND trunc(x.plan_start_time) < nvl(x.real_start_time, sysdate)  " +
                "      UNION ALL  " +
                "      SELECT  " +
                "        x.START_TIME AS PLAN_START_TIME,  " +
                "        k.TASK_NAME,  " +
                "        t1.DEPTNAME,  " +
                "        '巡视不合格'      AS ZT,  " +
                "        u.REALNAME,  " +
                "        mm.COMPANYNAME  " +
                "      FROM XS_ZC_TASK_EXEC_DETAIL x LEFT JOIN XS_ZC_TASK_EXEC t ON x.XS_ZC_TASK_EXEC_ID = t.ID  " +
                "        LEFT JOIN XS_ZC_TASK k ON t.XS_ZC_TASK_ID = k.ID  " +
                "        LEFT JOIN RZTSYSUSER u ON k.CM_USER_ID = u.ID  " +
                "        LEFT JOIN RZTSYSDEPARTMENT t1 ON u.DEPTID = t1.ID  " +
                "        LEFT JOIN RZTSYSCOMPANY mm ON k.CLASS_ID = mm.ID  " +
                "      WHERE is_dw = 1)  " +
                "ORDER BY PLAN_START_TIME DESC";
        try {
            return WebApiResponse.success(this.service.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @GetMapping("khAlarm")
    public WebApiResponse khAlarm() {
        String sql = "SELECT k.PLAN_START_TIME,k.TASK_NAME,'未按时间按时任务' as zt ,k.TDYW_ORG,u.REALNAME,k.WX_ORG   AS COMPANYNAME " +
                "FROM KH_TASK k   LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                "WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND PLAN_START_TIME < nvl(REAL_START_TIME, sysdate) ";
        try {
            return WebApiResponse.success(this.service.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }


    /**
     * 离线人员
     *
     * @return
     */
    @GetMapping("offLineUser")
    public Object offLineUser(Integer page, Integer size, String deptid) {
        Pageable pageable = new PageRequest(page, size);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(deptid)) {
            list.add(deptid);
            s += " and id =?" + list.size();
        }
        String sql = "SELECT " +
                "  u.REALNAME, " +
                "  d.DEPTNAME, " +
                "  d2.DEPTNAME classname, " +
                "  u.PHONE, " +
                "  u.EMAIL, " +
                "  '巡视' AS     WORKTYPE, " +
                "  u.AGE, " +
                "  d.ID " +
                "FROM (SELECT z.CM_USER_ID AS id " +
                "      FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                "      WHERE LOGINSTATUS = 0 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate " +
                "      GROUP BY z.CM_USER_ID) b LEFT JOIN RZTSYSUSER u ON b.id = u.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d ON u.DEPTID = d.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d2 ON u.CLASSNAME = d2.ID " +
                "WHERE LOGINSTATUS = 0 AND USERDELETE = 1";
        String sql1 = "SELECT " +
                "  u.REALNAME, " +
                "  d.DEPTNAME, " +
                "  d2.DEPTNAME classname, " +
                "  u.PHONE, " +
                "  u.EMAIL, " +
                "  '看护' AS     WORKTYPE, " +
                "  u.AGE,d.id " +
                "FROM (SELECT z.USER_ID AS id " +
                "      FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                "      WHERE LOGINSTATUS = 0 AND USERDELETE = 1  AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate " +
                "      GROUP BY z.USER_ID) b LEFT JOIN RZTSYSUSER u ON b.id = u.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d ON u.DEPTID = d.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d2 ON u.CLASSNAME = d2.ID " +
                "WHERE LOGINSTATUS = 0 AND USERDELETE = 1";
        String sql2 = " SELECT " +
                "  u.REALNAME, " +
                "  d.DEPTNAME, " +
                "  d2.DEPTNAME classname, " +
                "  u.PHONE, " +
                "  u.EMAIL, " +
                "  '稽查' AS     WORKTYPE, " +
                "  u.AGE,d.id " +
                "FROM (SELECT z.USER_ID AS id " +
                "      FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " +
                "      WHERE LOGINSTATUS = 0 AND USERDELETE = 1 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " +
                "      GROUP BY z.USER_ID) b LEFT JOIN RZTSYSUSER u ON b.id = u.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d ON u.DEPTID = d.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d2 ON u.CLASSNAME = d2.ID " +
                "WHERE LOGINSTATUS = 0 AND USERDELETE = 1 ";
        String sqlall = " select * from ( " + sql + "  union all " + sql1 + " union all " + sql2 + ") where 1=1" + s;
        return WebApiResponse.success(this.service.execSqlPage(pageable, sqlall, list.toArray()));
    }

    /**
     * 未按时开始任务
     *
     * @return
     */
    @GetMapping("notOnTime")
    public Object notOnTime(Integer page, Integer size, String orgid) {
        Pageable pageable = new PageRequest(page, size);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " and d.id = ?" + list.size();
        }
        String sql = "SELECT  " +
                "  xs.TASK_NAME,  " +
                "  r.PHONE,  " +
                "  d.DEPTNAME,  " +
                "  d2.DEPTNAME         classname,  " +
                "  r.REALNAME,  " +
                "  xs.plan_start_time,  " +
                "  CASE xs.STAUTS  " +
                "  WHEN 0  " +
                "    THEN '待办'  " +
                "  WHEN 1  " +
                "    THEN '进行中'  " +
                "  WHEN 2  " +
                "    THEN '已完成' END AS STAUTS,  " +
                "  xs.ID            AS taskid,  " +
                "  r.ID             AS userid  " +
                "FROM  " +
                "  (SELECT  " +
                "     ZC.ID,  " +
                "     ZC.TASK_NAME,  " +
                "     ZC.CM_USER_ID,  " +
                "     ZC.plan_start_time,  " +
                "     ZC.STAUTS  " +
                "   FROM xs_zc_task ZC  " +
                "   WHERE trunc(ZC.plan_start_time) = trunc(sysdate) AND ZC.plan_start_time < nvl(ZC.real_start_time, sysdate)  " +
                "   UNION ALL  " +
                "   SELECT  " +
                "     TX.ID,  " +
                "     TX.TASK_NAME,  " +
                "     TX.CM_USER_ID,  " +
                "     TX.plan_start_time,  " +
                "     TX.STAUTS  " +
                "   FROM xs_txbd_task TX  " +
                "   WHERE trunc(TX.plan_start_time) = trunc(sysdate) AND TX.plan_start_time < nvl(TX.real_start_time, sysdate)) xs  " +
                "  LEFT JOIN RZTSYSUSER r ON CM_USER_ID = r.ID  " +
                "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = r.DEPTID  " +
                "  LEFT JOIN RZTSYSDEPARTMENT d2 ON d2.ID = r.CLASSNAME  " +
                "WHERE 1 = 1" + s;

        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, list.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    /**
     * 巡视不合格
     */
    @GetMapping("xsbhg")
    public Object xsbhg(Integer page, Integer size, String orgid) {
        Pageable pageable = new PageRequest(page, size);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " AND p.ID=?" + list.size();
        }
        String sql = "SELECT " +
                "  xx.ID            AS taskid, " +
                "  r.ID             AS userid, " +
                "  x.YCMS, " +
                "  xx.TASK_NAME, " +
                "  xx.PLAN_START_TIME, " +
                "  r.REALNAME, " +
                "  r.PHONE, " +
                "  p.DEPTNAME, " +
                "  pp.DEPTNAME      AS classnameCASE, " +
                "  CASE xx.STAUTS " +
                "  WHEN 0 " +
                "    THEN '待办' " +
                "  WHEN 1 " +
                "    THEN '进行中' " +
                "  WHEN 2 " +
                "    THEN '已完成' END AS STAUTS " +
                "FROM (SELECT YCMS,TASK_ID FROM XS_ZC_EXCEPTION WHERE trunc(CREATE_TIME) = trunc(sysdate)) x LEFT JOIN XS_ZC_TASK xx ON x.TASK_ID = xx.ID " +
                "  LEFT JOIN RZTSYSUSER r ON r.ID = xx.CM_USER_ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT p ON p.ID = xx.TD_ORG " +
                "  LEFT JOIN RZTSYSDEPARTMENT pp ON pp.ID = xx.CLASS_ID WHERE 1=1 " + s;
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, list.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("TaskStatusXq")
    public WebApiResponse TaskStatusXq(Integer tasktype, Integer num) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        /**
         * 正常巡视未开始
         */
        String zcxsTask = " SELECT TASK_NAME as taskname,PLAN_START_TIME as plantime,CM_USER_ID  as userid FROM XS_ZC_TASK  WHERE STAUTS = ?1 AND trunc(PLAN_START_TIME) = trunc(sysdate) ";
        /**
         *保电巡视未开始
         */
        String bdxsTask = "SELECT   TASK_NAME as taskname,    PLAN_START_TIME as plantime,   CM_USER_ID as userid  FROM XS_TXBD_TASK  WHERE STAUTS = ?2 AND trunc(PLAN_START_TIME) = trunc(sysdate)";
        String xstask = zcxsTask + " UNION  ALL " + bdxsTask;
        /**
         * 看护未开始
         */
        String khTask = " SELECT TASK_NAME as taskname,PLAN_START_TIME as plantime,USER_ID as userid FROM KH_TASK WHERE STATUS = ?1 AND trunc(PLAN_START_TIME) = trunc(sysdate) ";
        /**
         * 稽查未开始
         */
        String jcTask = " SELECT TASK_NAME as taskname,PLAN_START_TIME as plantime,USER_ID as userid  FROM CHECK_LIVE_TASK  WHERE STATUS = ?1 AND trunc(PLAN_START_TIME) = trunc(sysdate) ";

        if (tasktype == 1) {
            List<Map<String, Object>> list = this.service.execSql(khTask, num);
            return taskname(hashOperations, list);
        } else if (tasktype == 2) {
            List<Map<String, Object>> list = this.service.execSql(xstask, num, num);
            return taskname(hashOperations, list);
        } else if (tasktype == 3) {
            List<Map<String, Object>> list = this.service.execSql(jcTask, num);
            return taskname(hashOperations, list);
        }
        return WebApiResponse.erro("erro");
    }

    @GetMapping("USERLOGINSTATUS")
    public WebApiResponse USERLOGINSTATUS(Integer tasktype, Integer num) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        String xsUser = " SELECT z.CM_USER_ID AS USERID " +
                "      FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                "      WHERE LOGINSTATUS = ?1 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate " +
                "      GROUP BY z.CM_USER_ID ";
        String khUser = " SELECT z.USER_ID AS USERID " +
                "FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                "WHERE LOGINSTATUS = ?1 AND USERDELETE = 1 AND PLAN_START_TIME< = sysdate AND PLAN_END_TIME >= sysdate " +
                "GROUP BY z.USER_ID ";
        String JCUSER = " SELECT z.USER_ID AS USERID " +
                "      FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " +
                "      WHERE LOGINSTATUS = ?1 AND USERDELETE = 1 AND trunc(z.PLAN_START_TIME) = trunc(sysdate) " +
                "      GROUP BY z.USER_ID ";
        if (tasktype == 2) {
            List<Map<String, Object>> list = this.service.execSql(xsUser, num);
            return taskname(hashOperations, list);
        } else if (tasktype == 1) {
            List<Map<String, Object>> list = this.service.execSql(khUser, num);
            return taskname(hashOperations, list);
        } else if (tasktype == 3) {
            List<Map<String, Object>> list = this.service.execSql(JCUSER, num);
            return taskname(hashOperations, list);
        }
        return WebApiResponse.erro("erro");
    }

    public WebApiResponse taskname(HashOperations hashOperations, List<Map<String, Object>> list) {
        Map<String, Map> userInformation = hashOperations.entries("UserInformation");
        for (Map<String, Object> task : list) {
            String userid = task.get("USERID").toString();
            Map user = userInformation.get(userid);
            if (StringUtils.isEmpty(user)) {
                task.put("CLASSNAME", "测试数据（暂不展示）");
                task.put("REALNAME", "测试数据（暂不展示）");
                task.put("PHONE", "测试数据（暂不展示）");
                task.put("DEPT", "测试数据（暂不展示）");
                task.put("GROUPNAME", "测试数据（暂不展示）");
            } else {
                task.putAll(user);
            }

        }
        return WebApiResponse.success(list);
    }


    @GetMapping("tgry")
    public WebApiResponse tgry(Integer page, Integer size, String orgid) {
        Pageable pageable = new PageRequest(page, size);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " and dept=?" + list.size();
        }
        String sql = " SELECT " +
                "  wkh.CREATE_TIME AS CREATETIME, " +
                "  wkh.TASK_NAME, " +
                "  us.REALNAME, " +
                "  us.PHONE, " +
                "  dept.DEPTNAME, " +
                "  depts.DEPTNAME as classname " +
                "FROM " +
                "  (SELECT " +
                "     wop.CREATE_TIME, " +
                "     kh.TASK_NAME, " +
                "     kh.USER_ID " +
                "   FROM WARNING_OFF_POST_USER wop " +
                "     LEFT JOIN KH_TASK kh ON wop.TASK_ID = kh.ID " +
                "   WHERE wop.STATUS = 1 AND trunc(kh.CREATE_TIME) = trunc(sysdate)) wkh " +
                "  LEFT JOIN RZTSYSUSER us ON wkh.USER_ID = us.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT dept ON dept.ID = us.DEPTID " +
                "  LEFT JOIN RZTSYSDEPARTMENT depts ON depts.ID = us.CLASSNAME WHERE 1=1 " + s;
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, list.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @GetMapping("touroverdue")
    public WebApiResponse touroverdue(Integer page, Integer size, String orgid) {
        Pageable pageable = new PageRequest(page, size);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " and d.id = ?" + list.size();
        }
        try {
            String sql = " SELECT " +
                    "  k.ID as taskid, " +
                    "  u.ID as userid, " +
                    "  k.TASK_NAME, " +
                    "  u.REALNAME, " +
                    "  u.PHONE, " +
                    "  CASE k.STAUTS " +
                    "  WHEN 0 " +
                    "    THEN '待办' " +
                    "  WHEN 1 " +
                    "    THEN '进行中' " +
                    "  WHEN 2 " +
                    "    THEN '已完成' END AS STAUTS, " +
                    "  d.DEPTNAME , " +
                    "  dd.DEPTNAME as calssname, " +
                    "  k.PLAN_START_TIME " +
                    "FROM (select ID,TASK_NAME,STAUTS,PLAN_START_TIME,CM_USER_ID,TD_ORG,CLASS_ID from  XS_ZC_TASK WHERE PLAN_END_TIME BETWEEN trunc( SYSDATE -1) AND trunc( SYSDATE ) AND STAUTS != 2 ) k LEFT JOIN  RZTSYSUSER u ON k.CM_USER_ID = u.ID " +
                    "  LEFT JOIN RZTSYSDEPARTMENT d ON k.TD_ORG = d.ID LEFT JOIN RZTSYSDEPARTMENT dd ON k.CLASS_ID=dd.ID where 1=1 " + s;
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, list.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("hiddenDetails")
    public WebApiResponse hiddenDetails(Integer type) {
        String sql = " SELECT TDWX_ORG ,TDYW_ORG,YHJB,LINE_NAME,YHLB, SGQK " +
                "FROM KH_YH_HISTORY h " +
                "WHERE yhjb1 = '施工隐患' ";
        String sql1 = " SELECT TDWX_ORG ,TDYW_ORG,YHJB,LINE_NAME,YHLB, SGQK " +
                "FROM KH_YH_HISTORY h " +
                "WHERE yhjb1 = '建筑隐患' ";
        String sql2 = " SELECT TDWX_ORG ,TDYW_ORG,YHJB,LINE_NAME,YHLB, SGQK " +
                "FROM KH_YH_HISTORY h " +
                "WHERE yhjb1 = '异物隐患' ";
        String sql3 = " SELECT TDWX_ORG ,TDYW_ORG,YHJB,LINE_NAME,YHLB, SGQK " +
                "FROM KH_YH_HISTORY h " +
                "WHERE yhjb1 = '树木隐患' ";
        if (type == 0) {
            return WebApiResponse.success(this.service.execSql(sql));
        } else if (type == 1) {
            return WebApiResponse.success(this.service.execSql(sql1));
        } else if (type == 2) {
            return WebApiResponse.success(this.service.execSql(sql2));
        } else if (type == 3) {
            return WebApiResponse.success(this.service.execSql(sql3));
        }
        return null;
    }

    @GetMapping("orgRanking")
    public WebApiResponse orgRanking() {
        try {
            String sql = " SELECT deptname,(select count(id) from guzhang where td_org_id=d.id and extract(year from create_data)='2016') count2016, " +
                    "(select count(id) from guzhang where td_org_id=d.id and extract(year from create_data)='2017') count2017 " +
                    "FROM RZTSYSDEPARTMENT d WHERE d.DEPTPID='402881e6603a69b801603a6ab1d70000'  ORDER BY  d.DEPTSORT ";
            return WebApiResponse.success(this.service.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @GetMapping("tasksprinkledpoint")
    public WebApiResponse tasksprinkledpoint(String status) {
        try {
            String xsSql = " SELECT count(1) as xs,TD_ORG FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = ?1  GROUP BY TD_ORG";
            String khSql = "   SELECT count(1) as KH,k.TDYW_ORG FROM KH_TASK k WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = ?1  GROUP BY k.TDYW_ORG ";
            String deptnameSql = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
            final List<Map<String, Object>> xs = this.service.execSql(xsSql, status);
            List<Map<String, Object>> kh = this.service.execSql(khSql, status);
            List<Map<String, Object>> deptname = this.service.execSql(deptnameSql);
            Map xsMap = new HashMap();
            Map khMap = new HashMap();
            for (Map<String, Object> singleXs : xs) {
                xsMap.put(singleXs.get("TD_ORG"), singleXs.get("XS"));
            }
            for (Map<String, Object> singleKh : kh) {
                khMap.put(singleKh.get("TDYW_ORG"), singleKh.get("KH"));
            }
            for (Map<String, Object> dept : deptname) {
                dept.put("xsNum", xsMap.get(dept.get("ID")));
                dept.put("khNum", khMap.get(dept.get("DEPTNAME")));
            }
            return WebApiResponse.success(deptname);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }
//
//    @GetMapping("flushUserInformationRedis")
//    public Object flushUserInfoRedis() {
//        try {
//            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
//            String sql = " SELECT * from USERINFO ";
//            List<Map<String, Object>> maps = this.service.execSql(sql);
//            for (Map<String, Object> map : maps) {
//                String id = map.get("ID").toString();
//                hashOperations.put("UserInformation", id, map);
//            }
//            return WebApiResponse.success("成功了");
//        } catch (Exception e) {
//            return WebApiResponse.erro("数据查询失败" + e.getMessage());
//        }
//    }
}