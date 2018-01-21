package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("ALARMDETAILS")
public class AlarmDetailsController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("AlarmList")
    public WebApiResponse AlarmList(String userId) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", userId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        if (roletype == 0) {
            String alarm = "SELECT A.*,T.DEPTNAME FROM RZTSYSDEPARTMENT T LEFT JOIN (SELECT " +
                    "  sum(decode(WARNING_TYPE, 2, 1, 8, 1, 0))  AS OFFLINES, " +
                    "  sum(decode(WARNING_TYPE, 4, 1, 10, 1, 0)) AS ANSWERTIME, " +
                    "  sum(decode(WARNING_TYPE, 1, 1, 0))        AS OVERDUE, " +
                    "  sum(decode(WARNING_TYPE, 7, 1, 0))        AS TEMPORARILY, " +
                    "  sum(decode(WARNING_TYPE, 5, 1, 0))        AS UNQUALIFIEDPATROL, " +
                    "  DEPTID " +
                    "FROM MONITOR_CHECK_EJ " +
                    "WHERE trunc(CREATE_TIME) = trunc(sysdate) " +
                    "GROUP BY DEPTID)A ON T.ID = A.DEPTID WHERE  T.DEPTSORT IS NOT NULL ORDER BY T.DEPTSORT ";
            List<Map<String, Object>> alarms = this.service.execSql(alarm);
            return WebApiResponse.success(alarms);
        } else if (roletype == 1 || roletype == 2) {
            Map<String, Integer> map1 = new HashMap();
            Map<String, Integer> map2 = new HashMap();
            Map<String, Integer> map3 = new HashMap();
            Map<String, Integer> map4 = new HashMap();
            Map<String, Integer> map5 = new HashMap();
            Map<String, Integer> map6 = new HashMap();
            Object deptid = jsonObject.get("DEPTID");
            String offlines = " SELECT count(1) AS OFFLINES," +
                    "  u.CLASSNAME as CLASS_ID FROM (SELECT USER_ID FROM MONITOR_CHECK_EJ  WHERE DEPTID = ?1 AND (WARNING_TYPE = 8 OR WARNING_TYPE = 2) AND" +
                    "  trunc(CREATE_TIME) = trunc(sysdate)) e LEFT JOIN RZTSYSUSER u ON e.USER_ID = u.ID GROUP BY u.CLASSNAME ";
            String xsAnswertime = " SELECT count(1) AS XSANSWERTIME,k.CLASS_ID as CLASS_ID  FROM (SELECT TASK_ID FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 4 OR WARNING_TYPE = 10) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = ?1 AND TASK_TYPE = 1) e LEFT JOIN XS_ZC_TASK k ON k.ID = e.TASK_ID GROUP BY k.CLASS_ID ";
            String khAnswertime = " SELECT count(1) AS KHANSWERTIME,r.CLASSNAME as CLASS_ID FROM (SELECT TASK_ID,USER_ID FROM MONITOR_CHECK_EJ WHERE (WARNING_TYPE = 4 OR WARNING_TYPE = 10) AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = ?1 AND TASK_TYPE = 2) e LEFT JOIN KH_TASK k ON k.ID = e.TASK_ID LEFT JOIN RZTSYSUSER r ON e.USER_ID = r.ID GROUP BY r.CLASSNAME ";
            String xsOverdue = " SELECT count(1) as OVERDUE ,k.CLASS_ID as CLASS_ID FROM (SELECT * FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 1 AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = ?1) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID GROUP BY k.CLASS_ID ";
            String khTemporarily = " SELECT count(1) as TEMPORARILY,u.CLASSNAME as CLASS_ID FROM (SELECT DEPTID,TASK_ID FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 7 AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = ?1) e LEFT JOIN KH_TASK k ON k.ID = e.TASK_ID LEFT JOIN RZTSYSUSER u ON k.USER_ID = u.ID GROUP BY CLASSNAME ";
            String xsUnqualifiedpatrol = " SELECT count(1) as unqualifiedpatrol,x.CLASS_ID as CLASS_ID FROM (SELECT TASK_ID FROM MONITOR_CHECK_EJ WHERE WARNING_TYPE = 5 AND trunc(CREATE_TIME) = trunc(sysdate) AND DEPTID = ?1 ) e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID GROUP BY x.CLASS_ID ";
            String className = " SELECT ID,DEPTNAME FROM (SELECT ID,DEPTNAME,LASTNODE FROM RZTSYSDEPARTMENT START WITH ID = ?1 CONNECT BY PRIOR ID = DEPTPID) WHERE LASTNODE = 0 ";
            List<Map<String, Object>> list = this.service.execSql(offlines, deptid);
            List<Map<String, Object>> list1 = this.service.execSql(xsAnswertime, deptid);
            List<Map<String, Object>> list2 = this.service.execSql(khAnswertime, deptid);
            List<Map<String, Object>> list3 = this.service.execSql(xsOverdue, deptid);
            List<Map<String, Object>> list4 = this.service.execSql(khTemporarily, deptid);
            List<Map<String, Object>> list5 = this.service.execSql(xsUnqualifiedpatrol, deptid);
            List<Map<String, Object>> list6 = this.service.execSql(className, deptid);
            for (int i = 0; i < list.size(); i++) {
                map1.put(list.get(i).get("CLASS_ID").toString(), Integer.parseInt(list.get(i).get("OFFLINES").toString()));
            }
            for (int i = 0; i < list1.size(); i++) {
                map2.put(list1.get(i).get("CLASS_ID").toString(), Integer.parseInt(list1.get(i).get("XSANSWERTIME").toString()));
            }
            for (int i = 0; i < list2.size(); i++) {
                map3.put(list2.get(i).get("CLASS_ID").toString(), Integer.parseInt(list2.get(i).get("KHANSWERTIME").toString()));
            }
            for (int i = 0; i < list3.size(); i++) {
                map4.put(list3.get(i).get("CLASS_ID").toString(), Integer.parseInt(list3.get(i).get("OVERDUE").toString()));
            }
            for (int i = 0; i < list4.size(); i++) {
                map5.put(list4.get(i).get("CLASS_ID").toString(), Integer.parseInt(list4.get(i).get("TEMPORARILY").toString()));
            }
            for (int i = 0; i < list5.size(); i++) {
                map6.put(list5.get(i).get("CLASS_ID").toString(), Integer.parseInt(list5.get(i).get("UNQUALIFIEDPATROL").toString()));
            }
            for (Map map : list6) {
                Object id = map.get("ID");

                Integer OFFLINESs = 0;
                Integer OFFLINES = map1.get(id);
                if (!StringUtils.isEmpty(OFFLINES)) {
                    OFFLINESs = OFFLINES;
                }
                Integer XSANSWERTIMEs = 0;
                Integer XSANSWERTIME = map1.get(id);
                if (!StringUtils.isEmpty(XSANSWERTIME)) {
                    XSANSWERTIMEs = XSANSWERTIME;
                }
                Integer KHANSWERTIMEs = 0;
                Integer KHANSWERTIME = map1.get(id);
                if (!StringUtils.isEmpty(KHANSWERTIME)) {
                    KHANSWERTIMEs = KHANSWERTIME;
                }
                Integer OVERDUEs = 0;
                Integer OVERDUE = map1.get(id);
                if (!StringUtils.isEmpty(OVERDUE)) {
                    OVERDUEs = OVERDUE;
                }
                Integer TEMPORARILYs = 0;
                Integer TEMPORARILY = map1.get(id);
                if (!StringUtils.isEmpty(TEMPORARILY)) {
                    TEMPORARILYs = TEMPORARILY;
                }
                Integer UNQUALIFIEDPATROLs = 0;
                Integer UNQUALIFIEDPATROL = map1.get(id);
                if (!StringUtils.isEmpty(UNQUALIFIEDPATROL)) {
                    UNQUALIFIEDPATROLs = OFFLINES;
                }
                map.put("OFFLINES", OFFLINESs);
                map.put("ANSWERTIME", XSANSWERTIMEs + KHANSWERTIMEs);
                map.put("OVERDUE", OVERDUEs);
                map.put("TEMPORARILY", TEMPORARILYs);
                map.put("UNQUALIFIEDPATROL", UNQUALIFIEDPATROLs);
            }
            return WebApiResponse.success(list6);
        }
        return WebApiResponse.success(null);
    }
}
