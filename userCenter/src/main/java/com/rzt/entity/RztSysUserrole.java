/**    
 * 文件名：RztSysUserrole           
 * 版本信息：    
 * 日期：2017/10/12 10:44:37    
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
 * 类名称：RztSysUserrole    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 10:44:37 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 10:44:37    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name = "RZTSYSUSERROLE")
public class RztSysUserrole extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
   	 @Column(name = "userid")
     private String userid;
    	//字段描述: 
   	 @Column(name = "roleid")
     private String roleid;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setUserid(String userid){
		this.userid = userid;
	}
	@ExcelResources(title="",order=2)
	public String getUserid(){
		return this.userid;
	}

	public void setRoleid(String roleid){
		this.roleid = roleid;
	}
	@ExcelResources(title="",order=3)
	public String getRoleid(){
		return this.roleid;
	}

}