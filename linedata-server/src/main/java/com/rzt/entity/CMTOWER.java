/**    
 * 文件名：CMTOWER           
 * 版本信息：    
 * 日期：2017/12/09 16:45:15    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**      
 * 类名称：CMTOWER    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/09 16:45:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/09 16:45:15    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CM_TOWER")
public class CMTOWER implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;        
    	//字段描述: 同塔并架，有多个名字
   	 @Column(name = "NAME")
     private String name;
    	//字段描述: 经度
   	 @Column(name = "LONGITUDE")
     private String longitude;
    	//字段描述: 维度
   	 @Column(name = "LATITUDE")
     private String latitude;
    	//字段描述: 是否同塔并线
   	 @Column(name = "IS_TTBJ")
     private String isTtbj;
    	//字段描述: 是否在运行
   	 @Column(name = "IN_USE")
     private String inUse;
    	//字段描述: 导数据临时使用，其他人务动
   	 @Column(name = "LINE_ID")
     private String lineId;

	public void setId(Long id){
		if(id==null||id==0){
			this.id = SnowflakeIdWorker.getInstance(9,6).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setName(String name){
		this.name = name;
	}
    public String getName(){
		return this.name;
	}
	
	

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}
    public String getLongitude(){
		return this.longitude;
	}
	
	

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}
    public String getLatitude(){
		return this.latitude;
	}
	
	

	public void setIsTtbj(String isTtbj){
		this.isTtbj = isTtbj;
	}
    public String getIsTtbj(){
		return this.isTtbj;
	}
	
	

	public void setInUse(String inUse){
		this.inUse = inUse;
	}
    public String getInUse(){
		return this.inUse;
	}
	
	

	public void setLineId(String lineId){
		this.lineId = lineId;
	}
    public String getLineId(){
		return this.lineId;
	}
	
	

}
