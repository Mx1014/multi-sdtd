/**
 * 文件名：MONITORCHECKEJService
 * 版本信息：
 * 日期：2018/01/08 11:06:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.Monitorcheckej;
import com.rzt.repository.AlarmOfflineRepository;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 类名称：MONITORCHECKEJService
 * 类描述：${table.comment}
 * 创建人：张虎成
 * 创建时间：2018/01/08 11:06:23
 * 修改人：张虎成
 * 修改时间：2018/01/08 11:06:23
 * 修改备注：
 */
@Service
public class Monitorcheckejservice extends CurdService<Monitorcheckej, Monitorcheckejrepository> {

    @Autowired
    private Monitorcheckejrepository resp;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    JedisPool pool;

    @Autowired
    private AlarmOfflineRepository offlineRepository;

    @Autowired
    private AlarmOfflineRepository offline;

    //获取通道公司ID
    public Object getDeptId(String userId) {
        String deptID = getDeptID(userId);
        String sql = "";
        if ("0".equals(deptID)) {
            sql = "SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT";
        } else if (!"-1".equals(deptID) && !"0".equals(deptID)) {
            sql = "SELECT DEPTNAME,ID FROM RZTSYSDEPARTMENT WHERE DEPTSORT IS NOT NULL AND  ID='" + deptID + "'" + " ORDER BY DEPTSORT ";
        }
        List<Map<String, Object>> maps = execSql(sql);
        return maps;
    }


    public void saveCheckEj(String[] messages) {
       resp.saveCheckEj(new SnowflakeIdWorker(0, 0).nextId(), Long.valueOf(messages[1]), Integer.valueOf(messages[2]), Integer.valueOf(messages[3]), messages[4], messages[5], messages[6]);

    }
    //添加未按时上线原因
    public boolean saveCheckEjWdw(String[] messages) {
        boolean flag = false;
        //巡视如果任务已经完成则不计
        if(Integer.parseInt(messages[3])==2){
            String sql=" SELECT STAUTS FROM XS_ZC_TASK WHERE ID=?1 ";
            String sql2 = "SELECT LOGINSTATUS FROM RZTSYSUSER WHERE ID=?1 AND USERDELETE=1";
            try {
                Map<String, Object> map = execSqlSingleResult(sql, Long.valueOf(messages[1]));
                Map<String, Object> map2 = execSqlSingleResult(sql2, messages[4]);
                //如果任务状态不为2，才将数据插入进去
                if(Integer.parseInt(map.get("STAUTS").toString())==0 && Integer.parseInt(map2.get("LOGINSTATUS").toString())==0){
                    resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[6],messages[7]);
                    lixianRedis( messages[4]);//将离线的userId放入redis中
                    flag = true;
                }
            } catch (Exception e) {
            }
        }else if(Integer.parseInt(messages[3])==8){
            String sql = "SELECT LOGINSTATUS FROM RZTSYSUSER WHERE ID=?1 AND USERDELETE=1";
            Map<String, Object> maps = null;
            try {
                maps = execSqlSingleResult(sql, messages[4]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(maps!=null && maps.size()>0 && Integer.parseInt(maps.get("LOGINSTATUS").toString())==0){
                //看护没有提前完成的
                resp.saveCheckEjWdw(SnowflakeIdWorker.getInstance(20,14).nextId(),Long.valueOf(messages[1]),Integer.valueOf(messages[2]),Integer.valueOf(messages[3]),messages[4],messages[5],messages[6],messages[7]);
                lixianRedis( messages[4]);//将离线的userId放入redis中
                flag = true;
            }
        }
        return flag;
    }
    private void lixianRedis(String userId){
        String sql = "SELECT * FROM ALARM_OFFLINE WHERE USER_ID=?1 AND trunc(CREATE_TIME)=trunc(sysdate)";
        List<Map<String, Object>> maps = execSql(sql, userId);
        Date date = new Date();
        Long timeLong = date.getTime()+5400000l;
        if(maps.size()==0){//如果ALARM_OFFLINE表中没有数据，则进行添加
            //向ALARM_OFFLINE中添加数据
            offline.addoffLine(SnowflakeIdWorker.getInstance(10,10).nextId(),userId,timeLong,date);
        }else{ //如果已经存在，则只更细时长和次数
            Integer frequency = Integer.parseInt(maps.get(0).get("OFFLINE_FREQUENCY").toString())+1;
            timeLong = Long.parseLong(maps.get(0).get("OFFLINE_TIME_LONG").toString())+timeLong;
            offline.updateoffLine(Long.parseLong(maps.get(0).get("ID").toString()),frequency,timeLong,date);
        }
        Jedis jedis=null;
        try {
            jedis = pool.getResource();
            jedis.select(5);
            jedis.hset("lixian",userId,String.valueOf(date.getTime()));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //判断权限，获取当前登录用户的deptId，如果是全部查询则返回0
    public String getDeptID(String userId) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        if (userInformation == null) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        String roletype = (String) jsonObject.get("ROLETYPE");
        if (roletype == null) {
            return null;
        }
        if ("0".equals(roletype)) {
            //0是查询全部
            return "0";
        } else if ("1".equals(roletype) || "2".equals(roletype)) {
            return (String) jsonObject.get("DEPTID");
        } else {
            return "-1";
        }
    }

    //告警未处理 列表查询
    public Object XSGJW(Integer page, Integer size, String startDate, String userId, Integer warningType, String deptID, Integer type, String endDate,String userName) {
        String deptId = getDeptID(userId);
        if (deptId == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(deptId)) {
            return "该用户无此权限";
        }
        Pageable pageable = new PageRequest(page, size);
        List<Object> list = new ArrayList<Object>();
        String s = "";
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            list.add(startDate);
            s += " AND  CREATE_TIME BETWEEN to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
            list.add(endDate);
            s += "  AND to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
        }else{
            s+=" and trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(warningType)) {
            list.add(warningType);
            s += " AND WARNING_TYPE = ?" + list.size();
        }
        if (!StringUtils.isEmpty(deptID)) {
            list.add(deptID);
            s += " AND DEPTID = ?" + list.size();
        }
        if(!StringUtils.isEmpty(userName)){
            /*String sql=" SELECT ID FROM RZTSYSUSER WHERE REALNAME ='"+userName.trim()+"'";
            List<Map<String, Object>> maps = execSql(sql, userName);
            String ids = (String) maps.get(0).get("ID");
            if(!StringUtils.isEmpty(ids)){
                s+= " AND USER_ID = '"+ids+"' ";
            }*/
            s+= " AND REALNAME like '%"+userName.trim()+"%' ";
        }
        Page<Map<String, Object>> pageResult = null;

        if ("0".equals(deptId)) {
            String sql ="";
            //巡视的sql
            if(type==1){
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,xs.PLAN_START_TIME,u.REALNAME FROM MONITOR_CHECK_YJ j " +
                        " LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID " +
                        "  LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1  AND xs.IS_DELETE=0  and j.TASK_TYPE=" + type;
            }else if(type==2){
                //看护的sql
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,kh.PLAN_START_TIME,u.REALNAME FROM MONITOR_CHECK_YJ j " +
                        "  LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID " +
                        "  LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1 and j.TASK_TYPE=" + type;

            }else if(type==3){
                //现场稽查
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,cl.PLAN_START_TIME,u.REALNAME FROM MONITOR_CHECK_YJ j " +
                        " LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID " +
                        "  LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1 and j.TASK_TYPE=" + type;
            }
            //查询所有
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = execSqlPage(pageable, sql1, list.toArray());
        } else {
            //String sql = "SELECT   TASK_ID, CREATE_TIME,TASK_NAME,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_EJ where STATUS=0 and TASK_TYPE=" + type;
            //巡视的sql
            String sql="";
            if(type==1){
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,xs.PLAN_START_TIME,u.REALNAME FROM MONITOR_CHECK_EJ j " +
                        " LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID " +
                        "  LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID where j.STATUS=0 AND j.USER_ID is NOT NULL AND u.USERDELETE=1 AND xs.IS_DELETE=0  and j.TASK_TYPE=" + type;
            }else if(type==2){
                //看护的sql
                sql = "SELECT   j.ID,j.APP_RETURN_INFO, j.TASK_ID, j.CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,kh.PLAN_START_TIME,u.REALNAME FROM MONITOR_CHECK_EJ j " +
                        " LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID " +
                        "  LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=0  AND j.USER_ID is NOT NULL AND u.USERDELETE=1 and j.TASK_TYPE=" + type;

            }else if(type==3){
                //现场稽查
                sql = "SELECT   j.ID,j.APP_RETURN_INFO, j.TASK_ID, j.CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,cl.PLAN_START_TIME,u.REALNAME FROM MONITOR_CHECK_EJ j " +
                        " LEFT JOIN RZTSYSUSER u ON j.USER_ID=u.ID " +
                        "  LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=0  AND j.USER_ID is NOT NULL AND u.USERDELETE=1 and j.TASK_TYPE=" + type;
            }
            //查询该deptId下的
            list.add(deptId);
            s += " AND DEPTID = ?" + list.size();
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = execSqlPage(pageable, sql1, list.toArray());
        }
        Iterator<Map<String, Object>> iterator = pageResult.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();

            //获取到表中每个任务对应的人员的信息
            String userID = (String) next.get("USER_ID");
            if("null".equals(userID) || userID==null){
                iterator.remove();
                continue;
            }
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            Object userInformation = hash.get("UserInformation", userID);
            if (userInformation == null) {
                continue;
            }

            JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
            if (jsonObject != null) {
                next.put("DEPT", jsonObject.get("DEPT"));
                next.put("COMPANYNAME", jsonObject.get("COMPANYNAME"));
                next.put("REALNAME", jsonObject.get("REALNAME"));
                next.put("PHONE", jsonObject.get("PHONE"));
                if("0".equals(deptId)){
                    next.put("USER_TYPE",1);
                }else{
                    next.put("USER_TYPE",2);
                }
            }
            /*Integer warning = Integer.parseInt(next.get("WARNING_TYPE").toString());
            if(warning==3){
                String sql = "";
            }*/
        }

        return pageResult;
    }

    //告警处理中列表查询
    public Object XSGJZ(Integer page, Integer size, String startDate, String userId, Integer warningType, String deptID, Integer type, String endDate,String userName) {
        String deptId = getDeptID(userId);
        if (deptId == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(deptId)) {
            return "该用户无此权限";
        }
        Pageable pageable = new PageRequest(page, size, null);
        List list = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            list.add(startDate);
            s += " AND  CREATE_TIME BETWEEN to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
            list.add(endDate);
            s += "  AND to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
        }else{
            s+=" and trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(warningType)) {
            list.add(warningType);
            s += " AND WARNING_TYPE = ?" + list.size();
        }
        if (!StringUtils.isEmpty(deptID)) {
            list.add(deptID);
            s += " AND DEPTID = ?" + list.size();
        }
        if(!StringUtils.isEmpty(userName)){
            String sql=" SELECT ID FROM RZTSYSUSER WHERE REALNAME ='"+userName+"'";
            List<Map<String, Object>> maps = execSql(sql, userName);
            String ids = (String) maps.get(0).get("ID");
            if(!StringUtils.isEmpty(ids)){
                s+= " AND USER_ID = '"+ids+"' ";
            }
        }
        Page<Map<String, Object>> pageResult=null;

        if ("0".equals(deptId)) {
           // String sql = "SELECT   TASK_ID, CREATE_TIME_Z as CREATE_TIME,TASK_NAME,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_YJ where STATUS=1 and TASK_TYPE=" + type;
            String sql="";
            //巡视的sql
            if(type==1){
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_Z AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,xs.PLAN_START_TIME FROM MONITOR_CHECK_YJ j " +
                        "  LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID where j.STATUS=1 and j.TASK_TYPE=" + type;
            }else if(type==2){
                //看护的sql
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_Z AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,kh.PLAN_START_TIME FROM MONITOR_CHECK_YJ j " +
                        "  LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=1 and j.TASK_TYPE=" + type;

            }else if(type==3){
                //现场稽查
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_Z AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,cl.PLAN_START_TIME FROM MONITOR_CHECK_YJ j " +
                        "  LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=1 and j.TASK_TYPE=" + type;
            }
            //查询所有
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            /*Page<Map<String, Object>> maps = this.execSqlPage(pageable, sql1, list.toArray());
            redisQuery(maps);
            return maps;*/
            pageResult = this.execSqlPage(pageable, sql1, list.toArray());
        } else {
            //String sql = "SELECT   TASK_ID, CREATE_TIME_Z  as CREATE_TIME,TASK_NAME,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE FROM MONITOR_CHECK_EJ where STATUS=1 and TASK_TYPE=" + type;
            String sql="";
            //巡视的sql
            if(type==1){
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_Z AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,xs.PLAN_START_TIME FROM MONITOR_CHECK_EJ j " +
                        "  LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID where j.STATUS=1 and j.TASK_TYPE=" + type;
            }else if(type==2){
                //看护的sql
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_Z AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,kh.PLAN_START_TIME FROM MONITOR_CHECK_EJ j " +
                        "  LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=1 and j.TASK_TYPE=" + type;

            }else if(type==3){
                //现场稽查
                sql = "SELECT  j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_Z AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,cl.PLAN_START_TIME FROM MONITOR_CHECK_EJ j " +
                        "  LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=1 and j.TASK_TYPE=" + type;
            }
            //查询该deptId下的
            list.add(deptId);
            s += " AND DEPTID = ?" + list.size();
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = this.execSqlPage(pageable, sql1, list.toArray());
        }
        Iterator<Map<String, Object>> iterator = pageResult.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();

            //获取到表中每个任务对应的人员的信息
            String userID = (String) next.get("USER_ID");
            if(userID==null){
                iterator.remove();
                continue;
            }
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            Object userInformation = hash.get("UserInformation", userID);
            if (userInformation == null) {
                // System.out.println(userInformation);
                continue;
            }

            JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
            if (jsonObject != null) {
                next.put("DEPT", jsonObject.get("DEPT"));
                next.put("COMPANYNAME", jsonObject.get("COMPANYNAME"));
                next.put("REALNAME", jsonObject.get("REALNAME"));
                next.put("PHONE", jsonObject.get("PHONE"));
            }
        }
        return pageResult;
    }


    //告警已处理查询列表
    public Object XSGJY(Integer page, Integer size, String startDate, String userId, Integer warningType, String deptID, Integer type, String endDate,String userName) {
        String deptId = getDeptID(userId);
        if (deptId == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(deptId)) {
            return "该用户无此权限";
        }
        Pageable pageable = new PageRequest(page, size, null);
        List<Object> list = new ArrayList<Object>();
        String s = "";
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            list.add(startDate);
            s += " AND  CREATE_TIME BETWEEN to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
            list.add(endDate);
            s += "  AND to_date( ?" + list.size() + ",'yyyy-MM-dd hh24:mi:ss') ";
        }else{
            s+=" and trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(warningType)) {
            list.add(warningType);
            s += " AND WARNING_TYPE = ?" + list.size();
        }
        if (!StringUtils.isEmpty(deptID)) {
            list.add(deptID);
            s += " AND DEPTID = ?" + list.size();
        }
        if(!StringUtils.isEmpty(userName)){
            String sql=" SELECT ID FROM RZTSYSUSER WHERE REALNAME ='"+userName+"'";
            List<Map<String, Object>> maps = execSql(sql, userName);
            String ids = (String) maps.get(0).get("ID");
            if(!StringUtils.isEmpty(ids)){
                s+= " AND USER_ID = '"+ids+"' ";
            }
        }
        Page<Map<String, Object>> pageResult = null;

        if ("0".equals(deptId)) {
           // String sql = "SELECT   TASK_ID, CREATE_TIME_C as CREATE_TIME,TASK_NAME,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE,CHECK_USER_ID,CHECK_DEPTID FROM MONITOR_CHECK_YJ where STATUS=2 and TASK_TYPE=" + type;
            String sql="";
            //巡视的sql
            if(type==1){
                sql = "SELECT j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_C AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,xs.PLAN_START_TIME,j.CHECK_USER_ID,j.CHECK_DEPTID FROM MONITOR_CHECK_YJ j " +
                        "  LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID where j.STATUS=2 and j.TASK_TYPE=" + type;
            }else if(type==2){
                //看护的sql
                sql = "SELECT j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_C AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,kh.PLAN_START_TIME,j.CHECK_USER_ID,j.CHECK_DEPTID FROM MONITOR_CHECK_YJ j " +
                        "  LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=2 and j.TASK_TYPE=" + type;

            }else if(type==3){
                //现场稽查
                sql = "SELECT j.ID,j.APP_RETURN_INFO,   j.TASK_ID, j.CREATE_TIME_C AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,cl.PLAN_START_TIME,j.CHECK_USER_ID,j.CHECK_DEPTID FROM MONITOR_CHECK_YJ j " +
                        "  LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=2 and j.TASK_TYPE=" + type;
            }
            //查询所有
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = execSqlPage(pageable, sql1, list.toArray());
        } else {
           // String sql = "SELECT   TASK_ID, CREATE_TIME_C as CREATE_TIME,TASK_NAME,TASK_TYPE,USER_ID,DEPTID,WARNING_TYPE,CHECK_USER_ID,CHECK_DEPTID FROM MONITOR_CHECK_EJ where STATUS=2 and TASK_TYPE=" + type;
            String sql="";
            //巡视的sql
            if(type==1){
                sql = "SELECT j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_C AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,xs.PLAN_START_TIME,j.CHECK_USER_ID,j.CHECK_DEPTID FROM MONITOR_CHECK_EJ j " +
                        "  LEFT JOIN XS_ZC_TASK xs ON j.TASK_ID=xs.ID where j.STATUS=2 and j.TASK_TYPE=" + type;
            }else if(type==2){
                //看护的sql
                sql = "SELECT j.ID,j.APP_RETURN_INFO,  j.TASK_ID, j.CREATE_TIME_C AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,kh.PLAN_START_TIME,j.CHECK_USER_ID,j.CHECK_DEPTID FROM MONITOR_CHECK_EJ j " +
                        "  LEFT JOIN KH_TASK kh ON j.TASK_ID=kh.ID where j.STATUS=2 and j.TASK_TYPE=" + type;

            }else if(type==3){
                //现场稽查
                sql = "SELECT j.ID,j.APP_RETURN_INFO,   j.TASK_ID, j.CREATE_TIME_C AS CREATE_TIME,j.TASK_NAME,j.TASK_TYPE,j.USER_ID,j.DEPTID,j.WARNING_TYPE,j.REASON,cl.PLAN_START_TIME,j.CHECK_USER_ID,j.CHECK_DEPTID FROM MONITOR_CHECK_EJ j " +
                        "  LEFT JOIN CHECK_LIVE_TASK cl ON j.TASK_ID=cl.ID where j.STATUS=2 and j.TASK_TYPE=" + type;
            }
            //查询该deptId下的
            list.add(deptId);
            s += " AND DEPTID = ?" + list.size();
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = execSqlPage(pageable, sql1, list.toArray());
        }
        Iterator<Map<String, Object>> iterator = pageResult.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();


            //获取到表中每个任务对应的人员的信息
            String userID = (String) next.get("USER_ID");
            if(userID==null){
                iterator.remove();
                continue;
            }
            String checkUserId = (String) next.get("CHECK_USER_ID");
            HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
            Object userInformation = hash.get("UserInformation", userID);
            Object checkUserInfo = hash.get("UserInformation", checkUserId);
            if (userInformation == null) {
                //System.out.println(userInformation);
                continue;
            }

            JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
            JSONObject checkJson = JSONObject.parseObject(checkUserInfo.toString());
            if (jsonObject != null) {
                next.put("DEPT", jsonObject.get("DEPT"));
                next.put("COMPANYNAME", jsonObject.get("COMPANYNAME"));
                next.put("REALNAME", jsonObject.get("REALNAME"));
                next.put("PHONE", jsonObject.get("PHONE"));
                next.put("CHECKUSER", checkJson.get("REALNAME"));
                next.put("CHECKDEPT", checkJson.get("DEPT"));
            }
        }

        return pageResult;
    }

    //获得当前登录用户的deptId
    public String getCurentDeptId(String userId) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        if (userInformation == null) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        String roletype = (String) jsonObject.get("ROLETYPE");
        if (roletype == null) {
            return null;
        }
        return (String) jsonObject.get("DEPTID");

    }

    //未处理到处理中处理
    @Transactional
    public WebApiResponse GJCL(String userId, Long taskId, Integer type, Integer warningType, String checkInfo, String checkAppInfo, String createTime,String checkMode) {
        String deptId = getDeptID(userId);
        if (deptId == null) {
            return WebApiResponse.erro("该用户状态为null");
        }
        try {
            //删除往一级单位推送的告警信息
            this.delRedisKey(taskId, type, warningType, createTime);
        }catch (Exception e){
        }
        //0表示是一级单位
        try {
            if ("0".equals(deptId)) {
                return WebApiResponse.success(resp.updateYJ(taskId, type, warningType, checkInfo, checkAppInfo,createTime,checkMode));
            } else {
                String userId1 = getUserId(2, taskId, type, warningType);//查找该任务负责人id
                checkAlarm(userId1,taskId,warningType,1);
                return WebApiResponse.success(resp.updateEJ(taskId, type, warningType, checkInfo, checkAppInfo,createTime,checkMode));
            }
        } catch (Exception e) {
            return WebApiResponse.erro("添加失败" + e.getMessage());
        }
    }

    //处理中到已处理处理
    @Transactional
    public WebApiResponse GJCLC(String userId, Long taskId, Integer type, Integer warningType, String checkInfo, String createTime,String checkMode) {
        String deptId = getDeptID(userId);
        if (deptId == null) {
            return WebApiResponse.erro("该用户状态为null");
        }
        //String curentDeptId = getCurentDeptId(userId);
        //0表示是一级单位
        try {
            if ("0".equals(deptId)) {
                return WebApiResponse.success(resp.updateYJC(taskId, type, warningType, checkInfo, userId,createTime,checkMode));
            } else {
                String userId1 = getUserId(2, taskId, type, warningType);//查找该任务负责人id
                checkAlarm(userId1,taskId,warningType,2);
                return WebApiResponse.success(resp.updateEJC(taskId, type, warningType, checkInfo, userId,createTime,checkMode));
            }
        } catch (Exception e) {
            return WebApiResponse.erro("添加失败" + e.getMessage());
        }
    }

    //更改Alarm系列表中的状态
    private void checkAlarm(String userId, Long taskId, Integer warningType,Integer status){
        if(warningType==2 || warningType==8||warningType==13){
            //更改离线表中的状态
            offlineRepository.updateOffLineStatus(userId,status);
        }else if(warningType==3 || warningType==5){
            //更改巡视不合格表中的状态
            offlineRepository.updateXS(userId,taskId,status);
        }else if(warningType==4 || warningType==10){
            //更改未按时接任务中的状态
            offlineRepository.updateNotNoTimeStatus(userId,taskId,status);
        }else if(warningType==1){
            //更改超期表中的状态
            offlineRepository.updateOverdue(userId,taskId,status);
        }else if(warningType==7){
            //更改看护脱岗表中的状态
            offlineRepository.updateoffWorkStatus(userId,taskId,status);
        }
    }

    private String getUserId(Integer userType, Long taskId, Integer type, Integer warningType){
        String sql="";
        String userId = "";
        if(userType==1){ //一级单位处理
                sql = "SELECT USER_ID FROM MONITOR_CHECK_YJ WHERE \n" +
                        "TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3 ";
        }else if (userType==2){ //二级单位处理
                sql="SELECT USER_ID FROM MONITOR_CHECK_EJ WHERE \n" +
                        "TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3  ";
        }
        List<Map<String, Object>> maps = execSql(sql, taskId, type, warningType);
        if(maps.size()>0){
           userId = maps.get(0).get("USER_ID").toString();
        }
        return userId;
    }

    //未按时接任务添加
    public void addXSWAS(String[] messages) {

        String sql = "";
        if ("4".equals(messages[3])) {
            //巡视未按时开始任务
            sql = " SELECT id FROM XS_ZC_TASK WHERE ID =?1 AND PLAN_START_TIME<nvl(REAL_START_TIME,sysdate) ";
        } else if ("10".equals(messages[3])) {
            //看护未按时间看护任务
            sql = "SELECT ID FROM KH_TASK WHERE ID=?1 AND PLAN_START_TIME<nvl(REAL_START_TIME,sysdate)";
        }
        try {
            List<Map<String, Object>> maps = execSql(sql, messages[1]);
            //如果有数据证明未按时开始
            if (maps.size() > 0) {
                //将未按时开始设置40分钟进行定时
                String key = "ONE+" + messages[1] + "+" + messages[2] + "+" + messages[3] + "+" + messages[4] + "+" + messages[5] + "+" + messages[6];
                //放进二级表中
                resp.saveCheckEj(SnowflakeIdWorker.getInstance(0, 0).nextId(), Long.valueOf(messages[1]), Integer.valueOf(messages[2]), Integer.valueOf(messages[3]), messages[4], messages[5], messages[6]);
                //设置时间向一级推送
                redisService.setex(key);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //未处理的告警任务处理后删除往一级单位推的键
    public void delRedisKey(Long taskId, Integer taskType, Integer warningType, String createTime) {

       /* String sql = "";
        if (taskType == 1) { //巡视任务
            sql = "SELECT CM_USER_ID AS USER_ID,TD_ORG AS DEPTID,TASK_NAME FROM XS_ZC_TASK WHERE ID=?1";
        } else if (taskType == 2) {//看护任务
            sql = "SELECT kh.USER_ID,d.ID AS DEPTID,kh.TASK_NAME FROM KH_TASK kh LEFT JOIN RZTSYSDEPARTMENT d " +
                    " ON kh.TDYW_ORG = d.DEPTNAME  WHERE kh.ID=?1";
        }*/
        String sql=" SELECT USER_ID,DEPTID,TASK_NAME,REASON FROM MONITOR_CHECK_EJ WHERE TASK_ID=?1 AND TASK_TYPE=?2 AND WARNING_TYPE=?3 AND CREATE_TIME=to_date( ?4,'yyyy-MM-dd hh24:mi:ss')";
        List<Map<String, Object>> maps = execSql(sql, taskId,taskType,warningType,createTime);
        for (Map<String, Object> map : maps) {
            String key ="";
            Object reason = map.get("REASON");
            if(!StringUtils.isEmpty(reason)){
                key = "ONE+" + taskId + "+" + taskType + "+" + warningType + "+" + map.get("USER_ID") + "+" + map.get("DEPTID") + "+" + map.get("TASK_NAME")+"+"+map.get("REASON");
            }else{
                key = "ONE+" + taskId + "+" + taskType + "+" + warningType + "+" + map.get("USER_ID") + "+" + map.get("DEPTID") + "+" + map.get("TASK_NAME");
            }

            redisService.delKey(key);
        }
    }

    //查询告警类型
    public Object warningType(String taskType) {
        String sql = "";
        sql = "SELECT * FROM WARNING_TYPE WHERE TASK_TYPE=?1";
       if(!StringUtils.isEmpty(taskType)){
           return execSql(sql, taskType);
       }
        return "";
    }

    /**
     * 告警总览
     * @param currentUserId
     * @return
     */
    public Object GJZL(String currentUserId) {

        String deptID = getDeptID(currentUserId);

        if (deptID == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(deptID)) {
            return "该用户无此权限";
        }

        String sql ="";
        List<Map<String, Object>> maps = null;
        if("0".equals(deptID)){
            /*sql = "SELECT A.*,T.DEPTNAME FROM RZTSYSDEPARTMENT T LEFT JOIN ( " +
                    "  SELECT nvl(sum(decode(TASK_TYPE, 1, 1)),0)  AS XS, " +
                    "   nvl(sum(decode(TASK_TYPE, 2, 1)),0)  AS KH, " +
                    "   nvl(sum(decode(TASK_TYPE, 3, 1)),0)  AS XCJC, " +
                    "   nvl(sum(decode(TASK_TYPE, 3, 1,1,1,2,1)),0)  AS total, " +
                    "  yj.DEPTID " +
                    "FROM MONITOR_CHECK_YJ yj LEFT JOIN RZTSYSUSER u ON yj.USER_ID=u.ID " +
                    "  WHERE u.USERDELETE=1 AND trunc(yj.CREATE_TIME)=trunc(sysdate) AND STATUS =0 " +
                    "        AND yj.USER_ID IS NOT NULL GROUP BY yj.DEPTID " +
                    "    )A ON T.ID = A.DEPTID WHERE  T.DEPTSORT IS NOT NULL ORDER BY T.DEPTSORT";*/
            sql="SELECT nvl(A.KH,0) AS KH, " +
                    "  nvl(A.XS,0) AS XS, " +
                    "  nvl(A.XCJC,0) AS XCJC, " +
                    "  nvl(A.total,0) AS total, " +
                    "  T.DEPTNAME,T.ID FROM RZTSYSDEPARTMENT T LEFT JOIN ( " +
                    "                      SELECT nvl(sum(decode(TASK_TYPE, 1, 1)),0)  AS XS, " +
                    "                       nvl(sum(decode(TASK_TYPE, 2, 1)),0)  AS KH, " +
                    "                       nvl(sum(decode(TASK_TYPE, 3, 1)),0)  AS XCJC, " +
                    "                       nvl(sum(decode(TASK_TYPE, 3, 1,1,1,2,1)),0)  AS total, " +
                    "                      yj.DEPTID " +
                    "                    FROM MONITOR_CHECK_YJ yj LEFT JOIN RZTSYSUSER u ON yj.USER_ID=u.ID " +
                    "                      WHERE u.USERDELETE=1 AND trunc(yj.CREATE_TIME)=trunc(sysdate) AND STATUS =0 " +
                    "                            AND yj.USER_ID IS NOT NULL GROUP BY yj.DEPTID " +
                    "                        )A ON T.ID = A.DEPTID WHERE  T.DEPTSORT IS NOT NULL ORDER BY T.DEPTSORT";
            maps = execSql(sql);
        }else{
            /*sql="SELECT nvl(sum(decode(TASK_TYPE, 1, 1)),0)  AS XS, " +
                    "     nvl(sum(decode(TASK_TYPE, 2, 1)),0)  AS KH, " +
                    "     nvl(sum(decode(TASK_TYPE, 3, 1)),0)  AS XCJC, " +
                    "     nvl(sum(decode(TASK_TYPE, 3, 1,1,1,2,1)),0)  AS total,d.DEPTNAME " +
                    "FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID LEFT JOIN RZTSYSDEPARTMENT d ON u.CLASSNAME=d.ID " +
                    "   WHERE u.USERDELETE=1  AND trunc(ej.CREATE_TIME)=trunc(sysdate) AND ej.STATUS =0 AND ej.DEPTID=?1 AND ej.USER_ID IS NOT NULL " +
                    "   GROUP BY d.DEPTNAME";*/
            sql = "SELECT nvl(A.KH,0) AS KH,   " +
                    "  nvl(A.XS,0) AS XS,   " +
                    "  nvl(A.XCJC,0) AS XCJC,   " +
                    "  nvl(A.total,0) AS total,   " +
                    "  T.DEPTNAME,   " +
                    "  T.ID FROM (SELECT ID,  DEPTNAME FROM (SELECT ID, DEPTNAME, LASTNODE  FROM RZTSYSDEPARTMENT   " +
                    "          START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0) T LEFT JOIN   " +
                    "  (SELECT nvl(sum(decode(TASK_TYPE, 1, 1)),0)  AS XS,   " +
                    "         nvl(sum(decode(TASK_TYPE, 2, 1)),0)  AS KH,   " +
                    "         nvl(sum(decode(TASK_TYPE, 3, 1)),0)  AS XCJC,   " +
                    "         nvl(sum(decode(TASK_TYPE, 3, 1,1,1,2,1)),0)  AS total,d.ID   " +
                    "    FROM MONITOR_CHECK_EJ ej LEFT JOIN RZTSYSUSER u ON ej.USER_ID=u.ID LEFT JOIN RZTSYSDEPARTMENT d ON u.CLASSNAME=d.ID   " +
                    "       WHERE u.USERDELETE=1  AND trunc(ej.CREATE_TIME)=trunc(sysdate)   " +
                    "             AND ej.STATUS =0 AND ej.DEPTID=?1 AND ej.USER_ID IS NOT NULL   " +
                    "       GROUP BY d.ID) A ON A.ID=T.ID";
            maps = execSql(sql, deptID);
        }
        return maps;
    }

    /**
     * 监控记录查询
     * @param currentUserId
     * @param taskId
     * @return
     */
    public Object jkjl(String currentUserId, Long taskId) {

        String deptID = getDeptID(currentUserId);

        if (deptID == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(deptID)) {
            return "该用户无此权限";
        }

        String sql ="";
        List<Map<String, Object>> maps = null;
        if("0".equals(deptID)){
           //一级
            sql = "SELECT CREATE_TIME,CREATE_TIME_Z,CREATE_TIME_C,CHECKC_INFO,CHECKZ_INFO FROM MONITOR_CHECK_YJ WHERE TASK_ID=?1";
        }else{
            //二级
            sql = "SELECT CREATE_TIME,CREATE_TIME_Z,CREATE_TIME_C,CHECKC_INFO,CHECKZ_INFO FROM MONITOR_CHECK_EJ WHERE TASK_ID=?1";
        }
        maps = execSql(sql,taskId);
        return maps;
    }
}