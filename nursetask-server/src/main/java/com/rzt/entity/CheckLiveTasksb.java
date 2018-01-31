/**    
 * 文件名：CheckLiveTasksb           
 * 版本信息：    
 * 日期：2018/01/21 08:27:36    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：CheckLiveTasksb
 * 创建人：李泽州
 * 创建时间：2018/01/21 08:27:36
 */
@Entity
public class CheckLiveTasksb  implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;
    	//字段描述: 
   	 @Column(name = "TASK_ID")
     private Long taskId;
    	//字段描述: （0 正常 1保电 2 特殊）
   	 @Column(name = "TASK_TYPE")
     private Integer taskType;
    	//字段描述: 稽查任务类型（0运检部 1各属地）
   	 @Column(name = "CHECK_TYPE")
     private Integer checkType;
    	//字段描述: 任务消缺时间
   	 @Column(name = "UPDATE_TIME")
     private Date updateTime;
    	//字段描述: 稽查任务创建时间
   	 @Column(name = "CREATE_TIME")
     private Date createTime;
    	//字段描述: 
   	 @Column(name = "PLAN_START_TIME")
     private Date planStartTime;
    	//字段描述: 
   	 @Column(name = "PLAN_END_TIME")
     private Date planEndTime;
    	//字段描述: 稽查任务名称
   	 @Column(name = "TASK_NAME")
     private String taskName;
    	//字段描述: 稽查人id
   	 @Column(name = "USER_ID")
     private String userId;
    	//字段描述: 隐患上报id
   	 @Column(name = "YHSB_ID")
     private Long yhsbId;
    	//字段描述: 任务派发状态  0未接单 1进行中 2已完成 3超期
   	 @Column(name = "STATUS")
     private Integer status;
    	//字段描述: 通道运维单位
   	 @Column(name = "TD_ORG_NAME")
     private String tdOrgName;
    	//字段描述: 稽查人部门
   	 @Column(name = "CHECK_DEPT")
     private String checkDept;
    	//字段描述: 通道单位id

   	 @Column(name = "TD_ORG_ID")
     private String tdOrgId;

	public void setId(Long id){
		if(id==null||id==0){
			SnowflakeIdWorker instance = SnowflakeIdWorker.getInstance(8, 6);
			this.id = instance.nextId();
		}else{
			this.id = id;
		}
	}

	public Long getId(){
		return this.id;
	}

	public void setTaskId(Long taskId){
		this.taskId = taskId;
	}
	public Long getTaskId(){
		return this.taskId;
	}

	public void setTaskType(Integer taskType){
		this.taskType = taskType;
	}
	public Integer getTaskType(){
		return this.taskType;
	}

	public void setCheckType(Integer checkType){
		this.checkType = checkType;
	}
	public Integer getCheckType(){
		return this.checkType;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	public Date getUpdateTime(){
		return this.updateTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	public Date getCreateTime(){
		return this.createTime;
	}

	public void setPlanStartTime(Date planStartTime){
		this.planStartTime = planStartTime;
	}
	public Date getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(Date planEndTime){
		this.planEndTime = planEndTime;
	}
	public Date getPlanEndTime(){
		return this.planEndTime;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	public String getTaskName(){
		return this.taskName;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}
	public String getUserId(){
		return this.userId;
	}

	public void setYhsbId(Long yhsbId){
		this.yhsbId = yhsbId;
	}
	public Long getYhsbId(){
		return this.yhsbId;
	}

	public void setStatus(Integer status){
		this.status = status;
	}
	public Integer getStatus(){
		return this.status;
	}

	public void setTdOrgName(String tdOrgName){
		this.tdOrgName = tdOrgName;
	}
	public String getTdOrgName(){
		return this.tdOrgName;
	}

	public void setCheckDept(String checkDept){
		this.checkDept = checkDept;
	}
	public String getCheckDept(){
		return this.checkDept;
	}

	public void setTdOrgId(String tdOrgId){
		this.tdOrgId = tdOrgId;
	}

	public String getTdOrgId(){
		return this.tdOrgId;
	}

}