package com.rzt.entity.model;

import com.rzt.entity.CheckLiveTaskCycle;
import com.rzt.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;



public class CheckLiveTaskCycleModel extends CheckLiveTaskCycle{


    private String execId;
    //计划开始时间
    private String startTime;
    //计划结束时间
    private String endTime;

    //创建时间处理
    private  String time;

    //任务ids
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    private SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void setStartTime(String startTime) {
        try{
            Date time = DateUtil.getPlanStartTime(startTime);
            super.setPlanStartTime(time);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setEndTime(String endTime) {
        try{
            Date time = DateUtil.getPlanEndTime(endTime);
            super.setPlanEndTime(time);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        try{
            this.time = time;
            Date d = sdf.parse(this.time);
            super.setCreateTime(d);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
