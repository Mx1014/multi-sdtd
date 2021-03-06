package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.DateUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.swagger.models.auth.In;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("CompanyNumTasks")
public class CompanyNumTasksController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @GetMapping("CompanyNumTask")
    public WebApiResponse CompanyNumTask(String currentUserId, Date day, String deptId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        JSONObject jsonObject = JSONObject.parseObject(String.valueOf(hashOperations.get("UserInformation", currentUserId)));
        Integer type = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        if (day == null) {
            day = new Date();
        }
        if (type == 0) {
            return CompanyNumTaskYj(day, deptId);
        } else if (type == 1 || type == 2) {
            String deptid = jsonObject.get("DEPTID").toString();
            return CompanyAllTasks(deptid, day);
        }
        return WebApiResponse.erro("NULL");
    }

    public WebApiResponse CompanyNumTaskYj(Date day, String deptId) {
        List<Map<String, Object>> deptnameList = null;
        try {
            Map<String, Integer> map1 = new HashMap();
            Map<String, Integer> map2 = new HashMap();
            Map<String, Integer> map3 = new HashMap();
            Map<String, Integer> map4 = new HashMap();
            Map<String, Integer> map5 = new HashMap();
            String s = "";
            String s1 = "";
            String s2 = "";
            String s3 = "";
            String s4 = "";
            if (!StringUtils.isEmpty(deptId)) {
                s += " and TD_ORG='" + deptId + "' ";
                s1 += " and yworg_id='" + deptId + "' ";
                s2 += " and u.deptid='" + deptId + "' ";
                s3 += " and DEPT_ID='" + deptId + "' ";
                s4 += " and id='" + deptId + "' ";
            }
            String zcXsZs = " SELECT count(1) AS num,TD_ORG FROM XS_ZC_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + s + " GROUP BY TD_ORG ";
            List<Map<String, Object>> zcXsZsList = this.service.execSql(zcXsZs, day);
            String bdXsZs = " SELECT count(1) AS num,TD_ORG FROM XS_TXBD_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + s + " GROUP BY TD_ORG ";
            List<Map<String, Object>> bdXsZsList = this.service.execSql(bdXsZs, day);
            String khZs = " SELECT nvl(a.num,0) as num,d.ID as TD_ORG FROM (SELECT COUNT(1) as num,TDYW_ORG FROM KH_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + s1 + " GROUP BY TDYW_ORG) a RIGHT JOIN RZTSYSDEPARTMENT d ON a.TDYW_ORG = d.DEPTNAME WHERE DEPTSORT IS NOT NULL ";
            List<Map<String, Object>> khZsList = this.service.execSql(khZs, day);
            String xcjczs = "SELECT count(1) as num,u.deptid FROM CHECK_LIVE_TASK t left join rztsysuser u on u.id = t.user_id WHERE to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > t.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < t.plan_end_time " + s2 + " group by u.deptid ";
            List<Map<String, Object>> xcjcist = this.service.execSql(xcjczs);
            String htjczs = "SELECT count(*) as num,DEPT_ID as td_org FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) " + s3 + " GROUP BY DEPT_ID";
            List<Map<String, Object>> htList = this.service.execSql(htjczs);
            String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL " + s4 + " ORDER BY t.DEPTSORT ";
            deptnameList = this.service.execSql(deptname);
            for (int i = 0; i < zcXsZsList.size(); i++) {
                map1.put(zcXsZsList.get(i).get("TD_ORG").toString(), Integer.parseInt(zcXsZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < bdXsZsList.size(); i++) {
                map2.put(bdXsZsList.get(i).get("TD_ORG").toString(), Integer.parseInt(bdXsZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < khZsList.size(); i++) {
                map3.put(khZsList.get(i).get("TD_ORG").toString(), Integer.parseInt(khZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < xcjcist.size(); i++) {
                map4.put(xcjcist.get(i).get("DEPTID").toString(), Integer.parseInt(xcjcist.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < htList.size(); i++) {
                map5.put(htList.get(i).get("TD_ORG").toString(), Integer.parseInt(htList.get(i).get("NUM").toString()));
            }
            for (Map<String, Object> map : deptnameList) {
                Integer zcXsS = 0;
                Object id = map.get("ID");
                Integer zcxs = map1.get(id);
                if (!StringUtils.isEmpty(zcxs)) {
                    zcXsS = zcxs;
                }
                Integer bdXsS = 0;
                Integer bdxs = map2.get(id);
                if (!StringUtils.isEmpty(bdxs)) {
                    bdXsS = bdxs;
                }
                Integer khzsL = 0;
                Integer khzx = map3.get(id);
                if (!StringUtils.isEmpty(khzx)) {
                    khzsL = khzx;
                }
                Integer xcjczsL = 0;
                Integer jczx = map4.get(id);
                if (!StringUtils.isEmpty(jczx)) {
                    xcjczsL = jczx;
                }
                Integer htjczsL = 0;
                Integer htzx = map5.get(id);
                if (!StringUtils.isEmpty(htzx)) {
                    htjczsL = htzx;
                }

                map.put("rwzs", zcXsS + bdXsS + khzsL + xcjczsL + htjczsL);
                map.put("xszs", zcXsS + bdXsS);
                map.put("khzs", khzsL);
                map.put("xcjczs", xcjczsL);
                map.put("htjczs", htjczsL + 1);
            }
            return WebApiResponse.success(deptnameList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    //二级综合展示数据
    public WebApiResponse CompanyAllTasks(String deptid, Date day) {
        if (StringUtils.isEmpty(deptid)) {
            return WebApiResponse.erro("Null");
        }
        try {
            Map<String, Integer> map1 = new HashMap();
            Map<String, Integer> map2 = new HashMap();
            Map<String, Integer> map3 = new HashMap();
            Map<String, Integer> map4 = new HashMap();
            Map<String, Integer> map5 = new HashMap();
            String className = " SELECT ID,DEPTNAME FROM (SELECT ID,DEPTNAME,LASTNODE FROM RZTSYSDEPARTMENT START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0 ";
            List<Map<String, Object>> classNameList = this.service.execSql(className, deptid);
            String calssZcXsZs = " SELECT count(1) AS num,CLASS_ID FROM XS_ZC_TASK WHERE PLAN_END_TIME >= trunc(?2) and  PLAN_START_TIME <= trunc(?2+1) AND TD_ORG = ?1 GROUP BY CLASS_ID ";
            List<Map<String, Object>> calssZcXsZsList = this.service.execSql(calssZcXsZs, deptid, day);
            String calssBdXsZs = " SELECT count(1) AS num,CLASS_ID FROM XS_txbd_TASK WHERE PLAN_END_TIME >= trunc(?2) and  PLAN_START_TIME <= trunc(?2+1) AND TD_ORG = ?1 GROUP BY CLASS_ID ";
            List<Map<String, Object>> calssBdXsZsList = this.service.execSql(calssBdXsZs, deptid, day);
            String calssKhZs = " SELECT count(1) as num,CLASSNAME FROM (SELECT u.CLASSNAME,u.DEPTID FROM KH_TASK k LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID WHERE PLAN_END_TIME >= trunc(?2) and  PLAN_START_TIME <= trunc(?2+1)) WHERE DEPTID = ?1 GROUP BY CLASSNAME ";
            String xcjczs = "SELECT count(1) as num,u.CLASSNAME FROM CHECK_LIVE_TASK t left join rztsysuser u on u.id = t.user_id WHERE to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > t.plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < t.plan_end_time and u.deptid=? group by u.CLASSNAME ";
            List<Map<String, Object>> xcjcist = this.service.execSql(xcjczs, deptid);
            String htjczs = "SELECT\n" +
                    "  count(u.CLASSNAME) as num ,u.CLASSNAME classname \n" +
                    "FROM TIMED_TASK_RECORD r LEFT JOIN RZTSYSUSER u ON instr(r.EX_USER, u.id) > 0\n" +
                    "WHERE r.CREATE_TIME >= trunc(sysdate) AND DEPT_ID = '" + deptid + "'\n" +
                    "GROUP BY u.CLASSNAME\n";
            List<Map<String, Object>> htList = this.service.execSql(htjczs);
            List<Map<String, Object>> calssKhZsList = this.service.execSql(calssKhZs, deptid, day);
            for (int i = 0; i < calssZcXsZsList.size(); i++) {
                if (calssZcXsZsList.get(i).get("CLASS_ID") != null) {
                    map1.put(calssZcXsZsList.get(i).get("CLASS_ID").toString(), Integer.parseInt(calssZcXsZsList.get(i).get("NUM").toString()));
                }
            }
            for (int i = 0; i < calssBdXsZsList.size(); i++) {
                map2.put(calssBdXsZsList.get(i).get("CLASS_ID").toString(), Integer.parseInt(calssBdXsZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < calssKhZsList.size(); i++) {
                map3.put(calssKhZsList.get(i).get("CLASSNAME").toString(), Integer.parseInt(calssKhZsList.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < xcjcist.size(); i++) {
                map4.put(xcjcist.get(i).get("CLASSNAME").toString(), Integer.parseInt(xcjcist.get(i).get("NUM").toString()));
            }
            for (int i = 0; i < htList.size(); i++) {
                map5.put(htList.get(i).get("CLASSNAME").toString(), Integer.parseInt(htList.get(i).get("NUM").toString()) / 3);
            }
            for (Map map : classNameList) {
                Integer zcXsS = 0;
                Object id = map.get("ID");
                Integer zcxs = map1.get(id);
                if (!StringUtils.isEmpty(zcxs)) {
                    zcXsS = zcxs;
                }

                Integer bdXsS = 0;
                Integer bdxs = map2.get(id);
                if (!StringUtils.isEmpty(bdxs)) {
                    bdXsS = bdxs;
                }
                Integer KhZs = 0;
                Integer KhZss = map3.get(id);
                if (!StringUtils.isEmpty(KhZss)) {
                    KhZs = KhZss;
                }
                Integer xcjczsL = 0;
                Integer jczx = map4.get(id);
                if (!StringUtils.isEmpty(jczx)) {
                    xcjczsL = jczx;
                }
                Integer htjczsL = 0;
                Integer htzx = map5.get(id);
                if (!StringUtils.isEmpty(htzx)) {
                    htjczsL = htzx;
                }
                map.put("xszs", zcXsS + bdXsS);
                map.put("khzs", KhZs);
                map.put("xcjczs", xcjczsL);
                map.put("rwzs", zcXsS + bdXsS + KhZs + xcjczsL + htjczsL);
                map.put("htjczs", htjczsL);
                if (map.get("DEPTNAME").toString().startsWith("后台稽查")) {
                    String user = "SELECT * FROM WORKING_TIMED WHERE DEPT_ID='" + deptid + "'";
                    Map<String, Object> map6 = new HashMap<>();
                    try {
                        map6 = this.service.execSqlSingleResult(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String format = formatter.format(new Date());
                    String s = format + " 00:00:00";
                    String start = map6.get("START_TIME").toString();
                    String end = map6.get("END_TIME").toString();
                    Date nowDate = DateUtil.getNowDate();
                    if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(end)).getTime()) {
                        if (map.get("DEPTNAME").toString().contains("白")) {
                            map.put("rwzs", zcXsS + bdXsS + KhZs + xcjczsL + htjczsL + 1);
                            map.put("htjczs", htjczsL + 1);
                        }
                    } else {
                        if (map.get("DEPTNAME").toString().contains("夜")) {
                            map.put("rwzs", zcXsS + bdXsS + KhZs + xcjczsL + htjczsL + 1);
                            map.put("htjczs", htjczsL + 1);
                        }
                    }
                }
            }
            return WebApiResponse.success(classNameList);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @GetMapping("deptZhu")
    public WebApiResponse deptZhu(String id, Date day, String currentUserId) {
        try {
            Map map = new HashMap();
           /* if (startDate == null && endDate == null) {
                startDate = timeUtil(1);
                endDate = timeUtil(2);
            }*/
            if (day == null) {
                day = new Date();
            }
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(hashOperations.get("UserInformation", currentUserId)));
            Integer type = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
            String xsField = "TD_ORG";
            String khField = "DEPTID";
            String htField = "r.dept_id=?";
            int num = 1;
            if (type != 0) {
                xsField = "class_id";
                khField = "classname";
                htField = " instr(EX_USER,(SELECT max(ID) " +
                        "FROM RZTSYSUSER WHERE CLASSNAME = ?)) >0";
                String sql1 = "select deptname from rztsysdepartment where id = '" + id + "'";
                Map<String, Object> map1 = this.service.execSqlSingleResult(sql1);
                if (map1.get("DEPTNAME").toString().equals("后台稽查夜班") || map1.get("DEPTNAME").toString().equals("后台稽查白班")) {
                    num = 1;
                } else {
                    num = 0;
                }
            }

            //正常
            String xszc = " SELECT nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_ZC_TASK WHERE PLAN_END_TIME >= trunc(?2) and  PLAN_START_TIME <= trunc(?2+1) AND " + xsField + " = ?1 ";
            //String xszc = " SELECT nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_ZC_TASK WHERE  PLAN_START_TIME between ?2 and ?3 AND " + xsField + " = ?1 ";
            Map<String, Object> xszcMap = this.service.execSqlSingleResult(xszc, id, day);
            //保电
            String txbd = " SELECT nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_txbd_TASK WHERE PLAN_END_TIME >= trunc(?2) and  PLAN_START_TIME <= trunc(?2+1) AND " + xsField + " = ?1 ";
            // String txbd = " SELECT nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_txbd_TASK WHERE  PLAN_START_TIME between ?2 and ?3 AND" + xsField + " = ?1 ";
            Map<String, Object> txbdMap = this.service.execSqlSingleResult(txbd, id, day);
            //看护
            String kh = "SELECT nvl(sum(decode(status, 0, 1, 0)),0) KHWKS,nvl(sum(decode(status, 1, 1, 0)),0) KHJXZ,nvl(sum(decode(status, 2, 1, 0)),0) KHYWC FROM KH_TASK k JOIN RZTSYSUSER u ON k.USER_ID = u.ID and PLAN_END_TIME >= trunc(?2) and  PLAN_START_TIME <= trunc(?2+1) and u." + khField + " = ?1 ";
            Map<String, Object> khMap = this.service.execSqlSingleResult(kh, id, day);
            //现场稽查
            String xcjc = "SELECT nvl(sum(decode(status, 0, 1, 0)),0) JCWKS,nvl(sum(decode(status, 1, 1, 0)),0) JCJXZ,nvl(sum(decode(status, 2, 1, 0)),0) JCYWC FROM CHECK_LIVE_TASK K  JOIN RZTSYSUSER u ON k.USER_ID = u.ID and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') <plan_end_time and u." + khField + " = ?1 ";
            Map<String, Object> xcjcMap = this.service.execSqlSingleResult(xcjc, id);
            /**
             *后台稽查未完成
             */
            String htJcWks = "SELECT count(*) FROM TIMED_TASK_RECORD R LEFT JOIN RZTSYSDEPARTMENT D ON D.ID=R.DEPT_ID WHERE R.CREATE_TIME >= trunc(sysdate) and (R.TASKS>R.COMPLETE) and " + htField;
            /**
             *后台稽查进行中
             */
            String htJcYks = "SELECT count(DISTINCT (R.DEPT_ID)) FROM TIMED_TASK_RECORD R LEFT JOIN RZTSYSDEPARTMENT D ON D.ID=R.DEPT_ID and " + htField;
            /**
             *后台稽查已完成
             */
            String htJcYwc = "SELECT count(*) FROM TIMED_TASK_RECORD R LEFT JOIN RZTSYSDEPARTMENT D ON D.ID=R.DEPT_ID WHERE R.CREATE_TIME >= trunc(sysdate) and (R.TASKS=R.COMPLETE) and " + htField;
            String htjc = "SELECT " + "(" + htJcWks + ") as htJcWks, " +
                    "(" + num + ") as htJcYks, " +
                    "(" + htJcYwc + ") as htJcYwc " +
                    "  FROM dual";
            Map<String, Object> htMap = this.service.execSqlSingleResult(htjc, id, id);
            map.put("xswks", Integer.parseInt(xszcMap.get("XSWKS").toString()) + Integer.parseInt(txbdMap.get("XSWKS").toString()));
            map.put("xsjxz", Integer.parseInt(xszcMap.get("XSJXZ").toString()) + Integer.parseInt(txbdMap.get("XSJXZ").toString()));
            map.put("xsywc", Integer.parseInt(xszcMap.get("XSYWC").toString()) + Integer.parseInt(txbdMap.get("XSYWC").toString()));

            map.put("khwks", Integer.parseInt(khMap.get("KHWKS").toString()));
            map.put("khjxz", Integer.parseInt(khMap.get("KHJXZ").toString()));
            map.put("khywc", Integer.parseInt(khMap.get("KHYWC").toString()));

            map.put("xcjcwks", Integer.parseInt(xcjcMap.get("JCWKS").toString()));
            map.put("xcjcjxz", Integer.parseInt(xcjcMap.get("JCJXZ").toString()));
            map.put("xcjcywc", Integer.parseInt(xcjcMap.get("JCYWC").toString()));

            map.put("htjcwks", Integer.parseInt(htMap.get("HTJCWKS").toString()));
            map.put("htjcjxz", Integer.parseInt(htMap.get("HTJCYKS").toString()));
            map.put("htjcywc", Integer.parseInt(htMap.get("HTJCYWC").toString()));
            return WebApiResponse.success(map);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    @GetMapping("deptDaZhu")
    public WebApiResponse deptDaZhu(Date day, String currentUserId, String tdorgId) {
        try {
            Map map = new HashMap();
            if (day == null) {
                day = new Date();
            }
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(hashOperations.get("UserInformation", currentUserId)));
            Integer type = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
            String deptid = jsonObject.get("DEPTID").toString();
            String xsCondition = "group by td_org";
            String khCondition = "group by u.deptid ";
            String xsField = "td_org";
            String khField = "u.deptid";
            String xscc = "";//巡视查询
            String khcc = "";
            String htcc = "";
            String deptcx = "";
            if (!StringUtils.isEmpty(tdorgId)) {
//                deptid = tdorgId;
                xscc = " and (td_org='" + tdorgId + "' or WX_ORG ='" + tdorgId + "' or class_id ='" + tdorgId + "' or group_id ='" + tdorgId + "')";
                khcc = " and (u.deptid='" + tdorgId + "' or u.companyid ='" + tdorgId + "' or u.classname ='" + tdorgId + "' or u.groupid ='" + tdorgId + "')";
                htcc = " and dept_id='" + tdorgId + "'";
                deptcx = " and id='" + tdorgId + "'";
            }
            if (type != 0) {
                xsField = "class_id";
                khField = "u.classname";
                xsCondition = "and td_org = '" + deptid + "' group by class_id";
                khCondition = "and u.deptid = '" + deptid + "' group by u.classname";
            }

            //正常
            String xszc = " SELECT " + xsField + " td_org,nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_ZC_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + xscc + xsCondition;
            List<Map<String, Object>> xszcMap = this.service.execSql(xszc, day);
            //保电
            String txbd = " SELECT " + xsField + " td_org,nvl(sum(decode(stauts, 0, 1, 0)),0) XSWKS,nvl(sum(decode(stauts, 1, 1, 0)),0) XSJXZ,nvl(sum(decode(stauts, 2, 1, 0)),0) XSYWC FROM XS_txbd_TASK WHERE PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + xscc + xsCondition;
            List<Map<String, Object>> txbdMap = this.service.execSql(txbd, day);
            //看护
            String kh = "SELECT " + khField + " td_org,nvl(sum(decode(status, 0, 1, 0)),0) KHWKS,nvl(sum(decode(status, 1, 1, 0)),0) KHJXZ,nvl(sum(decode(status, 2, 1, 0)),0) KHYWC FROM KH_TASK k JOIN RZTSYSUSER u ON k.USER_ID = u.ID and PLAN_END_TIME >= trunc(?1) and  PLAN_START_TIME <= trunc(?1+1) " + khcc + khCondition;
            List<Map<String, Object>> khMap = this.service.execSql(kh, day);
            //现场稽查
            String xcjc = "SELECT " + khField + " td_org,nvl(sum(decode(status, 0, 1, 0)),0) JCWKS,nvl(sum(decode(status, 1, 1, 0)),0) JCJXZ,nvl(sum(decode(status, 2, 1, 0)),0) JCYWC FROM CHECK_LIVE_TASK K  JOIN RZTSYSUSER u ON k.USER_ID = u.ID and to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') <plan_end_time " + khcc + khCondition;
            List<Map<String, Object>> xcjcMap = this.service.execSql(xcjc);
            List<Map<String, Object>> deptnameList;
            String htJcWks = "";
            String htJcYks = "";
            String htJcYwc = "";
            if (type == 0) {
                /**
                 *后台稽查未完成
                 */
                htJcWks = "SELECT count(*)  sum,DEPT_ID FROM TIMED_TASK_RECORD R LEFT JOIN RZTSYSDEPARTMENT D ON D.ID=R.DEPT_ID WHERE R.CREATE_TIME >= trunc(sysdate) and (R.TASKS>R.COMPLETE) " + htcc + " group by DEPT_ID";
                /**
                 *后台稽查进行中
                 */
                htJcYks = "SELECT count(DISTINCT (DEPT_ID)) FROM TIMED_TASK_RECORD";
                /**
                 *后台稽查已完成
                 */
                htJcYwc = "SELECT count(*) sum,DEPT_ID FROM TIMED_TASK_RECORD WHERE CREATE_TIME >= trunc(sysdate) and (TASKS=COMPLETE) " + htcc + " GROUP BY DEPT_ID";
                String deptname = " SELECT t.ID,t.DEPTNAME FROM RZTSYSDEPARTMENT t WHERE t.DEPTSORT IS NOT NULL " + deptcx + " ORDER BY t.DEPTSORT ";
                deptnameList = this.service.execSql(deptname);
            } else {
                htJcWks = "SELECT count(u.CLASSNAME) AS sum, u.CLASSNAME DEPT_ID FROM TIMED_TASK_RECORD r LEFT JOIN RZTSYSUSER u ON instr(r.EX_USER, u.id) > 0 WHERE r.CREATE_TIME >= trunc(sysdate) AND DEPT_ID = '" + deptid + "' and (R.TASKS>R.COMPLETE) GROUP BY u.CLASSNAME";
                htJcYwc = "SELECT count(u.CLASSNAME) AS sum, u.CLASSNAME DEPT_ID FROM TIMED_TASK_RECORD r LEFT JOIN RZTSYSUSER u ON instr(r.EX_USER, u.id) > 0 WHERE r.CREATE_TIME >= trunc(sysdate) AND DEPT_ID = '" + deptid + "' and (R.TASKS=R.COMPLETE) GROUP BY u.CLASSNAME";
                String className = " SELECT ID,DEPTNAME FROM (SELECT ID,DEPTNAME,LASTNODE FROM RZTSYSDEPARTMENT START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0 ";
                deptnameList = this.service.execSql(className, deptid);
            }
            List<Map<String, Object>> htMap = this.service.execSql(htJcWks);
            List<Map<String, Object>> htMap2 = this.service.execSql(htJcYwc);
            Map<String, Object> map1 = new HashMap();
            Map<String, Object> map2 = new HashMap();
            Map<String, Object> map3 = new HashMap();
            Map<String, Object> map4 = new HashMap();
            Map<String, Object> map5 = new HashMap();
            Map<String, Object> map6 = new HashMap();
            for (Map<String, Object> xs : xszcMap) {
                if (xs.get("TD_ORG") != null) {
                    map1.put(xs.get("TD_ORG").toString(), xs);
                }
            }
            for (Map<String, Object> tx : txbdMap) {
                if (tx.get("TD_ORG") != null) {
                    map2.put(tx.get("TD_ORG").toString(), tx);
                }
            }
            for (Map<String, Object> kha : khMap) {
                if (kha.get("TD_ORG") != null)
                    map3.put(kha.get("TD_ORG").toString(), kha);
            }
            for (Map<String, Object> jc : xcjcMap) {
                if (jc.get("TD_ORG") != null)
                    map4.put(jc.get("TD_ORG").toString(), jc);
            }
            for (Map<String, Object> ht : htMap) {
                if (ht.get("DEPT_ID") != null)
                    map5.put(ht.get("DEPT_ID").toString(), ht);
            }
            for (Map<String, Object> ht : htMap2) {
                if (ht.get("DEPT_ID") != null)
                    map6.put(ht.get("DEPT_ID").toString(), ht);
            }
            String deptName1 = "后台";
            String deptName2 = "白班";
            String deptName3 = "夜班";
            for (Map<String, Object> dept : deptnameList) {
                String deptId = dept.get("ID").toString();
                HashMap xsTask = (HashMap) map1.get(deptId);
                HashMap txTask = (HashMap) map2.get(deptId);
                HashMap khTask = (HashMap) map3.get(deptId);
                HashMap xcjcTask = (HashMap) map4.get(deptId);
                HashMap htTask1 = (HashMap) map5.get(deptId);
                HashMap htTask2 = (HashMap) map6.get(deptId);
                String deptname = dept.get("DEPTNAME").toString();
                StringBuffer sb = new StringBuffer();
                if (deptname.length()>6){
                    deptname = deptname.substring(0,5);
                }
                for (int i = 0; i < deptname.length(); i++) {
                    String substring = deptname.substring(i, i + 1);
                    sb.append(substring + "\n");
                }
                deptname = dept.get("DEPTNAME").toString();
                dept.put("DEPTNAME", sb.toString());
                dept.put("wks", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSWKS").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSWKS").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHWKS").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCWKS").toString()) + Integer.parseInt(htTask1 == null ? "0" : htTask1.get("SUM").toString()));
                dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCJXZ").toString()));
                dept.put("ywc", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSYWC").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSYWC").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHYWC").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCYWC").toString()) + Integer.parseInt(htTask2 == null ? "0" : htTask2.get("SUM").toString()));
                if (type != 0) {
                    if (!StringUtils.isEmpty(tdorgId)) {
                        String sql = "select * from rztsysdepartment where id ='" + tdorgId + "'";
                        Map<String, Object> map7 = this.service.execSqlSingleResult(sql);
                        deptName1 = map7.get("DEPTNAME").toString();
                        deptName2 = deptName3 = deptName1;
                    }
                    if (deptname.startsWith("后台稽查") && deptName1.contains("后台")) {
                        String user = "SELECT * FROM WORKING_TIMED WHERE DEPT_ID='" + deptid + "'";
                        Map<String, Object> map7 = new HashMap<>();
                        try {
                            map7 = this.service.execSqlSingleResult(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String format = formatter.format(new Date());
                        String s = format + " 00:00:00";
                        String start = map7.get("START_TIME").toString();
                        String end = map7.get("END_TIME").toString();
                        Date nowDate = DateUtil.getNowDate();
                        dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCJXZ").toString()));
                        if (nowDate.getTime() >= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(start)).getTime() && nowDate.getTime() <= DateUtil.addDate(DateUtil.parseDate(s), Double.parseDouble(end)).getTime()) {
                            if (deptname.contains("白") && deptName2.contains("白")) {
                                dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCJXZ").toString()) + 1);
                            }else {
                                dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCJXZ").toString()));
                            }
                        } else {
                            if (deptname.contains("夜") && deptName3.contains("夜")) {
                                dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCJXZ").toString()) + 1);
                            }else {
                                dept.put("jxz", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSJXZ").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSJXZ").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHJXZ").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCJXZ").toString()));
                            }
                        }
                        dept.put("wks", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSWKS").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSWKS").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHWKS").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCWKS").toString()) + Integer.parseInt(htTask1 == null ? "0" : Integer.parseInt(htTask1.get("SUM").toString()) / 3 + ""));
                        dept.put("ywc", Integer.parseInt(xsTask == null ? "0" : xsTask.get("XSYWC").toString()) + Integer.parseInt(txTask == null ? "0" : txTask.get("XSYWC").toString()) + Integer.parseInt(khTask == null ? "0" : khTask.get("KHYWC").toString()) + Integer.parseInt(xcjcTask == null ? "0" : xcjcTask.get("JCYWC").toString()) + Integer.parseInt(htTask2 == null ? "0" : Integer.parseInt(htTask2.get("SUM").toString()) / 3 + ""));
                    }

                }

            }
            return WebApiResponse.success(deptnameList);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }


    @InitBinder
    public void initBinder(ServletRequestDataBinder binder) {
        /*** 自动转换日期类型的字段格式
         */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));

    }

    public static Date timeUtil(int i) {
        String date = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        Date m = c.getTime();
        String mon = df.format(m);
        if (i == 1) {
            date = mon + " 00:00:00";
        } else {
            date = mon + " 23:59:59";
        }
        //  task.setPlanEndTime(df.format(new Date()) + " 23:59");
        return DateUtil.parseDate(date);
    }

}

