/**    
 * 文件名：ExamImg           
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;

import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.UUID;

/**
 * 类名称：ExamImg    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Entity
public class ExamImg extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 图片路径
   	 @Column(name = "img_path")
     private String imgPath;
    	//字段描述: 
   	 @Column(name = "text_id")
     private String textId;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setImgPath(String imgPath){
		this.imgPath = imgPath;
	}
	@ExcelResources(title="图片路径",order=2)
	public String getImgPath(){
		return this.imgPath;
	}

	public void setTextId(String textId){
		this.textId = textId;
	}
	@ExcelResources(title="",order=3)
	public String getTextId(){
		return this.textId;
	}

}