/**
 * 文件名：MONITORCHECKYJService
 * 版本信息：
 * 日期：2018/01/08 11:06:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.Monitorcheckyj;
import com.rzt.repository.Monitorcheckyjrepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 类名称：MONITORCHECKYJService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2018/01/08 11:06:23
 * 修改人：张虎成
 * 修改时间：2018/01/08 11:06:23
 * 修改备注：
 */
@Service
public class Monitorcheckyjservice extends CurdService<Monitorcheckyj, Monitorcheckyjrepository> {

    @Autowired
    private Monitorcheckyjrepository repo;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    public void saveCheckYj(String[] messages) {
        //保存到一级
        repo.saveCheckYj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[6]);
    }

    /**
     * 查询任务详情
     */
    public Object taskInfo(Long taskId, Integer warningType,Integer taskType) {
        //巡视
        String sql = "";
        if(taskType==1){
            sql = "SELECT ej.CREATE_TIME,ej.TASK_NAME,ej.WARNING_TYPE,xs.PLAN_START_TIME,xs.PLAN_END_TIME,xs.REAL_START_TIME,xs.PLAN_END_TIME,xs.CM_USER_ID AS user_id,xs.STAUTS  " +
                    " FROM MONITOR_CHECK_EJ ej LEFT JOIN XS_ZC_TASK xs ON ej.TASK_ID = xs.ID WHERE ej.TASK_ID=?1";
        }else if(taskType==2){
            sql = "SELECT ej.CREATE_TIME,ej.TASK_NAME,kh.REAL_START_TIME,kh.PLAN_END_TIME,kh.PLAN_START_TIME,kh.REAL_END_TIME,kh.USER_ID,kh.STATUS  " +
                    "FROM MONITOR_CHECK_EJ ej LEFT JOIN KH_TASK kh ON ej.TASK_ID = kh.ID";
        }
        try {
            Map<String, Object> map = execSqlSingleResult(sql, taskId);
            return map;
        } catch (Exception e) {
            e.getMessage();
            return "error";
        }

    }

    /**
     * 查看人员详情
     */
    public Object userInfo(String userId,Integer warningType) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        if(warningType==7){
            String sql = "SELECT count(kh_time.id) times," +
                    "     sum(ROUND(TO_NUMBER(nvl(kh_time.END_TIME,sysdate) - kh_time.START_TIME) * 24 * 60 * 60)) timeLong " +
                    "     FROM WARNING_OFF_POST_USER_TIME kh_time " +
                    "     WHERE kh_time.FK_USER_ID = ?1";
            List<Map<String, Object>> maps = execSql(sql, userId);
            if(maps.size()>0){
                Map<String, Object> map = maps.get(0);
                Long timelong = (Long) map.get("TIMELONG");
                Long h=timelong/3600;
                Long m=(timelong%3600)/60;
                Long s=(timelong%3600)%60;
                jsonObject.put("TIMES",map.get("TIMES").toString());
                jsonObject.put("TIMELONG",h+"小时"+m+"分"+s+"秒");
            }
        }
        return jsonObject;
    }

    /**
     * 柱状图查询
     * @param type
     * @return
     */
    public Object sumInfo(String userId,Integer type) {
        String deptID = getDeptID(userId);
        if("-1".equals(deptID)){
            return "该用户无此权限";
        }
        if("0".equals(deptID)){
            if(type==1){
                //查询未出理
                String sql1 = "SELECT count(1),DEPTID FROM MONITOR_CHECK_EJ WHERE TASK_TYPE=1 AND trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =0 GROUP BY DEPTID ";
                //查询处理中
                String sql2 = "SELECT count(1),DEPTID FROM MONITOR_CHECK_EJ WHERE TASK_TYPE=1 AND trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =1 GROUP BY DEPTID  ";
                //查询已处理
                String sql3 = "SELECT count(1),DEPTID FROM MONITOR_CHECK_EJ WHERE TASK_TYPE=1 AND trunc(CREATE_TIME)=trunc(sysdate) AND STATUS =2 GROUP BY DEPTID  ";
                List<Map<String, Object>> maps = execSql(sql1);
                List<Map<String, Object>> maps1 = execSql(sql2);
                List<Map<String, Object>> maps2 = execSql(sql3);

            }

        }else{

        }
        return null;
    }

    //判断权限，获取当前登录用户的deptId，如果是全部查询则返回0
    public String getDeptID(String userId){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        if(userInformation==null){
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        String roletype = (String) jsonObject.get("ROLETYPE");
        if(roletype==null){
            return null;
        }
        if("0".equals(roletype)){
            //0是查询全部
            return "0";
        }else if("1".equals(roletype)||"2".equals(roletype)){
            return (String) jsonObject.get("DEPTID");
        }else{
            return "-1";
        }
    }
}