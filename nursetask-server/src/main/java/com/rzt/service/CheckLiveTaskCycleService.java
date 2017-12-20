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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CheckLiveTaskCycleRepository repository;

    public List listCheckDoingById(String id) {
        String result = " c.task_name as taskName,u.user_name as userName,u.phone_num as phoneNum,c.check_type as checkType ";
//        List params = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String sql = "select "+result+" from check_live_task as c left join user u as u.id = c.user_id where c.id=?";
        return this.execSql(sql,id);
    }
    public List listAllCheckDoing(CheckLiveTaskCycle task, Pageable pageable) {
        String result = " c.task_name as taskName,c.check_type as type,u.user_name as useName,c.plan_start_time as startTime,c.plan_end_time as endTime,c.create_time as createTime,c.status";
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where c.status = 0");
        List params = new ArrayList<>();
        if (task.getPlanStartTime()!=null){
            buffer.append(" and c.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
            params.add(task.getPlanStartTime());
            params.add(task.getPlanEndTime());
        }
        if (task.getTaskName() != null){  //线路名查询
            task.setTaskName("%"+task.getTaskName()+"%");
            buffer.append(" and c.task_name like ? ");
            params.add(task.getTaskName());
        }
       /* if (task.getStatus() != null){
       少根据稽查人筛选的条件
            buffer.append(" and c.status = ? ");
            params.add(task.getStatus());
        }*/
        params.add(pageable.getPageNumber()*pageable.getPageSize());
        params.add((pageable.getPageNumber()+1)*pageable.getPageSize());
        buffer.append(" order by k.create_time desc ) a) b ");
        buffer.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from check_live_task_detail c left join user u on c.user_id = u.id " + buffer.toString();
        return this.execSql(sql,params.toArray());
    }



    @ApiOperation(value = "稽查维护维护",notes = "稽查维护的分页查询，条件搜索")
    public  Page<Map<String, Object>> listCheckTaskMain(String startTime,String endTime, String userId,String taskName,Pageable pageable) {
        String result =" c1.id id,c.task_name taskName,c.task_type taskType,u.realname realName,c.create_time createTime," +
                "c.plan_start_time startTime,c.check_cycle checkCycle ";
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
            params.add(taskName);
        }
        //用户人查询
        if(userId!=null){
            sb.append(" and c.user_id = ? ");
            params.add(userId);
        }
        //分页条件
        params.add(pageable.getPageNumber()*pageable.getPageSize());
        params.add((pageable.getPageNumber()+1)*pageable.getPageSize());
        sb.append(" order by c.create_time desc ) a) b ");
        sb.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+
                " from Chenk_Live_Task_Cycle c left join check_live_task_exec c1 on c.id=c1.cycle_id left join RZTSYSUSER u on  c.user_id = u.id " + sb.toString();
        /*
       Long count = this.repository.count();
        Map<String,Object> map = new HashMap<>();
        map.put("count",count);
        List<Map<String, Object>> list= this.execSql(sql,params.toArray());
        list.add(map);
        */
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
        return maps;

    }

    @ApiOperation(value = "稽查任务下子任务查询",notes = "稽查任务下子任务查询，条件搜索")
    public List listCheckTaskDetailById(String id) {
        String sql = " select c2.id id,c2.task_name taskName " +
                     "from  check_live_task_exec c  left join check_live_task_detail c1 on c.id = c1.exec_id left join check_live_task c2 on c1.task_id = c2.id where c.id=? ";
        return this.execSql(sql,id);
    }

    public List listAllCheckUser() {
        String sql = "select id,realname from rztsysuser where 1=1";
        return this.execSql(sql);
    }

    public List listAllCheckTaskExec() {
        String sql = "select distinct task_name from check_live_task_exec where 1=1";
        return this.execSql(sql);
    }
}
