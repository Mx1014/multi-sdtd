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
    public WebApiResponse OfflinesList(Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
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
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) " ;
        }
        String sql = " SELECT " +
                "  e.USER_ID, " +
                "  u.REALNAME, " +
                "  u.CLASSNAME, " +
                "  u.DEPT, " +
                "  u.COMPANYNAME, " +
                "  CASE u.WORKTYPE " +
                "  WHEN 1 " +
                "    THEN '看护' " +
                "  WHEN 2 " +
                "    THEN '巡视' " +
                "  WHEN 3 " +
                "    THEN '现场稽查' END AS WORKTYPE, " +
                "  count(1)          AS MORE,u.DEPTID,e.CREATE_TIME " +
                "FROM (SELECT USER_ID,CREATE_TIME " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE (WARNING_TYPE = 8 OR WARNING_TYPE = 2) " + s + " ) e LEFT JOIN USERINFO u " +
                "    ON e.USER_ID = u.ID " +
                "GROUP BY e.USER_ID, u.REALNAME, " +
                "  u.CLASSNAME, " +
                "  u.DEPT, " +
                "  u.COMPANYNAME, u.WORKTYPE,u.DEPTID,e.CREATE_TIME ";
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
