package com.rzt.TimedTask;

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
                System.out.println("夜晚");
                System.out.println(nightTime);
                System.out.println(cron);
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
                System.out.println("白天");
                System.out.println(cron);
                System.out.println(daytime);
            }
            return WebApiResponse.success("success");
        }catch (Exception e){
            return WebApiResponse.erro("发生错误"+e.getStackTrace());
        }


    }

    /**
     * 每三天刷新一次
     * 0点刷新
     */
    @Scheduled(cron="0 0 0 0/2  * ? ")
    private void threeDayScheduledTask(){
        System.out.println("0点刷新");

    }


}
