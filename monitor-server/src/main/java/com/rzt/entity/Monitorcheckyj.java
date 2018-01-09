/**    
 * 文件名：MONITORCHECKYJ           
 * 版本信息：    
 * 日期：2018/01/08 11:06:23    
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
     private String taskId;
    	//字段描述: 处理中创建时间
   	 @Column(name = "CREATE_TIME_Z")
     private Date createTimeZ;
    	//字段描述: 已处理创建时间
   	 @Column(name = "CREATE_TIME_C")
     private Date createTimeC;
    	//字段描述: 任务类型  1 巡视  2 看护  3 稽查
   	 @Column(name = "TASK_TYPE")
     private String taskType;
    	//字段描述: 处理状态  0未处理  1处理中  2已处理
   	 @Column(name = "STATUS")
     private String status;
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
     private String isView;
    	//字段描述: 告警任务创建时间
   	 @Column(name = "CREATE_TIME")
     private Date createTime;
    
	public void setId(Long id){
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(0,0).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setTaskId(String taskId){
		this.taskId = taskId;
	}
    public String getTaskId(){
		return this.taskId;
	}
	
	

	public void setCreateTimeZ(Date createTimeZ){
		this.createTimeZ = createTimeZ;
	}
    public Date getCreateTimeZ(){
		return this.createTimeZ;
	}
	
	

	public void setCreateTimeC(Date createTimeC){
		this.createTimeC = createTimeC;
	}
    public Date getCreateTimeC(){
		return this.createTimeC;
	}
	
	

	public void setTaskType(String taskType){
		this.taskType = taskType;
	}
    public String getTaskType(){
		return this.taskType;
	}
	
	

	public void setStatus(String status){
		this.status = status;
	}
    public String getStatus(){
		return this.status;
	}
	
	

	public void setCheckzInfo(String checkzInfo){
		this.checkzInfo = checkzInfo;
	}
    public String getCheckzInfo(){
		return this.checkzInfo;
	}
	
	

	public void setCheckzAppInfo(String checkzAppInfo){
		this.checkzAppInfo = checkzAppInfo;
	}
    public String getCheckzAppInfo(){
		return this.checkzAppInfo;
	}
	
	

	public void setCheckcInfo(String checkcInfo){
		this.checkcInfo = checkcInfo;
	}
    public String getCheckcInfo(){
		return this.checkcInfo;
	}
	
	

	public void setIsView(String isView){
		this.isView = isView;
	}
    public String getIsView(){
		return this.isView;
	}
	
	

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
    public Date getCreateTime(){
		return this.createTime;
	}
	
	

}
