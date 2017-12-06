/**
 * 文件名：XSZCTASKService
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service.appservice;

import com.rzt.entity.appentity.XSZCTASK;
import com.rzt.repository.apprepository.XSZCTASKRepository;
import com.rzt.service.CurdService;
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
     * @param xszt
     * @param dbyb
     * @return
     */
    public List<Map<String, Object>> xsTask(String userid, int xszt, int dbyb) {
        int one = 1;
        int two = 2;
        /**
         * 保电代办
         */
        String sqlBddb = "select id, task_name, cm_user_id, xs_txbd_cycle_id, td_org, wx_org, class_id, stauts, plan_xs_num, real_xs_num, plan_start_time, plan_end_time, real_start_time, sfqr_time, ddxc_time, xsks_time, real_end_time, task_num_in_cycle from xs_txbd_task where cm_user_id = ?1 and (stauts = 0 or stauts = 1) and trunc(plan_start_time) = trunc(sysdate)";
        /**
         * 正常巡视代办
         */
        String sqlZcdb = "  select id, task_name, cm_user_id, td_org, wx_org, class_id, xs_zc_cycle_id, stauts, plan_xs_num, real_xs_num, plan_start_time, plan_end_time, real_start_time, sfqr_time, ddxc_time, xsks_time, real_end_time, task_num_in_cycle from xs_zc_task where  cm_user_id =1? and (stauts=0 or stauts=1 ) and trunc(plan_start_time) = trunc(sysdate)";
        /**
         * 保电已办
         */
        String sqlBdyb = "select id, task_name, cm_user_id, xs_txbd_cycle_id, td_org, wx_org, class_id, stauts, plan_xs_num, real_xs_num, plan_start_time, plan_end_time, real_start_time, sfqr_time, ddxc_time, xsks_time, real_end_time, task_num_in_cycle from xs_txbd_task where cm_user_id = ?1 and (stauts = 2) and trunc(plan_start_time) = trunc(sysdate)";
        /**
         * 正常已办
         */
        String sqlZcyb = "  select id, task_name, cm_user_id, td_org, wx_org, class_id, xs_zc_cycle_id, stauts, plan_xs_num, real_xs_num, plan_start_time, plan_end_time, real_start_time, sfqr_time, ddxc_time, xsks_time, real_end_time, task_num_in_cycle from xs_zc_task where  cm_user_id =1? and (stauts=2 ) and trunc(plan_start_time) = trunc(sysdate)";
        if (xszt == one) {
            if (dbyb == one) {
                return this.execSql(sqlZcdb, userid);
            } else if (dbyb == two) {
                return this.execSql(sqlZcyb, userid);
            }
        } else if (xszt == two) {
            if (dbyb == one) {
                return this.execSql(sqlBddb, userid);
            } else if (dbyb == two) {
                return this.execSql(sqlBdyb, userid);
            }
        }
        return null;
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视
     *
     * @param xslx
     * @param id
     * @return
     */
    public List<Map<String, Object>> tourMissionDetails(int xslx, String id) {
        int one = 1;
        int two = 2;
        String bdtxSql = "select id, task_name, cm_user_id, xs_txbd_cycle_id, td_org, wx_org, class_id, stauts, plan_xs_num, real_xs_num, to_char(plan_start_time,'yyyy-MM-dd hh24:mi:ss') as plan_start_time, to_char(plan_end_time,'yyyy-MM-dd hh24:mi:ss') as plan_end_time, real_start_time, sfqr_time, ddxc_time, xsks_time, real_end_time, task_num_in_cycle from xs_txbd_task where id = ?1";
        String zcxsSql = "select id, task_name, cm_user_id, td_org, wx_org, class_id, xs_zc_cycle_id, stauts, plan_xs_num, real_xs_num, plan_start_time, plan_end_time, real_start_time, sfqr_time, ddxc_time, xsks_time, real_end_time, task_num_in_cycle from xs_zc_task where id = ?1";
        if (xslx == one) {
            return this.execSql(zcxsSql, id);
        } else if (xslx == two) {
            return this.execSql(bdtxSql, id);
        }
        return null;
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视
     *
     * @param id
     * @param xslx
     * @return
     */
    public int updateSfqrTime(String id, int xslx) {
        int one = 1;
        int two = 2;
        if (xslx == one) {
            return this.reposiotry.zxXsSfqrTime(id);
        } else if (two == two) {
            return this.reposiotry.bdXsSfqrTime(id);
        }
        return 0;
    }
}