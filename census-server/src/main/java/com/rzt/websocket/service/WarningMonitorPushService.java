package com.rzt.websocket.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.websocket.serverendpoint.warningMonitorServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WarningMonitorPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    warningMonitorServerEndpoint warningEndpoint;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //判断权限，获取当前登录用户的deptId，如果是全部查询则返回0
    public String getDeptID(String userId) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", userId);
        if (userInformation == null) {
            return null;
        }
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        String roletype = (String) jsonObject.get("ROLETYPE");
        if (roletype == null) {
            return null;
        }
        if ("0".equals(roletype)) {
            //0是查询全部
            return "0";
        } else if ("1".equals(roletype) || "2".equals(roletype)) {
            return (String) jsonObject.get("DEPTID");
        } else {
            return "-1";
        }
    }

    public void sendMsgs(Long id) {
        Map<String, HashMap> sendMsg = warningEndpoint.sendMsg();

        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        sendMsg.forEach((String sessionId, HashMap session) -> {
            String userId = (String) session.get("userId");
            String deptID = getDeptID(userId);
            if (deptID == null) {
                return ;
            }
            if ("-1".equals(deptID)) {
                return ;
            }
            try {
                String sql="";
                List<Object> list = new ArrayList<>();
                if("0".equals(deptID)){
                    sql = "SELECT * FROM WARNING_ONE_KEY WHERE ID=?1  ";
                    list.add(id);
                }else{
                    sql="SELECT key.* FROM WARNING_ONE_KEY key LEFT JOIN RZTSYSUSER u ON key.USER_ID=u.ID " +
                            "WHERE u.DEPTID=?1 AND key.ID=?2";
                    list.add(deptID);
                    list.add(id);
                }
                List<Map<String, Object>> maps = this.execSql(sql, list.toArray());
                if(maps.size()>0){
                    Map<String, Object> map = maps.get(0);
                    Object userInformation = hash.get("UserInformation", map.get("USER_ID"));
                    JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
                    if(jsonObject!=null){
                        map.put("DEPT", jsonObject.get("DEPT"));
                        map.put("COMPANYNAME", jsonObject.get("COMPANYNAME"));
                        map.put("REALNAME", jsonObject.get("REALNAME"));
                        map.put("PHONE", jsonObject.get("PHONE"));
                    }

                }

                warningEndpoint.sendText((Session) session.get("session"), maps);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

}
