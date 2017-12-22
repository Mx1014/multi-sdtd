/**    
 * 文件名：CheckLiveTask
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

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
public class CheckLiveTask  implements Serializable{
	//字段描述:
	@Id
	private Long id;
	//字段描述: 被稽查的任务id
	@Column(name = "TASK_ID")
	private Long taskId;
	//字段描述: 稽查任务类别（0新增 1正常 2危机）
	@Column(name = "TASK_TYPE")
	private Integer taskType;
	//字段描述: 稽查任务类型（0 看护  1巡视）
	@Column(name = "CHECK_TYPE")
	private Integer checkType;
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
	private Integer status;
	//字段描述: 通道运维单位
	@Column(name = "TDWH_ORG")
	private String tdwhOrg;
	//字段描述: 稽查人部门（0属地 1北京）
	@Column(name = "CHECK_DEPT")
	private Integer checkDept;
	//字段描述: 稽查周期
	@Column(name = "CHECK_CYCLE")
	private Integer checkCycle;
	//字段描述: 通道外协单位id
	@Column(name = "TDWX_ORGID")
	private String tdwxOrgid;
	//字段描述: 隐患id
	@Column(name = "YH_ID")
	private Long yhId;
	//字段描述: 是否设置了电子围栏（0是 1否）
	@Column(name = "DZWL")
	private Integer dzwl;
	//字段描述: 周期id
	@Column(name = "CYCLE_ID")
	private Long cycleId;

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

	public void setTaskType(Integer taskType){
		this.taskType = taskType;
	}
	@ExcelResources(title="稽查任务类别（0新增 1正常 2危机）",order=3)
	public Integer getTaskType(){
		return this.taskType;
	}

	public void setCheckType(Integer checkType){
		this.checkType = checkType;
	}
	@ExcelResources(title="稽查任务类型（0 看护  1巡视）",order=4)
	public Integer getCheckType(){
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

	public void setStatus(Integer status){
		this.status = status;
	}
	@ExcelResources(title="任务派发状态  0未派发  1已派发  2已消缺",order=9)
	public Integer getStatus(){
		return this.status;
	}

	public void setTdwhOrg(String tdwhOrg){
		this.tdwhOrg = tdwhOrg;
	}
	@ExcelResources(title="通道运维单位",order=10)
	public String getTdwhOrg(){
		return this.tdwhOrg;
	}

	public void setCheckDept(Integer checkDept){
		this.checkDept = checkDept;
	}
	@ExcelResources(title="稽查人部门（0属地 1北京）",order=11)
	public Integer getCheckDept(){
		return this.checkDept;
	}

	public void setCheckCycle(Integer checkCycle){
		this.checkCycle = checkCycle;
	}
	@ExcelResources(title="稽查周期",order=12)
	public Integer getCheckCycle(){
		return this.checkCycle;
	}

	public void setTdwxOrgid(String tdwxOrgid){
		this.tdwxOrgid = tdwxOrgid;
	}
	@ExcelResources(title="通道外协单位id",order=13)
	public String getTdwxOrgid(){
		return this.tdwxOrgid;
	}

	public void setYhId(Long yhId){
		this.yhId = yhId;
	}
	@ExcelResources(title="隐患id",order=14)
	public Long getYhId(){
		return this.yhId;
	}

	public void setDzwl(Integer dzwl){
		this.dzwl = dzwl;
	}
	@ExcelResources(title="是否设置了电子围栏（0是 1否）",order=15)
	public Integer getDzwl(){
		return this.dzwl;
	}

	public void setCycleId(Long cycleId){
		this.cycleId = cycleId;
	}
	@ExcelResources(title="周期id",order=16)
	public Long getCycleId(){
		return this.cycleId;
	}

}