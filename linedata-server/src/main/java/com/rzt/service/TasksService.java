package com.rzt.service;

import com.rzt.entity.KHYHHISTORY;
import com.rzt.repository.KHYHHISTORYRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 李成阳
 * 2018/1/31
 */
@Service
public class TasksService extends CurdService<KHYHHISTORY, KHYHHISTORYRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(TasksService.class);


    /**
     * 查询任务详情   总任务数  已完成数  未开始数
     * @return
     */
    public WebApiResponse findTaskInfo(){
        Map<String, Object> map = null;
  try {
      String sql = "     SELECT ((SELECT count(1)" +
              "         FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)) + (SELECT count(1)" +
              "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)) +" +
              "     (SELECT ((SELECT count(1) FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000') * 12) +1" +
              "       FROM dual) +" +
              "        (SELECT count(1)" +
              "         FROM CHECK_LIVE_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate))) AS SUM," +
              "     ((SELECT count(1)" +
              "     FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2)" +
              "     + (SELECT count(1)" +
              "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2) +" +
              "     (SELECT count(1) FROM TIMED_TASK_RECORD ti WHERE trunc(CHECK_TIME) = trunc(sysdate) AND ti.COMPLETE = TASKS) +" +
              "    (SELECT count(1)" +
              "    FROM CHECK_LIVE_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2)) AS COMPlETE," +
              "    ((SELECT count(1)" +
              "    FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0)" +
              "    + (SELECT count(1)" +
              "      FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0) +" +
              "     (SELECT" +
              "  (SELECT ((SELECT count(1) FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000') * 12) +1 FROM dual) -" +
              "  (SELECT count(1) FROM TIMED_TASK_RECORD ti WHERE trunc(CHECK_TIME) = trunc(sysdate)) +" +
              "  (SELECT count(1) FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000' OR ID = '402881e6603a69b801603a6ab1d70000')" +
              "   FROM dual  ) +" +
              "    (SELECT count(1)" +
              "    FROM CHECK_LIVE_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0)) AS WKS" +
              "    FROM dual";
      map = this.execSqlSingleResult(sql, null);
      LOGGER.info("任务详细查询成功");
  }catch (Exception e){
      LOGGER.error("任务详细查询失败"+e.getMessage());
      return WebApiResponse.erro("任务详细查询失败"+e.getMessage());
  }
  return WebApiResponse.success(map);
    }



    /**
     * 查看所有单位的任务详情  以单位分组
     * @return
     */
    public WebApiResponse findTasksGroupDept(){
        ArrayList<Object> list = new ArrayList<>();
        try {

            String deptSql = "SELECT ID,DEPTNAME" +
                    "           FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
            //所有的部门
            List<Map<String, Object>> maps = this.execSql(deptSql, null);
            for (Map<String, Object> map : maps) {
                //按部门查询并封装
                String id = map.get("ID").toString();
                String deptName = map.get("DEPTNAME").toString();
                String sql = "     SELECT ((SELECT count(1)" +
                        "        FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND TD_ORG = '"+id+"' )+  (SELECT count(1)" +
                        "    FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND YWORG_ID = '"+id+"' )+" +
                        "    (SELECT count(1)" +
                        "    FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "    WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND d.TDYW_ORGID = '"+id+"') +" +
                        "        (SELECT CASE WHEN ('"+id+"' = '402881e6603a69b801603a6ab1d70000')" +
                        "          THEN 1" +
                        "                WHEN ('"+id+"' != '402881e6603a69b801603a6ab1d70000')" +
                        "                  THEN 12" +
                        "                end" +
                        "         FROM dual)) AS SUM," +
                        "     ((SELECT count(1)" +
                        "     FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2   AND TD_ORG = '"+id+"' )+" +
                        "   (SELECT count(1)" +
                        "     FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2  AND YWORG_ID = '"+id+"')+" +
                        "   (SELECT count(1)" +
                        "   FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "   WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 2 AND d.TDYW_ORGID = '"+id+"') +" +
                        "      (SELECT count(1) FROM TIMED_TASK_RECORD WHERE DEPT_ID = '402881e6603a69b801603a6ab1d70000' AND COMPLETE = TASKS)) AS COMPlETE," +
                        "   ((SELECT count(1)" +
                        "   FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0   AND TD_ORG = '"+id+"' )+" +
                        "   (SELECT count(1)" +
                        "     FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0  AND YWORG_ID = '"+id+"')+" +
                        "     (SELECT count(1)" +
                        "     FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "     WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 0 AND d.TDYW_ORGID = '"+id+"') +" +
                        "    (SELECT  ((SELECT CASE WHEN ('"+id+"' = '402881e6603a69b801603a6ab1d70000')" +
                        "      THEN 1" +
                        "                      WHEN ('"+id+"' != '402881e6603a69b801603a6ab1d70000')" +
                        "                        THEN 12" +
                        "                      end" +
                        "               FROM dual) - ((SELECT count(1) FROM TIMED_TASK_RECORD t WHERE DEPT_ID = '"+id+"' AND trunc(t.CHECK_TIME) = trunc(sysdate)) +1))" +
                        "     from dual )) AS WKS" +
                        "    FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, null);
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("name",deptName);
                obj.put("id",id);
                obj.put("value",map1);
                list.add(obj);
            }
        }catch (Exception e){
            LOGGER.error("部门分组查询任务信息失败"+e.getMessage());
            return WebApiResponse.erro("部门分组查询任务信息失败"+e.getMessage());
        }
        LOGGER.info("部门分组查询任务信息成功");
        return WebApiResponse.success(list);
    }



    public WebApiResponse threeTasks(String deptId){
        HashMap<String, Object> obj = new HashMap<>();
        if(null == deptId || "".equals(deptId)){
            return WebApiResponse.erro("参数错误   deptId = "+deptId);
        }
        try {

            if(null !=  deptId && !"".equals(deptId)){
                //按部门查询并封装
                 //任务总数 饼图sql
                String sql = "     SELECT ((SELECT count(1)" +
                        "        FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND TD_ORG = '"+deptId+"' )+  (SELECT count(1)" +
                        "    FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)  AND YWORG_ID = '"+deptId+"' )+" +
                        "    (SELECT count(1)" +
                        "    FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "    WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND d.TDYW_ORGID = '"+deptId+"') +" +
                        "        (SELECT CASE WHEN ('"+deptId+"' = '402881e6603a69b801603a6ab1d70000')" +
                        "          THEN 1" +
                        "                WHEN ('"+deptId+"' != '402881e6603a69b801603a6ab1d70000')" +
                        "                  THEN 12" +
                        "                end" +
                        "         FROM dual)) AS SUM," +
                        "     ((SELECT count(1)" +
                        "     FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 2   AND TD_ORG = '"+deptId+"' )+" +
                        "   (SELECT count(1)" +
                        "     FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 2  AND YWORG_ID = '"+deptId+"')+" +
                        "   (SELECT count(1)" +
                        "   FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "   WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 2 AND d.TDYW_ORGID = '"+deptId+"') +" +
                        "      (SELECT count(1) FROM TIMED_TASK_RECORD WHERE DEPT_ID = '402881e6603a69b801603a6ab1d70000' AND COMPLETE = TASKS)) AS COMPlETE," +
                        "   ((SELECT count(1)" +
                        "   FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STAUTS = 0   AND TD_ORG = '"+deptId+"' )+" +
                        "   (SELECT count(1)" +
                        "     FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate) AND STATUS = 0  AND YWORG_ID = '"+deptId+"')+" +
                        "     (SELECT count(1)" +
                        "     FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "     WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 0 AND d.TDYW_ORGID = '"+deptId+"') +" +
                        "    (SELECT  ((SELECT CASE WHEN ('"+deptId+"' = '402881e6603a69b801603a6ab1d70000')" +
                        "      THEN 1" +
                        "                      WHEN ('"+deptId+"' != '402881e6603a69b801603a6ab1d70000')" +
                        "                        THEN 12" +
                        "                      end" +
                        "               FROM dual) - ((SELECT count(1) FROM TIMED_TASK_RECORD t WHERE DEPT_ID = '"+deptId+"' AND trunc(t.CHECK_TIME) = trunc(sysdate)) +1))" +
                        "     from dual )) AS WKS" +
                        "    FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, null);
                //饼图
                obj.put("values",map1);
                //巡视统计
                String xsSql = "SELECT (SELECT count(1)" +
                        "        FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "          AND TD_ORG = '"+deptId+"' ) AS SUM," +
                        "  (SELECT count(1)" +
                        "   FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "      AND STAUTS = 2   AND TD_ORG = '"+deptId+"' ) AS COMPlETE," +
                        "  (SELECT count(1)" +
                        "   FROM XS_ZC_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "      AND STAUTS = 0   AND TD_ORG = '"+deptId+"' ) AS WKS" +
                        "       FROM dual";
                Map<String, Object> xsMap = this.execSqlSingleResult(xsSql, null);
                obj.put("xsMap",xsMap);
                //看护统计
                String khSql = "SELECT (SELECT count(1)" +
                        "        FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "    AND YWORG_ID = '"+deptId+"' ) AS SUM," +
                        "  (SELECT count(1)" +
                        "   FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "    AND STATUS = 2  AND YWORG_ID = '"+deptId+"') AS COMPlETE," +
                        "  (SELECT count(1)" +
                        "   FROM KH_TASK WHERE trunc(PLAN_START_TIME) = trunc(sysdate)" +
                        "    AND STATUS = 0  AND YWORG_ID = '"+deptId+"') AS WKS" +
                        "   FROM dual";
                Map<String, Object> khMap = this.execSqlSingleResult(khSql, null);
                obj.put("khMap",khMap);
                //前台稽查统计
                String qtjcSql = "SELECT (SELECT count(1)" +
                        "        FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "        WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate)" +
                        "              AND d.TDYW_ORGID = '"+deptId+"') AS SUM," +
                        "  (SELECT count(1)" +
                        "   FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "   WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 2" +
                        "         AND d.TDYW_ORGID = '"+deptId+"') AS COMPlETE," +
                        "  (SELECT count(1)" +
                        "   FROM CHECK_LIVE_TASK t LEFT JOIN CHECK_LIVE_TASK_DETAIL d ON d.TASK_ID = t.ID" +
                        "   WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate) AND t.STATUS = 0" +
                        "         AND d.TDYW_ORGID = '"+deptId+"') AS WKS" +
                        "   FROM dual";
                Map<String, Object> qtjcMap = this.execSqlSingleResult(qtjcSql, null);
                obj.put("qtjcMap",qtjcMap);
                String htjcSql = "     SELECT" +
                        "        (SELECT CASE WHEN ('"+deptId+"' = '402881e6603a69b801603a6ab1d70000')" +
                        "          THEN 1" +
                        "                WHEN ('"+deptId+"' != '402881e6603a69b801603a6ab1d70000')" +
                        "                  THEN 12" +
                        "                end" +
                        "         FROM dual) AS SUM," +
                        "      ((SELECT CASE WHEN ('"+deptId+"' = '402881e6603a69b801603a6ab1d70000')" +
                        "          THEN 1" +
                        "          WHEN ('"+deptId+"' != '402881e6603a69b801603a6ab1d70000')" +
                        "          THEN 12" +
                        "          end" +
                        "          FROM dual) - ((SELECT count(1) FROM TIMED_TASK_RECORD t WHERE DEPT_ID = '402881e6603a69b801603a729f320016' AND trunc(t.CHECK_TIME) = trunc(sysdate)) +1)) AS WKS," +
                        "        (SELECT count(1) FROM TIMED_TASK_RECORD WHERE DEPT_ID = '"+deptId+"' AND COMPLETE = TASKS) AS COMPlETE" +
                        "        from dual";
                Map<String, Object> htjcMap = this.execSqlSingleResult(htjcSql, null);
                obj.put("htjcMap",htjcMap);
                LOGGER.info("三级页面任务详情查询成功");
            }
        }catch (Exception e){
            LOGGER.error("三级页面任务详情查询失败"+e.getMessage());
            return WebApiResponse.erro("三级页面任务详情查询失败"+e.getMessage());
        }
        return WebApiResponse.success(obj);
    }

    /**
     *  四级页面使用  查询本单位不同状态的任务
     * @param page
     * @param size
     * @param deptId 部门id
     * @param flag   0 未开始  1 进行中  2 以完成
     * @return
     */
    public WebApiResponse findTasksByStatus(Integer page, Integer size, String deptId, String flag) {
        if(null == flag  ||  "".equals(flag)) {
            return WebApiResponse.erro("状态错误  flag = " + flag);
        }
        PageRequest pageRequest = new PageRequest(page, size);

        String sql = "    SELECT * FROM (" +
                //巡视
                "  SELECT to_char(x.ID) AS TASKID,x.STAUTS AS STATUS,x.TASK_NAME,x.REAL_START_TIME,u.REALNAME,u.WORKTYPE,u.DEPTID AS DEPT" +
                "  FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u  ON u.ID = x.CM_USER_ID" +
                "  WHERE trunc(x.REAL_START_TIME) = trunc(sysdate)" +
                "  UNION ALL" +
                //看护
                "  SELECT to_char(k.ID) AS TASKID,k.STATUS,k.TASK_NAME,k.REAL_START_TIME,u.REALNAME,u.WORKTYPE,u.DEPTID AS DEPT" +
                "  FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON u.ID = k.USER_ID" +
                "  WHERE trunc(k.REAL_START_TIME) = trunc(sysdate)" +
                //现场稽查
                "  UNION ALL" +
                "  select to_char(t.id) AS TASKID,t.STATUS,t.TASK_NAME,t.PLAN_START_TIME,u.REALNAME,u.WORKTYPE,u.DEPTID AS DEPT" +
                "  from CHECK_LIVE_TASK t" +
                "    LEFT JOIN  rztsysuser u on u.id=t.USER_ID WHERE trunc(t.PLAN_START_TIME) = trunc(sysdate)" +
                "  UNION ALL" +
                //后台稽查
                "   SELECT t.ID AS TASKID,CASE  WHEN (t.TASKS = COMPLETE) THEN 2 WHEN (t.COMPLETE <t.TASKS) THEN 1 WHEN (t.COMPLETE = 0) THEN 0 END AS STATUS" +
                "    ,CASE" +
                "     WHEN (to_number(to_char(t.CHECK_TIME,'hh24')) >= to_number(w.START_TIME) ) AND" +
                "          (to_number(to_char(t.CHECK_TIME,'hh24')) <= to_number(w.END_TIME) )THEN 'pc后台稽查任务'" +
                "     WHEN (to_number(to_char(t.CHECK_TIME,'hh24')) <= to_number(w.START_TIME) ) OR" +
                "          (to_number(to_char(t.CHECK_TIME,'hh24')) >= to_number(w.END_TIME) )THEN 'pc后台稽查任务'" +
                "     END AS TASK_NAME,t.CHECK_TIME AS REAL_START_TIME," +
                "    CASE" +
                "    WHEN (to_number(to_char(t.CHECK_TIME,'hh24')) >= to_number(w.START_TIME) ) AND" +
                "         (to_number(to_char(t.CHECK_TIME,'hh24')) <= to_number(w.END_TIME) )THEN 'pc白班后台稽查人员'" +
                "    WHEN (to_number(to_char(t.CHECK_TIME,'hh24')) <= to_number(w.START_TIME) ) OR" +
                "         (to_number(to_char(t.CHECK_TIME,'hh24')) >= to_number(w.END_TIME) )THEN 'pc夜班后台稽查人员'" +
                "    END AS REALNAME,4 AS type,t.DEPT_ID AS DEPT" +
                "  FROM TIMED_TASK_RECORD t LEFT JOIN WORKING_TIMED w ON w.DEPT_ID = t.DEPT_ID" +
                "  WHERE trunc(t.CHECK_TIME) = trunc(sysdate)" +
                "  ) WHERE  STATUS = '"+flag+"'";
                if(null != deptId && !"".equals(deptId)){
                    sql += "  AND DEPT = '"+deptId+"'";
                }
        Page<Map<String, Object>> maps =null;
            try {
                maps = this.execSqlPage(pageRequest, sql, null);
            }catch (Exception e){
                    LOGGER.error("查询单位任务列表失败"+e.getMessage());
                    return WebApiResponse.erro("查询单位任务列表失败"+e.getMessage());
            }
             LOGGER.info("查询单位任务列表成功");
            return WebApiResponse.success(maps);
    }
    /**
     * 五级页面使用  查看当前任务详情
     * @param taskType
     * @param taskId
     * @param deptId   当任务类型为4时 需要使用部门id和抽查时间查询后台稽查任务
     * @param realTime  后台稽查任务的开始时间 就是抽查时间 也是任务开始时间
     * @return
     */
    public WebApiResponse findTaskInfoByTaskId(String taskType, String taskId, String deptId, String realTime) {
        if(null == taskType || "".equals(taskType)){
            return WebApiResponse.erro("参数错误 taskType = "+taskType);
        }
        Map<String, Object> map = null;
       try {
           if("4".equals(taskType)){
               //后台稽查
               String userSql = "  SELECT * FROM TIMED_TASK_RECORD t WHERE t.CHECK_TIME = to_date('"+realTime+"','YYYY-MM-dd HH24:mi:ss')" +
                       "       AND t.DEPT_ID = '"+deptId+"'";
               map = this.execSqlSingleResult(userSql);
               if(null != map ){
                   Object ex_user = map.get("EX_USER");
                   if(null != ex_user){
                       String[] split = ex_user.toString().split(",");
                       ArrayList<Object> objects = new ArrayList<>();
                       for (String s : split) {
                           String sql = " SELECT u.REALNAME,u.PHONE,d.DEPTNAME,dd.DEPTNAME GROUPNAME,com.COMPANYNAME,ddd.DEPTNAME AS DW FROM RZTSYSUSER u LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                                   "    LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = u.GROUPID" +
                                   "    LEFT JOIN RZTSYSDEPARTMENT ddd ON ddd.ID = u.CLASSNAME" +
                                   "    LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                                   "    WHERE u.ID = '"+s+"'";
                           Map<String, Object> map1 = this.execSqlSingleResult(sql);
                           objects.add(map1);
                       }
                    map.put("users",objects);
                   }
               }
               return WebApiResponse.success(map);
           }
           if(null == taskId || "".equals(taskId)){
               return WebApiResponse.erro("参数错误  taskId = "+taskId);
           }
           String sql = "";
           if("3".equals(taskType)){
               //现场稽查
                sql = "   SELECT x.ID,x.TASK_NAME,x.PLAN_START_TIME,x.PLAN_END_TIME,cd.REAL_START_TIME,cd.REAL_END_TIME," +
                       "   x.CREATE_TIME AS PD_TIME,cd.DDXC_TIME,x.STATUS" +
                       "  ,u.REALNAME,u.PHONE,d.DEPTNAME,dd.DEPTNAME GROUPNAME,com.COMPANYNAME,ddd.DEPTNAME AS DW" +
                       "  FROM CHECK_LIVE_TASK x" +
                       "  LEFT JOIN CHECK_LIVE_TASK_DETAIL cd ON cd.TASK_ID = x.ID" +
                       "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = u.GROUPID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT ddd ON ddd.ID = u.CLASSNAME" +
                       "  LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                       "  WHERE x.ID = '"+taskId+"'";
           }
           if("2".equals(taskType)){
               //巡视任务
                sql = "  SELECT x.ID,x.TASK_NAME,x.PLAN_START_TIME,x.PLAN_END_TIME,x.PLAN_XS_NUM,x.REAL_START_TIME,x.REAL_END_TIME,x.REAL_XS_NUM," +
                       "  x.PD_TIME,x.XSKS_TIME,x.DDXC_TIME,x.SFQR_TIME,x.WPTX_TIME,x.STAUTS as STATUS" +
                       "  ,u.REALNAME,u.PHONE,d.DEPTNAME,dd.DEPTNAME GROUPNAME,com.COMPANYNAME,ddd.DEPTNAME AS DW" +
                       "  FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON u.ID = x.CM_USER_ID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = u.GROUPID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT ddd ON ddd.ID = u.CLASSNAME" +
                       "  LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                       "  WHERE x.ID = '"+taskId+"'";

           }
           if("1".equals(taskType)){
               //看护任务
                sql = "   SELECT x.ID,x.TASK_NAME,x.PLAN_START_TIME,x.PLAN_END_TIME,x.REAL_START_TIME,x.REAL_END_TIME," +
                       "  x.CREATE_TIME AS PD_TIME,x.DDXC_TIME,x.SFQR_TIME,x.WPQR_TIME AS WPTX_TIME,x.STATUS" +
                       "  ,u.REALNAME,u.PHONE,d.DEPTNAME,dd.DEPTNAME GROUPNAME,com.COMPANYNAME,ddd.DEPTNAME AS DW" +
                       "   FROM KH_TASK x" +
                       "  LEFT JOIN RZTSYSUSER u ON u.ID = x.USER_ID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = u.GROUPID" +
                       "  LEFT JOIN RZTSYSDEPARTMENT ddd ON ddd.ID = u.CLASSNAME" +
                       "  LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                       "   WHERE x.ID = '"+taskId+"'";
           }
           map = this.execSqlSingleResult(sql);
       }catch (Exception e){
            LOGGER.error("五级页面任务详情查询失败"+e.getMessage());
            return WebApiResponse.erro("五级页面任务详情查询失败"+e.getMessage());
       }
        LOGGER.error("五级页面任务详情查询成功");
        return WebApiResponse.success(map);
    }



    public WebApiResponse deptDaZhu() {
        try {
            Map map = new HashMap();


            Date day = new Date();

            String xsCondition = "group by td_org";
            String khCondition = "group by u.deptid ";
            String xsField = "td_org";
            String khField = "u.deptid";


            //正常
            String xszc = " SELECT " + xsField + " td_org,nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_ZC_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + xsCondition;
            List<Map<String, Object>> xszcMap = this.execSql(xszc, day);
            //保电
            String txbd = " SELECT " + xsField + " td_org,nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_txbd_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + xsCondition;
            List<Map<String, Object>> txbdMap = this.execSql(txbd, day);
            //看护
            String kh = "SELECT " + khField + " td_org,nvl(sum(decode(status, 0, 1, 0)),0) KHWKS,nvl(sum(decode(status, 1, 1, 0)),0) KHJXZ,nvl(sum(decode(status, 2, 1, 0)),0) KHYWC FROM KH_TASK k JOIN RZTSYSUSER u ON k.USER_ID = u.ID and PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + khCondition;
            List<Map<String, Object>> khMap = this.execSql(kh, day);
            //通道单位
            List<Map<String, Object>> deptnameList;
            String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
            deptnameList = this.execSql(deptname);

            Map<String, Object> map1 = new HashMap();
            Map<String, Object> map2 = new HashMap();
            Map<String, Object> map3 = new HashMap();
            for (Map<String, Object> xs : xszcMap) {
                map1.put(xs.get("TD_ORG").toString(), xs);
            }
            for (Map<String, Object> tx : txbdMap) {
                map2.put(tx.get("TD_ORG").toString(), tx);
            }
            for (Map<String, Object> kha : khMap) {
                map3.put(kha.get("TD_ORG").toString(), kha);
            }
            for (Map<String, Object> dept : deptnameList) {
                String deptId = dept.get("ID").toString();
                HashMap xsTask = (HashMap) map1.get(deptId);
                HashMap txTask = (HashMap) map2.get(deptId);
                HashMap khTask = (HashMap) map3.get(deptId);
                dept.put("wks", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSWKS").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSWKS").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHWKS").toString()));
                dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()));
                dept.put("ywc", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSYWC").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSYWC").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHYWC").toString()));
            }
            return WebApiResponse.success(deptnameList);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }





    public WebApiResponse deptDaZhu1() {
        try {
            Map map = new HashMap();


            Date day = new Date();

            String xsCondition = "group by td_org";
            String khCondition = "group by u.deptid ";
            String xsField = "td_org";
            String khField = "u.deptid";


            //正常
            String xszc = " SELECT " + xsField + " td_org,nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_ZC_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + xsCondition;
            List<Map<String, Object>> xszcMap = this.execSql(xszc, day);
            //保电
            String txbd = " SELECT " + xsField + " td_org,nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_txbd_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + xsCondition;
            List<Map<String, Object>> txbdMap = this.execSql(txbd, day);
            //看护
            String kh = "SELECT " + khField + " td_org,nvl(sum(decode(status, 0, 1, 0)),0) KHWKS,nvl(sum(decode(status, 1, 1, 0)),0) KHJXZ,nvl(sum(decode(status, 2, 1, 0)),0) KHYWC FROM KH_TASK k JOIN RZTSYSUSER u ON k.USER_ID = u.ID and PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + khCondition;
            List<Map<String, Object>> khMap = this.execSql(kh, day);
            //通道单位
            List<Map<String, Object>> deptnameList;
            String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
            deptnameList = this.execSql(deptname);

            Map<String, Object> map1 = new HashMap();
            Map<String, Object> map2 = new HashMap();
            Map<String, Object> map3 = new HashMap();
            for (Map<String, Object> xs : xszcMap) {
                map1.put(xs.get("TD_ORG").toString(), xs);
            }
            for (Map<String, Object> tx : txbdMap) {
                map2.put(tx.get("TD_ORG").toString(), tx);
            }
            for (Map<String, Object> kha : khMap) {
                map3.put(kha.get("TD_ORG").toString(), kha);
            }
            for (Map<String, Object> dept : deptnameList) {
                String deptId = dept.get("ID").toString();
                HashMap xsTask = (HashMap) map1.get(deptId);
                HashMap txTask = (HashMap) map2.get(deptId);
                HashMap khTask = (HashMap) map3.get(deptId);
                dept.put("wks", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSWKS").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSWKS").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHWKS").toString()));
                dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()));
                dept.put("ywc", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSYWC").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSYWC").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHYWC").toString()));
            }
            int WKS = 0;
            int JXZ  = 0;
            int YWC = 0;
            for (Map<String, Object> dept : deptnameList) {
                String deptId = dept.get("ID").toString();


                Integer wks = (Integer) dept.get("wks");
                WKS+= wks == null ? 0 :wks;

                Integer jxz = (Integer) dept.get("jxz");
                JXZ+= jxz == null?0:jxz;

                Integer ywc = (Integer) dept.get("ywc");
                YWC += ywc == null ? 0 : ywc;
            }
            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("WKS",WKS);
            stringObjectHashMap.put("JXZ",JXZ);
            stringObjectHashMap.put("YWC",YWC);

            return WebApiResponse.success(stringObjectHashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }

}



}
