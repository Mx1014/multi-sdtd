/**    
 * 文件名：RztSysDepartment           
 * 版本信息：    
 * 日期：2017/10/10 10:26:33    
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
 * 类名称：RztSysDepartment    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/10 10:26:33 
 * 修改人：张虎成    
 * 修改时间：2017/10/10 10:26:33    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="RZTSYSDEPARTMENT")
public class RztSysDepartment extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 
   	 @Column(name = "deptName")
     private String deptname;
    	//字段描述: 
   	 @Column(name = "deptIcon")
     private String depticon;
    	//字段描述: 
   	 @Column(name = "deptPid")
     private String deptpid;
    	//字段描述: 
   	 @Column(name = "lft")
     private int lft;
    	//字段描述: 
   	 @Column(name = "rgt")
     private int rgt;
    	//字段描述: 
   	 @Column(name = "deptDesc")
     private String deptdesc;
    	//字段描述: 
   	 @Column(name = "createtime")
     private Date createtime;
	//字段描述: 角色id
	@Column(name = "roleid")
	private String roleid;


	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setDeptname(String deptname){
		this.deptname = deptname;
	}
	@ExcelResources(title="",order=2)
	public String getDeptname(){
		return this.deptname;
	}

	public void setDepticon(String depticon){
		this.depticon = depticon;
	}
	@ExcelResources(title="",order=3)
	public String getDepticon(){
		return this.depticon;
	}

	public void setDeptpid(String deptpid){
		this.deptpid = deptpid;
	}
	@ExcelResources(title="",order=4)
	public String getDeptpid(){
		return this.deptpid;
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

	public void setDeptdesc(String deptdesc){
		this.deptdesc = deptdesc;
	}
	@ExcelResources(title="",order=7)
	public String getDeptdesc(){
		return this.deptdesc;
	}

	public void setCreatetime(Date createtime){
		this.createtime = createtime;
	}
	@ExcelResources(title="",order=8)
	public Date getCreatetime(){
		return this.createtime;
	}

	public String getRoleid() {
		return roleid;
	}
	@ExcelResources(title="",order=9)
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

}