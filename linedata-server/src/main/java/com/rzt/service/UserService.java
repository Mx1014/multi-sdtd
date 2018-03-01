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
     * 各单位人员离线信息
     * @return
     */
    public WebApiResponse findUserInfoOne(String deptid) {


        List listLike = new ArrayList();
        String s = "";
        String s3 = "";

        s += " AND z.PLAN_END_TIME >= trunc( SYSDATE ) AND z.PLAN_START_TIME <= trunc(sysdate+1) ";
        s3 += " and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > z.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < z.plan_end_time ";


        Map<String, Map> xsMap = new HashMap();
        Map<String, Map> khMap = new HashMap();
        Map<String, Map> xcjcMap = new HashMap();
        Map<String, Map> htjcMap = new HashMap();
        String xs = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                " WHERE USERDELETE = 1  AND IS_DELETE=0   " + s  +
                " GROUP BY DEPTID,CM_USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String kh = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) khzx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) khlx,DEPTID FROM (SELECT USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                " WHERE USERDELETE = 1  " + s  +
                " GROUP BY DEPTID,USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String xcjc = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) zxjczx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) zxjclx ,DEPTID FROM (SELECT z.USER_ID AS USERID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " + s3  +
                " WHERE  USERDELETE = 1   GROUP BY z.USER_ID, DEPTID, CLASSNAME, LOGINSTATUS " +
                " ) GROUP BY DEPTID ";
        List<Map<String, Object>> htlist = new ArrayList<>();
        try {
            String user = "SELECT * FROM WORKING_TIMED where 1=1 " ;
            List<Map<String, Object>> maps = this.execSql(user);
            for (Map map : maps) {
                Map<String, Object> dept = new HashMap<>();
                int a = 0;
                int b = 0;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String format = formatter.format(new Date());
                String s1 = format + " 00:00:00";
                String userId = "";
                String start = map.get("START_TIME").toString();
                String end = map.get("END_TIME").toString();
                Date nowDate = DateUtil.getNowDate();
                if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s1), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s1), Double.parseDouble(end)).getTime()) {
                    userId = map.get("DAY_USER").toString();
                } else {
                    userId = map.get("NIGHT_USER").toString();
                }
                String[] split = userId.split(",");
                for (int i = 0; i < split.length; i++) {
                    String sql = "SELECT LOGINSTATUS status FROM RZTSYSUSER where id=?";
                    Map<String, Object> status = this.execSqlSingleResult(sql, split[i]);
                    if (status.get("STATUS").toString().equals("1")) {
                        a++;
                    } else {
                        b++;
                    }
                }
                dept.put("htzx", a);
                dept.put("htlx", b);
                dept.put("DEPTID", map.get("DEPT_ID"));
                htlist.add(dept);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL  ORDER BY t.DEPTSORT ";
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
        for (int i = 0; i < htlist.size(); i++) {
            htjcMap.put(htlist.get(i).get("DEPTID").toString(), htlist.get(i));
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
            Map id3 = htjcMap.get(map.get("ID"));
            if (id3 == null) {
                map.put("HTJCZX", 0);
                map.put("HTJCLX", 0);
            } else {
                map.putAll(id3);
            }

        }
        try {
            int XSZX = 0;
            int KHZX = 0;
            int ZXJCZX = 0;
            int HTJCZX = 0;

            int XSLX = 0;
            int KHLX = 0;
            int ZXJCLX = 0;
            int HTJCLX = 0;

            for (Map<String, Object> map : deptnameList) {
                XSZX += Integer.parseInt(map.get("XSZX") == null ? "0" : map.get("XSZX").toString());
                KHZX += Integer.parseInt(map.get("KHZX") == null ? "0" : map.get("KHZX").toString());
                ZXJCZX += Integer.parseInt(map.get("ZXJCZX") == null ? "0" : map.get("ZXJCZX").toString());
                HTJCZX += Integer.parseInt(map.get("htzx") == null ? "0" : map.get("htzx").toString());

                XSLX += Integer.parseInt(map.get("XSLX") == null ? "0" : map.get("XSLX").toString());
                KHLX += Integer.parseInt(map.get("KHLX") == null ? "0" : map.get("KHLX").toString());
                ZXJCLX += Integer.parseInt(map.get("ZXJCLX") == null ? "0" : map.get("ZXJCLX").toString());
                HTJCLX += Integer.parseInt(map.get("htlx") == null ? "0" : map.get("htlx").toString());

            }
            HashMap<String, Object> hashMap = new HashMap<>();
           /* String zxSql = "";
            String lxSql = "";*/
            /*Map<String, Object> map = this.service.execSqlSingleResult(zxSql);
            ZXJCZX = Integer.parseInt(map.get("") == null ? "0" : map.get("").toString());
            Map<String, Object> map1 = this.service.execSqlSingleResult(lxSql);
            ZXJCLX = Integer.parseInt(map1.get("") == null ? "0" : map.get("").toString());*/
            int LOGIN = XSZX + KHZX + ZXJCZX + HTJCZX;
            int OFF_LINE =  XSLX + KHLX + ZXJCLX + HTJCLX ;
            hashMap.put("SUM", LOGIN + OFF_LINE);
            hashMap.put("LOGIN", LOGIN);
            hashMap.put("OFF_LINE", OFF_LINE);
            return WebApiResponse.success(hashMap);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }


    /**
     * 四级页面  查看当前单位所有人 关联任务
     * @param deptId       部门
     * @param loginStatus  在线状态
     * @return
     */
    public WebApiResponse findUserAndTask(Integer page,Integer size,String deptId,String loginStatus,String condition ){
        Page<Map<String, Object>> maps = null;
        Map<String, Object> htjcMap = null;
        try {
            Pageable pageable = new PageRequest(page, size);

            //   查询  当前值班后台稽查人id
            String user = "SELECT * FROM WORKING_TIMED where 1=1  AND DEPT_ID='"+deptId+"' " ;
            htjcMap = this.execSqlSingleResult(user);
            Map<String, Object> dept = new HashMap<>();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String format = formatter.format(new Date());
            String s1 = format + " 00:00:00";
            String userId = "";
            String start = htjcMap.get("START_TIME").toString();
            String end = htjcMap.get("END_TIME").toString();
            Date nowDate = DateUtil.getNowDate();
            if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s1), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s1), Double.parseDouble(end)).getTime()) {
                userId = htjcMap.get("DAY_USER").toString();
            } else {
                userId = htjcMap.get("NIGHT_USER").toString();
            }
            String[] split = userId.split(",");
            String userID = "";
            for (String s : split) {
                if(null != s && !"".equals(s)){
                    userID += "," + "'" + s + "'";
                }
            }
            if(null != userID && userID.length() > 0){
                userID = userID.substring(1,userID.length());
            }
            String sql = "   SELECT * from (  SELECT u.ID AS USERID,k.ID AS TASKID ,k.TASK_NAME,k.REAL_START_TIME,k.CREATE_TIME,u.WORKTYPE,u.REALNAME,u.AVATAR" +
                    "      FROM RZTSYSUSER u" +
                    "        LEFT JOIN KH_TASK k ON k.USER_ID = u.ID" +
                    "      WHERE u.LOGINSTATUS = '"+loginStatus+"' AND u.USERDELETE = 1 AND u.WORKTYPE  = 1 AND  DEPTID = '"+deptId+"'" +
                    "            AND k.REAL_START_TIME = (SELECT max(kh.REAL_START_TIME) FROM KH_TASK kh" +
                    "      WHERE kh.USER_ID = u.ID AND  kh.STATUS != 0 AND kh.STATUS !=4) AND k.ID IS NOT NULL" +
                    "          UNION ALL" +
                    "      SELECT u.ID AS USERID,t.ID AS TASKID ,t.TASK_NAME,t.REAL_START_TIME,t.PD_TIME AS CREATE_TIME,u.WORKTYPE,u.REALNAME,u.AVATAR" +
                    "      FROM RZTSYSUSER u" +
                    "        LEFT JOIN XS_ZC_TASK t ON t.CM_USER_ID = u.ID" +
                    "        WHERE u.LOGINSTATUS =  '"+loginStatus+"'  AND u.USERDELETE = 1 AND u.WORKTYPE  = 2" +
                    "              AND  DEPTID = '"+deptId+"' AND t.ID IS NOT NULL" +
                    "              AND t.REAL_START_TIME = (SELECT max(kh.REAL_START_TIME) FROM XS_ZC_TASK kh WHERE kh.CM_USER_ID = u.ID" +
                    "              AND kh.STAUTS != 0 AND kh.IS_DELETE != 1)" +
                    "          UNION ALL" +
                    "      SELECT u.ID AS USERID,t.ID AS TASKID ,t.TASK_NAME,c.REAL_START_TIME,t.CREATE_TIME,u.WORKTYPE,u.REALNAME,u.AVATAR" +
                    "      FROM RZTSYSUSER u" +
                    "        LEFT JOIN CHECK_LIVE_TASK t ON t.USER_ID = u.ID LEFT JOIN CHECK_LIVE_TASK_DETAIL c ON c.TASK_ID = t.ID" +
                    "      WHERE u.LOGINSTATUS =  '"+loginStatus+"'  AND u.USERDELETE = 1 AND u.WORKTYPE  = 5" +
                    "      AND  DEPTID = '"+deptId+"' AND t.ID IS NOT NULL" +
                    "      AND t.PLAN_START_TIME = (SELECT max(kh.PLAN_START_TIME) FROM CHECK_LIVE_TASK kh" +
                    "      WHERE kh.USER_ID = u.ID AND kh.STATUS != 0 AND kh.STATUS != 4)" +
                    "        UNION ALL" +
                    "      SELECT u.ID AS USERID,1 AS TASKID,(select concat(u.REALNAME,'后台稽查任务') from dual) AS TASK_NAME" +
                    "      ,u.CREATETIME AS REAL_START_TIME,u.CREATETIME AS CREATE_TIME,u.WORKTYPE,u.REALNAME,u.AVATAR" +
                    "      FROM RZTSYSUSER u WHERE u.WORKTYPE = 4 AND u.DEPTID = '"+deptId+"' AND u.LOGINSTATUS =  '"+loginStatus+"'  AND u.USERDELETE = 1  " +
                    "           AND ( u.REALNAME LIKE '%白班%'   OR   u.REALNAME LIKE '%夜班%') AND u.ID IN ("+userID+") ) where 1=1 ";

            if( null != condition && !"".equals(condition)){
                sql += "  AND ( TASK_NAME like '%"+condition.trim()+"%'  OR  REALNAME like '%"+condition.trim()+"%'  )";
            }

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
            String sql = "   SELECT u.DEPTID,u.LOGINSTATUS,u.WORKTYPE,u.ID as USERID,u.REALNAME,u.PHONE,d.DEPTNAME,dd.DEPTNAME GROUPNAME,com.COMPANYNAME,ddd.DEPTNAME AS DW,u.CERTIFICATE,u.AVATAR" +
                    "  FROM RZTSYSUSER u" +
                    "  LEFT JOIN RZTSYSDEPARTMENT d ON d.ID = u.DEPTID" +
                    "  LEFT JOIN RZTSYSDEPARTMENT dd ON dd.ID = u.GROUPID" +
                    "  LEFT JOIN RZTSYSDEPARTMENT ddd ON ddd.ID = u.CLASSNAME" +
                    "  LEFT JOIN RZTSYSCOMPANY com ON com.ID = u.COMPANYID" +
                    "   WHERE u.ID = '"+userId+"'";
            map = this.execSqlSingleResult(sql);
            if(null != map){
                String worktype = map.get("WORKTYPE").toString();
                if(null != worktype && !"".equals(worktype)){
                    String taskSql = "";
                    if("1".equals(worktype)){//看护
                        taskSql = "   SELECT k.ID as TASKID,k.TASK_NAME," +
                                "  (SELECT COUNT(1) FROM KH_TASK kh WHERE kh.USER_ID = '"+userId+"'  ) AS sum" +
                                "  FROM KH_TASK k WHERE k.USER_ID = '"+userId+"'" +
                                "  AND k.REAL_START_TIME =" +
                                "      (SELECT max(kk.REAL_START_TIME) FROM KH_TASK kk WHERE kk.USER_ID = '"+userId+"')";
                    }
                    if("2".equals(worktype)){//巡视
                        taskSql = "   SELECT x.ID  as TASKID,x.TASK_NAME," +
                                "  (SELECT count(1) FROM XS_ZC_TASK xz WHERE xz.CM_USER_ID = '"+userId+"') AS sum" +
                                "   FROM XS_ZC_TASK x WHERE x.CM_USER_ID = '"+userId+"' AND x.REAL_START_TIME =" +
                                "   (SELECT max(xs.REAL_START_TIME) FROM XS_ZC_TASK xs WHERE xs.CM_USER_ID = '"+userId+"')";
                    }
                    if("3".equals(worktype)){//现场稽查
                        taskSql = "   SELECT c.ID  as TASKID,c.TASK_NAME," +
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




    /**
     * 单位详情页
     * @return
     */
    public WebApiResponse findUserInByDept(String deptid) {
        //后台稽查计数使用
        int a = 0;
        int b = 0;

        Map<String, Object> xsMap =   null;
        Map<String, Object> khMap =   null;
        Map<String, Object> xcjcMap = null;
        Map<String, Object> htjcMap = null;
        String xs = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                " WHERE USERDELETE = 1  AND IS_DELETE=0   AND r.DEPTID='"+deptid+"'  " +
                " GROUP BY DEPTID,CM_USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String kh = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) khzx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) khlx,DEPTID FROM (SELECT USER_ID,DEPTID,CLASSNAME,LOGINSTATUS" +
                " FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID" +
                " WHERE USERDELETE = 1  AND r.DEPTID='"+deptid+"'" +
                " GROUP BY DEPTID,USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String xcjc = "SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) zxjczx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) zxjclx ,DEPTID FROM (SELECT z.USER_ID AS USERID,DEPTID,CLASSNAME,LOGINSTATUS" +
                " FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID" +
                " WHERE  USERDELETE = 1   AND r.DEPTID='"+deptid+"'  GROUP BY z.USER_ID, DEPTID, CLASSNAME, LOGINSTATUS" +
                " ) GROUP BY DEPTID";
        try {
            String user = "SELECT * FROM WORKING_TIMED where 1=1  AND DEPT_ID='"+deptid+"' " ;
            htjcMap = this.execSqlSingleResult(user);
            Map<String, Object> dept = new HashMap<>();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String format = formatter.format(new Date());
            String s1 = format + " 00:00:00";
            String userId = "";
            String start = htjcMap.get("START_TIME").toString();
            String end = htjcMap.get("END_TIME").toString();
            Date nowDate = DateUtil.getNowDate();
            if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s1), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s1), Double.parseDouble(end)).getTime()) {
                userId = htjcMap.get("DAY_USER").toString();
            } else {
                userId = htjcMap.get("NIGHT_USER").toString();
            }
            String[] split = userId.split(",");
            for (int i = 0; i < split.length; i++) {
                String sql = "SELECT LOGINSTATUS status FROM RZTSYSUSER where id=?";
                Map<String, Object> status = this.execSqlSingleResult(sql, split[i]);
                if (status.get("STATUS").toString().equals("1")) {
                    a++;
                } else {
                    b++;
                }
            }
            dept.put("htzx", a);
            dept.put("htlx", b);


            xsMap = this.execSql(xs).size()==0?new HashedMap():this.execSql(xs).get(0);
            khMap = this.execSql(kh).size()==0?new HashedMap():this.execSql(kh).get(0);
            xcjcMap = this.execSql(xcjc).size()==0?new HashedMap():this.execSql(xcjc).get(0);





            int XSZX = Integer.parseInt(xsMap.get("XSZX")==null?"0":xsMap.get("XSZX").toString());
            int KHZX = Integer.parseInt(khMap.get("KHZX")==null?"0":khMap.get("KHZX").toString());
            int ZXJCZX = Integer.parseInt(xcjcMap.get("ZXJCZX")==null?"0":xcjcMap.get("ZXJCZX").toString());
            int HTJCZX =a;

            int XSLX = Integer.parseInt(xsMap.get("XSLX")==null?"0":xsMap.get("XSLX").toString());
            int KHLX = Integer.parseInt(khMap.get("KHLX")==null?"0":khMap.get("KHLX").toString());
            int ZXJCLX = Integer.parseInt(xcjcMap.get("ZXJCLX")==null?"0":xcjcMap.get("ZXJCLX").toString());
            int HTJCLX = b;


            HashMap<String, Object> hashMap = new HashMap<>();

            int LOGIN = XSZX + KHZX + ZXJCZX + HTJCZX;
            int OFF_LINE =  XSLX + KHLX + ZXJCLX + HTJCLX ;
            hashMap.put("SUM", LOGIN + OFF_LINE);
            hashMap.put("LOGIN", LOGIN);
            hashMap.put("OFF_LINE", OFF_LINE);

            hashMap.put("XSZX", XSZX);
            hashMap.put("XSLX", XSLX);
            hashMap.put("KHZX", KHZX);
            hashMap.put("KHLX", KHLX);
            hashMap.put("ZXJCZX", ZXJCZX);
            hashMap.put("ZXJCLX", ZXJCLX);
            hashMap.put("HTJCZX", HTJCZX);
            hashMap.put("HTJCLX", HTJCLX);

            return WebApiResponse.success(hashMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return WebApiResponse.erro(e.getMessage());
        }
    }


    public WebApiResponse findUserPic(String userId) {
        String sql = "SELECT AVATAR FROM RZTSYSUSER WHERE ID = '"+userId+"' ";
        try {
            Map<String, Object> map = this.execSqlSingleResult(sql);
            return WebApiResponse.success(map);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return WebApiResponse.erro(e.getMessage());
        }

    }

    public WebApiResponse getDayUser(String deptId) {

        try {
            String sql = "SELECT (" +
                    "  (SELECT count(1) FROM (" +
                    //巡视今日应执行任务人数
                    "    SELECT count(1) FROM XS_ZC_TASK x LEFT JOIN RZTSYSUSER u ON u.ID=x.CM_USER_ID" +
                    "    WHERE u.DEPTID='"+deptId+"' AND trunc(x.PLAN_START_TIME) = trunc(sysdate)" +
                    "    GROUP BY u.ID)) +" +
                    //看护今日应执行任务人数
                    "  (SELECT COUNT(1) FROM (SELECT count(k.USER_ID)" +
                    "                         FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID" +
                    "                         where  u.DEPTID='"+deptId+"' AND trunc(k.PLAN_START_TIME) = trunc(sysdate)" +
                    "                         GROUP BY k.USER_ID))+" +
                    //现场稽查今日应执行任务人数
                    "  (SELECT COUNT(1) FROM (SELECT count(c.USER_ID) FROM CHECK_LIVE_TASK c LEFT JOIN RZTSYSUSER u ON u.ID = c.USER_ID" +
                    "  where  u.DEPTID= '"+deptId+"' AND trunc(c.PLAN_START_TIME) = trunc(sysdate)" +
                    "  GROUP BY c.USER_ID)) +6" +// 后台稽查人
                    "   ) AS sum FROM dual";
            Map<String, Object> map = this.execSqlSingleResult(sql);
            return  WebApiResponse.success(map.get("SUM"));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return WebApiResponse.erro(e.getMessage());
        }


    }
}
