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
@RequestMapping("TEMPORARILY")
public class TemporarilyController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("temporarilyList")
    public WebApiResponse answertimeList(String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId) {
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        String s1 = "";
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s1 += " AND u.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s1 += " AND u.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            //s1 += " AND trunc(u.CREATE_TIME)=trunc(sysdate) ";
            s1 += " AND trunc(t.START_TIME)=trunc(sysdate) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND DEPTID= ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s += " AND u.COMPANYID = ?" + listLike.size();
        }
       /* String sql = " SELECT t.START_TIME,t.END_TIME,t.STATUS,k.TASK_NAME,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.DEPT,u.PHONE FROM " +
                "( SELECT u.TASK_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u " +
                " LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1 " + s1 +
                " )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID " + s;*/
        String sql = "SELECT t.*,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.DEPT,u.PHONE,'1' AS TASK_TYPE,k.TASK_NAME  FROM " +
                " ( SELECT u.TASK_ID,u.USER_ID, u.STATUS, t.START_TIME, t.END_TIME FROM WARNING_OFF_POST_USER u " +
                " LEFT JOIN WARNING_OFF_POST_USER_TIME t ON u.USER_ID=t.FK_USER_ID AND u.TASK_ID=t.FK_TASK_ID WHERE 1=1  AND t.TIME_STATUS=1 " + s1 +
                " )t LEFT JOIN KH_TASK k ON t.TASK_ID = k.ID JOIN USERINFO u ON k.USER_ID = u.ID" + s;
        return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
    }
}
