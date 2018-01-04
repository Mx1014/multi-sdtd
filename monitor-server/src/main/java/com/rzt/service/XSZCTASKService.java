package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 李成阳
 * 2018/1/2
 */
@Service
public class XSZCTASKService extends CurdService<TimedTask,XSZCTASKRepository>{
    protected static Logger LOGGER = LoggerFactory.getLogger(XSZCTASKService.class);
    @Autowired
    private XSZCTASKRepository repository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

/*
    public WebApiResponse findXSTASK(String id){

        String sql = "SELECT xst.ID,xst.REAL_START_TIME createTime,xst.TASK_NAME taskName,users.REALNAME realName,users.PHONE phone," +
                "  dept.DEPTNAME dept,xst.PLAN_START_TIME startTime,xst.PLAN_END_TIME endTime" +
                "    from XS_ZC_TASK xst" +
                "      LEFT JOIN RZTSYSUSER users" +
                "          on users.ID = xst.CM_USER_ID" +
                "            LEFT JOIN RZTSYSDEPARTMENT dept" +
                "              on dept.ID = xst.TD_ORG";
        ArrayList<Object> objects = new ArrayList<>();
        List<Map<String, Object>> maps = null;
        try {
            if(null!=id && !"".equals(id)){
                objects.add(id);
                sql+="id=?"+objects.size();
            }
             maps = this.execSql(sql, objects.toArray());
        }catch (Exception e){
            LOGGER.error("查询巡查信息失败，"+e.getStackTrace());
            System.out.println(e.getStackTrace());
            LOGGER.error("查询失败"+e.getStackTrace());
            return WebApiResponse.erro("查询巡查信息失败，"+e.getStackTrace());
        }
        LOGGER.info("查询巡查信息成功");
        return WebApiResponse.success(maps);
    }*/

    /**
     * 按照taskId查询当前任务的详情
     * @param taskId
     * @return
     */
    public WebApiResponse findByTaskId(String taskId) {
        ArrayList<Object> objects = new ArrayList<>();
        List<Map<String, Object>> maps = null;
        String sql = "";
            try {
                if(null != taskId && !"".equals(taskId)){
                    objects.add(taskId);
                    sql+="TASKID = ?"+objects.size();
                    maps = this.execSql(sql, objects);
                }
            }catch (Exception e){
                LOGGER.error("查询错误"+e.getStackTrace());
                return WebApiResponse.erro("查询错误"+e.getStackTrace());
            }
        LOGGER.info("查询成功");
        return WebApiResponse.success(maps);
    }

    /**
     * 供定时器使用   先查询需要的数据 查询后将数据添加进定时任务表
     */
    @Transactional
    public void xsTaskAddAndFind()  {

        try {
            String findSql = "select REAL_START_TIME,STAUTS,ID from XS_ZC_TASK ";
            List<Map<String, Object>> maps = this.execSql(findSql, null);
            maps.stream().forEach(a->repository.xsTaskAdd(a.get("STAUTS").toString(),new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),a.get("ID").toString(),UUID.randomUUID().toString()));
        }catch (Exception e){
            LOGGER.error("定时任务查询添加失败"+e.getStackTrace());
        }
        LOGGER.info("定时任务查询添加成功");



    }

    /**
     * 时间可能会有差异 所以在最近时间到最近时间的前10秒之内都算最新数据
     * @return
     */
    public WebApiResponse getXsTaskAll(String taskType,Integer status){
        String sql = " SELECT   " +
                "  TASKID,   " +
                "  CREATETIME,   " +
                "  USER_ID,   " +
                "  TASKNAME,   " +
                "  TASKTYPE   " +
                "FROM TIMED_TASK   " +
                "WHERE (CREATETIME BETWEEN (SELECT max(CREATETIME)   " +
                "     FROM TIMED_TASK) - 10 / (1 * 24 * 60 * 60) AND (SELECT max(CREATETIME)   " +
                "       FROM TIMED_TASK)) ";
        List<Object> list = new ArrayList<>();
        if(taskType!=null && !"".equals(taskType.trim())){
            list.add(taskType);
            sql+= " AND TASKTYPE = ?"+list.size();
        }
        if(status!=null){
            list.add(status);
            sql+="  AND STATUS = ?"+list.size();
        }
        List<Map<String, Object>> maps = null;
        try {
            maps = this.execSql(sql, list.toArray());
        }catch (Exception e){
            LOGGER.error("定时任务查询添加失败"+e.getStackTrace());
            return WebApiResponse.erro("定时任务查询添加失败"+e.getStackTrace());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        HashOperations hashOperations = redisTemplate.opsForHash();
        for (Map<String,Object> map:maps) {
            Map<String,Object> m = new HashMap<>();
            String userID =(String)map.get("USER_ID");
            JSONObject jsonObject = JSONObject.parseObject(hashOperations.get("UserInformation", userID).toString());
            if(jsonObject!=null){
                System.out.println(jsonObject);
                m.put("CREATETIME",map.get("CREATETIME"));
                m.put("DEPT",jsonObject.get("DEPT"));
                m.put("COMPANYNAME",jsonObject.get("COMPANYNAME"));
                m.put("TASK_NAME",map.get("TASK_NAME"));
                m.put("REALNAME",jsonObject.get("REALNAME"));
                m.put("PHONE",jsonObject.get("PHONE"));
                m.put("CHTYPE"," "); // 抽查类型
            }
            result.add(m);
        }
    return WebApiResponse.success(result);
    }



}
