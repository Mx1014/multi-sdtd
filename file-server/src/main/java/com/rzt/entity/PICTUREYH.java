/**    
 * 文件名：PICTUREYH           
 * 版本信息：    
 * 日期：2017/12/19 15:31:04    
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
 * 类名称：PICTUREYH    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/19 15:31:04 
 * 修改人：张虎成    
 * 修改时间：2017/12/19 15:31:04    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="PICTURE_YH")
public class PICTUREYH implements Serializable{
	//字段描述: 流程步骤名称
	@Column(name = "PROCESS_NAME")
	private String processName;
	//字段描述:
	@Column(name = "TASK_ID")
	private Long taskId;
	//字段描述:
	@Column(name = "USER_ID")
	private String userId;
	//字段描述: 纬度
	@Column(name = "LAT")
	private String lat;
	//字段描述: 经度
	@Column(name = "LON")
	private String lon;
	//字段描述: 流程步骤id
	@Column(name = "PROCESS_ID")
	private Long processId;
	//字段描述:
	@Id
	private Long id;
	//字段描述:
	@Column(name = "CREATE_TIME")
	private Date createTime;
	//字段描述:
	@Column(name = "FILE_NAME")
	private String fileName;
	//字段描述:
	@Column(name = "FILE_PATH")
	private String filePath;
	//字段描述:
	@Column(name = "FILE_SMALL_PATH")
	private String fileSmallPath;
	//字段描述: 文件类型1图片2录音3摄像
	@Column(name = "FILE_TYPE")
	private String fileType;

	public void setProcessName(String processName){
		this.processName = processName;
	}
	public String getProcessName(){
		return this.processName;
	}



	public void setTaskId(Long taskId){
		this.taskId = taskId;
	}
	public Long getTaskId(){
		return this.taskId;
	}



	public void setUserId(String userId){
		this.userId = userId;
	}
	public String getUserId(){
		return this.userId;
	}



	public void setLat(String lat){
		this.lat = lat;
	}
	public String getLat(){
		return this.lat;
	}



	public void setLon(String lon){
		this.lon = lon;
	}
	public String getLon(){
		return this.lon;
	}



	public void setProcessId(Long processId){
		this.processId = processId;
	}
	public Long getProcessId(){
		return this.processId;
	}



	public void setId(Long id){
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(5,4).nextId();
		}else{
			this.id = id;
		}
	}
	public Long getId(){
		return this.id;
	}



	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	public Date getCreateTime(){
		return this.createTime;
	}



	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public String getFileName(){
		return this.fileName;
	}



	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	public String getFilePath(){
		return this.filePath;
	}



	public void setFileSmallPath(String fileSmallPath){
		this.fileSmallPath = fileSmallPath;
	}
	public String getFileSmallPath(){
		return this.fileSmallPath;
	}



	public void setFileType(String fileType){
		this.fileType = fileType;
	}
	public String getFileType(){
		return this.fileType;
	}



}
