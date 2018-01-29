package com.rzt.entity.model;

import com.rzt.entity.KhTask;

import java.util.List;

/**
 * Created by admin on 2017/12/2.
 */
public class KhTaskModel {
    private String planStartTime;

    private String planEndTime;

    private String taskName;

    private String userId;

    private String status;

    private String userName;

    private String taskType;

    private String tdOrg;

    public void setTdOrg(String tdOrg) {
        this.tdOrg = tdOrg;
    }

    public String getTdOrg() {
        return tdOrg;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(String planEndTime) {
        this.planEndTime = planEndTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
