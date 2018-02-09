package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("OFFLINES")
public class OfflinesController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 离线人员告警
     *
     * @param page
     * @param size
     * @param startTime
     * @param endTime
     * @param deptId
     * @return
     */
    @RequestMapping("OfflinesList")
    public WebApiResponse OfflinesList(Integer workType, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType,String loginType) {
        org.springframework.data.domain.Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        String s2 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(workType)) {
            listLike.add(workType);
            s1 += " AND worktypes= ?" + listLike.size();
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginType)){
            listLike.add(loginType);
            s2 += " AND LOGINSTATUS=?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        if (!StringUtils.isEmpty(taskType)) {
            s += " and TASK_TYPE = " + taskType;
        }
//        String sql = "SELECT DISTINCT   " +
//                "  ce.USER_ID AS userID,   " +
//                "  ce.REASON, " +
//                "  ce.TASK_TYPE, " +
//                "  ce.TASK_ID, " +
//                "  ch.*   " +
//                "FROM (SELECT   " +
//                "        e.USER_ID,   " +
//                "        u.REALNAME,   " +
//                "        u.CLASSNAME,   " +
//                "        u.DEPT,   " +
//                "        u.COMPANYNAME,   " +
//                "        CASE u.WORKTYPE   " +
//                "        WHEN 1   " +
//                "          THEN '看护'   " +
//                "        WHEN 2   " +
//                "          THEN '巡视'   " +
//                "        WHEN 3   " +
//                "          THEN '现场稽查' END AS WORKTYPE,   " +
//                "        e.a               AS MORE,   " +
//                "        u.DEPTID,   " +
//                "        e.CREATE_TIME,   " +
//                "        e.ONLINE_TIME  " +
//                "      FROM (SELECT   " +
//                "              count(1)    AS a,   " +
//                "              ej.USER_ID,   " +
//                "              MAX(ej.CREATE_TIME)       AS CREATE_TIME,   " +
//                "              nvl(to_char(MAX(ej.ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') AS ONLINE_TIME   " +
//                "            FROM MONITOR_CHECK_EJ ej   " +
//                "            WHERE (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2) " + s +
//                "            GROUP BY USER_ID) e LEFT JOIN USERINFO u ON e.USER_ID = u.ID) ch LEFT JOIN MONITOR_CHECK_EJ ce   " +
//                "    ON ch.USER_ID = ce.USER_ID AND ch.CREATE_TIME = ce.CREATE_TIME";
        String sql = " SELECT DISTINCT " +
                "  ce.USER_ID AS userID, " +
                "  ce.REASON, " +
                "  ce.TASK_TYPE, " +
                "  ce.TASK_ID, " +
                " nvl(to_char(ce.ONLINE_TIME, 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') AS ONLINE_TIME , " +
                "  ch.*, " +
                "  CASE ch.WORKTYPEs " +
                "        WHEN 1 " +
                "          THEN '看护' " +
                "        WHEN 2 " +
                "          THEN '巡视' " +
                "        WHEN 3 " +
                "          THEN '现场稽查' END AS WORKTYPE " +
                "FROM (SELECT " +
                "        e.USER_ID, " +
                "        u.REALNAME, " +
                "        u.CLASSNAME, " +
                "        u.DEPT, " +
                "        u.LOGINSTATUS, " +
                "        u.COMPANYNAME, " +
                "         u.WORKTYPE AS WORKTYPEs, " +
                "        e.a               AS MORE, " +
                "        u.DEPTID, " +
                "        e.CREATE_TIME " +
                "      FROM (SELECT " +
                "              count(1)                                                            AS a, " +
                "              ej.USER_ID, " +
                "              MAX(ej.CREATE_TIME)                                                 AS CREATE_TIME " +

                "            FROM MONITOR_CHECK_EJ ej " +
                "            WHERE (ej.WARNING_TYPE = 8 OR ej.WARNING_TYPE = 2) AND USER_ID !='null'  " + s +
                "            GROUP BY USER_ID) e JOIN USERINFO u ON e.USER_ID = u.ID AND u.USERDELETE=1 "+s2+" ) ch LEFT JOIN MONITOR_CHECK_EJ ce " +
                "    ON ch.USER_ID = ce.USER_ID AND ch.CREATE_TIME = ce.CREATE_TIME  " + s1;
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
