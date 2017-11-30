/**    
 * 文件名：CMTOWER           
 * 版本信息：    
 * 日期：2017/11/28 18:05:13    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * 类名称：CMTOWER    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
@Entity
public class CMTOWER extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
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
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setName(String name){
		this.name = name;
	}
	@ExcelResources(title="",order=2)
	public String getName(){
		return this.name;
	}

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}
	@ExcelResources(title="经度",order=3)
	public String getLongitude(){
		return this.longitude;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}
	@ExcelResources(title="维度",order=4)
	public String getLatitude(){
		return this.latitude;
	}

	public void setIsTtbj(String isTtbj){
		this.isTtbj = isTtbj;
	}
	@ExcelResources(title="是否同塔并线",order=5)
	public String getIsTtbj(){
		return this.isTtbj;
	}

	public void setInUse(String inUse){
		this.inUse = inUse;
	}
	@ExcelResources(title="是否在运行",order=6)
	public String getInUse(){
		return this.inUse;
	}

}