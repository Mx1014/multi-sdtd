package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
/**
 * Created by admin on 2017/12/5.
 */
public class CheckLiveTaskDetail extends BaseEntity implements Serializable{
    //字段描述:
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
    //字段描述: 稽查主任务id
    @Column(name = "TASK_ID")
    private String taskId;
    //字段描述: 稽查人id
    @Column(name = "USER_ID")
    private String userId;
    //字段描述: 稽查任务名称
    @Column(name = "TASK_NAME")
    private String taskName;
    //字段描述: 稽查任务派发时间
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述: 稽查任务计划开始时间
    @Column(name = "PLAN_START_TIME")
    private Date planStartTime;
    //字段描述: 稽查任务计划结束时间
    @Column(name = "PLAN_END_TIME")
    private Date planEndTime;
    //字段描述: 任务状态  0 待稽查 1进行中 2已稽查 3已过期
    @Column(name = "STATUS")
    private String status;
    //字段描述: 稽查任务类型
    @Column(name = "CHECK_TYPE")
    private String checkType;

    public void setId(String id){
        this.id = UUID.randomUUID().toString();
    }
    @ExcelResources(title="",order=1)
    public String getId(){
        return this.id;
    }

    public void setTaskId(String taskId){
        this.taskId = taskId;
    }
    @ExcelResources(title="稽查主任务id",order=2)
    public String getTaskId(){
        return this.taskId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
    @ExcelResources(title="稽查人id",order=3)
    public String getUserId(){
        return this.userId;
    }

    public void setTaskName(String taskName){
        this.taskName = taskName;
    }
    @ExcelResources(title="稽查任务名称",order=4)
    public String getTaskName(){
        return this.taskName;
    }

    public void setCreateTime(Date createTime){
        this.createTime = createTime;
    }
    @ExcelResources(title="稽查任务派发时间",order=5)
    public Date getCreateTime(){
        return this.createTime;
    }

    public void setPlanStartTime(Date planStartTime){
        this.planStartTime = planStartTime;
    }
    @ExcelResources(title="稽查任务计划开始时间",order=6)
    public Date getPlanStartTime(){
        return this.planStartTime;
    }

    public void setPlanEndTime(Date planEndTime){
        this.planEndTime = planEndTime;
    }
    @ExcelResources(title="稽查任务计划结束时间",order=7)
    public Date getPlanEndTime(){
        return this.planEndTime;
    }

    public void setStatus(String status){
        this.status = status;
    }
    @ExcelResources(title="任务状态",order=8)
    public String getStatus(){
        return this.status;
    }

    public void setCheckType(String checkType){
        this.checkType = checkType;
    }
    @ExcelResources(title="稽查任务类型",order=9)
    public String getCheckType(){
        return this.checkType;
    }
}
