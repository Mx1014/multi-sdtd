/**    
 * 文件名：ExamOptions           
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
 * 类名称：ExamOptions    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Entity
public class ExamOptions extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 试题选项
   	 @Column(name = "option_name")
     private String optionName;
    	//字段描述: 是否为正确选项（0错误1正确）
   	 @Column(name = "is_right")
     private int isRight;
    	//字段描述: 选项分值
   	 @Column(name = "option_score")
     private int optionScore;
    	//字段描述: 试题id
   	 @Column(name = "text_id")
     private String textId;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setOptionName(String optionName){
		this.optionName = optionName;
	}
	@ExcelResources(title="试题选项",order=2)
	public String getOptionName(){
		return this.optionName;
	}

	public void setIsRight(int isRight){
		this.isRight = isRight;
	}
	@ExcelResources(title="是否为正确选项（0错误1正确）",order=3)
	public int getIsRight(){
		return this.isRight;
	}

	public void setOptionScore(int optionScore){
		this.optionScore = optionScore;
	}
	@ExcelResources(title="选项分值",order=4)
	public int getOptionScore(){
		return this.optionScore;
	}

	public void setTextId(String textId){
		this.textId = textId;
	}
	@ExcelResources(title="试题id",order=5)
	public String getTextId(){
		return this.textId;
	}

}