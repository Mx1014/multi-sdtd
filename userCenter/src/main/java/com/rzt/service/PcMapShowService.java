package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/***
* @Class  PcMapShowService
* @Description  pc端地图模块的service
* @date 2018/1/6 22:38
* @author nwz
*/
@Service
public class PcMapShowService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CmcoordinateService cmcoordinateService;

    public Object dataAccessByUserId(String userId) throws Exception {
        Object deptId = "err";
        if(userId != null) {
            //从reids中拿userInfo
            Map<String,Object> jsonObject = userInfoFromRedis(userId);
            try {
                Integer roletype = Integer.parseInt(jsonObject.get("ROLETYPE").toString());
                Object tdId = jsonObject.get("DEPTID");
                Object classid = jsonObject.get("CLASSID");
                Object companyid = jsonObject.get("COMPANYID");
                if(roletype == null) {
                    return "err";
                }
                switch (roletype) {
                    case 0:
                        deptId = "all";
                        break;
                    case 1:
                        deptId = "all";
                        break;
                    case 2:
                        deptId = tdId;
                        break;
                    case 3:
                        deptId = companyid;
                        break;
                    case 4:
                        deptId = classid;
                        break;
                    case 5:
                        deptId = userId;
                        break;
                }
                return deptId;
            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            }

        }
        //拼权限的sql
        return deptId;

    }


    /**
     *
     * @param userId
     * @return 从redis中拿人员信息
     * @throws Exception
     */
    public Map<String, Object> userInfoFromRedis(String userId) throws Exception {
        HashOperations hashOperations = redisTemplate.opsForHash();

        Map<String,Object> userInfo = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if(userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            userInfo = cmcoordinateService.execSqlSingleResult(sql, userId);
            hashOperations.put("UserInformation",userId,userInfo);
        } else {
            userInfo = JSON.parseObject(userInformation.toString(),Map.class);
        }
        return userInfo;
    }

    /**
     *
     * @param deptId
     * @return 从redis中拿部门的人
     * @throws Exception
     */
    public List<Map<String,Object>> deptMenFromRedis(String deptId) throws Exception {
        HashOperations hashOperations = redisTemplate.opsForHash();

        List userList = null;
        Object userInformation = hashOperations.get("menInDept", deptId);
        if(userInformation == null) {
            if(deptId.equals("all")) {
                String userListSql = "select id,worktype from RZTSYSUSER where USERDELETE = 0";
                userList = cmcoordinateService.execSql(userListSql);
            } else {
                String userListSql = "select id,worktype from RZTSYSUSER where USERDELETE = 0 and (DEPTID = ?1 or COMPANYID = ?1 or GROUPID = ?1 or CLASSNAME = ?1 ) ";
                userList = cmcoordinateService.execSql(userListSql, deptId);
            }
            hashOperations.put("menInDept",deptId,userList);
        } else {
            userList = JSONObject.parseArray(userInformation.toString());
        }
        return userList;
    }
}
