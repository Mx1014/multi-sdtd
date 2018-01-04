package com.rzt.service.quartz;

import com.rzt.service.KhTaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.TimerTask;

/**
 * Created by admin on 2018/1/1.
 */
public class CreateTask extends TimerTask{

    @Autowired
    private KhTaskService service;

    @Override
    public void run() {
        try {
            service.CreateTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //定时生成任务的逻辑
    public void createTask(){
//        String sql =
    }
}
