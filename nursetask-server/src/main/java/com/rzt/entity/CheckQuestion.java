/**    
 * 文件名：CheckQuestion
 * 版本信息：    
 * 日期：2017/12/07 10:32:48    
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
 * 类名称：CheckQuestion
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 10:32:48 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 10:32:48    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CHECK_QUESTION")
public class CheckQuestion extends BaseEntity implements Serializable{
	//字段描述: 问题内容
   	 @Column(name = "QUESTION")
     private String question;
    	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    
	public void setQuestion(String question){
		this.question = question;
	}
	@ExcelResources(title="问题内容",order=1)
	public String getQuestion(){
		return this.question;
	}

	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=2)
	public String getId(){
		return this.id;
	}

}