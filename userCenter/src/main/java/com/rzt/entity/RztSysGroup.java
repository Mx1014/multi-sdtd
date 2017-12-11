/**    
 * 文件名：RztSysGroup           
 * 版本信息：    
 * 日期：2017/10/10 10:47:25    
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
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：RztSysGroup    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 10:47:25 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 10:47:25    
 * 修改备注：    
 * @version        
 */
@Entity
public class RztSysGroup extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
   	 @Column(name = "groupName")
     private String groupname;
    	//字段描述: 
   	 @Column(name = "groupIcon")
     private String groupicon;
    	//字段描述: 
   	 @Column(name = "groupPid")
     private String grouppid;
    	//字段描述: 
   	 @Column(name = "lft")
     private int lft;
    	//字段描述: 
   	 @Column(name = "rgt")
     private int rgt;
    	//字段描述: 
   	 @Column(name = "groupDesc")
     private String groupdesc;
    	//字段描述: 
   	 @Column(name = "createtime")
     private Date createtime;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setGroupname(String groupname){
		this.groupname = groupname;
	}
	@ExcelResources(title="",order=2)
	public String getGroupname(){
		return this.groupname;
	}

	public void setGroupicon(String groupicon){
		this.groupicon = groupicon;
	}
	@ExcelResources(title="",order=3)
	public String getGroupicon(){
		return this.groupicon;
	}

	public void setGrouppid(String grouppid){
		this.grouppid = grouppid;
	}
	@ExcelResources(title="",order=4)
	public String getGrouppid(){
		return this.grouppid;
	}

	public void setLft(int lft){
		this.lft = lft;
	}
	@ExcelResources(title="",order=5)
	public int getLft(){
		return this.lft;
	}

	public void setRgt(int rgt){
		this.rgt = rgt;
	}
	@ExcelResources(title="",order=6)
	public int getRgt(){
		return this.rgt;
	}

	public void setGroupdesc(String groupdesc){
		this.groupdesc = groupdesc;
	}
	@ExcelResources(title="",order=7)
	public String getGroupdesc(){
		return this.groupdesc;
	}

	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	@ExcelResources(title="",order=8)
	public Date getCreatetime(){
		return this.createtime;
	}

}