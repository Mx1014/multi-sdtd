package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("USERDISPLAYSYNTHESIS")
public class UserDisplaySynthesisController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @PersistenceContext
    EntityManager entityManager;

    @RequestMapping("userdisplaysynthesisList")
    public WebApiResponse userdisplaysynthesisList(String currentUserId, String startTime, String endTime, String deptId) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
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
        if (roletype == 1 || roletype == 2) {
            s2 += " AND r.DEPTID= '" + deptid + "' ";
            s4 += " AND dept_id= '" + deptid + "' ";
            s5 += " and id='" + deptid + "' ";
        }
        if (!StringUtils.isEmpty(deptId)) {
            s2 += " AND r.DEPTID= '" + deptId + "' ";
            s4 += " AND dept_id= '" + deptId + "' ";
            s5 += " and id='" + deptId + "' ";
        }
        Map<String, Map> xsMap = new HashMap();
        Map<String, Map> khMap = new HashMap();
        Map<String, Map> xcjcMap = new HashMap();
        Map<String, Map> htjcMap = new HashMap();
        String xs = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx," +
                "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                " FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID " +
                " WHERE USERDELETE = 1  and z.is_delete = 0 " + s + s2 +
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
            String s = "";
            String s2 = "";
            String s3 = "";
            String s4 = "";
            if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
                listLike.add(endTime);
                s += " AND z.PLAN_END_TIME >= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
                s3 += " AND z.PLAN_END_TIME >= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
                listLike.add(startTime);
                s += " PLAN_START_TIME <= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
                s3 += " AND z.PLAN_START_TIME <= to_date(?,'yyyy-mm-dd hh24:mi:ss') ";
            } else {
                s += " AND PLAN_END_TIME >= trunc( SYSDATE ) AND PLAN_START_TIME <= trunc(sysdate+1) ";
                s3 += " and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > z.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < z.plan_end_time ";
            }
            /*if (roletype == 1 || roletype == 2) {
                s2 += " AND r.DEPTID= '" + deptid + "' ";
                s4 += " AND dept_id= '" + deptid + "' ";
            }*/
            if (!StringUtils.isEmpty(deptId)) {
                s2 += " AND r.DEPTID= '" + deptId + "' ";
                s4 += " AND dept_id= '" + deptId + "' ";
            }
            String xsLogin = "SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) xszx,\n" +
                    "                  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) xslx,DEPTID FROM (SELECT CM_USER_ID,DEPTID,CLASSNAME,LOGINSTATUS\n" +
                    "                 FROM RZTSYSUSER r RIGHT JOIN XS_ZC_TASK z ON r.ID = z.CM_USER_ID\n" +
                    "                 WHERE USERDELETE = 1 " + s + s2 +
                    "                 GROUP BY DEPTID,CM_USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID";

            String khLogin = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) khzx," +
                    "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) khlx,DEPTID FROM (SELECT USER_ID,DEPTID,CLASSNAME,LOGINSTATUS " +
                    " FROM RZTSYSUSER r RIGHT JOIN KH_TASK z ON r.ID = z.USER_ID " +
                    " WHERE USERDELETE = 1  " + s + s2 +
                    " GROUP BY DEPTID,USER_ID,CLASSNAME,LOGINSTATUS ) GROUP BY DEPTID ";
            String xcjcLogin = " SELECT nvl(sum(decode(LOGINSTATUS,1,1,0)),0) zxjczx," +
                    "  nvl(sum(decode(LOGINSTATUS,0,1,0)),0) zxjclx ,DEPTID FROM (SELECT z.USER_ID AS USERID,DEPTID,CLASSNAME,LOGINSTATUS " +
                    " FROM RZTSYSUSER r RIGHT JOIN CHECK_LIVE_TASK z ON r.ID = z.USER_ID " +
                    " WHERE  USERDELETE = 1   " + s3 + s2 +
                    "  GROUP BY z.USER_ID, DEPTID, CLASSNAME, LOGINSTATUS) GROUP BY DEPTID ";
            Map<String, Object> htjcMap = new HashMap<>();
            try {
                String user = "SELECT * FROM WORKING_TIMED where 1=1 " + s4;
                Map<String, Object> map = this.service.execSqlSingleResult(user);
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
                htjcMap.put("htzx", a);
                htjcMap.put("htlx", b);
                htjcMap.put("DEPTID", map.get("DEPT_ID"));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            }
            Map<String, Object> xcLoginMap = null;
            try {
                xcLoginMap = this.service.execSqlSingleResult(xcjcLogin, listLike.toArray());
            } catch (Exception e) {
            }
            Map<Object, Object> map = new HashMap<>();
            map.put("xsls", Integer.parseInt(xsLoginMap == null ? "0" : xsLoginMap.get("XSLX").toString()));
            map.put("xszx", Integer.parseInt(xsLoginMap == null ? "0" : xsLoginMap.get("XSZX").toString()));
            map.put("khlx", Integer.parseInt(khLoginMap == null ? "0" : khLoginMap.get("KHLX").toString()));
            map.put("khzx", Integer.parseInt(khLoginMap == null ? "0" : khLoginMap.get("KHZX").toString()));
            map.put("xcjclx", Integer.parseInt(xcLoginMap == null ? "0" : xcLoginMap.get("ZXJCLX").toString()));
            map.put("xcjczx", Integer.parseInt(xcLoginMap == null ? "0" : xcLoginMap.get("ZXJCZX").toString()));
            map.put("htjczx", Integer.parseInt(htjcMap == null ? "0" : htjcMap.get("htzx").toString()));
            map.put("htjclx", Integer.parseInt(htjcMap == null ? "0" : htjcMap.get("htlx").toString()));
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

    @GetMapping("htjcList")
    public WebApiResponse htjcList(String loginType, String deptId,Integer page,Integer size){
        try {
            String s1 = "";
            if (!StringUtils.isEmpty(deptId)){
                s1 += " where dept_id ='"+deptId+"' ";
            }
            Map<Object, Object> returnMap = new HashMap<>();
            List<Object> list = new ArrayList<>();
            try {
                String user = "SELECT * FROM WORKING_TIMED "+s1;
                List<Map<String, Object>> maps = this.service.execSql(user);
                for (Map map : maps) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String format = formatter.format(new Date());
                    String s = format + " 00:00:00";
                    String userId = "";
                    String start = map.get("START_TIME").toString();
                    String end = map.get("END_TIME").toString();
                    Date nowDate = DateUtil.getNowDate();
                    if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(end)).getTime()) {
                        userId += map.get("DAY_USER").toString();
                    } else {
                        userId += map.get("NIGHT_USER").toString();
                    }
                    String[] split = userId.split(",");
                    for (int i = 0; i < split.length; i++) {
                        String sql = "SELECT d.deptname,u.*  FROM RZTSYSUSER u left join rztsysdepartment d on d.id = u.deptid where u.id=? order by d.deptname ";
                        Map<String, Object> users = this.service.execSqlSingleResult(sql, split[i]);
                        if(loginType!=null && !loginType.equals("")){
                            if (users.get("LOGINSTATUS").toString().equals(loginType)) {
                                list.add(users);
                            }
                        }else {
//                            this.service.execSqlPage()
                            list.add(users);
                        }
                    }
                }
                List returnList = new ArrayList();
                int s = list.size()-page*size>size?((page+1)*size):list.size();
                for (int i=page*size;i<(list.size()-page*size>size?((page+1)*size):list.size());i++){
                    returnList.add(list.get(i));
                }
                returnMap.put("content",returnList);
                returnMap.put("totalElements",list.size());
                returnMap.put("totalPages",(list.size()%size==0?list.size()/size:list.size()/size+1));
                returnMap.put("number",page);
                returnMap.put("size",size);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return WebApiResponse.success(returnMap);
        }catch (Exception e){
            return WebApiResponse.erro("失败");
        }
    }

   /* public Page<Map<String, Object>> execQuerySqlPage(Pageable pageable,Long sizeQuery) {
        Query q = this.entityManager.createNativeQuery(sql);
        Query sizeQuery = this.entityManager.createNativeQuery("select count(*) from (" + sql + ")");
        if(objects != null && objects.length > 0) {
            for(int i = 0; i < objects.length; ++i) {
                q.setParameter(i + 1, objects[i]);
                sizeQuery.setParameter(i + 1, objects[i]);
            }
        }

        long size = 0L;
        if(pageable != null) {
            size = Long.valueOf(sizeQuery.getResultList().get(0).toString()).longValue();
            q.setFirstResult(pageable.getOffset());
            q.setMaxResults(pageable.getPageSize());
        }

        ((SQLQuery)q.unwrap(SQLQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return pageable == null?new PageImpl(q.getResultList()):new PageImpl(q.getResultList(), pageable, size);
    }*/
}

