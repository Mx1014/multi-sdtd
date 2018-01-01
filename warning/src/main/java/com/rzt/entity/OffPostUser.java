/**    
 * 文件名：WarningOffPostUser           
 * 版本信息：    
 * 日期：2017/12/27 03:58:05    
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
 * 类名称：WarningOffPostUser    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 03:58:05 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 03:58:05    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="WARNING_OFF_POST_USER")
public class OffPostUser implements Serializable{
	@Id
	private Long id;
	@Column(name="USER_ID")
	private String userId;  //脱岗人员id
	@Column(name="TASK_ID")
	private Long taskId;  //任务id
	@Column(name="CREATE_TIME")
	private Date createTime; //创建时间
	@Column(name="STATUS")
	private int status; //0表示已回岗   1表示脱岗中

	public Long getId() {
		return id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public void setId(Long id) {
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(0,0).nextId();
		}else{
			this.id = id;
		}
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


}