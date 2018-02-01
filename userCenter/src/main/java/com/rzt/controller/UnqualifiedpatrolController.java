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
@RequestMapping("UNQUALIFIEDPATROL")
public class UnqualifiedpatrolController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("unqualifiedpatrolList")
    public WebApiResponse unqualifiedpatrolList(Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String userName) {
        JSONObject jsonObject = JSONObject.parseObject(redisTemplate.opsForHash().get("UserInformation", currentUserId).toString());
        int roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
        Object deptid = jsonObject.get("DEPTID");
        Pageable pageable = new PageRequest(page, size);
        List listLike = new ArrayList();
        String s = "";
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            listLike.add(startTime);
            s += " AND e.CREATE_TIME >= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
            listLike.add(endTime);
            s += " AND e.CREATE_TIME <= to_date(?" + listLike.size() + ",'yyyy-mm-dd hh24:mi:ss') ";
        } else {
            s += " AND trunc(e.CREATE_TIME) = trunc(sysdate) ";
        }
        if (roletype == 1 || roletype == 2) {
            listLike.add(deptid);
            s += " AND e.DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(deptId)) {
            listLike.add(deptId);
            s += " AND e.DEPTID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(userName)) {
            listLike.add(userName + "%");
            s += " AND u.REALNAME LIKE ?" + listLike.size();
        }
        //  修改增加未到位类别   增加未到位原因字段      ---> 李成阳
        String sql = " SELECT *" +
                "         FROM (SELECT e.CREATE_TIME,x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON" +
                "      FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                "      WHERE WARNING_TYPE = 5 "+s+" )" +
                "        UNION ALL" +
                "    SELECT * FROM (SELECT e.CREATE_TIME,x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '未到位' as  type,e.REASON" +
                "                   FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                "                   WHERE WARNING_TYPE = 3  "+s+"  )";
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }
}
