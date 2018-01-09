package com.rzt.TimedTask;

import com.rzt.controller.CurdController;
import com.rzt.controller.XSZCTASKController;
import com.rzt.service.TimedService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2017/12/29
 * 定时器任务调度类
 */
@RestController
@RequestMapping("timing")
public class Timing  extends
        CurdController<Timing,TimedService> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NightDynamicScheduledTask night;
    @Autowired
    private DayDynamicScheduledTask day;
    @Autowired
    private XSZCTASKService xszctaskService;

    /**
     * 动态修改定时器内变量
     * @param nightTime  夜间时间
     * @param daytime      白天时间
     * @param startTime    开始时间  代表白天刷新时间
     * @param endTime      结束时间 代表夜间刷新时间
     * @return
     */
    @PostMapping("setCron")
    public WebApiResponse setCron(String nightTime, String daytime, String startTime, String endTime){
        WebApiResponse timedConfig =null;
        try{
            //夜晚
            if((null != nightTime && !"".equals(nightTime)) || (null != endTime && !"".equals(endTime))){//当更改某时段定时周期时  更改当前定时周期时间和当前时段中的定时周期
                //更改夜晚定时起始时间
                String cron = null;
                if(null != endTime && !"".equals(endTime)){
                    cron = "0 0 "+endTime+" * * ?";
                    //cron = "0 "+endTime+" * * * ?";
                }
                night.setCron(cron,nightTime);

            }
            //白天
            if((null != daytime && !"".equals(daytime)) || (null != startTime && !"".equals(startTime))){//当更改某时段定时周期时  更改当前定时周期时间和当前时段中的定时周期
                //更改白天定时起始时间
                //day.setCron("0 0 "+startTime+" * * ?",daytime);
                String cron = null;
                if(null != startTime && !"".equals(startTime)){
                    cron = "0 0 "+startTime+" * * ?";
                    //cron = "0 "+startTime+" * * * ?";
                }
                day.setCron(cron,daytime);

            }
             timedConfig = service.updateTimedConfig(nightTime, daytime, startTime, endTime);


        }catch (Exception e){
            return WebApiResponse.erro("发生错误"+e.getStackTrace());
        }

        return timedConfig;
    }

    @GetMapping("/getTimeConfig")
    public WebApiResponse getTimeConfig(String userId){
        return service.getTimedConfig(userId);
    }




    /**
     * 每三天刷新一次
     * 0点刷新
     */
    @Scheduled(cron="0 0 0 0/3 * ? ")
    private void threeDayScheduledTask(){
        xszctaskService.xsTaskAddAndFindThree();
    }

}
