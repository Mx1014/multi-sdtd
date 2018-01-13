/**    
 * 文件名：MONITORCHECKEJService           
 * 版本信息：    
 * 日期：2018/01/08 11:06:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.Monitorcheckej;
import com.rzt.repository.Monitorcheckejrepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 类名称：MONITORCHECKEJService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2018/01/08 11:06:23
 * 修改人：张虎成
 * 修改时间：2018/01/08 11:06:23
 * 修改备注：
 * @version
 */
@Service
public class Monitorcheckejservice extends CurdService<Monitorcheckej,Monitorcheckejrepository> {

    @Autowired
    private Monitorcheckejrepository resp;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RedisService redisService;


    public void saveCheckEj(String[] messages) {

        resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[5]);
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

    //告警未处理 列表查询
    public Object XSGJW( Integer page, Integer size, String date,String userId,Integer warningType,String deptID,Integer type) {
        String deptId = getDeptID(userId);
        if(deptId==null){
            return "该用户状态为null";
        }
        if("-1".equals(deptId)){
            return "该用户无此权限";
        }
        Pageable pageable = new PageRequest(page, size, null);
        List<Object> list = new ArrayList<Object>();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  CREATE_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        }
        if (!StringUtils.isEmpty(warningType)) {
            list.add(warningType);
            s += " AND WARNING_TYPE = ?" + list.size();
        }
        if (!StringUtils.isEmpty(deptID)) {
            list.add(deptID);
            s += " AND DEPTID = ?" + list.size();
        }

        Page<Map<String, Object>> pageResult = null;
        if("0".equals(deptId)){
            String sql = "SELECT CREATE_TIME,TASK_NAME, TASK_ID,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_YJ where STATUS=0 and trunc(CREATE_TIME) = trunc(sysdate) and TASK_TYPE="+type;
            //查询所有
            String sql1 = "SELECT * FROM ( " +sql+" ) where 1=1 "+s;
            pageResult = execSqlPage(pageable, sql1,list.toArray());
        }else{
            String sql = "SELECT CREATE_TIME,TASK_NAME, TASK_ID,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_EJ where STATUS=0 and trunc(CREATE_TIME) = trunc(sysdate) and TASK_TYPE="+type;
            //查询该deptId下的
            list.add(deptId);
            s+=" AND DEPTID = ?" + list.size();
            String sql1 = "SELECT * FROM ( " +sql+" ) where 1=1 "+s;
            pageResult = execSqlPage(pageable, sql1,list.toArray());
        }
        Iterator<Map<String, Object>> iterator = pageResult.iterator();
        while (iterator.hasNext()){
            Map<String, Object> next = iterator.next();


            //获取到表中每个任务对应的人员的信息
            String userID =(String)next.get("USER_ID");
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            Object userInformation = hash.get("UserInformation", userID);
            if(userInformation==null){
                System.out.println(userInformation);
                continue;
            }
            JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
            if(jsonObject!=null){
                next.put("DEPT",jsonObject.get("DEPT"));
                next.put("COMPANYNAME",jsonObject.get("COMPANYNAME"));
                next.put("REALNAME",jsonObject.get("REALNAME"));
                next.put("PHONE",jsonObject.get("PHONE"));
            }
        }

        return pageResult;
    }

    //告警处理中列表查询
    public Object XSGJZ(Integer page, Integer size, String date, String userId, Integer warningType, String deptID, Integer type) {
        String deptId = getDeptID(userId);
        if(deptId==null){
            return "该用户状态为null";
        }
        if("-1".equals(deptId)){
            return "该用户无此权限";
        }
        Pageable pageable = new PageRequest(page, size, null);
        List<Object> list = new ArrayList<Object>();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  CREATE_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        }
        if (!StringUtils.isEmpty(warningType)) {
            list.add(warningType);
            s += " AND WARNING_TYPE = ?" + list.size();
        }
        if (!StringUtils.isEmpty(deptID)) {
            list.add(deptID);
            s += " AND DEPTID = ?" + list.size();
        }

        Page<Map<String, Object>> pageResult = null;
        if("0".equals(deptId)){
            String sql = "SELECT CREATE_TIME,TASK_NAME, TASK_ID,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_YJ where STATUS=1 and trunc(CREATE_TIME) = trunc(sysdate) and TASK_TYPE="+type;
            //查询所有
            String sql1 = "SELECT * FROM ( " +sql+" ) where 1=1 "+s;
            pageResult = execSqlPage(pageable, sql1,list.toArray());
        }else{
            String sql = "SELECT CREATE_TIME,TASK_NAME, TASK_ID,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_EJ where STATUS=1 and trunc(CREATE_TIME) = trunc(sysdate) and TASK_TYPE="+type;
            //查询该deptId下的
            list.add(deptId);
            s+=" AND DEPTID = ?" + list.size();
            String sql1 = "SELECT * FROM ( " +sql+" ) where 1=1 "+s;
            pageResult = execSqlPage(pageable, sql1,list.toArray());
        }
        Iterator<Map<String, Object>> iterator = pageResult.iterator();
        while (iterator.hasNext()){
            Map<String, Object> next = iterator.next();

            //获取到表中每个任务对应的人员的信息
            String userID =(String)next.get("USER_ID");
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            Object userInformation = hash.get("UserInformation", userID);
            if(userInformation==null){
                System.out.println(userInformation);
                continue;
            }

            JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
            if(jsonObject!=null){
                next.put("DEPT",jsonObject.get("DEPT"));
                next.put("COMPANYNAME",jsonObject.get("COMPANYNAME"));
                next.put("REALNAME",jsonObject.get("REALNAME"));
                next.put("PHONE",jsonObject.get("PHONE"));
            }
        }

        return pageResult;
    }

    //告警已处理查询列表
    public Object XSGJY(Integer page, Integer size, String date, String userId, Integer warningType, String deptID, Integer type) {
        String deptId = getDeptID(userId);
        if(deptId==null){
            return "该用户状态为null";
        }
        if("-1".equals(deptId)){
            return "该用户无此权限";
        }
        Pageable pageable = new PageRequest(page, size, null);
        List<Object> list = new ArrayList<Object>();
        String s = "";
        if (!StringUtils.isEmpty(date)) {
            list.add( date );
            s += " AND  CREATE_TIME <= to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss')  ";
        }
        if (!StringUtils.isEmpty(warningType)) {
            list.add(warningType);
            s += " AND WARNING_TYPE = ?" + list.size();
        }
        if (!StringUtils.isEmpty(deptID)) {
            list.add(deptID);
            s += " AND DEPTID = ?" + list.size();
        }

        Page<Map<String, Object>> pageResult = null;
        if("0".equals(deptId)){
            String sql = "SELECT CREATE_TIME,TASK_NAME, TASK_ID,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_YJ where STATUS=2 and trunc(CREATE_TIME) = trunc(sysdate) and TASK_TYPE="+type;
            //查询所有
            String sql1 = "SELECT * FROM ( " +sql+" ) where 1=1 "+s;
            pageResult = execSqlPage(pageable, sql1,list.toArray());
        }else{
            String sql = "SELECT CREATE_TIME,TASK_NAME, TASK_ID,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_EJ where STATUS=2 and trunc(CREATE_TIME) = trunc(sysdate) and TASK_TYPE="+type;
            //查询该deptId下的
            list.add(deptId);
            s+=" AND DEPTID = ?" + list.size();
            String sql1 = "SELECT * FROM ( " +sql+" ) where 1=1 "+s;
            pageResult = execSqlPage(pageable, sql1,list.toArray());
        }
        Iterator<Map<String, Object>> iterator = pageResult.iterator();
        while (iterator.hasNext()){
            Map<String, Object> next = iterator.next();


            //获取到表中每个任务对应的人员的信息
            String userID =(String)next.get("USER_ID");
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            Object userInformation = hash.get("UserInformation", userID);
            if(userInformation==null){
                System.out.println(userInformation);
                continue;
            }

            JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
            if(jsonObject!=null){
                next.put("DEPT",jsonObject.get("DEPT"));
                next.put("COMPANYNAME",jsonObject.get("COMPANYNAME"));
                next.put("REALNAME",jsonObject.get("REALNAME"));
                next.put("PHONE",jsonObject.get("PHONE"));
            }
        }

        return pageResult;
    }

    @Transactional
    public WebApiResponse GJCL(String userId, Long taskId, Integer taskType, Integer warningType, String checkInfo, String checkAppInfo) {
        String deptId = getDeptID(userId);
        if(deptId==null){
            return WebApiResponse.erro( "该用户状态为null");
        }
        //0表示是一级单位
        try {
            if ("0".equals(deptId)){
                return WebApiResponse.success(resp.updateYJ(taskId,taskType,warningType,checkInfo,checkAppInfo));
            }else{
                return WebApiResponse.success(resp.updateEJ(taskId,taskType,warningType,checkInfo,checkAppInfo));
            }
        }catch (Exception e){
            return WebApiResponse.erro("添加失败"+e.getMessage());
        }
    }

    @Transactional
    public WebApiResponse GJCLC(String userId, Long taskId, Integer taskType, Integer warningType, String checkInfo) {
        String deptId = getDeptID(userId);
        if(deptId==null){
            return WebApiResponse.erro( "该用户状态为null");
        }
        //0表示是一级单位
        try {
            if ("0".equals(deptId)){
                return WebApiResponse.success(resp.updateYJC(taskId,taskType,warningType,checkInfo));
            }else{
                return WebApiResponse.success(resp.updateEJC(taskId,taskType,warningType,checkInfo));
            }
        }catch (Exception e){
            return WebApiResponse.erro("添加失败"+e.getMessage());
        }
    }

    //未按时接任务添加
    public void addXSWAS(String[] messages) {

        String sql = "";
        if("4".equals(messages[3])){
            //巡视未按时开始任务
            sql = " SELECT id FROM XS_ZC_TASK WHERE ID =?1 AND PLAN_START_TIME<nvl(REAL_START_TIME,sysdate) ";
        }else if("10".equals(messages[3])){
            //看护未按时间看护任务
            sql = "SELECT ID FROM KH_TASK WHERE ID=?1 AND PLAN_START_TIME<nvl(REAL_START_TIME,sysdate)";
        }
        try {
            List<Map<String, Object>> maps= execSql(sql, messages[1]);
            //如果有数据证明未按时开始
            if(maps.size()>0){
                //将未按时开始设置40分钟进行定时
                String key = "ONE+"+messages[1]+"+"+messages[2]+"+"+messages[3]+"+"+messages[4]+"+"+messages[5]+"+"+messages[6];
                //放进二级表中
                resp.saveCheckEj(new SnowflakeIdWorker(0,0).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[6]);
                //设置时间向一级推送
                redisService.setex(key);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}