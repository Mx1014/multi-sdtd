package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.netflix.discovery.converters.Auto;
import com.rzt.entity.Monitorcheckyj;
import com.rzt.entity.WarningOneKey;
import com.rzt.repository.WarningOneKeyrepository;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class WarningOneKeyservice extends CurdService<WarningOneKey, WarningOneKeyrepository> {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

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

    /**
     * 查询未处理告警信息
     * @param currentUserId
     * @return
     */
    public Object oneKeyW(String currentUserId, String deptId, String startDate, String endDate,Integer page, Integer size) {
        String DeptId = getDeptID(currentUserId);
        if (DeptId == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(DeptId)) {
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
        if (!StringUtils.isEmpty(deptId)) {
            list.add(deptId);
            s += " AND DEPTID = ?" + list.size();
        }
        Page<Map<String, Object>> pageResult = null;

        if ("0".equals(DeptId)) {
            String sql =" SELECT k.*,u.DEPTID FROM WARNING_ONE_KEY k LEFT JOIN RZTSYSUSER u ON k.USER_ID=u.ID " +
                    " WHERE STATUS_YJ=0 ";
            //查询所有
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = execSqlPage(pageable, sql1, list.toArray());
        } else {
            String sql =" SELECT k.*,u.DEPTID FROM WARNING_ONE_KEY k LEFT JOIN RZTSYSUSER u ON k.USER_ID=u.ID " +
                    " WHERE STATUS_EJ=0 ";
            //查询该deptId下的
            list.add(DeptId);
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
            }

        }
        return pageResult;
    }

    /**
     * 查询处理中告警信息
     * @param currentUserId
     * @return
     */
    public Object oneKeyZ(String currentUserId, String deptId, String startDate, String endDate,Integer page, Integer size) {
        String DeptId = getDeptID(currentUserId);
        if (DeptId == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(DeptId)) {
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
        if (!StringUtils.isEmpty(deptId)) {
            list.add(deptId);
            s += " AND DEPTID = ?" + list.size();
        }
        Page<Map<String, Object>> pageResult = null;

        if ("0".equals(DeptId)) {
            String sql =" SELECT k.*,u.DEPTID FROM WARNING_ONE_KEY k LEFT JOIN RZTSYSUSER u ON k.USER_ID=u.ID " +
                    " WHERE STATUS_YJ=1 ";
            //查询所有
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = execSqlPage(pageable, sql1, list.toArray());
        } else {
            String sql =" SELECT k.*,u.DEPTID FROM WARNING_ONE_KEY k LEFT JOIN RZTSYSUSER u ON k.USER_ID=u.ID " +
                    " WHERE STATUS_EJ=1 ";

            //查询该deptId下的
            list.add(DeptId);
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
            }

        }
        return pageResult;
    }

    /**
     * 查询已处理告警信息
     * @param currentUserId
     * @return
     */
    public Object oneKeyY(String currentUserId, String deptId, String startDate, String endDate,Integer page, Integer size) {
        String DeptId = getDeptID(currentUserId);
        if (DeptId == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(DeptId)) {
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
        if (!StringUtils.isEmpty(deptId)) {
            list.add(deptId);
            s += " AND DEPTID = ?" + list.size();
        }
        Page<Map<String, Object>> pageResult = null;

        if ("0".equals(DeptId)) {
            String sql =" SELECT k.*,u.DEPTID  FROM WARNING_ONE_KEY k LEFT JOIN RZTSYSUSER u ON k.USER_ID=u.ID " +
                    " WHERE STATUS_YJ=2 ";
            //查询所有
            String sql1 = "SELECT * FROM ( " + sql + " ) where 1=1 " + s;
            pageResult = execSqlPage(pageable, sql1, list.toArray());
        } else {
            String sql =" SELECT k.*,u.DEPTID  FROM WARNING_ONE_KEY k LEFT JOIN RZTSYSUSER u ON k.USER_ID=u.ID " +
                    " WHERE STATUS_EJ=2 ";
            //查询该deptId下的
            list.add(DeptId);
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
            }

        }
        return pageResult;
    }


    public Object GJPhoto(Long taskId) {
        String sql="SELECT FILE_PATH,FILE_TYPE FROM PICTURE_WARN WHERE TASK_ID=?1";
        List<Map<String, Object>> maps = execSql(sql, taskId);
        return maps;
    }

    /**
     * 处理
     * @param taskId
     * @return
     */
    public Object GJcl(Long taskId,String checkInfo,String currentUserId) {
        String deptID = getDeptID(currentUserId);
        if (deptID == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(deptID)) {
            return "该用户无此权限";
        }
        try {
            if("0".equals(deptID)){
                return WebApiResponse.success(reposiotry.updateGjYj(taskId,checkInfo));
            }else{
                return WebApiResponse.success(reposiotry.updateGjEj(taskId,checkInfo));
            }
        }catch (Exception e){
            return WebApiResponse.erro("fail:"+e.getMessage());
        }
    }

    public Object GJclc(Long taskId, String checkInfo,String currentUserId) {
        String deptID = getDeptID(currentUserId);
        if (deptID == null) {
            return "该用户状态为null";
        }
        if ("-1".equals(deptID)) {
            return "该用户无此权限";
        }
        try {
            if("0".equals(deptID)){
                return WebApiResponse.success(reposiotry.updateGjYjc(taskId,checkInfo));
            }else{
                return WebApiResponse.success(reposiotry.updateGjEjc(taskId,checkInfo));
            }
        }catch (Exception e){
            return WebApiResponse.erro("fail:"+e.getMessage());
        }
    }
}
