/**    
 * 文件名：ExamScore           
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
 * 类名称：ExamScore    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Entity
public class ExamScore extends BaseEntity implements Serializable{
	//字段描述: uuid
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 分数
   	 @Column(name = "score")
     private String score;
    	//字段描述: 考试人员名称
   	 @Column(name = "user_name")
     private String userName;
    	//字段描述: 考试人员id
   	 @Column(name = "user_id")
     private String userId;
    	//字段描述: 试卷id
   	 @Column(name = "paper_id")
     private String paperId;
    	//字段描述: 胶卷时间
   	 @Column(name = "commit_time")
     private String commitTime;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="uuid",order=1)
	public String getId(){
		return this.id;
	}

	public void setScore(String score){
		this.score = score;
	}
	@ExcelResources(title="分数",order=2)
	public String getScore(){
		return this.score;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}
	@ExcelResources(title="考试人员名称",order=3)
	public String getUserName(){
		return this.userName;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}
	@ExcelResources(title="考试人员id",order=4)
	public String getUserId(){
		return this.userId;
	}

	public void setPaperId(String paperId){
		this.paperId = paperId;
	}
	@ExcelResources(title="试卷id",order=5)
	public String getPaperId(){
		return this.paperId;
	}

	public void setCommitTime(String commitTime){
		this.commitTime = commitTime;
	}
	@ExcelResources(title="胶卷时间",order=6)
	public String getCommitTime(){
		return this.commitTime;
	}

}