/**    
 * 文件名：KhCycle           
 * 版本信息：    
 * 日期：2017/11/28 14:43:44    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity;
import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * 类名称：KhCycle    
 * 类描述：InnoDB free: 536576 kB    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 14:43:44 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 14:43:44    
 * 修改备注：    
 * @version        
 */
@Entity
public class KhCycle extends BaseEntity implements Serializable{
	//字段描述: 
   	 @Id
     @GeneratedValue(generator = "uuid")
	 @GenericGenerator(name = "uuid", strategy = "uuid")
     private String id;        
    	//字段描述: 电压等级
   	 @Column(name = "VTYPE")
     private int vtype;
    	//字段描述: 线路名称
   	 @Column(name = "LINE_NAME")
     private String lineName;
    	//字段描述: 段落
   	 @Column(name = "SECTION")
     private String section;
    	//字段描述: 开始时间
   	 @Column(name = "START_TOWER")
     private int startTower;
    	//字段描述: 结束时间
   	 @Column(name = "END_TOWER")
     private int endTower;
    	//字段描述: 计划开始时间
   	 @Column(name = "PLAN_START_TIME")
     private Date planStartTime;
    	//字段描述: 计划结束时间
   	 @Column(name = "PLAN_END_TIME")
     private Date planEndTime;
    	//字段描述: 通道运维单位
   	 @Column(name = "TDYW_ORG")
     private String tdywOrg;
    	//字段描述: 是否停用  0未停用
   	 @Column(name = "IN_USE")
     private int inUse;
    	//字段描述: 生成任务次数
   	 @Column(name = "TASK_TIMES")
     private int taskTimes;
    	//字段描述: 任务类型
   	 @Column(name = "TASK_TYPE")
     private String taskType;
    	//字段描述: 线路id
   	 @Column(name = "LINE_ID")
     private String lineId;
    	//字段描述: 隐患id
   	 @Column(name = "YH_ID")
     private String yhId;
    	//字段描述: 看护范围
   	 @Column(name = "KH_RANGE")
     private String khRange;
    {
    	this.id = UUID.randomUUID().toString();
	}
	public void setId(String id){
		this.id = id;
	}
	@ExcelResources(title="",order=1)
	public String getId(){
		return this.id;
	}

	public void setVtype(int vtype){
		this.vtype = vtype;
	}
	@ExcelResources(title="",order=2)
	public int getVtype(){
		return this.vtype;
	}

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	@ExcelResources(title="",order=3)
	public String getLineName(){
		return this.lineName;
	}

	public void setSection(String section){
		this.section = section;
	}
	@ExcelResources(title="",order=4)
	public String getSection(){
		return this.section;
	}

	public void setStartTower(int startTower){
		this.startTower = startTower;
	}
	@ExcelResources(title="",order=5)
	public int getStartTower(){
		return this.startTower;
	}

	public void setEndTower(int endTower){
		this.endTower = endTower;
	}
	@ExcelResources(title="",order=6)
	public int getEndTower(){
		return this.endTower;
	}

	public void setPlanStartTime(Date planStartTime){
		this.planStartTime = planStartTime;
	}
	@ExcelResources(title="",order=7)
	public Date getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(Date planEndTime){
		this.planEndTime = planEndTime;
	}
	@ExcelResources(title="",order=8)
	public Date getPlanEndTime(){
		return this.planEndTime;
	}

	public void setTdywOrg(String tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	@ExcelResources(title="",order=9)
	public String getTdywOrg(){
		return this.tdywOrg;
	}

	public void setInUse(int inUse){
		this.inUse = inUse;
	}
	@ExcelResources(title="",order=10)
	public int getInUse(){
		return this.inUse;
	}

	public void setTaskTimes(int taskTimes){
		this.taskTimes = taskTimes;
	}
	@ExcelResources(title="",order=11)
	public int getTaskTimes(){
		return this.taskTimes;
	}

	public void setTaskType(String taskType){
		this.taskType = taskType;
	}
	@ExcelResources(title="",order=12)
	public String getTaskType(){
		return this.taskType;
	}

	public void setLineId(String lineId){
		this.lineId = lineId;
	}
	@ExcelResources(title="",order=13)
	public String getLineId(){
		return this.lineId;
	}

	public void setYhId(String yhId){
		this.yhId = yhId;
	}
	@ExcelResources(title="",order=14)
	public String getYhId(){
		return this.yhId;
	}

	public void setKhRange(String khRange){
		this.khRange = khRange;
	}
	@ExcelResources(title="",order=15)
	public String getKhRange(){
		return this.khRange;
	}

}