package com.rzt.service;

import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
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
    public WebApiResponse getXsTaskAll(){
        String sql = "SELECT * from TIMED_TASK WHERE CREATETIME BETWEEN (SELECT max(CREATETIME) from TIMED_TASK) - 10/(1*24*60*60) and (SELECT max(CREATETIME) from TIMED_TASK) ";
        List<Map<String, Object>> maps = null;
        try {
             maps = this.execSql(sql, null);
        }catch (Exception e){
            LOGGER.error("定时任务查询添加失败"+e.getStackTrace());
            return WebApiResponse.erro("定时任务查询添加失败"+e.getStackTrace());
        }

    return WebApiResponse.success(maps);
    }



}
