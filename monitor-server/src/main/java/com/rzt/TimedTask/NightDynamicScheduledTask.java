package com.rzt.TimedTask;

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
 * 负责夜晚定时周期的副定时器
 */
@Component
public class NightDynamicScheduledTask implements SchedulingConfigurer {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
  //默认晚5点开始
  private String cron = "0 0 20 * * ?";
  //private String cron = "0 20 * * * ?";
  private String time = "3";
  //注入主定时器
  @Autowired
  private DynamicScheduledTask dyn;

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.addTriggerTask(new Runnable() {
      @Override
      public void run() {
        /**
         * 更改夜晚时段  起始定时时间
         *  结束时段由夜晚起始时间控制
         */
        dyn.setCron("0 0 0/"+time+" * * ?");
        //dyn.setCron("0 0/"+time+" * * * ?");
        System.out.println("夜晚时间：" + dateFormat.format(new Date()));
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

  public void setCron(String cron,String time) {
    if(null != cron && !"".equals(cron)){
      this.cron = cron;
    }
    if(null != time && !"".equals(time)){
      this.time=time;
    }
  }
}