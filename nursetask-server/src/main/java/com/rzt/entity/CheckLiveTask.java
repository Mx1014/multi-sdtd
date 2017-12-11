/**    
 * 文件名：CheckLiveTask
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
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
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;
	//字段描述: 被稽查的任务id
	@Column(name = "TASK_ID")
	private Long taskId;
	//字段描述: 稽查任务类别（0新增 1正常 2危机）
	@Column(name = "TASK_TYPE")
	private String taskType;
	//字段描述: 稽查任务类型（0 看护  1巡视）
	@Column(name = "CHECK_TYPE")
	private String checkType;
	//字段描述: 任务消缺时间
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
	private String userId;
	//字段描述: 任务派发状态  0未派发  1已派发  2已消缺
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
	//字段描述: 通道外协单位id
	@Column(name = "TDWX_ORGID")
	private String tdwxOrgid;
	//字段描述: 隐患id
	@Column(name = "YH_ID")
	private String yhId;
	//字段描述: 任务状态  0 待稽查 1进行中 2已稽查 3已过期
	@Column(name = "task_STATUS")
	private String taskStatus;
	//字段描述: 计划开始时间
	@Column(name = "PLAN_START_TIME")
	private Date planStartTime;
	//字段描述: 计划结束时间
	@Column(name = "PLAN_END_TIME")
	private Date planEndTime;
	//字段描述: 实际开始时间
	@Column(name = "REAL_START_TIME")
	private Date realStartTime;
	//字段描述: 实际结束时间
	@Column(name = "REAL_END_TIME")
	private Date realEndTime;
	//字段描述: 到达现场时间
	@Column(name = "DDXC_TIME")
	private Date ddxcTime;
	//字段描述: 周期id
	@Column(name = "CYCLE_ID")
	private String cycleId;

	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
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
	@ExcelResources(title="任务消缺时间",order=5)
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

	public void setUserId(String userId){
		this.userId = userId;
	}
	@ExcelResources(title="稽查人id",order=8)
	public String getUserId(){
		return this.userId;
	}

	public void setStatus(String status){
		this.status = status;
	}
	@ExcelResources(title="任务派发状态  0未派发  1已派发  2已消缺",order=9)
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

	public void setTdwxOrgid(String tdwxOrgid){
		this.tdwxOrgid = tdwxOrgid;
	}
	@ExcelResources(title="通道外协单位id",order=13)
	public String getTdwxOrgid(){
		return this.tdwxOrgid;
	}

	public void setYhId(String yhId){
		this.yhId = yhId;
	}
	@ExcelResources(title="隐患id",order=14)
	public String getYhId(){
		return this.yhId;
	}

	public void setTaskStatus(String taskStatus){
		this.taskStatus = taskStatus;
	}
	@ExcelResources(title="任务状态  0 待稽查 1进行中 2已稽查 3已过期",order=15)
	public String getTaskStatus(){
		return this.taskStatus;
	}

	public void setPlanStartTime(Date planStartTime){
		this.planStartTime = planStartTime;
	}
	@ExcelResources(title="计划开始时间",order=16)
	public Date getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(Date planEndTime){
		this.planEndTime = planEndTime;
	}
	@ExcelResources(title="计划结束时间",order=17)
	public Date getPlanEndTime(){
		return this.planEndTime;
	}

	public void setRealStartTime(Date realStartTime){
		this.realStartTime = realStartTime;
	}
	@ExcelResources(title="实际开始时间",order=18)
	public Date getRealStartTime(){
		return this.realStartTime;
	}

	public void setRealEndTime(Date realEndTime){
		this.realEndTime = realEndTime;
	}
	@ExcelResources(title="实际结束时间",order=19)
	public Date getRealEndTime(){
		return this.realEndTime;
	}

	public void setDdxcTime(Date ddxcTime){
		this.ddxcTime = ddxcTime;
	}
	@ExcelResources(title="到达现场时间",order=20)
	public Date getDdxcTime(){
		return this.ddxcTime;
	}

	public void setCycleId(String cycleId){
		this.cycleId = cycleId;
	}
	@ExcelResources(title="周期id",order=21)
	public String getCycleId(){
		return this.cycleId;
	}

}