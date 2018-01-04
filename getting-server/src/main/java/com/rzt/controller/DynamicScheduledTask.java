package com.rzt.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;


/**
 * 主定时器    负责周期性的定时执行
 *      周期逻辑由副定时器负责
 */
@Component
public class DynamicScheduledTask implements SchedulingConfigurer {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
  //默认两小时一次
  //private String cron = "0 0 0/2 * * ?";
  private String cron = "0 0/2 * * * ?";


  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.addTriggerTask(new Runnable() {
      @Override
      public void run() {
        /**
         * 序号	抽查时间	通道公司	外协单位	任务详情	责任人	联系方式	抽查类型
         * 获取当前巡查对象   故障多发线路  隐患集中地区   评分较低人员
         */
        System.out.println("主定时器时间：" + dateFormat.format(new Date()));
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

  public void setCron(String cron) {
    this.cron = cron;
  }
}