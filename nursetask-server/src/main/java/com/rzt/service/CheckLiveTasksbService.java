/**    
 * 文件名：CheckLiveTasksbService           
 * 版本信息：    
 * 日期：2018/01/21 08:27:36    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.rzt.entity.CheckLiveTasksb;
import com.rzt.repository.CheckLiveTasksbRepository;
import com.rzt.utils.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CheckLiveTasksbService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2018/01/21 08:27:36 
 * 修改人：张虎成    
 * 修改时间：2018/01/21 08:27:36    
 * 修改备注：    
 * @version        
 */
@Service
public class CheckLiveTasksbService extends CurdService<CheckLiveTasksb,CheckLiveTasksbRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTasksb.class);
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public Page<Map<String,Object>> pageCheckLiveTasksb(Pageable pageable, String lineId, String tddwId, String currentUserId) {

        String sql = "SELECT t.id,t.task_name,t.create_time,t.td_org_name,t.td_org_id,t.check_type,t.check_dept,h.TDYW_ORG,h.TDWX_ORG,h.yhjb,h.yhjb1,h.yhlb,h.yhfxsj from check_live_tasksb t " +
                "LEFT JOIN XS_SB_YH h on h.id=t.yhsb_id where t.status=0 ";

        List params = new ArrayList<>();
        //线路查询
        if (!StringUtils.isEmpty(lineId)) {
            params.add(lineId);
            sql += " AND h.LINE_ID =?";
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
            sql += " AND t.td_org_id =?";
        }
        sql += "  order by t.create_time ";
        return execSqlPage(pageable, sql, params.toArray());
    }


    public Page<Map<String,Object>> pageCheckLiveTasksbDone(Pageable pageable, String lineId, String tddwId, String currentUserId) {

        String sql = "SELECT t.task_name,t.create_time,t.td_org_name,t.td_org_id,t.check_type,t.check_dept,h.TDYW_ORG,h.TDWX_ORG,h.yhjb,h.yhjb1,h.yhlb,h.yhfxsj,h.YHMS,h.YHZRDW,h.YHZRDWLXR,h.YHZRDWDH from check_live_tasksb t " +
                "LEFT JOIN XS_SB_YH h on h.id=t.yhsb_id where t.status!=0 ";

        List params = new ArrayList<>();
        //线路查询
        if (!StringUtils.isEmpty(lineId)) {
            params.add(lineId);
            sql += " AND h.LINE_ID =?";
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
            sql += " AND t.td_org_id =?";
        }
        sql += "  order by t.create_time ";
        return execSqlPage(pageable, sql, params.toArray());
    }


    @Transactional
    public void sendCheckLiveTasksb(String id, String userId, String planStartTime, String planEndTime) {
        reposiotry.updateCheckLiveTasksb(Long.valueOf(id),userId, DateTool.parse(planStartTime),DateTool.parse(planEndTime));
    }

    @Transactional
    public void addCheckLiveTasksb(CheckLiveTasksb object) {
        object.setId(null);
        object.setCreateTime(new Date());
        object.setStatus(0);
        //object.setTaskName();
        reposiotry.save(object);
    }

    public Page<Map<String,Object>> appChecksbList(Pageable pageable, String userId, String taskType) {
        if("0,0".equals(taskType)){

        }
        String sql = "";

        List params = new ArrayList<>();

        //通道单位查询
        if (!StringUtils.isEmpty(userId)) {
            params.add(userId);
            sql += " AND t.td_org_id =?";
        }
        sql += "  order by t.create_time ";
        return execSqlPage(pageable, sql, params.toArray());
    }
    public void checkLiveTasksbComplete(Long id) {

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


}