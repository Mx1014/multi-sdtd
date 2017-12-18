package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
/**
 * Created by admin on 2017/12/5.
 */
@Entity
@Table(name="CHENK_LIVE_TASK_CYCLE")
public class CheckLiveTaskCycle  implements Serializable{
    //字段描述: 稽查周期id
    @Id
    private Long id;
    //字段描述: 任务生成总次数
    @Column(name = "COUNT")
    private Long count;
    //字段描述: 稽查人id
    @Column(name = "USER_ID")
    private String userId;
    //字段描述: 稽查任务名称
    @Column(name = "TASK_NAME")
    private String taskName;
    //字段描述: 稽查任务创建时间
    @Column(name = "CREATE_TIME")
    private Date createTime;
    //字段描述: 计划开始时段
    @Column(name = "PLAN_START_TIME")
    private Date planStartTime;
    //字段描述: 计划结束时段
    @Column(name = "PLAN_END_TIME")
    private Date planEndTime;
    //字段描述: 是否停用（0不停用 1停用）
    @Column(name = "STATUS")
    private String status;
    //字段描述: 任务类别（0 看护  1巡视）
    @Column(name = "TASK_TYPE")
    private String taskType;
    //字段描述: 稽查部门（0 属地公司 1北京公司）
    @Column(name = "CHECK_DEPT")
    private String checkDept;
    //字段描述: 稽查周期
    @Column(name = "CHECK_CYCLE")
    private String checkCycle;

    public void setId(){
        this.id =   Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
    }
    @ExcelResources(title="稽查周期id",order=1)
    public Long getId(){
        return this.id;
    }

    public void setCount(Long count){
        this.count = count;
    }
    @ExcelResources(title="任务生成总次数",order=2)
    public Long getCount(){
        return this.count;
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
    @ExcelResources(title="稽查任务创建时间",order=5)
    public Date getCreateTime(){
        return this.createTime;
    }

    public void setPlanStartTime(Date planStartTime){
        this.planStartTime = planStartTime;
    }
    @ExcelResources(title="计划开始时段",order=6)
    public Date getPlanStartTime(){
        return this.planStartTime;
    }

    public void setPlanEndTime(Date planEndTime){
        this.planEndTime = planEndTime;
    }
    @ExcelResources(title="计划结束时段",order=7)
    public Date getPlanEndTime(){
        return this.planEndTime;
    }

    public void setStatus(String status){
        this.status = status;
    }
    @ExcelResources(title="是否停用（0不停用 1停用）",order=8)
    public String getStatus(){
        return this.status;
    }

    public void setTaskType(String taskType){
        this.taskType = taskType;
    }
    @ExcelResources(title="任务类别（0 看护  1巡视）",order=9)
    public String getTaskType(){
        return this.taskType;
    }

    public void setCheckDept(String checkDept){
        this.checkDept = checkDept;
    }
    @ExcelResources(title="稽查部门（0 属地公司 1北京公司）",order=10)
    public String getCheckDept(){
        return this.checkDept;
    }

    public void setCheckCycle(String checkCycle){
        this.checkCycle = checkCycle;
    }
    @ExcelResources(title="稽查周期",order=11)
    public String getCheckCycle(){
        return this.checkCycle;
    }

}