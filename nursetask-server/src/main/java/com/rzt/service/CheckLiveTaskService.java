/**    
 * 文件名：CHECKLIVETASKService           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.CheckLiveTask;
import com.rzt.entity.CheckLiveTaskDetail;
import com.rzt.entity.KhTask;
import com.rzt.repository.CheckLiveTaskRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**      
 * 类名称：CHECKLIVETASKService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/04 15:13:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/04 15:13:15    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class CheckLiveTaskService extends CurdService<CheckLiveTask, CheckLiveTaskRepository> {


    public List listAllCheckTask(KhTask task, Pageable pageable) {
        task = timeUtil(task);
        String result = " c.task_name as taskName,u.user_name as userName,u.class as class,k.tdtw_org as org,c.create_time as createTime,c.plan_start_time as startTime,c.plan_end_time as endTime,c.status as status";
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where 1=1 ");
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
        if (task.getStatus() != null){
            buffer.append(" and c.status = ? ");
            params.add(task.getStatus());
        }
        params.add(pageable.getPageNumber()*pageable.getPageSize());
        params.add((pageable.getPageNumber()+1)*pageable.getPageSize());
        buffer.append(" order by c.create_time desc ) a) b ");
        buffer.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from check_live_task_detail c left join user u on c.user_id = u.id left join check_live_task c1 on c.task_id=c.id left join kh_site k on k.id = c.task_id " + buffer.toString();
        return this.execSql(sql,params.toArray());
    }

    public List listAllCheckNotDo(KhTask task, Pageable pageable) {
        String result = " c.task_name as taskName,c.tdwx_org as org,y.yhms as yhms,y.yhjb as yhjb,c.task_type as type,c.create_time as createTime ";
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
        buffer.append(" order by c.create_time desc ) a) b ");
        buffer.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from check_live_task c left join kh_site k on k.id = c.task_id left join kh_yh_history y on y.id = k.yh_id " + buffer.toString();
        return this.execSql(sql,params.toArray());
    }

    

    public KhTask timeUtil(KhTask task){
        if (task.getPlanStartTime() == null||task.getPlanStartTime().length()==0){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_MONTH, -7);
            Date m = c.getTime();
            String mon = df.format(m);
            task.setPlanStartTime(mon);
            task.setPlanEndTime(df.format(new Date()));
        }
        return task;
    }

    public List<Map<String,Object>> AppListCheckTaskByUserId(CheckLiveTaskDetail task) {
        String result = " c.task_name as taskName,c.create_time as createTime";
        String sql = "select " + result + " from check_live_task_detail c where c.status = ? and c.check_type=? and user_id = ? order by c.create_time desc" ;
        return this.execSql(sql,task.getStatus(),task.getCheckType(),task.getUserId());
    }
}