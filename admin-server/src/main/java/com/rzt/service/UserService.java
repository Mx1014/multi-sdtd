package com.rzt.service;

import com.rzt.entity.TimedTask;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/31
 * 用户数据查询
 */
@Service
public class UserService extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * 查询在线人数 离线人数  总人数
     * @return
     */
    public WebApiResponse findUser(){
        Map<String, Object> map = null;
        try {
           //离线
           String sql = " SELECT (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND u.WORKTYPE  != 5 ) AS OFF_LINE," +
                   "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND u.WORKTYPE  != 5 ) AS LOGIN," +
                   "  (SELECT count(1) FROM RZTSYSUSER  WHERE (WORKTYPE = 5 OR USERDELETE = 0)) AS DELORADMIN," +
                   "  (SELECT count(1) FROM RZTSYSUSER) AS SUM" +
                   "   FROM dual ";
            map = this.execSqlSingleResult(sql, null);
            LOGGER.info("人员在线信息查询成功");
       }catch (Exception e){
            LOGGER.error("人员在线信息查询错误"+e.getMessage());
            return WebApiResponse.erro("人数信息查询错误"+e.getMessage());
       }
       return WebApiResponse.success(map);
    }

    /**
     * 二级页面使用
     * 各单位人员离线信息
     * @return
     */
    public WebApiResponse findUserInfoTwo(){
        ArrayList<Object> objects = new ArrayList<>();

        try {
            String deptSql = "SELECT ID,DEPTNAME" +
                    "           FROM RZTSYSDEPARTMENT WHERE DEPTPID = '402881e6603a69b801603a6ab1d70000'";
            //所有的部门
            List<Map<String, Object>> maps = this.execSql(deptSql, null);
            for (Map<String, Object> map : maps) {
                //按部门查询并封装
                String id = map.get("ID").toString();
                String deptName = map.get("DEPTNAME").toString();


                String sql = "   SELECT (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND u.WORKTYPE  != 5 AND  DEPTID = '"+id+"') AS OFF_LINE," +
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND u.WORKTYPE  != 5 AND  DEPTID = '"+id+"') AS LOGIN," +
                        "  (SELECT count(1) FROM RZTSYSUSER  WHERE (WORKTYPE = 5 OR USERDELETE = 0) AND  DEPTID = '"+id+"') AS DELORADMIN," +
                        "  (SELECT count(1) FROM RZTSYSUSER WHERE DEPTID = '"+id+"') AS SUM" +
                        "   FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, null);
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("name", deptName);
                obj.put("id", id);
                obj.put("value", map1);
                objects.add(obj);
            }
        }catch (Exception e){
            LOGGER.error("各单位人员离线信息查询失败"+e.getMessage());
            return WebApiResponse.erro("各单位人员离线信息查询失败"+e.getMessage());
        }
            LOGGER.info("各单位人员离线信息查询成功");
        return WebApiResponse.success(objects);
    }
    /**
     * 三级页面使用
     * 各单位人员离线信息
     * @return
     */
    public WebApiResponse findUserInfoThree(String id){
        if(null == id || "".equals(id)){
            return WebApiResponse.erro("参数错误   deptId = "+id);
        }
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(id);
        HashMap<String, Object> obj = new HashMap<>();

        try {
            //饼状图sql
            String sql = "   SELECT (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND u.WORKTYPE  != 5 AND  DEPTID =?1) AS OFF_LINE," +
                    "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND u.WORKTYPE  != 5 AND  DEPTID =?1) AS LOGIN," +
                    "  (SELECT count(1) FROM RZTSYSUSER  WHERE (WORKTYPE = 5 OR USERDELETE = 0) AND  DEPTID =?1) AS DELORADMIN," +
                    "  (SELECT count(1) FROM RZTSYSUSER WHERE DEPTID =?1) AS SUM" +
                    "   FROM dual";

            //看护
                String infoSql = "   SELECT (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND u.WORKTYPE  = 1 AND  DEPTID = ?1) AS KHOFF_LINE," +
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND u.WORKTYPE   = 1 AND  DEPTID = ?1) AS KHLOGIN," +
                        "  (SELECT count(1) FROM RZTSYSUSER WHERE DEPTID = ?1 AND WORKTYPE   = 1 ) AS KHSUM," +
                        //巡视
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND u.WORKTYPE  = 2 AND  DEPTID = ?1) AS XSOFF_LINE," +
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND u.WORKTYPE   = 2 AND  DEPTID = ?1) AS XSLOGIN," +
                        "  (SELECT count(1) FROM RZTSYSUSER WHERE DEPTID = ?1 AND WORKTYPE   = 2 ) AS XSSUM," +
                        //现场稽查
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND u.WORKTYPE  = 3 AND  DEPTID = ?1) AS QTJCOFF_LINE," +
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND u.WORKTYPE   = 3  AND  DEPTID = ?1) AS QTJCLOGIN," +
                        "  (SELECT count(1) FROM RZTSYSUSER WHERE DEPTID = ?1 AND WORKTYPE   = 3) AS QTJCSUM," +
                        //后台稽查
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 AND u.WORKTYPE  = 4 AND  DEPTID = ?1) AS HTJCOFF_LINE," +
                        "  (SELECT count(1) FROM RZTSYSUSER u WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 AND u.WORKTYPE  = 4  AND  DEPTID = ?1) AS HTJCLOGIN," +
                        "  (SELECT count(1) FROM RZTSYSUSER WHERE DEPTID = ?1 AND WORKTYPE  = 4 ) AS HTJCSUM," +
                        //删除 或领导数
                        "  (SELECT count(1) FROM RZTSYSUSER  WHERE (WORKTYPE = 5 OR USERDELETE = 0) AND  DEPTID = ?1) AS DELORADMIN" +
                        "       FROM dual";
                Map<String, Object> map1 = this.execSqlSingleResult(sql, objects.toArray());
                obj.put("id", id);
                obj.put("sum", map1);
            Map<String, Object> map = this.execSqlSingleResult(infoSql, objects.toArray());
            obj.put("values",map);
        } catch (Exception e){
            LOGGER.error("各单位人员离线信息查询失败"+e.getMessage());
            return WebApiResponse.erro("各单位人员离线信息查询失败"+e.getMessage());
        }
        LOGGER.info("各单位人员离线信息查询成功");
        return WebApiResponse.success(obj);
    }

    /**
     * 四级页面  查看当前单位所有人 关联任务
     * @param deptId       部门
     * @param loginStatus  在线状态
     * @return
     */
    public WebApiResponse findUserAndTask(Integer page,Integer size,String deptId,String loginStatus ){
        Page<Map<String, Object>> maps = null;
        try {
            Pageable pageable = new PageRequest(page, size);
            String sql = "     SELECT u.ID AS USERID,k.ID AS TASKID ,k.TASK_NAME,k.REAL_START_TIME,k.CREATE_TIME,u.WORKTYPE,u.REALNAME" +
                    "      FROM RZTSYSUSER u" +
                    "        LEFT JOIN KH_TASK k ON k.USER_ID = u.ID" +
                    "      WHERE u.LOGINSTATUS = '"+loginStatus+"' AND u.USERDELETE = 1 AND u.WORKTYPE  = 1 AND  DEPTID = '"+deptId+"'" +
                    "            AND k.REAL_START_TIME = (SELECT max(kh.REAL_START_TIME) FROM KH_TASK kh" +
                    "      WHERE kh.USER_ID = u.ID AND  kh.STATUS != 0 AND kh.STATUS !=4) AND k.ID IS NOT NULL" +
                    "          UNION ALL" +
                    "      SELECT u.ID AS USERID,t.ID AS TASKID ,t.TASK_NAME,t.REAL_START_TIME,t.PD_TIME AS CREATE_TIME,u.WORKTYPE,u.REALNAME" +
                    "      FROM RZTSYSUSER u" +
                    "        LEFT JOIN XS_ZC_TASK t ON t.CM_USER_ID = u.ID" +
                    "        WHERE u.LOGINSTATUS =  '"+loginStatus+"'  AND u.USERDELETE = 1 AND u.WORKTYPE  = 2" +
                    "              AND  DEPTID = '"+deptId+"' AND t.ID IS NOT NULL" +
                    "              AND t.REAL_START_TIME = (SELECT max(kh.REAL_START_TIME) FROM XS_ZC_TASK kh WHERE kh.CM_USER_ID = u.ID" +
                    "              AND kh.STAUTS != 0 AND kh.IS_DELETE != 1)" +
                    "          UNION ALL" +
                    "      SELECT u.ID AS USERID,t.ID AS TASKID ,t.TASK_NAME,c.REAL_START_TIME,t.CREATE_TIME,u.WORKTYPE,u.REALNAME" +
                    "      FROM RZTSYSUSER u" +
                    "        LEFT JOIN CHECK_LIVE_TASK t ON t.USER_ID = u.ID LEFT JOIN CHECK_LIVE_TASK_DETAIL c ON c.TASK_ID = t.ID" +
                    "      WHERE u.LOGINSTATUS =  '"+loginStatus+"'  AND u.USERDELETE = 1 AND u.WORKTYPE  = 5" +
                    "      AND  DEPTID = '"+deptId+"' AND t.ID IS NOT NULL" +
                    "      AND t.PLAN_START_TIME = (SELECT max(kh.PLAN_START_TIME) FROM CHECK_LIVE_TASK kh" +
                    "      WHERE kh.USER_ID = u.ID AND kh.STATUS != 0 AND kh.STATUS != 4)" +
                    "        UNION ALL" +
                    "      SELECT u.ID AS USERID,1 AS TASKID,(select concat(u.REALNAME,'后台稽查任务') from dual) AS TASK_NAME" +
                    "      ,u.CREATETIME AS REAL_START_TIME,u.CREATETIME AS CREATE_TIME,u.WORKTYPE,u.REALNAME" +
                    "      FROM RZTSYSUSER u WHERE u.WORKTYPE = 4 AND u.DEPTID = '"+deptId+"' AND u.LOGINSTATUS =  '"+loginStatus+"'  AND u.USERDELETE = 1  ";

            maps = this.execSqlPage(pageable, sql, null);

        }catch (Exception e){
            LOGGER.error("用户四级页面查询人员关联任务失败"+e.getMessage());
            return WebApiResponse.erro("用户四级页面查询人员关联任务失败"+e.getMessage());
        }

        return WebApiResponse.success(maps);
    }

    /**
     * 根据用户id查询当前用户
     * @param userId
     * @return
     */
    public WebApiResponse findUserInfo(String userId){
        if(null == userId || "".equals(userId)){
            return WebApiResponse.erro("参数错误 userId = "+userId);
        }
        Map<String, Object> map = null;
        try {
            String sql = "   SELECT u.DEPTID,u.LOGINSTATUS,u.WORKTYPE,u.ID,u.REALNAME,u.PHONE,d.DEPTNAME,dd.DEPTNAME GROUPNAME,com.COMPANYNAME,ddd.DEPTNAME AS DW,u.CERTIFICATE" +
                    "  FROM RZTSYSUSER u" +
                    "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                    "  LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = u.GROUPID" +
                    "  LEFT JOIN RZTSYSDEPARTMENT ddd ON ddd.ID = u.CLASSNAME" +
                    "  LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                    "   WHERE u.ID = '"+userId+"'";
            map = this.execSqlSingleResult(sql);
            if(null != map){
                String worktype = (String) map.get("WORKTYPE");
                if(null != worktype && !"".equals(worktype)){
                    String taskSql = "";
                    if("1".equals(worktype)){//看护
                         taskSql = "   SELECT k.ID,k.TASK_NAME," +
                                "  (SELECT COUNT(1) FROM KH_TASK kh WHERE kh.USER_ID = '"+userId+"') AS sum" +
                                "  FROM KH_TASK k WHERE k.USER_ID = '"+userId+"'" +
                                "  AND k.REAL_START_TIME =" +
                                "      (SELECT max(kk.REAL_START_TIME) FROM KH_TASK kk WHERE kk.USER_ID = '"+userId+"')";
                    }
                    if("2".equals(worktype)){//巡视
                         taskSql = "   SELECT x.ID,x.TASK_NAME," +
                                "  (SELECT count(1) FROM XS_ZC_TASK xz WHERE xz.CM_USER_ID = '"+userId+"') AS sum" +
                                "   FROM XS_ZC_TASK x WHERE x.CM_USER_ID = '"+userId+"' AND x.REAL_START_TIME =" +
                                "   (SELECT max(xs.REAL_START_TIME) FROM XS_ZC_TASK xs WHERE xs.CM_USER_ID = '"+userId+"')";
                    }
                    if("3".equals(worktype)){//现场稽查
                         taskSql = "   SELECT c.ID,c.TASK_NAME," +
                                "  (SELECT count(1) FROM CHECK_LIVE_TASK ccc WHERE ccc.USER_ID = '"+userId+"') AS sum" +
                                "   FROM CHECK_LIVE_TASK c WHERE c.USER_ID = '"+userId+"'" +
                                "  AND c.REAL_START_TIME = (SELECT max(cc.REAL_START_TIME) FROM CHECK_LIVE_TASK cc WHERE cc.USER_ID = '"+userId+"')";
                    }
                    if("4".equals(worktype)){//后台稽查
                        taskSql = "   SELECT concat(to_char(t.CHECK_TIME,'YYYY-MM-dd HH24:mi:ss'),'期后台稽查任务') AS TASK_NAME," +
                               "  (SELECT count(1) FROM TIMED_TASK_RECORD ttt WHERE ttt.EX_USER LIKE '%"+userId+"%') AS sum" +
                               "   FROM TIMED_TASK_RECORD t WHERE t.EX_USER LIKE '%"+userId+"%' AND t.CREATE_TIME =" +
                               "  (SELECT max(tt.CREATE_TIME) FROM TIMED_TASK_RECORD tt WHERE tt.EX_USER LIKE '%"+userId+"%')";
                    }
                    Map<String, Object> map1 = this.execSqlSingleResult(taskSql);
                    map.putAll(map1);
                }
            }
        } catch (Exception e) {
            LOGGER.error("人员信息详情查询失败"+e.getMessage());
            return WebApiResponse.erro("人员信息详情查询失败"+e.getMessage());
        }
        LOGGER.info("人员信息详情查询成功");
        return WebApiResponse.success(map);
    }


}
