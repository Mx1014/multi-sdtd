package com.rzt.service.quartz;

import com.rzt.controller.CurdController;
import com.rzt.entity.KhTask;
import com.rzt.service.KhTaskService;
import com.rzt.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * Created by admin on 2018/1/1.
 */
@Service
public class CreateTask extends CurdController<KhTask, KhTaskService> implements SchedulingConfigurer {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
    @Autowired
    private KhTaskService service;

    private String cron = "0 0 0 * * ?";

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                //定时器启动时抓取任务信息
               // service.createTask();
                System.out.println("定时任务"+dateFormat.format(DateUtil.getCurrentDate()));
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
