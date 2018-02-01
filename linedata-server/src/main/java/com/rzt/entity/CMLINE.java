/**    
 * 文件名：CMLINE           
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
 * 类名称：CMLINE    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/09 16:45:15 
 * 修改人：张虎成    
 * 修改时间：2017/12/09 16:45:15    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CM_LINE")
public class CMLINE implements Serializable{
	//字段描述: 
   	 @Id
	 //@Column(name="id",columnDefinition="NUMBER")
     private Long id;        
    	//字段描述: 
   	 @Column(name = "LINE_NAME")
     private String lineName;
    	//字段描述: 35kV 110kV 220kV 500kV
   	 @Column(name = "V_LEVEL")
     private String vLevel;
    	//字段描述: 线路级别  平原 山地
   	 @Column(name = "LINE_JB")
     private String lineJb;
    	//字段描述: 
   	 @Column(name = "SECTION")
     private String section;
    	//字段描述: 是否删除0未删除 1已删除
   	 @Column(name = "IS_DEL")
     private Integer isDel;

	public void setId(Long id){
		if(id==null||id==0){
			this.id = SnowflakeIdWorker.getInstance(9,2).nextId();
		}else{
			this.id = id;
		}
	}
    public Long getId(){
		return this.id;
	}
	
	

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
    public String getLineName(){
		return this.lineName;
	}
	
	

	public void setVLevel(String vLevel){
		this.vLevel = vLevel;
	}
    public String getVLevel(){
		return this.vLevel;
	}
	
	

	public void setLineJb(String lineJb){
		this.lineJb = lineJb;
	}
    public String getLineJb(){
		return this.lineJb;
	}
	
	

	public void setSection(String section){
		this.section = section;
	}
    public String getSection(){
		return this.section;
	}
	
	

	public void setIsDel(Integer isDel){
		this.isDel = isDel;
	}
    public Integer getIsDel(){
		return this.isDel;
	}
	
	

}
