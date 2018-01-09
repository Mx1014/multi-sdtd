package com.rzt.TimedTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.rzt.controller.CurdController;
import com.rzt.entity.CheckResult;
import com.rzt.entity.TimedTask;
import com.rzt.service.CheckResultService;
import com.rzt.service.TimedService;
import com.rzt.service.XSZCTASKService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DynamicScheduledTask extends CurdController<TimedTask,TimedService> implements SchedulingConfigurer {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
  protected static Logger LOGGER = LoggerFactory.getLogger(XSZCTASKService.class);
  //默认两小时一次
  private String cron = "0 0 0/2 * * ?";
  //private String cron = "0 0/2 * * * ?";
  @Autowired
  private XSZCTASKService service;


  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.addTriggerTask(new Runnable() {
      @Override
      public void run() {
        //定时器启动时抓取任务信息
        service.xsTaskAddAndFind();
        LOGGER.info("主定时器查询数据");
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