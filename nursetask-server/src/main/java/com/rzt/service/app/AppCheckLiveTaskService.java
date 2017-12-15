package com.rzt.service.app;

import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.repository.CheckLiveTaskDetailRepository;
import com.rzt.service.CurdService;
import com.rzt.util.WebApiResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/13.
 */
@Service
@Transactional
public class AppCheckLiveTaskService extends CurdService<CheckLiveTaskDetail,CheckLiveTaskDetailRepository> {

    //查询该用户的所有父任务
    public WebApiResponse listCheckTask(String userId,String status) {
        try {
            String result = " c.id as execid,c.task_name as taskName,c.create_time as createTime";
            String sql = "select"+result+" from check_live_task_exec c " +
                    "left join rztsysuser u on u.id = c.user_id where c.user_id = ? and status =?";
            List<Map<String, Object>> maps = this.execSql(sql, userId, Integer.parseInt(status));
            return WebApiResponse.success(maps);
        }catch (Exception e){
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }
    //查询该用户某条任务的所有子任务
    public WebApiResponse appListAllCheckTask(String execId, String userId) {
        try {
//            this.reposiotry.findCheckById();
            String result = " c.id as detailId,c1.check_type as type,k.task_name as taskName,c.status as status";
            String sql = "select"+result+" from check_live_task_detail c " +
                        " left join check_live_task c1 on c.task_id = c1.id " +
                        " left join kh_task k on k.site_id = c1.task_id where c1.user_id = ? and c.exec_id = ?";
            List<Map<String, Object>> maps = this.execSql(sql, userId,execId);
            return WebApiResponse.success(maps);
        }catch (Exception e){
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

//查看某条看护任务 siteid  即时性查 where kh_task.planstarttime<现在 and planendtime>现在
    public WebApiResponse appListKhTaskById(String taskId, String detailId) {
        //字段：看护人、看护人员、是否在线、所属公司、照片、看护任务名称、计划开始时间、通道运维单位、联系电话、隐患地理位置
        //   kh_task rztsysuser 人员头像  kh_yh_history  check_live_task_detail check_live_task
        //待改
        try {
            String result = "u.realname as name,u.logintype as login,d.deptname as dept,u.头像字段 as picture,k.plan_start_time as startTime,u.phone as phone, k.task_name as taskname,k.thyw_org as yworg";
            String sql = "select "+result+" from check_live_task_detail c " +
                        "left join check_live_task c1 on c.task_id = c1.id " +
                        "left join kh_task k on k.site_id = c1.task_id " +
                        "left join rztsysuser u on u.id = k.user_id " +
                        "left join rzysysdepartment d on u.companid = d.id " +
                        "where c.id = ? and c1.user_id = ? and k.plan_start_time >=to_date(?,'YYYY-MM-DD hh24:mi') and k.plan_end_time <=to_date(?,'YYYY-MM-DD hh24:mi')";
            List<Map<String, Object>> maps = this.execSql(sql, detailId,taskId);
            return WebApiResponse.success(maps);
        }catch (Exception e){
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListXSTaskByTowerId(String userId, String towerId) {
        try {
            return WebApiResponse.success("");
        }catch (Exception e){
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败");
        }
    }

    public WebApiResponse appListXSTaskById(String userId, String xsId, int xslx) {
        //字段 任务名称 外协单位 运维单位 开始时间  结束时间 巡视人员 联系电话 人员评价
        try {
            String result = "";
            return WebApiResponse.success("");
        }catch (Exception e){
            e.printStackTrace();
            return WebApiResponse.erro("数据获取失败"+e.getMessage());
        }
    }


    //轨迹查询  追踪
}