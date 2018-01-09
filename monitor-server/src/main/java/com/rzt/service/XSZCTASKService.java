package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    /**
     *
     * @param page
     * @param size
     * @param taskType 任务类型  条件查询使用0
     * @return
     */
    public WebApiResponse getXsTaskAll(Integer page,Integer size, String taskType ){
        List<Object> list = new ArrayList<>();
        Pageable pageable = new PageRequest(page, size, null);
        //sql 中 拉取数据为刷新时间至刷新时间前10分钟
        String sql = " SELECT  ID, " +
                "  TASKID,   " +
                "  CREATETIME,   " +
                "  USER_ID,   " +
                "  TASKNAME,   " +
                "  TASKTYPE,CHECKSTATUS ,TARGETSTATUS  " +
                "FROM TIMED_TASK   " +
                "WHERE (CREATETIME BETWEEN (SELECT max(CREATETIME)   " +
                "     FROM TIMED_TASK) - 600 / (1 * 24 * 60 * 60) AND (SELECT max(CREATETIME)   " +
                "       FROM TIMED_TASK))   AND STATUS = 0";
            //  0 代表当前任务列表为未检查
        if(taskType!=null && !"".equals(taskType.trim())){
            list.add(taskType);
            sql+= " AND TASKTYPE = ?"+list.size();
        }

        Page<Map<String, Object>> pageResult = null;
        try {
            pageResult = this.execSqlPage(pageable, sql, list.toArray());
            Iterator<Map<String, Object>> iterator = pageResult.iterator();
            HashOperations hashOperations = redisTemplate.opsForHash();

            while (iterator.hasNext()){

                Map<String, Object> next = iterator.next();
                Object taskid = next.get("TASKID");
                Object tasktype = next.get("TASKTYPE");
                //巡视
                if(tasktype!=null && tasktype.equals("1")){
                    String sqll = "SELECT  c.LINE_ID FROM XS_ZC_TASK x LEFT JOIN XS_ZC_CYCLE c ON c.ID = x.XS_ZC_CYCLE_ID WHERE x.ID =?1";
                    List<Map<String, Object>> maps = execSql(sqll,taskid);
                    if(list.size()>0)
                        next.put("LINE_ID",maps.get(0).get("LINE_ID"));
                }else if (tasktype!=null && tasktype.equals("2")){ //看护
                    String sqlll = "SELECT LINE_ID FROM KH_YH_HISTORY WHERE TASK_ID =?1";
                    List<Map<String, Object>> maps = execSql(sqlll, taskid);
                    if(maps.size()>0)
                        next.put("LINE_ID",maps.get(0).get("LINE_ID"));
                }else if (tasktype!=null && tasktype.equals("3")){ //稽查

                }

                String userID =(String)next.get("USER_ID");
                Object userInformation = hashOperations.get("UserInformation", userID);
                if(userInformation==null){
                    continue;
                }
                JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
                if(jsonObject!=null){
                    next.put("DEPT",jsonObject.get("DEPT"));
                    next.put("COMPANYNAME",jsonObject.get("COMPANYNAME"));
                    next.put("REALNAME",jsonObject.get("REALNAME"));
                    next.put("PHONE",jsonObject.get("PHONE"));
                    next.put("CHTYPE"," "); // 抽查类型
                }
            }
       }catch (Exception e){
            LOGGER.error("抽查任务查询失败"+e.getMessage());
            return WebApiResponse.erro("抽查任务查询失败"+e.getMessage());
        }
        return WebApiResponse.success(pageResult);
    }
    /**
     * 供定时器使用   先查询需要的数据 查询后将数据添加进定时任务表
     */
   @Transactional
    public void xsTaskAddAndFind()  {

        try {
            //巡视sql
            String findSql1 = "select x.TASK_NAME,x.STAUTS,x.ID,x.CM_USER_ID from XS_ZC_TASK x" +
                    "  WHERE x.ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 1 ) AND  x.STAUTS != 0 ";
            //看护sql
            String findSql2 = "SELECT kht.TASK_NAME,kht.ID,kht.STATUS,USER_ID FROM KH_TASK kht WHERE kht.ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 2 )  AND kht.STATUS != 0 ";
            List<Map<String, Object>> maps = this.execSql(findSql1, null);
            List<Map<String, Object>> maps2 = this.execSql(findSql2, null);
            Iterator<Map<String, Object>> iterator = maps.iterator();
            while (iterator.hasNext()){
                Map<String, Object> a = iterator.next();
                Integer CheckStatus = 0;
                //任务状态  0 未开始   1 进行中  2 已完成  3 未知或值为空
                if("2".equals( a.get("STAUTS").toString())){
                    CheckStatus = 1;
                }
                repository.xsTaskAdd(null!= a.get("STAUTS")?a.get("STAUTS").toString():"4"
                        ,new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),
                        null!= a.get("ID")?a.get("ID").toString():"",UUID.randomUUID().toString(),
                        null!=a.get("CM_USER_ID")?a.get("CM_USER_ID").toString():"","1" ,
                        null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():"",CheckStatus);




            }

            Iterator<Map<String, Object>> iterator1 = maps2.iterator();
            while (iterator1.hasNext()){
                Map<String, Object> b = iterator1.next();
                Integer CheckStatus = 0;
                if("2".equals(b.get("STATUS").toString())){
                    CheckStatus = 1;
                }
               repository.xsTaskAdd(
                        null!=b.get("STATUS")?b.get("STATUS").toString():"4",
                        new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),null != b.get("ID")?b.get("ID").toString():"",UUID.randomUUID().toString(),
                        null != b.get("USER_ID")? b.get("USER_ID").toString():"","2" ,null != b.get("TASK_NAME")?b.get("TASK_NAME").toString():"",CheckStatus);

            }

        }catch (Exception e){
            LOGGER.error("定时任务查询添加失败"+e.getMessage());
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
                sql += "  and  xd.XS_ZC_TASK_EXEC_ID in (select xe.ID from XS_ZC_TASK_EXEC xe where xe.XS_ZC_TASK_ID in ?"+strings.size()+") ORDER BY xd.START_TIME";
            }
            maps = this.execSql(sql, strings.toArray());
        }catch (Exception e){
            LOGGER.error("任务进度查询失败"+e.getMessage());
            return WebApiResponse.erro("查询错误"+e.getMessage());
        }
        LOGGER.info("任务进度查询成功");
        return WebApiResponse.success(maps);
    }

    /**
     * 根据taskid获取当前任务的隐患信息
     * @param taskId
     * @return
     */
    public WebApiResponse findYHByTaskId(String taskId,String TASKTYPE) {
        ArrayList<String> strings = new ArrayList<>();
        List<Map<String, Object>> maps = null;
        List<Map<String, Object>> maps2 = null;
        List<Map<String, Object>> maps3 = null;

       try {
           if(null != taskId && !"".equals(taskId)){
               if (null != TASKTYPE && !"".equals(TASKTYPE)){//判断当前任务类型   1 巡视 2 看护 其他待定
                if("1".equals(TASKTYPE)){
                    String sql = "SELECT yh.YHMS,li.LINE_NAME,li.SECTION" +
                            "       from KH_YH_HISTORY yh" +
                            "          LEFT JOIN" +
                            "            CM_LINE li on li.ID = yh.LINE_ID ";
                    strings.add(taskId);
                    //取到当前隐患的详细信息
                    sql += "             WHERE yh.LINE_ID = (SELECT  xc.LINE_ID" +
                            "                FROM XS_ZC_TASK xt" +
                            "                   LEFT JOIN XS_ZC_CYCLE xc" +
                            "                       on xc.ID = xt.XS_ZC_CYCLE_ID" +
                            "                           WHERE xt.ID = ?"+strings.size()+")";
                    //取当前隐患的位置  去重
                    String sql3 = "SELECT distinct li.LINE_NAME,li.SECTION" +
                            "       from KH_YH_HISTORY yh" +
                            "          LEFT JOIN" +
                            "            CM_LINE li on li.ID = yh.LINE_ID" +
                            "             WHERE yh.LINE_ID = (SELECT  xc.LINE_ID" +
                            "                FROM XS_ZC_TASK xt" +
                            "                   LEFT JOIN XS_ZC_CYCLE xc" +
                            "                       on xc.ID = xt.XS_ZC_CYCLE_ID" +
                            "                           WHERE xt.ID = ?"+strings.size()+")";
                    maps = this.execSql(sql, strings.toArray());
                    maps3 = this.execSql(sql3, strings.toArray());
                    maps2 = (List<Map<String, Object>>) findExecDetallByTaskId(taskId).getData();
                    LOGGER.info("巡视任务详情查询");
                }

                   if("2".equals(TASKTYPE)){ //看护

                       strings.add(taskId);
                       //取到当前隐患的详细信息
                       String sql = "SELECT yh.YHMS,li.LINE_NAME,li.SECTION" +
                               "                                             from KH_YH_HISTORY yh" +
                               "                                               LEFT JOIN" +
                               "                                               CM_LINE li on li.ID = yh.LINE_ID" +
                               "                                             WHERE yh.ID = (SELECT k.YH_ID FROM KH_SITE k LEFT JOIN KH_TASK kh ON kh.SITE_ID = k.ID" +
                               "             WHERE kh.ID = ?"+strings.size()+")";
                       //取当前隐患的位置  去重
                       String sql3 = "SELECT distinct li.LINE_NAME,li.SECTION" +
                               "       from KH_YH_HISTORY yh" +
                               "          LEFT JOIN" +
                               "            CM_LINE li on li.ID = yh.LINE_ID" +
                               "             WHERE yh.ID = (SELECT k.YH_ID FROM KH_SITE k LEFT JOIN KH_TASK kh ON kh.SITE_ID = k.ID" +
                               "             WHERE kh.ID = ?"+strings.size()+")";
                       //获取任务详情
                       String sql4 = "SELECT p.PROCESS_NAME OPERATE_NAME,p.CREATE_TIME START_TIME,p.ID from PICTURE_KH p LEFT JOIN KH_TASK k ON  k.ID = p.TASK_ID  where k.ID = ?"+strings.size()+" ORDER BY p.CREATE_TIME";

                       maps = this.execSql(sql, strings.toArray());
                       maps3 = this.execSql(sql3, strings.toArray());
                       maps2 = this.execSql(sql4,strings.toArray());
                       LOGGER.info("看护任务详情查询");
                   }
               }


           }

       }catch (Exception e){
            LOGGER.error("查询任务隐患错误"+e.getMessage());
           return WebApiResponse.erro("查询任务隐患错误"+e.getMessage());
       }
        LOGGER.info("查询任务隐患信息");
        Map<String, Object> stringMapHashMap = new HashMap<>();
        stringMapHashMap.put("YH",maps);
        stringMapHashMap.put("XQ",maps2);
        stringMapHashMap.put("THWZ",maps3);
        return WebApiResponse.success(stringMapHashMap);
    }


    @Transactional
    public void checkOff(Long questionTaskId) {
        repository.xsTaskUpdate(questionTaskId);
    }


}
