/**    
 * 文件名：CheckLiveTask
 * 版本信息：    
 * 日期：2017/12/04 15:13:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
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
public class CheckLiveTask extends BaseEntity implements Serializable{
	//字段描述: 稽查任务类型(0 看护  1 巡视）
	@Column(name = "CHECK_TYPE")
	private String checkType;
	//字段描述: 被稽查的任务id
	@Column(name = "TASK_ID")
	private String taskId;
	//字段描述: 稽查任务类别（0 新增 1 周期）
	@Column(name = "TASK_TYPE")
	private String taskType;
	//字段描述: 状态修改时间
	@Column(name = "UPDATE_TIME")
	private Date updateTime;
	//字段描述: 通道运维单位
	@Column(name = "TDWX_ORG")
	private String tdwxOrg;
	//字段描述:
	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid")
	private String id;
	//字段描述: 稽查人id
	@Column(name = "USER_ID")
	private String userId;
	//字段描述: 稽查任务名称
	@Column(name = "TASK_NAME")
	private String taskName;
	//字段描述: 稽查任务创建时间
	@Column(name = "CREATE_TIME")
	private Date createTime;
	//字段描述: 稽查任务状态（0 未消除 1 已消除）
	@Column(name = "STATUS")
	private String status;

	public void setCheckType(String checkType){
		this.checkType = checkType;
	}
	@ExcelResources(title="稽查任务类型(0 看护  1 巡视）",order=1)
	public String getCheckType(){
		return this.checkType;
	}

	public void setTaskId(String taskId){
		this.taskId = taskId;
	}
	@ExcelResources(title="被稽查的任务id",order=2)
	public String getTaskId(){
		return this.taskId;
	}

	public void setTaskType(String taskType){
		this.taskType = taskType;
	}
	@ExcelResources(title="稽查任务类别（0 新增 1 周期）",order=3)
	public String getTaskType(){
		return this.taskType;
	}

	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	@ExcelResources(title="状态修改时间",order=4)
	public Date getUpdateTime(){
		return this.updateTime;
	}

	public void setTdwxOrg(String tdwxOrg){
		this.tdwxOrg = tdwxOrg;
	}
	@ExcelResources(title="通道运维单位",order=5)
	public String getTdwxOrg(){
		return this.tdwxOrg;
	}

	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=6)
	public String getId(){
		return this.id;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}
	@ExcelResources(title="稽查人id",order=7)
	public String getUserId(){
		return this.userId;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="稽查任务名称",order=8)
	public String getTaskName(){
		return this.taskName;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="稽查任务创建时间",order=9)
	public Date getCreateTime(){
		return this.createTime;
	}

	public void setStatus(String status){
		this.status = status;
	}
	@ExcelResources(title="稽查任务状态（0 未消除 1 已消除）",order=10)
	public String getStatus(){
		return this.status;
	}

}