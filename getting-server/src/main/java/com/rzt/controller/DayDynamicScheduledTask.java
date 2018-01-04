package com.rzt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 负责白天定时周期的副定时器
 */
@Component
public class DayDynamicScheduledTask implements SchedulingConfigurer {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
  //默认早8点开始
  //private String cron = "0 0 8 * * ?";
  private String cron = "0 52 11 * * ?";
  private Integer time = 3;
  //注入主定时器
  @Autowired
  private DynamicScheduledTask dyn;


  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.addTriggerTask(new Runnable() {
      @Override
      public void run() {
          /**
           * 更改白天时段  起始定时时间
         *  结束时段由夜晚起始时间控制
         */
        //dyn.setCron("0 0 0/"+time+" * * ?");
        dyn.setCron("0 0/"+time+" * * * ?");
        System.out.println("白天时间：" + dateFormat.format(new Date()));
        System.out.println("表达式"+cron);


      }
    }, new Trigger() {
      @Override
      public Date nextExecutionTime(TriggerContext triggerContext) {
    // 定时任务触发，可修改定时任务的执行周期
    CronTrigger trigger = new CronTrigger(cron);
    Date nextExecDate = trigger.nextExecutionTime(triggerContext);
    return nextExecDate;
      }
    });
  }

  public void setCron(String cron,Integer time) {
    if(null != cron && !"".equals(cron)){
      this.cron = cron;
    }
    if(null != time && time>0){
      this.time=time;
    }

  }
}