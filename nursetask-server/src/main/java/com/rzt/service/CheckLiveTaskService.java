/**    
 * 文件名：CHECKLIVETASKService           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.CheckLiveTask;
import com.rzt.repository.CheckLiveTaskRepository;
import com.rzt.util.WebApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


     public  Page<Map<String, Object>> listAllCheckTask(String startTime,String endTime,String taskName,Integer status,String userId,Pageable pageable) {
        String result = " c1.id id,c.id cycleid,c1.task_name taskName,u.realname realName,g.DEPTNAME className,c1.tdwh_org tdwhorg, " +
                        " c.create_time createTime,c.plan_start_time startTime,c.plan_end_time endTime,c1.task_status taskStatus ";
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
        //任务状态查询
        if (status!=null) {
            sb.append(" and c1.task_status = ? ");
            params.add(status);
        }

         //用户人查询
         if(!StringUtils.isEmpty(userId)){
             sb.append(" and c.user_id = ? ");
             params.add(userId);
         }

        sb.append(" order by c.create_time desc ");

        String sql = "select " + result + " from chenk_live_task_cycle c left join" +
                     " check_live_task_exec c1 on c.id = c1.cycle_id left join rztsysuser u " +
                     "on c.user_id = u.id left join RZTSYSDEPARTMENT  g on u.CLASSNAME = g.ID" + sb.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());
        return maps;

    }


    public  Page<Map<String, Object>> listpaifaCheckTask(String startTime,String endTime, String userId,String taskName,Pageable pageable) {
        String result = " c.id id,c.task_name taskName,c.TDWH_ORG tddw,r.companyname tdwxdw,k.yhms yhms,k.yhjb yhjb," +
                        " c.task_type taskType, c.check_type checkType,d.DEPTNAME sdgs,c.create_time createTime " ;
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

        sb.append(" and c.status = 0 ");//未派发定死

        //用户人查询
        if (!StringUtils.isEmpty(userId)) {
            sb.append(" and c.user_id = ? ");
            params.add(userId);
        }

        sb.append(" order by c.create_time desc  ");
        String sql = "select " + result + " from Check_Live_Task c left join " +
                     "RZTSYSCOMPANY r on c.tdwx_orgid = r.id LEFT JOIN  RZTSYSDEPARTMENT d on c.DEPT_ID=d.ID " +
                     "left join KH_YH_HISTORY k " +
                     "on c.yh_id = k.id " + sb.toString();
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql, params.toArray());

        return maps;

    }


    public List getCheckTaskById(String id) {
        Long ids = Long.parseLong(id);
        String sql = "select c.id id,c.task_name taskName,c.CREATE_TIME createtime,c2.CHECK_CYCLE checkcycle, c.TDWH_ORG tddw," +
                "c1.yhjb yhjb,u.REALNAME realname from check_live_task c left join kh_yh_history c1 on c.yh_id = c1.id " +
                " LEFT JOIN  RZTSYSUSER u on c.USER_ID = u.ID LEFT JOIN  CHENK_LIVE_TASK_CYCLE c2 on c.CYCLE_ID = c2.ID where c.id=?";
        return this.execSql(sql,ids);
    }

    public List getCheckTaskName() {
        String sql = " select task_name   from CHECK_LIVE_TASK  ";
        return this.execSql(sql);
    }

    public CheckLiveTask findLiveTask(String value) {
        long id = Long.parseLong(value);
        return this.reposiotry.findLiveTask(id);
    }

    public void updateLiveTask(CheckLiveTask tt, String value) {

        long id = Long.parseLong(value);
        this.reposiotry.updateLiveTask(tt.getStatus(),tt.getCycleId(),id);
    }

    public WebApiResponse deleteById(String id) {
        try {
            String[] split = id.split(",");
            if (split.length>0){
                for (int i = 0;i < split.length;i++) {
                    this.reposiotry.deleteById(Long.parseLong(split[i]));
                }
            }else{
                this.reposiotry.deleteById(Long.parseLong(id));
            }
            return WebApiResponse.success("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return WebApiResponse.erro("删除失败");
        }
    }
}
