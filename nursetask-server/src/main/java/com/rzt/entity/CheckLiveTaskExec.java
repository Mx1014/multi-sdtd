/**    
 * 文件名：CHECKLIVETASKEXEC           
 * 版本信息：    
 * 日期：2017/12/22 14:09:41    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：CHECKLIVETASKEXEC    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/22 14:09:41 
 * 修改人：张虎成    
 * 修改时间：2017/12/22 14:09:41    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CHECK_LIVE_TASK_EXEC")
public class CheckLiveTaskExec implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;        
    	//字段描述: 周期id
   	 @Column(name = "CYCLE_ID")
     private Long cycleId;
    	//字段描述: 稽查任务名称
   	 @Column(name = "TASK_NAME")
     private String taskName;
    	//字段描述: 周期内第多少次任务
   	 @Column(name = "COUNT")
     private Integer count;
    	//字段描述: 任务状态（0未开始 1进行中 2已完成 3已超期）
   	 @Column(name = "STATUS")
     private Integer status;
    	//字段描述: 派发时间
   	 @Column(name = "CREATE_TIME")
     private Date createTime;
    	//字段描述: 稽查人id
   	 @Column(name = "USER_ID")
     private String userId;
    	//字段描述: 0 未稽查 1已稽查2超期
   	 @Column(name = "TASK_STATUS")
     private Integer taskStatus;
    	//字段描述: 通道外协单位
   	 @Column(name = "TDWH_ORG")
     private String tdwhOrg;

	//字段描述: 计划开始时段
	@Column(name = "PLAN_START_TIME")
	//@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date planStartTime;
	//字段描述: 计划结束时段
	@Column(name = "PLAN_END_TIME")
	//@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date planEndTime;


	//字段描述: 任务类别
	@Column(name = "TASK_TYPE")
	private String taskType;

	public Date getPlanStartTime() {
		return planStartTime;
	}

	public void setPlanStartTime(Date planStartTime) {
		this.planStartTime = planStartTime;
	}

	public Date getPlanEndTime() {
		return planEndTime;
	}

	public void setPlanEndTime(Date planEndTime) {
		this.planEndTime = planEndTime;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public void setId(Long id){
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(0,0).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setCycleId(Long cycleId){
		this.cycleId = cycleId;
	}
    public Long getCycleId(){
		return this.cycleId;
	}
	
	

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
    public String getTaskName(){
		return this.taskName;
	}
	
	

	public void setCount(Integer count){
		this.count = count;
	}
    public Integer getCount(){
		return this.count;
	}
	
	

	public void setStatus(Integer status){
		this.status = status;
	}
    public Integer getStatus(){
		return this.status;
	}
	
	

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
    public Date getCreateTime(){
		return this.createTime;
	}
	
	

	public void setUserId(String userId){
		this.userId = userId;
	}
    public String getUserId(){
		return this.userId;
	}
	
	

	public void setTaskStatus(Integer taskStatus){
		this.taskStatus = taskStatus;
	}
    public Integer getTaskStatus(){
		return this.taskStatus;
	}
	
	

	public void setTdwhOrg(String tdwhOrg){
		this.tdwhOrg = tdwhOrg;
	}
    public String getTdwhOrg(){
		return this.tdwhOrg;
	}
	
	

}
