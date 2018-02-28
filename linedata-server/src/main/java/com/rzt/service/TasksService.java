package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.KHYHHISTORY;
import com.rzt.repository.KHYHHISTORYRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
     *  四级页面使用  查询本单位不同状态的任务
     * @param page
     * @param size
     * @param deptId 部门id
     * @param flag   0 未开始  1 进行中  2 以完成
     * @return
     */
    public WebApiResponse findTasksByStatus(Integer page, Integer size, String deptId, String flag,String condition) {
        if(null == flag  ||  "".equals(flag)) {
            return WebApiResponse.erro("状态错误  flag = " + flag);
        }
        PageRequest pageRequest = new PageRequest(page, size);
        // SELECT AVATAR FROM RZTSYSUSER WHERE ID = '"+userId+"'
        /**
         * 任务有周期  按照周期内查询
         */
        String sql = "    SELECT * FROM (" +
                //巡视
                "  SELECT to_char(x.ID) AS TASKID,x.STAUTS AS STATUS,x.TASK_NAME,x.REAL_START_TIME,u.REALNAME,u.WORKTYPE,u.DEPTID AS DEPT,u.ID userID,u.AVATAR,x.PLAN_END_TIME,x.PLAN_START_TIME" +
                "  FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u  ON u.ID = x.CM_USER_ID" +
                "  WHERE  u.DEPTID = '"+deptId+"' " +
                "  UNION ALL" +
                //看护
                "  SELECT to_char(k.ID) AS TASKID,k.STATUS as STATUS,k.TASK_NAME,k.REAL_START_TIME,u.REALNAME,u.WORKTYPE,u.DEPTID AS DEPT,u.ID userID,u.AVATAR,k.PLAN_END_TIME,k.PLAN_START_TIME " +
                "   FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON u.ID = k.USER_ID " +
                "    WHERE  YWORG_ID = '"+deptId+"' " +
                //现场稽查
                "  UNION ALL" +
                "  select to_char(t.id) AS TASKID,t.STATUS as STATUS,t.TASK_NAME,t.PLAN_START_TIME,u.REALNAME,u.WORKTYPE,u.DEPTID AS DEPT,u.ID userID,u.AVATAR,t.PLAN_END_TIME,t.PLAN_START_TIME" +
                "  from CHECK_LIVE_TASK t" +
                "    LEFT JOIN  rztsysuser u on u.id=t.USER_ID WHERE " +
                "      u.DEPTID = '"+deptId+"' " +


//                SELECT count(1)  " +
//        "FROM KH_TASK " +
//                "WHERE STATUS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)

                //"  UNION ALL" +
               /* //后台稽查
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
                "  WHERE trunc(t.CHECK_TIME) = trunc(sysdate)" +*/

                "  ) WHERE  STATUS = '"+flag+"'  AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate) ";



              /*  if(!"2".equals(flag)){
                    sql += "  AND trunc(t.PLAN_START_TIME) = trunc(sysdate)";
                }else{
                    sql += "  AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
                }
*/
        if(null != condition  && !"".equals(condition)){
            sql += "  AND ( TASK_NAME like '%"+condition.trim()+"%'  OR  REALNAME like '%"+condition.trim()+"%'  )";
        }
        //进行中
        String htsql1 = "SELECT  DISTINCT CREATE_TIME , ID ,TASKS,COMPLETE,CHECK_TIME,EX_USER  FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) >= trunc(sysdate)\n" +
                " AND DEPT_ID = '"+deptId+"' AND TASKS > COMPLETE AND CREATE_TIME = (SELECT max(CREATE_TIME)\n" +
                "  FROM TIMED_TASK_RECORD)";
        //未完成
        String htsql2 = "SELECT  DISTINCT CREATE_TIME , ID ,TASKS,COMPLETE,CHECK_TIME,EX_USER  FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) >= trunc(sysdate)\n" +
                "  AND DEPT_ID = '"+deptId+"' AND TASKS > COMPLETE AND CREATE_TIME != (SELECT max(CREATE_TIME)\n" +
                "  FROM TIMED_TASK_RECORD)";
        //已完成
        String htsql3 = "SELECT  DISTINCT CREATE_TIME , ID ,TASKS,COMPLETE,CHECK_TIME,EX_USER  FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) >= trunc(sysdate)\n" +
                "   AND DEPT_ID = '"+deptId+"' AND TASKS = COMPLETE";


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
            String xszc = " SELECT " + xsField + " td_org,nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_ZC_TASK  WHERE PLAN_END_TIME >= trunc(?1) and  is_delete = 0  and  PLAN_START_TIME <= trunc(?1+1) " + xsCondition;
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
        /**
         * 正常巡视未开始
         */
        String zcXsWks = "SELECT count(1)  " +
                "FROM XS_ZC_TASK " +
                "WHERE is_delete = 0 and STAUTS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 保电巡视未开始
         */
        String bdXsWks = "SELECT count(1)  " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 看护未开始
         */
        String khWks = "SELECT count(1)  " +
                "FROM KH_TASK " +
                "WHERE STATUS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 现场稽查未开始
         */
        String xcJcWks = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK " +
                "WHERE STATUS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 正常巡视进行中
         */
        String zcXsJxz = "SELECT count(1)  " +
                "FROM XS_ZC_TASK " +
                "WHERE is_delete = 0 and STAUTS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 保电巡视进行中
         */
        String bdXsJxz = "SELECT count(1)  " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 看护进行中
         */
        String khJxz = "SELECT count(1)  " +
                "FROM KH_TASK " +
                "WHERE STATUS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 现场稽查进行中
         */
        String xcJcJxz = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK " +
                "WHERE STATUS = 1  AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 正常巡视已完成
         */
        String zcXsYwc = "SELECT count(1)  " +
                "FROM XS_ZC_TASK " +
                "WHERE is_delete = 0 and STAUTS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 保电巡视已完成
         */
        String bdXsYwc = "SELECT count(1)  " +
                "FROM XS_TXBD_TASK " +
                "WHERE STAUTS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         * 看护已完成
         */
        String khYwc = "SELECT count(1)  " +
                "FROM KH_TASK " +
                "WHERE STATUS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";
        /**
         *现场稽查已完成
         */
        String xcJcYwc = "SELECT count(1)  " +
                "FROM CHECK_LIVE_TASK " +
                "WHERE STATUS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)";

        /**
         *后台稽查未完成
         */
        String htJcWks = "SELECT COUNT(1) FROM (SELECT DISTINCT CREATE_TIME FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) >= trunc(sysdate)" +
                "   and (TASKS>COMPLETE) )";
        /**
         *后台稽查进行中
         */
//            String htJcYks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
        String htJcYks = "SELECT count(DISTINCT (DEPT_ID)) FROM TIMED_TASK_RECORD";
        /**
         *后台稽查已完成
         */
        String htJcYwc = "SELECT COUNT(1) FROM (SELECT DISTINCT CREATE_TIME FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) >= trunc(sysdate)" +
                "   and (TASKS=COMPLETE) )";
        String sql = "SELECT " +
                "(" + zcXsWks + ")+(" + bdXsWks + ") as XsWks," +
                "(" + zcXsJxz + ")+(" + bdXsJxz + ") as XsJxz," +
                "(" + zcXsYwc + ")+(" + bdXsYwc + ") as XsYwc," +
                "(" + khJxz + ") as khJxz," +
                "(" + khWks + ") as khWks, " +
                "(" + khYwc + ") as khYwc," +
                "(" + xcJcJxz + ") as xcJcJxz," +
                "(" + xcJcWks + ") as xcJcWks," +
                "(" + xcJcYwc + ") as xcJcYwc, " +
                "(" + htJcWks + ") as htJcWks, " +
                "(" + htJcYks + ") as htJcYks, " +
                "(" + htJcYwc + ") as htJcYwc " +
                "  FROM dual";
        List<Map<String, Object>> list = this.execSql(sql);
        Map map = new HashMap();
        map.put("data", list);
        map.put("adminModule", "6_1");


        if(null != list && list.size() ==1){
            Map<String, Object> map1 = list.get(0);
            int khwks = Integer.parseInt(map1.get("KHWKS").toString());
            int xcjcwks = Integer.parseInt(map1.get("XCJCWKS").toString());
            int htjcwks = Integer.parseInt(map1.get("HTJCWKS").toString());
            int xswks = Integer.parseInt(map1.get("XSWKS").toString());
            Integer WKS =  khwks+xcjcwks+htjcwks+xswks;

            int xcjcjxz = Integer.parseInt(map1.get("XCJCJXZ").toString());
            int khjxz = Integer.parseInt(map1.get("KHJXZ").toString());
            int htjcyks = Integer.parseInt(map1.get("HTJCYKS").toString());
            int xsjxz = Integer.parseInt(map1.get("XSJXZ").toString());
            Integer JXZ = xcjcjxz+khjxz+htjcyks+xsjxz;

            int xsywc = Integer.parseInt(map1.get("XSYWC").toString());
            int khywc = Integer.parseInt(map1.get("KHYWC").toString());
            int xcjcywc = Integer.parseInt(map1.get("XCJCYWC").toString());
            int htjcywc = Integer.parseInt(map1.get("HTJCYWC").toString());
            Integer YWC = xsywc+khywc+xcjcywc+htjcywc;

            HashMap<String, Object> stringObjectHashMap = new HashMap<>();
            stringObjectHashMap.put("WKS",WKS);
            stringObjectHashMap.put("JXZ",JXZ);
            stringObjectHashMap.put("YWC",YWC);
            return WebApiResponse.success(stringObjectHashMap);
        }
        return WebApiResponse.success(list);

    }

    /**
     * 按照单位查询任务
     * @return
     */
    public WebApiResponse deptDaZhu2(String deptId) {
        List<Map<String, Object>> list = null;
        try {
            /**
             * 正常巡视未开始
             */
            String zcXsWks = "SELECT count(1)  " +
                    "  FROM XS_ZC_TASK " +
                    "  WHERE is_delete = 0 and STAUTS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "          AND TD_ORG = '"+deptId+"'  ";
            /**
             * 保电巡视未开始 TD_ORG
             */
            String bdXsWks = "SELECT count(1)  " +
                    "  FROM XS_TXBD_TASK " +
                    "  WHERE STAUTS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "           AND TD_ORG = '"+deptId+"'  ";
            /**
             * 看护未开始
             */
            String khWks = "SELECT count(1)  " +
                    "FROM KH_TASK " +
                    "WHERE STATUS = 0 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "             AND YWORG_ID = '"+deptId+"'  ";
            /**
             * 现场稽查未开始
             */
            String xcJcWks = "SELECT count(1)" +
                    "  FROM CHECK_LIVE_TASK c LEFT JOIN RZTSYSUSER u ON u.ID = c.USER_ID" +
                    "  WHERE c.STATUS = 0 AND c.PLAN_START_TIME <= sysdate AND c.PLAN_END_TIME >= trunc(sysdate)" +
                    "  AND u.DEPTID = '"+deptId+"'  ";
            /**
             * 正常巡视进行中
             */
            String zcXsJxz = "SELECT count(1)  " +
                    "  FROM XS_ZC_TASK " +
                    "  WHERE is_delete = 0 and STAUTS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "      AND TD_ORG = '"+deptId+"' ";
            /**
             * 保电巡视进行中
             */
            String bdXsJxz = "SELECT count(1)  " +
                    "   FROM XS_TXBD_TASK " +
                    "   WHERE STAUTS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "          AND TD_ORG = '"+deptId+"'  ";
            /**
             * 看护进行中
             */
            String khJxz = "SELECT count(1)  " +
                    "  FROM KH_TASK " +
                    "  WHERE STATUS = 1 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "          AND YWORG_ID = '"+deptId+"' ";
            /**
             * 现场稽查进行中
             */
            String xcJcJxz = "SELECT count(1)" +
                    "  FROM CHECK_LIVE_TASK c LEFT JOIN RZTSYSUSER u ON u.ID = c.USER_ID" +
                    "   WHERE STATUS = 1  AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "  AND u.DEPTID = '"+deptId+"'  ";
            /**
             * 正常巡视已完成
             */
            String zcXsYwc = "SELECT count(1)  " +
                    "  FROM XS_ZC_TASK " +
                    "   WHERE is_delete = 0 and STAUTS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "        AND TD_ORG = '"+deptId+"' ";
            /**
             * 保电巡视已完成
             */
            String bdXsYwc = "SELECT count(1)  " +
                    "   FROM XS_TXBD_TASK " +
                    "   WHERE STAUTS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "       AND TD_ORG = '"+deptId+"' ";
            /**
             * 看护已完成
             */
            String khYwc = "SELECT count(1)  " +
                    "  FROM KH_TASK " +
                    "  WHERE STATUS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)" +
                    "     AND YWORG_ID = '"+deptId+"' ";
            /**
             *现场稽查已完成
             */

            String xcJcYwc = "SELECT count(1)" +
                    "  FROM CHECK_LIVE_TASK c LEFT JOIN RZTSYSUSER u ON u.ID = c.USER_ID" +
                    "  WHERE STATUS = 2 AND PLAN_START_TIME <= sysdate AND PLAN_END_TIME >= trunc(sysdate)"+
                    "  AND u.DEPTID = '"+deptId+"'  ";
           /* String sql1 = "select * from(SELECT CREATETIME FROM TIMED_TASK where THREEDAY=1 ORDER BY CREATETIME DESC ) where ROWNUM=1";
            List<Map<String, Object>> maps = this.execSql(sql1);
            Date createtime = DateUtil.parseDate(maps.get(0).get("CREATETIME").toString());
            Date nextTime = DateUtil.addDate(createtime, 72);*/
            /**
             *后台稽查未完成
             */
            String htJcWks = "SELECT COUNT(1) FROM (SELECT DISTINCT CREATE_TIME FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) >= trunc(sysdate) " +
                    "         and (TASKS>COMPLETE)  AND DEPT_ID = '"+deptId+"' AND CREATE_TIME != (SELECT max(CREATE_TIME)" +
                    "   FROM TIMED_TASK_RECORD) ) ";
            /**
             *后台稽查进行中
             */
//            String htJcYks = "SELECT count(1) FROM TIMED_TASK t WHERE  t.CREATETIME >  ( select sysdate - (3 * 24 * 60 * 60 + 60 * 60) / (1 * 24 * 60 * 60)   from  dual) AND  THREEDAY  = 1 AND t.STATUS = 1";
            String htJcYks = "SELECT count(DISTINCT (DEPT_ID)) FROM TIMED_TASK_RECORD  WHERE  DEPT_ID = '"+deptId+"' ";
            /**
             *后台稽查已完成
             */
            String htJcYwc = "SELECT COUNT(1) FROM (SELECT DISTINCT CREATE_TIME FROM TIMED_TASK_RECORD WHERE trunc(CREATE_TIME) >= trunc(sysdate)" +
                    "         and (TASKS=COMPLETE)  AND DEPT_ID = '"+deptId+"'  AND CREATE_TIME != (SELECT max(CREATE_TIME)" +
                    "   FROM TIMED_TASK_RECORD) ) ";

            //应开始  未开始 统计
            //巡视
            String XSViolation = "SELECT count(1)" +
                    "  FROM XS_ZC_TASK" +
                    "   WHERE is_delete = 0 and STAUTS = 0 AND PLAN_START_TIME <= sysdate" +
                    "        AND TD_ORG =  '"+deptId+"'   ";
            //看护
            String KHViolation ="SELECT count(1)" +
                    "  FROM KH_TASK" +
                    "  WHERE STATUS = 0 AND PLAN_START_TIME <= sysdate" +
                    "     AND YWORG_ID = '"+deptId+"' ";
            //现场稽查
            String XCJCViolation ="SELECT count(1)" +
                    "  FROM CHECK_LIVE_TASK c LEFT JOIN RZTSYSUSER u ON u.ID = c.USER_ID" +
                    "  WHERE STATUS = 0 AND PLAN_START_TIME <= sysdate" +
                    "  AND u.DEPTID = '"+deptId+"' ";


            //未到开始时间

            //巡视
            String XSArrive = "SELECT count(1)" +
                    "  FROM XS_ZC_TASK" +
                    "   WHERE is_delete = 0 and STAUTS = 0 AND PLAN_START_TIME >= sysdate" +
                    "        AND TD_ORG =  '"+deptId+"'   ";
            //看护
            String KHArrive ="SELECT count(1)" +
                    "  FROM KH_TASK" +
                    "  WHERE STATUS = 0 AND PLAN_START_TIME >= sysdate" +
                    "     AND YWORG_ID = '"+deptId+"' ";
            //现场稽查
            String XCJCArrive ="SELECT count(1)" +
                    "  FROM CHECK_LIVE_TASK c LEFT JOIN RZTSYSUSER u ON u.ID = c.USER_ID" +
                    "  WHERE STATUS = 0 AND PLAN_START_TIME >= sysdate" +
                    "  AND u.DEPTID = '"+deptId+"' ";

            String sql = "SELECT " +
                    "(" + zcXsWks + ")+(" + bdXsWks + ") as XsWks," +
                    "(" + zcXsJxz + ")+(" + bdXsJxz + ") as XsJxz," +
                    "(" + zcXsYwc + ")+(" + bdXsYwc + ") as XsYwc," +
                    "(" + khJxz + ") as khJxz," +
                    "(" + khWks + ") as khWks, " +
                    "(" + khYwc + ") as khYwc," +
                    "(" + xcJcJxz + ") as xcJcJxz," +
                    "(" + xcJcWks + ") as xcJcWks," +
                    "(" + xcJcYwc + ") as xcJcYwc, " +
                    "(" + htJcWks + ") as htJcWks, " +
                    "(" + htJcYks + ") as htJcyks, " +
                    "(" + htJcYwc + ") as htJcYwc, " +
                    " ( "+ XSViolation +") + ("+KHViolation+") + ("+XCJCViolation+")  as Violation,"+//应开始  未开始
                    " ( "+ XSArrive +")+ ("+KHArrive+") + ("+XCJCArrive+")  as Arrive "+//未到开始时间
                    "  FROM dual";

            list = this.execSql(sql);
        }catch (Exception e){
            LOGGER.error("单位任务详情查询失败"+e.getMessage());
            return WebApiResponse.erro("单位任务详情查询失败"+e.getMessage());
        }
        return WebApiResponse.success(list);

    }

    /**
     * 获取当前任务图片
     * @param taskId
     * @param taskType
     * @param page
     * @param size
     * @return
     */
    public WebApiResponse findPicByTaskId(String taskId,String taskType,Integer page,Integer size) {
        if(null == taskId || "".equals(taskId)) {
            return WebApiResponse.erro("参数无效TaskId="+taskId);
        }
        if(null == taskType || "".equals(taskType)) {
            return WebApiResponse.erro("参数无效taskType="+taskType);
        }
        try {
            Pageable pageable = new PageRequest(page, size);
            if(null != taskType && !"".equals(taskType)){
                String sql = "";
                if("2".equals(taskType)){//巡视   AND END_TOWER_ID = 0
                    //巡视图片查询
                     sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME,l.OPERATE_NAME,l.START_TOWER_ID" +
                            "     FROM xs_zc_task k LEFT JOIN XS_ZC_TASK_EXEC x ON k.ID = x.XS_ZC_TASK_ID" +
                            "      LEFT JOIN XS_ZC_TASK_EXEC_DETAIL l ON x.ID = l.XS_ZC_TASK_EXEC_ID RIGHT JOIN PICTURE_TOUR p ON l.ID = p.PROCESS_ID" +
                            "       WHERE p.TASK_ID = '"+taskId+"'  AND P.FILE_TYPE = 1  AND OPERATE_NAME IS NOT NULL ORDER BY  p.CREATE_TIME DESC";
                }
                if("1".equals(taskType)){//看护
                    //看护图片的返回
                     sql = "SELECT p.ID,FILE_PATH,p.CREATE_TIME,p.PROCESS_NAME as OPERATE_NAME" +
                            "  FROM PICTURE_KH p" +
                            "    WHERE p.TASK_ID = '"+taskId+"' AND FILE_TYPE = 1   AND p.PROCESS_ID NOT IN (1,2,3)" +
                            "     ORDER BY p.CREATE_TIME DESC";
                }
                Page<Map<String, Object>> maps = this.execSqlPage(pageable, sql, null);
                LOGGER.info("任务图片查询成功");
                return WebApiResponse.success(maps);
            }
        }catch (Exception e){
            LOGGER.info("任务图片查询失败"+e.getMessage());
            return WebApiResponse.erro("任务图片查询失败"+e.getMessage());
        }
        return  WebApiResponse.erro("参数错误");
    }
}
