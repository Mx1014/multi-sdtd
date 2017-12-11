/**    
 * 文件名：CheckLiveTask
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：CheckLiveTask
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/04 15:13:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/04 15:13:15    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CHECK_LIVE_TASK")
public class CheckLiveTask implements Serializable{
	//字段描述:
	@Id
	private Long id;
	//字段描述: 被稽查的任务id
	@Column(name = "TASK_ID")
	private Long taskId;
	//字段描述: 稽查任务类别（0新增 1正常 2危机）
	@Column(name = "TASK_TYPE")
	private String taskType;
	//字段描述: 稽查任务类型（0 看护  1巡视）
	@Column(name = "CHECK_TYPE")
	private String checkType;
	//字段描述: 状态修改时间
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	//字段描述: 稽查任务创建时间
	@Column(name = "CREATE_TIME")
	private Date createTime;
	//字段描述: 稽查任务名称
	@Column(name = "TASK_NAME")
	private String taskName;
	//字段描述: 稽查人id
	@Column(name = "USER_ID")
	private Long userId;
	//字段描述: 稽查任务状态（0 未消除 1 已消除）
	@Column(name = "STATUS")
	private String status;
	//字段描述: 通道运维单位
	@Column(name = "TDWH_ORG")
	private String tdwhOrg;
	//字段描述: 稽查人部门（0属地 1北京）
	@Column(name = "CHECK_DEPT")
	private String checkDept;
	//字段描述: 稽查周期
	@Column(name = "CHECK_CYCLE")
	private String checkCycle;

	public void setId(Long id){
		this.id =   Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
	}
	@ExcelResources(title="",order=1)
	public Long getId(){
		return this.id;
	}

	public void setTaskId(Long taskId){
		this.taskId = taskId;
	}
	@ExcelResources(title="被稽查的任务id",order=2)
	public Long getTaskId(){
		return this.taskId;
	}

	public void setTaskType(String taskType){
		this.taskType = taskType;
	}
	@ExcelResources(title="稽查任务类别（0新增 1正常 2危机）",order=3)
	public String getTaskType(){
		return this.taskType;
	}

	public void setCheckType(String checkType){
		this.checkType = checkType;
	}
	@ExcelResources(title="稽查任务类型（0 看护  1巡视）",order=4)
	public String getCheckType(){
		return this.checkType;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	@ExcelResources(title="状态修改时间",order=5)
	public Date getUpdateTime(){
		return this.updateTime;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="稽查任务创建时间",order=6)
	public Date getCreateTime(){
		return this.createTime;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="稽查任务名称",order=7)
	public String getTaskName(){
		return this.taskName;
	}

	public void setUserId(Long userId){
		this.userId = userId;
	}
	@ExcelResources(title="稽查人id",order=8)
	public Long getUserId(){
		return this.userId;
	}

	public void setStatus(String status){
		this.status = status;
	}
	@ExcelResources(title="稽查任务状态（0 未消除 1 已消除）",order=9)
	public String getStatus(){
		return this.status;
	}

	public void setTdwhOrg(String tdwhOrg){
		this.tdwhOrg = tdwhOrg;
	}
	@ExcelResources(title="通道运维单位",order=10)
	public String getTdwhOrg(){
		return this.tdwhOrg;
	}

	public void setCheckDept(String checkDept){
		this.checkDept = checkDept;
	}
	@ExcelResources(title="稽查人部门（0属地 1北京）",order=11)
	public String getCheckDept(){
		return this.checkDept;
	}

	public void setCheckCycle(String checkCycle){
		this.checkCycle = checkCycle;
	}
	@ExcelResources(title="稽查周期",order=12)
	public String getCheckCycle(){
		return this.checkCycle;
	}

}