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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public WebApiResponse taskCheckConduct(String currentUserId) {
        /**
         *   所有权限	    0
         公司本部权限	1
         属地单位权限	2
         外协队伍权限	3
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
        return WebApiResponse.success(maps);


    }

    public WebApiResponse taskCheckComplete(String currentUserId) {
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
                                    "     FROM TIMED_TASK_RECORD WHERE trunc(CHECK_TIME) = trunc(sysdate) AND DEPT_ID = '"+deptid+"'";
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
                                        "  to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') as checktime" +
                                        "   FROM dual";
                                Map<String, Object> map2 = this.execSqlSingleResult(sqlByDept);
                                list.add(map2);
                                LOGGER.info("查询所有单位后台稽查任务成功");
                            }

                        }
                    }if("2".equals(roletype)){//属地单位   只显示本单位的任务
                        if(null != deptid && !"".equals(deptid)){//当前用户单位信息获取成功
                            //查询本单位今天的抽查任务
                        String checkTimeSql = "SELECT CHECK_TIME" +
                                "     FROM TIMED_TASK_RECORD WHERE trunc(CHECK_TIME) = trunc(sysdate) AND DEPT_ID = '"+deptid+"'";
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
                                        "  to_date('"+check_time+"','YYYY-MM-dd HH24:mi:ss') as checktime" +
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
        return WebApiResponse.success(list);

    }
}
