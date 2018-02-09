package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.Cmcoordinate;
import com.rzt.repository.CmcoordinateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CheckLiveTaskService extends CurdService<Cmcoordinate, CmcoordinateRepository> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    protected static Logger LOGGER = LoggerFactory.getLogger(CheckLiveTaskService.class);

    //看护已派发稽查任务列表
    public Page<Map<String, Object>> listKhCheckTaskPage(Pageable pageable, String userId, String tddwId, String currentUserId, String startTime, String endTime, String status, String queryAll, String loginType) {

        String sql = "select t.id,t.TASK_ID,t.CREATE_TIME,t.TASK_NAME,t.PLAN_START_TIME,t.PLAN_END_TIME,u.REALNAME,d.DEPTNAME, " +
                "  t.status , t.TASK_TYPE ,u.LOGINSTATUS,C.COMPANYNAME,U.PHONE" +
                " from CHECK_LIVE_TASK t " +
                "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                "  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = u.DEPTID " +
                "  LEFT JOIN RZTSYSCOMPANY C ON C.ID = U.COMPANYID where 1=1 ";

        List params = new ArrayList<>();
        //任务状态人查询`
        if (!StringUtils.isEmpty(status)) {
            sql += " AND t.status =" + status;
        }
        //人员在线状态查询
        if (!StringUtils.isEmpty(loginType)) {
            int login = Integer.parseInt(loginType);
            sql += " AND u.LOGINSTATUS =" + login;
        }
        //稽查人查询
        if (!StringUtils.isEmpty(userId)) {
            params.add(userId);
            sql += " AND u.id =?";
        }
        //时间段查询
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            params.add(endTime);
            params.add(startTime);
            sql += " and (to_date(?,'yyyy-MM-dd HH24:mi') > t.plan_start_time or to_date(?,'yyyy-MM-dd HH24:mi') < t.plan_end_time) ";
        }
        if (!StringUtils.isEmpty(currentUserId)) {
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid = map.get("DEPTID").toString();
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
        if (!"queryAll".equals(queryAll)) {
            //通道单位查询
            if (!StringUtils.isEmpty(tddwId)) {
                params.add(tddwId);
                sql += " and t.CHECK_TYPE=2 ";
                sql += " AND d.ID =?";
            } else {
                sql += " and t.CHECK_TYPE=2 ";
            }
        }
        return execSqlPage(pageable, sql, params.toArray());
    }


    public Page<Map<String, Object>> listXsCheckTaskPage(Pageable pageable, String startTime, String endTime, String userId, String tddwId, String currentUserId, String status) {
        String sql = "select t.id,t.TASK_ID,t.CREATE_TIME,t.TASK_NAME,u.REALNAME,td.PLAN_START_TIME,td.PLAN_END_TIME,d.DEPTNAME, t.TASK_TYPE,t.status from CHECK_LIVE_TASKXS t " +
                "  LEFT JOIN CHECK_LIVE_TASK_DETAILXS td ON t.id=td.task_id " +
                "  LEFT JOIN  rztsysuser u on u.id=t.USER_ID " +
                "  LEFT JOIN  RZTSYSDEPARTMENT d on d.ID = u.DEPTID where 1=1 ";

        List params = new ArrayList<>();
        //状态查询
        if (!StringUtils.isEmpty(status)) {
            sql += " and t.status=" + status;
        }
        //时间段查询
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            params.add(endTime);
            params.add(startTime);
            sql += " and to_date(?,'yyyy-MM-dd HH24:mi') > td.plan_start_time and to_date(?,'yyyy-MM-dd HH24:mi') < td.plan_end_time";
        }
        //稽查人查询
        if (!StringUtils.isEmpty(userId)) {
            params.add(userId);
            sql += " AND u.id =?";
        }
        if (!StringUtils.isEmpty(currentUserId)) {
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid = map.get("DEPTID").toString();
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

        Map<String, Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if (userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            try {
                jsonObject = this.execSqlSingleResult(sql, userId);
            } catch (Exception e) {
                LOGGER.error("currentUserId未获取到唯一数据!", e);
            }
            hashOperations.put("UserInformation", userId, jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(), Map.class);
        }
        return jsonObject;
    }

    public Page<Map<String, Object>> listHtCheckTaskPage(String currentUserId, Pageable pageable, String taskType, String status, String deptId) {
        Map<String, Object> stringObjectMap = userInfoFromRedis(currentUserId);
        Integer roletype = Integer.parseInt(stringObjectMap.get("ROLETYPE").toString());
        String deptid = stringObjectMap.get("DEPTID").toString();
        List list = new ArrayList();
        String s = "";
        //查询标识 0未抽查 1已抽查
        if (!StringUtils.isEmpty(status)) {
            list.add(status);
            s += " AND t.STATUS = ?" + list.size();
        }
        //1巡视，2看护，3看护稽查，4巡视稽查
        if (!StringUtils.isEmpty(taskType)) {
            list.add(taskType);
            s += " AND t.TASKTYPE =?" + list.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            list.add(deptId);
            s += " AND u.DEPTID = ?" + list.size();
        }
        if (roletype == 0) {
            String sql = " SELECT t.TASKNAME,t.CREATETIME,t.STATUS,u.REALNAME,u.PHONE,u.DEPT,u.CLASSNAME,t.TASKTYPE,t.TARGETSTATUS " +
                    "FROM TIMED_TASK t LEFT JOIN USERINFO u ON t.USER_ID = u.ID " +
                    "WHERE t.CREATETIME > sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60) AND THREEDAY = 1  " + s;
            return this.execSqlPage(pageable, sql, list.toArray());
        } else if (roletype == 1 || roletype == 2) {
            if (!StringUtils.isEmpty(deptid)) {
                list.add(deptid);
                s += " AND u.DEPTID = ?" + list.size();
            }
            String sql = " SELECT t.TASKNAME,t.CREATETIME,t.STATUS,u.REALNAME,u.PHONE,u.DEPT,u.CLASSNAME,t.TASKTYPE,t.TARGETSTATUS " +
                    "                    FROM TIMED_TASK t LEFT JOIN USERINFO u ON t.USER_ID = u.ID " +
                    "                    WHERE t.CREATETIME > (SELECT max(CREATETIME)-10/24/60 FROM TIMED_TASK) AND THREEDAY = 0 " + s;
            return this.execSqlPage(pageable, sql, list.toArray());
        }
        return null;
    }
}
