/**    
 * 文件名：WarningOffPostUserTime           
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
 * 类名称：WarningOffPostUserTime    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 03:58:05 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 03:58:05    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="WARNING_OFF_POST_USER_TIME")
public class OffPostUserTime implements Serializable{
	@Id
	private Long id;
	@Column(name="FK_USER_ID")
	private String FkUserId;  //关联OffPostUser中的id键
	@Column(name="START_TIME")
	private Date startTime;  //脱岗时间
	@Column(name="END_TIME")
	private Date endTime;  //回岗时间
	@Column(name = "FK_TASK_ID")
	private Long FkTaskId; //任务id

	public Long getFkTaskId() {
		return FkTaskId;
	}

	public void setFkTaskId(Long fkTaskId) {
		FkTaskId = fkTaskId;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(0,0).nextId();
		}else{
			this.id = id;
		}
	}
	public String getFkUserId() {
		return FkUserId;
	}
	public void setFkUserId(String fkUserId) {
		FkUserId = fkUserId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}


}