package com.rzt.service;

import com.rzt.entity.KHYHHISTORY;
import com.rzt.repository.KHYHHISTORYRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 李成阳
 * 2018/1/31
 * 用户数据查询
 */
@Service
public class UserService extends CurdService<KHYHHISTORY, KHYHHISTORYRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * 二级页
     * 查询各单位在线人数 离线人数  总人数
     * @return
     */
    public WebApiResponse findUser(String deptId){
        List listLike = new ArrayList();
        String s = "";

            s += " AND PLAN_END_TIME >= trunc( SYSDATE ) AND PLAN_START_TIME <= trunc(sysdate+1) ";


        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND r.DEPTID= ?" + listLike.size();
        }
        Map<String, Map> xsMap = new HashMap();
        Map<String, Map> khMap = new HashMap();
        Map<String, Map> xcjcMap = new HashMap();
        String xs = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                " WHERE USERDELETE = 1  " + s +
                " GROUP BY DEPTID,CM_USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String kh = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) khzx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) khlx,DEPTID FROM (SELECT USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                " WHERE USERDELETE = 1  " + s +
                " GROUP BY DEPTID,USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String xcjc = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) zxjczx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) zxjclx ,DEPTID FROM (SELECT z.USER_ID AS USERID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " +
                " WHERE  USERDELETE = 1  " + s +
                " GROUP BY z.USER_ID,DEPTID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL ORDER BY t.DEPTSORT ";
        List<Map<String, Object>> xsList = this.execSql(xs, listLike.toArray());
        List<Map<String, Object>> khList = this.execSql(kh, listLike.toArray());
        List<Map<String, Object>> xcjcList = this.execSql(xcjc, listLike.toArray());
        List<Map<String, Object>> deptnameList = this.execSql(deptname);
        for (int i = 0; i < xsList.size(); i++) {
            xsMap.put(xsList.get(i).get("DEPTID").toString(), xsList.get(i));
        }
        for (int i = 0; i < khList.size(); i++) {
            khMap.put(khList.get(i).get("DEPTID").toString(), khList.get(i));
        }
        for (int i = 0; i < xcjcList.size(); i++) {
            xcjcMap.put(xcjcList.get(i).get("DEPTID").toString(), xcjcList.get(i));
        }
        for (Map map : deptnameList) {
            Map id = xsMap.get(map.get("ID"));
            if (id == null) {
                map.put("XSZX", 0);
                map.put("XSLX", 0);
            } else {
                map.putAll(id);
            }
            Map id1 = khMap.get(map.get("ID"));
            if (id1 == null) {
                map.put("KHZX", 0);
                map.put("KHLX", 0);
            } else {
                map.putAll(id1);
            }
            Map id2 = xcjcMap.get(map.get("ID"));
            if (id2 == null) {
                map.put("ZXJCZX", 0);
                map.put("ZXJCLX", 0);
            } else {
                map.putAll(id2);
            }


        }
        try {
            return WebApiResponse.success(deptnameList);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    /**
     * 一级页面使用
     * 各单位人员离线信息
     * @return
     */
    public WebApiResponse findUserInfoOne(String deptid) {


        String xsZxUser = " SELECT count(1) SM " +
                "FROM (SELECT z.CM_USER_ID " +
                "      FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                "      WHERE  z.is_delete = 0 and LOGINSTATUS = 1 AND USERDELETE = 1 AND z.PLAN_START_TIME< = sysdate AND z.PLAN_END_TIME >= sysdate " +
                "      GROUP BY z.CM_USER_ID) ";
        /**
         * 巡视离线人员
         */
        String xsLxUser = " SELECT count(1) SM  FROM (SELECT z.CM_USER_ID " +
                "  FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                "  WHERE z.is_delete = 0 and LOGINSTATUS = 0 AND USERDELETE = 1  AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) " +
                "  GROUP BY z.CM_USER_ID) ";
        /**
         * 看护在线人员
         */
        String khZxUser = " SELECT count(1) SM FROM (SELECT count(u.ID) " +
                "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                "WHERE LOGINSTATUS = 1 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) " +
                "GROUP BY k.USER_ID) ";
        /**
         * 看护离线人员
         */
        String khLxUser = " SELECT count(1) SM FROM (SELECT count(u.ID) " +
                "FROM RZTSYSUSER u LEFT JOIN KH_TASK k ON u.ID = k.USER_ID " +
                "WHERE LOGINSTATUS = 0 AND WORKTYPE = 1 AND USERDELETE = 1 AND USERTYPE = 0 AND PLAN_START_TIME< = trunc(sysdate+1) AND PLAN_END_TIME >= trunc(sysdate) " +
                "GROUP BY k.USER_ID) ";

        /**
         * 前台稽查在线人员
         */
        String qjcZxUser = " SELECT count(1) SM FROM (SELECT " +
                "    count(1) " +
                "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                "  WHERE u.LOGINSTATUS = 1 AND u.USERDELETE = 1 and k.check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
        /**
         * 前台稽查离线人员
         */
        String qjcLxUser = " SELECT count(1) SM FROM (SELECT " +
                "    count(1) " +
                "  FROM CHECK_LIVE_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID " +
                "  WHERE u.LOGINSTATUS = 0 AND u.USERDELETE = 1 and k.check_type=1 and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > k.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < k.plan_end_time GROUP BY k.USER_ID) ";
        int a = 0;
        int b = 0;
        try {
            if(deptid != null  &&  !"".equals(deptid)){

            String user = "SELECT * FROM WORKING_TIMED WHERE DEPT_ID='" + deptid + "'";
            List<Map<String, Object>> maps = this.execSql(user);
            Map<String,Object> map = new HashedMap();
            if(null != maps && maps.size()>0){
                map = maps.get(0);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String format = formatter.format(new Date());
            String s = format + " 00:00:00";
            String userId = "";
            String start = map.get("START_TIME").toString();
            String end = map.get("END_TIME").toString();
            Date nowDate = DateUtil.getNowDate();
            if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(end)).getTime()) {
                userId = map.get("DAY_USER").toString();
            } else {
                userId = map.get("NIGHT_USER").toString();
            }
            String[] split = userId.split(",");
            for (int i = 0; i < split.length; i++) {
                String sql = "SELECT LOGINSTATUS status FROM RZTSYSUSER where id=?";
                Map<String, Object> status = this.execSqlSingleResult(sql, split[i]);
                if (status.get("STATUS").toString().equals("0")) {
                    a++;
                } else {
                    b++;
                }
            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 后台稽查在线人员
         */
        //   String hjcZxUser = " SELECT count(id) SM FROM RZTSYSUSER WHERE LOGINSTATUS = 1 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 ";
        /**
         * 后台稽查离线人员
         */
        //  String hjcLxUser = " SELECT count(id) SM  FROM RZTSYSUSER WHERE LOGINSTATUS = 0 AND WORKTYPE = 4 AND USERDELETE = 1  AND USERTYPE=0 ";
        try {
            Map<Object, Object> returnMap = new HashMap<>();
            Map<Object, Object> iocMap = new HashMap<>();
            Map<String, Object> xsZxUserMap = this.execSqlSingleResult(xsZxUser);
            Map<String, Object> xsLxUserMap = this.execSqlSingleResult(xsLxUser);
            Map<String, Object> khZxUserMap = this.execSqlSingleResult(khZxUser);
            Map<String, Object> khLxUserMap = this.execSqlSingleResult(khLxUser);
            Map<String, Object> qjcZxUserMap = this.execSqlSingleResult(qjcZxUser);
            Map<String, Object> qjcLxUserMap = this.execSqlSingleResult(qjcLxUser);
            // Map<String, Object> hjcZxUserMap = this.execSqlSingleResult(hjcZxUser);
            // Map<String, Object> hjcLxUserMap = this.execSqlSingleResult(hjcLxUser);
            /*iocMap.put("XSZX", xsZxUserMap.get("SM").toString());
            iocMap.put("XSLX", xsLxUserMap.get("SM").toString());
            iocMap.put("KHZX", khZxUserMap.get("SM").toString());
            iocMap.put("KHLX", khLxUserMap.get("SM").toString());
            iocMap.put("QJCZX", qjcZxUserMap.get("SM").toString());
            iocMap.put("QJCLX", qjcLxUserMap.get("SM").toString());
            iocMap.put("HJCZX", a);
            iocMap.put("HJCLX", b);*/
            Integer xsZx =  Integer.parseInt(xsZxUserMap.get("SM") == null ? "0":xsZxUserMap.get("SM").toString());
            Integer xsLx = Integer.parseInt(xsLxUserMap.get("SM") == null ? "0":xsLxUserMap.get("SM").toString());
            Integer khZx = Integer.parseInt(khZxUserMap.get("SM") == null ? "0" : khZxUserMap.get("SM").toString());
            Integer khLx = Integer.parseInt(khLxUserMap.get("SM") == null ? "0" : khLxUserMap.get("SM").toString());
            Integer qjcZx = Integer.parseInt(qjcZxUserMap.get("SM") == null ? "0" : qjcZxUserMap.get("SM").toString());
            Integer qjcLx = Integer.parseInt(qjcLxUserMap.get("SM") == null ? "0" : qjcLxUserMap.get("SM").toString());
            int OFF_LINE  = (xsLx==null? 0 : xsLx) + (khLx == null ? 0:xsLx +(qjcLx == null ? 0 : qjcLx) +b)/*+ b*/;
                /*+(qjcLx == null ? 0 : qjcLx)*/

            int LOGIN = (xsZx == null ? 0 :xsZx ) + (khZx == null ? 0 : khZx )+ (qjcZx == null ? 0 : qjcZx) + a /*+ (qjcZx == null ? 0 : qjcZx) + a*/;
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("SUM", LOGIN + OFF_LINE);
            hashMap.put("LOGIN", LOGIN);
            hashMap.put("OFF_LINE", OFF_LINE);
            //returnMap.put("data", iocMap);
            return WebApiResponse.success(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WebApiResponse.success("");
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
