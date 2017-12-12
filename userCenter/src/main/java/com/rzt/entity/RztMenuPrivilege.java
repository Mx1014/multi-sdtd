/**    
 * 文件名：RztMenuPrivilege           
 * 版本信息：    
 * 日期：2017/10/12 10:30:09    
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
 * 类名称：RztMenuPrivilege    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 10:30:09 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 10:30:09    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name = "RZTMENUPRIVILEGE")
public class RztMenuPrivilege extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
   	 @Column(name = "roleid")
     private String roleid;
    	//字段描述: 
   	 @Column(name = "menuId")
     private String menuid;
    	//字段描述: 
   	 @Column(name = "operateId")
     private String operateid;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setRoleid(String roleid){
		this.roleid = roleid;
	}
	@ExcelResources(title="",order=2)
	public String getRoleid(){
		return this.roleid;
	}

	public void setMenuid(String menuid){
		this.menuid = menuid;
	}
	@ExcelResources(title="",order=3)
	public String getMenuid(){
		return this.menuid;
	}

	public void setOperateid(String operateid){
		this.operateid = operateid;
	}
	@ExcelResources(title="",order=4)
	public String getOperateid(){
		return this.operateid;
	}

}