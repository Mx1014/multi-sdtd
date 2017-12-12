/**    
 * 文件名：RztSysOperate           
 * 版本信息：    
 * 日期：2017/10/12 10:25:31    
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
 * 类名称：RztSysOperate    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 10:25:31 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 10:25:31    
 * 修改备注：    
 * @version        
 */
@Entity
public class RztSysOperate extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
   	 @Column(name = "operatenum")
     private String operatenum;
    	//字段描述: 
   	 @Column(name = "operatename")
     private String operatename;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setOperatenum(String operatenum){
		this.operatenum = operatenum;
	}
	@ExcelResources(title="",order=2)
	public String getOperatenum(){
		return this.operatenum;
	}

	public void setOperatename(String operatename){
		this.operatename = operatename;
	}
	@ExcelResources(title="",order=3)
	public String getOperatename(){
		return this.operatename;
	}

}