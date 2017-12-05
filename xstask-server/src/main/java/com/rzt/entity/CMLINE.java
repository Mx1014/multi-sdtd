/**    
 * 文件名：CMLINE           
 * 版本信息：    
 * 日期：2017/11/28 18:05:13    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * 类名称：CMLINE    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name = "CM_LINE")
public class CMLINE extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
   	 @Column(name = "LINE_NAME")
     private String lineName;
    	//字段描述: 0 35kV 1 110kV 2 220kV 3 500kV
   	 @Column(name = "V_LEVEL")
     private Integer vLevel;
    	//字段描述: 线路级别 0 平原 1 山地
   	 @Column(name = "LINE_JB")
     private Integer lineJb;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	@ExcelResources(title="",order=2)
	public String getLineName(){
		return this.lineName;
	}

	public void setVLevel(Integer vLevel){
		this.vLevel = vLevel;
	}
	@ExcelResources(title="0 35kV 1 110kV 2 220kV 3 500kV",order=3)
	public Integer getVLevel(){
		return this.vLevel;
	}

	public void setLineJb(Integer lineJb){
		this.lineJb = lineJb;
	}
	@ExcelResources(title="线路级别 0 平原 1 山地",order=4)
	public Integer getLineJb(){
		return this.lineJb;
	}

}