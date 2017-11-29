/**    
 * 文件名：KhTaskService           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.repository.KhTaskRepository;
import com.rzt.entity.KhTask;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rzt.service.CurdService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.*;

/**      
 * 类名称：KhTaskService    
 * 类描述：InnoDB free: 536576 kB    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class KhTaskService extends CurdService<KhTask,KhTaskRepository> {

    @PersistenceContext
    EntityManager entityManager;
    
    public List findAllKhTask(KhTask task, Pageable pageable) {
        //默认获取一个月的看护任务记录
        if (task.getPlanStartTime() == null||task.getPlanStartTime().length()==0){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, -1);
            Date m = c.getTime();
            String mon = df.format(m);
            task.setPlanStartTime(mon);
            task.setPlanEndTime(df.format(new Date()));
        }
        String result = " k.task_name as taskName,k.tdyw_org as tdywOrg,k.CREATE_TIME as createTime,k.plan_start_time as startTime,k.plan_end_time as endTime,k.status as status";
        //,u.user_name as userName,u.class as class
        StringBuffer buffer = new StringBuffer();
        buffer.append(" where k.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
        if (task.getTaskName() != null){
            task.setTaskName("%"+task.getTaskName()+"%");
            buffer.append(" and k.task_name like ? ");
        }
         if (task.getStatus() != null){
            task.setStatus("%"+task.getStatus()+"%");
            buffer.append(" and k.status like ? ");
         }
         //此处加分页 人员表换成真实表
         buffer.append(" order by k.create_time desc ) a) b ");
         buffer.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select "+result+" from kh_task k " +
               /* " left join 人员表 u on u.id = k.user_id "+*/buffer.toString();
       List params = new ArrayList<>();
       params.add(task.getPlanStartTime());
       params.add(task.getPlanEndTime());
        if (task.getTaskName() != null) {
            params.add(task.getTaskName());
        }
        if (task.getStatus() != null){
            params.add(task.getStatus());
        }
        params.add((pageable.getPageNumber()-1)*pageable.getPageSize());
        params.add(pageable.getPageNumber()*pageable.getPageSize());
        List<Map<String, Object>> maps = execSql(sql, params.toArray());
        return maps;
    }

    public List findAllTaskDoing(KhTask task, Pageable pageable) {
        return new ArrayList<>();
    }

    public List findAllTaskNotDo(KhTask task, Pageable pageable) {
        return new ArrayList<>();
    }
}