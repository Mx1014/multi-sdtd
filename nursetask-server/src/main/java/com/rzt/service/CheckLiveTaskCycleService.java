/**
 * 文件名：CHECKLIVETASKDETAILService
 * 版本信息：
 * 日期：2017/12/05 10:24:09
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.rzt.entity.CheckLiveTaskCycle;
import com.rzt.repository.CheckLiveTaskCycleRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类名称：CHECKLIVETASKDETAILService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2017/12/05 10:24:09
 * 修改人：张虎成
 * 修改时间：2017/12/05 10:24:09
 * 修改备注：
 * @version
 */
@Service
public class CheckLiveTaskCycleService extends CurdService<CheckLiveTaskCycle, CheckLiveTaskCycleRepository> {

    @Autowired
    private CheckLiveTaskCycleRepository repository;

    @ApiOperation(value = "稽查维护维护",notes = "稽查维护的分页查询，条件搜索")
    public  Page<Map<String, Object>> listCheckTaskMain(String startTime,String endTime, String userId,String taskName,Pageable pageable) {
        String result =" c.id id,c.task_name taskName,c.TASK_TYPE taskType,u.realname realName," +
                       "c.create_time createTime,c.plan_start_time startTime,c.plan_end_time endTime,c1.CHECK_CYCLE checkcycle ";
        StringBuilder sb = new StringBuilder();
        sb.append(" where 1=1 ");
        List params = new ArrayList<>();
        //时间段查询

        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
            startTime =  startTime.substring(0, 10);
            endTime = endTime.substring(0, 10);
            sb.append(" and c.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
            params.add(startTime);
            params.add(endTime);
        }
        //任务名查询
        if (!StringUtils.isEmpty(taskName)) {
            sb.append(" and c.task_name like ? ");
            params.add("%"+taskName+"%");
        }
        //用户人查询
        if(!StringUtils.isEmpty(userId)){
            sb.append(" and c.user_id = ? ");
            params.add(userId);
        }

        sb.append(" order by c.create_time desc ");
        String sql = "select "+result+ " from  CHECK_LIVE_TASK_EXEC c left join chenk_live_task_cycle c1 on c.CYCLE_ID=c1.ID " +
                    " left join rztsysuser u on c.user_id = u.id " + sb.toString();

        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());

        return maps;

    }

    @ApiOperation(value = "稽查任务下子任务查询",notes = "稽查任务下子任务查询，条件搜索")
    public List  listCheckTaskDetailById(String id,String taskType) {
        String sql =null;
        if(taskType.equals("0")){
            //看护
            sql = " select c2.id id,c2.task_name taskName,c1.SFZG sfzg,c1.RYYZ ryyz,c1.DYDJ dydj,c1.YHXX yhxx,c1.CZFA czfa,c1.QTWT qtwt " +
                    " from  check_live_task_exec c  left join check_live_task_detail c1 on c.id = c1.exec_id " +
                    " left join KH_SITE c2 on c1.KH_TASK_ID = c2.id where c.id=? ";

        }else{
            //稽查
            sql = " select c2.id id,c2.task_name taskName,c1.SFZG sfzg,c1.RYYZ ryyz,c1.DYDJ dydj,c1.YHXX yhxx,c1.CZFA czfa,c1.QTWT qtwt " +
                    " from  check_live_task_exec c  left join CHECK_LIVE_TASK_DETAILXS c1 on c.id = c1.exec_id " +
                    " left join XS_ZC_TASK c2 on c1.XS_TASK_ID = c2.id where c.id=? ";

        }


        return this.execSql(sql,id);
    }

    public List listAllCheckUser() {

     //   String sql = "select distinct c.USER_ID id,u.REALNAME realname from CHECK_LIVE_TASK_EXEC c left join RZTSYSUSER u on c.user_id = u.id where ROWNUM<=15";
       String sql = "select ID, REALNAME taskname from RZTSYSUSER ";
        return this.execSql(sql);
    }

    public List listAllCheckTaskExec() {
        String sql = "select distinct task_name from check_live_task_exec where ROWNUM<=15";
        return this.execSql(sql);
    }


    public CheckLiveTaskCycle findCycle(long id) {

        return this.repository.findCycle(id);
    }

    public void updateCycle(CheckLiveTaskCycle cycle, Long cycleId) {
       /* this.repository.updateCycle(cycle.getTaskName(),cycle.getTaskType(),
                                      cycle.getUserId(),cycle.getCreateTime(),
                                       cycle.getPlanStartTime(),cycle.getCheckCycle(),cycleId);*/
    }
}
