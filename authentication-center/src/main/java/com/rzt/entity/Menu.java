/**    
 * 文件名：Menu           
 * 版本信息：    
 * 日期：2017/01/23 15:23:53    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import java.io.Serializable;
import java.util.UUID;


public class Menu  implements Serializable {
	//字段描述: id

     private String id;        

     private String menuname;
    	//字段描述: 父菜单Id
     private String parentid;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	public String getId(){
		return this.id;
	}

	public void setMenuname(String menuname){
		this.menuname = menuname;
	}
	public String getMenuname(){
		return this.menuname;
	}

	public void setParentid(String parentid){
		this.parentid = parentid;
	}
	public String getParentid(){
		return this.parentid;
	}

}
