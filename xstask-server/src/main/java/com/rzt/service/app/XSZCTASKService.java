/**
 * 文件名：XSZCTASKService
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service.app;

import com.alibaba.druid.sql.visitor.functions.Char;
import com.rzt.entity.appentity.XSZCTASK;
import com.rzt.repository.app.XSZCTASKRepository;
import com.rzt.service.CurdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 类名称：XSZCTASKService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:02:41
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:02:41
 * 修改备注：
 */
@Service
public class XSZCTASKService extends CurdService<XSZCTASK, XSZCTASKRepository> {
    /**
     * 正常巡视与保电特寻app代办进行中已办查询
     *
     * @param userid
     * @param dbyb   1 代办 2已办
     * @return
     */
    public Page<Map<String, Object>> xsTask(int page, int size, String userid, int dbyb) {
        Pageable pageable = new PageRequest(page, size, null);
        int one = 1;
        int two = 2;
        if (dbyb == one) {
            /**
             * 保电代办
             */
            String sqlBddb = "SELECT id,plan_start_time AS planstarttime,plan_end_time   AS planendtime,task_name       AS taskname,XSLX_NUM        AS xslxnum,STAUTS FROM xs_txbd_task WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_start_time) = trunc(sysdate) and cm_user_id = ?1 ";
            /**
             * 正常巡视代办
             */
            String sqlZcdb = "SELECT id,plan_start_time AS planstarttime,plan_end_time   AS planendtime,task_name       AS taskname,2        AS xslxnum,STAUTS FROM xs_zc_task WHERE (stauts = 0 OR stauts = 1) AND trunc(plan_start_time) = trunc(sysdate) AND cm_user_id = ?2  ";
            String sql = "(" + sqlBddb + ") UNION ALL (" + sqlZcdb + ")";
            return this.execSqlPage(pageable, sql, userid, userid);
        } else if (dbyb == two) {
            /**
             * 保电已办
             */
            String sqlBdyb = "select id,plan_start_time as planstarttime ,plan_end_time as planendtime,task_name as taskname from xs_txbd_task,STAUTS where cm_user_id = ?1 and (stauts = 2) and trunc(plan_start_time) = trunc(sysdate)";
            /**
             * 正常已办
             */
            String sqlZcyb = "  select id,plan_start_time as planstarttime ,plan_end_time as planendtime,task_name as taskname  from xs_zc_task,STAUTS where  cm_user_id =?2 and (stauts=2 ) and trunc(plan_start_time) = trunc(sysdate)";
            String sql = "(" + sqlBdyb + ") UNION AL (" + sqlZcyb + ")";
            return this.execSqlPage(pageable, sql, userid, userid);
        }
        return null;
    }

    /**
     * xslx 巡视类型 0 特殊 1 保电 2 正常
     * 巡视任务详情
     *
     * @param xslx
     * @param id
     * @return
     */
    public List<Map<String, Object>> tourMissionDetails(int xslx, String id) {
        int zero = 0;
        int one = 1;
        int two = 2;
        String bdtxSql = "SELECT ID,task_name       AS taskname,plan_start_time AS starttime,td_org          AS orgname,cm_user_id      AS username,WX_ORG          AS wxname,CLASS_ID        AS classname,PD_TIME         AS pdtime,STAUTS,plan_xs_num     AS plannum,real_xs_num     AS realnum FROM xs_txbd_task WHERE id = ?1";
        String zcxsSql = "SELECT ID,  task_name       AS taskname, plan_start_time AS starttime, td_org          AS orgname, cm_user_id      AS username, WX_ORG          AS wxname, CLASS_ID        AS classname, PD_TIME         AS pdtime,  STAUTS,  plan_xs_num     AS plannum, real_xs_num     AS realnum FROM xs_zc_task   WHERE id = ?1";
        if (xslx == one || xslx == zero) {
            return this.execSql(bdtxSql, id);
        } else if (xslx == two) {
            return this.execSql(zcxsSql, id);
        }
        return null;
    }

    /**
     * 人员信息采集查询
     *
     * @param xslx 0 特殊 1 保电 2 正常
     * @param id   任务ID
     * @return
     */
    public List<Map<String, Object>> personCollection(int xslx, String id) {
        int zero = 0;
        int one = 1;
        int two = 2;
        String zcXsSql = "SELECT ID, CM_USER_ID AS cmuser,CLASS_ID as classname,'暂无' AS phonecontact FROM XS_ZC_TASK WHERE  ID = ?1 ";
        String bdXsSql = "SELECT ID, CM_USER_ID AS cmuser,CLASS_ID as classname,'暂无' AS phonecontact FROM XS_TXBD_TASK WHERE  ID = ?1 ";
        if (xslx == one || xslx == zero) {
            return this.execSql(bdXsSql, id);
        } else if (xslx == two) {
            return this.execSql(zcXsSql, id);
        }
        return null;
    }

    /**
     * 物品提醒查询
     *
     * @param xslx 0 特殊 1 保电 2 正常
     * @param id   任务ID
     * @return
     */
    public List<Map<String, Object>> itemsToRemind(int xslx, String id) {
        int zero = 0, one = 1, two = 2;
        String txbd = "SELECT ID,TASKID,WP_ZT AS WPZT FROM XS_TXBD_TASKWPQR where taskid=?1 ";
        String zcbd = "SELECT ID,TASKID,WP_ZT AS WPZT FROM XS_ZC_TASKWPQR where taskid=?1 ";
        if (xslx == zero || xslx == one) {
            return this.execSql(txbd, id);
        } else if (xslx == two) {
            return this.execSql(zcbd, id);
        }
        return null;
    }
}