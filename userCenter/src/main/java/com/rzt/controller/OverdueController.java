package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("OVERDUE")
public class OverdueController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("overdueList")
    public WebApiResponse overdueList(Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID = ?" + listLike.size();
        } else if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(CREATE_TIME) = trunc(sysdate) ";
        }
        String sql = " SELECT " +
                "  k.TASK_NAME,u.DEPT,u.CLASSNAME,u.COMPANYNAME,u.REALNAME,u.PHONE,k.PLAN_END_TIME,e.* " +
                "FROM (SELECT TASK_ID,USER_ID,TASK_TYPE  " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 1 " + s +
                "     ) e LEFT JOIN XS_ZC_TASK k ON e.TASK_ID = k.ID LEFT JOIN USERINFO u ON k.CM_USER_ID = u.ID ";
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
