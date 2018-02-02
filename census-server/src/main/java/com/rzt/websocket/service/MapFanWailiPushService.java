package com.rzt.websocket.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.websocket;
import com.rzt.repository.websocketRepository;
import com.rzt.service.CurdService;
import com.rzt.util.DateUtil;
import com.rzt.util.WebApiResponse;
import com.rzt.websocket.serverendpoint.MapFanWailiEndpoint;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;

import javax.websocket.Session;
import java.util.*;

@Service
public class MapFanWailiPushService extends CurdService<websocket, websocketRepository> {
    @Autowired
    MapFanWailiEndpoint mapFanWailiEndpoint;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PcMapShowService pcMapShowService;

//    @Scheduled(fixedRate = 30000)
    public void module1() {
        Map<String, Map> allMap = new HashMap<String, Map>();
        Map<String, HashMap> map = mapFanWailiEndpoint.sendMsg();
        map.forEach((sessionId, session) -> {
            try {
                mapshow(allMap, session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void mapshow(Map<String, Map> allMap, HashMap session) throws Exception {
        Object menInMap = giveMeUserList(session);
        Map<String, HashMap> map = mapFanWailiEndpoint.sendMsg();
        mapFanWailiEndpoint.sendText((Session) session.get("session"), menInMap);
    }

    public Object giveMeUserList(HashMap session) throws Exception {
        Date startDate = new Date();
        String needDateString = DateUtil.dateFormatToDay(startDate);
        long timeSecond = startDate.getTime();
        //准备要返回的list
        List<Map> menInMap = new ArrayList<>();
        HashOperations<String, String, Map> hashOperations = redisTemplate.opsForHash();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        //0 根据人查 有人就直接结束
        String currentUserId = session.get("ID").toString();
        String dept = pcMapShowService.dataAccessByUserId(currentUserId).toString();
        String deptId = "";
        //1.初始数据权限
        //1.1 根据部门筛选
        String sql = "select DISTINCT t.USER_ID id from MONITOR_CHECK_EJ t where t.STATUS = 0 and CREATE_TIME > trunc(sysdate) and t.user_id is not null";
        List<Map<String, Object>> userList = this.execSql(sql);
        Set<String> keys = new HashSet();
        //单位 外协 组织 班组 都走这里
        for (Map<String, Object> user : userList) {
            String id = user.get("ID").toString();
            keys.add(id);
        }
        menInMap = hashOperations.multiGet("menInMap", keys);
        //-->去除list中为null的元素
        menInMap.removeAll(Collections.singleton(null));
        //2.根据工作类型来分
        chouYiXia(null, 2, needDateString, timeSecond, menInMap, valueOperations);
        return WebApiResponse.success(menInMap);
    }

    private void chouYiXia(String workTypes, Integer loginStatus, String needDateString, long timeSecond, List<Map> menInMap, ValueOperations<String, Object> valueOperations) {
        JSONObject allMen = new JSONObject();
        if (workTypes == null || workTypes.contains("1")) {
            JSONObject khMenAll = JSONObject.parseObject(valueOperations.get("khMenAll:" + needDateString).toString());
            allMen.putAll(khMenAll);
        }
        if (workTypes == null || workTypes.contains("2")) {
            JSONObject xsMenAll = JSONObject.parseObject(valueOperations.get("xsMenAll:" + needDateString).toString());
            allMen.putAll(xsMenAll);
        }
        if (workTypes == null || workTypes.contains("3")) {

        }
        Iterator<Map> iterator = menInMap.iterator();
        while (iterator.hasNext()) {
            Map men = iterator.next();
            String userid = men.get("userid").toString();

            Long createtime = Long.parseLong(men.get("createtime").toString());
            //默认90分钟
            int offLineJudgeNum = 5400000;
            //如果redis有配置 则用redis的数据
            Object offLineJudge = valueOperations.get("offLineJudge");
            if(offLineJudge != null) {
                offLineJudgeNum = Integer.parseInt(offLineJudge.toString()) * 60 * 1000;
            }
            if (timeSecond - createtime > offLineJudgeNum) {
                //大于九十分钟 离线
                //显示在线
                if (loginStatus == 1) {
                    iterator.remove();
                    continue;
                }
                men.put("loginStatus", 0);
            } else {
                //小于十分钟 在线
                //显示离线
                if (loginStatus == 0) {
                    iterator.remove();
                    continue;
                }
                men.put("loginStatus", 1);
            }

            if (!allMen.containsKey(userid)) {
                //注意这个地方
                iterator.remove();
            } else {
                men.put("statuts", allMen.get(userid));
            }
        }
    }
}
