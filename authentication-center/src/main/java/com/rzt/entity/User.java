/**    
 * 文件名：User           
 * 版本信息：    
 * 日期：2017/01/23 15:23:54    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * 类名称：User    
 * 类描述：InnoDB free: 979968 kB    
 * 创建人：张虎成   
 * 创建时间：2017/01/23 15:23:54 
 * 修改人：张虎成    
 * 修改时间：2017/01/23 15:23:54    
 * 修改备注：    
 * @version        
 */
public class User  implements Serializable {
	//字段描述: id
     private String id;
    	//字段描述: 用户名
     private String username;
    	//字段描述: 密码
     private String password;
    	//字段描述: 
     private String realname;
    	//字段描述: 邮箱
     private String email;
    	//字段描述: 手机号码
     private String mobilenum;
    	//字段描述: im号码
     private int imnum;
    	//字段描述: 登录时间
     private String logintime;
    	//字段描述: 登录IP地址
     private String loginip;
    	//字段描述: 个人头像
     private String icon;
    	//字段描述: 地址
     private String address;
    	//字段描述: 登录类型 pc或者mobile
     private String logintype;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	public String getId(){
		return this.id;
	}

	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return this.username;
	}

	public void setPassword(String password){
		this.password = password;
	}
	public String getPassword(){
		return this.password;
	}

	public void setRealname(String realname){
		this.realname = realname;
	}
	public String getRealname(){
		return this.realname;
	}

	public void setEmail(String email){
		this.email = email;
	}
	public String getEmail(){
		return this.email;
	}

	public void setMobilenum(String mobilenum){
		this.mobilenum = mobilenum;
	}
	public String getMobilenum(){
		return this.mobilenum;
	}

	public void setImnum(int imnum){
		this.imnum = imnum;
	}
	public int getImnum(){
		return this.imnum;
	}

	public void setLogintime(String logintime){
		this.logintime = logintime;
	}
	public String getLogintime(){
		return this.logintime;
	}

	public void setLoginip(String loginip){
		this.loginip = loginip;
	}
	public String getLoginip(){
		return this.loginip;
	}

	public void setIcon(String icon){
		this.icon = icon;
	}
	public String getIcon(){
		return this.icon;
	}

	public void setAddress(String address){
		this.address = address;
	}
	public String getAddress(){
		return this.address;
	}

	public void setLogintype(String logintype){
		this.logintype = logintype;
	}
	public String getLogintype(){
		return this.logintype;
	}

}
