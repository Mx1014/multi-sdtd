/**
 * 文件名：ANOMALYMONITORINGService
 * 版本信息：
 * 日期：2017/12/31 16:25:17
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.Anomalymonitoring;
import com.rzt.repository.AnomalymonitoringRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.List;


/**
 * 类名称：ANOMALYMONITORINGService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/31 16:25:17
 * 修改人：张虎成
 * 修改时间：2017/12/31 16:25:17
 * 修改备注：
 */
@Service
public class AnomalymonitoringService extends CurdService<Anomalymonitoring, AnomalymonitoringRepository> {
    /**
     * 看护
     *
     * @param orgtype
     * @param page
     * @param size
     * @param date
     * @param orgid
     * @param type
     * @return
     */
    public WebApiResponse khGj(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        Pageable pageable = new PageRequest(page, size, null);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add("'" + date + "'");
            s += " AND PLAN_START_TIME=to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
        } else {
            list.add("'" + DateUtil.stringNow() + "'");
            s += " AND PLAN_START_TIME=to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
        }
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " AND orgid = ?" + list.size();
        }
        if (!StringUtils.isEmpty(type)) {
            list.add(orgid);
            s += " AND type = ?" + list.size();
        }
        /**
         * 二级未按规定时间开始任务
         */
        String ejkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND (ceil((sysdate - PLAN_START_TIME) * 24 * 60) < 40 OR" +
                "                                                                  a.ONECHECK_STATUS = 0) AND USERDELETE = 1";
        /**
         * 一级未按规定时间开始任务
         */
        String yjkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND ceil((sysdate - PLAN_START_TIME) * 24 * 60) > 40 AND" +
                "      a.ONECHECK_STATUS IS NULL AND USERDELETE = 1";
        /**
         * 二级看护人员未上线
         */
        String ejkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      (ceil((sysdate - PLAN_START_TIME) * 24 * 60) < 40 OR" +
                "       a.ONECHECK_STATUS = 0) AND USERDELETE = 1";
        /**
         * 一级看护人员未上线
         */
        String yjkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG  " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      ceil((sysdate - PLAN_START_TIME) * 24 * 60) > 40 AND" +
                "      a.ONECHECK_STATUS IS NULL AND USERDELETE = 1";
        /**
         * 二级脱岗
         */
        String ejkhTg = "SELECT k.ID,u.USERNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,u.PHONE,,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE ceil((sysdate - UT.START_TIME ) * 24 * 60) < 40 OR a.ONECHECK_STATUS = 0";
        /**
         * 一级脱岗
         */
        String yjkhTg = "SELECT k.ID,u.USERNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,u.PHONE,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE ceil((sysdate - UT.START_TIME ) * 24 * 60) > 40 AND a.ONECHECK_STATUS IS NULL";
        if (orgtype != 0) {
            String sql = "SELECT * FROM (" + ejkhTg + "union ALL" + ejkhUserWsx + "union ALL" + ejkhWks + ") where 1=1 " + s;
            try {
                return WebApiResponse.success(this.execSqlPage(pageable, sql, list.toArray()));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("erro");
            }
        } else {
            String sql = "SELECT * FROM (" + yjkhTg + " union ALL " + yjkhUserWsx + " union ALL " + yjkhWks + ") where 1=1 " + s;
            try {
                return WebApiResponse.success(this.execSqlPage(pageable, sql, list.toArray()));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("erro");
            }
        }

    }
}