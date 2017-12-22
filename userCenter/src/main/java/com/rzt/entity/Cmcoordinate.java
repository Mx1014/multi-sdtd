/**    
 * 文件名：Cmcoordinate
 * 版本信息：    
 * 日期：2017/12/20 15:22:15    
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
 * 类名称：Cmcoordinate
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/20 15:22:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/20 15:22:15    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CM_COORDINATE")
public class Cmcoordinate implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;        
    	//字段描述: 用户id
   	 @Column(name = "USERID")
     private String userid;
    	//字段描述: 经度
   	 @Column(name = "LONGITUDE")
     private float longitude;
    	//字段描述: 纬度
   	 @Column(name = "LATITUDE")
     private float latitude;
    	//字段描述: 创建日期
   	 @Column(name = "CREATETIME")
     private Date createtime;
    	//字段描述: 手机串号
   	 @Column(name = "IMEI")
     private String imei;
    
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
	
	

	public void setUserid(String userid){
		this.userid = userid;
	}
    public String getUserid(){
		return this.userid;
	}
	
	

	public void setLongitude(float longitude){
		this.longitude = longitude;
	}
    public float getLongitude(){
		return this.longitude;
	}
	
	

	public void setLatitude(float latitude){
		this.latitude = latitude;
	}
    public float getLatitude(){
		return this.latitude;
	}
	
	

	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
    public Date getCreatetime(){
		return this.createtime;
	}
	
	

	public void setImei(String imei){
		this.imei = imei;
	}
    public String getImei(){
		return this.imei;
	}
	
	

}
