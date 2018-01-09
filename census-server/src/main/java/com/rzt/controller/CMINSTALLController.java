/**
 * 文件名：CMINSTALLController
 * 版本信息：
 * 日期：2017/12/11 15:58:59
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.CMINSTALL;
import com.rzt.service.CMINSTALLService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public Object offLineUser() {

        String sql = "SELECT " +
                "  u.REALNAME, " +
                "  d.DEPTNAME, " +
                "  d2.DEPTNAME classname, " +
                "  u.PHONE, " +
                "  u.EMAIL, " +
                "CASE WORKTYPE " +
                "    WHEN 1 THEN '看护' " +
                "    WHEN 2 THEN '巡视' " +
                "    WHEN 3 THEN '现场稽查' " +
                "    WHEN 4 THEN '后台稽查' END  as WORKTYPE, " +
                "u.AGE " +
                "FROM RZTSYSUSER u LEFT JOIN RZTSYSDEPARTMENT d ON u.DEPTID = d.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d2 ON u.CLASSNAME = d2.ID " +
                "WHERE LOGINSTATUS = 0 AND USERDELETE = 1  AND USERTYPE=0 ";

        try {
            return WebApiResponse.success(this.service.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    /**
     * 未按时开始任务
     *
     * @return
     */
    @GetMapping("notOnTime")
    public Object notOnTime() {

        String sql = "SELECT " +
                "  xs.TASK_NAME, " +
                "  r.PHONE, " +
                "  d.DEPTNAME, " +
                "  d2.DEPTNAME classname, " +
                "  r.REALNAME,r.EMAIL,CASE r.LOGINSTATUS WHEN 1 THEN '在线' WHEN 0 THEN '离线' END  AS LOGINSTATUS " +
                "FROM " +
                "  (SELECT " +
                "     ZC.ID, " +
                "     ZC.TASK_NAME, " +
                "     ZC.CM_USER_ID " +
                "   FROM xs_zc_task ZC " +
                "   WHERE trunc(ZC.plan_start_time) = trunc(sysdate) AND trunc(ZC.plan_start_time) < nvl(ZC.real_start_time, sysdate) " +
                "   UNION ALL " +
                "   SELECT " +
                "     TX.ID, " +
                "     TX.TASK_NAME, " +
                "     TX.CM_USER_ID " +
                "   FROM xs_txbd_task TX " +
                "   WHERE trunc(TX.plan_start_time) = trunc(sysdate) AND TX.plan_start_time < nvl(TX.real_start_time, sysdate)) xs " +
                "  LEFT JOIN RZTSYSUSER r ON CM_USER_ID = r.ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = r.DEPTID " +
                "  LEFT JOIN RZTSYSDEPARTMENT d2 ON d2.ID = r.CLASSNAME";

        try {
            return WebApiResponse.success(this.service.execSql(sql));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    /**
     * 巡视不合格
     */
    @GetMapping("xsbhg")
    public Object xsbhg() {
        String sql = "SELECT " +
                "  a.TASK_NAME, " +
                "  nvl(a.REASON, '未填写原因') AS REASON, " +
                "  u.REALNAME, " +
                "  r.DEPTNAME, " +
                "  tt.DEPTNAME            AS classname, " +
                "  u.PHONE, " +
                "  CASE u.LOGINSTATUS " +
                "  WHEN 1 " +
                "    THEN '在线' " +
                "  WHEN 0 " +
                "    THEN '离线' END        AS LOGINSTATUS " +
                "FROM (SELECT " +
                "        ta.ID, " +
                "        z.REASON, " +
                "        ta.TASK_NAME, " +
                "        ta.CM_USER_ID " +
                "      FROM XS_ZC_TASK_EXEC_DETAIL z LEFT JOIN XS_ZC_TASK_EXEC e ON z.XS_ZC_TASK_EXEC_ID = e.ID " +
                "        LEFT JOIN XS_ZC_TASK ta ON ta.ID = e.XS_ZC_TASK_ID " +
                "      WHERE z.IS_DW = 1) a LEFT JOIN RZTSYSUSER u ON u.ID = a.CM_USER_ID " +
                "  LEFT JOIN RZTSYSDEPARTMENT r ON r.ID = u.DEPTID " +
                "  LEFT JOIN RZTSYSDEPARTMENT tt ON tt.ID = u.CLASSNAME";
        try {
            return WebApiResponse.success(this.service.execSql(sql));
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
        String xsUser = " SELECT id as USERID  FROM RZTSYSUSER  WHERE LOGINSTATUS = ?1 AND WORKTYPE = 2 AND USERDELETE = 1 AND USERTYPE = 0 ";
        String khUser = " SELECT id as USERID  FROM RZTSYSUSER WHERE LOGINSTATUS = ?1 AND WORKTYPE = 1 AND USERDELETE = 1  AND USERTYPE=0 ";
        String JCUSER = " SELECT id as USERID  FROM RZTSYSUSER WHERE LOGINSTATUS = ?1 AND WORKTYPE = 3 AND USERDELETE = 1  AND USERTYPE=0 ";
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

    private WebApiResponse taskname(HashOperations hashOperations, List<Map<String, Object>> list) {
        for (int i = 0; i < list.size(); i++) {
            Object useid = list.get(i).get("USERID");
            Object information = hashOperations.get("UserInformation", useid);
            JSONObject jsonObject = JSONObject.parseObject(information.toString());
            Object dept = jsonObject.get("DEPT");
            Object classname = jsonObject.get("CLASSNAME");
            Object realname = jsonObject.get("REALNAME");
            Object phone = jsonObject.get("PHONE");
            Object groupname = jsonObject.get("GROUPNAME");
            Object loginstatus = jsonObject.get("LOGINSTATUS");
            if (Integer.valueOf(loginstatus.toString()) == 0) {
                list.get(i).put("LOGINSTATUS", "离线");
            } else {
                list.get(i).put("LOGINSTATUS", "在线");
            }
            list.get(i).put("CLASSNAME", classname);
            list.get(i).put("REALNAME", realname);
            list.get(i).put("PHONE", phone);
            list.get(i).put("DEPT", dept);
            list.get(i).put("GROUPNAME", groupname);
        }
        return WebApiResponse.success(list);
    }
}