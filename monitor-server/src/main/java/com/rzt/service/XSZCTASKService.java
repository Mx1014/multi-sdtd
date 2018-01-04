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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     *
     * @param taskType 任务类型  条件查询使用
     * @param status  任务状态   条件查询使用
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
            //巡视sql
            String findSql1 = "select TASK_NAME,STAUTS,ID,CM_USER_ID from XS_ZC_TASK ";
            //看护sql
            String findSql2 = "SELECT kht.TASK_NAME,kht.ID,kht.STATUS,USER_ID FROM KH_TASK kht";
            List<Map<String, Object>> maps = this.execSql(findSql1, null);
            List<Map<String, Object>> maps2 = this.execSql(findSql2, null);
            maps.stream().forEach(a->repository.xsTaskAdd(null!= a.get("STAUTS")?a.get("STAUTS").toString():"4"
                        ,new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),
                            null!= a.get("ID")?a.get("ID").toString():"",UUID.randomUUID().toString(),
                                null!=a.get("CM_USER_ID")?a.get("CM_USER_ID").toString():"","1" ,
                                    null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():""));
            //     看护任务状态标识为中文
           maps2.stream().forEach(b->repository.xsTaskAdd(
                    null!=b.get("STATUS")?b.get("STATUS").toString():"4",
                        new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),null != b.get("ID")?b.get("ID").toString():"",UUID.randomUUID().toString(),
                            null != b.get("USER_ID")? b.get("USER_ID").toString():"","2" ,null != b.get("TASK_NAME")?b.get("TASK_NAME").toString():""));

        }catch (Exception e){
            LOGGER.error("定时任务查询添加失败"+e.getStackTrace());
        }
        LOGGER.info("定时任务查询添加成功");



    }

    /**
     * 根据taskId 查询当前任务详情 包含每轮的巡视任务
     * @param taskId
     * @return
     */
    public WebApiResponse findExecDetallByTaskId(String taskId) {
        ArrayList<String> strings = new ArrayList<>();
        String sql = "select xd.START_TIME,xd.END_TIME,xd.OPERATE_NAME,xd.ID from XS_ZC_TASK_EXEC_DETAIL xd where 1=1";
        List<Map<String, Object>> maps = null;

        try {
            if(null != taskId && !"".equals(taskId)){
                strings.add(taskId);
                sql += "  and  xd.XS_ZC_TASK_EXEC_ID = (select xe.ID from XS_ZC_TASK_EXEC xe where xe.XS_ZC_TASK_ID = ?"+strings.size()+") ORDER BY xd.START_TIME ";
            }
            maps = this.execSql(sql, strings.toArray());
        }catch (Exception e){
            LOGGER.error("任务进度查询失败"+e.getStackTrace());
            return WebApiResponse.erro("查询错误"+e.getStackTrace());
        }
        LOGGER.info("任务进度查询成功");
        return WebApiResponse.success(maps);
    }

    /**
     * 根据taskid获取当前任务的隐患信息
     * @param taskId
     * @return
     */
    public WebApiResponse findYHByTaskId(String taskId) {
        ArrayList<String> strings = new ArrayList<>();
        List<Map<String, Object>> maps = null;
        List<Map<String, Object>> maps2 = null;
        List<Map<String, Object>> maps3 = null;
        String sql = "SELECT yh.YHMS,li.LINE_NAME,li.SECTION" +
                "       from KH_YH_HISTORY yh" +
                "          LEFT JOIN" +
                "            CM_LINE li on li.ID = yh.LINE_ID ";
        String sql3 = "";
       try {
           if(null != taskId && !"".equals(taskId)){
               strings.add(taskId);
               //取到当前隐患的详细信息
               sql += "             WHERE yh.LINE_ID = (SELECT  xc.LINE_ID" +
                       "                FROM XS_ZC_TASK xt" +
                       "                   LEFT JOIN XS_ZC_CYCLE xc" +
                       "                       on xc.ID = xt.XS_ZC_CYCLE_ID" +
                       "                           WHERE xt.ID = ?"+strings.size()+")";
               //取当前隐患的位置  去重
                sql3 = "SELECT distinct li.LINE_NAME,li.SECTION" +
                       "       from KH_YH_HISTORY yh" +
                       "          LEFT JOIN" +
                       "            CM_LINE li on li.ID = yh.LINE_ID" +
                       "" +
                       "             WHERE yh.LINE_ID = (SELECT  xc.LINE_ID" +
                       "                FROM XS_ZC_TASK xt" +
                       "                   LEFT JOIN XS_ZC_CYCLE xc" +
                       "                       on xc.ID = xt.XS_ZC_CYCLE_ID" +
                       "                           WHERE xt.ID = ?"+strings.size()+")";
               maps = this.execSql(sql, strings.toArray());
               maps3 = this.execSql(sql3, strings.toArray());
               maps2 = (List<Map<String, Object>>) findExecDetallByTaskId(taskId).getData();
           }

       }catch (Exception e){
            LOGGER.error("查询任务隐患错误"+e.getStackTrace());
           return WebApiResponse.erro("查询任务隐患错误"+e.getStackTrace());
       }
        LOGGER.info("查询任务隐患信息");
        Map<String, Object> stringMapHashMap = new HashMap<>();
        stringMapHashMap.put("YH",maps);
        stringMapHashMap.put("XQ",maps2);
        stringMapHashMap.put("THWZ",maps3);
        return WebApiResponse.success(stringMapHashMap);
    }
}
