/**    
 * 文件名：CHECKLIVETASKService           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.CheckLiveTask;
import com.rzt.repository.CheckLiveTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class CheckLiveTaskService extends CurdService<CheckLiveTask, CheckLiveTaskRepository> {

    @PersistenceContext
    private EntityManager entityManager;


     public  Page<Map<String, Object>> listAllCheckTask(String startTime,String endTime,String taskName,String status,Pageable pageable) {
        String result = " c.id id,c.task_name taskName,u.realname realName,u.classname className,c1.tdwh_org tdwhorg, " +
                        " c.create_time createTime,c.plan_start_time startTime,c.plan_end_time endTime,c1.task_status taskStatus ";
        StringBuilder sb = new StringBuilder();
        sb.append(" where 1=1 ");
        List params = new ArrayList<>();
         //时间段查询
         if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
             sb.append(" and c.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
             params.add(startTime);
             params.add(endTime);
         }
         //任务名查询
         if (!StringUtils.isEmpty(taskName)) {
             sb.append(" and c.task_name like ? ");
             params.add(taskName);
         }
        //任务状态查询
        if (!StringUtils.isEmpty(status)) {
            sb.append(" and c.task_status = ? ");
            //params.add(task.getTaskStatus());
        }

        //分页条件
        params.add(pageable.getPageNumber() * pageable.getPageSize());
        params.add((pageable.getPageNumber() + 1) * pageable.getPageSize());
        sb.append(" order by c.create_time desc ) a) b ");
        sb.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select " + result +
                     " from chenk_live_task_cycle c left join check_live_task_exec c1 on c.id = c1.cycle_id left join rztsysuser u on c.user_id = u.id " + sb.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
        return maps;

    }


    /**
     * 任务详情待做   具体页面需要显示什么字段还不知道  service
     */
    /*
    public List findCheckTaskById(String id) {


    }*/


    public  Page<Map<String, Object>> listpaifaCheckTask(String startTime,String endTime, String userId,String taskName,Pageable pageable) {
        String result = " c.id id,c.task_name taskName,r.companyname tdwxdw,k.yhms yhms,k.yhjb yhjb," +
                        " c.task_type taskType, c.check_type checkType,c.create_time createTime " ;
        StringBuilder sb = new StringBuilder();
        sb.append(" where 1=1 ");
        List params = new ArrayList<>();
        //时间段查询
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
            sb.append(" and c.create_time between to_date(?,'YYYY-MM-DD') and to_date(?,'YYYY-MM-DD') ");
            params.add(startTime);
            params.add(endTime);
        }
        //任务名查询
        if (!StringUtils.isEmpty(taskName)) {
            sb.append(" and c.task_name like ? ");
            params.add(taskName);
        }

        sb.append(" and c.status = 0 ");//未派发定死

        //用户人查询
        if (userId != null) {
            sb.append(" and c.user_id = ? ");
            params.add(userId);
        }

        //分页条件
        params.add(pageable.getPageNumber() * pageable.getPageSize());
        params.add((pageable.getPageNumber() + 1) * pageable.getPageSize());
        sb.append(" order by c.create_time desc ) a) b ");
        sb.append(" where b.rn>? and b.rn <=?");
        String sql = "select * from (select a.*,rownum rn from (select " + result +
                     " from Check_Live_Task c left join RZTSYSCOMPANY r on c.tdwx_orgid = r.id left join KH_YH_HISTORY k on c.yh_id = k.id " + sb.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
        return maps;

    }


}

