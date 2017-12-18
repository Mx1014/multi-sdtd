package com.rzt.service.app;

import com.rzt.entity.KhTask;
import com.rzt.entity.KhTaskWpqr;
import com.rzt.repository.AppKhTaskRepository;
import com.rzt.repository.KhTaskWpqrRepository;
import com.rzt.service.CurdService;
import com.rzt.service.KhTaskWpqrService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/17.
 */
@Service
@Transactional
public class AppKhTaskService extends CurdService<KhTask, AppKhTaskRepository> {

    @Autowired
    private KhTaskWpqrService wpqrService;

    public List<Map<String,Object>> appListkhTask(int dbyb, Pageable pageable, String userId) {
        String result = " k.id as taskId,k.plan_start_time as startTime,k.task_name as taskName,k.status as status,u.realname as name";
        StringBuffer buffer = new StringBuffer();
        if (dbyb == 1) {
            buffer.append("where (status like '未开始' or status like '进行中')");
        } else if (dbyb == 2) {
            buffer.append(" where status like '已完成'");
        }
        String sql = "select " + result + " from kh_task k left join rztsysuser u on u.id = k.user_id " + buffer.toString() + " and user_id = ?";
        return this.execSql(sql, userId);
    }

    public WebApiResponse appListkhTaskById(String taskId) {
        try {
            String result ="K.TASK_NAME AS TASKNAME,H.YHMS AS MS,H.YHJB AS JB,K.PLAN_START_TIME AS STARTTIME,K.PLAN_END_TIME AS ENDTIME,K.STATUS AS STATUS ";
            String sql = "SELECT "+result+" FROM KH_TASK k LEFT JOIN KH_YH_HISTORY H WHERE K.ID=?";
            return WebApiResponse.success(this.execSql(sql,Long.parseLong(taskId)));
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取成功");
        }
    }

    public WebApiResponse appListUserInfoById(String userId,String taskId) {
        try {
            this.reposiotry.updateRealStartTime(Long.parseLong(taskId), DateUtil.dateNow());
            String result = "u.realname as name,u.classname as class,u.phone as phone";
            String sql = "select "+result+" from rztsysuser u";
            return WebApiResponse.success(this.execSql(sql,userId));
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取成功");
        }
    }

    public WebApiResponse appSavePhoto(String userId,String taskId) {
        try {
            this.reposiotry.updateSFQRTime(DateUtil.dateNow(),Long.parseLong(taskId));
            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取成功");
        }
    }

    public WebApiResponse appSaveWpzt(KhTaskWpqr task) {
        try {
            wpqrService.add(task);
            this.reposiotry.updateWpqrTime(task.getTaskId(),DateUtil.dateNow());
            //缺少保存物品图片
            //缺少目前施工进度
            String result = "y.YHMS as ms,y.YHZRDW as DW,Y.YHZRDWLXR AS PEOPLE,Y.YHZRDWDH AS PHONE";
            String sql = "SELECT "+result+"  FROM KH_TASK K LEFT JOIN KH_YH_HISTORY Y ON y.id = k.YH_ID WHERE k.ID = ? ";
            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取成功");
        }
    }

    public WebApiResponse appDdcx(String taskId) {
        try {
            this.reposiotry.updateDDTime(DateUtil.dateNow(),Long.parseLong(taskId));
            return WebApiResponse.success("");
        } catch (Exception e) {
            return WebApiResponse.erro("数据获取成功");
        }
    }
}