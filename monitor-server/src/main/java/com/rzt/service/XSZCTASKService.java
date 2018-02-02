package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.repository.TimedConfigRepository;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.RedisUtil;
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
import java.lang.reflect.Array;
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
    @Autowired
    private TimedConfigRepository timedConfigRepository;
    @Autowired
    private RedisUtil redisUtil;


    public String findDeptAuth(String userId){
       try {
           Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
           if(null != userInformation1 && !"".equals(userInformation1)) {
               JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
               String roletype = (String) jsonObject1.get("ROLETYPE");
               return roletype;
           }
       }catch (Exception e){
           LOGGER.error("通道公司权限查询失败"+e.getMessage());
        return "-1";
       }
       return "1";
    }

    /**
     *查询所有为抽查任务列表
     * @param page
     * @param size
     * @param taskType 任务类型  条件查询使用0
     * @return
     */
    public WebApiResponse getXsTaskAll(Integer page,Integer size, String taskType ,String userId,String userName,String TD,String targetType){
        /**
         *   所有权限	    0
             公司本部权限	1
             属地单位权限	2
             外协队伍权限	3
             组织权限	    4
             个人权限	    5

         */
        List<Object> list = new ArrayList<>();
        Pageable pageable = new PageRequest(page, size);
        //sql 中 拉取数据为刷新时间至刷新时间前10分钟
        String sql = "";
        Page<Map<String, Object>> pageResult = null;
        try {
        if(null == userId || "".equals(userId)){
            return WebApiResponse.success("");
        }

        Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
        if(null != userInformation1 && !"".equals(userInformation1)){
            JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
            String roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
            String deptid = (String) jsonObject1.get("DEPTID");//当角色权限为3时需要只显示本单位的任务信息
            if(null != roletype && !"".equals(roletype)){//证明当前用户信息正常
                int i = Integer.parseInt(roletype);
                switch (i){
                    case 0 :{//一级单位   显示三天周期抽查的任务   ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual)

                        sql = "  SELECT * FROM (  SELECT DISTINCT t.TASKID," +
                                "   t.ID," +
                                "   t.CREATETIME," +
                                "   t.USER_ID," +
                                "   t.TASKNAME," +
                                "    t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did," +
                                "  d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = xs.WX_ORG) AS COMPANYNAME" +
                                "  ,xs.WX_ORG" +
                                "    FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                "   LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID LEFT JOIN XS_ZC_TASK xs ON xs.ID = t.TASKID" +
                                "    WHERE t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual)" +
                                "     AND t.STATUS = 0 AND t.THREEDAY = 0 AND t.TASKTYPE = 1" +
                                "  UNION ALL" +
                                "   SELECT DISTINCT t.TASKID," +
                                "  t.ID," +
                                "  t.CREATETIME," +
                                "  t.USER_ID," +
                                "  t.TASKNAME," +
                                "  t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did," +
                                "    d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = kh.WX_ORG) AS COMPANYNAME" +
                                "  ,kh.WX_ORG" +
                                "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID LEFT JOIN KH_TASK kh ON kh.ID = t.TASKID" +
                                "   WHERE t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual)  " +
                                "   AND t.STATUS = 0 AND t.THREEDAY = 0 AND t.TASKTYPE = 2 ) WHERE 1=1";
                        break;
                    }case 1 :{//二级单位   显示全部周期为两小时的任务
                        if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功，进入流程
                            list.add(deptid);
                            sql = "  SELECT * FROM (  SELECT DISTINCT t.TASKID," +
                                    "   t.ID," +
                                    "   t.CREATETIME," +
                                    "   t.USER_ID," +
                                    "   t.TASKNAME," +
                                    "    t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did," +
                                    "  d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = xs.WX_ORG) AS COMPANYNAME" +
                                    "  ,xs.WX_ORG" +
                                    "    FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                    "   LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID LEFT JOIN XS_ZC_TASK xs ON xs.ID = t.TASKID" +
                                    "    WHERE t.CREATETIME > ( SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)" +
                                    "   FROM TIMED_TASK  WHERE THREEDAY = 0 )     AND t.STATUS = 0 AND t.THREEDAY = 0 AND t.TASKTYPE = 1" +
                                    "  UNION ALL" +
                                    "   SELECT DISTINCT t.TASKID," +
                                    "  t.ID," +
                                    "  t.CREATETIME," +
                                    "  t.USER_ID," +
                                    "  t.TASKNAME," +
                                    "  t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as DID," +
                                    "    d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = kh.WX_ORG) AS COMPANYNAME" +
                                    "  ,kh.WX_ORG" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                    "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID LEFT JOIN KH_TASK kh ON kh.ID = t.TASKID" +
                                    "   WHERE t.CREATETIME > ( SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)" +
                                    "      FROM TIMED_TASK  WHERE THREEDAY = 0 )     AND t.STATUS = 0 AND t.THREEDAY = 0 AND t.TASKTYPE = 2 )  WHERE DID  = ?"+list.size();
                        }else {
                            LOGGER.error("获取当前用户单位信息失败");
                            return WebApiResponse.erro("获取当前用户单位信息失败");
                        }
                        break;
                    }case 2 :{//三级单位   只显示本单位的任务

                        if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功，进入流程
                            list.add(deptid);
                            sql = " SELECT * FROM ( SELECT DISTINCT t.TASKID," +
                                    "   t.ID," +
                                    "   t.CREATETIME," +
                                    "   t.USER_ID," +
                                    "   t.TASKNAME," +
                                    "    t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did," +
                                    "  d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = xs.WX_ORG) AS COMPANYNAME" +
                                    "  ,xs.WX_ORG" +
                                    "    FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                    "   LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID LEFT JOIN XS_ZC_TASK xs ON xs.ID = t.TASKID" +
                                    "    WHERE t.CREATETIME > ( SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)" +
                                    "   FROM TIMED_TASK  WHERE THREEDAY = 0 )     AND t.STATUS = 0 AND t.THREEDAY = 0 AND t.TASKTYPE = 1" +
                                    "  UNION ALL" +
                                    "   SELECT DISTINCT t.TASKID," +
                                    "  t.ID," +
                                    "  t.CREATETIME," +
                                    "  t.USER_ID," +
                                    "  t.TASKNAME," +
                                    "  t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did," +
                                    "    d.DEPTNAME as DEPT,u.REALNAME as REALNAME,u.PHONE,(SELECT COMPANYNAME FROM RZTSYSCOMPANY WHERE ID = kh.WX_ORG) AS COMPANYNAME" +
                                    "  ,kh.WX_ORG" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                    "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID LEFT JOIN KH_TASK kh ON kh.ID = t.TASKID" +
                                    "    WHERE t.CREATETIME > ( SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)" +
                                    "      FROM TIMED_TASK  WHERE THREEDAY = 0 )     AND t.STATUS = 0 AND t.THREEDAY = 0 AND t.TASKTYPE = 2 ) WHERE DID  = ?"+list.size();
                        }else {
                            LOGGER.error("获取当前用户单位信息失败");
                            return WebApiResponse.erro("获取当前用户单位信息失败");
                        }
                        break;
                    }default:{
                        LOGGER.error("获取登录人权限失败,当前权限未知");
                        return WebApiResponse.erro("获取登录人权限失败,当前权限未知");
                    }
                }
            }
        }
        if(taskType!=null && !"".equals(taskType.trim())){// 判断当前任务类型  巡视1   看护2  看护稽查3  巡视稽查4
            sql+= "  AND t.TASKTYPE = "+taskType;
        }

        //查询责任人
            if(null != userName && !"".equals(userName)){
            sql += "  AND  t.USER_ID in (SELECT ru.ID from RZTSYSUSER ru WHERE ru.REALNAME LIKE '%"+userName+"%')";
            }
            //通道单位
            if(null != TD && !"".equals(TD)){
            sql += "  AND  d.ID = '"+TD+"'";
            }
            //任务状态
            if(null != targetType && !"".equals(targetType)){
                sql += "  AND  t.TARGETSTATUS =  "+targetType;
            }

        if(null != sql && !"".equals(sql)){
            sql +="   ORDER BY CREATETIME DESC     ";
        }

            pageResult = this.execSqlPage(pageable, sql, list.toArray());
            /*Iterator<Map<String, Object>> iterator = pageResult.iterator();
            HashOperations hashOperations = redisTemplate.opsForHash();*/

           /* while (iterator.hasNext()){

                Map<String, Object> next = iterator.next();
                Object taskid = next.get("TASKID");
                Object tasktype = next.get("TASKTYPE");
                //巡视
                if(tasktype!=null && tasktype.equals("1")){
                    String sqll = "SELECT  c.LINE_ID FROM XS_ZC_TASK x LEFT JOIN XS_ZC_CYCLE c ON c.ID = x.XS_ZC_CYCLE_ID WHERE x.ID =?1";
                    List<Map<String, Object>> maps = execSql(sqll,taskid);
                    if(list.size()>0){
                        next.put("LINE_ID",maps.get(0).get("LINE_ID"));
                    }
                }else if (tasktype!=null && tasktype.equals("2")){ //看护
                    String sqlll = "SELECT LINE_ID FROM KH_YH_HISTORY WHERE TASK_ID =?1";
                    List<Map<String, Object>> maps = execSql(sqlll, taskid);
                    if(maps.size()>0){
                        next.put("LINE_ID",maps.get(0).get("LINE_ID"));
                    }
                }else if (tasktype!=null && tasktype.equals("3")){ //看护稽查

                }else if (tasktype!=null && tasktype.equals("4")){ //巡视稽查

                }else {
                    LOGGER.error("查询类型不明确");
                    return WebApiResponse.erro("类型不明确");
                }
                Object userInformation = null;
                        String userID =(String)next.get("USER_ID");
                        if(null !=userID && !"".equals(userID)){
                            userInformation = hashOperations.get("UserInformation", userID);
                        }

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
            }*/
       }catch (Exception e){
            LOGGER.error("抽查任务查询失败"+e.getMessage());
            return WebApiResponse.erro("抽查任务查询失败"+e.getMessage());
        }
        return WebApiResponse.success(pageResult);
    }
    /**
     * 二级单位使用   固定时间抽查任务 小时为单位
     * 先查询需要的数据 查询后将数据添加进定时任务表
     */
   @Transactional
    public void xsTaskAddAndFind()  {
       //统一时间作为阶段标识
       Date date1 = new Date();
       try {
            //抽查之前需要记录上一次抽查任务的审核完成情况
            //查询所有通道单位的sql
            String deptSql = "SELECT d.ID" +
                    "   FROM RZTSYSDEPARTMENT d WHERE d.DEPTPID = '402881e6603a69b801603a6ab1d70000'";
            List<Map<String, Object>> maps1 = this.execSql(deptSql);
            for (Map<String, Object> map : maps1) {
                String deptId = (String) map.get("ID");
                String sumSql = "SELECT count(*) AS SUM" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                        "     WHERE u.ID IS  NOT  null AND t.CREATETIME >= (SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)" +
                        "       FROM TIMED_TASK  WHERE THREEDAY = 0 )  AND d.ID = '"+deptId+"'";
                //实际检查完成数
                String ComSumSql = "SELECT count(*) AS COMSUM" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                        "     WHERE u.ID IS  NOT  null AND t.CREATETIME >= (SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)" +
                        "       FROM TIMED_TASK  WHERE THREEDAY = 0 ) AND STATUS = 1  AND d.ID = '"+deptId+"'";
                //抽查结束时间
                String dateSql = "SELECT max(CREATETIME) as TIME" +
                        "   FROM TIMED_TASK  WHERE THREEDAY = 0";
                Map<String, Object> map1 = this.execSqlSingleResult(sumSql);
                Map<String, Object> map2 = this.execSqlSingleResult(ComSumSql);
                Map<String, Object> map3 = this.execSqlSingleResult(dateSql);

                String sum = map1.get("SUM").toString();
                String comSum = map2.get("COMSUM").toString();
                String date =  map3.get("TIME").toString();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH");
                //任务抽查时间
                Date parse = simpleDateFormat.parse(date);
                //插入到记录表中
                String uuid = UUID.randomUUID().toString();

                // 查询当前时间值班人    查询出值班人id
                //   当前部门    deptId
                  String workingSql = "SELECT * " +
                          "           FROM WORKING_TIMED WHERE DEPT_ID = '"+deptId+"'";
                  Map<String, Object> map4 = this.execSqlSingleResult(workingSql, null);
                  // 逻辑是  开始时间和结束时间之内属于白班    之外属于夜班
                  //倒班白天开始时间
                  String start_time = map4.get("START_TIME").toString();
                  //倒班白班结束时间
                  String end_time = map4.get("END_TIME").toString();
                  //白班用户id
                  String day_user = map4.get("DAY_USER").toString();
                  //夜班用户id
                  String night_user = map4.get("NIGHT_USER").toString();
                  //获取到当前抽查时间的小时位   查看这条抽查任务稽查人
                  int hours = parse.getHours();
                  String JCID = night_user;
                  //判断证明是白班用户
                  if(hours <= Integer.parseInt(end_time)&& hours >= Integer.parseInt(start_time)){
                      JCID = day_user;
                  }
                  timedConfigRepository.insertTaskRecord(uuid,date1,sum,comSum,deptId,parse,JCID);

                LOGGER.info(deptId+ "单位本周期查询情况添加");
            }


            //巡视sql
            String findSql1 = "select x.TASK_NAME,x.STAUTS,x.ID,x.CM_USER_ID from XS_ZC_TASK x" +
                    "  WHERE x.ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 1 ) AND  x.STAUTS != 0  AND  x.STAUTS != 3   AND x.IS_DELETE = 0 ";
            //看护sql
            String findSql2 = "SELECT kht.TASK_NAME,kht.ID,kht.STATUS,USER_ID FROM KH_TASK kht WHERE kht.ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 2 )  AND kht.STATUS != 0  AND  kht.STATUS != 3  ";
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
                        null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():"",CheckStatus,"0");
            }
            LOGGER.info("巡视稽查任务抽查完毕");
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
                        null != b.get("USER_ID")? b.get("USER_ID").toString():"","2" ,null != b.get("TASK_NAME")?b.get("TASK_NAME").toString():"",CheckStatus,"0");
            }
            LOGGER.info("看护任务抽查完毕");
            //看护稽查
          /*  String khjcsql = "SELECT ID,STATUS,USER_ID,TASK_NAME  " +
                    "         FROM CHECK_LIVE_TASK  WHERE STATUS != 0 AND ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 3 )";
            List<Map<String, Object>> khjcmaps = this.execSql(khjcsql, null);
            Iterator<Map<String, Object>> khjciterator = maps.iterator();
            while (khjciterator.hasNext()){
                Map<String, Object> a = khjciterator.next();
                Integer CheckStatus = 0;
                //任务状态  0 未开始   1 进行中  2 已完成  3 未知或值为空
                if("2".equals( a.get("STATUS").toString())){
                    CheckStatus = 1;
                }
                // 看护稽查任务添加
                repository.xsTaskAdd(null!= a.get("STATUS")?a.get("STATUS").toString():"4"
                        ,new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),
                        null!= a.get("ID")?a.get("ID").toString():"",UUID.randomUUID().toString(),
                        null!=a.get("USER_ID")?a.get("USER_ID").toString():"","3" ,
                        null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():"",CheckStatus,"0");
            }
            LOGGER.info("看护稽查任务抽查完毕");
            //巡视稽查
            String xsjcsql = "SELECT ID,STATUS,USER_ID,TASK_NAME" +
                    "    FROM CHECK_LIVE_TASKXS  WHERE STATUS != 0   AND ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 4 )";
            List<Map<String, Object>> xsjcmaps = this.execSql(xsjcsql, null);
            Iterator<Map<String, Object>> xsjciterator = maps.iterator();
            while (xsjciterator.hasNext()){
                Map<String, Object> a = xsjciterator.next();
                Integer CheckStatus = 0;
                //任务状态  0 未开始   1 进行中  2 已完成  3 未知或值为空
                if("2".equals( a.get("STATUS").toString())){
                    CheckStatus = 1;
                }
                // 巡视稽查任务添加
                repository.xsTaskAdd(null!= a.get("STATUS")?a.get("STATUS").toString():"4"
                        ,new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),
                        null!= a.get("ID")?a.get("ID").toString():"",UUID.randomUUID().toString(),
                        null!=a.get("USER_ID")?a.get("USER_ID").toString():"","4" ,
                        null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():"",CheckStatus,"0");
            }
                LOGGER.info("巡视稽查任务抽查完毕");*/


          // 此处更改定时器配置表中的上次刷新时间

            timedConfigRepository.updateTimedConfigLastTime(new Date(),"TIME_CONFIG");

        }catch (Exception e){
            LOGGER.error("（二级单位，周期可变，小时为单位 ）定时任务数据抽取失败"+e.getMessage());
        }
        LOGGER.info("（二级单位，周期可变，小时为单位）定时任务数据抽取成功");



    }





    /**
     * 一级单位使用   固定时间抽查任务 三天为一个周期
     * 先查询需要的数据 查询后将数据添加进定时任务表
     */
    @Transactional
    public void xsTaskAddAndFindThree()  {

        try {




            //巡视sql
            String findSql1 = "SELECT k.TASK_NAME,k.STAUTS,k.ID,k.CM_USER_ID" +
                    "  FROM RZTSYSUSER u" +
                    "  LEFT JOIN XS_ZC_TASK k ON k.CM_USER_ID = u.ID" +
                    "  WHERE WORKTYPE = 2 AND k.ID IS NOT  NULL AND k.REAL_START_TIME =" +
                    "   (SELECT max(h.REAL_START_TIME)" +
                    "   FROM XS_ZC_TASK h WHERE h.CM_USER_ID = u.ID) AND k.STAUTS != 0 AND" +
                    "   k.ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 1 AND THREEDAY = 1) AND  k.STAUTS != 0   AND k.IS_DELETE = 0";
            //看护sql
            String findSql2 = "SELECT k.TASK_NAME,k.ID,k.STATUS,k.USER_ID" +
                    "  FROM RZTSYSUSER u" +
                    "  LEFT JOIN KH_TASK k ON k.USER_ID = u.ID" +
                    "  WHERE WORKTYPE = 1 AND k.ID IS NOT  NULL AND k.CREATE_TIME =" +
                    "  (SELECT max(h.CREATE_TIME)" +
                    "   FROM KH_TASK h WHERE h.USER_ID = u.ID) AND k.STATUS != 0 AND k.STATUS != 3"  +
                    "   AND k.ID NOT IN" +
                    "       (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 2 AND THREEDAY =1)";
            List<Map<String, Object>> maps = this.execSql(findSql1, null);

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
                        null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():"",CheckStatus,"1");
            }
            LOGGER.info("巡视任务抽查完毕");
            List<Map<String, Object>> maps2 = this.execSql(findSql2, null);
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
                        null != b.get("USER_ID")? b.get("USER_ID").toString():"","2" ,null != b.get("TASK_NAME")?b.get("TASK_NAME").toString():"",CheckStatus,"1");

            }
            LOGGER.info("看护任务抽查完毕");
            //看护稽查
            /*String khjcsql = "SELECT ID,STATUS,USER_ID,TASK_NAME  " +
                    "         FROM CHECK_LIVE_TASK  WHERE STATUS != 0 AND ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 3 )";
            List<Map<String, Object>> khjcmaps = this.execSql(khjcsql, null);
            Iterator<Map<String, Object>> khjciterator = maps.iterator();
            while (khjciterator.hasNext()){
                Map<String, Object> a = khjciterator.next();
                Integer CheckStatus = 0;
                //任务状态  0 未开始   1 进行中  2 已完成  3 未知或值为空
                if("2".equals( a.get("STATUS").toString())){
                    CheckStatus = 1;
                }
                // 看护稽查任务添加
                repository.xsTaskAdd(null!= a.get("STATUS")?a.get("STATUS").toString():"4"
                        ,new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),
                        null!= a.get("ID")?a.get("ID").toString():"",UUID.randomUUID().toString(),
                        null!=a.get("USER_ID")?a.get("USER_ID").toString():"","3" ,
                        null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():"",CheckStatus,"1");
            }
            LOGGER.info("看护稽查任务抽查完毕");
            //巡视稽查
            String xsjcsql = "SELECT ID,STATUS,USER_ID,TASK_NAME" +
                    "    FROM CHECK_LIVE_TASKXS  WHERE STATUS != 0   AND ID NOT IN (SELECT  t.TASKID from TIMED_TASK t WHERE t.CHECKSTATUS = 1 AND t.TASKTYPE = 4 )";
            List<Map<String, Object>> xsjcmaps = this.execSql(xsjcsql, null);
            Iterator<Map<String, Object>> xsjciterator = maps.iterator();
            while (xsjciterator.hasNext()){
                Map<String, Object> a = xsjciterator.next();
                Integer CheckStatus = 0;
                //任务状态  0 未开始   1 进行中  2 已完成  3 未知或值为空
                if("2".equals( a.get("STATUS").toString())){
                    CheckStatus = 1;
                }
                // 巡视稽查任务添加
                repository.xsTaskAdd(null!= a.get("STATUS")?a.get("STATUS").toString():"4"
                        ,new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()),
                        null!= a.get("ID")?a.get("ID").toString():"",UUID.randomUUID().toString(),
                        null!=a.get("USER_ID")?a.get("USER_ID").toString():"","4" ,
                        null!=a.get("TASK_NAME")?a.get("TASK_NAME").toString():"",CheckStatus,"1");
            }
            LOGGER.info("巡视稽查任务抽查完毕");

*/

        }catch (Exception e){
            LOGGER.error("（一级单位，三天周期）定时任务数据抽取失败"+e.getMessage());
        }
        LOGGER.info("（一级单位，三天周期）定时任务数据抽取成功");
    }
    /**
     * 根据taskId 查询当前任务详情 包含每轮的巡视任务
     * @param taskId
     * @return
     */
    public WebApiResponse findExecDetallByTaskId(Long taskId,String taskType) {
        ArrayList<Object> strings = new ArrayList<>();
        String sql = "";
        List<Map<String, Object>> maps = null;

        try {
            if(null != taskId && !"".equals(taskId)){
                strings.add(taskId);
                if(null != taskType && !"".equals(taskType)){//判断当前任务类型
                    switch (Integer.parseInt(taskType)){
                        case 1 :{//巡视
                             sql =  " select xd.START_TIME,xd.END_TIME,xd.OPERATE_NAME,xd.ID from XS_ZC_TASK_EXEC_DETAIL xd where   xd.XS_ZC_TASK_EXEC_ID in (select xe.ID from XS_ZC_TASK_EXEC xe where xe.XS_ZC_TASK_ID = ?"+strings.size()+") ORDER BY xd.START_TIME";
                            break;
                        }case 2 :{//看护
                            sql = "SELECT PLAN_START_TIME as START_TIME,PLAN_END_TIME as END_TIME,TASK_NAME as OPERATE_NAME,ID" +
                                    "   FROM KH_TASK WHERE ID = ?"+strings.size();
                            break;
                        }case 3 :{//看护稽查
                            String khsql = "SELECT c.ID,c.PLAN_START_TIME as START_TIME ,c.PLAN_END_TIME as END_TIME,(SELECT TASK_NAME" +
                                    "         FROM KH_TASK k WHERE k.ID = c.KH_TASK_ID ) as OPERATE_NAME,c.KH_TASK_ID" +
                                    "           FROM CHECK_LIVE_TASK_DETAIL c WHERE TASK_ID = ?"+strings.size();
                            break;
                        }case 4 :{//巡视稽查
                            String xsjcsql = "SELECT c.ID,c.PLAN_START_TIME as START_TIME ,c.PLAN_END_TIME as END_TIME,(SELECT x.TASK_NAME" +
                                    "   FROM XS_ZC_TASK x WHERE x.ID = c.XS_TASK_ID ) as OPERATE_NAME" +
                                    "    FROM CHECK_LIVE_TASK_DETAILXS c WHERE TASK_ID = ?"+strings.size();
                            break;
                        }default:{
                            LOGGER.error("任务详情查询失败--任务类型错误");
                                return WebApiResponse.erro("任务详情查询失败--任务类型错误");
                        }
                    }
                }
            }
            if(null != sql && !"".equals(sql)){
                maps = this.execSql(sql, strings.toArray());
            }

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
    public WebApiResponse findYHByTaskId(Long taskId,String TASKTYPE) {
        ArrayList<Object> strings = new ArrayList<>();
        List<Map<String, Object>> maps = new ArrayList<>();
        List<Map<String, Object>> maps2 = new ArrayList<>();
        List<Map<String, Object>> maps3 = new ArrayList<>();

       try {
           if(null != taskId && !"".equals(taskId)){
               if (null != TASKTYPE && !"".equals(TASKTYPE)){//判断当前任务类型   1 巡视 2 看护 其他待定
                if("1".equals(TASKTYPE)){
                    strings.add(taskId);


                    String sql = "SELECT *" +
                            "      FROM XS_SB_YH WHERE XSTASK_ID = ?"+strings.size();
                    String sql3 =  "SELECT distinct LINE_NAME" +
                            "      FROM XS_SB_YH WHERE XSTASK_ID = ?"+strings.size();
                    maps = this.execSql(sql, strings.toArray());
                    maps3 = this.execSql(sql3, strings.toArray());
                    maps2 = (List<Map<String, Object>>) findExecDetallByTaskId(taskId,TASKTYPE).getData();
                    LOGGER.info("巡视任务详情查询");
                }

                   if("2".equals(TASKTYPE)){ //看护

                       strings.add(taskId);
                       //取到当前隐患的详细信息
                       String sql = "SELECT yh.YHMS,li.LINE_NAME,li.SECTION,yh.ID" +
                               "        from KH_YH_HISTORY yh" +
                               "            LEFT JOIN" +
                               "              CM_LINE li on li.ID = yh.LINE_ID" +
                               "                 WHERE yh.ID = (SELECT k.YH_ID FROM KH_SITE k LEFT JOIN KH_TASK kh ON kh.SITE_ID = k.ID" +
                               "                    WHERE kh.ID = ?"+strings.size()+")";
                       //取当前隐患的位置  去重
                       String sql3 = "SELECT distinct li.LINE_NAME,li.SECTION" +
                               "       from KH_YH_HISTORY yh" +
                               "          LEFT JOIN" +
                               "            CM_LINE li on li.ID = yh.LINE_ID" +
                               "             WHERE yh.ID = (SELECT k.YH_ID FROM KH_SITE k LEFT JOIN KH_TASK kh ON kh.SITE_ID = k.ID" +
                               "             WHERE kh.ID = ?"+strings.size()+")";
                       //获取任务详情  OPERATE_NAME   三种情况
                       //  人员信息上传情况
                       String khsql1 = "SELECT DISTINCT PROCESS_NAME as OPERATE_NAME,TASK_ID,(SELECT min(CREATE_TIME) FROM PICTURE_KH " +
                               "    WHERE TASK_ID = ?"+strings.size()+" AND FILE_TYPE = 1 and PROCESS_ID = 1) AS START_TIME,1 AS PROID" +
                               "    FROM PICTURE_KH WHERE TASK_ID = ?"+strings.size()+" AND FILE_TYPE = 1 and PROCESS_ID = 1";

                       String khsql2 = "SELECT DISTINCT PROCESS_NAME as OPERATE_NAME,TASK_ID,(SELECT min(CREATE_TIME) FROM PICTURE_KH " +
                               "    WHERE TASK_ID = ?"+strings.size()+" AND FILE_TYPE = 1 and PROCESS_ID = 2) AS START_TIME,2 AS PROID" +
                               "    FROM PICTURE_KH WHERE TASK_ID = ?"+strings.size()+" AND FILE_TYPE = 1 and PROCESS_ID = 2";
                       String khsql3 = "SELECT DISTINCT PROCESS_NAME as OPERATE_NAME,TASK_ID,(SELECT min(CREATE_TIME) FROM PICTURE_KH " +
                               "    WHERE TASK_ID = ?"+strings.size()+" AND FILE_TYPE = 1 and PROCESS_ID = 3) AS START_TIME,3 AS PROID" +
                               "    FROM PICTURE_KH WHERE TASK_ID = ?"+strings.size()+" AND FILE_TYPE = 1 and PROCESS_ID = 3";

                       List<Map<String, Object>> m1 = this.execSql(khsql1, strings);
                       if(null != m1 && m1.size()>0){
                           maps2.add(m1.get(0));
                       }
                       List<Map<String, Object>> m2 = this.execSql(khsql2, strings);
                       if(null != m2 && m2.size()>0){
                           maps2.add(m2.get(0));
                       }
                       List<Map<String, Object>> m3 = this.execSql(khsql3, strings);
                       if(null != m3 && m3.size()>0){
                           maps2.add(m3.get(0));
                       }

                       maps = this.execSql(sql, strings.toArray());
                       maps3 = this.execSql(sql3, strings.toArray());
                       LOGGER.info("看护任务详情查询");
                   }
               }


           }

       }catch (Exception e){
            LOGGER.error("查询任务隐患错误"+e.getMessage());
           return WebApiResponse.erro("查询任务隐患错误"+e.getMessage());
       }
        LOGGER.info("查询任务隐患信息");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        Map<String, Object> stringMapHashMap = new HashMap<>();
        ArrayList<Object> strings1 = new ArrayList<>();
        HashMap<String, Object> OPERATE_NAME = new HashMap<>();
        OPERATE_NAME.put("OPERATE_NAME","暂无照片信息");
        OPERATE_NAME.put("START_TIME",simpleDateFormat.format(new Date()));
        OPERATE_NAME.put("END_TIME",simpleDateFormat.format(new Date()));
            strings1.add(OPERATE_NAME);
        ArrayList<Object> strings2 = new ArrayList<>();
        HashMap<String, Object> LINE_NAME = new HashMap<>();
        LINE_NAME.put("LINE_NAME","暂无照片信息");
            strings2.add(LINE_NAME);
        ArrayList<Object> strings3 = new ArrayList<>();
        HashMap<String, Object> YHMS = new HashMap<>();
        YHMS.put("YHMS","暂无照片信息");
             strings3.add(YHMS);
        // OPERATE_NAME  进度    LINE_NAME 位置   YHMS 隐患
        stringMapHashMap.put("YH",maps.size()>0?maps:strings3);
        stringMapHashMap.put("XQ",maps2.size()>0?maps2:strings1);
        stringMapHashMap.put("THWZ",maps3.size()>0?maps3:strings2);
        return WebApiResponse.success(stringMapHashMap);
    }


    @Transactional
    public void checkOff(String id) {
        repository.xsTaskUpdate(id);
    }


    public WebApiResponse findWorking(String currentUserId) {
        if(null == currentUserId || "".equals(currentUserId)){
            return WebApiResponse.erro("参数错误currentUserId = "+currentUserId);
        }
        String sql = "";
        List<Map<String, Object>> maps = null;
       try {
           String deptId = redisUtil.findTDIDByUserId(currentUserId);

           if(null != deptId && !"".equals(deptId)){
               sql = "SELECT * " +
                       "     FROM WORKING_TIMED WHERE DEPT_ID = '"+deptId+"'";
           }
           //公司本部情况
           if("40283781608b848701608b85d3700000".equals(deptId)){
               sql = "SELECT * FROM WORKING_TIMED " ;
           }
            maps = this.execSql(sql);
           LOGGER.info("查询倒班信息成功");
       }catch (Exception e){
           LOGGER.error("查询排班情况失败"+e.getMessage());
           return WebApiResponse.erro("查询排班情况失败"+e.getMessage());
       }

       return WebApiResponse.success(maps);

    }

    public WebApiResponse updateWorkings(String currentUserId, String deptId, String startTime, String endTime, String dayUserId, String nightUserId) {

        if(null == deptId || "".equals(deptId) ){
            return WebApiResponse.erro("参数错误 deptId="+deptId);
        }
        try {
            //修改倒班信息
            timedConfigRepository.updateWorkings(deptId,startTime,endTime,dayUserId,nightUserId);
            LOGGER.info("修改倒班信息成功");
        }catch (Exception e){
            LOGGER.error("修改倒班信息失败"+e.getMessage());
            return WebApiResponse.erro("修改倒班信息失败"+e.getMessage());
        }

        return WebApiResponse.success("");
    }









    /**
     *   供poi使用
     * @param taskType 任务类型  条件查询使用0
     * @return
     */
    public List<Map<String, Object>> usePoi( String taskType ,String userId,String userName,String TD,String targetType){
        /**
         *   所有权限	    0
         公司本部权限	1
         属地单位权限	2
         外协队伍权限	3
         组织权限	    4
         个人权限	    5

         */
        List<Object> list = new ArrayList<>();
        //sql 中 拉取数据为刷新时间至刷新时间前10分钟
        String sql = "";
        List<Map<String, Object>> pageResult = null;
        try {
            if(null == userId || "".equals(userId)){
                return null;
            }

            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", userId);
            if(null != userInformation1 && !"".equals(userInformation1)){
                JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
                String roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
                String deptid = (String) jsonObject1.get("DEPTID");//当角色权限为3时需要只显示本单位的任务信息
                if(null != roletype && !"".equals(roletype)){//证明当前用户信息正常
                    int i = Integer.parseInt(roletype);
                    switch (i){
                        case 0 :{//一级单位   显示三天周期抽查的任务
                         /*sql = " SELECT    TASKID," +
                                 "  ID," +
                                 "  CREATETIME," +
                                 "  USER_ID," +
                                 "  TASKNAME," +
                                 "  TASKTYPE,CHECKSTATUS ,TARGETSTATUS" +
                                 "   FROM TIMED_TASK" +*/
                            sql = " SELECT  DISTINCT t.TASKID," +
                                    "    t.ID," +
                                    "    t.CREATETIME," +
                                    "    t.USER_ID," +
                                    "    t.TASKNAME," +
                                    "     t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did" +
                                    "     FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                    "    LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                                    "   WHERE t.CREATETIME > ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual)" +
                                    "         AND t.STATUS = 0 AND t.THREEDAY = 1  ";
                            break;
                        }case 1 :{//二级单位   显示全部周期为两小时的任务
                            if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功，进入流程
                                list.add(deptid);
                                sql = " SELECT  DISTINCT t.TASKID," +
                                        "    t.ID," +
                                        "    t.CREATETIME," +
                                        "    t.USER_ID," +
                                        "    t.TASKNAME," +
                                        "     t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did" +
                                        "     FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                        "    LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                                        "     WHERE t.CREATETIME > ( SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)    " +
                                        "    FROM TIMED_TASK  WHERE THREEDAY = 0 )     AND t.STATUS = 0 AND t.THREEDAY = 0  AND d.ID  = ?"+list.size();
                            }else {
                                LOGGER.error("获取当前用户单位信息失败");
                                return null;
                            }
                            break;
                        }case 2 :{//三级单位   只显示本单位的任务

                            if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功，进入流程
                                list.add(deptid);
                                sql = " SELECT DISTINCT t.TASKID," +
                                        "    t.ID," +
                                        "    t.CREATETIME," +
                                        "    t.USER_ID," +
                                        "    t.TASKNAME," +
                                        "     t.TASKTYPE,t.CHECKSTATUS ,t.TARGETSTATUS,d.ID as did" +
                                        "     FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID" +
                                        "    LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                                        "     WHERE t.CREATETIME > ( SELECT max(CREATETIME) -  600   / (1 * 24 * 60 * 60)        FROM TIMED_TASK  WHERE THREEDAY = 0 )     AND t.STATUS = 0 AND t.THREEDAY = 0  AND d.ID  = ?"+list.size();
                            }else {
                                LOGGER.error("获取当前用户单位信息失败");
                                return null;
                            }
                            break;
                        }default:{
                            LOGGER.error("获取登录人权限失败,当前权限未知");
                            return null;
                        }
                    }
                }
            }
            if(taskType!=null && !"".equals(taskType.trim())){// 判断当前任务类型  巡视1   看护2  看护稽查3  巡视稽查4
                sql+= "  AND t.TASKTYPE = "+taskType;
            }

            //查询责任人
            if(null != userName && !"".equals(userName)){
                sql += "  AND  t.USER_ID in (SELECT ru.ID from RZTSYSUSER ru WHERE ru.REALNAME LIKE '%"+userName+"%')";
            }
            //通道单位
            if(null != TD && !"".equals(TD)){
                sql += "  AND  d.ID = '"+TD+"'";
            }
            //任务状态
            if(null != targetType && !"".equals(targetType)){
                sql += "  AND  t.TARGETSTATUS =  "+targetType;
            }

            if(null != sql && !"".equals(sql)){
                sql +="   ORDER BY CREATETIME DESC     ";
            }

            pageResult = this.execSql( sql, list.toArray());
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
                    if(list.size()>0){
                        next.put("LINE_ID",maps.get(0).get("LINE_ID"));
                    }
                }else if (tasktype!=null && tasktype.equals("2")){ //看护
                    String sqlll = "SELECT LINE_ID FROM KH_YH_HISTORY WHERE TASK_ID =?1";
                    List<Map<String, Object>> maps = execSql(sqlll, taskid);
                    if(maps.size()>0){
                        next.put("LINE_ID",maps.get(0).get("LINE_ID"));
                    }
                }else if (tasktype!=null && tasktype.equals("3")){ //看护稽查

                }else if (tasktype!=null && tasktype.equals("4")){ //巡视稽查

                }else {
                    LOGGER.error("查询类型不明确");
                    return null;
                }
                Object userInformation = null;
                String userID =(String)next.get("USER_ID");
                if(null !=userID && !"".equals(userID)){
                    userInformation = hashOperations.get("UserInformation", userID);
                }

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
            return null;
        }
        return pageResult;
    }









}
