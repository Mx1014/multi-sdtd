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
    public WebApiResponse OfflinesList(Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String taskType) {
        org.springframework.data.domain.Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
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
        /*String sql = " SELECT\n" +
                "  e.USER_ID,\n" +
                "  u.REALNAME,\n" +
                "  u.CLASSNAME,\n" +
                "  u.DEPT,\n" +
                "  u.COMPANYNAME,\n" +
                "  CASE u.WORKTYPE\n" +
                "  WHEN 1\n" +
                "    THEN '看护'\n" +
                "  WHEN 2\n" +
                "    THEN '巡视'\n" +
                "  WHEN 3\n" +
                "    THEN '现场稽查' END AS WORKTYPE,\n" +
                "  e.a          AS MORE,\n" +
                "  u.DEPTID,\n" +
                "  e.CREATE_TIME,\n" +
                "  e.ONLINE_TIME\n" +
                "FROM (SELECT\n" +
                "count(1) as a,\n" +
                "USER_ID, MAX (CREATE_TIME) AS CREATE_TIME,\n" +
                "nvl(to_char( MAX (ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') as ONLINE_TIME\n" +
                "FROM MONITOR_CHECK_EJ\n" +
                "WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2) " + s + " GROUP BY USER_ID) e LEFT JOIN USERINFO u\n" +
                "ON e.USER_ID = u.ID ";*/
        String sql = "SELECT DISTINCT ce.USER_ID,ce.REASON,ch.* FROM(\n" +
                " SELECT\n" +
                "   e.USER_ID,\n" +
                "    u.REALNAME,\n" +
                "    u.CLASSNAME,\n" +
                "    u.DEPT,\n" +
                "    u.COMPANYNAME,\n" +
                "    CASE u.WORKTYPE\n" +
                "    WHEN 1\n" +
                "      THEN '看护'\n" +
                "    WHEN 2\n" +
                "      THEN '巡视'\n" +
                "    WHEN 3\n" +
                "      THEN '现场稽查' END AS WORKTYPE,\n" +
                "    e.a          AS MORE,\n" +
                "    u.DEPTID,\n" +
                "    e.CREATE_TIME,\n" +
                "    e.ONLINE_TIME\n" +
                "     FROM (SELECT\n" +
                "     count(1) as a,\n" +
                "     USER_ID, MAX (CREATE_TIME) AS CREATE_TIME,\n" +
                "     nvl(to_char( MAX (ONLINE_TIME), 'yyyy-MM-dd hh24:mi:ss'), '人员未上线') as ONLINE_TIME\n" +
                "     FROM MONITOR_CHECK_EJ\n" +
                "     WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2)  " + s + "  GROUP BY USER_ID) e LEFT JOIN USERINFO u\n" +
                "     ON e.USER_ID = u.ID) ch LEFT JOIN MONITOR_CHECK_EJ ce\n" +
                "ON ch.USER_ID=ce.USER_ID AND ch.CREATE_TIME=ce.CREATE_TIME";
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
