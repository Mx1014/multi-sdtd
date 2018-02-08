package com.rzt.controller;

import com.rzt.entity.KHYHHISTORY;
import com.rzt.service.UserService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 李成阳
 * 2018/1/31
 * 管理app用户数据统计
 */
@RestController
@RequestMapping("/users")
public class UserController extends CurdController<KHYHHISTORY, UserService> {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 二级页使用
     * 查询在线人数 离线人数  总人数
     *
     * @return
     */
    @GetMapping("/findUserInfoTwo")
    public WebApiResponse findUserSum(String deptId) {
        return userService.findUser(deptId);
    }

    /**
     * 一级页面用户信息查询
     * 按照部门分组
     *
     * @return
     */
    @GetMapping("/findUserSum")
    public WebApiResponse findUserInfoTwo(String deptId) {
        return userService.findUserInfoOne(deptId);
    }

    /**
     * 三级页面用户信息查询
     * 根据部门查询
     *
     * @return
     */
    @GetMapping("/findUserInfoThree")
    public WebApiResponse findUserInfoThree(String deptId) {
        return userService.findUserInfoThree(deptId);
    }

    /**
     * 四级页面  查看当前单位所有人 关联任务
     *
     * @param deptId      部门
     * @param loginStatus 在线状态 0不在  1在
     * @return
     */
    @GetMapping("/findUserInfoUserAndTask")
    public WebApiResponse findUserInfoUserAndTask(Integer page, Integer size, String deptId, String loginStatus) {
        return userService.findUserAndTask(page, size, deptId, loginStatus);
    }

    /**
     * 五级页面 查询用户详细信息
     *
     * @return
     */
    @GetMapping("/findUserInfo")
    public WebApiResponse findUserInfo(String userId) {
        return userService.findUserInfo(userId);
    }





    @RequestMapping("userdisplaysynthesisList")
    public WebApiResponse userdisplaysynthesisList(String currentUserId, String startTime, String endTime, String deptId) {
        List listLike = new ArrayList();
        String s = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND z.PLAN_END_TIME >= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
            s3 += " AND z.PLAN_END_TIME >= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND z.PLAN_START_TIME <= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
            s3 += " AND z.PLAN_START_TIME <= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND z.PLAN_END_TIME >= trunc( SYSDATE ) AND z.PLAN_START_TIME <= trunc(sysdate+1) ";
            s3 += " and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > z.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < z.plan_end_time ";
        }

        Map<String, Map> xsMap = new HashMap();
        Map<String, Map> khMap = new HashMap();
        Map<String, Map> xcjcMap = new HashMap();
        Map<String, Map> htjcMap = new HashMap();
        String xs = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                " WHERE USERDELETE = 1  " + s + s2 +
                " GROUP BY DEPTID,CM_USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String kh = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) khzx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) khlx,DEPTID FROM (SELECT USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                " WHERE USERDELETE = 1  " + s + s2 +
                " GROUP BY DEPTID,USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String xcjc = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) zxjczx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) zxjclx ,DEPTID FROM (SELECT z.USER_ID AS USERID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " + s3 + s2 +
                " WHERE  USERDELETE = 1   " +
                " ) GROUP BY DEPTID ";
        List<Map<String, Object>> htlist = new ArrayList<>();
        try {
            String user = "SELECT * FROM WORKING_TIMED where 1=1 " + s4;
            List<Map<String, Object>> maps = this.service.execSql(user);
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
                    Map<String, Object> status = this.service.execSqlSingleResult(sql, split[i]);
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
        String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL " + s5 + "ORDER BY t.DEPTSORT ";
        List<Map<String, Object>> xsList = this.service.execSql(xs, listLike.toArray());
        List<Map<String, Object>> khList = this.service.execSql(kh, listLike.toArray());
        List<Map<String, Object>> xcjcList = this.service.execSql(xcjc, listLike.toArray());
        List<Map<String, Object>> deptnameList = this.service.execSql(deptname);
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
            return WebApiResponse.success(deptnameList);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }




    @RequestMapping("users")
    public WebApiResponse users (String currentUserId, String startTime, String endTime, String deptId) {
        List listLike = new ArrayList();
        String s = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";

            s += " AND z.PLAN_END_TIME >= trunc( SYSDATE ) AND z.PLAN_START_TIME <= trunc(sysdate+1) ";
            s3 += " and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > z.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < z.plan_end_time ";


        Map<String, Map> xsMap = new HashMap();
        Map<String, Map> khMap = new HashMap();
        Map<String, Map> xcjcMap = new HashMap();
        Map<String, Map> htjcMap = new HashMap();
        String xs = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                " WHERE USERDELETE = 1  " + s + s2 +
                " GROUP BY DEPTID,CM_USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String kh = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) khzx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) khlx,DEPTID FROM (SELECT USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                " WHERE USERDELETE = 1  " + s + s2 +
                " GROUP BY DEPTID,USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
        String xcjc = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) zxjczx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) zxjclx ,DEPTID FROM (SELECT z.USER_ID AS USERID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " + s3 + s2 +
                " WHERE  USERDELETE = 1   GROUP BY z.USER_ID, DEPTID, CLASSNAME, LOGINSTATUS " +
        " ) GROUP BY DEPTID ";
        List<Map<String, Object>> htlist = new ArrayList<>();
        try {
            String user = "SELECT * FROM WORKING_TIMED where 1=1 " + s4;
            List<Map<String, Object>> maps = this.service.execSql(user);
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
                    Map<String, Object> status = this.service.execSqlSingleResult(sql, split[i]);
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
        String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL " + s5 + "ORDER BY t.DEPTSORT ";
        List<Map<String, Object>> xsList = this.service.execSql(xs, listLike.toArray());
        List<Map<String, Object>> khList = this.service.execSql(kh, listLike.toArray());
        List<Map<String, Object>> xcjcList = this.service.execSql(xcjc, listLike.toArray());
        List<Map<String, Object>> deptnameList = this.service.execSql(deptname);
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



}

