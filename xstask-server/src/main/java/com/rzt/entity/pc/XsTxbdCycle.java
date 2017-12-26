/**    
 * 文件名：XsTxbdCycle           
 * 版本信息：    
 * 日期：2017/12/26 16:46:02    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity.pc;

import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 类名称：XsTxbdCycle    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/26 16:46:02 
 * 修改人：张虎成    
 * 修改时间：2017/12/26 16:46:02    
 * 修改备注：    
 * @version        
 */
@Entity
public class XsTxbdCycle implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;
    	//字段描述: 电压等级
   	 @Column(name = "V_LEVEL")
     private Integer vLevel;
    	//字段描述: 线路名称
   	 @Column(name = "LINE_NAME")
     private String lineName;
    	//字段描述: 段落
   	 @Column(name = "SECTION")
     private String section;
    	//字段描述: 巡视起始杆序号
   	 @Column(name = "XS_START_SORT")
     private Integer xsStartSort;
    	//字段描述: 巡视终止杆序号
   	 @Column(name = "XS_END_SORT")
     private Integer xsEndSort;
    	//字段描述: 不间断或次数
   	 @Column(name = "FATE")
     private Integer fate;
    	//字段描述: 时间段跨度
   	 @Column(name = "START_DATE")
     private String startDate;
    	//字段描述: 时间段跨度
   	 @Column(name = "END_DATE")
     private String endDate;
    	//字段描述: 是否运营 0 运营 1 不运营
   	 @Column(name = "IN_USE")
     private Integer inUse;
    	//字段描述: 通道运维单位
   	 @Column(name = "TDYW_ORG")
     private Integer tdywOrg;
    	//字段描述: 生成任务总次数
   	 @Column(name = "BORN_TASK_TOTAL")
     private Integer bornTaskTotal;
    	//字段描述: 1 特巡、0 保电
   	 @Column(name = "XS_TYPE")
     private Integer xsType;
    	//字段描述: 几班倒
   	 @Column(name = "JBD")
     private Integer jbd;
    	//字段描述: 计划开始时间1
   	 @Column(name = "PLAN_START_TIME1")
     private String planStartTime1;
    	//字段描述: 计划开始时间2
   	 @Column(name = "PLAN_START_TIME2")
     private String planStartTime2;
    	//字段描述: 计划开始时间3
   	 @Column(name = "PLAN_START_TIME3")
     private String planStartTime3;
    	//字段描述: 计划开始时间4
   	 @Column(name = "PLAN_START_TIME4")
     private String planStartTime4;
    	//字段描述: 特巡保电周期
   	 @Column(name = "XS_TXBD_CYCLE_ID")
     private Integer xsTxbdCycleId;
    
	public void setId(){
		this.id = this.id = Long.valueOf(new SnowflakeIdWorker(0,1).nextId());;
	}
	@ExcelResources(title="",order=1)
	public Long getId(){
		return this.id;
	}

	public void setVLevel(Integer vLevel){
		this.vLevel = vLevel;
	}
	@ExcelResources(title="电压等级",order=2)
	public Integer getVLevel(){
		return this.vLevel;
	}

	public void setLineName(String lineName){
		this.lineName = lineName;
	}
	@ExcelResources(title="线路名称",order=3)
	public String getLineName(){
		return this.lineName;
	}

	public void setSection(String section){
		this.section = section;
	}
	@ExcelResources(title="段落",order=4)
	public String getSection(){
		return this.section;
	}

	public void setXsStartSort(Integer xsStartSort){
		this.xsStartSort = xsStartSort;
	}
	@ExcelResources(title="巡视起始杆序号",order=5)
	public Integer getXsStartSort(){
		return this.xsStartSort;
	}

	public void setXsEndSort(Integer xsEndSort){
		this.xsEndSort = xsEndSort;
	}
	@ExcelResources(title="巡视终止杆序号",order=6)
	public Integer getXsEndSort(){
		return this.xsEndSort;
	}

	public void setFate(Integer fate){
		this.fate = fate;
	}
	@ExcelResources(title="不间断或次数",order=7)
	public Integer getFate(){
		return this.fate;
	}

	public void setStartDate(String startDate){
		this.startDate = startDate;
	}
	@ExcelResources(title="时间段跨度",order=8)
	public String getStartDate(){
		return this.startDate;
	}

	public void setEndDate(String endDate){
		this.endDate = endDate;
	}
	@ExcelResources(title="时间段跨度",order=9)
	public String getEndDate(){
		return this.endDate;
	}

	public void setInUse(Integer inUse){
		this.inUse = inUse;
	}
	@ExcelResources(title="是否运营 0 运营 1 不运营",order=10)
	public Integer getInUse(){
		return this.inUse;
	}

	public void setTdywOrg(Integer tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	@ExcelResources(title="通道运维单位",order=11)
	public Integer getTdywOrg(){
		return this.tdywOrg;
	}

	public void setBornTaskTotal(Integer bornTaskTotal){
		this.bornTaskTotal = bornTaskTotal;
	}
	@ExcelResources(title="生成任务总次数",order=12)
	public Integer getBornTaskTotal(){
		return this.bornTaskTotal;
	}

	public void setXsType(Integer xsType){
		this.xsType = xsType;
	}
	@ExcelResources(title="1 特巡、0 保电",order=13)
	public Integer getXsType(){
		return this.xsType;
	}

	public void setJbd(Integer jbd){
		this.jbd = jbd;
	}
	@ExcelResources(title="几班倒",order=14)
	public Integer getJbd(){
		return this.jbd;
	}

	public void setPlanStartTime1(String planStartTime1){
		this.planStartTime1 = planStartTime1;
	}
	@ExcelResources(title="计划开始时间1",order=15)
	public String getPlanStartTime1(){
		return this.planStartTime1;
	}

	public void setPlanStartTime2(String planStartTime2){
		this.planStartTime2 = planStartTime2;
	}
	@ExcelResources(title="计划开始时间2",order=16)
	public String getPlanStartTime2(){
		return this.planStartTime2;
	}

	public void setPlanStartTime3(String planStartTime3){
		this.planStartTime3 = planStartTime3;
	}
	@ExcelResources(title="计划开始时间3",order=17)
	public String getPlanStartTime3(){
		return this.planStartTime3;
	}

	public void setPlanStartTime4(String planStartTime4){
		this.planStartTime4 = planStartTime4;
	}
	@ExcelResources(title="计划开始时间4",order=18)
	public String getPlanStartTime4(){
		return this.planStartTime4;
	}

	public void setXsTxbdCycleId(Integer xsTxbdCycleId){
		this.xsTxbdCycleId = xsTxbdCycleId;
	}
	@ExcelResources(title="特巡保电周期",order=19)
	public Integer getXsTxbdCycleId(){
		return this.xsTxbdCycleId;
	}

}