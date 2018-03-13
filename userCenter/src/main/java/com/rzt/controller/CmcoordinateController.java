/**
 * 文件名：CMCOORDINATEController
 * 版本信息：
 * 日期：2017/12/20 15:22:15
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.Cmcoordinate;
import com.rzt.entity.MyCoordinate;
import com.rzt.entity.RztSysUser;
import com.rzt.service.CmcoordinateService;
import com.rzt.service.RztSysUserService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.Constances;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 类名称：CMCOORDINATEController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/20 15:22:15
 * 修改人：张虎成
 * 修改时间：2017/12/20 15:22:15
 * 修改备注：
 */
@RestController
@RequestMapping("Cmcoordinate")
public class CmcoordinateController extends
        CurdController<Cmcoordinate, CmcoordinateService> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RztSysUserService rztSysUserService;

    @PostMapping("addCmcoordinate")
    @Transactional
    public WebApiResponse addCmcoordinate(MyCoordinate myCoordinate) {
        try {
            String currentDate = DateUtil.getCurrentDate();
            //1.处理数据
            Date date = DateUtil.dateNow();
            Cmcoordinate cmcoordinate = new Cmcoordinate();
            cmcoordinate.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            cmcoordinate.setCreatetime(date);
            cmcoordinate.setUserid(myCoordinate.getID());
            cmcoordinate.setGzlx(myCoordinate.getWORKTYPE());
            cmcoordinate.setImei2(myCoordinate.getImei2());
            cmcoordinate.setLongitude(myCoordinate.getLongitude());
            cmcoordinate.setLatitude(myCoordinate.getLatitude());
            cmcoordinate.setOnLine(myCoordinate.getLOGINSTATUS());
            cmcoordinate.setUserName(myCoordinate.getREALNAME());
            cmcoordinate.setDEPT(myCoordinate.getDEPT());
            cmcoordinate.setDEPTID(myCoordinate.getDEPTID());

            //2.添加用户坐标到redis geo,用于范围查询，和最新位置查询。
            Point point = new Point(cmcoordinate.getLongitude(), cmcoordinate.getLatitude());
            GeoOperations geoOperations = redisTemplate.opsForGeo();
            geoOperations.geoAdd(Constances.LOCATION_OBJ, point, cmcoordinate.getUserid());

            //3.用来展示pc端地图的key
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

            hashOperations.put("menInMap", cmcoordinate.getUserid(), cmcoordinate);

            //4.为每个用户每天创建一个key，用于保存当天的坐标  暂定三天失效
            ZSetOperations setOperations = redisTemplate.opsForZSet();
            String key = currentDate + ":" + cmcoordinate.getUserid();
            long time = date.getTime();
            setOperations.add(key, cmcoordinate, time);
            redisTemplate.expire(key, 2, TimeUnit.DAYS);

            //5.存一个zset 用来判断在线离线
            setOperations.add("currentUser", myCoordinate.getID(), time);
            this.service.add(cmcoordinate);
            return WebApiResponse.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    //根据用户id查询当天历史轨迹
    @GetMapping("findTodayCoordinate")
    public Set<Cmcoordinate> findTodayCoordinate(String userid) {
        String key = userid + "-" + DateUtil.getCurrentDate();
        ZSetOperations setOperations = redisTemplate.opsForZSet();
        Set<Cmcoordinate> set = setOperations.range(userid, 0, -1);
        return set;
    }

    //根据坐标以及距离查询附近所有人，单位为m
    @GetMapping("getRangeUser")
    public WebApiResponse getRangeUser(float lon, float lat, int multiplier) {
        try {
            GeoOperations geoOperations = redisTemplate.opsForGeo();
            Point point = new Point(new Double(lon), new Double(lat));
            CustomMetric customMetric = new CustomMetric(6.37D, "m");
            Distance distance = new Distance(multiplier, customMetric);
            Circle circle = new Circle(point, distance);
            RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
            geoRadiusCommandArgs.includeCoordinates();
            geoRadiusCommandArgs.includeDistance();
            GeoResults geoResult = geoOperations.geoRadius("location", circle, geoRadiusCommandArgs);
            Iterator<GeoResult> iterator = geoResult.iterator();
            List<Map> maps = new ArrayList<>();
            while (iterator.hasNext()) {
                boolean flag = false;
                GeoResult result = iterator.next();
                RedisGeoCommands.GeoLocation geo = (RedisGeoCommands.GeoLocation) result.getContent();
                String userid = (String) geo.getName();
                String userQuery = " SELECT * FROM USERINFO WHERE ID=?1 ";
                List<Map<String, Object>> one = this.service.execSql(userQuery, userid);
                if (one.size() == 1) {
                    Map map = new HashMap();
                    Point point1 = geo.getPoint();
                    String username = String.valueOf(one.get(0).get("ID"));
                    int worktype = Integer.parseInt(one.get(0).get("WORKTYPE").toString());
                    String realname = String.valueOf(one.get(0).get("REALNAME"));
                    if (worktype == 1) {
                        String KHSQL = " SELECT ID,TASK_NAME,PLAN_START_TIME,PLAN_END_TIME,STATUS AS STATUS FROM KH_TASK WHERE PLAN_START_TIME< = trunc(sysdate + 1) AND PLAN_END_TIME >= trunc(sysdate) AND STATUS!=3  AND USER_ID = ?1 ";
                        taskType(KHSQL, maps, flag, map, username, point1, realname, worktype, userid, one);
                    } else if (worktype == 2) {
                        String XSSQL = " SELECT ID,TASK_NAME,PLAN_START_TIME,PLAN_END_TIME,STAUTS AS STATUS FROM XS_ZC_TASK  WHERE is_delete = 0 AND PLAN_START_TIME< = trunc(sysdate + 1) AND PLAN_END_TIME >= trunc(sysdate) AND CM_USER_ID = ?1 ";
                        taskType(XSSQL, maps, flag, map, username, point1, realname, worktype, userid, one);
                    } else if (worktype == 3) {
                        String XCJXZ = "SELECT ID,TASK_NAME,PLAN_START_TIME,PLAN_END_TIME,STATUS AS STATUS  " +
                                "FROM CHECK_LIVE_TASK " +
                                "WHERE to_date('" + DateUtil.timeUtil(2) + "','yyyy-MM-dd HH24:mi') > plan_start_time and to_date('" + DateUtil.timeUtil(1) + "','yyyy-MM-dd HH24:mi') < plan_end_time AND USER_ID=?1";
                        taskType(XCJXZ, maps, flag, map, username, point1, realname, worktype, userid, one);
                    }
                }
            }
            return WebApiResponse.success(maps);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("erro");
        }
    }

    private void taskType(String SQL, List<Map> maps, boolean flag, Map map, String username, Point point1, String realname, int worktype, String userid, List<Map<String, Object>> one) {
        List<Map<String, Object>> maps1 = this.service.execSql(SQL, username);
        if (maps1.size() > 0) {
            for (int i1 = 0; i1 < maps1.size(); i1++) {
                Integer stauts = Integer.parseInt(maps1.get(i1).get("STATUS").toString());
                if (stauts == 1) {
                    map.put("STATUS", 1);
                    break;
                } else if (stauts == 0) {
                    flag = true;
                }
            }
            if (StringUtils.isEmpty(map.get("STATUS"))) {
                if (flag == true) {
                    map.put("STATUS", 0);
                } else {
                    map.put("STATUS", 2);
                }
            }
            map.put("X", point1.getX());
            map.put("Y", point1.getY());
            map.put("USERID", userid);
            map.put("REALNAME", realname);
            map.put("WORKTYPE", worktype);
            map.put("PHONE", one.get(0).get("PHONE"));
            map.put("LOGINSTATUS", one.get(0).get("LOGINSTATUS"));
            map.put("DEPT", one.get(0).get("DEPT"));
            map.put("CLASSNAME", one.get(0).get("CLASSNAME"));
            map.put("TASKXX", maps1);
            maps.add(map);
        }
    }

    @GetMapping("getUserCoordinate")
    public List<Point> getUserCoordinate(String userids) {
        String[] str = userids.split(",");
        GeoOperations geoOperations = redisTemplate.opsForGeo();
        List<Point> list = geoOperations.geoPos(Constances.LOCATION_OBJ, str);
        return list;
    }
}