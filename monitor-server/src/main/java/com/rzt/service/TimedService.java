package com.rzt.service;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.repository.TimedConfigRepository;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 李成阳
 * 2018/1/4
 */
@Service
public class TimedService  extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(XSZCTASKService.class);
    @Autowired
    private TimedConfigRepository repository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //定时器配置ID
    private static final String TIMED_CONFIG = "TIME_CONFIG";

    /**
     * 配置定时器时需要的回显数据
     * @return
     */
    @Transactional
    public WebApiResponse getTimedConfig(String userId){
        List<Map<String, Object>> maps = null;
        String time  = "";
      try {
          String sql = "SELECT START_TIME,END_TIME,DAY_ZQ,NIGHT_ZQ from TIMED_CONFIG WHERE ID = '"+TIMED_CONFIG+"'";
          maps = this.execSql(sql, null);
          Map<String, Object> stringObjectMap = maps.get(0);

          Map<String, Object> map = maps.get(0);
          Date date = new Date();
          SimpleDateFormat hh = new SimpleDateFormat("HH");
          int i = Integer.parseInt(hh.format(date));
          if(i>=Integer.parseInt((String) map.get("START_TIME")) && i<Integer.parseInt((String) map.get("END_TIME"))){
              time = (String) map.get("DAY_ZQ")+"小时";
          }else{
              time = (String) map.get("NIGHT_ZQ")+"小时";
          }


          LOGGER.info("定时器配置查询成功");

          if(null!= userId && !"".equals(userId)){
              Object userInformation = redisTemplate.opsForHash().get("UserInformation", userId);
              JSONObject jsonObject1 = JSONObject.parseObject(userInformation.toString());
              if(null != jsonObject1){
                  String roletype = (String) jsonObject1.get("ROLETYPE");
                  if(null != roletype && !"".equals(roletype)){
                      switch (Integer.parseInt(roletype)){
                          case 0 :{

                              return WebApiResponse.success("3天");
                          }
                          case 1 :{
                              return WebApiResponse.success("3天");
                          }
                          case 2 :{
                              return WebApiResponse.success(time);
                          }
                          default:{
                              LOGGER.error("获取权限信息失败");
                              return WebApiResponse.success("周期未知");
                          }
                      }
                  }
              }


          }

      }catch (Exception e){
          LOGGER.error("获取权限信息失败"+e.getMessage());
          return WebApiResponse.success("周期未知");
      }

        return WebApiResponse.success(time);
    }

    /**
     * 定时器配置
     * @param nightTime 夜间周期
     * @param daytime   白天周期
     * @param startTime 开始时间 代表白天的刷新点
     * @param endTime   结束时间 代表夜间刷新点
     * @return
     */
    @Transactional
    public WebApiResponse updateTimedConfig(String nightTime, String daytime, String startTime, String endTime){
        int i = 0;
        try {
             i = repository.updateTimedConfig(nightTime, daytime, startTime, endTime, TIMED_CONFIG);
        }catch (Exception e){
            LOGGER.error("修改定时器配置失败"+e.getMessage());
            return WebApiResponse.erro("修改定时器配置失败"+e.getMessage());
        }
        return WebApiResponse.success(i==0?"失败":"成功");
    }
}
