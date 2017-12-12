/**    
 * 文件名：RztSysUserauth           
 * 版本信息：    
 * 日期：2017/10/10 17:28:27    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：RztSysUserauth    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 17:28:27 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 17:28:27    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name = "RZTSYSUSERAUTH")
public class RztSysUserauth extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
   	 @Column(name = "userid")
     private String userid;
    	//字段描述: 登录标识 0 用户名 1 手机号 2 邮箱 3 QQ 4 微信 5 微博
   	 @Column(name = "identifier")
     private int identifier;
    	//字段描述: 登录类型：手机号 邮箱 用户名 微信 微博
   	 @Column(name = "identitytype")
     private String identitytype;
    	//字段描述: 密码或第三方凭证
   	 @Column(name = "password")
     private String password;
    	//字段描述: 创建时间
   	 @Column(name = "createtime")
     private Date createtime;
    	//字段描述: 最后一次登录时间
   	 @Column(name = "lastlogintime")
     private Date lastlogintime;
    	//字段描述: 最后一次登录ip
   	 @Column(name = "lastloginip")
     private String lastloginip;
    
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

	public void setIdentifier(int identifier){
		this.identifier = identifier;
	}
	@ExcelResources(title="登录标识 0 用户名 1 手机号 2 邮箱 3 QQ 4 微信 5 微博",order=3)
	public int getIdentifier(){
		return this.identifier;
	}

	public void setIdentitytype(String identitytype){
		this.identitytype = identitytype;
	}
	@ExcelResources(title="登录类型：手机号 邮箱 用户名 微信 微博",order=4)
	public String getIdentitytype(){
		return this.identitytype;
	}

	public void setPassword(String password){
		this.password = password;
	}
	@ExcelResources(title="密码或第三方凭证",order=5)
	public String getPassword(){
		return this.password;
	}

	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	@ExcelResources(title="创建时间",order=6)
	public Date getCreatetime(){
		return this.createtime;
	}

	public void setLastlogintime(Date lastlogintime){
		this.lastlogintime = lastlogintime;
	}
	@ExcelResources(title="最后一次登录时间",order=7)
	public Date getLastlogintime(){
		return this.lastlogintime;
	}

	public void setLastloginip(String lastloginip){
		this.lastloginip = lastloginip;
	}
	@ExcelResources(title="最后一次登录ip",order=8)
	public String getLastloginip(){
		return this.lastloginip;
	}

}