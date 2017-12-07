/**    
 * 文件名：CheckQuestionAnswer
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
 * 类名称：CheckQuestionAnswer
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 10:32:48 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 10:32:48    
 * 修改备注：    
 * @version        
 */
@Entity
@Table(name="CHECK_QUESTION_ANSWER")
public class CheckQuestionAnswer extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 问题id
   	 @Column(name = "QUESTION_ID")
     private String questionId;
    	//字段描述: 稽查任务id
   	 @Column(name = "TASK_ID")
     private String taskId;
    	//字段描述: 问题答案
   	 @Column(name = "ANSWER")
     private String answer;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setQuestionId(String questionId){
		this.questionId = questionId;
	}
	@ExcelResources(title="问题id",order=2)
	public String getQuestionId(){
		return this.questionId;
	}

	public void setTaskId(String taskId){
		this.taskId = taskId;
	}
	@ExcelResources(title="稽查任务id",order=3)
	public String getTaskId(){
		return this.taskId;
	}

	public void setAnswer(String answer){
		this.answer = answer;
	}
	@ExcelResources(title="问题答案",order=4)
	public String getAnswer(){
		return this.answer;
	}

}