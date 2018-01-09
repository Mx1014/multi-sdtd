package com.rzt.service;

import com.rzt.entity.TimedTask;
import com.rzt.repository.TimedConfigRepository;
import com.rzt.repository.XSZCTASKRepository;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/4
 */
@Service
public class TimedService  extends CurdService<TimedTask,XSZCTASKRepository>{

    protected static Logger LOGGER = LoggerFactory.getLogger(XSZCTASKService.class);
    @Autowired
    private TimedConfigRepository repository;


    //定时器配置ID
    private static final String TIMED_CONFIG = "TIME_CONFIG";

    /**
     * 配置定时器时需要的回显数据
     * @return
     */
    public WebApiResponse getTimedConfig(){
        List<Map<String, Object>> maps = null;
        try {
            String sql = "SELECT START_TIME,END_TIME,DAY_ZQ,NIGHT_ZQ from TIMED_CONFIG WHERE ID = '"+TIMED_CONFIG+"'";
            maps = this.execSql(sql, null);
            LOGGER.info("定时器配置查询成功");
        }catch (Exception e){
            LOGGER.error("定时器配置查询失败"+e.getMessage());
            return WebApiResponse.erro("定时器配置查询失败"+e.getMessage());
        }
        return WebApiResponse.success(maps);
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
