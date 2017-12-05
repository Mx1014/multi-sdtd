/**    
 * 文件名：ExamPapers           
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
 * 类名称：ExamPapers    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Entity
public class ExamPapers extends BaseEntity implements Serializable{
	//字段描述: uuid
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 试卷名
   	 @Column(name = "paper_name")
     private String paperName;
    	//字段描述: 考试时长（分钟）
   	 @Column(name = "exam_time")
     private int examTime;
	//字段描述: 总分
   	 @Column(name = "total_score")
     private int totalScore;
    	//字段描述: 模块分类标识（1、2、3、4...）
   	 @Column(name = "sys_module")
     private int sysModule;
    	//字段描述: 试卷发布时间
   	 @Column(name = "create_time")
     private String createTime;
    
	public void setId(String id){
		this.id = UUID.randomUUID().toString();
	}
	@ExcelResources(title="uuid",order=1)
	public String getId(){
		return this.id;
	}

	public void setPaperName(String paperName){
		this.paperName = paperName;
	}
	@ExcelResources(title="试卷名",order=2)
	public String getPaperName(){
		return this.paperName;
	}

	public void setExamTime(int examTime){
		this.examTime = examTime;
	}
	@ExcelResources(title="考试时长（分钟）",order=3)
	public int getExamTime(){
		return this.examTime;
	}

	public void setSysModule(int sysModule){
		this.sysModule = sysModule;
	}
	@ExcelResources(title="模块分类标识（1、2、3、4...）",order=4)
	public int getSysModule(){
		return this.sysModule;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}
	@ExcelResources(title="试卷发布时间",order=5)
	public String getCreateTime(){
		return this.createTime;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
}