/**    
 * 文件名：Rztsyscompanyfile           
 * 版本信息：    
 * 日期：2017/12/08 16:40:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * 类名称：Rztsyscompanyfile    
 * 类描述：InnoDB free: 536576 kB    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 16:40:23 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 16:40:23    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="rztsyscompanyfile")
public class Rztsyscompanyfile extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;
    	//字段描述: 1 中标通知书 2 营业执照 
   	 @Column(name = "fileType")
     private int filetype;
    	//字段描述: 
   	 @Column(name = "fileName")
     private String filename;
    	//字段描述: 
   	 @Column(name = "companyId")
     private String companyid;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setFiletype(int filetype){
		this.filetype = filetype;
	}
	@ExcelResources(title="1 中标通知书 2 营业执照 ",order=2)
	public int getFiletype(){
		return this.filetype;
	}

	public void setFilename(String filename){
		this.filename = filename;
	}
	@ExcelResources(title="",order=3)
	public String getFilename(){
		return this.filename;
	}

	public void setCompanyid(String companyid){
		this.companyid = companyid;
	}
	@ExcelResources(title="",order=4)
	public String getCompanyid(){
		return this.companyid;
	}

}