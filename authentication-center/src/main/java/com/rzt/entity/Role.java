/**    
 * 文件名：Role           
 * 版本信息：    
 * 日期：2017/01/23 15:23:54    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import java.io.Serializable;

/**
 * 类名称：Role    
 * 类描述：InnoDB free: 979968 kB    
 * 创建人：张虎成   
 * 创建时间：2017/01/23 15:23:54 
 * 修改人：张虎成    
 * 修改时间：2017/01/23 15:23:54    
 * 修改备注：    
 * @version        
 */
public class Role  implements Serializable{
	//字段描述: id
     private String id;
    	//字段描述: 角色名称
     private String rolename;
    
	public void setId(String id){
		this.id =id;
	}
	public String getId(){
		return this.id;
	}

	public void setRolename(String rolename){
		this.rolename = rolename;
	}
	public String getRolename(){
		return this.rolename;
	}

}
