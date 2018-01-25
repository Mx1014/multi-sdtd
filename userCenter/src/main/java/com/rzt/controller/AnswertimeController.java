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
@RequestMapping("ANSWERTIME")
public class AnswertimeController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("answertimeList")
    public WebApiResponse answertimeList(Integer page, Integer size, String userId, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", userId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s1 += " AND CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s1 += " AND CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s1 += " AND trunc(CREATE_TIME)=trunc(sysdate) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND u.DEPTID = ?" + listLike;
        } else if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND u.DEPTID = ?" + listLike;
        }

        String sql = "  SELECT * FROM (SELECT x.TASK_NAME,x.PLAN_START_TIME," +
                "  x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STAUTS " +
                "FROM (SELECT TASK_ID,USER_ID " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 4 " + s1 + " ) e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1 " + s +
                " UNION ALL " +
                " SELECT x.TASK_NAME,x.PLAN_START_TIME,x.REAL_START_TIME,u.REALNAME,u.COMPANYNAME,u.DEPT,u.CLASSNAME,u.PHONE, X.STATUS as STAUTS " +
                " FROM (SELECT TASK_ID,USER_ID " +
                "      FROM MONITOR_CHECK_EJ " +
                "      WHERE WARNING_TYPE = 10 " + s1 + " ) e LEFT JOIN KH_TASK x ON e.TASK_ID = x.ID " +
                "  LEFT JOIN USERINFO u ON e.USER_ID = u.ID WHERE 1=1 " + s + " ) ORDER BY  PLAN_START_TIME DESC  ";
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }

    }
}
