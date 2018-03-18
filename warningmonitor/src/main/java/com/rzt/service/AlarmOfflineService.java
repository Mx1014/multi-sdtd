package com.rzt.service;

import com.rzt.entity.AlarmOffline;
import com.rzt.repository.AlarmOfflineRepository;
import com.rzt.utils.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlarmOfflineService extends CurdService<AlarmOffline, AlarmOfflineRepository> {

    @Autowired
    private AlarmOfflineRepository repository;

    //未按时开始任务 向ALARM表中添加数据
    public void addAlarm(String[] messages){
        Integer taskType=null;
        if("4".equals(messages[3])){
            taskType=2; //ALARM_NOT_ON_TIME_TASK表中任务类型 2是巡视 1是看护 与MONITOR_CHECK_EJ表中类型相反，所以转换一下
        }else if ("10".equals(messages[3])){
            taskType=1;
        }
        repository.addNotNoTimeTask(SnowflakeIdWorker.getInstance(10,10).nextId(),Long.parseLong(messages[1]),messages[4],taskType);
    }

}
