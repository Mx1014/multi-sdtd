/**    
 * 文件名：CMCOORDINATEController
 * 版本信息：    
 * 日期：2017/12/20 15:22:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;
import com.rzt.entity.Cmcoordinate;
import com.rzt.service.CmcoordinateService;
import com.rzt.utils.Constances;
import com.rzt.utils.DateUtil;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 类名称：CMCOORDINATEController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/20 15:22:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/20 15:22:15    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("Cmcoordinate")
public class CmcoordinateController extends
		CurdController<Cmcoordinate, CmcoordinateService> {

	private RedisTemplate<String,Object> redisTemplate;

	@PostMapping("addCmcoordinate")
    public void addCmcoordinate(Cmcoordinate cmcoordinate){
		Date date = new Date();
		cmcoordinate.setCreatetime(date);
		Point point  = new Point(cmcoordinate.getLatitude(),cmcoordinate.getLongitude());
		GeoOperations geoOperations = redisTemplate.opsForGeo();
		//添加用户坐标到redis geo,用于范围查询，和最新位置查询。
		geoOperations.geoAdd(Constances.LOCATION_OBJ,point,cmcoordinate.getUserid());
		ZSetOperations setOperations = redisTemplate.opsForZSet();
		//为每个用户每天创建一个key，用于保存当天的坐标。
		String key = cmcoordinate.getUserid() + "-" + DateUtil.getCurrentDate();
		setOperations.add(key,cmcoordinate,date.getTime());

		this.service.add(cmcoordinate);
	}

	//根据用户id查询当天历史轨迹
	@GetMapping("findTodayCoordinate")
	public Set<Cmcoordinate> findTodayCoordinate(String userid){
		String key = userid + "-" + DateUtil.getCurrentDate();
		ZSetOperations setOperations = redisTemplate.opsForZSet();
		Set<Cmcoordinate> set = setOperations.range(userid,0,-1);
		return set;
	}

	//根据坐标以及距离查询附近所有人，单位为m
	@GetMapping("getRangeUser")
	public GeoResults getRangeUser(float lon,float lat,int multiplier){
		GeoOperations geoOperations = redisTemplate.opsForGeo();
		Point point = new Point(new Double(lon),new Double(lat));
		CustomMetric customMetric = new CustomMetric(6.37D,"m");
		Distance distance = new Distance(multiplier,customMetric);
		Circle circle = new Circle(point,distance);
		RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
		geoRadiusCommandArgs.includeCoordinates();
		geoRadiusCommandArgs.includeDistance();
		GeoResults geoResult =  geoOperations.geoRadius("location",circle,geoRadiusCommandArgs);
		return geoResult;
	}
}