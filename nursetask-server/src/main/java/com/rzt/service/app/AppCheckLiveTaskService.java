package com.rzt.service.app;

import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.repository.AppCheckLiveTaskRepository;
import com.rzt.repository.CheckLiveTaskDetailRepository;
import com.rzt.service.CurdService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/13.
 */
@Service
@Transactional
public class AppCheckLiveTaskService extends CurdService<CheckLiveTaskDetail, AppCheckLiveTaskRepository> {

    //查询该用户的所有父任务
    public WebApiResponse listCheckTask(String userId, int status) {
        try {
            String result = " c.id as execid,c.task_name as taskName,c.create_time as createTime";
            String sql = "select" + result + " from check_live_task_exec c " +
                    "left join rztsysuser u on u.id = c.user_id where c.user_id = ? and status =?";
            List<Map<String, Object>> maps = this.execSql(sql, userId, status);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    //查询该用户某条任务的所有子任务
    public WebApiResponse appListAllCheckTask(long execId, String userId) {
        try {
//            this.reposiotry.findCheckById();
            String result = " c.id as detailId,c1.check_type as type,k.task_name as taskName,c.status as status";
            String sql = "select" + result + " from check_live_task_detail c " +
                    " left join check_live_task c1 on c.task_id = c1.id " +
                    " left join kh_task k on k.site_id = c1.task_id where c1.user_id = ? and c.exec_id = ?";
            List<Map<String, Object>> maps = this.execSql(sql, userId, execId);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    //查看某条看护任务 siteid  即时性查 where kh_task.planstarttime<现在 and planendtime>现在
    public WebApiResponse appListKhTaskById(String userId, long detailId) {
        //字段：看护人、看护人员、是否在线、所属公司、照片、看护任务名称、计划开始时间、通道运维单位、联系电话、隐患地理位置
        //   kh_task rztsysuser 人员头像  kh_yh_history  check_live_task_detail check_live_task
        //待改
        try {
            String result = "k.id as khTaskId,c.id as detailId,c1.id as checkId,u.realname as name,u.LOGINSTATUS as login,d.deptname as dept,u.头像字段 as picture,k.plan_start_time as startTime,u.phone as phone, k.task_name as taskname,k.tdyw_org as yworg";
            String sql = "select " + result + " from check_live_task_detail c " +
                    "left join check_live_task c1 on c.task_id = c1.id " +
                    "left join kh_task k on k.site_id = c1.task_id " +
                    "left join rztsysuser u on u.id = k.user_id " +
                    "left join RZTSYSDEPARTMENT d on u.DEPTID = d.id " +
                    "where c.id = ? and c1.user_id = ? and trunc(k.plan_start_time) >= trunc(sysdate) and trunc(k.plan_end_time) <= trunc(sysdate)";
            //k.plan_start_time >=to_date(?,'YYYY-MM-DD hh24:mi')
            List<Map<String, Object>> maps = this.execSql(sql, detailId, userId);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListXSTaskByTowerId(String userId, long towerId) {
        try {
            //查询即时的正常巡视任务
            String sql = "(select T.STAUTS AS STATUS,T.ID AS TASKID,T.TASK_NAME AS TASKNAME,2 AS XSLX from XS_ZC_TASK T LEFT JOIN XS_ZC_CYCLE_LINE_TOWER C ON T.XS_ZC_CYCLE_ID = C.XS_ZC_CYCLE_ID  WHERE TRUNC(T.PLAN_START_TIME)<= TRUNC(SYSDATE) AND TRUNC(T.PLAN_END_TIME) >= TRUNC(SYSDATE) AND C.CM_LINE_TOWER_ID = ? ) " +
                    "UNION ALL " +
                    "(select T.STAUTS AS STATUS,T.ID AS TASKID,T.TASK_NAME AS TASKNAME,T.XSLX AS XSLX from XS_TXBD_TASK T LEFT JOIN XS_TXBD_CYCLE_LINE_TOWER C  ON T.XS_TXBD_CYCLE_ID = C.XS_TXBD_CYCLE_ID WHERE TRUNC(T.PLAN_START_TIME)<= TRUNC(SYSDATE) AND TRUNC(T.PLAN_END_TIME) >= TRUNC(SYSDATE) AND C.CM_LINE_TOWER_ID =?)";
            List<Map<String, Object>> list = this.execSql(sql, towerId, towerId);
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListXSTaskById(String userId, Long xsId, int xslx) {
        //字段 任务名称 外协单位 运维单位 开始时间  结束时间 巡视人员 联系电话 人员评价
        try {
            String result = "T.TASK_NAME AS TASKNAME,T.WX_ORG AS WXORG,T.TD_ORG AS YWORG,T.PLAN_START_TIME AS STARTTIME,T.PLAN_END_TIME AS ENDTIME,U.REALNAME AS NAME,U.PHONE AS PHONE";
            String sql = "";
            if (xslx == 1 || xslx == 0) {
                sql = "select " + result + " FROM XS_ZC_TASK T LEFT JOIN RZTSYSUSER U ON U.ID = T.CM_USER_ID WHERE T.ID = ?";
            } else if (xslx == 2) {
                sql = "select " + result + " FROM XS_TXBD_TASK T LEFT JOIN RZTSYSUSER U ON U.ID = T.CM_USER_ID WHERE T.ID = ?";
            }
            List<Map<String, Object>> list = this.execSql(sql, xsId);
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败" + e.getMessage());
        }
    }

    public WebApiResponse updateDdxcTime(String userId, long KhTaskId, String date, long detailId) {
        try {
            Date date1 = DateUtil.parseDate(date);
            this.reposiotry.updateDdxcTime(userId, KhTaskId, date1, detailId);
            String sql = "SELECT C1.ID AS CHECKID,C.ID AS DETAILID,C.SFZG AS SFXZ,C.RYYZ AS RYYZ,(人员图片),C1.DZWL AS DZWL FROM CHECK_LIVE_TASK_DETAIL C LEFT JOIN SDTD27.CHECK_LIVE_TASK C1 ON C.TASK_ID = C1.ID WHERE C.KH_TASK_ID = ?";
            List<Map<String, Object>> maps = this.execSql(sql, KhTaskId);
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败" + e.getMessage());
        }
    }

    //到岗到位
    public WebApiResponse appDgdwCheck(CheckLiveTaskDetail detail) {
        try {
            this.reposiotry.updateDgdwCheck(detail.getSfzg(), detail.getRyyz(), detail.getExecId());
            this.reposiotry.updateDzwl(detail.getTaskId(), detail.getDydj());
            String sql = "SELECT  C.ID AS DETAILID,H.VTYPE AS VOLTAGE,H.LINE_NAME AS LINENAME,H.SECTION AS SECTION,H.YHMS AS MS,H.YHZRDW AS DW,H.YHZRDWLXR AS PERSON,H.YHZRDWDH AS PHONE,C.DYDJ AS DYDJ,C.YHXX AS YHXX,C.CZFA AS CZFA,C.QTWT AS QTWT  FROM CHECK_LIVE_TASK_DETAIL C LEFT JOIN CHECK_LIVE_TASK T ON T.ID = C.TASK_ID LEFT JOIN KH_YH_HISTORY H ON T.YH_ID = H.ID WHERE ID = ?";
            List<Map<String, Object>> maps = this.execSql(sql, detail.getExecId());
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败" + e.getMessage());
        }
    }

    public WebApiResponse appCompleteTask(CheckLiveTaskDetail detail) {
        try {
            this.reposiotry.completeTask(detail.getTaskId(), detail.getDydj(), detail.getYhxx(), detail.getCzfa(), detail.getQtwt(), new Date());
            return WebApiResponse.success("");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败" + e.getMessage());
        }
    }


    //轨迹查询  追踪
}