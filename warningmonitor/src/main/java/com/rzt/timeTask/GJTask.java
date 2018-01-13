package com.rzt.timeTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 统一定时使用
 * Created by huyuening on 2018/1/12.
 */
@Component
public class GJTask {

    @Autowired
    private KHGJ khgj;


    //定时拉数据  1
    @Scheduled(cron = "0/15 * * * * ?")
    public void gjTask(){
        khgj.inspectionMissionOverdue();// 巡视超期任务
        khgj.XSWJRW();
        //alarm.notReceivingATaskAtASpecifiedTime();//巡视未按规定时间接任务
    }


   // @Scheduled(cron = "0/10 * * * * ?")
    public void gjTask2(){
        khgj.XSWSX();   //巡视未上线 给定时拉取数据用
        System.out.println("++XSWSX+++++++++++++++++++++++++++++++++++++++++++++++");
    }

    //1
    //@Scheduled(cron = "0/30 * * * * ?")
    public void gjTask3(){
        khgj.XSWJRW();  //巡视未按规定时间接任务 定时拉去数据用
        System.out.println("++XSWJRW+++++++++++++++++++++++++++++++++++++++++++++++");
    }

   // @Scheduled(cron = "0/30 * * * * ?")
    public void gjTask4(){
        khgj.KHWSX();   //看护未上线  给定时拉取数据用
        System.out.println("++KHWSX+++++++++++++++++++++++++++++++++++++++++++++++");
    }
    //@Scheduled(cron = "0/30 * * * * ?")
    public void gjTask5(){
        khgj.KHWKH();   //看护人员未按规定时间看护任务 定时拉取数据用
        System.out.println("++KHWKH+++++++++++++++++++++++++++++++++++++++++++++++");
    }


}
