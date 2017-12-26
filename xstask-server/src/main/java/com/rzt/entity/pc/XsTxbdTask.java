/**    
 * 文件名：XsTxbdTask           
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
import java.util.Date;

/**
 * 类名称：XsTxbdTask    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/26 16:46:02 
 * 修改人：张虎成    
 * 修改时间：2017/12/26 16:46:02    
 * 修改备注：    
 * @version        
 */
@Entity
public class XsTxbdTask implements Serializable{
	//字段描述: id
   	 @Id
     private Long id;
    	//字段描述: 任务名称
   	 @Column(name = "TASK_NAME")
     private String taskName;
    	//字段描述: 任务执行人
   	 @Column(name = "CM_USER_ID")
     private Integer cmUserId;
    	//字段描述: 特巡/保电任务周期id
   	 @Column(name = "XS_TXBD_CYCLE_ID")
     private Integer xsTxbdCycleId;
    	//字段描述: 通道单位
   	 @Column(name = "TD_ORG")
     private Integer tdOrg;
    	//字段描述: 外协单位
   	 @Column(name = "WX_ORG")
     private Integer wxOrg;
    	//字段描述: 班组
   	 @Column(name = "CLASS_ID")
     private Integer classId;
    	//字段描述: 任务状态0  待办 1进行中 2已完成 
   	 @Column(name = "STAUTS")
     private Integer stauts;
    	//字段描述: 巡视频率 每次任务中巡视应该重复的次数
   	 @Column(name = "PLAN_XS_NUM")
     private Integer planXsNum;
    	//字段描述: 本次巡视重复次数
   	 @Column(name = "REAL_XS_NUM")
     private Integer realXsNum;
    	//字段描述: 计划开始时间
   	 @Column(name = "PLAN_START_TIME")
     private Date planStartTime;
    	//字段描述: 计划结束时间
   	 @Column(name = "PLAN_END_TIME")
     private Date planEndTime;
    	//字段描述: 实际开始时间
   	 @Column(name = "REAL_START_TIME")
     private Date realStartTime;
    	//字段描述: 身份确认时间
   	 @Column(name = "SFQR_TIME")
     private Date sfqrTime;
    	//字段描述: 到达现场时间
   	 @Column(name = "DDXC_TIME")
     private Date ddxcTime;
    	//字段描述: 巡视开始时间
   	 @Column(name = "XSKS_TIME")
     private Date xsksTime;
    	//字段描述: 实际结束时间
   	 @Column(name = "REAL_END_TIME")
     private Date realEndTime;
    	//字段描述: 周期内第多少次任务
   	 @Column(name = "TASK_NUM_IN_CYCLE")
     private Integer taskNumInCycle;
    	//字段描述: 巡视类型 0 特巡 1 保电
   	 @Column(name = "XSLX")
     private Integer xslx;
    	//字段描述: 执行页数
   	 @Column(name = "ZXYS_NUM")
     private Integer zxysNum;
    	//字段描述: 巡视次数
   	 @Column(name = "XSCS_NUM")
     private Integer xscsNum;
    	//字段描述: 派单时间
   	 @Column(name = "PD_TIME")
     private Date pdTime;
    	//字段描述: 物品提醒时间
   	 @Column(name = "WPTX_TIME")
     private Date wptxTime;
    
	public void setId(){
		this.id = Long.valueOf(new SnowflakeIdWorker(0,2).nextId());
	}
	@ExcelResources(title="id",order=1)
	public Long getId(){
		return this.id;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="任务名称",order=2)
	public String getTaskName(){
		return this.taskName;
	}

	public void setCmUserId(Integer cmUserId){
		this.cmUserId = cmUserId;
	}
	@ExcelResources(title="任务执行人",order=3)
	public Integer getCmUserId(){
		return this.cmUserId;
	}

	public void setXsTxbdCycleId(Integer xsTxbdCycleId){
		this.xsTxbdCycleId = xsTxbdCycleId;
	}
	@ExcelResources(title="特巡/保电任务周期id",order=4)
	public Integer getXsTxbdCycleId(){
		return this.xsTxbdCycleId;
	}

	public void setTdOrg(Integer tdOrg){
		this.tdOrg = tdOrg;
	}
	@ExcelResources(title="通道单位",order=5)
	public Integer getTdOrg(){
		return this.tdOrg;
	}

	public void setWxOrg(Integer wxOrg){
		this.wxOrg = wxOrg;
	}
	@ExcelResources(title="外协单位",order=6)
	public Integer getWxOrg(){
		return this.wxOrg;
	}

	public void setClassId(Integer classId){
		this.classId = classId;
	}
	@ExcelResources(title="班组",order=7)
	public Integer getClassId(){
		return this.classId;
	}

	public void setStauts(Integer stauts){
		this.stauts = stauts;
	}
	@ExcelResources(title="任务状态0  待办 1进行中 2已完成 ",order=8)
	public Integer getStauts(){
		return this.stauts;
	}

	public void setPlanXsNum(Integer planXsNum){
		this.planXsNum = planXsNum;
	}
	@ExcelResources(title="巡视频率 每次任务中巡视应该重复的次数",order=9)
	public Integer getPlanXsNum(){
		return this.planXsNum;
	}

	public void setRealXsNum(Integer realXsNum){
		this.realXsNum = realXsNum;
	}
	@ExcelResources(title="本次巡视重复次数",order=10)
	public Integer getRealXsNum(){
		return this.realXsNum;
	}

	public void setPlanStartTime(Date planStartTime){
		this.planStartTime = planStartTime;
	}
	@ExcelResources(title="计划开始时间",order=11)
	public Date getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(Date planEndTime){
		this.planEndTime = planEndTime;
	}
	@ExcelResources(title="计划结束时间",order=12)
	public Date getPlanEndTime(){
		return this.planEndTime;
	}

	public void setRealStartTime(Date realStartTime){
		this.realStartTime = realStartTime;
	}
	@ExcelResources(title="实际开始时间",order=13)
	public Date getRealStartTime(){
		return this.realStartTime;
	}

	public void setSfqrTime(Date sfqrTime){
		this.sfqrTime = sfqrTime;
	}
	@ExcelResources(title="身份确认时间",order=14)
	public Date getSfqrTime(){
		return this.sfqrTime;
	}

	public void setDdxcTime(Date ddxcTime){
		this.ddxcTime = ddxcTime;
	}
	@ExcelResources(title="到达现场时间",order=15)
	public Date getDdxcTime(){
		return this.ddxcTime;
	}

	public void setXsksTime(Date xsksTime){
		this.xsksTime = xsksTime;
	}
	@ExcelResources(title="巡视开始时间",order=16)
	public Date getXsksTime(){
		return this.xsksTime;
	}

	public void setRealEndTime(Date realEndTime){
		this.realEndTime = realEndTime;
	}
	@ExcelResources(title="实际结束时间",order=17)
	public Date getRealEndTime(){
		return this.realEndTime;
	}

	public void setTaskNumInCycle(Integer taskNumInCycle){
		this.taskNumInCycle = taskNumInCycle;
	}
	@ExcelResources(title="周期内第多少次任务",order=18)
	public Integer getTaskNumInCycle(){
		return this.taskNumInCycle;
	}

	public void setXslx(Integer xslx){
		this.xslx = xslx;
	}
	@ExcelResources(title="巡视类型 0 特巡 1 保电",order=19)
	public Integer getXslx(){
		return this.xslx;
	}

	public void setZxysNum(Integer zxysNum){
		this.zxysNum = zxysNum;
	}
	@ExcelResources(title="执行页数",order=20)
	public Integer getZxysNum(){
		return this.zxysNum;
	}

	public void setXscsNum(Integer xscsNum){
		this.xscsNum = xscsNum;
	}
	@ExcelResources(title="巡视次数",order=21)
	public Integer getXscsNum(){
		return this.xscsNum;
	}

	public void setPdTime(Date pdTime){
		this.pdTime = pdTime;
	}
	@ExcelResources(title="派单时间",order=22)
	public Date getPdTime(){
		return this.pdTime;
	}

	public void setWptxTime(Date wptxTime){
		this.wptxTime = wptxTime;
	}
	@ExcelResources(title="物品提醒时间",order=23)
	public Date getWptxTime(){
		return this.wptxTime;
	}

}