package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("USERDISPLAYSYNTHESIS")
public class UserDisplaySynthesisController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("userdisplaysynthesisList")
    public WebApiResponse userdisplaysynthesisList(String currentUserId, String startTime, String endTime, String deptId) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        List listLike = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(endTime);
            s += " AND z.PLAN_END_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(startTime);
            s += " PLAN_START_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND PLAN_END_TIME >= trunc( SYSDATE ) AND PLAN_START_TIME <= trunc(sysdate+1) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND r.DEPTID= ?" + listLike.size();
        }
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

    @GetMapping("userLoginTypeZhu")
    public WebApiResponse userLoginTypeZhu(String currentUserId, String startTime, String endTime, String deptId) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
            int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
            Object deptid = jsonObject.get("DEPTID");
            List listLike = new ArrayList();
            StringBuffer buffer = new StringBuffer();
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                listLike.add(endTime);
                buffer.append(" AND z.PLAN_END_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ");
                listLike.add(startTime);
                buffer.append(" PLAN_START_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ");
            } else {
                buffer.append(" AND PLAN_END_TIME >= trunc( SYSDATE ) AND PLAN_START_TIME <= trunc(sysdate+1) ");
            }
            if (roletype == 1 || roletype == 2) {
                listLike.add(deptid);
                buffer.append(" AND r.DEPTID= ?" + listLike.size());
            }
            if (!StringUtils.isEmpty(deptId)) {
                listLike.add(deptId);
                buffer.append(" AND r.DEPTID= ?" + listLike.size());
            }
            String xsLogin = "SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx,\n" +
                    "                  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS\n" +
                    "                 FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID\n" +
                    "                 WHERE USERDELETE = 1 " + buffer.toString() +
                    "                 GROUP BY DEPTID,CM_USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID";
            String khLogin = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) khzx," +
                    "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) khlx,DEPTID FROM (SELECT USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                    " FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                    " WHERE USERDELETE = 1  " + buffer.toString() +
                    " GROUP BY DEPTID,USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
            String xcjcLogin = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) zxjczx," +
                    "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) zxjclx ,DEPTID FROM (SELECT z.USER_ID AS USERID,DEPTID,CLASSNAME,LOGINSTATUS " +
                    " FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " +
                    " WHERE  USERDELETE = 1  " + buffer.toString() +
                    " GROUP BY z.USER_ID,DEPTID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
            String htjcLogin = "";
            Map<String, Object> xsLoginMap = null;
            try {
                xsLoginMap = this.service.execSqlSingleResult(xsLogin, listLike.toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<String, Object> khLoginMap = null;
            try {
                khLoginMap = this.service.execSqlSingleResult(khLogin, listLike.toArray());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Map<Object, Object> map = new HashMap<>();
            map.put("xcjclx",0);
            map.put("xcjczx",0);
            map.put("htjczx", 0);
            map.put("htjclx", 0);
            try {
                Map<String, Object> xcjcLoginMap = this.service.execSqlSingleResult(xcjcLogin, listLike.toArray());
                map.put("xcjclx", Integer.parseInt(xcjcLoginMap.get("ZXJCLX").toString()));
                map.put("xcjczx", Integer.parseInt(xcjcLoginMap.get("ZXJCZX").toString()));
            } catch (Exception e) {

            }

            map.put("xsls", Integer.parseInt(xsLoginMap == null?"0":xsLoginMap.get("XSLX").toString()));
            map.put("xszx", Integer.parseInt(xsLoginMap == null?"0":xsLoginMap.get("XSZX").toString()));
            map.put("khlx", Integer.parseInt(khLoginMap == null?"0":khLoginMap.get("KHLX").toString()));
            map.put("khzx", Integer.parseInt(khLoginMap == null?"0":khLoginMap.get("KHZX").toString()));

            return WebApiResponse.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    @GetMapping("userLoginTypeList")
    public Object userLoginTypeList(String currentUserId, String startTime, String endTime, String deptId) {
        new StringBuffer("select ");
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        List listLike = new ArrayList();
        StringBuffer buffer = new StringBuffer();
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(endTime);
            buffer.append(" AND z.PLAN_END_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ");
            listLike.add(startTime);
            buffer.append("AND PLAN_START_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ");
        } else {
            buffer.append(" AND PLAN_END_TIME >= trunc( SYSDATE ) AND PLAN_START_TIME <= trunc(sysdate+1) ");
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            buffer.append(" AND r.DEPTID= ?" + listLike.size());
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            buffer.append(" AND r.DEPTID= ?" + listLike.size());
        }
        return 1;
    }
}
