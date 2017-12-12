/**    
 * 文件名：CheckQuestion
 * 版本信息：    
 * 日期：2017/12/07 10:32:48    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
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
public class CheckQuestion implements Serializable{
	//字段描述: 问题内容
   	 @Column(name = "QUESTION")
     private String question;
    	//字段描述: 
   	 @Id
/*1	张三	班组一	13313331313
2	李四	班组二	15515551555
3	王五	班组三	18333888383
4	赵六	班组四	17717177171*/
     private Long id;
    
	public void setQuestion(String question){
		this.question = question;
	}
	@ExcelResources(title="问题内容",order=1)
	public String getQuestion(){
		return this.question;
	}

	public void setId(Long id){
		this.id =   Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
	}
	@ExcelResources(title="",order=1)
	public Long getId(){
		return this.id;
	}
}