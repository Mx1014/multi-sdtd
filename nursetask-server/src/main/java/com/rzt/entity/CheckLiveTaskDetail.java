package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
/**
 * Created by admin on 2017/12/5.
 */
@Entity
@Table(name="CHENK_LIVE_TASK_DETAIL")
public class CheckLiveTaskDetail extends BaseEntity implements Serializable{
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
    private String createTime;
    //字段描述: 稽查任务计划开始时间
    @Column(name = "PLAN_START_TIME")
    private String planStartTime;
    //字段描述: 稽查任务计划结束时间
    @Column(name = "PLAN_END_TIME")
    private String planEndTime;
    //字段描述: 任务状态  0 待稽查 1进行中 2已稽查 3已过期
    @Column(name = "STATUS")
    private String status;
    //字段描述: 稽查任务类型（0 看护 1巡视）
    @Column(name = "CHECK_TYPE")
    private String checkType;
    //字段描述: 稽查部门（0 属地公司 1北京公司）
    @Column(name = "CHECK_DEPT")
    private String checkDept;
    //字段描述: 稽查周期
    @Column(name = "CHECK_CYCLE")
    private String checkCycle;
    //字段描述: 到达现场时间
    @Column(name = "DDXC_TIME")
    private String ddxcTime;

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

    public void setCreateTime(String createTime){
        this.createTime = createTime;
    }
    @ExcelResources(title="稽查任务派发时间",order=5)
    public String getCreateTime(){
        return this.createTime;
    }

    public void setPlanStartTime(String planStartTime){
        this.planStartTime = planStartTime;
    }
    @ExcelResources(title="稽查任务计划开始时间",order=6)
    public String getPlanStartTime(){
        return this.planStartTime;
    }

    public void setPlanEndTime(String planEndTime){
        this.planEndTime = planEndTime;
    }
    @ExcelResources(title="稽查任务计划结束时间",order=7)
    public String getPlanEndTime(){
        return this.planEndTime;
    }

    public void setStatus(String status){
        this.status = status;
    }
    @ExcelResources(title="任务状态  0 待稽查 1进行中 2已稽查 3已过期",order=8)
    public String getStatus(){
        return this.status;
    }

    public void setCheckType(String checkType){
        this.checkType = checkType;
    }
    @ExcelResources(title="稽查任务类型（0 看护 1巡视）",order=9)
    public String getCheckType(){
        return this.checkType;
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

    public void setDdxcTime(String ddxcTime){
        this.ddxcTime = ddxcTime;
    }
    @ExcelResources(title="到达现场时间",order=12)
    public String getDdxcTime(){
        return this.ddxcTime;
    }

}
