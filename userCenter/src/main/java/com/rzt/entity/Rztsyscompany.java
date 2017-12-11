/**    
 * 文件名：Rztsyscompany           
 * 版本信息：    
 * 日期：2017/12/08 16:40:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：Rztsyscompany    
 * 类描述：InnoDB free: 536576 kB    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 16:40:23 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 16:40:23    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="rztsyscompany")
public class Rztsyscompany extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;
    	//字段描述: 
   	 @Column(name = "companyName")
     private String companyname;
    	//字段描述: 存储单位id，以逗号分隔
   	 @Column(name = "orgId")
     private String orgid;
    	//字段描述: 
   	 @Column(name = "createTime")
     private Date createtime;
    	//字段描述: 
   	 @Column(name = "updateTime")
     private Date updatetime;
    
	public void setId(String  id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setCompanyname(String companyname){
		this.companyname = companyname;
	}
	@ExcelResources(title="",order=2)
	public String getCompanyname(){
		return this.companyname;
	}

	public void setOrgid(String orgid){
		this.orgid = orgid;
	}
	@ExcelResources(title="存储单位id，以逗号分隔",order=3)
	public String getOrgid(){
		return this.orgid;
	}

	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	@ExcelResources(title="",order=4)
	public Date getCreatetime(){
		return this.createtime;
	}

	public void setUpdatetime(Date updatetime){
		this.updatetime = updatetime;
	}
	@ExcelResources(title="",order=5)
	public Date getUpdatetime(){
		return this.updatetime;
	}

}