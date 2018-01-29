package com.rzt.service.quartz;

import com.rzt.service.KhTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 2018/1/1.
 */
@Component
@EnableScheduling
public class QuartzService  {

    @Autowired
    private KhTaskService service;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
/*
   @Scheduled(cron = "0 0 0 * * ?") // 每天1点执行一次
    // @Scheduled(cron = "*//**//**//**//*60 * * * * ?") // 每60秒执行一次
    public void reportCurrentTime() {
        service.createTask();
    }//ss*/
    /*//每天0点执行
    private String cron = "0 0 0 * * ?";

    private String time = "2";
    //注入主定时器
    @Autowired
    private CreateTask task;
    //    每分钟启动
  // @Scheduled(cron = "0 0/1 * * * ?")
    public void timerToNow(){
        System.out.println("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
    public static void timer4() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0); // 控制时
        calendar.set(Calendar.MINUTE, 0);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒

        Date time = calendar.getTime();         // 得出执行任务的时间,此处为今天的12：00：00

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

            }
        }, time, 1000 * 60 * 60 * 24);// 这里设定将延时每天固定执行
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                task.setCron(cron);
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                return null;
            }
        });
    }*/


}