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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Map;
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
}