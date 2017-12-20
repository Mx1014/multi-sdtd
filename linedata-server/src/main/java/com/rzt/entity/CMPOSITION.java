/**    
 * 文件名：CMPOSITION           
 * 版本信息：    
 * 日期：2017/12/17 17:15:06    
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
 * 类名称：CMPOSITION    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/17 17:15:06 
 * 修改人：张虎成    
 * 修改时间：2017/12/17 17:15:06    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CM_POSITION")
public class CMPOSITION implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;        
    	//字段描述: 经度
   	 @Column(name = "LON")
     private String lon;
    	//字段描述: 纬度
   	 @Column(name = "LAT")
     private String lat;
    	//字段描述: 
   	 @Column(name = "CREATE_TIME")
     private Date createTime;
    	//字段描述: 
   	 @Column(name = "FK_ID")
     private String fkId;
    	//字段描述: 表标识
   	 @Column(name = "TABLE_TYPE")
     private String tableType;
    
	public void setId(Long id){
		if(id==null||id==0){
			this.id = new SnowflakeIdWorker(1,2).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setLon(String lon){
		this.lon = lon;
	}
    public String getLon(){
		return this.lon;
	}
	
	

	public void setLat(String lat){
		this.lat = lat;
	}
    public String getLat(){
		return this.lat;
	}
	
	

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
    public Date getCreateTime(){
		return this.createTime;
	}
	
	

	public void setFkId(String fkId){
		this.fkId = fkId;
	}
    public String getFkId(){
		return this.fkId;
	}
	
	

	public void setTableType(String tableType){
		this.tableType = tableType;
	}
    public String getTableType(){
		return this.tableType;
	}
	
	

}
