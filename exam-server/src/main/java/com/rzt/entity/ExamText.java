/**    
 * 文件名：ExamText           
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
 * 类名称：ExamText    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Entity
public class ExamText extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 题型标识（1单选2多选3判断）
   	 @Column(name = "text_type")
     private int textType;
    	//字段描述: 题号
   	 @Column(name = "text_order")
     private int textOrder;
    	//字段描述: 所属科目（试题分类，属于哪一类的题）
   	 @Column(name = "text_project")
     private int textProject;
    	//字段描述: 试题描述
   	 @Column(name = "text_body")
     private String textBody;
    	//字段描述: 分值
   	 @Column(name = "text_points")
     private int textPoints;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setTextType(int textType){
		this.textType = textType;
	}
	@ExcelResources(title="题型标识（1单选2多选3判断）",order=2)
	public int getTextType(){
		return this.textType;
	}

	public void setTextOrder(int textOrder){
		this.textOrder = textOrder;
	}
	@ExcelResources(title="题号",order=3)
	public int getTextOrder(){
		return this.textOrder;
	}

	public void setTextProject(int textProject){
		this.textProject = textProject;
	}
	@ExcelResources(title="所属科目（试题分类，属于哪一类的题）",order=4)
	public int getTextProject(){
		return this.textProject;
	}

	public void setTextBody(String textBody){
		this.textBody = textBody;
	}
	@ExcelResources(title="试题描述",order=5)
	public String getTextBody(){
		return this.textBody;
	}

	public void setTextPoints(int textPoints){
		this.textPoints = textPoints;
	}
	@ExcelResources(title="分值",order=6)
	public int getTextPoints(){
		return this.textPoints;
	}

}