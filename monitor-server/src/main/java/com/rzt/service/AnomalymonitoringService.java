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
import org.springframework.data.domain.Page;
import com.rzt.utils.DateUtil;
import com.rzt.utils.SnowflakeIdWorker;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名称：ANOMALYMONITORINGService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/31 16:25:17
 * 修改人：张虎成
 * 修改时间：2017/12/31 16:25:17
 * 修改备注：
 * @version
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
            list.add( date );
            s += " AND  PLAN_START_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        } else {
            s += " AND trunc(PLAN_START_TIME)=trunc(sysdate) ";
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
        String ejkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND (ceil((sysdate - PLAN_START_TIME) * 24 * 60) < 40 OR" +
                "                                                                  a.ONECHECK_STATUS = 0) AND USERDELETE = 1";
        /**
         * 一级未按规定时间开始任务
         */
        String yjkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND ceil((sysdate - PLAN_START_TIME) * 24 * 60) > 40 AND" +
                "      a.ONECHECK_STATUS IS NULL AND USERDELETE = 1";
        /**
         * 二级看护人员未上线
         */
        String ejkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      (ceil((sysdate - PLAN_START_TIME) * 24 * 60) < 40 OR" +
                "       a.ONECHECK_STATUS = 0) AND USERDELETE = 1";
        /**
         * 一级看护人员未上线
         */
        String yjkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG  " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      ceil((sysdate - PLAN_START_TIME) * 24 * 60) > 40 AND" +
                "      a.ONECHECK_STATUS IS NULL AND USERDELETE = 1";
        /**
         * 二级脱岗
         */
        String ejkhTg = "SELECT k.ID,u.REALNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,k.WX_ORG AS COMPANYNAME,u.PHONE,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE ceil((sysdate - UT.START_TIME ) * 24 * 60) < 40 OR a.ONECHECK_STATUS = 0";
        /**
         * 一级脱岗
         */
        String yjkhTg = "SELECT k.ID,u.REALNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,k.WX_ORG AS COMPANYNAME,u.PHONE,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE ceil((sysdate - UT.START_TIME ) * 24 * 60) > 40 AND a.ONECHECK_STATUS IS NULL";
        if (orgtype != 0) {
            String sql = "SELECT * FROM (" + ejkhTg + " union ALL " + ejkhUserWsx + " union ALL " + ejkhWks + ") where 1=1 " + s;
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
    //巡视任务警告查询所有
    public Object XSGJ(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        Pageable pageable = new PageRequest(page, size, null);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  PLAN_START_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        } else {
            s += " AND trunc(PLAN_START_TIME)=trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " AND orgid = ?" + list.size();
        }
        if (!StringUtils.isEmpty(type)) {
            list.add(orgid);
            s += " AND type = ?" + list.size();
        }

        //二级保电任务超期警告
        String sql1 = " select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME " +
                "FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_txbd_task x LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and (ceil((sysdate - plan_end_time) * 24 * 60) < 40 or a.ONECHECK_STATUS = 0)) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //二级正常巡视任务超期警告
        String sql2 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_zc_task x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and (ceil((sysdate - plan_end_time) * 24 * 60) < 40 or a.ONECHECK_STATUS = 0)) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视任务未上线 二级
        String sql3 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE (ceil((sysdate - plan_end_time) * 24 * 60) < 40 or a.ONECHECK_STATUS = 0)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视任务未上线 二级
        String sql4 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE (ceil((sysdate - plan_end_time) * 24 * 60) < 40 or a.ONECHECK_STATUS = 0)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视未按时接任务 二级
        String sql5 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND (ceil((sysdate - plan_end_time) * 24 * 60) < 40 or a.ONECHECK_STATUS = 0)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视未按时接任务 二级
        String sql6 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND (ceil((sysdate - plan_end_time) * 24 * 60) < 40 or a.ONECHECK_STATUS = 0)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //////一级
        //一级保电任务超期警告
        String sql11 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME " +
                "FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_txbd_task x LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and (ceil((sysdate - plan_end_time) * 24 * 60) > 40 and a.ONECHECK_STATUS is null)) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //一级正常巡视任务超期警告
        String sql12 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_zc_task x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and (ceil((sysdate - plan_end_time) * 24 * 60) > 40 and a.ONECHECK_STATUS is null)) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视任务未上线 一级
        String sql13 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE (ceil((sysdate - plan_end_time) * 24 * 60) > 40 and a.ONECHECK_STATUS is null)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视任务未上线 一级
        String sql14 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE (ceil((sysdate - plan_end_time) * 24 * 60) > 40 and a.ONECHECK_STATUS is null)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视未按时接任务 一级
        String sql15 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND (ceil((sysdate - plan_end_time) * 24 * 60) > 40 and a.ONECHECK_STATUS is null)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视未按时接任务 一级
        String sql16 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND (ceil((sysdate - plan_end_time) * 24 * 60) > 40 and a.ONECHECK_STATUS is null)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //type == 0 为一级
        String sql = "";
        if(orgtype!=0){
            sql="SELECT * FROM (" +sql1+" UNION ALL "+sql2+" UNION ALL "+sql3+" UNION ALL "+sql4+" UNION ALL "+sql5+" UNION ALL "+sql6+") where 1=1 "+s;
        }else if (orgtype==0){
            sql = "SELECT * FROM (" +sql11+" UNION ALL "+sql12+" UNION ALL "+sql13+" UNION ALL "+sql14+" UNION ALL "+sql15+" UNION ALL "+sql16+") where 1=1 "+s;
        }
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return maps;
    }

    //巡视任务警告查询所有已处理警告
    public Object XSGJC(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        Pageable pageable = new PageRequest(page, size, null);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  PLAN_START_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        } else {
            s += " AND trunc(PLAN_START_TIME)=trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " AND orgid = ?" + list.size();
        }
        if (!StringUtils.isEmpty(type)) {
            list.add(orgid);
            s += " AND type = ?" + list.size();
        }

        //二级保电任务超期警告
        String sql1 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME " +
                "FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_txbd_task x LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and a.TWOCHECK_STATUS = 0) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //二级正常巡视任务超期警告
        String sql2 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_zc_task x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and a.TWOCHECK_STATUS = 0) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //正常巡视任务未上线 二级
        String sql3 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE a.TWOCHECK_STATUS = 0) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视任务未上线 二级
        String sql4 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE a.TWOCHECK_STATUS = 0) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视未按时接任务 二级
        String sql5 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND  a.TWOCHECK_STATUS = 0) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视未按时接任务 二级
        String sql6 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND  a.TWOCHECK_STATUS = 0) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //////一级
        //一级保电任务超期警告
        String sql11 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME " +
                "FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_txbd_task x LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and  a.ONECHECK_STATUS=0)xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //一级正常巡视任务超期警告
        String sql12 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_zc_task x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and  a.ONECHECK_STATUS=0) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视任务未上线 一级
        String sql13 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE a.ONECHECK_STATUS =0) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视任务未上线 一级
        String sql14 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE a.ONECHECK_STATUS =0) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视未按时接任务 一级
        String sql15 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND (ceil((sysdate - plan_end_time) * 24 * 60) > 40 and a.ONECHECK_STATUS =0)) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视未按时接任务 一级
        String sql16 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND  a.ONECHECK_STATUS =0) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //type == 0 为一级
        String sql = "";
        if(orgtype!=0){
            sql="SELECT * FROM (" +sql1+" UNION ALL "+sql2+" UNION ALL "+sql3+" UNION ALL "+sql4+" UNION ALL "+sql5+" UNION ALL "+sql6+") where 1=1 "+s;
        }else if (orgtype==0){
            sql = "SELECT * FROM (" +sql11+" UNION ALL "+sql12+" UNION ALL "+sql13+" UNION ALL "+sql14+" UNION ALL "+sql15+" UNION ALL "+sql16+") where 1=1 "+s;
        }
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return maps;
    }

    //巡视任务警告查询所有处理中警告
    public Object XSGJCZ(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        Pageable pageable = new PageRequest(page, size, null);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  PLAN_START_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(PLAN_START_TIME)=trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(orgid)) {
            list.add(orgid);
            s += " AND orgid = ?" + list.size();
        }
        if (!StringUtils.isEmpty(type)) {
            list.add(orgid);
            s += " AND type = ?" + list.size();
        }

        //二级保电任务超期警告
        String sql1 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME " +
                "FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_txbd_task x LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and  a.TWOCHECK_STATUS = 1) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //二级正常巡视任务超期警告
        String sql2 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_zc_task x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and  a.TWOCHECK_STATUS = 1) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //正常巡视任务未上线 二级
        String sql3 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE  a.TWOCHECK_STATUS = 1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视任务未上线 二级
        String sql4 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE  a.TWOCHECK_STATUS = 1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视未按时接任务 二级
        String sql5 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND  a.TWOCHECK_STATUS = 1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视未按时接任务 二级
        String sql6 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.TWOCHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND  a.TWOCHECK_STATUS = 1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //////一级
        //一级保电任务超期警告
        String sql11 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME " +
                "FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_txbd_task x LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "        and a.ONECHECK_STATUS=1)xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //一级正常巡视任务超期警告
        String sql12 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME FROM(SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  " +
                "   FROM(SELECT x.TASK_NAME,x.PLAN_START_TIME,'1' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "      FROM xs_zc_task x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                "      WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_end_time) < trunc(sysdate) " +
                "       and a.ONECHECK_STATUS=1) xx " +
                "     JOIN RZTSYSUSER us ON xx.cm_user_id = us.id) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视任务未上线 一级
        String sql13 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE a.ONECHECK_STATUS =1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视任务未上线 一级
        String sql14 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'2' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE  a.ONECHECK_STATUS =1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID WHERE us.LOGINSTATUS = 0 AND us.WORKTYPE = 2 AND us.USERDELETE = 1) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //正常巡视未按时接任务 一级
        String sql15 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_ZC_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND  a.ONECHECK_STATUS =1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";
        //保电巡视未按时接任务 一级
        String sql16 = "select cc.*,d.DEPTNAME from ( SELECT xxx.*,c.COMPANYNAME  FROM " +
                "  (SELECT xx.*,us.REALNAME, us.PHONE,us.COMPANYID,us.DEPTID  FROM " +
                "  (SELECT x.TASK_NAME,x.PLAN_START_TIME,'4' AS xs_warning_type,a.ONECHECK_STATUS,x.CM_USER_ID,x.ID " +
                "from XS_TXBD_TASK x JOIN ANOMALY_MONITORING a ON a.TASKID = x.ID " +
                " WHERE trunc(plan_start_time) = trunc(sysdate) AND plan_start_time < nvl(real_start_time, sysdate) " +
                "   AND  a.ONECHECK_STATUS =1) xx " +
                "  JOIN RZTSYSUSER us ON xx.CM_USER_ID = us.ID) xxx " +
                "  JOIN RZTSYSCOMPANY c ON c.ID = xxx.COMPANYID ) cc JOIN RZTSYSDEPARTMENT d on d.ID = cc.DEPTID";

        //type == 0 为一级
        String sql = "";
        if(orgtype!=0){
            sql="SELECT * FROM (" +sql1+" UNION ALL "+sql2+" UNION ALL "+sql3+" UNION ALL "+sql4+" UNION ALL "+sql5+" UNION ALL "+sql6+") where 1=1 "+s;
        }else if (orgtype==0){
            sql = "SELECT * FROM (" +sql11+" UNION ALL "+sql12+" UNION ALL "+sql13+" UNION ALL "+sql14+" UNION ALL "+sql15+" UNION ALL "+sql16+") where 1=1 "+s;
        }
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return maps;
    }

    //看护已处理
    public WebApiResponse khGjC(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        Pageable pageable = new PageRequest(page, size, null);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  PLAN_START_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        } else {
            s += " AND trunc(PLAN_START_TIME)=trunc(sysdate) ";
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
        String ejkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND (" +
                "                                                    a.TWOCHECK_STATUS = 0) AND USERDELETE = 1";
        /**
         * 一级未按规定时间开始任务
         */
        String yjkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND " +
                "      a.ONECHECK_STATUS =0 AND USERDELETE = 1";
        /**
         * 二级看护人员未上线
         */
        String ejkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      (" +
                "       a.TWOCHECK_STATUS = 0) AND USERDELETE = 1";
        /**
         * 一级看护人员未上线
         */
        String yjkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG  " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      " +
                "      a.ONECHECK_STATUS =0 AND USERDELETE = 1";
        /**
         * 二级脱岗
         */
        String ejkhTg = "SELECT k.ID,u.REALNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,k.WX_ORG AS COMPANYNAME,u.PHONE,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE a.TWOCHECK_STATUS = 0";
        /**
         * 一级脱岗
         */
        String yjkhTg = "SELECT k.ID,u.REALNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,k.WX_ORG AS COMPANYNAME,u.PHONE,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE  a.ONECHECK_STATUS =0";
        if (orgtype != 0) {
            String sql = "SELECT * FROM (" + ejkhTg + " union ALL " + ejkhUserWsx + " union ALL " + ejkhWks + ")  where 1=1 " + s;
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
    //看护处理中
    public WebApiResponse khGjCZ(Integer orgtype, Integer page, Integer size, String date, String orgid, String type) {
        Pageable pageable = new PageRequest(page, size, null);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  PLAN_START_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(PLAN_START_TIME)=trunc(sysdate) ";
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
        String ejkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND (" +
                "                                                    a.TWOCHECK_STATUS = 1) AND USERDELETE = 1";
        /**
         * 一级未按规定时间开始任务
         */
        String yjkhWks = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'未按规定时间开始任务' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID LEFT JOIN RZTSYSUSER u ON x.USER_ID = u.ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME=x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND " +
                "      a.ONECHECK_STATUS =1 AND USERDELETE = 1";
        /**
         * 二级看护人员未上线
         */
        String ejkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      (" +
                "       a.TWOCHECK_STATUS = 1) AND USERDELETE = 1";
        /**
         * 一级看护人员未上线
         */
        String yjkhUserWsx = "SELECT x.ID,u.REALNAME,x.PLAN_START_TIME,x.TDYW_ORG,x.TASK_NAME,x.WX_ORG AS COMPANYNAME,u.PHONE,'看护人员未上线' AS TYPE,de.ID AS orgid " +
                "FROM KH_TASK x LEFT JOIN ANOMALY_MONITORING a ON x.ID = a.TASKID" +
                "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID LEFT JOIN RZTSYSDEPARTMENT de ON de.DEPTNAME = x.TDYW_ORG  " +
                "WHERE trunc(plan_start_time) < nvl(real_start_time, sysdate) AND U.LOGINSTATUS = 0 AND" +
                "      " +
                "      a.ONECHECK_STATUS =1 AND USERDELETE = 1";
        /**
         * 二级脱岗
         */
        String ejkhTg = "SELECT k.ID,u.REALNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,k.WX_ORG AS COMPANYNAME,u.PHONE,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE  a.TWOCHECK_STATUS = 1";
        /**
         * 一级脱岗
         */
        String yjkhTg = "SELECT k.ID,u.REALNAME,k.PLAN_START_TIME,k.TDYW_ORG,k.TASK_NAME,k.WX_ORG AS COMPANYNAME,u.PHONE,'脱岗' AS TYPE,de.ID AS orgid " +
                "FROM WARNING_OFF_POST_USER_TIME UT" +
                "  JOIN WARNING_OFF_POST_USER UR ON UT.FK_USER_ID = UR.USER_ID" +
                "  JOIN KH_TASK K ON UR.TASK_ID = K.ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                "  LEFT JOIN ANOMALY_MONITORING a ON a.TASKID = k.ID   LEFT JOIN RZTSYSDEPARTMENT de ON k.TDYW_ORG = de.DEPTNAME " +
                "WHERE  a.ONECHECK_STATUS =1";
        if (orgtype != 0) {
            String sql = "SELECT * FROM (" + ejkhTg + " union ALL " + ejkhUserWsx + " union ALL " + ejkhWks + ")   where 1=1 " + s;
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

    /**
     * 处理中
     *
     * @param orgtype
     * @param explain
     * @param status
     * @param tasktype
     * @param anomalytype
     * @return
     */
    public WebApiResponse anomalyIns(String orgtype, String explain, Integer status, Integer tasktype, Integer anomalytype) {
        if (!orgtype.equals("0")) {
            Long id = new SnowflakeIdWorker(19, 25).nextId();
            try {
                return WebApiResponse.success(this.reposiotry.ejAnomalyIns(id, explain, status, tasktype, anomalytype));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("erro");
            }
        } else {
            Long id = new SnowflakeIdWorker(19, 25).nextId();
            try {
                return WebApiResponse.success(this.reposiotry.yiAnomalyIns(id, explain, status, tasktype, anomalytype));
            } catch (Exception e) {
                return WebApiResponse.erro("erro");
            }
        }

    }

    /**
     * 已完成处理
     *
     * @param orgtype
     * @param explain
     * @param status
     * @param tasktype
     * @param anomalytype
     * @return
     */
    public WebApiResponse anomalyInsO(String orgtype, String explain, Integer status, Integer tasktype, Integer anomalytype,Integer assessment) {
        if (!orgtype.equals("0")) {
            Long id = new SnowflakeIdWorker(19, 25).nextId();
            try {
                return WebApiResponse.success(this.reposiotry.ejAnomalyInsO(id, explain, status, tasktype, anomalytype,assessment));
            } catch (Exception e) {
                e.printStackTrace();
                return WebApiResponse.erro("erro");
            }
        } else {
            Long id = new SnowflakeIdWorker(19, 25).nextId();
            try {
                return WebApiResponse.success(this.reposiotry.yjAnomalyInsO(id, explain, status, tasktype, anomalytype,assessment));
            } catch (Exception e) {
                return WebApiResponse.erro("erro");
            }
        }

    }

}