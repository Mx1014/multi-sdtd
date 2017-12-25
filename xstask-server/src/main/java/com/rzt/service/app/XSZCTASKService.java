/**
 * 文件名：XSZCTASKService
 * 版本信息：
 * 日期：2017/12/05 10:02:41
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service.app;

import com.rzt.entity.app.XSZCTASK;
import com.rzt.repository.app.XSZCTASKRepository;
import com.rzt.service.CurdService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
     * @param status   0 代办 1已办
     * @return
     */
    public Page<Map<String, Object>> xsTask(Integer page, Integer size, String userid, Integer status) {
        Pageable pageable = new PageRequest(page, size, null);
        if (status == null || status == 0) {
            /**
             * 保电代办
             */
            String sqlBddb = "SELECT id,plan_start_time AS planstarttime,plan_end_time   AS planendtime,task_name AS taskname,XSLX AS xslxnum,STAUTS STATUS,ZXYS_NUM AS zxys,XSCS_NUM AS XSCS,plan_xs_num FROM xs_txbd_task WHERE (stauts = 0 OR stauts = 1) AND plan_start_time >= trunc(sysdate) and cm_user_id = ?1 ";
            /**
             * 正常巡视代办
             */
            String sqlZcdb = "SELECT id,plan_start_time AS planstarttime,plan_end_time   AS planendtime,task_name AS taskname,2 AS xslxnum,STAUTS STATUS,ZXYS_NUM AS zxys,XSCS_NUM AS XSCS,plan_xs_num  FROM xs_zc_task WHERE (stauts = 0 OR stauts = 1) AND plan_start_time >= trunc(sysdate) AND cm_user_id = ?2  ";
            String sql = "" + sqlBddb + " UNION ALL " + sqlZcdb + "";
            return this.execSqlPage(pageable, sql, userid, userid);
        } else {
            /**
             * 保电已办
             */
            String sqlBdyb = "select id,plan_start_time as planstarttime ,plan_end_time as planendtime,task_name as taskname,XSLX AS xslxnum,STAUTS status from xs_txbd_task where cm_user_id = ?1 and (stauts = 2)";
            /**
             * 正常已办
             */
            String sqlZcyb = "  select id,plan_start_time as planstarttime ,plan_end_time as planendtime,task_name as taskname,2 AS xslxnum,STAUTS status  from xs_zc_task where  cm_user_id =?2 and (stauts=2 )";
            String sql = "" + sqlBdyb + " UNION ALL " + sqlZcyb + "";
            return this.execSqlPage(pageable, sql, userid, userid);
        }
    }

    /**
     * xslx 巡视类型 0 特殊 1 保电 2 正常
     * 巡视任务详情
     *
     * @param xslx
     * @param id
     * @return
     */
    public Object tourMissionDetails(Integer xslx, Long id) throws Exception{
        String bdtxSql = "SELECT ID,td_org,real_start_time,stauts status,task_name  AS taskname,plan_start_time AS starttime,cm_user_id AS userid,PD_TIME AS pdtime,plan_xs_num AS plannum,xs_txbd_cycle_id cycleid FROM xs_txbd_task WHERE id = ?1";
        String zcxsSql = "SELECT ID,td_org,real_start_time,stauts status,task_name AS taskname, plan_start_time AS starttime,  cm_user_id AS userid,  PD_TIME AS pdtime,plan_xs_num AS plannum,xs_zc_cycle_id cycleid FROM xs_zc_task   WHERE id = ?1";
        String userSql = "SELECT\n" +
                "  t.REALNAME,\n" +
                "  tt.DEPTNAME,\n" +
                "  ttt.COMPANYNAME\n" +
                "FROM (SELECT *\n" +
                "      FROM RZTSYSUSER\n" +
                "      WHERE id = ?) t\n" +
                "  JOIN RZTSYSDEPARTMENT tt ON t.DEPTID = tt.id\n" +
                "  JOIN RZTSYSCOMPANY ttt ON ttt.id = t.COMPANYID";
        String deptmentSql = "select DEPTNAME from RZTSYSDEPARTMENT where id = ?";
        Map<String, Object> task = null;
        if (xslx == 0 || xslx == 1) {
            task = this.execSqlSingleResult(bdtxSql, id);
        } else {
            task = this.execSqlSingleResult(zcxsSql, id);
        }
        String userid = task.get("USERID").toString();
        String cycleid = task.get("CYCLEID").toString();
        String td_org = task.get("TD_ORG").toString();
        Map<String, Object> user = this.execSqlSingleResult(userSql, userid);;
        task.put("USERNAME",user.get("REALNAME"));
        task.put("CLASSNAME",user.get("DEPTNAME"));
        task.put("WXNAME",user.get("COMPANYNAME"));
        Map<String, Object> cycle = null;
        if (xslx == 0 || xslx == 1) {
            String cycleSql = "SELECT\n" +
                    "  tt.LINE_NAME,\n" +
                    "  t.section,\n" +
                    "  t.V_LEVEL\n" +
                    "FROM (SELECT *\n" +
                    "      FROM XS_TXBD_CYCLE\n" +
                    "      WHERE id = ?) t\n" +
                    "  JOIN CM_LINE tt ON t.LINE_ID = tt.id";
            cycle = this.execSqlSingleResult(cycleSql, cycleid);

        } else {
            String cycleSql = "SELECT\n" +
                    "  tt.LINE_NAME LINENAME,\n" +
                    "  t.section,\n" +
                    "  t.V_LEVEL\n" +
                    "FROM (SELECT *\n" +
                    "      FROM XS_ZC_CYCLE\n" +
                    "      WHERE id = ?) t\n" +
                    "  JOIN CM_LINE tt ON t.LINE_ID = tt.id";
            cycle = this.execSqlSingleResult(cycleSql, cycleid);
        }
        task.put("LINENAME",cycle.get("LINENAME"));
        task.put("SECTION",cycle.get("SECTION"));
        task.put("VLEVEL",cycle.get("V_LEVEL"));
        Map<String, Object> td = this.execSqlSingleResult(deptmentSql, td_org);
        task.put("TDNAME",td.get("DEPTNAME"));

        return task;
    }

    /**
     * 人员信息采集查询
     *
     * @param xslx 0 特殊 1 保电 2 正常
     * @param id   任务ID
     * @return
     */
    public Object personCollection(Integer xslx, Long id) throws Exception {
        String bdtxSql = "SELECT ID,cm_user_id AS userid,SFQR_TIME AS sfqr FROM xs_txbd_task WHERE id = ?1";
        String zcxsSql = "SELECT ID, cm_user_id AS userid,SFQR_TIME AS sfqr FROM xs_zc_task   WHERE id = ?1";
        String userSql = "SELECT\n" +
                "  t.REALNAME,\n" +
                "  tt.DEPTNAME,\n" +
                "  t.PHONE\n" +
                "FROM (SELECT *\n" +
                "      FROM RZTSYSUSER\n" +
                "      WHERE id = ?) t\n" +
                "  JOIN RZTSYSDEPARTMENT tt ON t.DEPTID = tt.id\n";
        String deptmentSql = "select DEPTNAME from RZTSYSDEPARTMENT where id = ?";
        Map<String, Object> task = null;
        if (xslx == 0 || xslx == 1) {
            task = this.execSqlSingleResult(bdtxSql, id);
        } else {
            task = this.execSqlSingleResult(zcxsSql, id);
        }
        String userid = task.get("USERID").toString();
        Map<String, Object> user = this.execSqlSingleResult(userSql, userid);;
        user.put("sfqrTime",task.get("SFQR"));
        return user;
    }


    /**
     * 物品提醒查询
     *
     * @param xslx   0 特殊 1 保电 2 正常
     * @param taskId 任务ID
     * @return
     */
    public Object itemsToRemind(Integer xslx, Long taskId)  {

            String bdtxSql = "SELECT ID,cm_user_id AS userid,wptx_time AS wptxTime FROM xs_txbd_task WHERE id = ?1";
            String zcxsSql = "SELECT ID, cm_user_id AS userid,wptx_time AS wptxTime FROM xs_zc_task   WHERE id = ?1";
            String userSql = "SELECT\n" +
                    "  t.REALNAME,\n" +
                    "  tt.DEPTNAME,\n" +
                    "  t.PHONE\n" +
                    "FROM (SELECT *\n" +
                    "      FROM RZTSYSUSER\n" +
                    "      WHERE id = ?) t\n" +
                    "  JOIN RZTSYSDEPARTMENT tt ON t.DEPTID = tt.id\n";
            String deptmentSql = "select DEPTNAME from RZTSYSDEPARTMENT where id = ?";
            Map<String, Object> task = null;
            Map<String, Object> wpts = null;
            if (xslx == 0 || xslx == 1) {
                String txbd = "SELECT ID,TASKID,WP_ZT AS WPZT FROM XS_TXBD_TASKWPQR where taskid=?1 ";
                try {
                    wpts = this.execSqlSingleResult(txbd, taskId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    task = this.execSqlSingleResult(bdtxSql, taskId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String zcbd = "SELECT ID,TASKID,WP_ZT AS WPZT FROM XS_ZC_TASKWPQR where taskid=?1 ";
                try {
                    wpts = this.execSqlSingleResult(zcbd, taskId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    task = this.execSqlSingleResult(zcxsSql, taskId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(wpts != null) {
                task.put("wpts",wpts.get("WPZT"));
            }
            return task;

    }
    /**
    * @Method xsTaskCount
    * @Description  返回待办/已办任务数
    * @param [userid, status]
    * @return java.lang.Object
    * @date 2017/12/15 11:58
    * @author nwz
    */
    public Object xsTaskCount(String userid, Integer status) {
        if (status == 0) {
            /**
             * 保电代办
             */
            String sqlBddb = "SELECT count(1) FROM xs_txbd_task WHERE (stauts = 0 OR stauts = 1) AND plan_start_time >= trunc(sysdate) and cm_user_id = ?1 ";
            /**
             * 正常巡视代办
             */
            String sqlZcdb = "SELECT count(1)  FROM xs_zc_task WHERE (stauts = 0 OR stauts = 1) AND plan_start_time >= trunc(sysdate) AND cm_user_id = ?2  ";
            String sql = "select (" + sqlBddb + ") + (" + sqlZcdb + ") as count from dual";
            return this.execSql(sql, userid, userid);
        } else {
            /**
             * 保电已办
             */
            String sqlBdyb = "select count(1) from xs_txbd_task where cm_user_id = ?1 and (stauts = 2)";
            /**
             * 正常已办
             */
            String sqlZcyb = "  select count(1) from xs_zc_task where  cm_user_id =?2 and (stauts = 2 )";
            String sql = "select (" + sqlBdyb + ") + (" + sqlZcyb + ") as count from dual";
            return this.execSql( sql, userid, userid);
        }
    }

    /***
    * @Method xsTowerList
    * @Description 返回巡视页面的杆塔列表
    * @param [xslx, taskId, cycleId]
    * @return java.lang.Object
    * @date 2017/12/15 15:42
    * @author nwz
    */
    public Object xsTowerList(Integer xslx, Long taskId) {
        List<Map<String, Object>> xsTowers = null;
        Map<String,Object> map = null;
        List<Map<String, Object>> execs = null;
        List<Map<String, Object>> execDetails = null;
        Map<String,Object> resultMap = new HashMap<String,Object>();
        if(xslx == 0 || xslx == 1) {
            String execSql = "select * from XS_txbd_TASK_EXEC where XS_txbd_TASK_ID = ? order by XS_REPEAT_NUM desc";
            execs = this.execSql(execSql, taskId);
            Long execId = Long.parseLong(execs.get(0).get("id").toString());
            String execDetailId = "select t.*,ttt.LONGITUDE,ttt.LATITUDE from (select * from XS_txbd_TASK_EXEC_DETAIL where XS_txbd_TASK_EXEC_ID = ?) t join CM_LINE_TOWER tt on t.START_TOWER_ID = tt.ID join CM_TOWER ttt on tt.TOWER_ID = ttt.id order by t.id ";
            execDetails = this.execSql(execDetailId, execId);

        } else {
            String execSql = "select * from XS_ZC_TASK_EXEC where XS_ZC_TASK_ID = ? order by XS_REPEAT_NUM desc";
            execs = this.execSql(execSql, taskId);
            Long execId = Long.parseLong(execs.get(0).get("ID").toString());
            String execDetailId = "select t.*,ttt.LONGITUDE,ttt.LATITUDE from (select * from XS_ZC_TASK_EXEC_DETAIL where XS_ZC_TASK_EXEC_ID = ?) t join CM_LINE_TOWER tt on t.START_TOWER_ID = tt.ID join CM_TOWER ttt on tt.TOWER_ID = ttt.id order by t.id";
            execDetails = this.execSql(execDetailId, execId);
        }
        resultMap.put("towerList",xsTowers);
        resultMap.put("execs",execs);
        resultMap.put("execDetails",execDetails);
        return resultMap;
    }


    /***
    * @Method historyXsTowerList
    * @Description 历史巡视情况展示
    * @param [xslx, execId]
    * @return java.lang.Object
    * @date 2017/12/19 16:25
    * @author nwz
    */
    public Object historyXsTowerList(Integer xslx, Long execId) {
        Map<String,Object> map = null;
        List<Map<String, Object>> execDetails = null;
        Map<String,Object> resultMap = new HashMap<String,Object>();
        if(xslx == 0 || xslx == 1) {
            String execDetailId = "select * from XS_ZC_TASK_EXEC_DETAIL where XS_ZC_TASK_EXEC_ID = ? ";
            execDetails = this.execSql(execDetailId, execId);

        } else {
            String execDetailId = "select * from XS_ZC_TASK_EXEC_DETAIL where XS_ZC_TASK_EXEC_ID = ? order by id";
            execDetails = this.execSql(execDetailId, execId);
        }
        resultMap.put("execDetails",execDetails);
        return resultMap;
    }

}