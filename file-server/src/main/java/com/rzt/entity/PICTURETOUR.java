/**    
 * 文件名：PICTURETOUR           
 * 版本信息：    
 * 日期：2017/11/29 09:35:42    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * 类名称：PICTURETOUR    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/29 09:35:42 
 * 修改人：张虎成    
 * 修改时间：2017/11/29 09:35:42    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name = "picture_tour")
public class PICTURETOUR extends BaseEntity implements Serializable{
	//字段描述: 步骤名字
   	 @Column(name = "PROCESS_NAME")
     private String processName;
    	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 文件名称
   	 @Column(name = "FILE_NAME")
     private String fileName;
    	//字段描述: 文件路径
   	 @Column(name = "FILE_PATH")
     private String filePath;
    	//字段描述: 图片缩略图路径
   	 @Column(name = "FILE_SMALL_PATH")
     private String fileSmallPath;
    	//字段描述: 上传时间
   	 @Column(name = "CREATE_TIME")
     private Date createTime;
    	//字段描述: 流程步骤id
   	 @Column(name = "PROCESS_ID")
     private String processId;
    	//字段描述: 经度
   	 @Column(name = "LON")
     private String lon;
    	//字段描述: 纬度
   	 @Column(name = "LAT")
     private String lat;
    	//字段描述: 文件类型1图片2录音3摄像
   	 @Column(name = "FILE_TYPE")
     private String fileType;
    	//字段描述: 上传人id
   	 @Column(name = "USER_ID")
     private String userId;
    	//字段描述: 任务id
   	 @Column(name = "TASK_ID")
     private String taskId;
    
	public void setProcessName(String processName){
		this.processName = processName;
	}
	@ExcelResources(title="步骤名字",order=1)
	public String getProcessName(){
		return this.processName;
	}

	public void setId(String id){
		if(id==null||"".equals(id.trim())){
			this.id = String.valueOf(new SnowflakeIdWorker(0,0).nextId());
		}else{
			this.id = id;
		}
		//this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=2)
	public String getId(){
		return this.id;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	@ExcelResources(title="文件名称",order=3)
	public String getFileName(){
		return this.fileName;
	}

	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	@ExcelResources(title="文件路径",order=4)
	public String getFilePath(){
		return this.filePath;
	}

	public void setFileSmallPath(String fileSmallPath){
		this.fileSmallPath = fileSmallPath;
	}
	@ExcelResources(title="图片缩略图路径",order=5)
	public String getFileSmallPath(){
		return this.fileSmallPath;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="上传时间",order=6)
	public Date getCreateTime(){
		return this.createTime;
	}

	public void setProcessId(String processId){
		this.processId = processId;
	}
	@ExcelResources(title="流程步骤id",order=7)
	public String getProcessId(){
		return this.processId;
	}

	public void setLon(String lon){
		this.lon = lon;
	}
	@ExcelResources(title="经度",order=8)
	public String getLon(){
		return this.lon;
	}

	public void setLat(String lat){
		this.lat = lat;
	}
	@ExcelResources(title="纬度",order=9)
	public String getLat(){
		return this.lat;
	}

	public void setFileType(String fileType){
		this.fileType = fileType;
	}
	@ExcelResources(title="文件类型",order=10)
	public String getFileType(){
		return this.fileType;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}
	@ExcelResources(title="上传人id",order=11)
	public String getUserId(){
		return this.userId;
	}

	public void setTaskId(String taskId){
		this.taskId = taskId;
	}
	@ExcelResources(title="任务id",order=12)
	public String getTaskId(){
		return this.taskId;
	}

}