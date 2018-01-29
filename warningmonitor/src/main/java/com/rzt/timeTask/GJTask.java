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
    @Scheduled(cron = "0 30 0 * * ? ")
    public void gjTask(){
        khgj.inspectionMissionOverdue();// 巡视超期任务
        khgj.XSWJRW(); //未按时接任务
        khgj.XSWSX();  //人员未上线
        khgj.KHWSX();  //看护未上线
        khgj.KHWKH();  //未按时间接任务

        khgj.JCOutOfTime();  //稽查超期
        khgj.JCWsx();  //稽查未上线
        khgj.JCWdxc();  //稽查未到达现场
    }



}
