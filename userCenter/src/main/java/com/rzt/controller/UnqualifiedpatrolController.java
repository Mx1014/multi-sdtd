package com.rzt.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CommonService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("UNQUALIFIEDPATROL")
public class UnqualifiedpatrolController extends CurdController<RztSysUser, CommonService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("unqualifiedpatrolList")
    public WebApiResponse unqualifiedpatrolList(String loginstatus, String taskname, String companyid, Integer page, Integer size, String currentUserId, String startTime, String endTime, String deptId, String userName) {
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
        if (!StringUtils.isEmpty(companyid)) {
            listLike.add(companyid);
            s += " AND u.COMPANYID = ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(taskname)) {
            listLike.add("%" + taskname + "%");
            s += " AND x.TASK_NAME LIKE ?" + listLike.size();
        }
        if (!StringUtils.isEmpty(loginstatus)) {
            listLike.add(loginstatus);
            s += " AND u.LOGINSTATUS  = ?" + listLike.size();
        }
        //  修改增加未到位类别   增加未到位原因字段      ---> 李成阳
        String sql = "SELECT e.CREATE_TIME,x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON,e.TASK_ID,e.USER_ID,e.TASK_TYPE,u.LOGINSTATUS " +
                "      FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                "      WHERE WARNING_TYPE = 5 " + s + "";
       /* String sql = " SELECT *" +
                "         FROM (SELECT e.CREATE_TIME,x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '巡视超速' as  type,e.REASON" +
                "      FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                "      WHERE WARNING_TYPE = 5 "+s+" )" +
                "        UNION ALL" +
                "    SELECT * FROM (SELECT e.CREATE_TIME,x.TASK_NAME,u.DEPT,u.COMPANYNAME,u.CLASSNAME,u.REALNAME,u.PHONE, '未到位' as  type,e.REASON" +
                "                   FROM MONITOR_CHECK_EJ e LEFT JOIN XS_ZC_TASK x ON e.TASK_ID=x.ID LEFT JOIN USERINFO u ON x.CM_USER_ID = u.ID" +
                "                   WHERE WARNING_TYPE = 3  "+s+"  )";*/
        try {
            return WebApiResponse.success(this.service.execSqlPage(pageable, sql, listLike.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("");
        }
    }

    @GetMapping("/csInfo")
    public WebApiResponse csInfo(Long taskId) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();
            String sql = "select YCDATA,CREATE_TIME from XS_ZC_EXCEPTION WHERE TASK_ID=?1";
            List<Map<String, Object>> maps = this.service.execSql(sql, taskId);
            maps.forEach(map -> {
                Object ycdata = map.get("YCDATA");
                JSONArray objects = JSONObject.parseArray(ycdata.toString());
                for (int i = 0; i < objects.size(); i++) {
                    Map<String, Object> m = (Map<String, Object>) objects.get(i);
                    result.add(m);
                }
            });
            return WebApiResponse.success(result);
        } catch (Exception e) {
            return WebApiResponse.erro("erro" + e.getMessage());
        }
    }

    @GetMapping("/csPicture")
    public WebApiResponse csPicture(Long taskId) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {

            String sql = "select YCDATA from XS_ZC_EXCEPTION WHERE TASK_ID=?1";
            List<Map<String, Object>> maps = this.service.execSql(sql, taskId);
            StringBuffer ids = new StringBuffer();
            for (int j = 0; j < maps.size(); j++) {
                Map<String, Object> map = maps.get(j);
                Object ycdata = map.get("YCDATA");
                JSONArray objects = JSONObject.parseArray(ycdata.toString());
                for (int i = 0; i < objects.size(); i++) {
                    Map<String, Object> m = (Map<String, Object>) objects.get(i);
                    String id = (String) m.get("ID");
                    if (i != objects.size() - 1) {
                        ids.append(id + ", ");
                    } else {
                        ids.append(id);
                    }
                }
                if (j != maps.size() - 1) {
                    ids.append(",");
                }
            }
            String sql1 = "select FILE_PATH,PROCESS_NAME AS OPERATE_NAME ,CREATE_TIME from PICTURE_TOUR where PROCESS_ID in (" + ids.toString() + ")";
            List<Map<String, Object>> maps1 = this.service.execSql(sql1);
            result.addAll(maps1);
            return WebApiResponse.success(result);
        } catch (Exception e) {
            return WebApiResponse.erro("erro" + e.getMessage());
        }
    }
}
