/**    
 * 文件名：CHECKLIVETASKEXEC           
 * 版本信息：    
 * 日期：2017/12/13 17:32:33    
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
 * 类名称：CHECKLIVETASKEXEC    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/13 17:32:33 
 * 修改人：张虎成    
 * 修改时间：2017/12/13 17:32:33    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CHECK_LIVE_TASK_EXEC")
public class CheckLiveTaskExec  implements Serializable{
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
     private Long count;
    	//字段描述: 任务状态（0未开始 1进行中 2已完成 3已超期）
   	 @Column(name = "STATUS")
     private String status;
    	//字段描述: 派发时间
   	 @Column(name = "CREATE_TIME")
     private Date createTime;
    	//字段描述: 稽查人id
   	 @Column(name = "USER_ID")
     private String userId;

	//字段描述: 稽查任务状态
	@Column(name = "TASK_STATUS")
	private Long taskStatus;

	//字段描述: 稽查任务状态
	@Column(name = "TDWH_ORG")
	private String tdwhOrg;

	public String getTdwhOrg() {
		return tdwhOrg;
	}

	public void setTdwhOrg(String tdwhOrg) {
		this.tdwhOrg = tdwhOrg;
	}

	public Long getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(Long taskStatus) {
		this.taskStatus = taskStatus;
	}

	public void setId(){

		this.id =   Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
	}
	@ExcelResources(title="",order=1)
	public Long getId(){
		return this.id;
	}

	public void setCycleId(Long cycleId){
		this.cycleId = cycleId;
	}
	@ExcelResources(title="周期id",order=2)
	public Long getCycleId(){
		return this.cycleId;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="稽查任务名称",order=3)
	public String getTaskName(){
		return this.taskName;
	}

	public void setCount(Long count){
		this.count = count;
	}
	@ExcelResources(title="周期内第多少次任务",order=4)
	public Long getCount(){
		return this.count;
	}

	public void setStatus(String status){
		this.status = status;
	}
	@ExcelResources(title="任务状态（0未开始 1进行中 2已完成 3已超期）",order=5)
	public String getStatus(){
		return this.status;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="派发时间",order=6)
	public Date getCreateTime(){
		return this.createTime;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}
	@ExcelResources(title="稽查人id",order=7)
	public String getUserId(){
		return this.userId;
	}

}