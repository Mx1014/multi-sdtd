package com.rzt.entity.model;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by admin on 2017/12/5.
 */
public class CheckLiveTasks extends BaseEntity implements Serializable{
    private String id;
    //字段描述: 稽查主任务id
    private String taskId;
    //字段描述: 稽查人id
    private String userId;
    //字段描述: 稽查任务名称
    private String taskName;
    //字段描述: 稽查任务派发时间
    private String createTime;
    //字段描述: 稽查任务计划开始时间
    private String planStartTime;
    //字段描述: 稽查任务计划结束时间
    private String planEndTime;
    //字段描述: 任务状态  0 待稽查 1进行中 2已稽查 3已过期
    private String status;
    //字段描述: 稽查任务类型（0 看护 1巡视）
    private String checkType;
    //字段描述: 稽查部门（0 属地公司 1北京公司）
    private String checkDept;
    //字段描述: 稽查周期
    private String checkCycle;
    //字段描述: 到达现场时间
    private String ddxcTime;

    public void setId(String id){
        this.id = id;
    }
    public String getId(){
        return this.id;
    }

    public void setTaskId(String taskId){
        this.taskId = taskId;
    }
    public String getTaskId(){
        return this.taskId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getUserId(){
        return this.userId;
    }

    public void setTaskName(String taskName){
        this.taskName = taskName;
    }
    public String getTaskName(){
        return this.taskName;
    }

    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    public String getCreateTime(){
        return this.createTime;
    }

    public void setPlanStartTime(String planStartTime){
        this.planStartTime = planStartTime;
    }
    public String getPlanStartTime(){
        return this.planStartTime;
    }

    public void setPlanEndTime(String planEndTime){
        this.planEndTime = planEndTime;
    }
    public String getPlanEndTime(){
        return this.planEndTime;
    }

    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public void setCheckType(String checkType){
        this.checkType = checkType;
    }
    public String getCheckType(){
        return this.checkType;
    }

    public void setCheckDept(String checkDept){
        this.checkDept = checkDept;
    }
    public String getCheckDept(){
        return this.checkDept;
    }

    public void setCheckCycle(String checkCycle){
        this.checkCycle = checkCycle;
    }
    public String getCheckCycle(){
        return this.checkCycle;
    }

    public void setDdxcTime(String ddxcTime){
        this.ddxcTime = ddxcTime;
    }
    public String getDdxcTime(){
        return this.ddxcTime;
    }

}
