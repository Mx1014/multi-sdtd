/**    
 * 文件名：Resource           
 * 版本信息：    
 * 日期：2017/01/23 15:23:54    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * 类名称：Resource    
 * 类描述：InnoDB free: 979968 kB    
 * 创建人：张虎成   
 * 创建时间：2017/01/23 15:23:54 
 * 修改人：张虎成    
 * 修改时间：2017/01/23 15:23:54    
 * 修改备注：    
 * @version        
 */
public class Resource implements Serializable {
	//字段描述: id
     private String id;
    	//字段描述: 资源名称
     private String resourcename;
    	//字段描述: 资源路径
     private String url;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	public String getId(){
		return this.id;
	}

	public void setResourcename(String resourcename){
		this.resourcename = resourcename;
	}
	public String getResourcename(){
		return this.resourcename;
	}

	public void setUrl(String url){
		this.url = url;
	}
	public String getUrl(){
		return this.url;
	}

}
