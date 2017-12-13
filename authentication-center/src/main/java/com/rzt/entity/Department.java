/**    
 * 文件名：Department           
 * 版本信息：    
 * 日期：2017/01/23 15:23:53    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import java.io.Serializable;
import java.util.UUID;

/**      
 * 类名称：Department    
 * 类描述：InnoDB free: 979968 kB    
 * 创建人：张虎成   
 * 创建时间：2017/01/23 15:23:53 
 * 修改人：张虎成    
 * 修改时间：2017/01/23 15:23:53    
 * 修改备注：    
 * @version        
 */
public class Department  implements Serializable {
	//字段描述: id
     private String id;
    	//字段描述: 部门名称
     private String departmentname;
    	//字段描述: 父部门Id
     private String parentid;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	public String getId(){
		return this.id;
	}

	public void setDepartmentname(String departmentname){
		this.departmentname = departmentname;
	}
	public String getDepartmentname(){
		return this.departmentname;
	}

	public void setParentid(String parentid){
		this.parentid = parentid;
	}
	public String getParentid(){
		return this.parentid;
	}

	public   String getComment(){
		return "部门";
	}

}
