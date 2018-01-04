package com.rzt.controller;

import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2017/12/29
 * 定时器任务调度类
 */
@RestController
@RequestMapping("timing")
public class Timing  {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NightDynamicScheduledTask night;
    @Autowired
    private DayDynamicScheduledTask day;
    //计算天数用
    private Integer num = 0;

    /**
     * 动态修改定时器内变量
     * @param
     */
    @GetMapping("setCron")
    public WebApiResponse setCron(Integer nightTime, Integer daytime, Integer startTime, Integer endTime){
        try{
            //夜晚
            if((null != nightTime && nightTime>0) || (null != endTime && endTime>0)){//当更改某时段定时周期时  更改当前定时周期时间和当前时段中的定时周期
                //更改夜晚定时起始时间
                //night.setCron("0 0 "+endTime+" * * ?",nightTime);
                String cron = null;
                if(null != endTime && endTime>0){
                    cron = "0 0 "+endTime+" * * ?";
                }
                night.setCron(cron,nightTime);
            }
            //白天
            if((null != daytime && daytime>0) || (null != startTime && startTime>0)){//当更改某时段定时周期时  更改当前定时周期时间和当前时段中的定时周期
                //更改白天定时起始时间
                //day.setCron("0 0 "+startTime+" * * ?",daytime);
                String cron = null;
                if(null != startTime && startTime>0){
                    cron = "0 0 "+startTime+" * * ?";
                }
                day.setCron(cron,daytime);
            }
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("发生错误"+e.getStackTrace());
        }


    }

    /**
     * 每三天刷新一次
     * 在每天12点时刷新  当刷新时为变量num加一   当变量模3得0时证明时间过了三天
     */
    @Scheduled(cron="0 0 12 * * ?")
    private void a(){
        System.out.println("中午12点一次刷新");
        num++;
        if(num%3 == 0){//三天  写入逻辑
            System.out.println("三天到了 ");
        }
    }


}
