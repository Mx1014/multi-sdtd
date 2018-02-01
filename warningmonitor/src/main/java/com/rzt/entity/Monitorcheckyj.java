/**    
 * 文件名：MONITORCHECKYJ           
 * 版本信息：    
 * 日期：2018/01/08 11:06:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：MONITORCHECKYJ    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2018/01/08 11:06:23 
 * 修改人：张虎成    
 * 修改时间：2018/01/08 11:06:23    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="MONITOR_CHECK_YJ")
public class Monitorcheckyj implements Serializable{
	//字段描述: 任务id
   	 @Id
     private Long id;        
    	//字段描述: 任务ID
   	 @Column(name = "TASK_ID")
     private Long taskId;
    	//字段描述: 处理中创建时间
   	 @Column(name = "CREATE_TIME_Z")
     private Date createTimeZ;
    	//字段描述: 已处理创建时间
   	 @Column(name = "CREATE_TIME_C")
     private Date createTimeC;
    	//字段描述: 任务类型  1 巡视  2 看护  3 稽查
   	 @Column(name = "TASK_TYPE")
     private Integer taskType;
    	//字段描述: 处理状态  0未处理  1处理中  2已处理
   	 @Column(name = "STATUS")
     private Integer status;
    	//字段描述: 处理中建议信息
   	 @Column(name = "CHECKZ_INFO")
     private String checkzInfo;
    	//字段描述: 处理中建议信息
   	 @Column(name = "CHECKZ_APP_INFO")
     private String checkzAppInfo;
    	//字段描述: 已处理建议
   	 @Column(name = "CHECKC_INFO")
     private String checkcInfo;
    	//字段描述: 是否可见 0可以查看  1不可查看
   	 @Column(name = "IS_VIEW")
     private Integer isView;
    	//字段描述: 告警任务创建时间
   	 @Column(name = "CREATE_TIME")
     private Date createTime;

	@Column(name = "TASK_NAME")
	private String taskName;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "DEPTID")
	private String deptId;

	//字段描述: 告警类型
	@Column(name = "WARNING_TYPE")
	private Integer warningType;

	@Column(name = "CHECK_USER_ID")
	private String checkUserId;
	@Column(name = "CHECK_DEPTID")
	private String checkDeptId;

	@Column(name = "REASON")
	private String reason;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getCheckUserId() {
		return checkUserId;
	}

	public void setCheckUserId(String checkUserId) {
		this.checkUserId = checkUserId;
	}

	public String getCheckDeptId() {
		return checkDeptId;
	}

	public void setCheckDeptId(String checkDeptId) {
		this.checkDeptId = checkDeptId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Date getCreateTimeZ() {
		return createTimeZ;
	}

	public void setCreateTimeZ(Date createTimeZ) {
		this.createTimeZ = createTimeZ;
	}

	public Date getCreateTimeC() {
		return createTimeC;
	}

	public void setCreateTimeC(Date createTimeC) {
		this.createTimeC = createTimeC;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCheckzInfo() {
		return checkzInfo;
	}

	public void setCheckzInfo(String checkzInfo) {
		this.checkzInfo = checkzInfo;
	}

	public String getCheckzAppInfo() {
		return checkzAppInfo;
	}

	public void setCheckzAppInfo(String checkzAppInfo) {
		this.checkzAppInfo = checkzAppInfo;
	}

	public String getCheckcInfo() {
		return checkcInfo;
	}

	public void setCheckcInfo(String checkcInfo) {
		this.checkcInfo = checkcInfo;
	}

	public Integer getIsView() {
		return isView;
	}

	public void setIsView(Integer isView) {
		this.isView = isView;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public Integer getWarningType() {
		return warningType;
	}

	public void setWarningType(Integer warningType) {
		this.warningType = warningType;
	}
}
