/**    
 * 文件名：CmFile           
 * 版本信息：    
 * 日期：2017/12/08 11:06:32    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：CmFile    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 11:06:32 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 11:06:32    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name = "cm_file")
public class CmFile implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;
    	//字段描述: 
   	 @Column(name = "FILE_NAME")
     private String fileName;
    	//字段描述: 
   	 @Column(name = "FILE_PATH")
     private String filePath;
    	//字段描述: 0 头像 1 中标通知书 2 营业执照

   	 @Column(name = "FILE_TYPE")
     private Integer fileType;
    	//字段描述: 
   	 @Column(name = "CREATE_TIME")
	 private Date createTime;
    	//字段描述: 
   	 @Column(name = "FK_ID")
     private Long fkId;

   	 @Column(name = "FK_ID_STR")
   	 private String fkIdStr;

	public void setId(Long id){
		if(id==null||id==0){
			SnowflakeIdWorker instance = SnowflakeIdWorker.getInstance(5, 5);
			this.id = instance.nextId();
		}else{
			this.id = id;
		}
	}
	@ExcelResources(title="",order=1)
	public Long getId(){
		return this.id;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	@ExcelResources(title="",order=2)
	public String getFileName(){
		return this.fileName;
	}

	public void setFilePath(String filePath){
		this.filePath = filePath;
	}
	@ExcelResources(title="",order=3)
	public String getFilePath(){
		return this.filePath;
	}

	public void setFileType(Integer fileType){
		this.fileType = fileType;
	}
	@ExcelResources(title="0 头像 1 中标通知书 2 营业执照 ",order=4)
	public Integer getFileType(){
		return this.fileType;
	}

	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="",order=5)
	public Date getCreateTime(){
		return this.createTime;
	}

	public void setFkId(Long fkId){
		this.fkId = fkId;
	}
	@ExcelResources(title="",order=6)
	public Long getFkId(){
		return this.fkId;
	}

	public String getFkIdStr() {
		return fkIdStr;
	}

	public void setFkIdStr(String fkIdStr) {
		this.fkIdStr = fkIdStr;
	}
}