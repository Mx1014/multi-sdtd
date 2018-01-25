/**    
 * 文件名：CHECKLIVETASKService           
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.CheckLiveTaskDetailXs;
import com.rzt.entity.CheckLiveTaskXs;
import com.rzt.repository.CheckLiveTaskDetailXsRepository;
import com.rzt.repository.CheckLiveTaskXsRepository;
import com.rzt.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
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
public class CheckLiveTaskXsService extends CurdService<CheckLiveTaskXs, CheckLiveTaskXsRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTaskXsService.class);

    @Autowired
    private CheckLiveTaskDetailXsRepository checkLiveTaskDetailXsRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //巡视稽查列表查询展示
    public Page<Map<String,Object>> listXsCheckPage(Pageable pageable, String startTime, String endTime, String lineId, String tddwId,String currentUserId) {
        String result = " c.ID id,c.task_name taskName,d.DEPTNAME tddw ," +
                " c.plan_start_time startTime,c.plan_end_time endTime,u.realname realname" ;
        String sql = "select " + result +
                " from XS_ZC_TASK c LEFT JOIN  XS_ZC_CYCLE c1 on c.XS_ZC_CYCLE_ID = c1.ID" +
                " LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = c.TD_ORG" +
                "   left join rztsysuser u on u.id=c.CM_USER_ID  where c.jc_status=0 and sysdate+1/24<c.plan_end_time ";

        List params = new ArrayList<>();
        //时间段查询
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
            params.add(endTime);
            params.add(startTime);
            sql += " and to_date(?,'yyyy-MM-dd HH24:mi') > c.plan_start_time and to_date(?,'yyyy-MM-dd HH24:mi') < c.plan_end_time";
        }
        //线路查询
        if (!StringUtils.isEmpty(lineId)) {
            params.add(lineId);
            sql += " AND c1.LINE_ID =?";
        }
        if(!StringUtils.isEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    tddwId = deptid;
                    break;
                case 2:
                    tddwId = deptid;
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }
        //通道单位查询
        if (!StringUtils.isEmpty(tddwId)) {
            params.add(tddwId);
            sql += " AND c.TD_ORG =?";
        }

        return execSqlPage(pageable, sql, params.toArray());
    }

    public Map<String, Object> xsTaskDetail(String taskId) throws Exception {
        String sql = "select c.ID id,c.task_name taskName,d.DEPTNAME tddw ," +
                " c.plan_start_time startTime,c.plan_end_time endTime,u.realname realname" +
                " from XS_ZC_TASK c LEFT JOIN  XS_ZC_CYCLE c1 on c.XS_ZC_CYCLE_ID = c1.ID" +
                " LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = c.TD_ORG" +
                "   left join rztsysuser u on u.id=c.CM_USER_ID  where c.id = ?1";
        Map<String, Object> map = execSqlSingleResult(sql, taskId);
        return map;
    }

    @Transactional
    public void paifaXsCheckTask(CheckLiveTaskXs task , String planStartTime, String planEndTime, String username) throws Exception {

        Map<String, Object> map = execSqlSingleResult("SELECT ID,TD_ORG,WX_ORG FROM XS_ZC_TASK WHERE ID = ?1", task.getTaskId());
        task.setId(0L);
        task.setTdwhOrg(String.valueOf(map.get("TD_ORG")).replace("null",""));
        task.setTdwxOrgid(String.valueOf(map.get("WX_ORG")).replace("null",""));
        task.setCreateTime(new Date());
        task.setStatus(0);//任务派发状态  0未接单 1进行中 2已完成 3超期
        task.setCheckType(1); //0 看护  1巡视
        //task.setTaskType(0);//（0 正常 1保电 2 特殊）
        task.setTaskName(username+ planStartTime.substring(0,10)+"稽查任务");
        CheckLiveTaskXs save = reposiotry.save(task);

        reposiotry.updateTaskStatus(1,save.getTaskId());
        CheckLiveTaskDetailXs taskDetailXs = new CheckLiveTaskDetailXs();
        taskDetailXs.setId(null);
        taskDetailXs.setCreateTime(new Date());
        taskDetailXs.setPlanStartTime(DateUtil.parseDate(planStartTime));
        taskDetailXs.setPlanEndTime(DateUtil.parseDate(planEndTime));
        taskDetailXs.setStatus(0);// 0未开始 1进行中 2已完成 3已超期
        taskDetailXs.setXsTaskType(save.getTaskType());//（（0 正常 1保电 2 特殊）
        taskDetailXs.setXsTaskId(save.getTaskId());
        taskDetailXs.setTaskId(save.getId());
        checkLiveTaskDetailXsRepository.save(taskDetailXs);

    }


    public Page<Map<String,Object>> listXsCheckTaskPage(Pageable pageable, String startTime, String endTime, String userId, String tddwId,String currentUserId,String status) {
        String sql = "select t.id,t.TASK_ID,t.CREATE_TIME,t.TASK_NAME,u.REALNAME,td.PLAN_START_TIME,td.PLAN_END_TIME,d.DEPTNAME, t.TASK_TYPE,t.status from CHECK_LIVE_TASKXS t " +
                "  LEFT JOIN CHECK_LIVE_TASK_DETAILXS td ON t.id=td.task_id " +
                "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                "  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = u.DEPTID where 1=1 ";

        List params = new ArrayList<>();
        //状态查询
        if (!StringUtils.isEmpty(status)){
            sql += " and t.status="+status;
        }
        //时间段查询
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)){
            params.add(endTime);
            params.add(startTime);
            sql += " and to_date(?,'yyyy-MM-dd HH24:mi') > td.plan_start_time and to_date(?,'yyyy-MM-dd HH24:mi') < td.plan_end_time";
        }
        //稽查人查询
        if (!StringUtils.isEmpty(userId)) {
            params.add(userId);
            sql += " AND u.id =?";
        }
        if(!StringUtils.isEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    tddwId = deptid;
                    break;
                case 2:
                    tddwId = deptid;
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }
        //通道单位查询
        if (!StringUtils.isEmpty(tddwId)) {
            params.add(tddwId);
            sql += " AND d.ID =?";
        }

        return execSqlPage(pageable, sql, params.toArray());
    }

    public Map<String, Object> userInfoFromRedis(String userId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        Map<String,Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if(userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            try {
                jsonObject = this.execSqlSingleResult(sql, userId);
            } catch (Exception e) {
                LOGGER.error("currentUserId未获取到唯一数据!",e);
            }
            hashOperations.put("UserInformation",userId,jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(),Map.class);
        }
        return jsonObject;
    }

    public void updateXsCheckUser(Long id, String userId, String userName) {
        reposiotry.updateXsCheckUser(id,userId,userName);
    }
}
