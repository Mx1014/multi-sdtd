/**    
 * 文件名：XsZcCycle           
 * 版本信息：    
 * 日期：2017/12/07 07:50:10    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.entity.pc;

import com.rzt.entity.BaseEntity;
import com.rzt.util.excelUtil.ExcelResources;
import com.rzt.utils.SnowflakeIdWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 类名称：XsZcCycle    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 07:50:10 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 07:50:10    
 * 修改备注：    
 * @version        
 */
@Entity
public class XsZcCycle implements Serializable{
	//字段描述: 
   	 @Id
     private Long id;
	//字段描述: 电压等级 0 1 2 3
   	 @Column(name = "V_LEVEL")
     private Integer vLevel;
    	//字段描述: 线路id
   	 @Column(name = "LINE_ID")
     private Long lineId;
    	//字段描述: 线路名称
   	 @Column(name = "TASK_NAME")
     private String taskName;
    	//字段描述: 段落
   	 @Column(name = "SECTION")
     private String section;
    	//字段描述: 巡视起始杆序号
   	 @Column(name = "XS_START_SORT")
     private Integer xsStartSort;
    	//字段描述: 巡视终止杆序号
   	 @Column(name = "XS_END_SORT")
     private Integer xsEndSort;
    	//字段描述: 计划开始时段 没有天数概念
   	 @Column(name = "PLAN_START_TIME")
     private String planStartTime;
    	//字段描述: 计划结束时段 没有天数概念
   	 @Column(name = "PLAN_END_TIME")
     private String planEndTime;
    	//字段描述: 不间断或次数
   	 @Column(name = "PLAN_XS_NUM")
     private Integer planXsNum;
    	//字段描述: 24小制 用天表示
   	 @Column(name = "CYCLE")
     private Integer cycle;
    	//字段描述: 通道运维单位
   	 @Column(name = "TD_ORG")
     private String tdywOrg;
    	//字段描述: 是否停用 0 不停用 1 停用
   	 @Column(name = "IN_USE")
     private Integer inUse = 0;
    	//字段描述: 
   	 @Column(name = "TOTAL_TASK_NUM")
     private Integer totalTaskNum = 0;
        //字段描述
   	 @Column(name = "CREATE_TIME")
    private Date createTime;
	//字段描述
	@Column(name = "is_delete")
	private Integer isDelete = 0;


	public void setId(){
		this.id = Long.valueOf(new SnowflakeIdWorker(0,0).nextId());
	}
	@ExcelResources(title="id",order=1)
	public Long getId(){
		return this.id;
	}

	public void setVLevel(Integer vLevel){
		this.vLevel = vLevel;
	}
	@ExcelResources(title="电压等级 0 1 2 3",order=2)
	public Integer getVLevel(){
		return this.vLevel;
	}

	public void setLineId(Long lineId){
		this.lineId = lineId;
	}
	@ExcelResources(title="线路id",order=3)
	public Long getLineId(){
		return this.lineId;
	}

	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	@ExcelResources(title="线路名称",order=4)
	public String getTaskName(){
		return this.taskName;
	}

	public void setSection(String section){
		this.section = section;
	}
	@ExcelResources(title="段落",order=5)
	public String getSection(){
		return this.section;
	}

	public void setXsStartSort(Integer xsStartSort){
		this.xsStartSort = xsStartSort;
	}
	@ExcelResources(title="巡视起始杆序号",order=6)
	public Integer getXsStartSort(){
		return this.xsStartSort;
	}

	public void setXsEndSort(Integer xsEndSort){
		this.xsEndSort = xsEndSort;
	}
	@ExcelResources(title="巡视终止杆序号",order=7)
	public Integer getXsEndSort(){
		return this.xsEndSort;
	}

	public void setPlanStartTime(String planStartTime){
		this.planStartTime = planStartTime;
	}
	@ExcelResources(title="计划开始时段 没有天数概念",order=8)
	public String getPlanStartTime(){
		return this.planStartTime;
	}

	public void setPlanEndTime(String planEndTime){
		this.planEndTime = planEndTime;
	}
	@ExcelResources(title="计划结束时段 没有天数概念",order=9)
	public String getPlanEndTime(){
		return this.planEndTime;
	}

	public void setPlanXsNum(Integer planXsNum){
		this.planXsNum = planXsNum;
	}
	@ExcelResources(title="不间断或次数",order=10)
	public Integer getPlanXsNum(){
		return this.planXsNum;
	}

	public void setCycle(Integer cycle){
		this.cycle = cycle;
	}
	@ExcelResources(title="24小制 用天表示",order=11)
	public Integer getCycle(){
		return this.cycle;
	}

	public void setTdywOrg(String tdywOrg){
		this.tdywOrg = tdywOrg;
	}
	@ExcelResources(title="通道运维单位",order=12)
	public String getTdywOrg(){
		return this.tdywOrg;
	}

	public void setInUse(Integer inUse){
		this.inUse = inUse;
	}
	@ExcelResources(title="是否停用 0 不停用 1 停用",order=13)
	public Integer getInUse(){
		return this.inUse;
	}

	public void setTotalTaskNum(Integer totalTaskNum){
		this.totalTaskNum = totalTaskNum;
	}
	public Integer getTotalTaskNum(){
		return this.totalTaskNum;
	}

    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
}