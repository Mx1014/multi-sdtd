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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 李成阳
 * 2018/3/12
 * 后台稽查任务
 */
@Service
public class TaskCheckService extends CurdService<TimedTask,XSZCTASKRepository>{
    protected static Logger LOGGER = LoggerFactory.getLogger(TaskCheckService.class);
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 进行中查询
     * @param currentUserId
     * @return
     */
    public WebApiResponse taskCheckConduct(String currentUserId,String page,String size) {
        /**
         *   所有权限	0
         公司本部权限	    1
         属地单位权限	    2
         外协队伍权限	    3
         组织权限	    4
         个人权限	    5

         */
        List<Map<String, Object>> maps = new ArrayList<>();
        try {
            String sqlByDept = "";

            ArrayList<String> strings = new ArrayList<>();
            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", currentUserId);
            if(null != userInformation1 && !"".equals(userInformation1)){
                JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
                String roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
                String deptid = (String) jsonObject1.get("DEPTID");//当角色权限为3时需要只显示本单位的任务信息
                sqlByDept = "SELECT" +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                        "     AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "      FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "     AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND t.TASKTYPE = 2) AS kh," +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                        "   AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "    FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "   AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND t.TASKTYPE = 1) AS xs," +
                        "  (SELECT count(1)" +
                        " FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                        "  AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "   FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "  AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND t.TASKTYPE = 2 AND t.STATUS = 1) AS khcomplete," +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                        "    AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "     FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "    AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND t.TASKTYPE = 1 AND t.STATUS = 1) AS xscomplete," +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                        "   AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "   FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "   AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) ) AS jc," +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                        "    AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "      FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "    AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND t.STATUS = 1) AS jccomplete," +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                        "   WHERE u.ID IS  NOT  NULL" +
                        "         AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "          FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "         AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND t.TASKTYPE = 2 AND d.CREATE_TIME >= t.CREATETIME" +
                        "         AND d.ID IS NOT  NULL) AS khproblem," +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                        "   WHERE u.ID IS  NOT  NULL" +
                        "         AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "                              FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "         AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND t.TASKTYPE = 1 AND d.CREATE_TIME >= t.CREATETIME" +
                        "         AND d.ID IS NOT  NULL) AS xsproblem," +
                        "  (SELECT count(1)" +
                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                        "   WHERE u.ID IS  NOT  NULL" +
                        "         AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                        "                              FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate))" +
                        "         AND u.DEPTID = ?1 AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND d.CREATE_TIME >= t.CREATETIME" +
                        "         AND d.ID IS NOT  NULL) AS jcproblem," +
                        "  (SELECT max(CREATE_TIME)" +
                        "   FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 AND trunc(CHECK_TIME) = trunc(sysdate)) AS tasktime," +
                        "     ?1 AS deptid,(SELECT DEPTNAME FROM RZTSYSDEPARTMENT WHERE ID = ?1) AS deptname" +
                        "   FROM dual";
                if(null != roletype && !"".equals(roletype)){//证明当前用户信息正常
                        if("0".equals(roletype) || "1".equals(roletype)){//一级单位 或公司本部  显示所有后台稽查
                            //查询所有单位
                            String deptSql = "SELECT ID FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
                            List<Map<String, Object>> maps1 = this.execSql(deptSql, null);
                            for (Map<String, Object> map : maps1) {
                                ArrayList<String> strings1 = new ArrayList<>();
                                strings1.add(map.get("ID").toString());
                                Map<String, Object> map1 = this.execSqlSingleResult(sqlByDept, strings1.toArray());
                                maps.add(map1);
                            }

                            String sqlByDept1 = "   SELECT" +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                    "     AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "      FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "     AND t.THREEDAY = 1 AND t.TASKTYPE = 2) AS kh," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                    "   AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "    FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "    AND t.THREEDAY = 1 AND t.TASKTYPE = 1) AS xs," +
                                    "  (SELECT count(1)" +
                                    " FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                    "  AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "   FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "  AND t.THREEDAY = 1 AND t.TASKTYPE = 2 AND t.STATUS = 1) AS khcomplete," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                    "    AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "     FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "     AND t.THREEDAY = 1 AND t.TASKTYPE = 1 AND t.STATUS = 1) AS xscomplete," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                    "   AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "   FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "  AND t.THREEDAY = 1 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) ) AS jc," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                    "    AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "      FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "    AND t.THREEDAY = 1 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND t.STATUS = 1) AS jccomplete," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                    "     LEFT JOIN CHECK_RESULT r ON r.CHECK_DETAIL_ID = d.ID" +
                                    "   WHERE u.ID IS  NOT  NULL" +
                                    "         AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "          FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "        AND t.THREEDAY = 1 AND t.TASKTYPE = 2 AND d.CREATE_TIME >= t.CREATETIME" +
                                    "         AND d.ID IS NOT  NULL AND r.ID IS NOT NULL) AS khproblem," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                    "     LEFT JOIN CHECK_RESULT r ON r.CHECK_DETAIL_ID = d.ID" +
                                    "   WHERE u.ID IS  NOT  NULL" +
                                    "         AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                              FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "         AND t.THREEDAY = 1 AND t.TASKTYPE = 1 AND d.CREATE_TIME >= t.CREATETIME" +
                                    "         AND d.ID IS NOT  NULL AND r.ID IS NOT NULL) AS xsproblem," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                    "     LEFT JOIN CHECK_RESULT r ON r.CHECK_DETAIL_ID = d.ID" +
                                    "   WHERE u.ID IS  NOT  NULL" +
                                    "         AND t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                              FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 )" +
                                    "          AND t.THREEDAY = 1 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND d.CREATE_TIME >= t.CREATETIME" +
                                    "         AND d.ID IS NOT  NULL  AND r.ID IS NOT NULL) AS jcproblem," +
                                    "  (SELECT max(CREATE_TIME)" +
                                    "   FROM TIMED_TASK_RECORD WHERE DEPT_ID = ?1 ) AS tasktime," +
                                    "     ?1 AS deptid,(SELECT DEPTNAME FROM RZTSYSDEPARTMENT WHERE ID = ?1) AS deptname" +
                                    "   FROM dual";
                            ArrayList<String> listBybj = new ArrayList<>();
                            listBybj.add("40283781608b848701608b85d3700000");
                            Map<String, Object> map = this.execSqlSingleResult(sqlByDept1, listBybj.toArray());
                            maps.add(map);

                        }if("2".equals(roletype)){//属地单位   只显示本单位的任务
                            if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功
                                //查询本单位今天的抽查任务
                                strings.add(deptid);
                                 maps = this.execSql(sqlByDept, strings.toArray());
                            }else {
                                LOGGER.error("获取当前用户单位信息失败");
                                return WebApiResponse.erro("获取当前用户单位信息失败");
                            }
                    }
                }
            }


        }catch (Exception e){
            LOGGER.error("后台稽查任务查询失败"+e.getMessage());
            return WebApiResponse.erro("后台稽查任务查询失败"+e.getMessage());
        }
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        if((null != page && !"".equals(page)) && (null != size && !"".equals(size))){
            int Page = Integer.parseInt(page);
            int Size = Integer.parseInt(size);
            if(0 == Page){
                if(Size <= maps.size()){
                    stringObjectHashMap.put("data",maps.subList(0,Size));
                }else {
                    stringObjectHashMap.put("data",maps);
                }

            }else{
                if((Page * Size) > maps.size()){
                    ArrayList<Object> objects = new ArrayList<>();
                    stringObjectHashMap.put("data",objects);
                }else{
                    stringObjectHashMap.put("data",maps.subList(Page * Size ,(Page * Size + Size) > maps.size()?maps.size():(Page * Size + Size) ));
                }
            }
        }
        stringObjectHashMap.put("totalElements",maps.size());
        stringObjectHashMap.put("page",page);
        stringObjectHashMap.put("size",size);
        return WebApiResponse.success(stringObjectHashMap);


    }

    /**
     * 已完成
     * @param currentUserId
     * @return
     */
    public WebApiResponse taskCheckComplete(String currentUserId,String page,String size) {
        /**
         *   所有权限	    0
         公司本部权限	1
         属地单位权限	2
         外协队伍权限	3
         组织权限	    4
         个人权限	    5

         */
        ArrayList<Object> list = new ArrayList<>();
        try {
            String sqlByDept = "";
            String check_time = "";

            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", currentUserId);
            if(null != userInformation1 && !"".equals(userInformation1)){
                JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
                String roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
                String deptid = (String) jsonObject1.get("DEPTID");//当角色权限为3时需要只显示本单位的任务信息

                if(null != roletype && !"".equals(roletype)){//证明当前用户信息正常
                    if("0".equals(roletype) || "1".equals(roletype)){//一级单位 或公司本部  显示所有后台稽查
                        //查询所有单位
                        String deptSql = "SELECT ID FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
                        List<Map<String, Object>> maps1 = this.execSql(deptSql, null);
                        for (Map<String, Object> map : maps1) {
                            deptid = map.get("ID").toString();
                            String checkTimeSql = "SELECT CHECK_TIME" +
                                    "     FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) = trunc(sysdate) AND DEPT_ID = '"+deptid+"' AND COMPLETE = TASKS";
                            List<Map<String, Object>> maps2 = this.execSql(checkTimeSql);
                            for (Map<String, Object> map1 : maps2) {
                                // 当前任务抽查时间
                                check_time = map1.get("CHECK_TIME").toString();
                                if(null != check_time && check_time.length() > 0){
                                    check_time = check_time.substring(0,check_time.length()-2);
                                }
                                sqlByDept = "SELECT" +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "     AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 2) AS kh," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "   AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 1) AS xs," +
                                        "  (SELECT count(1)" +
                                        " FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                    AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                    AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "  AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 2 AND t.STATUS = 1) AS khcomplete," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "    AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 1 AND t.STATUS = 1) AS xscomplete," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "   AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) ) AS jc," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "    AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND t.STATUS = 1) AS jccomplete," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                        "   WHERE u.ID IS  NOT  NULL" +
                                        "         AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "         AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "         AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 2 AND d.CREATE_TIME >= t.CREATETIME" +
                                        "         AND d.ID IS NOT  NULL) AS khproblem," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                        "   WHERE u.ID IS  NOT  NULL" +
                                        "         AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "         AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "         AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 1 AND d.CREATE_TIME >= t.CREATETIME" +
                                        "         AND d.ID IS NOT  NULL) AS xsproblem," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                        "   WHERE u.ID IS  NOT  NULL" +
                                        "         AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "         AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "         AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND d.CREATE_TIME >= t.CREATETIME" +
                                        "         AND d.ID IS NOT  NULL) AS jcproblem," +
                                        "  '"+deptid+"' AS deptid," +
                                        "  (SELECT DEPTNAME FROM RZTSYSDEPARTMENT WHERE ID = '"+deptid+"') AS deptname," +
                                        "  to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') as TASKTIME" +
                                        "   FROM dual";
                                Map<String, Object> map2 = this.execSqlSingleResult(sqlByDept);
                                list.add(map2);
                                LOGGER.info("查询所有单位后台稽查任务成功");
                            }


                        }


                        //查询北京局最新的稽查是否完成
                        String complete = "SELECT max(CREATE_TIME)  AS TIME " +
                                "   FROM TIMED_TASK_RECORD  WHERE DEPT_ID = '40283781608b848701608b85d3700000'" +
                                "   AND TASKS = COMPLETE AND CREATE_TIME = (SELECT max(CREATE_TIME)" +
                                "   FROM TIMED_TASK_RECORD  WHERE DEPT_ID = '40283781608b848701608b85d3700000')";
                        List<Map<String, Object>> maps = this.execSql(complete);
                        //北京局最新的稽查任务以完成  继续查询  未完成时不需要查询
                        if(null != maps.get(0) && null != maps.get(0).get("TIME")){
                            //查询北京局最新完成的任务
                            String sql = "   SELECT" +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "   WHERE u.ID IS NOT NULL  AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "       t.THREEDAY = 1 AND t.TASKTYPE = 2)                     AS kh," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "   WHERE u.ID IS NOT NULL AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "         t.THREEDAY = 1 AND t.TASKTYPE = 1)                     AS xs," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "   WHERE u.ID IS NOT NULL AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "          t.THREEDAY = 1 AND t.TASKTYPE = 2 AND t.STATUS =" +
                                    "                                                                                                 1)               AS khcomplete," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "   WHERE u.ID IS NOT NULL AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "          t.THREEDAY = 0 AND t.TASKTYPE = 1 AND t.STATUS =  1)               AS xscomplete," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "   WHERE u.ID IS NOT NULL AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "          t.THREEDAY = 1 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4)) AS jc," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "   WHERE u.ID IS NOT NULL AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "         t.THREEDAY = 1 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND" +
                                    "         t.STATUS =" +
                                    "         1)                                                                                                       AS jccomplete," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                    "     LEFT JOIN CHECK_RESULT r ON r.CHECK_DETAIL_ID = d.ID AND r.ID IS NOT NULL" +
                                    "   WHERE u.ID IS NOT NULL  AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "          t.THREEDAY = 1 AND t.TASKTYPE = 2 AND" +
                                    "         d.CREATE_TIME >= t.CREATETIME AND d.ID IS NOT" +
                                    "                                           NULL)                                                                  AS khproblem," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                    "     LEFT JOIN CHECK_RESULT r ON r.CHECK_DETAIL_ID = d.ID AND r.ID IS NOT NULL" +
                                    "   WHERE u.ID IS NOT NULL AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "          t.THREEDAY = 1 AND t.TASKTYPE = 1 AND" +
                                    "         d.CREATE_TIME >= t.CREATETIME AND d.ID IS NOT" +
                                    "                                           NULL)                                                                  AS xsproblem," +
                                    "  (SELECT count(1)" +
                                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                    "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                    "     LEFT JOIN CHECK_RESULT r ON r.CHECK_DETAIL_ID = d.ID AND d.ID IS NOT  NULL" +
                                    "   WHERE u.ID IS NOT NULL AND" +
                                    "         t.CREATETIME >= (SELECT max(CREATE_TIME)" +
                                    "                          FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000') AND" +
                                    "          t.THREEDAY = 1 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND" +
                                    "         d.CREATE_TIME >= t.CREATETIME AND d.ID IS NOT" +
                                    "                                           NULL)                                                                  AS jcproblem," +
                                    "  '40283781608b848701608b85d3700000'                                                                              AS deptid," +
                                    "  (SELECT DEPTNAME" +
                                    "   FROM RZTSYSDEPARTMENT" +
                                    "   WHERE ID =" +
                                    "         '40283781608b848701608b85d3700000')                                                                      AS deptname," +
                                    "  (SELECT max(CREATE_TIME)" +
                                    "   FROM TIMED_TASK_RECORD WHERE DEPT_ID = '40283781608b848701608b85d3700000')   AS TASKTIME" +
                                    "   FROM dual";
                            Map<String, Object> map1 = this.execSqlSingleResult(sql);
                            //放到完成的集合中
                            list.add(map1);
                        }





                    }if("2".equals(roletype)){//属地单位   只显示本单位的任务
                        if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功
                            //查询本单位今天的抽查任务
                        String checkTimeSql = "SELECT CHECK_TIME" +
                                "     FROM TIMED_TASK_RECORD WHERE trunc(CHECK_TIME) = trunc(sysdate) AND DEPT_ID = '"+deptid+"'  AND COMPLETE = TASKS";
                            List<Map<String, Object>> maps1 = this.execSql(checkTimeSql);
                            for (Map<String, Object> map : maps1) {
                                // 当前任务抽查时间
                                check_time = map.get("CHECK_TIME").toString();
                                if(null != check_time && check_time.length() > 0){
                                    check_time = check_time.substring(0,check_time.length()-2);
                                }
                                sqlByDept = "SELECT" +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "     AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 2) AS kh," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "   AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 1) AS xs," +
                                        "  (SELECT count(1)" +
                                        " FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                    AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                    AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "  AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 2 AND t.STATUS = 1) AS khcomplete," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "    AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 1 AND t.STATUS = 1) AS xscomplete," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "   AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) ) AS jc," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID WHERE u.ID IS  NOT  NULL" +
                                        "                                                                      AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "                                                                      AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "    AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND t.STATUS = 1) AS jccomplete," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                        "   WHERE u.ID IS  NOT  NULL" +
                                        "         AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "         AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "         AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 2 AND d.CREATE_TIME >= t.CREATETIME" +
                                        "         AND d.ID IS NOT  NULL) AS khproblem," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                        "   WHERE u.ID IS  NOT  NULL" +
                                        "         AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "         AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "         AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND t.TASKTYPE = 1 AND d.CREATE_TIME >= t.CREATETIME" +
                                        "         AND d.ID IS NOT  NULL) AS xsproblem," +
                                        "  (SELECT count(1)" +
                                        "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID" +
                                        "     LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                                        "   WHERE u.ID IS  NOT  NULL" +
                                        "         AND t.CREATETIME <= to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss')" +
                                        "         AND t.CREATETIME >= (to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') - (600 / (1 * 24 * 60 * 60)))" +
                                        "         AND u.DEPTID = '"+deptid+"' AND t.THREEDAY = 0 AND (t.TASKTYPE = 3 OR t.TASKTYPE = 4) AND d.CREATE_TIME >= t.CREATETIME" +
                                        "         AND d.ID IS NOT  NULL) AS jcproblem," +
                                        "  '"+deptid+"' AS deptid," +
                                        "  (SELECT DEPTNAME FROM RZTSYSDEPARTMENT WHERE ID = '"+deptid+"') AS deptname," +
                                        "  to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') as TASKTIME" +
                                        "   FROM dual";
                                Map<String, Object> map1 = this.execSqlSingleResult(sqlByDept);
                                list.add(map1);

                            }
                            LOGGER.info("查询后台稽查任务成功");

                        }else {
                            LOGGER.error("获取当前用户单位信息失败");
                            return WebApiResponse.erro("获取当前用户单位信息失败");
                        }
                    }
                }
            }


        }catch (Exception e){
            LOGGER.error("后台稽查任务查询失败"+e.getMessage());
            return WebApiResponse.erro("后台稽查任务查询失败"+e.getMessage());
        }

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        if((null != page && !"".equals(page)) && (null != size && !"".equals(size))){
            int Page = Integer.parseInt(page);
            int Size = Integer.parseInt(size);
            if(0 == Page){
                if(Size <= list.size()){
                    stringObjectHashMap.put("data",list.subList(0,Size));
                }else {
                    stringObjectHashMap.put("data",list);
                }

            }else{
                if((Page * Size) > list.size()){
                    ArrayList<Object> objects = new ArrayList<>();
                    stringObjectHashMap.put("data",objects);
                }else{
                    stringObjectHashMap.put("data",list.subList(Page * Size ,(Page * Size + Size) > list.size()?list.size():(Page * Size + Size) ));
                }
            }
        }
        stringObjectHashMap.put("totalElements",list.size());
        stringObjectHashMap.put("page",page);
        stringObjectHashMap.put("size",size);
        return WebApiResponse.success(stringObjectHashMap);

    }

    /**
     * @param flag         标识 0有问题  1以审核  2全部
     * @param taskType    任务类型
     * @param deptid     部门
     * @param taskTime  任务时间
     * @return
     */
    public WebApiResponse findCompleteTaskByFlag( String flag, String taskType, String deptid, String taskTime,Integer page,Integer size) {

        if(null == flag || "".equals(flag)){
            LOGGER.error("查询任务稽查详情失败 flag  = "+flag);
            return WebApiResponse.erro("查询任务稽查详情失败 flag  = "+flag);
        }
        if(null == taskType || "".equals(taskType)){
            LOGGER.error("查询任务稽查详情失败 taskType  = "+taskType);
            return WebApiResponse.erro("查询任务稽查详情失败 taskType  = "+taskType);
        }
        PageRequest pageRequest = new PageRequest(page,size);
        Page<Map<String, Object>> maps1 = null;
        String sql = "";
        String dept = "";
        try {
            String roleAuth = "0";
            if (null != deptid && "40283781608b848701608b85d3700000".equals(deptid)) {
                roleAuth = "1";
            }else{
                 dept = " AND u.DEPTID = '"+deptid+"'";
            }
            String type = "";
            if("3".equals(taskType) || "4".equals(taskType)){
                type += "AND (TASKTYPE = 3) OR ( TASKTYPE = 4) ";
            }else{
                type += "AND TASKTYPE = "+taskType+" ";
            }

             sql = "  SELECT t.TASKTYPE,t.CREATETIME,t.STATUS,t.USER_ID,t.TASKNAME,u.REALNAME,u.DEPTID " +
                    "   FROM TIMED_TASK t LEFT JOIN RZTSYSUSER u ON t.USER_ID = u.ID and u.ID IS NOT NULL" +
                    "  LEFT JOIN CHECK_DETAIL d ON d.QUESTION_TASK_ID = t.TASKID" +
                    "  WHERE   t.CREATETIME >to_date('"+taskTime+"','YYYY-MM-DD HH24:mi:ss')" +
                    "  AND t.CREATETIME <= (to_date('"+taskTime+"','YYYY-MM-DD HH24:mi:ss') +  (600 / (1 * 24 * 60 * 60)))" +
                    "  "+dept+"  "+type+"  AND t.THREEDAY = "+roleAuth+" ";
            if("0".equals(flag)){//有问题
                sql += "  AND STATUS = 1 AND D.ID IS NOT NULL ";
            }
            if("1".equals(flag)){//已审核
                sql += "  AND STATUS = 1 ";
            }
            if("2".equals(flag)){//全部
                sql += "";
            }
            if("3".equals(flag)){//未审核
                sql += "  AND STATUS = 0 ";
            }

            maps1 = this.execSqlPage(pageRequest, sql, null);
        }catch (Exception e){
        LOGGER.error(e.getMessage());
        return WebApiResponse.erro(e.getMessage());
        }



        return WebApiResponse.success(maps1);
    }

    /**
     * 根据任务id和任务类型查询任务的详细信息
     * @param taskId
     * @param taskType
     * @return
     */
    public WebApiResponse findTaskInfoByTaskId(String taskId, String taskType)  {

        if(null == taskType || "".equals(taskType)){
            return WebApiResponse.erro("参数错误  taskType = "+taskType);
        }
        if(null == taskId || "".equals(taskId)){
            return WebApiResponse.erro("参数错误  taskId = "+taskId);
        }
        String sql = "";
        String picSql = "";
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        try {
        if("1".equals(taskType)){//巡视
            sql = "   SELECT x.TASK_NAME,d.DEPTNAME,c.COMPANYNAME,u.REALNAME,u.PHONE,x.PLAN_XS_NUM," +
                    "  x.REAL_XS_NUM,x.REAL_START_TIME,x.REAL_END_TIME,x.PLAN_START_TIME,x.PLAN_END_TIME" +
                    "   FROM XS_ZC_TASK x LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = x.TD_ORG" +
                    "  LEFT JOIN RZTSYSCOMPANY c ON c.ID = x.WX_ORG" +
                    "  LEFT JOIN RZTSYSUSER u ON u.ID = x.CM_USER_ID WHERE x.ID = '"+taskId+"'";
            map = this.execSqlSingleResult(sql);
            stringObjectHashMap.put("task",map);
            picSql = "SELECT ID,FILE_PATH,FILE_SMALL_PATH,PROCESS_NAME" +
                    "   FROM PICTURE_TOUR WHERE TASK_ID = '"+taskId+"' AND FILE_TYPE = 1";
            List<Map<String, Object>> maps = this.execSql(picSql);
            stringObjectHashMap.put("pic",maps);

        }
        if("2".equals(taskType)){//看护
            sql = "  SELECT k.TASK_NAME,k.PLAN_START_TIME,k.PLAN_END_TIME,k.REAL_START_TIME,k.REAL_END_TIME,k.SFQR_TIME," +
                    "  k.DDXC_TIME,k.WX_ORG,k.TDYW_ORG,k.WPQR_TIME,k.ZXYS_NUM,u.PHONE,u.REALNAME,k.ID AS taskid,u.ID as userid" +
                    "    FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON u.ID = k.USER_ID WHERE k.ID = '"+taskId+"'";
            map = this.execSqlSingleResult(sql);
            stringObjectHashMap.put("task",map);
            picSql = "SELECT ID,FILE_PATH,FILE_SMALL_PATH,PROCESS_NAME" +
                    "    FROM PICTURE_KH WHERE TASK_ID = '"+taskId+"' AND FILE_TYPE = 1";
            List<Map<String, Object>> maps = this.execSql(picSql);
            stringObjectHashMap.put("pic",maps);

        }
        if("3".equals(taskType)){//稽查
            sql = "   SELECT u.REALNAME,u.PHONE,d.DEPTNAME,c.COMPANYNAME,t.ID as taskid," +
                    "  u.ID as userid,t.REAL_START_TIME,t.REAL_END_TIME,t.PLAN_START_TIME,t.PLAN_END_TIME," +
                    "  t.TASK_NAME,t.CREATE_TIME,t.TASK_ID" +
                    "    FROM CHECK_LIVE_TASK t LEFT JOIN RZTSYSUSER u ON u.ID = t.USER_ID LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                    "  LEFT JOIN RZTSYSCOMPANY c ON c.ID = u.COMPANYID WHERE t.ID = '"+taskId+"' ";
            map = this.execSqlSingleResult(sql);
            stringObjectHashMap.put("task",map);
            picSql = "SELECT ID,FILE_PATH,FILE_SMALL_PATH,PROCESS_NAME" +
                    "   FROM PICTURE_JC WHERE TASK_ID = '"+taskId+"'  AND FILE_TYPE =1";
            List<Map<String, Object>> maps = this.execSql(picSql);
            stringObjectHashMap.put("pic",maps);
        }


        } catch (Exception e) {
            LOGGER.error("查询任务详情失败"+e.getMessage());
            return WebApiResponse.erro("查询任务详情失败"+e.getMessage());
        }
        LOGGER.info("查询任务详情成功");
        return WebApiResponse.success(stringObjectHashMap);
    }

    /**
     * 未开始列表
     * @param currentUserId
     * @param page
     * @param size
     * @return
     */
    public WebApiResponse findTaskWKS(String currentUserId, String page, String size) {
        ArrayList<Object> objects = new ArrayList<>();
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        try {
            Object userInformation1 = redisTemplate.opsForHash().get("UserInformation", currentUserId);
            if(null != userInformation1 && !"".equals(userInformation1)) {
                JSONObject jsonObject1 = JSONObject.parseObject(userInformation1.toString());
                String roletype = (String) jsonObject1.get("ROLETYPE");//用户权限信息  0 为1级单位  1为二级单位 2为单位 只展示当前单位的任务
                String deptid = (String) jsonObject1.get("DEPTID");//当角色权限为3时需要只显示本单位的任务信息

                if (null != roletype && !"".equals(roletype)) {//证明当前用户信息正常
                    if ("0".equals(roletype) || "1".equals(roletype)) {//一级单位 或公司本部  显示所有后台稽查
                        String sql = "SELECT ID,DEPTNAME" +
                                "     FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
                        List<Map<String, Object>> maps = this.execSql(sql);
                        for (Map<String, Object> map : maps) {
                            if( (null != map.get("DEPTNAME") && !"".equals(map.get("DEPTNAME").toString())) && (null != map.get("ID") && !"".equals(map.get("ID").toString())) ){
                                String deptname = map.get("DEPTNAME").toString();
                                String ID = map.get("ID").toString();
                                //查询进行中的时间
                                String timedSql = "SELECT to_char(max(CREATE_TIME),'HH24') AS TIME " +
                                        "      FROM TIMED_TASK_RECORD WHERE DEPT_ID = '"+ID+"'";
                                Map<String, Object> map1 = this.execSqlSingleResult(timedSql);
                                if(null != map1.get("TIME") && !"".equals(map1.get("TIME").toString())){
                                    int hours = Integer.parseInt(map1.get("TIME").toString());
                                    while (hours + 2 <= 22){
                                        hours += 2;
                                        HashMap<String, Object> stringObjectHashMap1 = new HashMap<>();
                                        stringObjectHashMap1.put("hours",hours);
                                        stringObjectHashMap1.put("dept",ID);
                                        stringObjectHashMap1.put("deptName",deptname);
                                        objects.add(stringObjectHashMap1);
                                    }

                                }
                            }
                        }

                    }else {//属地单位  查询本单位未开始后台稽查
                        String sql = "SELECT ID,DEPTNAME" +
                                "           FROM RZTSYSDEPARTMENT WHERE ID = '"+deptid+"'";
                        Map<String, Object> map = this.execSqlSingleResult(sql);
                        if(null != map.get("DEPTNAME") && !"".equals(map.get("DEPTNAME").toString())){
                            String deptname = map.get("DEPTNAME").toString();
                            String timedSql = "SELECT to_char(max(CREATE_TIME),'HH24') AS TIME " +
                                    "      FROM TIMED_TASK_RECORD WHERE DEPT_ID = '"+deptid+"'";
                            Map<String, Object> map1 = this.execSqlSingleResult(timedSql);
                            if(null != map1.get("TIME") && !"".equals(map1.get("TIME").toString())){
                                int hours = Integer.parseInt(map1.get("TIME").toString());
                                while (hours + 2 <= 22){
                                    hours += 2;
                                    HashMap<String, Object> stringObjectHashMap1 = new HashMap<>();
                                    stringObjectHashMap1.put("hours",hours);
                                    stringObjectHashMap1.put("dept",deptid);
                                    stringObjectHashMap1.put("deptName",deptname);
                                    objects.add(stringObjectHashMap1);
                                }

                            }

                        }
                    }
                }
            }


            if((null != page && !"".equals(page)) && (null != size && !"".equals(size))){
                int Page = Integer.parseInt(page);
                int Size = Integer.parseInt(size);
                if(0 == Page){
                    if(Size <= objects.size()){
                        stringObjectHashMap.put("data",objects.subList(0,Size));
                    }else {
                        stringObjectHashMap.put("data",objects);
                    }

                }else{
                    if((Page * Size) > objects.size()){
                        ArrayList<Object> data = new ArrayList<>();
                        stringObjectHashMap.put("data",data);
                    }else{
                        stringObjectHashMap.put("data",objects.subList(Page * Size ,(Page * Size + Size) > objects.size()?objects.size():(Page * Size + Size) ));
                    }
                }
            }
            stringObjectHashMap.put("totalElements",objects.size());
            stringObjectHashMap.put("page",page);
            stringObjectHashMap.put("size",size);

        } catch (Exception e) {
            LOGGER.error("后台稽查未开始查询失败"+e.getMessage());
            return WebApiResponse.erro("后台稽查未开始查询失败"+e.getMessage());
        }
        LOGGER.info("后台稽查未开始查询成功");
        return WebApiResponse.success(stringObjectHashMap);
    }
}
