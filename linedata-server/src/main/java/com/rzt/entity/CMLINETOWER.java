/**    
 * 文件名：CMLINETOWER           
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
 * 类名称：CMLINETOWER    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/09 16:45:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/09 16:45:15    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CM_LINE_TOWER")
public class CMLINETOWER implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;        
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

	public void setId(Long id){
		if(id==null||id==0){
			this.id = SnowflakeIdWorker.getInstance(9,4).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setLineId(String lineId){
		this.lineId = lineId;
	}
    public String getLineId(){
		return this.lineId;
	}
	
	

	public void setTowerId(String towerId){
		this.towerId = towerId;
	}
    public String getTowerId(){
		return this.towerId;
	}
	
	

	public void setTowerName(String towerName){
		this.towerName = towerName;
	}
    public String getTowerName(){
		return this.towerName;
	}
	
	

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
    public String getLineName(){
		return this.lineName;
	}
	
	

	public void setSort(String sort){
		this.sort = sort;
	}
    public String getSort(){
		return this.sort;
	}
	
	

}
