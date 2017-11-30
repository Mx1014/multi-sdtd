/**    
 * 文件名：CMLINETOWER           
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
 * 类名称：CMLINETOWER    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
@Entity
public class CMLINETOWER extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 线路id
   	 @Column(name = "LINE_ID")
     private String lineId;
    	//字段描述: 杆塔id
   	 @Column(name = "TOWER_ID")
     private String towerId;
    	//字段描述: 杆塔名称
   	 @Column(name = "TOWER_NAME")
     private String towerName;
    	//字段描述: 线路名
   	 @Column(name = "LINE_NAME")
     private String lineName;
    	//字段描述: 顺序
   	 @Column(name = "SORT")
     private String sort;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setLineId(String lineId){
		this.lineId = lineId;
	}
	@ExcelResources(title="线路id",order=2)
	public String getLineId(){
		return this.lineId;
	}

	public void setTowerId(String towerId){
		this.towerId = towerId;
	}
	@ExcelResources(title="杆塔id",order=3)
	public String getTowerId(){
		return this.towerId;
	}

	public void setTowerName(String towerName){
		this.towerName = towerName;
	}
	@ExcelResources(title="杆塔名称",order=4)
	public String getTowerName(){
		return this.towerName;
	}

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	@ExcelResources(title="线路名",order=5)
	public String getLineName(){
		return this.lineName;
	}

	public void setSort(String sort){
		this.sort = sort;
	}
	@ExcelResources(title="顺序",order=6)
	public String getSort(){
		return this.sort;
	}

}