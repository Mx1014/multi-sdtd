/**
 * 文件名：CMCOORDINATEController
 * 版本信息：
 * 日期：2017/12/20 15:22:15
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.Cmcoordinate;
import com.rzt.entity.MyCoordinate;
import com.rzt.service.CmcoordinateService;
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
import org.springframework.http.HttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

            //2.添加用户坐标到redis geo,用于范围查询，和最新位置查询。
            Point point = new Point(cmcoordinate.getLongitude(), cmcoordinate.getLatitude());
            GeoOperations geoOperations = redisTemplate.opsForGeo();
            geoOperations.geoAdd(Constances.LOCATION_OBJ, point, cmcoordinate.getUserid());

            //3.用来展示pc端地图的key
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.put("menInMap",cmcoordinate.getUserid(),cmcoordinate);

            //4.为每个用户每天创建一个key，用于保存当天的坐标  暂定三天失效
            ZSetOperations setOperations = redisTemplate.opsForZSet();
            String key = currentDate + ":" + cmcoordinate.getUserid();
            setOperations.add(key, cmcoordinate, date.getTime());
            redisTemplate.expire(key,3, TimeUnit.DAYS);

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
    public GeoResults getRangeUser(float lon, float lat, int multiplier) {
        GeoOperations geoOperations = redisTemplate.opsForGeo();
        Point point = new Point(new Double(lon), new Double(lat));
        CustomMetric customMetric = new CustomMetric(6.37D, "m");
        Distance distance = new Distance(multiplier, customMetric);
        Circle circle = new Circle(point, distance);
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
        geoRadiusCommandArgs.includeCoordinates();
        geoRadiusCommandArgs.includeDistance();
        GeoResults geoResult = geoOperations.geoRadius("location", circle, geoRadiusCommandArgs);
        return geoResult;
    }

    @GetMapping("getUserCoordinate")
    public List<Point> getUserCoordinate(String userids){
        String[] str = userids.split(",");
        GeoOperations geoOperations = redisTemplate.opsForGeo();
        List<Point> list = geoOperations.geoPos(Constances.LOCATION_OBJ,str);
        return list;
    }

    public static void main(String[] args) {
        System.out.println(null + "-");
    }
}